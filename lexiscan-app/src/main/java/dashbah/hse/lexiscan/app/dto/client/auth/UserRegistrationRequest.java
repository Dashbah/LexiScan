package dashbah.hse.lexiscan.app.dto.client.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
}