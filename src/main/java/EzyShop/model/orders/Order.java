package EzyShop.model.orders;

import EzyShop.model.User;
import EzyShop.model.store.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@SQLRestriction("status IN ('PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELED', 'PENDING_PAYMENT')")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "reference_id", unique = true, nullable = false)
    private String referenceId;
    // Owner
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Store relation
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // Transaction group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    // Item list
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelCode channelCode;

    // Biaya khusus per toko
    @Column(precision = 19, scale = 4)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 4)
    private BigDecimal shippingCost;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_type", nullable = false)
    private ShippingType shippingType;

    private Instant paymentReceivedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;  

    // === Helpers ===
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    public void calculateAndSetTotal() {
        // Hitung subtotal berdasarkan semua item
        this.subtotal = items.stream()
                .map(OrderItem::getSubtotal) // price * quantity
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Hitung totalAmount = subtotal + shippingCost + tax
        this.totalAmount = subtotal
                .add(shippingCost != null ? shippingCost : BigDecimal.ZERO);
    }

}
