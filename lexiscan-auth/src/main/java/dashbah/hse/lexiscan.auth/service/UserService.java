package dashbah.hse.lexiscan.auth.service;

import dashbah.hse.lexiscan.auth.dto.UserRegistrationRequest;
import dashbah.hse.lexiscan.auth.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    public UserEntity registerUser(UserRegistrationRequest userRegistrationRequest);
}
