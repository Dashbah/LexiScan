package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.CreateChatRs;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // TODO: fix giving 500 if token is expired
    @PostMapping(path = "/new")
    public ResponseEntity<String> createChat(@RequestParam String username, @RequestHeader String rquid) {
        log.info(rquid, "Принят запрос на создание чата");
        try {
            // TODO: take user from context
            CreateChatRs rs = chatService.createNewChat(rquid, username);
            log.info(rquid, "Чат успешно создан: " + rs);
            return ResponseEntity.ok(rs.toString());
        } catch (UserNotFoundException e) {
            log.warn(rquid, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error(rquid + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating chat");
        }
    }
}