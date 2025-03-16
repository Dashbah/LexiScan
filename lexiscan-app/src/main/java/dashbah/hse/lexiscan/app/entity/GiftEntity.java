package dashbah.hse.lexiscan.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gift")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GiftEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "gift_uid")
    private String giftUId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
