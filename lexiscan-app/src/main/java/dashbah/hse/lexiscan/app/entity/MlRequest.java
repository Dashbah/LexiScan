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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "m1_request_seq")
    @SequenceGenerator(name = "m1_request_seq", sequenceName = "m1_request_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String rquid;

    @Column
    private String status;

    @Column
    private Double percentage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;
}
