package kc.wheremybuckgoes.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AuditExternalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long externalRequestId;

    private TaskType  type;

    private long date;

    private String message;
}
