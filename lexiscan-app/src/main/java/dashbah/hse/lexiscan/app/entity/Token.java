package dashbah.hse.lexiscan.app.entity;

import dashbah.hse.lexiscan.app.util.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean isExpired;
    private boolean isRevoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}