package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.constants.ApplicationConstant.TransactionType;
import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.utils.ApplicationHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
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

    @Builder.Default
    private boolean disableForCharts = false;

    public TransactionDTO convertToDTO() {
        TransactionDTO dto = this.convertToDTOWithOutID();
        dto.setId(transactionId);
        return dto;
    }

    public TransactionDTO convertToDTOWithOutID() {
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
                .category(category)
                .isDeleted(isDeleted)
                .createdDate(createdDate)
                .disableForCharts(disableForCharts)
                .build();
    }

    public static Transaction convertFromJSONObject(User user, JSONObject json){
        long amount = json.optLong("price", json.optLong("amount", 0));
        String spendAt = json.optString("spendAt", "");

        String dateTag = "verify date";
        if(json.has("tags")){
            JSONArray temp = json.optJSONArray("tags", null);
            if(temp != null && !temp.isEmpty()){
                dateTag = json.getJSONArray("tags").getString(0);
            }
        }
        String[] tags = new String[] { dateTag, json.optString("transactionMode", json.optString("mode","parsingError")), json.optString("smsReceivedAt","NA")};
        TransactionType type = TransactionType.fromString(json.optString("type", ""));
        String transactionMode= json.optString("transactionMode", json.optString("mode","parsingError"));
        String account = json.optString("account", "");
        String category = json.optString("category");
        String description = json.optString("description");
        long createdDate = json.optLong("createdDate", System.currentTimeMillis());
        String parsedDate = json.optString("date", null);
        if(parsedDate != null){
            Long parsedDateInMillis = ApplicationHelper.convertDateToMillis(parsedDate, new String[] {"dd MMM yy", "dd MMM yyyy", "dd MM yy", "dd MM yyyy", "ddMMyy", "ddMMyyyy"});
            if(parsedDateInMillis != null){
                createdDate = parsedDateInMillis;
            }
        }

        return Transaction
                .builder()
                .account(account)
                .amount(amount)
                .spendAt(spendAt)
                .tags(tags)
                .type(type)
                .createdDate(createdDate)
                .createdBy(user)
                .category(category)
                .description(description)
                .transactionMode(transactionMode).build();
    }
}
