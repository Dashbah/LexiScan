package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.config.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class ImageUploadController {
    private final ImageStorageProperties imageStorageProperties;

    @PostMapping(path = "/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(@RequestPart("file") MultipartFile file) {
        System.out.println("Принят запрос на загрузку изображения");
        try {
            if (file == null || file.isEmpty()) {
                System.out.println("Нет файла в запросе");
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            String filePath = saveImage(file);
            System.out.println("Изображение успешно загружено");
            return ResponseEntity.ok("Image uploaded successfully: " + filePath);
        } catch (IOException e) {
            System.out.println("Произошла ошибка загрузки изображения");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(imageStorageProperties.getUploadingDirectory());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
}