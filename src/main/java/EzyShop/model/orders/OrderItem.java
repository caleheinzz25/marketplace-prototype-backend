package EzyShop.model.orders;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Relasi ke Produk (tidak wajib digunakan di invoice/riwayat) ===
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "product_id")
    // private Product product;

    // === Relasi ke Order ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;

    // === Snapshot dari produk saat order dibuat ===
    private String productName;

    private Long productId;
    private String brand;
    private String productSku;
    private String productImage; // misalnya URL

    @Column(precision = 19, scale = 4)
    private BigDecimal price; // harga satuan saat pembelian

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Legacy support (jika masih pakai unitPrice di beberapa tempat)
    @Deprecated
    public double getUnitPrice() {
        return price.doubleValue();
    }
}
