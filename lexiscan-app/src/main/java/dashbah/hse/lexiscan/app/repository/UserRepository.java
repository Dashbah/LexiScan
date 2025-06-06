package dashbah.hse.lexiscan.app.repository;

import dashbah.hse.lexiscan.app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> getByUsername(String username);

    void deleteByUsername(String username);
}
