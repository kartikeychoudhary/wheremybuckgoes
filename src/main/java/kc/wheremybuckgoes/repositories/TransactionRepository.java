package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCreatedByEmail(String email);
}
