package EzyShop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import EzyShop.model.carts.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Define any custom query methods if needed
    // For example, to find a cart by user ID:
    // Optional<Cart> findByUserId(Long userId);

    // You can also define methods to delete carts by user ID or other criteria if
    // necessary
    // Optional<Cart> deleteByUserId(Long userId);

    List<Cart> findByUserId(Long userId);

    Optional<Cart> findByUserIdAndCheckedOutFalse(Long userId);
}
