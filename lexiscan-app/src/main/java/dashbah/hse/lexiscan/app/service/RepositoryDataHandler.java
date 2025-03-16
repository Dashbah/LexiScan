package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;

public interface RepositoryDataHandler {
    Chat findChatByChatUId(String chatUid) throws ChatNotFoundException;
}
