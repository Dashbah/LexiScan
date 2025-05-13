package dashbah.hse.lexiscan.app.config.s3;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class S3Properties {
    @Value("${s3.key-id}")
    @NotNull
    private String KEY_ID;

    @Value("${s3.secret-key}")
    @NotNull
    private String SECRET_KEY;

    @Value("${s3.region}")
    private String REGION = "ru-central1";

    @Value("${s3.s3-endpoint}")
    private String S3_ENDPOINT = "https://storage.yandexcloud.net";

    @Value("${s3.bucket}")
    @NotNull
    private String BUCKET;
}
