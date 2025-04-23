package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.dto.CreateChatRs;
import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.UserEntity;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.repository.MlRequestRepository;
import dashbah.hse.lexiscan.app.repository.wrapper.ChatRepositoryWrapper;
import dashbah.hse.lexiscan.app.repository.wrapper.UserRepositoryWrapper;
import dashbah.hse.lexiscan.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepositoryWrapper chatRepositoryWrapper;
    private final UserRepositoryWrapper userRepositoryWrapper;
    private final MlRequestRepository mlRequestRepository;

    @Override
    public CreateChatRs createNewChat(String rquid, String username) throws UserNotFoundException {
        UserEntity user = userRepositoryWrapper.findUserByUserName(username);

        Chat chatEntity = Chat.builder()
                .chatUid("chat_" + System.currentTimeMillis())
                .title("new chat")
                .user(user)
                .build();

        Chat newChatEntity = chatRepositoryWrapper.save(chatEntity);
        log.info(rquid, newChatEntity);

        return CreateChatRs.builder()
                .rquid(rquid)
                .chatUId(newChatEntity.getChatUid())
                .build();
    }

//    @Override
//    public ChatHistoryRs getChatHistory(String rquid, String chatUID) throws ChatNotFoundException {
//        ChatEntity chatEntity = chatRepositoryWrapper.findChatByChatUID(chatUID);
//
//        List<ChatHistoryRs.ImageProcessingRsWithImage> images = chatEntity.getMessages().stream()
//                .flatMap(image -> image.getImage().stream())
//                .map(this::mapImageToDto)
//                .collect(Collectors.toList());
//
//        return ChatHistoryRs.builder()
//                .rquid(rquid)
//                .chatUId(chatUID)
//                .chatName(chatEntity.getTitle())
//                .imageProcessingRsWithImages(images)
//                .build();
//    }
}
