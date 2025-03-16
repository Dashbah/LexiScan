package dashbah.hse.lexiscan.auth.controller;

import dashbah.hse.lexiscan.auth.dto.UserDTO;
import dashbah.hse.lexiscan.auth.dto.UserUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @GetMapping
    public ResponseEntity<UserDTO> getAccount() {
        // Логика получения информации о текущем пользователе
        return ResponseEntity.ok(new UserDTO());
    }

    @PutMapping
    public ResponseEntity<String> updateAccount(@RequestBody UserUpdateRequest request) {
        // Логика обновления информации о пользователе
        return ResponseEntity.ok("Account updated");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAccount() {
        // Логика удаления аккаунта
        return ResponseEntity.noContent().build();
    }
}