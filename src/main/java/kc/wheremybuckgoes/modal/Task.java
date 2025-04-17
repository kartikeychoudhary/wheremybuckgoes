package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskType;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskStatus;
import kc.wheremybuckgoes.dto.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long taskId;
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private long createdDate;
    private long startDate;
    private long endDate;
    private TaskType type;
    @Lob
    private String request;
    @Lob
    private byte[] response;
    private long executionTime;

    @Builder.Default
    private boolean isDeleted = false;

    public TaskDTO convertToDTO(){
        if(response == null) {
            response = "".getBytes();
        }
        return TaskDTO.builder()
                .taskId(taskId)
                .status(status)
                .createdDate(createdDate)
                .startDate(startDate)
                .endDate(endDate)
                .type(type)
                .request(request)
                .response(new String(response))
                .executionTime(executionTime)
                .isDeleted(isDeleted)
                .build();
    }
}
