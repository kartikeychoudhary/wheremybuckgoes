package kc.wheremybuckgoes.modal.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Range {
    long start;
    long end;
    long dynamic;
}