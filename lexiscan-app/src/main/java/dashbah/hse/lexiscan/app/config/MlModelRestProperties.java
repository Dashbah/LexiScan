package dashbah.hse.lexiscan.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MlModelRestProperties {
    @Value("${model.url}")
    private String mlModelUrl;
}
