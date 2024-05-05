package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import kc.wheremybuckgoes.dto.TransactionSettingsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TransactionSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long settingsId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private List<String> accountList;

    private List<String> categories;

    private List<String> transactionMode;

    public TransactionSettingsDTO convertToDTO(){
        return TransactionSettingsDTO.builder()
                .accountList(accountList)
                .categories(categories)
                .transactionMode(transactionMode)
                .build();
    }
}
