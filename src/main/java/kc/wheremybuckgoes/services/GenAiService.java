package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.exceptions.CustomGenericRuntimeException;
import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.modal.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskStatus;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskType;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenAiService {

    @Value("${genai.provider}")
    private String provider;
    @Value("${genai.restrictions.maxRequestInQueue}")
    private int maxRequestInQueue;

    private final GeminiAiService geminiAiService;
    private final TaskService taskService;

    public Task createGenAiRequest(User user, String userText){
        List<Task> tasks = taskService.getAllTaskByStatusAndType(user, TaskStatus.OPEN, TaskType.GenAi);
        if(tasks != null && tasks.size() >= this.maxRequestInQueue){
            throw new CustomGenericRuntimeException(ApplicationConstant.Exceptions.GEN_AI_MAX_REQUEST_THRESHOLD_EXCEEDS, new RuntimeException());
        }
        return taskService.createGenAiTask(user, userText);
    }

    public Task executeGenAiRequest(Task task){
        log.info("GenAiService: executeGenAiRequest - Task Started:" + task.getTaskId());
        task = taskService.startTask(task);
        try{
            String response = geminiAiService.makeRequest(new String(task.getRequest()));
            log.info("GenAiService: executeGenAiRequest - Task Completed:" + task.getTaskId());
            return taskService.completeTask(task, response);
        }catch (CustomGenericRuntimeException e){
            log.info("GenAiService: executeGenAiRequest - Task Failed:" + task.getTaskId());
            return taskService.failedTask(task, e.getMessage());
        }
    }

    public String testGenAIRequest(String request){
        try{
            String response = geminiAiService.makeRequest(request);
            log.info("GenAiService: testGenAIRequest - Task Completed");
            return response;
        }catch (CustomGenericRuntimeException e){
            log.info("GenAiService: testGenAIRequest - Task Failed");
            return "failed";
        }
    }
}
