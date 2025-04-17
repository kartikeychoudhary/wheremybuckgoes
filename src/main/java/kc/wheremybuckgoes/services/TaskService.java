package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskStatus;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskType;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;

    public Task createOrUpdateTask(Task task){
        log.info("TaskService: createOrUpdateTask()");
        return taskRepository.save(task);
    }

    public void delete(Task task){
        log.info("TaskService: delete() - Task: " + task.getTaskId());
        taskRepository.delete(task);
    }

    public List<Task> getAllTask(User user){
        log.info("TaskService: getAllTaskUser()");
        return taskRepository.findAllByUser(user).orElse(null);
    }

    public List<Task> getAllTask(){
        log.info("TaskService: getAllTask()");
        return taskRepository.findAll();
    }

    public List<Task> getAllTaskByStatusAndType(TaskStatus status, TaskType type){
        log.info("TaskService: getAllTaskByStatusAndType()");
        return taskRepository.findAllByStatusAndType(status, type).orElse(new ArrayList<>());
    }

    public List<Task> getAllTaskByType(User user, TaskType type){
        log.info("TaskService: getAllTaskByType()");
        return taskRepository.findAllByUserAndType(user,type).orElse(null);
    }

    public List<Task> getAllTaskByStatus(User user, TaskStatus status){
        log.info("TaskService: getAllTaskByStatus()");
        return taskRepository.findAllByUserAndStatus(user,status).orElse(null);
    }

    public List<Task> getAllTaskByStatusAndType(User user, TaskStatus status, TaskType type){
        log.info("TaskService: getAllTaskByStatusAndType()");
        return taskRepository.findAllByUserAndStatusAndType(user,status, type).orElse(null);
    }

    public Task getTaskById(User user, Long id){
        log.info("TaskService: getTaskById() - Task:" + id);
        Task task = taskRepository.findById(id).orElse(null);
        if(task != null && task.getUser().getEmail().equals(user.getEmail())){
            return task;
        }
        return null;
    }

    public Task createGenAiTask(User user, String request){
        log.info("TaskService: createGenAiTask()");
        return this.createOrUpdateTask(Task.builder()
                .status(TaskStatus.OPEN)
                .request(request.getBytes())
                .type(TaskType.GenAi)
                .user(user)
                .createdDate(System.currentTimeMillis())
                .build());
    }

    public Task startTask(Task task){
        log.info("TaskService: startTask()");
        task.setStartDate(System.currentTimeMillis());
        task.setStatus(TaskStatus.IN_PROGRESS);
        return this.createOrUpdateTask(task);
    }

    public Task completeTask(Task task, String response){
        log.info("TaskService: completeTask()");
        task.setEndDate(System.currentTimeMillis());
        task.setStatus(TaskStatus.COMPLETED);
        task.setExecutionTime(task.getEndDate()-task.getStartDate());
        task.setResponse(response.getBytes());
        return this.createOrUpdateTask(task);
    }
    public Task failedTask(Task task, String response){
        log.info("TaskService: failedTask()");
        task.setEndDate(System.currentTimeMillis());
        task.setStatus(TaskStatus.FAILED);
        task.setExecutionTime(task.getEndDate()-task.getStartDate());
        task.setResponse(response.getBytes());
        return this.createOrUpdateTask(task);
    }

}
