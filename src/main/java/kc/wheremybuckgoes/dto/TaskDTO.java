package kc.wheremybuckgoes.dto;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.modal.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long taskId;
    private ApplicationConstant.TaskStatus status;
    private long createdDate;
    private long startDate;
    private long endDate;
    private ApplicationConstant.TaskType type;
    private String request;
    private String response;
    private long executionTime;
    private boolean isDeleted;

    public Task convert(User user){
        if(response == null){setResponse("");}
        return Task.builder().
                taskId(taskId)
                .status(status)
                .createdDate(createdDate)
                .startDate(startDate)
                .endDate(endDate)
                .type(type)
                .user(user)
                .request(request)
                .response(response.getBytes())
                .isDeleted(isDeleted)
                .executionTime(executionTime).build();
    }
}
