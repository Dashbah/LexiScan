package dashbah.hse.lexiscan.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ImageStorageProperties {
    @Value("${storage.url}")
    private String url;
    @Value("${storage.username}")
    private String username;
    @Value("${storage.password}")
    private String password;
}
