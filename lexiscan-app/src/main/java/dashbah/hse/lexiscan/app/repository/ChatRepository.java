package dashbah.hse.lexiscan.app.repository;

import dashbah.hse.lexiscan.app.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByChatUid(String chatUId);
}