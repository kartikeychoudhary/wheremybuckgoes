package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.modal.Dashboard;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.DashboardService;
import kc.wheremybuckgoes.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final DashboardService dashboardService;

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
}
