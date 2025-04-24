package dashbah.hse.lexiscan.app.repository.wrapper;

import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.UserEntity;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ChatRepositoryWrapper {
    private final ChatRepository chatRepository;

    public Chat findChatByChatUID(String chatUId) throws ChatNotFoundException {
        return chatRepository.findByChatUid(chatUId).orElseThrow(() -> new ChatNotFoundException(chatUId));
    }

    public Chat save(Chat chat) {
        return chatRepository.save(chat);
    }

    public List<Chat> findAllByUser(UserEntity user) {
        return chatRepository.findAllByUser(user);
    }
}
