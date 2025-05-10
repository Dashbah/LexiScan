package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.dto.client.auth.UserDTO;
import dashbah.hse.lexiscan.app.dto.client.auth.UserDTORs;
import dashbah.hse.lexiscan.app.dto.client.auth.UserUpdateRequest;
import dashbah.hse.lexiscan.app.entity.UserEntity;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.repository.wrapper.UserRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepositoryWrapper userRepositoryWrapper;

    private final PasswordEncoder passwordEncoder;

    public UserDTO updateUser(String username, UserUpdateRequest updateRequest) throws UserNotFoundException {
        UserEntity user = userRepositoryWrapper.findUserByUserName(username);

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank()) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        var savedUser = userRepositoryWrapper.saveUser(user);

        return UserDTO.builder()
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public UserDTORs getUserInfo(String username) throws UserNotFoundException {
        UserEntity user = userRepositoryWrapper.findUserByUserName(username);

        return UserDTORs.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public void deleteUserAccount(String username) throws UserNotFoundException {
        userRepositoryWrapper.findUserByUserName(username); // for checking if it exists
        userRepositoryWrapper.deleteUser(username);
    }
}
