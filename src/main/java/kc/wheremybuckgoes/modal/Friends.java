package kc.wheremybuckgoes.modal;

import jakarta.persistence.*;
import kc.wheremybuckgoes.dto.FriendsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long friendsId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private List<String> friendsList;

    public FriendsDTO convertToDTO(){
        return FriendsDTO.builder().friendsId(friendsId).friendsList(friendsList).build();
    }
}
