package dashbah.hse.lexiscan.app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dashbah.hse.lexiscan.app.config.MlModelRestProperties;
import dashbah.hse.lexiscan.app.dto.client.imageprocessing.ImageProcessingRs;
import dashbah.hse.lexiscan.app.dto.mlmodel.MlModelRs;
import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.Image;
import dashbah.hse.lexiscan.app.entity.Message;
import dashbah.hse.lexiscan.app.entity.MlRequest;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.repository.ImageRepository;
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
    private final ImageRepository imageRepository;
    private final ImageService imageS3Service;
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
        MlRequest updatedMlRq = saveRequest(chatUId, image); // saves to db and to s3

        try {
            MlModelRs mlResponse = sendToMl(rquid, image, fileName);

            updatedMlRq = updateMlRq(mlResponse, updatedMlRq, "Completed"); // saves result to db and to s3

            return ImageProcessingRs.builder()
                    .imageUploadedUId(updatedMlRq.getImageUId())
                    .imageResultUId(updatedMlRq.getResultImageUId())
                    .build();
        } catch (RestClientException e) {
            log.warn(rquid + ": rest ошибка отправки сообщения в мл модель. " + e.getMessage());
            updateMlRqStatus(updatedMlRq, "REST_ERROR");
            throw e;
        } catch (Exception e) {
            log.error(rquid + ": ошибка отправки сообщения в мл модель. " + e.getMessage());
            updateMlRqStatus(updatedMlRq, "ERROR");
            throw e;
        }
    }

    MlModelRs sendToMl(String rquid, byte[] image, String fileName) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(mlModelRestProperties.getMlModelUrl());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("rquid", rquid, ContentType.TEXT_PLAIN);

        builder.addBinaryBody(
                "image",
                image,
                ContentType.APPLICATION_OCTET_STREAM,
                fileName
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        log.info(rquid + ": image sending to ml-model, filename: " + fileName);

        try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
            HttpEntity responseEntity = response.getEntity();
            String contentType = responseEntity.getContentType().getValue();

            if (contentType.contains("application/json")) {
                String responseString = EntityUtils.toString(responseEntity);
                log.info("{}: received JSON response from ml model: {}", rquid, responseString);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(responseString);

                if (root.has("error")) {
                    String errorMsg = root.get("error").asText();
                    throw new RuntimeException("ML model error: " + errorMsg);
                } else {
                    log.warn("JSON type response without error from ml");
                    // Если JSON с результатом, десериализуем
                    return objectMapper.treeToValue(root, MlModelRs.class);
                }
            } else if (contentType.contains("image/png")) {
                log.info(rquid + "received image.png response from ml");
                byte[] imageBytes = EntityUtils.toByteArray(responseEntity);
                MlModelRs mlModelRs = new MlModelRs();
                mlModelRs.setResultImageBytes(imageBytes);
                return mlModelRs;
            } else {
                throw new RuntimeException("Unexpected content type: " + contentType);
            }
        }
    }

    @Transactional
    private MlRequest updateMlRq(MlModelRs mlResponse, MlRequest mlRequest, String status) {
        String imageUId = "img_" + System.currentTimeMillis();
        Message message = messageRepository.findByImageUID(mlRequest.getImageUId());
        Image image = Image.builder()
                .imageUid(imageUId)
                .message(message)
                .build();

        imageRepository.save(image);
        imageS3Service.saveImage(imageUId, mlResponse.getResultImageBytes());

        log.info(mlRequest.getRquid() + ": image saved");
        mlRequest.setResultImageUId(imageUId);
        mlRequest.setStatus(status);
        return mlRequestRepository.save(mlRequest);
    }

    private MlRequest updateMlRqStatus(MlRequest mlRequest, String status) {
        mlRequest.setStatus(status);
        return mlRequestRepository.save(mlRequest);
    }

    @Transactional
    private MlRequest saveRequest(String chatUId, byte[] imageData) throws ChatNotFoundException {
        // TODO: validate that this is user's chat

        Chat chatEntity = repositoryDataHandler.findChatByChatUId(chatUId);

        String imageUId = "img_" + System.currentTimeMillis();

        Message message = Message.builder()
                .messageUid("msg_" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .chat(chatEntity)
                .imageUID(imageUId)
                .build();
        messageRepository.save(message);

        Image image = Image.builder()
                .imageUid(imageUId)
                .message(message)
                .build();
        imageRepository.save(image);
        imageS3Service.saveImage(imageUId, imageData);

        MlRequest mlRequest = MlRequest.builder()
                .rquid("req_" + System.currentTimeMillis())
                .imageUId(image.getImageUid())
                .status("PENDING")
                .build();
        mlRequestRepository.save(mlRequest);

        return mlRequest;
    }
}
