package dashbah.hse.lexiscan.app.repository;

import dashbah.hse.lexiscan.app.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // TODO: check if we need to change it to (token.isExpired = false AND token.isRevoked = false)
    @Query("""
            select token from Token token inner join UserEntity user on token.user.id = user.id
            where user.id = :userId and (token.isExpired = false or token.isRevoked = false)
            """)
    List<Token> findAllValidTokensByUser(@Param("userId") Long userId);

    Optional<Token> findFirstByToken(String token);
}

