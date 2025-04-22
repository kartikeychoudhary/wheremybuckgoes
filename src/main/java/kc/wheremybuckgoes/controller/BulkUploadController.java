/**
 * Controller for handling bulk upload operations for transactions.
 * <p>
 * This controller provides endpoints for downloading a CSV template, previewing
 * transaction data from uploaded files, and submitting bulk transactions.
 * It facilitates efficient data import capabilities for the WhereMyBuckGoes
 * expense tracking application.
 *
 * @author Kartikey Choudhary (kartikey31choudhary@gmail.com)
 * @see TransactionService Service handling transaction persistence
 * @see SheetParsingHelper Utility for parsing CSV files into transaction objects
 */
package kc.wheremybuckgoes.controller;

import jakarta.servlet.http.HttpServletResponse;
import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.TransactionService;
import kc.wheremybuckgoes.utils.SheetParsingHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/bulkUpload")
@RequiredArgsConstructor
public class BulkUploadController {
    /**
     * Service for transaction operations.
     * Handles the creation and persistence of transaction records.
     */
    private final TransactionService ts;
    /**
     * Spring's ResourceLoader for accessing application resources.
     * Used to load the CSV template file from the classpath.
     */
    private final ResourceLoader resourceLoader;

    /**
     * Provides a downloadable CSV template for bulk transaction uploads.
     * <p>
     * This endpoint streams a pre-defined CSV template file to the client,
     * which users can fill with their transaction data and upload back to the system.
     * The template includes the required column structure and formatting examples.
     *
     * @param response The HTTP response object used to set content type and headers
     * @throws IOException If the template file cannot be read or streamed
     * @throws FileNotFoundException If the template file does not exist in the classpath
     */
    @GetMapping()
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/template.csv");

        if (resource.exists() && resource.isReadable()) {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"template.csv\"");
            FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
        } else {
            throw new FileNotFoundException("Template file not found!");
        }
    }

    /**
     * Previews transactions from an uploaded CSV file without saving them.
     * <p>
     * This endpoint parses the uploaded file and converts it to transaction objects,
     * returning them to the client for review before final submission. This allows
     * users to verify the data before committing it to the database.
     *
     * @param file The uploaded CSV file containing transaction data
     * @return ResponseEntity containing:
     *         - 200 OK with a list of parsed transaction DTOs (without IDs)
     *         - Error response if file parsing fails
     * @throws IOException If the file cannot be read or parsed
     */
    @PostMapping()
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> preview(@RequestParam("file") MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Transaction> transactions = SheetParsingHelper.parseFile(new InputStreamReader(file.getInputStream()), user);
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().map(Transaction::convertToDTOWithOutID).toList());
        return ResponseEntity.ok().body(gr);
    }

    /**
     * Processes and saves transactions from an uploaded CSV file.
     * <p>
     * This endpoint parses the uploaded file, converts it to transaction objects,
     * and persists them to the database. It performs the final step in the bulk
     * upload workflow after the user has reviewed the preview.
     *
     * @param file The uploaded CSV file containing transaction data
     * @return ResponseEntity containing:
     *         - 200 OK with a list of created transaction DTOs (with assigned IDs)
     *         - Error response if file parsing or saving fails
     * @throws IOException If the file cannot be read or parsed
     */
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
