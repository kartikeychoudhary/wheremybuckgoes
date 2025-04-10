package kc.wheremybuckgoes.dto;

import kc.wheremybuckgoes.modal.TransactionSettings;
import kc.wheremybuckgoes.modal.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSettingsDTO {
    private Long settingsId;

    private List<String> accountList;

    private List<String> categories;

    private List<String> transactionMode;

    public TransactionSettings convertTo(User user){
        return TransactionSettings.builder()
                .accountList(accountList)
                .categories(categories)
                .transactionMode(transactionMode)
                .user(user)
                .build();
    }
}
