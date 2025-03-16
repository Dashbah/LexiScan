package dashbah.hse.lexiscan.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
}