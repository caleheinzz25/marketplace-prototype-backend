package EzyShop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import EzyShop.model.orders.Transaction;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReferenceId(String referenceId);
}