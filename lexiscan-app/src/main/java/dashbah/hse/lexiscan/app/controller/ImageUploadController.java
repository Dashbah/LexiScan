package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.ImageProcessingRs;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/model")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {
    private final ImageProcessingService imageProcessingService;

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(@RequestParam String chatUId, @RequestParam String username, @RequestHeader String rquid, @RequestPart("file") MultipartFile file) {
        log.info(rquid, "Принят запрос на загрузку изображения");
        try {
            if (file == null || file.isEmpty()) {
                log.info(rquid, "Нет файла в запросе");
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            // TODO: take user from context
            ImageProcessingRs rq = imageProcessingService.processImage(rquid, chatUId, file.getBytes());
            log.info(rquid, "Изображение успешно обработано: " + rq);
            return ResponseEntity.ok(rq.toString());
        } catch (ChatNotFoundException e) {
            log.warn(rquid, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error(rquid, (Object) e.getStackTrace());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }
}