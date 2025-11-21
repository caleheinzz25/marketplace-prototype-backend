package EzyShop.config;

import EzyShop.model.Role;
import EzyShop.model.User;
import EzyShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String defaultEmail = "superadmin@niaganow.com";

        if (userRepository.existsByEmail(defaultEmail)) {
            log.info("Super admin already exists.");
            return;
        }

        User superAdmin = User.builder()
                .username("superadmin")
                .email(defaultEmail)
                .password(passwordEncoder.encode("superadmin")) // ganti ke env variable untuk keamanan
                .fullName("Super Admin")
                .role(Role.SUPER_ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(superAdmin);
        log.info("Default SUPER_ADMIN user created: {}", defaultEmail);
    }
}
