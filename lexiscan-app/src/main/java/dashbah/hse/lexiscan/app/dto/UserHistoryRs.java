package dashbah.hse.lexiscan.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserHistoryRs {
    String rquid;
    String username;
    List<ChatHistoryRs> chatHistoryRs;
}
