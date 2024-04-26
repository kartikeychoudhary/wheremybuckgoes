package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import kc.wheremybuckgoes.constants.ApplicationConstant.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TokenTable")
public class Token implements Serializable {
    @Serial
    private static final long serialVersionUID = 1905122041950251207L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
