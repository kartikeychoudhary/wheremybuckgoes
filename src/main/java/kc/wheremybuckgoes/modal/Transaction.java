package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import kc.wheremybuckgoes.constants.ApplicationConstant.TransactionType;
import kc.wheremybuckgoes.dto.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long transactionId;

    private long amount;

    private String account;

    private String description;

    private String[] tags;

    private TransactionType type;

    private String transactionMode;

    private boolean isSplit;

    private List<Long> splits;

    private List<String> splitsOwner;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private long createdDate;

    private Boolean isDeleted = false;

    private String spendAt;

    public TransactionDTO convertToDTO() {
        return TransactionDTO
                .builder()
                .amount(amount)
                .description(description)
                .tags(tags)
                .account(account)
                .transactionMode(transactionMode)
                .type(type)
                .spendAt(spendAt)
                .isSplit(isSplit)
                .id(transactionId)
                .build();
    }

    public static Transaction convertFromJSONObject(User user, JSONObject json){
        long amount = json.getLong("price");
        String spendAt= json.getString("spendAt");
        String[] tags = new String[] {json.getString("mode")};
        TransactionType type = TransactionType.fromString(json.getString("type"));
        String transactionMode= json.getString("mode");
        String account = json.getString("account");
        return Transaction
                .builder()
                .account(account)
                .amount(amount)
                .spendAt(spendAt)
                .tags(tags)
                .type(type)
                .createdDate(System.currentTimeMillis())
                .createdBy(user)
                .transactionMode(transactionMode).build();
    }
}
