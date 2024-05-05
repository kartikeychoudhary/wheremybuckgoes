package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.TransactionSettings;
import kc.wheremybuckgoes.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionSettingsRepository  extends JpaRepository<TransactionSettings, Long> {
    Optional<TransactionSettings> findByUser(User user);
}
