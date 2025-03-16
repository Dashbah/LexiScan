package dashbah.hse.lexiscan.app.repository.wrapper;

import dashbah.hse.lexiscan.app.entity.UserEntity;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.repository.UserRepository;
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
