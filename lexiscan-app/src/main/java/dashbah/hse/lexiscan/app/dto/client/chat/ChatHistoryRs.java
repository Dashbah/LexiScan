package dashbah.hse.lexiscan.app.dto.client.chat;

import dashbah.hse.lexiscan.app.dto.client.imageprocessing.ImageProcessingRs;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatHistoryRs {

    String chatUId;
    String chatName;
    List<ImageProcessingRs> imageProcessingImages;
}
