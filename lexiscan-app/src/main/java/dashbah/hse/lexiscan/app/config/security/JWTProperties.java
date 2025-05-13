package dashbah.hse.lexiscan.app.config.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JWTProperties {
    @Value("${jwt-security.key}")
    private String key;
}