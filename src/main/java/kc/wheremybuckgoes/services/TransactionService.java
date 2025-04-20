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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public List<Transaction> createAllTransaction(List<Transaction> transaction){
        log.info("TransactionService: createTransaction()");
        return transactionRepo.saveAll(transaction);
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

    public List<String> getAllAccountsForUser(Long id, String email){
        log.info("TransactionService: getAllAccountsForUser()");
        return transactionRepo.findAllDistinctAccountByCreatedByEmail(id, email);
    }

    public List<Transaction> getAllTransactionForUserAfterDate(User user, long date){
        log.info("TransactionService: getAllTransactionForUserAfterDate()");
        return transactionRepo.findAllByCreatedByIdAndByDateGreaterThan(user.getId(), date);
    }
    public List<Transaction> getAllTransactionForUserBeforeDate(User user, long date){
        log.info("TransactionService: getAllTransactionForUserBeforeDate()");
        return transactionRepo.findAllByCreatedByIdAndByDateLessThan(user.getId(), date);
    }

    public List<Transaction> getAllTransactionForUserBetweenDate(User user, long date1, long date2){
        log.info("TransactionService: getAllTransactionForUserBetweenDate()");
        return transactionRepo.findAllByCreatedByIdAndByDateBetween(user.getId(), date1, date2);
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
            transaction.setDescription(task.getRequest());
            // transaction.setCreatedDate(task.getCreatedDate());
            this.createTransaction(transaction);
            return taskService.completeTask(task, new String(task.getResponse()));
        }catch (Exception e){
            return taskService.failedTask(task, new String(task.getResponse()));
        }
    }

    public List<Transaction> getAllTransactionForUserPerPage(User user, int offset, int size){
        log.info("TransactionService: getAllTransactionForUserPerPage()");
        Page<Transaction> transactions = transactionRepo.findAllByCreatedById(user.getId(), PageRequest.of(offset, size).withSort(Sort.by(Sort.Direction.DESC,"createdDate")));
        return transactions.getContent();
    }

    public List<Transaction> getAllTransactionForUserAfterDate(User user, long date, int offset, int size){
        log.info("TransactionService: getAllTransactionForUserAfterDate(User user, long date, int offset, int size)");
        return transactionRepo.findAllByCreatedByIdAndByDateGreaterThan(user.getId(), date, PageRequest.of(offset, size).withSort(Sort.by(Sort.Direction.DESC,"createdDate"))).getContent();
    }
}
