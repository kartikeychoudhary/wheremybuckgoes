package kc.wheremybuckgoes.modal.charts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class SingleDimSeries {
    private List<Long> series;
    private List<String> colors;
    private List<String> labels;
}
