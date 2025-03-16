package dashbah.hse.lexiscan.auth.repository.wrapper;

import dashbah.hse.lexiscan.auth.entity.UserEntity;
import dashbah.hse.lexiscan.auth.exception.UserNotFoundException;
import dashbah.hse.lexiscan.auth.repository.UserRepository;
import dashbah.hse.lexiscan.auth.entity.UserEntity;
import dashbah.hse.lexiscan.auth.exception.UserNotFoundException;
import dashbah.hse.lexiscan.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRepositoryWrapper {
    private final UserRepository userRepository;

    public UserEntity findUserByUserName(String username) throws UserNotFoundException {
        return userRepository.getByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }
}
