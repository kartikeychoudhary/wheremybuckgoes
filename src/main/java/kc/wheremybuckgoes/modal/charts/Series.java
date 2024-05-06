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
public class Series {
    private String name;
    private String color;
    private List<Long> data;
}
