package kc.wheremybuckgoes.modal.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class GridsterConfig {
    Integer cols;
    Integer rows;
    Integer y;
    Integer x;
}
