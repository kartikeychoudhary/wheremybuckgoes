package kc.wheremybuckgoes.dto;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.Settings;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.modal.Visualize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualizeDTO {
    long visualizeId;
    String dashboard;
}
