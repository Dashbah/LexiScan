package dashbah.hse.lexiscan.app.dto.client.imageprocessing;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageProcessingRs {
    String imageUploadedUId;
    String imageResultUId;
    String processingStatus;
}
