package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.ChatHistoryRs;
import dashbah.hse.lexiscan.app.dto.CreateChatRs;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static dashbah.hse.lexiscan.app.util.Util.generateUid;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
//    private final ChatHistoryService chatHistoryService;

    // TODO: fix giving 500 if token is expired
    @PostMapping(path = "/new")
    public ResponseEntity<String> createChat() {
        String rquid = generateUid();
        log.info(rquid, "Принят запрос на создание чата");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            {
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
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/{chatUId}/history")
    public ResponseEntity<ChatHistoryRs> getChatHistory(@RequestHeader String rquid, @PathVariable String chatUId) {
//        try {
//            ChatHistoryRs response = chatHistoryService.getChatHistory(rquid, chatUId);
        return ResponseEntity.ok().build();
//        }
//        catch (ChatNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
    }
}