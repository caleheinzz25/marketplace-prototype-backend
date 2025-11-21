    package EzyShop.model.orders;

    import EzyShop.dto.payment.ActionDto;
    import EzyShop.model.User;
    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.math.BigDecimal;
    import java.time.Instant;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "transactions")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Transaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "reference_id", unique = true, nullable = false)
        private String referenceId;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Builder.Default
        @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Order> orders = new ArrayList<>();

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PaymentStatus status;

        @Column(precision = 19, scale = 4)
        private BigDecimal totalAmount;

        @Column(precision = 19, scale = 4)
        private BigDecimal subTotal;

        @Column(precision = 10, scale = 4)
        private BigDecimal shippingCost;

        @Column(precision = 10, scale = 4)
        private BigDecimal tax;

        @Convert(converter = ActionListConverter.class)
        @Column(columnDefinition = "TEXT")
        private List<ActionDto> actions;

        private String actionValue;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ChannelCode channelCode;

        @Embedded
        private ShippingInfoSnapshot shippingInfo;

        private Instant expiresAt;
        private Instant paymentCreatedAt;
        private Instant paymentReceivedAt;
        private Instant paymentUpdatedAt;

        @CreationTimestamp
        @Column(updatable = false)
        private Instant createdAt;

        @UpdateTimestamp
        private Instant updatedAt;

        // === Helper Methods ===
        public void addOrder(Order order) {
            orders.add(order);
            order.setTransaction(this);
        }

        public void calculateSubTotal() {
            this.subTotal = orders.stream()
                    .map(Order::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public void calculateTotalAmount() {
            this.totalAmount = orders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(tax != null ? tax : BigDecimal.ZERO);
        }

        public void calculateShippingCost() {
            this.shippingCost = orders.stream()
                    .map(Order::getShippingCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
