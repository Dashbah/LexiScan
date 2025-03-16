package dashbah.hse.lexiscan.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MlModelRs {
    String rquid;
    byte[] image;
    double percentage;
}
