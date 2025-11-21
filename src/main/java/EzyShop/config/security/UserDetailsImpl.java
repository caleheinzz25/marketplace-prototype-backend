package EzyShop.config.security;

import EzyShop.model.Role;
import EzyShop.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Implementasi UserDetails untuk menyimpan informasi pengguna yang diperlukan
 * oleh Spring Security.
 */
public class UserDetailsImpl implements UserDetails {
    private final User user;

    /**
     * Konstruktor untuk membuat instance UserDetailsImpl.
     *
     * @param user Objek User yang akan disimpan.
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Mengembalikan otoritas (peran) yang dimiliki oleh pengguna.
     *
     * @return Koleksi GrantedAuthority yang sesuai dengan role pengguna.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().getAuthorities();
    }

    /**
     * Mengembalikan password pengguna.
     *
     * @return Password pengguna.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Mengembalikan username pengguna.
     *
     * @return Username pengguna.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Memeriksa apakah akun pengguna belum kedaluwarsa.
     *
     * @return True jika akun belum kedaluwarsa, false jika sudah.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Akun tidak pernah kedaluwarsa
    }

    /**
     * Memeriksa apakah akun pengguna tidak terkunci.
     *
     * @return True jika akun tidak terkunci, false jika terkunci.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Akun tidak pernah terkunci
    }

    /**
     * Memeriksa apakah kredensial pengguna belum kedaluwarsa.
     *
     * @return True jika kredensial belum kedaluwarsa, false jika sudah.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Kredensial tidak pernah kedaluwarsa
    }

    /**
     * Memeriksa apakah akun pengguna aktif.
     *
     * @return True jika akun aktif, false jika tidak.
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    /**
     * Mengembalikan ID pengguna.
     *
     * @return ID pengguna.
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * Mengembalikan role pengguna.
     *
     * @return Role pengguna.
     */
    public Role getRole() {
        return user.getRole();
    }

    /**
     * Mengembalikan objek User yang disimpan.
     *
     * @return Objek User.
     */
    public User getUser() {
        return user;
    }
}
