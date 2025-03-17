package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.config.MlModelRestProperties;
import dashbah.hse.lexiscan.app.dto.ImageProcessingRs;
import dashbah.hse.lexiscan.app.dto.MlModelRq;
import dashbah.hse.lexiscan.app.dto.MlModelRs;
import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.Image;
import dashbah.hse.lexiscan.app.entity.Message;
import dashbah.hse.lexiscan.app.entity.MlRequest;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.exception.ImageNotFoundException;
import dashbah.hse.lexiscan.app.repository.MessageRepository;
import dashbah.hse.lexiscan.app.repository.MlRequestRepository;
import dashbah.hse.lexiscan.app.service.ImageProcessingService;
import dashbah.hse.lexiscan.app.service.ImageService;
import dashbah.hse.lexiscan.app.service.RepositoryDataHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final RepositoryDataHandler repositoryDataHandler;
    private final MessageRepository messageRepository;
    private final MlRequestRepository mlRequestRepository;
    private final ImageService imageService;
    private final MlModelRestProperties mlModelRestProperties;

    /**
     * Сохраняет изображение в чат и отправляет его в ML-модель.
     *
     * @param chatUId ID чата, куда отправляется изображение.
     * @param image   Бинарные данные изображения.
     * @return Процент от ML-модели.
     */
    @Override
    public ImageProcessingRs processImage(String rquid, String chatUId, byte[] image) throws ChatNotFoundException {
        MlRequest mlRequest = saveRequest(chatUId, image);
        MlModelRq mlModelRq = null;
        try {
            mlModelRq = buildMlModelRq(mlRequest);
        } catch (ImageNotFoundException e) {
            log.error(rquid + ": Image was not found");
            throw new RuntimeException(e);
        }

        // Вызов ML-модели
        try {
            log.info(rquid + ": image sending to ml-model, body: " + mlModelRq.toString().substring(0, 15));
            MlModelRs mlResponse = restTemplate.postForObject(mlModelRestProperties.getMlModelUrl(), mlModelRq, MlModelRs.class);
            mlRequest = updateMlRq(mlResponse, mlRequest, "Completed");

            return ImageProcessingRs.builder()
                    .rquid(rquid)
                    .percentage(mlResponse.getPercentage())
                    .build();
        } catch (RestClientException e) {
            log.warn(rquid, "ошибка отправки сообщения в мл модель. " + e.getMessage());
            updateMlRqStatus(mlRequest, "REST_ERROR");
            throw e;
        } catch (Exception e) {
            log.error(rquid, "ошибка отправки сообщения в мл модель. " + e.getMessage());
            updateMlRqStatus(mlRequest, "ERROR");
            throw e;
        }
    }

    @Transactional
    private MlRequest updateMlRq(MlModelRs mlResponse, MlRequest mlRequest, String status) {
        mlRequest.setStatus(status);
        mlRequest.setPercentage(mlResponse.getPercentage());
        return mlRequestRepository.save(mlRequest);
    }

    private MlRequest updateMlRqStatus(MlRequest mlRequest, String status) {
        mlRequest.setStatus(status);
        return mlRequestRepository.save(mlRequest);
    }

    private MlModelRq buildMlModelRq(MlRequest mlRequest) throws ImageNotFoundException {
        var imageBody = imageService.getImageBinaryDataByUid(mlRequest.getImage_uid());
        return MlModelRq.builder()
                .rquid(mlRequest.getRquid())
                .image(Base64.getEncoder().encodeToString(imageBody))
                .build();
    }

    @Transactional
    private MlRequest saveRequest(String chatUId, byte[] imageData) throws ChatNotFoundException {
        // TODO: validate that this is my chat

        Chat chat = repositoryDataHandler.findChatByChatUId(chatUId);

        // Создаем сообщение
        Message message = Message.builder()
                .messageUid("msg_" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .chat(chat)
                .build();
        messageRepository.save(message);

        // Сохраняем изображение
        Image image = Image.builder()
                .imageUid("img_" + System.currentTimeMillis())
                .body(imageData.clone())
                .message(message)
                .build();
        imageService.saveImage(image);

        MlRequest mlRequest = MlRequest.builder()
                .rquid("req_" + System.currentTimeMillis())
                .image_uid(image.getImageUid())
                .status("PENDING")
                .build();
        mlRequestRepository.save(mlRequest);

        return mlRequest;
    }
}
