package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.dto.UserRegistrationRequest;
import dashbah.hse.lexiscan.app.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    public UserEntity registerUser(UserRegistrationRequest userRegistrationRequest);
}
