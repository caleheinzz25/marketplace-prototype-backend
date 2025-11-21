package EzyShop.model.orders;

import java.math.BigDecimal;

public enum ShippingType {
    STANDARD("Standard Shipping", "Delivery in 5-7 business days", new BigDecimal("20000")),
    EXPRESS("Express Shipping", "Delivery in 2-3 business days", new BigDecimal("44000")),
    OVERNIGHT("Overnight Shipping", "Delivery by tomorrow", new BigDecimal("70000"));

    private final String label;
    private final String info;
    private final BigDecimal cost;

    ShippingType(String label, String info, BigDecimal cost) {
        this.label = label;
        this.info = info;
        this.cost = cost;
    }

    public String getLabel() {
        return label;
    }

    public String getInfo() {
        return info;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
