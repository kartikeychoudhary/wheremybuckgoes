package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.modal.AuditExternalRequest;
import kc.wheremybuckgoes.repositories.AuditExternalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditExternalRequestService {
    private final AuditExternalRequestRepository auditRepo;

    public void saveAudit(AuditExternalRequest audit){
        auditRepo.save(audit);
    }

    public List<AuditExternalRequest> getAllAudit(){
        return auditRepo.findAll();
    }

    public List<AuditExternalRequest> getAllAuditForDateAfter(long date){
        return auditRepo.findByDateGreaterThan(date);
    }

    public long getAllCountOfAuditForDateAfterALong(long date){
        return auditRepo.countByDateGreaterThan(date);
    }
}
