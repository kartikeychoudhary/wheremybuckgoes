package kc.wheremybuckgoes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String profilePicURL;
}
