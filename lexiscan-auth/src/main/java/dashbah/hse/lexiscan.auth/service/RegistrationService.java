package dashbah.hse.lexiscan.auth.service;

import dashbah.hse.lexiscan.auth.config.security.JwtService;
import dashbah.hse.lexiscan.auth.dto.UserDTO;
import dashbah.hse.lexiscan.auth.entity.Role;
import dashbah.hse.lexiscan.auth.entity.Token;
import dashbah.hse.lexiscan.auth.entity.UserEntity;
import dashbah.hse.lexiscan.auth.repository.TokenRepository;
import dashbah.hse.lexiscan.auth.repository.UserRepository;
import dashbah.hse.lexiscan.auth.util.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String registerClient(UserDTO userDTO) {
        if (userDTO.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("Can't register user with role: " + userDTO.getRole());
        }
        return registerUser(userDTO);
    }

    public String registerManagerOrAdmin(UserDTO userDTO) {
        if (userDTO.getRole() != Role.MANAGER && userDTO.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Can't register user with role: " + userDTO.getRole());
        }
        return registerUser(userDTO);
    }

    public String registerUser(UserDTO userDTO) throws AuthenticationException {
        if (userRepository.getByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        var user = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUsername(), userDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(authentication);

        return jwtToken;
    }

    private void saveUserToken(UserEntity savedUser, String jwtToken) {
        var token = Token.builder()
                .user(savedUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .isRevoked(false)
                .isExpired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public String authenticateUser(UserDTO userDTO) throws AuthenticationException {
        var user = userRepository.getByUsername(userDTO.getUsername());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username doesn't exists");
        }


        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUsername(), userDTO.getPassword(), user.get().getAuthorities()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var jwtToken = jwtService.generateToken(user.get());
        revokeAllUserTokens(user.get());
        saveUserToken(user.get(), jwtToken);


        // testing
        var auth = SecurityContextHolder.getContext().getAuthentication();
        //
        System.out.println();

        return jwtToken;
    }
}

