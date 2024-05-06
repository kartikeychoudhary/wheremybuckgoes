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
public class MultiDimSeries {
    List<Series> series;
    List<String> labels;
}
