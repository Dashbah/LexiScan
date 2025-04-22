package dashbah.hse.lexiscan.app.dto;

import dashbah.hse.lexiscan.app.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTORs {
    private String username;
    private String email;
    private Role role;
}
