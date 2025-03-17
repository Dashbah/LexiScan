package dashbah.hse.lexiscan.app.service.impl;

import dashbah.hse.lexiscan.app.config.ImageStorageProperties;
import dashbah.hse.lexiscan.app.entity.Image;
import dashbah.hse.lexiscan.app.exception.ImageNotFoundException;
import dashbah.hse.lexiscan.app.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageStorageProperties imageStorageProperties;

    /**
     * Сохраняет изображение в базу данных.
     *
     * @param image Сущность изображения.
     */
    public void saveImage(Image image) {
        String sql = "INSERT INTO image (image_uid, body, message_id) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(imageStorageProperties.getUrl(), imageStorageProperties.getUsername(), imageStorageProperties.getPassword());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Устанавливаем параметры
            ps.setString(1, image.getImageUid());
            ps.setBytes(2, image.getBody());  // Используем setBytes для byte[]
            ps.setLong(3, image.getMessage().getId());

            // Выполняем запрос
            ps.executeUpdate();
            System.out.println("Изображение успешно сохранено в базу данных.");

        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении изображения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Извлекает бинарные данные изображения по его уникальному идентификатору (image_uid).
     *
     * @param imageUid Уникальный идентификатор изображения.
     * @return Бинарные данные изображения.
     * @throws ImageNotFoundException Если изображение не найдено.
     */
    public byte[] getImageBinaryDataByUid(String imageUid) throws ImageNotFoundException {
        String sql = "SELECT body FROM image WHERE image_uid = ?";

        try (Connection conn = DriverManager.getConnection(imageStorageProperties.getUrl(), imageStorageProperties.getUsername(), imageStorageProperties.getPassword());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Устанавливаем параметр image_uid
            ps.setString(1, imageUid);

            // Выполняем запрос
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Извлекаем бинарные данные
                    return rs.getBytes("body");
                } else {
                    throw new ImageNotFoundException("Изображение с image_uid = " + imageUid + " не найдено.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при извлечении изображения: " + e.getMessage(), e);
        }
    }
}
