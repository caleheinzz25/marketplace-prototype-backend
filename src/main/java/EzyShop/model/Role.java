package EzyShop.model;

import static EzyShop.model.Permision.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    // USER permissions
    USER(Set.of(
            USER_READ,
            USER_CREATE,
            USER_DELETE,
            USER_UPDATE)),

    // SELLER permissions
    SELLER(Set.of(
            SELLER_READ,
            SELLER_CREATE,
            SELLER_DELETE,
            SELLER_UPDATE,
            USER_READ,
            USER_CREATE,
            USER_DELETE,
            USER_UPDATE)),

    // ADMIN permissions
    ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_CREATE,
            ADMIN_DELETE,
            ADMIN_UPDATE,
            USER_READ,
            USER_CREATE,
            USER_DELETE,
            USER_UPDATE)),

    // SUPER ADMIN permissions (all)
    SUPER_ADMIN(Set.of(Permision.values())); // memiliki semua permissions

    @Getter
    private final Set<Permision> permisions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(getPermisions()
                .stream()
                .map(permision -> new SimpleGrantedAuthority(permision.getPermission()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

    public static Role fromString(String value) {
        if (value == null || value.isEmpty()) {
            return USER; // default role
        }
        return Arrays.stream(Role.values())
                .filter(r -> r.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + value));
    }

}
