package kc.wheremybuckgoes.modal;


import jakarta.persistence.*;
import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.dto.SettingsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long settingsId;
    private ApplicationConstant.Theme theme;
    @Lob
    private String[] categories;
    private String currency;
    private String symbol;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_id")
    private Account[] accounts;
    @OneToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public SettingsDTO convertToDTO(){
        return SettingsDTO.builder().settingsId(settingsId).theme(theme.toString()).categories(categories).currency(currency).symbol(symbol).accounts(accounts).build();
    }
}
