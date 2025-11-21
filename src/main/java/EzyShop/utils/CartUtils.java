package EzyShop.utils;

import EzyShop.dto.cart.CartDto;
import EzyShop.dto.cart.CartItemDto;

import java.math.BigDecimal;
import java.util.List;

public class CartUtils {

    /**
     * Update setiap CartItem dengan nilai subtotal = price * quantity.
     */
    public static void updateCartItemSubtotals(List<CartItemDto> items) {
        if (items == null)
            return;

        for (CartItemDto item : items) {
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            item.setSubtotal(price.multiply(BigDecimal.valueOf(quantity)));
        }
    }

    /**
     * Hitung subtotal cart berdasarkan item-item di dalamnya.
     */
    public static BigDecimal calculateCartSubtotal(List<CartItemDto> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(item -> {
                    BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Hitung total dari semua cart.
     * Jika cart tidak memiliki total, maka akan dihitung dari subtotal
     * item-itemnya.
     */
    public static BigDecimal calculateTotalFromCarts(List<CartDto> carts) {
        if (carts == null || carts.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return carts.stream()
                .map(cart -> {
                    if (cart.getTotal() != null) {
                        return cart.getTotal();
                    } else {
                        updateCartItemSubtotals(cart.getItems());
                        return calculateCartSubtotal(cart.getItems());
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Hitung total akhir seluruh cart (grand total) dengan tambahan pajak dan
     * ongkir.
     *
     * @param carts       daftar cart (per store)
     * @param tax         total pajak (bisa 0)
     * @param shippingFee total ongkir (bisa 0)
     * @return grand total = total cart + tax + shippingFee
     */
    public static BigDecimal calculateGrandTotal(List<CartDto> carts, BigDecimal tax, BigDecimal shippingFee) {
        BigDecimal cartTotal = calculateTotalFromCarts(carts);
        tax = tax != null ? tax : BigDecimal.ZERO;
        shippingFee = shippingFee != null ? shippingFee : BigDecimal.ZERO;

        return cartTotal.add(tax).add(shippingFee);
    }
}
