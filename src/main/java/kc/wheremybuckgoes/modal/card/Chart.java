package kc.wheremybuckgoes.modal.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Chart {
    String type;
    JSONObject data;
}

