package dashbah.hse.lexiscan.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageProcessingRs {
    String rquid;
    Double confidence;
    String prediction;
}
