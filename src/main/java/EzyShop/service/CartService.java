package EzyShop.service;

import EzyShop.dto.cart.CartDto;
import EzyShop.dto.cart.CartItemDto;
import EzyShop.exception.BusinessException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.mapper.CartMapper;
import EzyShop.model.User;
import EzyShop.model.carts.Cart;
import EzyShop.model.carts.CartItem;
import EzyShop.model.products.Product;
import EzyShop.model.store.Store;
import EzyShop.repository.CartItemRepository;
import EzyShop.repository.CartRepository;
import EzyShop.repository.ProductRepository;
import EzyShop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Transactional
    public List<CartDto> getAllCarts(Long userId) {
        log.debug("Getting all carts for userId={}", userId);
        return cartRepository.findByUserId(userId).stream()
                .map(cartMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CartDto> getCartsByCartItemIds(Long userId, List<Long> cartItemIds) {
        log.debug("Getting carts for userId={} and cartItemIds={}", userId, cartItemIds);
        List<Cart> carts = cartRepository.findByUserId(userId);

        return cartMapper.toListDto(carts);
    }

    @Transactional
    public CartDto addCart(Long userId, CartItemDto itemDto) {
        log.debug("Adding item to cart: userId={}, productId={}, quantity={}",
                userId, itemDto.getProductId(), itemDto.getQuantity());

        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Store store = product.getStore();
        if (store == null) {
            throw new IllegalStateException("Product does not have a store assigned");
        }

        Cart cart = cartRepository.findByUserIdAndCheckedOutFalse(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .store(store)
                            .checkedOut(false)
                            .build();
                    return cartRepository.save(newCart);
                });

        log.info("cart id={}", cart.getId());

        if (itemDto.getQuantity() > product.getStock()) {
            throw new BusinessException("Quantity exceeds available stock", HttpStatus.BAD_REQUEST);
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(itemDto.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem item = CartItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .priceSnapshot(product.getPrice())
                    .cart(cart)
                    .build();
            cart.addItem(item);
        }

        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        log.debug("Deleting cart item with id={}", cartItemId);
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public CartDto increaseQuantity(Long cartItemId, int amount) {
        log.debug("Increasing quantity of cart item id={} by {}", cartItemId, amount);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        item.setQuantity(item.getQuantity() + amount);
        cartItemRepository.save(item);

        return cartMapper.toDto(item.getCart());
    }
}
