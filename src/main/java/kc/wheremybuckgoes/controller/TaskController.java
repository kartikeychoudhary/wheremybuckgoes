package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.dto.TaskDTO;
import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.exceptions.CustomGenericRuntimeException;
import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.GenAiService;
import kc.wheremybuckgoes.services.TaskService;
import kc.wheremybuckgoes.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final GenAiService genAiService;
    private final TransactionService transactionService;

    @GetMapping()
    public ResponseEntity<GenericResponse<List<TaskDTO>>> getAllTasks() {
        log.info("TaskController: getAllTasks()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        GenericResponse<List<TaskDTO>> gr = mapToGenericResponse(HttpStatus.OK, taskService.getAllTask(user).stream().filter(task -> !task.isDeleted()).map(Task::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<GenericResponse<List<Long>>> bulkDelete(@RequestBody List<Long> ids) {
        log.info("TaskController: bulkDelete()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Task> tasks = taskService.bulkGet(user, ids);
        List<Long> deletedIds = tasks.stream().map(Task::getTaskId).toList();
        ids.removeAll(new HashSet<>(deletedIds));
        taskService.bulkDelete(tasks.stream().map(Task::getTaskId).toList());
        GenericResponse<List<Long>> gr = mapToGenericResponse(HttpStatus.OK, ids);
        return ResponseEntity.ok().body(gr);
    }

    @PostMapping()
    public ResponseEntity<GenericResponse<TaskDTO>> addSaveTask(@RequestBody TaskDTO taskDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Task task = taskService.createOrUpdateTask(taskDTO.convert(user));
        GenericResponse<TaskDTO> gr = mapToGenericResponse(HttpStatus.OK, task.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }

    @PatchMapping()
    public ResponseEntity<GenericResponse<TaskDTO>> updateTask(@RequestBody TaskDTO taskDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Task task = taskService.getTaskById(user, taskDTO.getTaskId());
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        Task updatedTask = taskService.createOrUpdateTask(taskDTO.convert(user));
        GenericResponse<TaskDTO> gr = mapToGenericResponse(HttpStatus.OK, updatedTask.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }

    @PostMapping("/start")
    public ResponseEntity<GenericResponse<TaskDTO>> executeTask(@RequestBody TaskDTO task) {
        log.info("TaskController: executeTask()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Task tt = taskService.getTaskById(user, task.getTaskId());
        if(tt.getType().equals(ApplicationConstant.TaskType.GenAi) && tt.getStatus().equals(ApplicationConstant.TaskStatus.OPEN)){
            tt = genAiService.executeGenAiRequest(tt);
        }
        GenericResponse<TaskDTO> gr = mapToGenericResponse(HttpStatus.OK, tt.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }

    @PostMapping("/convert")
    public ResponseEntity<GenericResponse<TaskDTO>> convertTask(@RequestBody TaskDTO task) {
        log.info("TaskController: convertTask()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Task tt = taskService.getTaskById(user, task.getTaskId());
        try{
        if(tt.getType().equals(ApplicationConstant.TaskType.GenAi) && tt.getStatus().equals(ApplicationConstant.TaskStatus.COMPLETED)){
           tt = transactionService.convertTaskToTransaction(user, tt);
        }}catch (Exception e){
            return  ResponseEntity.badRequest().build();
        }
        GenericResponse<TaskDTO> gr = mapToGenericResponse(HttpStatus.OK, tt.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
}
