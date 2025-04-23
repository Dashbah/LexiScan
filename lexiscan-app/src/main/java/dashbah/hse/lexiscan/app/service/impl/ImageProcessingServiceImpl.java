package dashbah.hse.lexiscan.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dashbah.hse.lexiscan.app.config.MlModelRestProperties;
import dashbah.hse.lexiscan.app.dto.ImageProcessingRs;
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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {

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
    public ImageProcessingRs processImage(String rquid, String chatUId, byte[] image, String fileName) throws ChatNotFoundException, IOException {
        MlRequest mlRequest = saveRequest(chatUId, image);
        byte[] mlModelRq = null;
        try {
            mlModelRq = buildMlModelRq(mlRequest);
        } catch (ImageNotFoundException e) {
            log.error(rquid + ": Image was not found");
            throw new RuntimeException(e);
        }

        // Вызов ML-модели
        try {
            MlModelRs mlResponse = sendToMl(rquid, mlModelRq, fileName);

            mlRequest = updateMlRq(mlResponse, mlRequest, "Completed");

            return ImageProcessingRs.builder()
                    .rquid(rquid)
                    .percentage(mlResponse.getConfidence())
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


    MlModelRs sendToMl(String rquid, byte[] mlModelRq, String fileName) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(mlModelRestProperties.getMlModelUrl());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("rquid", rquid, ContentType.TEXT_PLAIN);

        builder.addBinaryBody(
                "image",
                mlModelRq,
                ContentType.APPLICATION_OCTET_STREAM,
                fileName
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        log.info(rquid + ": image sending to ml-model, filename: " + fileName);
        try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
            HttpEntity responseEntity = response.getEntity();

            String responseString = EntityUtils.toString(responseEntity);
            log.info("{}: received response from ml model: {}", rquid, responseString);

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(responseString, MlModelRs.class);
        }
    }

    @Transactional
    private MlRequest updateMlRq(MlModelRs mlResponse, MlRequest mlRequest, String status) {
        mlRequest.setStatus(status);
        mlRequest.setPercentage(mlResponse.getConfidence());
        return mlRequestRepository.save(mlRequest);
    }

    private MlRequest updateMlRqStatus(MlRequest mlRequest, String status) {
        mlRequest.setStatus(status);
        return mlRequestRepository.save(mlRequest);
    }

    private byte[] buildMlModelRq(MlRequest mlRequest) throws ImageNotFoundException {
        return imageService.getImageBinaryDataByUid(mlRequest.getImage_uid());
    }

    @Transactional
    private MlRequest saveRequest(String chatUId, byte[] imageData) throws ChatNotFoundException {
        // TODO: validate that this is my chat

        Chat chatEntity = repositoryDataHandler.findChatByChatUId(chatUId);

        // Создаем сообщение
        Message message = Message.builder()
                .messageUid("msg_" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .chat(chatEntity)
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
