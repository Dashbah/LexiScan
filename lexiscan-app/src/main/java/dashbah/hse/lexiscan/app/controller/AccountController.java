package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.UserDTORs;
import dashbah.hse.lexiscan.app.dto.UserUpdateRequest;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/account")
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountController {

    private final UserServiceImpl userService;

    @GetMapping
    public ResponseEntity<UserDTORs> getAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            try {
                return ResponseEntity.ok(userService.getUserInfo(username));
            } catch (UserNotFoundException e) {
                log.warn(e.getMessage());
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                log.error(Arrays.toString(e.getStackTrace()));
                log.error(e.getMessage() + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PutMapping
    public ResponseEntity<String> updateAccount(@RequestBody UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

            try {
                userService.updateUser(username, request);
            } catch (UserNotFoundException e) {
                log.warn(request.getRquid(), e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                log.error(Arrays.toString(e.getStackTrace()));
                log.error(e.getMessage() + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
            }

            return ResponseEntity.ok("Account updated for user: " + username);
        }

        return ResponseEntity.status(401).build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            try {
                userService.deleteUserAccount(username);
                return ResponseEntity.ok(String.format("user with username %s deleted", username));
            } catch (UserNotFoundException e) {
                log.warn(e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                log.error(Arrays.toString(e.getStackTrace()));
                log.error(e.getMessage() + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
            }
        }
        return ResponseEntity.status(401).build();
    }
}