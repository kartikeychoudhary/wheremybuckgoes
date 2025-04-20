package kc.wheremybuckgoes.modal.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Filters {
    String[] accounts;
    String[] categories;
    String[] transactionModes;
    String[] transactionTypes;
}
