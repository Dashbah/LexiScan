package dashbah.hse.lexiscan.app.controller;

import dashbah.hse.lexiscan.app.dto.ImageProcessingRs;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;
import dashbah.hse.lexiscan.app.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static dashbah.hse.lexiscan.app.util.Util.generateUid;

@RestController
@RequestMapping("/api/model")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {
    private final ImageProcessingService imageProcessingService;

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ImageProcessingRs> uploadImage(@RequestParam String chatUId, @RequestPart("file") MultipartFile file) {
        String rquid = generateUid();
        log.info(rquid + ": Принят запрос на загрузку изображения");
        try {
            if (file == null || file.isEmpty()) {
                log.info(rquid + ": Нет файла в запросе");
                return ResponseEntity.badRequest().build();
            }

            // TODO: take user from context
            ImageProcessingRs rs = imageProcessingService.processImage(rquid, chatUId, file.getBytes(), file.getOriginalFilename());
            log.info(rquid + ": Изображение успешно обработано: " + rs);
            return ResponseEntity.ok(rs);
        } catch (ChatNotFoundException e) {
            log.warn(rquid, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(rquid, (Object) e.getStackTrace());
            log.error(e.getMessage() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}