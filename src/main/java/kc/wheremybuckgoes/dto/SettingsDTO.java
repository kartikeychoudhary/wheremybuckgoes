package kc.wheremybuckgoes.dto;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.Account;
import kc.wheremybuckgoes.modal.Settings;
import kc.wheremybuckgoes.modal.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsDTO {

    private Long settingsId;
    private String theme;
    private String[] categories;
    private String currency;
    private String symbol;
    private Account[] accounts;

    public Settings convert(User user){
        return Settings.builder().createdBy(user).settingsId(settingsId).theme(ApplicationConstant.Theme.fromString(theme)).categories(categories).currency(currency).symbol(symbol).accounts(accounts).build();
    }
}
