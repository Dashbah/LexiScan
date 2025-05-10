package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.dto.client.chat.ChatHistoryRs;
import dashbah.hse.lexiscan.app.dto.client.chat.CreateChatRs;
import dashbah.hse.lexiscan.app.dto.client.chat.UserHistoryRs;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;

public interface ChatService {
    CreateChatRs createNewChat(String rquid, String username) throws UserNotFoundException;

    ChatHistoryRs getChatHistory(String rquid, String chatUID) throws ChatNotFoundException;

    void deleteChatHistory(String rquid, String chatUID);

    UserHistoryRs getAllChatHistory(String rquid, String username) throws UserNotFoundException;
}
