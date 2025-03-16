package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.UserDTO;
import dashbah.hse.lexiscan.app.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegistrationService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        if (userDTO == null) {
            return ResponseEntity.badRequest().body("UserDTO should not be null");
        }
        try {
            return ResponseEntity.ok(service.registerClient(userDTO));
        } catch (IllegalArgumentException | AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/admin")
    public ResponseEntity<String> registerManager(@RequestBody UserDTO userDTO) {
        if (userDTO == null) {
            return ResponseEntity.badRequest().body("UserDTO should not be null");
        }
        try {
            return ResponseEntity.ok(service.registerManagerOrAdmin(userDTO));
        } catch (IllegalArgumentException | AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody UserDTO userDTO) {
        try {
            System.out.println("Hello from authenticate");
            return ResponseEntity.ok(service.authenticateUser(userDTO));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}