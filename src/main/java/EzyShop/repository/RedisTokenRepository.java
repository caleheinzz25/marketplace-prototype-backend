package EzyShop.repository;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisTokenRepository implements TokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String userId, String tokenId) {
        return "refresh:" + userId + ":" + tokenId;
    }

    @Override
    public Duration getTTL() {
        return Duration.ofDays(7);
    }

    @Override
    public void storeRefreshToken(String userId, String tokenId, String userAgent) {
        redisTemplate.opsForValue().set(
                key(userId, tokenId),
                userAgent,
                getTTL().toMillis(),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteRefreshToken(String userId, String tokenId) {
        redisTemplate.delete(key(userId, tokenId));
    }

    @Override
    public boolean exists(String userId, String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(userId, tokenId)));
    }

    // 🔥 Hapus semua token milik user (misalnya saat disable/delete user)
    public void deleteAllTokensByUserId(Long userId) {
        String pattern = "refresh:" + userId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Deleted {} token(s) for userId={}", keys.size(), userId);
        } else {
            log.info("No tokens found for userId={}", userId);
        }
    }
}
