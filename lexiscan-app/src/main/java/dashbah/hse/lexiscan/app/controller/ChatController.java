package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.client.chat.ChatHistoryRs;
import dashbah.hse.lexiscan.app.dto.client.chat.CreateChatRs;
import dashbah.hse.lexiscan.app.dto.client.chat.UserHistoryRs;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static dashbah.hse.lexiscan.app.util.Util.generateUid;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping(path = "/new")
    public ResponseEntity<CreateChatRs> createChat() {
        String rquid = generateUid();
        log.info(rquid + ": Принят запрос на создание чата");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            {
                try {
                    // TODO: take user from context
                    CreateChatRs rs = chatService.createNewChat(rquid, username);
                    log.info(rquid, "Чат успешно создан: " + rs);
                    return ResponseEntity.ok(rs);
                } catch (UserNotFoundException e) {
                    log.warn(rquid, e.getMessage());
                    return ResponseEntity.badRequest().build();
                } catch (Exception e) {
                    log.error(rquid + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/{chatUId}/history")
    public ResponseEntity<ChatHistoryRs> getChatHistory(@PathVariable String chatUId) {
        var rquid = generateUid();
        log.info(rquid + ": запрос на получение истории чатов");
        try {
            ChatHistoryRs response = chatService.getChatHistory(rquid, chatUId);
            return ResponseEntity.ok(response);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("rquid = " + rquid + " " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{chatUId}")
    public ResponseEntity<Void> deleteChatHistory(@PathVariable String chatUId) {
        var rquid = generateUid();
        log.info(rquid + ": запрос на удаление чата");
        try {
            chatService.deleteChatHistory(rquid, chatUId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("rquid = " + rquid + " " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history/all-user-history")
    public ResponseEntity<UserHistoryRs> getChatHistory() {
        var rquid = generateUid();
        log.info(rquid + ": запрос на получение истории всех чатов");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            try {
                UserHistoryRs response = chatService.getAllChatHistory(rquid, username);
                return ResponseEntity.ok(response);
            } catch (UserNotFoundException e) {
                log.warn(rquid, e.getMessage());
                return ResponseEntity.badRequest().build();
            } catch (Exception e) {
                log.error(rquid, (Object) e.getStackTrace());
                log.error(e.getMessage() + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(401).build();
    }
}