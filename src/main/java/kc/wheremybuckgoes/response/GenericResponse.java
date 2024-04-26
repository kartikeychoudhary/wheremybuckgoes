package kc.wheremybuckgoes.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
//@NoArgsConstructor
@Builder
public class GenericResponse <T> {
    private HttpStatus status;
    private Map<String, T> payload;
    private String message;
}
