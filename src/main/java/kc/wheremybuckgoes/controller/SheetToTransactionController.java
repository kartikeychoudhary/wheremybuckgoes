package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.TransactionService;
import kc.wheremybuckgoes.utils.SheetParsingHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/parseSheet")
@RequiredArgsConstructor
public class SheetToTransactionController {

    private final TransactionService ts;

    @PostMapping()
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> preview(@RequestParam("file") MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Transaction> transactions = SheetParsingHelper.parseFile(new InputStreamReader(file.getInputStream()), user);
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().map(Transaction::convertToDTOWithOutID).toList());
        return ResponseEntity.ok().body(gr);
    }

    @PostMapping("/submit")
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> submit(@RequestParam("file") MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Transaction> transactions = SheetParsingHelper.parseFile(new InputStreamReader(file.getInputStream()), user);
        List<Transaction> newTransactions = ts.createAllTransaction(transactions);
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, newTransactions.stream().map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }
}
