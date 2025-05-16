package dashbah.hse.lexiscan.app.repository;

import dashbah.hse.lexiscan.app.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByImageUID(String imageUID);
}
