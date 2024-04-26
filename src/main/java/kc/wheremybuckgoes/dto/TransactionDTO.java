package kc.wheremybuckgoes.dto;

import kc.wheremybuckgoes.constants.ApplicationConstant.TransactionType;
import kc.wheremybuckgoes.constants.ApplicationConstant.SplitType;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private long id;
    private long amount;
    private String account;
    private String description;
    private String[] tags;
    private TransactionType type;
    private String transactionMode;

    public Transaction convert(User user){
        return Transaction
                .builder()
                .transactionId(id)
                .amount(amount)
                .account(account)
                .description(description)
                .tags(tags)
                .type(type)
                .transactionMode(transactionMode)
                .createdBy(user)
                .createdDate(System.currentTimeMillis())
                .build();
    }

    public Transaction convert(User user, Long id){
        this.id = id;
        return this.convert(user);
    }
}
