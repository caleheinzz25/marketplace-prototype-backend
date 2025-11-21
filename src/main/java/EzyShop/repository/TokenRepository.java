package EzyShop.repository;

import java.time.Duration;

public interface TokenRepository {

    Duration getTTL();

    void storeRefreshToken(String userId, String tokenId, String userAgent);

    void deleteRefreshToken(String userId, String tokenId);

    boolean exists(String userId, String tokenId);
}
