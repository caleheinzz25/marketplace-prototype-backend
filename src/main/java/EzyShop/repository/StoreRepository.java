package EzyShop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import EzyShop.model.store.Store;
import java.util.List;
import EzyShop.model.User;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByOwner(User owner);

    boolean existsByStoreNameIgnoreCase(String storeName);

    @Query("SELECT s.storeNo FROM Store s ORDER BY s.id DESC LIMIT 1")
    Optional<String> findLastStoreNo();
}
