package dashbah.hse.lexiscan.app.dto.client.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String rquid;
    private String email;
    private String password;
}
