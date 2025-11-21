package EzyShop.model.products;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    private String comment;

    @Column(name = "review_date")
    private OffsetDateTime date;

    private String reviewerName;

    private String reviewerEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = OffsetDateTime.now(ZoneOffset.UTC); // Konsisten waktu global
        }
    }

}
