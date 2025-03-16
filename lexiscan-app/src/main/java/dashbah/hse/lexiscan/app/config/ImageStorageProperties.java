package dashbah.hse.lexiscan.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ImageStorageProperties {
    @Value("${file.uploadingDirectory}")
    private String uploadingDirectory;
}
