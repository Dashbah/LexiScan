package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.dto.client.chat.ChatHistoryRs;
import dashbah.hse.lexiscan.app.dto.client.chat.CreateChatRs;
import dashbah.hse.lexiscan.app.dto.client.chat.UserHistoryRs;
import dashbah.hse.lexiscan.app.dto.client.imageprocessing.ImageProcessingRs;
import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.UserEntity;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;
import dashbah.hse.lexiscan.app.repository.MlRequestRepository;
import dashbah.hse.lexiscan.app.repository.wrapper.ChatRepositoryWrapper;
import dashbah.hse.lexiscan.app.repository.wrapper.UserRepositoryWrapper;
import dashbah.hse.lexiscan.app.service.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                .title("NewChat" + System.currentTimeMillis())
                .user(user)
                .build();

        Chat newChatEntity = chatRepositoryWrapper.save(chatEntity);
        log.info(rquid, newChatEntity);

        return CreateChatRs.builder()
                .rquid(rquid)
                .chatUId(newChatEntity.getChatUid())
                .build();
    }

    @Override
    @Transactional
    public ChatHistoryRs getChatHistory(String rquid, String chatUID) throws ChatNotFoundException {
        Chat chatEntity = chatRepositoryWrapper.findChatByChatUID(chatUID);

        List<ImageProcessingRs> imageRses = new ArrayList<>();

        for (var messageEntity : chatEntity.getMessages()) {
            var mlRequest = mlRequestRepository.findByImageUId(messageEntity.getImageUID());
            if (mlRequest != null && mlRequest.getStatus().equals("Completed")) {
                imageRses.add(ImageProcessingRs.builder()
                        .imageUploadedUId(mlRequest.getImageUId())
                        .imageResultUId(mlRequest.getResultImageUId())
                        .processingStatus(mlRequest.getStatus())
                        .build());
            }
        }

        return ChatHistoryRs.builder()
                .chatUId(chatUID)
                .chatName(chatEntity.getTitle())
                .imageProcessingImages(imageRses)
                .build();
    }

    @Override
    public void deleteChatHistory(String rquid, String chatUID) {
        chatRepositoryWrapper.deleteChat(chatUID);
        log.info("chat deleted with chatUId = " + chatUID);
    }

    @Override
    public UserHistoryRs getAllChatHistory(String rquid, String username) throws UserNotFoundException {
        UserEntity user = userRepositoryWrapper.findUserByUserName(username);

        var chats = chatRepositoryWrapper.findAllByUser(user);
        List<ChatHistoryRs> chatRses = new ArrayList<>();
        for (var chat : chats) {
            try {
                chatRses.add(getChatHistory(rquid, chat.getChatUid()));
            } catch (ChatNotFoundException e) {
                log.error(rquid + e.getMessage());
            }
        }

        return UserHistoryRs.builder()
                .rquid(rquid)
                .username(username)
                .chatHistoryRs(chatRses)
                .build();
    }
}
