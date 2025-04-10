package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.Settings;
import kc.wheremybuckgoes.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Optional<Settings> findByCreatedBy(User user);
}
