package EzyShop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import EzyShop.model.JwtToken;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    // Define any custom query methods if needed
    // For example, to find a token by its value:
    // Optional<JwtToken> findByToken(String token);

    // You can also define methods to delete tokens by user ID or other criteria if
    // necessary

    Optional<JwtToken> findTokenVersionByUserIdAndUserAgent(Long userId, String userAgent);

}
