package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCreatedByEmail(String email);
    @Query(value = """
      select t from Transaction t
      INNER JOIN User u
      on t.createdBy.id = u.id
      where u.id = ?1 AND (t.createdDate > ?2)\s
      """)
    List<Transaction> findAllByCreatedByIdAndByDateGreaterThan(Long id, long date);

    @Query(value = """
      select distinct t.account from Transaction t
      INNER JOIN User u
      on t.createdBy.id = u.id
      where u.id = ?1 AND u.email = ?2\s
      """)
    List<String> findAllDistinctAccountByCreatedByEmail(Long id, String email);
}
