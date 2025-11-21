package EzyShop.model.categories;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import EzyShop.model.User;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nama kategori (bisa unik atau tidak tergantung desain)
    @Column(nullable = false)
    private String name;

    // Slug atau penanda URL-friendly
    @Column(nullable = false, unique = true)
    private String slug;

    // Kategori parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // Kategori anak
    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> children = new HashSet<>();

    // Menyimpan informasi siapa yang membuat kategori (opsional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // Flag untuk membedakan predefined (admin) vs user-generated (seller)
    private boolean systemDefined;
}
