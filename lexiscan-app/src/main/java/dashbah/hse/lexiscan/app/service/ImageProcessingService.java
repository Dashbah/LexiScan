package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.dto.client.imageprocessing.ImageProcessingRs;
import dashbah.hse.lexiscan.app.exception.ChatNotFoundException;

import java.io.IOException;

public interface ImageProcessingService {
    ImageProcessingRs processImage(String rquid, String chatUId, byte[] file, String fileName) throws ChatNotFoundException, IOException;
}
