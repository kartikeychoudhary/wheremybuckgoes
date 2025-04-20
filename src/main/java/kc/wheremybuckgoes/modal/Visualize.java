package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import kc.wheremybuckgoes.dto.VisualizeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Visualize {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long visualizeId;

    @Lob
    @Column(columnDefinition="MEDIUMBLOB")
    private byte[] dashboard;

    @OneToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public VisualizeDTO convertToDTO(){
        return VisualizeDTO.builder().visualizeId(visualizeId).dashboard(new String(dashboard)).build();
    }
}
