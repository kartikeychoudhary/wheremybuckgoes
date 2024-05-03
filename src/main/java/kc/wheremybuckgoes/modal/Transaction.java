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

    private String category;

    private boolean isSplit;

    private List<Long> splits;

    private List<String> splitsOwner;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private long createdDate;

    @Builder.Default
    private boolean isDeleted = false;

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
                .category(category)
                .isDeleted(isDeleted)
                .build();
    }

    public static Transaction convertFromJSONObject(User user, JSONObject json){
        long amount = json.optLong("price", json.optLong("amount", 0));
        String spendAt= json.optString("spendAt", "");
        String[] tags = new String[] {json.optString("transactionMode", json.optString("mode","parsingError"))};
        TransactionType type = TransactionType.fromString(json.optString("type", ""));
        String transactionMode= json.optString("transactionMode", json.optString("mode","parsingError"));
        String account = json.optString("account", "");
        String category = json.optString("category");
        String description = json.optString("description");
        return Transaction
                .builder()
                .account(account)
                .amount(amount)
                .spendAt(spendAt)
                .tags(tags)
                .type(type)
                .createdDate(System.currentTimeMillis())
                .createdBy(user)
                .category(category)
                .description(description)
                .transactionMode(transactionMode).build();
    }
}
