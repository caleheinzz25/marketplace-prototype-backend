package EzyShop.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import EzyShop.config.security.UserDetailsImpl;
import EzyShop.dto.User.ProfileDto;
import EzyShop.dto.store.StoreRegistrationRequest;
import EzyShop.exception.*;
import EzyShop.mapper.UserMapper;
import EzyShop.model.Role;
import EzyShop.model.User;
import EzyShop.model.store.Store;
import EzyShop.repository.StoreRepository;
import EzyShop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final StoreRepository storeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
    }

    @Transactional
    public User registerUser(String username, String rawPassword, String email, String fullName) {
        log.info("Registering user: username={}, email={}", username, email);

        if (userRepository.existsByUsername(username)) {
            log.warn("Username '{}' already taken", username);
            throw new DuplicateResourceException("Username already taken");
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Email '{}' already registered", email);
            throw new DuplicateResourceException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(Role.USER); // default

        try {
            User savedUser = userRepository.save(user);
            log.info("User registered successfully: userId={}", savedUser.getId());
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation during registration", e);
            throw new IllegalStateException("Data constraint violated during user registration");
        }
    }

    public String getUserRole(Long userId) {
        return userRepository.findById(userId)
                .map(User::getRole)
                .map(Role::name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }

    public User loginUser(String username, String rawPassword) {
        log.info("Attempting login for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found for username '{}'", username);
                    return new AuthenticationException("Invalid username or password");
                });

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("Login failed: invalid password for username '{}'", username);
            throw new AuthenticationException("Invalid username or password");
        }

        log.info("Login successful for user: {}", username);
        return user;
    }

    public ProfileDto getUserProfile(String username) {
        log.info("Fetching profile for authenticated user");

        if (username == null || username.isBlank()) {
            log.warn("Unauthorized access attempt: missing credentials");
            throw new UnauthorizedException("Unauthorized or missing credentials");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found for username: {}", username);
                    return new ResourceNotFoundException("User not found");
                });

        log.info("Successfully fetched profile for user: {}", username);
        return userMapper.toProfileDto(user);
    }

    public ResponseEntity<?> updateUserProfile(String username, ProfileDto request) {
        log.info("Updating profile for user: {}", username);

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found for username: {}", username);
                        return new IllegalArgumentException("User not found");
                    });

            // Update only if values are provided
            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
            }
            if (request.getPhoneNumber() != null) {
                user.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }

            User updatedUser = userRepository.save(user);
            ProfileDto profileDto = userMapper.toProfileDto(updatedUser);

            log.info("Successfully updated profile for user: {}", username);
            return ResponseEntity.ok(Map.of("profile", profileDto));

        } catch (IllegalArgumentException e) {
            log.warn("Update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during profile update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user profile"));
        }
    }

    @Transactional
    public ResponseEntity<?> deleteUser(Long userId) {
        log.info("Attempting hard delete for user ID: {}", userId);

        return userRepository.findById(userId)
                .map(user -> {
                    try {
                        userRepository.delete(user);
                        log.info("User hard deleted: ID={}", userId);
                        return ResponseEntity.ok(Map.of("message", "User permanently deleted"));
                    } catch (DataIntegrityViolationException ex) {
                        log.error("Deletion failed due to constraint violation", ex);
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of("error", "Cannot delete user with linked data"));
                    }
                })
                .orElseGet(() -> {
                    log.warn("User not found for hard deletion: ID={}", userId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "User not found"));
                });
    }

    @Transactional
    public ResponseEntity<?> disableUser(Long userId) {
        log.info("Disabling (soft delete) user ID: {}", userId);

        return userRepository.findById(userId)
                .map(user -> {
                    if (!user.getEnabled()) {
                        log.info("User ID={} is already disabled", userId);
                        return ResponseEntity.ok(Map.of("message", "User already disabled"));
                    }

                    user.setEnabled(false);
                    userRepository.save(user);

                    log.info("User disabled successfully: ID={}", userId);
                    return ResponseEntity.ok(Map.of("message", "User disabled (soft delete)"));
                })
                .orElseGet(() -> {
                    log.warn("User not found for disable: ID={}", userId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "User not found"));
                });
    }

    @Transactional
    public User upgradeToSeller(Long userId, StoreRegistrationRequest storeDto) {
        log.info("Upgrading user ID {} to SELLER role", userId);

        if (!storeDto.getPassword().equals(storeDto.getConfirmPassword())) {
            throw new UnauthorizedException("Konfirmasi password tidak sesuai dengan password.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        if (user.getRole().equals(Role.SELLER)) {
            throw new UnauthorizedException("Anda sudah menjadi penjual");
        }

        // ✅ Validasi bahwa username di DTO harus sama dengan username user
        if (!user.getUsername().equalsIgnoreCase(storeDto.getUsername())) {
            throw new UnauthorizedException("Username tidak sesuai dengan akun Anda.");
        }

        if (!passwordEncoder.matches(storeDto.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid password for username '{}'", user.getUsername());
            throw new AuthenticationException("Invalid username or password");
        }

        // ✅ Validasi nama toko unik
        if (storeRepository.existsByStoreNameIgnoreCase(storeDto.getStoreName())) {
            throw new DuplicateResourceException("Nama toko sudah digunakan. Silakan pilih nama lain.");
        }

        if (user.getStore() == null) {
            String storeNo = generateStoreNo();

            Store store = Store.builder()
                    .storeName(storeDto.getStoreName())
                    .storeType(storeDto.getStoreType())
                    .storeEmail(storeDto.getStoreEmail())
                    .contactPhone(storeDto.getContactPhone())
                    .description(storeDto.getDescription())
                    .storeNo(storeNo)
                    .owner(user)
                    .build();

            user.setStore(store);
        }

        User updated = userRepository.save(user);
        log.info("User upgraded to seller successfully: userId={}, storeNo={}", updated.getId(),
                user.getStore().getStoreNo());
        return updated;
    }

    public String generateStoreNo() {
        Optional<String> lastNo = storeRepository.findLastStoreNo();
        int next = 1;

        if (lastNo.isPresent()) {
            String numberPart = lastNo.get().replaceAll("\\D+", "");
            next = Integer.parseInt(numberPart) + 1;
        }

        return String.format("ST%04d", next); // ST0001, ST0002, ...
    }
}
