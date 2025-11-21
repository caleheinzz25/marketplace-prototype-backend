package EzyShop.model.carts;

import java.math.BigDecimal;

import EzyShop.model.products.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relasi dengan Cart (banyak item dalam satu cart)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /**
     * Relasi dengan Product (satu produk per item)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Jumlah produk yang dipesan
     */
    private int quantity;

    /**
     * Harga satuan produk saat ditambahkan ke cart
     */
    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSnapshot;

    /**
     * Hitung subtotal tanpa memperhitungkan diskon: price * quantity
     */
    public BigDecimal getSubtotal() {
        BigDecimal price = priceSnapshot != null ? priceSnapshot : BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
