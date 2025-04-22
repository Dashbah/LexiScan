package dashbah.hse.lexiscan.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String rquid;
    private String email;
    private String password;
}
