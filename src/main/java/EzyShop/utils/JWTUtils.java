package EzyShop.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import EzyShop.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTUtils {

    private String secretKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

    public String generateAccessToken(Long userId, String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role.name());
        return buildToken(claims, username, accessTokenExpiration);
    }

    public String generateRefreshToken(Long userId, String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role.name());
        return buildToken(claims, username, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public Role extractRole(String token) {
        return extractClaim(token, claims -> Role.valueOf(claims.get("role", String.class)));
    }

    public String extractTokenId(String token) {
        return extractClaim(token, claims -> claims.getId());
    }

    public long getRefreshTokenExpiration() {
        return this.refreshTokenExpiration;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimResolver.apply(claims);
        } catch (Exception e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public ParsedJWT extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new ParsedJWT(claims);
        } catch (Exception e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            boolean expired = isTokenExpired(token);
            if (expired) {
                log.warn("Token expired at {}", extractExpiration(token));
            }
            return !expired;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // public boolean isRefreshTokenValid(String token) {
    // try {
    // if (!validateToken(token))
    // return false;

    // String userId = extractUserId(token).toString();
    // String jti = extractTokenId(token);

    // return redisTokenRepository.exists(userId, jti); // bisa diganti blacklist
    // check juga
    // } catch (Exception e) {
    // log.warn("Refresh token validation failed: {}", e.getMessage());
    // return false;
    // }
    // }
    public String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
