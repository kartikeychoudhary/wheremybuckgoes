package kc.wheremybuckgoes.controller;

import kc.wheremybuckgoes.dto.FriendsDTO;
import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.dto.TransactionSettingsDTO;
import kc.wheremybuckgoes.modal.Friends;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.TransactionSettings;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.TransactionSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/transactionSettings")
@RequiredArgsConstructor
public class TransactionSettingsController {
    private final TransactionSettingsService settingsService;

    @PostMapping()
    public ResponseEntity<GenericResponse<TransactionSettingsDTO>> addTransactionSettings(@RequestBody TransactionSettingsDTO settingsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        TransactionSettings settings = settingsService.createTransactionSettings(settingsDTO.convertTo(user));
        GenericResponse<TransactionSettingsDTO> gr = mapToGenericResponse(HttpStatus.OK, settings.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
    @GetMapping
    public ResponseEntity<GenericResponse<TransactionSettingsDTO>> getTransactionSettings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        TransactionSettings settings = settingsService.getTransactionSettings(user);
        if (settings == null) {
            return ResponseEntity.badRequest().body(null);
        }
        GenericResponse<TransactionSettingsDTO> gr = mapToGenericResponse(HttpStatus.OK, settings.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
    @PatchMapping()
    public ResponseEntity<GenericResponse<TransactionSettingsDTO>> updateFriends(@RequestBody TransactionSettingsDTO settingsDTO) {
        TransactionSettings settings = settingsService.getTransactionSettingsById(settingsDTO.getSettingsId());
        if (settings == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (settings.getUser().getEmail().equals(user.getEmail())) {
            TransactionSettings newSettings = settingsService.updateTransaction(settings);
            GenericResponse<TransactionSettingsDTO> gr = mapToGenericResponse(HttpStatus.OK, newSettings.convertToDTO());
            return ResponseEntity.ok().body(gr);
        }
        return ResponseEntity.notFound().build();
    }
}
