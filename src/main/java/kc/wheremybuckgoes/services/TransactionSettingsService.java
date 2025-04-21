package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.TransactionSettings;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.repositories.TransactionSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionSettingsService {
    private final TransactionSettingsRepository settingsRepository;

    public TransactionSettings createTransactionSettings(TransactionSettings settings){
        log.info("TransactionSettingsService: createTransactionSettings()");
        return settingsRepository.save(settings);
    }

    public TransactionSettings getTransactionSettings(User user){
        log.info("TransactionSettingsService: getTransactionSettings()");
        return settingsRepository.findByUser(user).orElse(null);
    }

    public TransactionSettings getTransactionSettingsById(Long id){
        log.info("TransactionSettingsService: getTransactionSettingsById()");
        return settingsRepository.findById(id).orElse(null);
    }

    public TransactionSettings updateTransaction(TransactionSettings settings){
        log.info("TransactionSettingsService: updateTransaction()");
        return settingsRepository.save(settings);
    }

    public void deleteTransactionSettings(TransactionSettings settings){
        log.info("TransactionService: deleteTransaction()");
        settingsRepository.delete(settings);
    }
}
