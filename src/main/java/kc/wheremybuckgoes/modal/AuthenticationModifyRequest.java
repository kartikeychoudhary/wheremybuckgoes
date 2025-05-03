package kc.wheremybuckgoes.modal;

import kc.wheremybuckgoes.request.AuthenticationRequest;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticationModifyRequest extends AuthenticationRequest {
    private String newPassword;
}
