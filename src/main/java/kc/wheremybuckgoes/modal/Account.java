package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long accountId;
    private Long balance;
    private String color;
    private String description;
    private String icon;
    private String issuer;
    private String type;
    private String uniqueName;
    private String name;
}
