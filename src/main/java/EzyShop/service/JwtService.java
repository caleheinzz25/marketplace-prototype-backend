package EzyShop.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import EzyShop.exception.InvalidTokenException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.model.JwtToken;
import EzyShop.model.Role;
import EzyShop.model.User;
import EzyShop.repository.JwtTokenRepository;
import EzyShop.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JWTUtils jwtUtils;
    private final JwtTokenRepository jwtTokenRepository;

    @Transactional
    public Optional<String> generateAccessToken(Long userId, String username, Role role, String userAgent) {
        try {
            String token = jwtUtils.generateAccessToken(userId, username, role);
            return Optional.of(token);
        } catch (Exception e) {
            log.error("Failed to generate access token", e);
            throw new InvalidTokenException("Failed to generate access token");
        }
    }

    public Optional<String> generateRefreshToken(Long userId, String username, Role role, String userAgent) {
        JwtToken jwtToken = jwtTokenRepository
                .findTokenVersionByUserIdAndUserAgent(userId, userAgent)
                .orElse(null);

        if (jwtToken == null) {
            jwtToken = new JwtToken();
            jwtToken.setUser(User.builder().id(userId).build());
            jwtToken.setUserAgent(userAgent);
        }

        try {
            String refreshToken = jwtUtils.generateRefreshToken(userId, username, role);
            jwtToken.setRefreshToken(refreshToken);
            jwtTokenRepository.save(jwtToken);
            return Optional.of(refreshToken);
        } catch (Exception e) {
            log.error("Failed to generate refresh token", e);
            throw new InvalidTokenException("Failed to generate refresh token");
        }
    }

    public boolean validateAccessToken(String token) {
        try {
            return jwtUtils.validateToken(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            throw new InvalidTokenException("Access token is invalid");
        }
    }

    public Optional<String> refreshAccessToken(String refreshToken, String userAgent) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        Long userId = jwtUtils.extractUserId(refreshToken);
        String username = jwtUtils.extractUsername(refreshToken);
        Role role = jwtUtils.extractRole(refreshToken);

        JwtToken storedToken = jwtTokenRepository
                .findTokenVersionByUserIdAndUserAgent(userId, userAgent)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found for user"));

        if (!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new InvalidTokenException("Refresh token mismatch or invalid");
        }

        try {
            String newAccessToken = jwtUtils.generateAccessToken(userId, username, role);
            return Optional.of(newAccessToken);
        } catch (Exception e) {
            log.error("Failed to refresh access token", e);
            throw new InvalidTokenException("Failed to refresh access token");
        }
    }
}