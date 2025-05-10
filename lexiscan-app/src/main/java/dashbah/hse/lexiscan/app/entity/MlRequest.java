package dashbah.hse.lexiscan.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ml_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MlRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ml_request_seq")
    @SequenceGenerator(name = "ml_request_seq", sequenceName = "ml_request_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String rquid;

    // ERROR
    // PENDING
    // COMPLETED
    // REST_ERROR
    @Column
    private String status;

    @Column
    private Double percentage;

    @Column(name = "image_uid")
    private String imageUId;

    @Column(name = "result_image_uid")
    private String resultImageUId;
}
