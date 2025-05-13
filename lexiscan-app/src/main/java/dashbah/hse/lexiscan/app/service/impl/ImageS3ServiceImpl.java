package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.config.s3.S3Properties;
import dashbah.hse.lexiscan.app.exception.ImageNotFoundException;
import dashbah.hse.lexiscan.app.service.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class ImageS3ServiceImpl implements ImageService {

    private S3Client s3Client;
    private final S3Properties s3Properties;

    // Настройки для Yandex Cloud Object Storage

    @PostConstruct
    public void init() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3Properties.getKEY_ID(), s3Properties.getSECRET_KEY());

        s3Client = S3Client.builder()
                .region(Region.of(s3Properties.getREGION()))
                .endpointOverride(URI.create(s3Properties.getS3_ENDPOINT()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    @PreDestroy
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    public void saveImage(String key, byte[] data) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBUCKET())
                .key(key)
                .contentType("image/jpeg") // укажите нужный MIME тип
                .contentLength((long) data.length)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
    }

    public byte[] getImageBinaryDataByUid(String imageUid) throws ImageNotFoundException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.getBUCKET())
                    .key(imageUid)  // imageUid должен соответствовать ключу объекта в бакете
                    .build();

            try (ResponseInputStream<?> s3Object = s3Client.getObject(getObjectRequest)) {
                return s3Object.readAllBytes();
            }
        } catch (NoSuchKeyException e) {
            throw new ImageNotFoundException("Image with UID " + imageUid + " not found" + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error reading image data", e);
        }
    }
}
