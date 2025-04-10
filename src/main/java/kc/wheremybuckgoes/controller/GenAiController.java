package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.FriendsDTO;
import kc.wheremybuckgoes.dto.TaskDTO;
import kc.wheremybuckgoes.exceptions.CustomGenericRuntimeException;
import kc.wheremybuckgoes.modal.Friends;
import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.GeminiAiService;
import kc.wheremybuckgoes.services.GenAiService;
import kc.wheremybuckgoes.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/genAi")
@RequiredArgsConstructor
@Slf4j
public class GenAiController {

    @Value("${genai.enabled}")
    private boolean isEnabled;

    private final GenAiService genAiService;

    @PostMapping()
    public ResponseEntity<GenericResponse<TaskDTO>> addGenAiTask(@RequestBody String userTask) {
        log.info("GenAiController: addGenAiTask");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TaskDTO task;
        User user = (User) authentication.getPrincipal();
        try {
            task = genAiService.createGenAiRequest(user, userTask).convertToDTO();
        }catch (CustomGenericRuntimeException e){
            return ResponseEntity.badRequest().body(mapToGenericResponse(HttpStatus.TOO_MANY_REQUESTS, null, e.getMessage()));
        }
        GenericResponse<TaskDTO> gr = mapToGenericResponse(HttpStatus.OK, task);
        return ResponseEntity.ok().body(gr);
    }

//    @PostMapping("/test")
//    public ResponseEntity<GenericResponse<String>> testGenAi(@RequestBody String userTask) {
//        log.info("GenAiController: testGenAi");
//        String response;
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(!authentication.isAuthenticated()){
//            return ResponseEntity.badRequest().body(new GenericResponse<>());
//        }
//        try {
//            response = genAiService.testGenAIRequest(userTask);
//        }catch (CustomGenericRuntimeException e){
//            return ResponseEntity.badRequest().body(mapToGenericResponse(HttpStatus.TOO_MANY_REQUESTS, null, e.getMessage()));
//        }
//        GenericResponse<String> gr = mapToGenericResponse(HttpStatus.OK, response);
//        return ResponseEntity.ok().body(gr);
//    }

}
