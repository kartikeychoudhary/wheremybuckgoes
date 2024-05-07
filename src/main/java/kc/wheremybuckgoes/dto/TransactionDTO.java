package kc.wheremybuckgoes.dto;

import kc.wheremybuckgoes.constants.ApplicationConstant.TransactionType;
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
    private boolean isSplit;
    private TransactionType type;
    private String transactionMode;
    private String spendAt;
    private String category;
    private boolean isDeleted;
    private long createdDate;
    private boolean disableForCharts;

    public Transaction convert(User user){
        return Transaction
                .builder()
                .transactionId(id)
                .amount(amount)
                .account(account)
                .description(description)
                .tags(tags)
                .type(type)
                .isSplit(isSplit)
                .transactionMode(transactionMode)
                .createdBy(user)
                .spendAt(spendAt)
                .category(category)
                .isDeleted(isDeleted)
                .createdDate(createdDate)
                .disableForCharts(disableForCharts)
                .build();
    }

    public Transaction convert(User user, Long id){
        this.id = id;
        return this.convert(user);
    }
}
