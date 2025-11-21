package EzyShop.utils;

import java.util.Date;

import EzyShop.model.Role;
import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class ParsedJWT {

    private final Claims claims;
    
    public ParsedJWT(Claims claims) {
        this.claims = claims;
    }

    public String getSubject() {
        return claims.getSubject();
    }

    public Long getUserId() {
        return claims.get("userId", Long.class);
    }

    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    public String getTokenId() {
        return claims.getId();
    }

    public Date getExpiration() {
        return claims.getExpiration();
    }

    public boolean isExpired() {
        return getExpiration().before(new Date());
    }
}
