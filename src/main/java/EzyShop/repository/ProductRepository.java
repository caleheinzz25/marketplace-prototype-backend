package EzyShop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import EzyShop.model.products.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be defined here if needed
    Optional<Product> findByTitle(String title);

    Optional<Product> findById(Long id);

    boolean existsByIdAndEnabledTrue(Long id);
}