package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.modal.Visualize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisualizeRepository  extends JpaRepository<Visualize, Long> {
    Optional<Visualize> findByCreatedBy(User user);
}
