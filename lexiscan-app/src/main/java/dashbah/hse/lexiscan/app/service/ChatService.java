package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.dto.ChatHistoryRs;
import dashbah.hse.lexiscan.app.dto.CreateChatRs;
import dashbah.hse.lexiscan.app.dto.UserHistoryRs;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;

public interface ChatService {
    CreateChatRs createNewChat(String rquid, String username) throws UserNotFoundException;

    ChatHistoryRs getChatHistory(String rquid, String chatUID) throws ChatNotFoundException;

    UserHistoryRs getAllChatHistory(String rquid, String username) throws UserNotFoundException;
}
