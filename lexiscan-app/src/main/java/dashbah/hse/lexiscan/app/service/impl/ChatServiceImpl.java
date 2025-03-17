package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.dto.CreateChatRs;
import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.UserEntity;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.repository.ChatRepository;
import dashbah.hse.lexiscan.app.repository.wrapper.UserRepositoryWrapper;
import dashbah.hse.lexiscan.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepositoryWrapper userRepositoryWrapper;

    @Override
    public CreateChatRs createNewChat(String rquid, String username) throws UserNotFoundException {
        UserEntity user = userRepositoryWrapper.findUserByUserName(username);

        Chat chat = Chat.builder()
                .chatUid("chat_" + System.currentTimeMillis())
                .title("new chat")
                .user(user)
                .build();

        Chat newChat = chatRepository.save(chat);
        log.info(rquid, newChat);

        return CreateChatRs.builder()
                .rquid(rquid)
                .chatUId(newChat.getChatUid())
                .build();
    }
}
