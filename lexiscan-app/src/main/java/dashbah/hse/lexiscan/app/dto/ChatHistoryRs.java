package dashbah.hse.lexiscan.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatHistoryRs {

    String chatUId;
    String chatName;
    List<ImageProcessingRsWithImage> imageProcessingImages;

    @Data
    @Builder
    public static class ImageProcessingRsWithImage {
        String imageUId;
        String percentage;
        String processingStatus;
    }
}
