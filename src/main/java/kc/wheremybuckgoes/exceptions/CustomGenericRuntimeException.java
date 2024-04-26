package kc.wheremybuckgoes.exceptions;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;



@NoArgsConstructor
public class CustomGenericRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public CustomGenericRuntimeException(String msg) {
        super(msg);
    }
    public CustomGenericRuntimeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
