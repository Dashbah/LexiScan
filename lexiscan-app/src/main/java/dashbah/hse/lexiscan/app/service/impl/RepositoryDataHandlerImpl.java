package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.repository.ChatRepository;
import dashbah.hse.lexiscan.app.service.RepositoryDataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepositoryDataHandlerImpl implements RepositoryDataHandler {
    private final ChatRepository chatRepository;

    private final String CHAT_NOT_FOUND_MESSAGE = "Chat not found with chatUid = %s";

    public Chat findChatByChatUId(String chatUid) throws ChatNotFoundException {
        return chatRepository.findByChatUid(chatUid).orElseThrow(() ->
                new ChatNotFoundException(String.format(CHAT_NOT_FOUND_MESSAGE, chatUid)));
    }
}
