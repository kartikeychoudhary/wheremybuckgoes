package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<GenericResponse<TransactionDTO>> addTransaction(@RequestBody TransactionDTO transactionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Transaction transaction = transactionService.createTransaction(transactionDTO.convert(user));
        GenericResponse<TransactionDTO> gr = mapToGenericResponse(HttpStatus.OK, transaction.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUser(user.getEmail());
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().map(Transaction::convertToDTO).collect(Collectors.toList()));
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<GenericResponse<TransactionDTO>> getTransaction(@PathVariable("id") Long id) {
        Transaction transaction = transactionService.getTransaction(id);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (transaction.getCreatedBy().getEmail().equals(user.getEmail())) {
            GenericResponse<TransactionDTO> gr = mapToGenericResponse(HttpStatus.OK, transaction.convertToDTO());
            return ResponseEntity.ok().body(gr);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<GenericResponse<TransactionDTO>> updateTransaction(@PathVariable("id") Long id, @RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionService.getTransaction(id);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (transaction.getCreatedBy().getEmail().equals(user.getEmail())) {
            Transaction updatedTransaction = transactionService.updateTransaction(transactionDTO.convert(user, id));
            GenericResponse<TransactionDTO> gr = mapToGenericResponse(HttpStatus.OK, updatedTransaction.convertToDTO());
            return ResponseEntity.ok().body(gr);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<GenericResponse<TransactionDTO>> deleteTransaction(@PathVariable("id") Long id) {
        Transaction transaction = transactionService.getTransaction(id);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (transaction.getCreatedBy().getEmail().equals(user.getEmail())) {
            transaction.setIsDeleted(true);
            transaction = transactionService.updateTransaction(transaction);
            GenericResponse<TransactionDTO> gr = mapToGenericResponse(HttpStatus.OK, transaction.convertToDTO());
            return ResponseEntity.ok().body(gr);
        }
        return ResponseEntity.notFound().build();
    }
}
