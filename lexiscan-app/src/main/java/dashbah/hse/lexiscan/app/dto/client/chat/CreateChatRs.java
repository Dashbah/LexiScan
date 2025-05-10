package dashbah.hse.lexiscan.app.dto.client.chat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatRs {
    String rquid;
    String chatUId;
}
