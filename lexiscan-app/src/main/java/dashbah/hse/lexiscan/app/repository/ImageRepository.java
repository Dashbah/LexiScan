package dashbah.hse.lexiscan.app.repository;

import dashbah.hse.lexiscan.app.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
