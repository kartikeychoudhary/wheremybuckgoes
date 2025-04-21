package kc.wheremybuckgoes.scheduler;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.services.AuditExternalRequestService;
import kc.wheremybuckgoes.services.GenAiService;
import kc.wheremybuckgoes.services.TaskService;
import kc.wheremybuckgoes.utils.NotificationGotifyHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomTaskScheduler {


    private final TaskService taskService;
    private final GenAiService genAiService;
    private final AuditExternalRequestService auditService;
    private final NotificationGotifyHelper gotify;

    @Value("${genai.restrictions.maxRequestInOneMinute}")
    private int maxRequestInMinute;

    private long lastFoundException;

    @Scheduled(fixedRate = 2 * 60 * 1000) // Runs every 2 min
    public void completeTaskFromQueueForGenAi() {
        log.info("Custom Task Scheduler: completeTaskFromQueueForGenAi: START");
        List<Task> tasks = taskService.getAllTaskByStatusAndType(ApplicationConstant.TaskStatus.OPEN, ApplicationConstant.TaskType.GEN_AI);
            for (Task task : tasks) {
                genAiService.executeGenAiRequest(task);
            }
        log.info("Custom Task Scheduler: completeTaskFromQueueForGenAi: END");
    }

//    @Scheduled(fixedRate = 60 * 1000) // Runs every 1 min
//    public void auditExternalRequest() {
//        log.info("Custom Task Scheduler: auditExternalRequest: START");
//        long lastMinute = System.currentTimeMillis()-60*1000;
//        long count = auditService.getAllCountOfAuditForDateAfterALong(lastMinute);
//        if(count >= maxRequestInMinute){
//            try {
//                log.info("Custom Task Scheduler: completeTaskFromQueueForGenAi: GenAi requests exceeds");
//                gotify.sendNotification("Gemini requests exceeds: " + maxRequestInMinute + " current count: " + count, "API request Exceeds", 5);
//            } catch (IOException e) {
//                throw new CustomGenericRuntimeException("Gotify request failed", e);
//            }
//        }
//        log.info("Custom Task Scheduler: completeTaskFromQueueForGenAi: END");
//    }
}
