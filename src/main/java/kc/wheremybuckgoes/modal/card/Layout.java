package kc.wheremybuckgoes.modal.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Layout {
    String function;
    String dimension;
    DateOptions dateOptions;
    Filters filters;
}
