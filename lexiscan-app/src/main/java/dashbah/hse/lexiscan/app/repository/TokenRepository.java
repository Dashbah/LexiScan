package dashbah.hse.lexiscan.app.repository;

import dashbah.hse.lexiscan.app.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
            select token from Token token inner join UserEntity user on token.user.id = user.id
            where user.id = :userId and (token.isExpired = false or token.isRevoked = false)
            """)
    List<Token> findAllValidTokensByUser(Long userId);

    Optional<Token> findFirstByToken(String token);
}

