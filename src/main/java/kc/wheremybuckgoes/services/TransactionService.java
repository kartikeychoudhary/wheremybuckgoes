package kc.wheremybuckgoes.services;


import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.exceptions.CustomGenericRuntimeException;
import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final TaskService taskService;

    public Transaction createTransaction(Transaction transaction){
        log.info("TransactionService: createTransaction()");
        return transactionRepo.save(transaction);
    }

    public Transaction updateTransaction(Transaction transaction){
        log.info("TransactionService: updateTransaction()");
        return transactionRepo.save(transaction);
    }

    public void deleteTransaction(Transaction transaction){
        log.info("TransactionService: deleteTransaction()");
        transactionRepo.delete(transaction);
    }

    public List<Transaction> getAllTransactionForUser(String email){
        log.info("TransactionService: getAllTransactionForUser()");
        return transactionRepo.findAllByCreatedByEmail(email);
    }

    public Transaction getTransaction(Long id){
        log.info("TransactionService: getTransaction()");
        return transactionRepo.findById(id).orElse(null);
    }

    public Task convertTaskToTransaction(User user, Task task){
        log.info("TransactionService: convertTaskToTransaction() - Task: " + task.getTaskId());
        task.setType(ApplicationConstant.TaskType.Transaction);
        task = taskService.startTask(task);
        try {
            JSONObject json = new JSONObject(new String(task.getResponse()));
            Transaction transaction = Transaction.convertFromJSONObject(user, json);
            transaction.setCreatedDate(task.getCreatedDate());
            this.createTransaction(transaction);
            return taskService.completeTask(task, new String(task.getResponse()));
        }catch (Exception e){
            return taskService.failedTask(task, new String(task.getResponse()));
        }
    }
}
