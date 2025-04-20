package kc.wheremybuckgoes.modal.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    String id;
    String title;
    Chart chart;
    GridsterConfig gridsterConfig;
    Layout layout;
}
