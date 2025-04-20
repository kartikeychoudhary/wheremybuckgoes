package kc.wheremybuckgoes.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kc.wheremybuckgoes.dto.SettingsDTO;
import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.dto.VisualizeDTO;
import kc.wheremybuckgoes.modal.*;
import kc.wheremybuckgoes.modal.card.Card;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.DashboardService;
import kc.wheremybuckgoes.services.TransactionService;
import kc.wheremybuckgoes.services.VisualizeService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final DashboardService dashboardService;
    private final VisualizeService visualizeService;

    @GetMapping(path = "/after/{date}")
    public ResponseEntity<GenericResponse<Dashboard>> getTransactionsAfterDate(@PathVariable("date") String date) {
        long d = 0;
        if(!date.equalsIgnoreCase("all")){
            d = Long.parseLong(date);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Dashboard dashboard = dashboardService.getDashboardAfterDate(user, d);
        GenericResponse<Dashboard> gr = mapToGenericResponse(HttpStatus.OK, dashboard);
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactionsForDate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUser(user.getEmail());
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(transaction -> !transaction.isDeleted()).map(Transaction::convertToDTO).collect(Collectors.toList()));
        return ResponseEntity.ok().body(gr);
    }

    @PostMapping(path = "/visualize/preview")
    public ResponseEntity<GenericResponse<String>> getChartPreview(@RequestBody String cardDTO) {
        ObjectMapper mapper = new ObjectMapper();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        try {
            Card card = mapper.readValue(cardDTO, Card.class);
            JSONObject result = visualizeService.getChartPreview(user, card);
            return ResponseEntity.ok().body(mapToGenericResponse(HttpStatus.OK, result.toString(1)));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/visualize")
    public ResponseEntity<GenericResponse<VisualizeDTO>>  getDashboard(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Visualize visualize = visualizeService.getSettingByUser(user);
        GenericResponse<VisualizeDTO> gr = mapToGenericResponse(HttpStatus.OK, visualize.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }

    @PostMapping("/visualize")
    public ResponseEntity<GenericResponse<VisualizeDTO>>  updateSettings(@RequestBody VisualizeDTO settingsDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Visualize visualize = visualizeService.getSettingByUser(user);
        if(visualize == null){
            return ResponseEntity.notFound().build();
        }
        Visualize updatedDashboard = visualizeService.createOrUpdateVisualize(Visualize.builder().createdBy(user).visualizeId(visualize.getVisualizeId()).dashboard(settingsDTO.getDashboard().getBytes()).build());
        GenericResponse<VisualizeDTO> gr = mapToGenericResponse(HttpStatus.OK, updatedDashboard.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
}
