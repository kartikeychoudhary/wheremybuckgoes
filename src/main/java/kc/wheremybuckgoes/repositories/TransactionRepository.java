package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
      select t from Transaction t
      INNER JOIN User u
      on t.createdBy.id = u.id
      where u.id = ?1 AND (t.createdDate < ?2)\s
      """)
    List<Transaction> findAllByCreatedByIdAndByDateLessThan(Long id, long date);

    @Query(value = """
      select t from Transaction t
      INNER JOIN User u
      on t.createdBy.id = u.id
      where u.id = ?1 AND (t.createdDate > ?2) AND (t.createdDate < ?3)\s
      """)
    List<Transaction> findAllByCreatedByIdAndByDateBetween(Long id, long start, long end);

    @Query(value = """
      select distinct t.account from Transaction t
      INNER JOIN User u
      on t.createdBy.id = u.id
      where u.id = ?1 AND u.email = ?2\s
      """)
    List<String> findAllDistinctAccountByCreatedByEmail(Long id, String email);
    Page<Transaction> findAllByCreatedById(Long id, Pageable pageable);

    @Query(value = """
      select t from Transaction t
      INNER JOIN User u
      on t.createdBy.id = u.id
      where u.id = ?1 AND (t.createdDate > ?2)\s
      """)
    Page<Transaction> findAllByCreatedByIdAndByDateGreaterThan(Long id, long date, Pageable pageable);
}
