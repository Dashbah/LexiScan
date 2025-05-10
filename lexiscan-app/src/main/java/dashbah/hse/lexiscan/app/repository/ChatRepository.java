package dashbah.hse.lexiscan.app.repository;

import dashbah.hse.lexiscan.app.entity.Chat;
import dashbah.hse.lexiscan.app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByChatUid(String chatUId);

    List<Chat> findAllByUser(UserEntity user);

    void deleteByChatUid(String chatUId);
}