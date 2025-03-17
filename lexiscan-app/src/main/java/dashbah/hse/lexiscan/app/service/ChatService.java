package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.dto.CreateChatRs;
import dashbah.hse.lexiscan.app.exception.UserNotFoundException;

public interface ChatService {
    CreateChatRs createNewChat(String rquid, String username) throws UserNotFoundException;
}
