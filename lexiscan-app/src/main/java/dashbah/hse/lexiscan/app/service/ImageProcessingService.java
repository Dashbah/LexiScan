package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.dto.ImageProcessingRs;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;

public interface ImageProcessingService {
    ImageProcessingRs processImage(String rquid, String chatUId, byte[] file) throws ChatNotFoundException;
}
