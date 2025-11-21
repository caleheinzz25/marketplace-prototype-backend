package EzyShop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import EzyShop.model.orders.Order;
import EzyShop.model.store.Store;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByReferenceId(String referenceId);

    List<Order> findAllByUser_Id(Long userId);

    List<Order> findByStore(Store store);

}
