package kc.wheremybuckgoes.exceptions;


import lombok.NoArgsConstructor;
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
