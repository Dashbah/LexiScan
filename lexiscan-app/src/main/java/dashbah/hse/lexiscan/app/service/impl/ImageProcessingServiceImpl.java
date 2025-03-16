package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.dto.ImageProcessingRs;
import dashbah.hse.lexiscan.app.dto.MlModelRq;
import dashbah.hse.lexiscan.app.dto.MlModelRs;
import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.Image;
import dashbah.hse.lexiscan.app.entity.Message;
import dashbah.hse.lexiscan.app.entity.MlRequest;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.repository.ImageRepository;
import dashbah.hse.lexiscan.app.repository.MessageRepository;
import dashbah.hse.lexiscan.app.repository.MlRequestRepository;
import dashbah.hse.lexiscan.app.service.ImageProcessingService;
import dashbah.hse.lexiscan.app.service.RepositoryDataHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final RepositoryDataHandler repositoryDataHandler;
    private final MessageRepository messageRepository;
    private final ImageRepository imageRepository;
    private final MlRequestRepository mlRequestRepository;
    private static final String ML_MODEL_URL = "http://localhost:7070/ml-model/count";

    /**
     * Сохраняет изображение в чат и отправляет его в ML-модель.
     *
     * @param chatUId ID чата, куда отправляется изображение.
     * @param image   Бинарные данные изображения.
     * @return Процент от ML-модели.
     */
    @Override
    public ImageProcessingRs processImage(String rquid, String chatUId, byte[] image) throws ChatNotFoundException {
        MlRequest mlRequest = saveImage(chatUId, image);
        MlModelRq mlModelRq = buildMlModelRq(mlRequest);

        // Вызов ML-модели
        try {
            MlModelRs mlResponse = restTemplate.postForObject(ML_MODEL_URL, mlModelRq, MlModelRs.class);
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

    private MlModelRq buildMlModelRq(MlRequest mlRequest) {
        return MlModelRq.builder()
                .rquid(mlRequest.getRquid())
                .image(mlRequest.getImage().getBody())
                .build();
    }

    @Transactional
    private MlRequest saveImage(String chatUId, byte[] imageData) throws ChatNotFoundException {
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
                .body(imageData)
                .message(message)
                .build();
        imageRepository.save(image);

        MlRequest mlRequest = MlRequest.builder()
                .rquid("req_" + System.currentTimeMillis())
                .image(image)
                .status("PENDING")
                .build();
        mlRequestRepository.save(mlRequest);

        return mlRequest;
    }
}
