package EzyShop.controller;

import EzyShop.config.contstant.SuccessMessage;
import EzyShop.dto.User.ProfileDto;
import EzyShop.dto.User.UserDto;
import EzyShop.dto.store.StoreRegistrationRequest;
import EzyShop.exception.BusinessException;
import EzyShop.exception.InvalidTokenException;
import EzyShop.model.User;
import EzyShop.repository.RedisTokenRepository;
import EzyShop.service.JwtService;
import EzyShop.service.UserService;
import EzyShop.utils.JWTUtils;
import EzyShop.utils.ParsedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService; // Asumsi ada UserService untuk mengelola user
    private final JWTUtils jwtUtils;
    private final RedisTokenRepository redisTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto request,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response) {
        try {
            log.info("Login attempt for username: {}", request.getUsername());
            User user = userService.loginUser(request.getUsername(), request.getPassword());

            Optional<String> refreshTokenOpt = jwtService.generateRefreshToken(user.getId(), user.getUsername(),
                    user.getRole(), userAgent);

            String refreshToken = refreshTokenOpt.get();

            ParsedJWT parsed = jwtUtils.extractAllClaims(refreshToken);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofMillis(jwtUtils.getRefreshTokenExpiration()))
                    .sameSite("Lax") // atau "Strict" / "None" jika perlu
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            redisTokenRepository.storeRefreshToken(parsed.getUserId().toString(), parsed.getTokenId(), userAgent);

            log.info("Login success for username: {}", user.getUsername());
            return ResponseEntity.ok(Map.of("message", SuccessMessage.LOGIN_SUCCESS));
        } catch (IllegalArgumentException e) {
            log.warn("Login failed for username {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto request,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response) {
        try {
            log.info("Registering new user: {}", request.getUsername());
            User user = userService.registerUser(request.getUsername(), request.getPassword(), request.getEmail(),
                    request.getFullName());

            Optional<String> refreshTokenOpt = jwtService.generateRefreshToken(user.getId(), user.getUsername(),
                    user.getRole(), userAgent);

            if (refreshTokenOpt.isPresent()) {
                String refreshToken = refreshTokenOpt.get();

                ParsedJWT parsed = jwtUtils.extractAllClaims(refreshToken);

                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(Duration.ofMillis(jwtUtils.getRefreshTokenExpiration()))
                        .sameSite("Lax") // atau "Strict" / "None" jika perlu
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                redisTokenRepository.storeRefreshToken(parsed.getUserId().toString(), parsed.getTokenId(), userAgent);
            }

            log.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                    "message", SuccessMessage.REGISTRATION_SUCCESS));
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/register/seller")
    public ResponseEntity<?> upgradeToSeller(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestBody StoreRegistrationRequest store,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Missing or invalid refresh token");
        }

        ParsedJWT parsed = jwtUtils.extractAllClaims(refreshToken);

        User upgradedUser = userService.upgradeToSeller(parsed.getUserId(), store);

        String newRefreshToken = jwtService.generateRefreshToken(upgradedUser.getId(),
                upgradedUser.getUsername(), upgradedUser.getRole(), userAgent)
                .orElseThrow(() -> new BusinessException("Failed to generate new refresh token",
                        HttpStatus.INTERNAL_SERVER_ERROR));

        ParsedJWT newParsed = jwtUtils.extractAllClaims(newRefreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMillis(jwtUtils.getRefreshTokenExpiration()))
                .sameSite("Lax") // atau "Strict" / "None" jika perlu
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        redisTokenRepository.storeRefreshToken(
                newParsed.getUserId().toString(),
                newParsed.getTokenId(),
                userAgent);

        log.info("User {} upgraded to seller", upgradedUser.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "User upgraded to seller: " + upgradedUser.getUsername()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestHeader("User-Agent") String userAgent) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Missing refresh token");
        }

        ParsedJWT jwt = jwtUtils.extractAllClaims(refreshToken);

        String userId = jwt.getUserId().toString();
        String jti = jwt.getTokenId();
        String username = jwt.getSubject();
        String role = jwt.getRole().toString();

        boolean validInRedis = redisTokenRepository.exists(userId, jti);
        if (!validInRedis) {
            throw new InvalidTokenException("Invalid or blacklisted refresh token");
        }

        Optional<String> newAccessToken = jwtService.refreshAccessToken(refreshToken, userAgent);
        if (newAccessToken.isEmpty()) {
            throw new InvalidTokenException("Failed to refresh access token");
        }

        log.info("Access token refreshed successfully for user: {}", username);
        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken.get(),
                "role", role,
                "username", username));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String bearerToken) {
        try {
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                log.warn("Token validation failed: missing or invalid format");
                return ResponseEntity.badRequest().body(Map.of("message", "Missing token"));
            }

            String accessToken = bearerToken.substring(7);
            boolean isValid = jwtService.validateAccessToken(accessToken);
            log.info("Access token validation result: {}", isValid);

            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestHeader("User-Agent") String userAgent) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Missing refresh token");
        }

        if (userAgent == null || userAgent.isBlank()) {
            throw new InvalidTokenException("Missing User-Agent header");
        }

        String userId = String.valueOf(jwtUtils.extractUserId(refreshToken));
        String jti = jwtUtils.extractTokenId(refreshToken);

        if (!redisTokenRepository.exists(userId, jti)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String username = jwtUtils.extractUsername(refreshToken);
        if (username == null || username.isBlank()) {
            throw new InvalidTokenException("Invalid token payload");
        }

        ProfileDto profile = userService.getUserProfile(username);
        return ResponseEntity.ok(Map.of("profile", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestBody ProfileDto request) {
        String username = jwtUtils.extractUsername(refreshToken);
        return userService.updateUserProfile(username, request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            String userId = jwtUtils.extractUserId(refreshToken).toString();
            String jti = jwtUtils.extractTokenId(refreshToken);

            log.info("user logout");
            // Hapus dari Redis
            redisTokenRepository.deleteRefreshToken(userId, jti);

            // Hapus cookie di client
            Cookie cookie = new Cookie("refreshToken", "");
            cookie.setHttpOnly(true);
            cookie.setPath("/api/v1/auth");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            Long userId = jwtUtils.extractUserId(refreshToken);
            String jti = jwtUtils.extractTokenId(refreshToken);
            redisTokenRepository.deleteRefreshToken(userId.toString(), jti);
            userService.deleteUser(userId);
        }
        // Hapus user, dan bersihkan cookie

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "Account deleted"));
    }

  
}