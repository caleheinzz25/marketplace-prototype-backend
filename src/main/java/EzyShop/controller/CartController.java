package EzyShop.controller;

import EzyShop.dto.cart.CartDto;
import EzyShop.dto.cart.CartItemDto;
import EzyShop.service.CartService;
import EzyShop.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final JWTUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getFilteredCarts(
            HttpServletRequest request,
            @RequestParam(name = "cartItemIds", required = false) List<Long> cartItemIds) {
        String token = jwtUtils.extractAccessToken(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            log.warn("Invalid or missing access token");
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Long userId = jwtUtils.extractUserId(token);

        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            log.info("Fetching filtered carts for userId={} with cartItemIds={}", userId, cartItemIds);
            List<CartDto> carts = cartService.getCartsByCartItemIds(userId, cartItemIds);
            return ResponseEntity.ok(Map.of("carts", carts));
        } else {
            log.info("Fetching all carts for userId={}", userId);
            List<CartDto> carts = cartService.getAllCarts(userId);
            return ResponseEntity.ok(Map.of("carts", carts));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(
            HttpServletRequest request,
            @RequestBody CartItemDto cartItemDto) {

        String token = jwtUtils.extractAccessToken(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            log.warn("Invalid or missing access token");
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Long userId = jwtUtils.extractUserId(token);
        log.info("Adding productId={} to cart for userId={}, quantity={}",
                cartItemDto.getProductId(), userId, cartItemDto.getQuantity());

        CartDto updatedCart = cartService.addCart(userId, cartItemDto);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        log.info("Removing cart item with id={}", cartItemId);
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/items/{cartItemId}/increase")
    public ResponseEntity<CartDto> increaseQuantity(
            @PathVariable Long cartItemId,
            @RequestParam int amount) {
        log.info("Increasing quantity of cart item id={} by amount={}", cartItemId, amount);
        CartDto updatedCart = cartService.increaseQuantity(cartItemId, amount);
        return ResponseEntity.ok(updatedCart);
    }

}
