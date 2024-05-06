package kc.wheremybuckgoes.modal;

import kc.wheremybuckgoes.modal.charts.MultiDimSeries;
import kc.wheremybuckgoes.modal.charts.SingleDimSeries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@Builder
public class Dashboard {
    private Map<String, MultiDimSeries> multiDimChart;
    private Map<String, Map<String, List<SingleDimSeries>>> singleDimChart;

    public Dashboard(){
        this.multiDimChart = new HashMap<>();
        this.singleDimChart = new HashMap<>();
    }
}
