package EzyShop.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import EzyShop.model.carts.Cart;
import EzyShop.model.store.Store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String fullName;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

    private String userImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    // Relasi ke refresh/jwt tokens
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JwtToken> tokens = new ArrayList<>();

    // 🌟 Relasi ke cart: satu user bisa punya banyak cart (atau 1 cart jika desain
    // 1-to-1, ganti ke OneToOne)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Cart> carts = new ArrayList<>();

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private Store store;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Tambahkan Cart ke User, jaga dua arah hubungan
     */
    public void addCart(Cart cart) {
        carts.add(cart);
        cart.setUser(this);
    }

    /**
     * Remove Cart dari User
     */
    public void removeCart(Cart cart) {
        carts.remove(cart);
        cart.setUser(null);
    }
}
