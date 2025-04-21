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

    @GetMapping(path = "/after/{date}")
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactionsAfterDate(@PathVariable("date") String date) {
        long d = Long.parseLong(date);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUserAfterDate(user, d);
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(transaction -> !transaction.isDeleted()).map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping(path = "/between/{date1}/{date2}")
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactionsAfterDate(@PathVariable("date1") String date1, @PathVariable("date2") String date2) {
        long d1 = Long.parseLong(date1);
        long d2 = Long.parseLong(date2);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUserBetweenDate(user, d1, d2);
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(transaction -> !transaction.isDeleted()).map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactionsForDate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUser(user.getEmail());
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(transaction -> !transaction.isDeleted()).map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping("/deleted")
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getDeletedTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUser(user.getEmail());
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(Transaction::isDeleted).map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping("/accounts")
    public ResponseEntity<GenericResponse<List<String>>> getAllAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<String> accounts = transactionService.getAllAccountsForUser(user.getId(), user.getEmail());
        GenericResponse<List<String>> gr = mapToGenericResponse(HttpStatus.OK, accounts);
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

    @PatchMapping()
    public ResponseEntity<GenericResponse<TransactionDTO>> updateTransaction(@RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionService.getTransaction(transactionDTO.getId());
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (transaction.getCreatedBy().getEmail().equals(user.getEmail())) {
            Transaction updatedTransaction = transactionService.updateTransaction(transactionDTO.convert(user, transactionDTO.getId()));
            GenericResponse<TransactionDTO> gr = mapToGenericResponse(HttpStatus.OK, updatedTransaction.convertToDTO());
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
            transaction.setDeleted(true);
            transaction = transactionService.updateTransaction(transaction);
            GenericResponse<TransactionDTO> gr = mapToGenericResponse(HttpStatus.OK, transaction.convertToDTO());
            return ResponseEntity.ok().body(gr);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/after/{date}/{offset}/{size}")
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactionsAfterDate(@PathVariable("date") String date, @PathVariable("offset") int offset, @PathVariable("size") int size) {
        long d = Long.parseLong(date);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUserAfterDate(user, d, offset, size);
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(transaction -> !transaction.isDeleted()).map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }

    @GetMapping(path="/{offset}/{size}")
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactionsForDate(@PathVariable("offset") int offset, @PathVariable("size") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUserPerPage(user, offset, size);
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(transaction -> !transaction.isDeleted()).map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }
}
