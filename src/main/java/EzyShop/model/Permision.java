package EzyShop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permision {

    // USER permissions
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete"),

    // ADMIN permissions
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    // SELLER permissions
    SELLER_READ("seller:read"),
    SELLER_CREATE("seller:create"),
    SELLER_UPDATE("seller:update"),
    SELLER_DELETE("seller:delete"),

    // SUPER ADMIN permissions
    SUPER_ADMIN_ALL("super_admin:all");

    @Getter
    private final String permission;
}
