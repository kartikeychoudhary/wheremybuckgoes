package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.AuditExternalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditExternalRequestRepository extends JpaRepository<AuditExternalRequest, Long> {
    List<AuditExternalRequest> findByDateGreaterThan(long date);
    long countByDateGreaterThan(long date);
}
