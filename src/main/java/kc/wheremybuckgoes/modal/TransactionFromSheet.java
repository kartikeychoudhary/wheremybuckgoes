package kc.wheremybuckgoes.modal;

import kc.wheremybuckgoes.constants.ApplicationConstant.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFromSheet {



    private long date;
    private String description;
    private TransactionType type;
    private String mode;
    private String category;
    private String account;
    private String spendAt;
    private long amount;

    public long convertDateToMillis(String dateString) throws ParseException {
        if(dateString == null){ return 0;}
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dd = formatter.parse(dateString);
        return dd.getTime();
    }

    public void setSpendAtAndCategory(String newCategory){
        if(newCategory != null) {
            if(newCategory.contains(" | ")){
                String[] values = newCategory.split(" \\| ");
                this.category = values[0];
                this.spendAt = values[1];
            }
                else {
            this.category = newCategory;
                }
        }
    }

    public Transaction convertToTransaction(User user){
        return Transaction.builder()
                .createdBy(user)
                .transactionId(null)
                .createdDate(date)
                .description(description)
                .type(type)
                .transactionMode(mode)
                .category(category)
                .spendAt(spendAt)
                .account(account)
                .amount(amount)
                .disableForCharts(false)
                .build();

    }
}
