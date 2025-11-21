package EzyShop.model.orders;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Snapshot
    private String productName;
    private String productImage;
    private String brand;
    private String category;
    private int quantity;
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    // Optional link to actual product (not recommended as source of truth)
    private Long productId;

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

}
