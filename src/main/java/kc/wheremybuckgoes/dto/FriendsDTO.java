package kc.wheremybuckgoes.dto;

import kc.wheremybuckgoes.modal.Friends;
import kc.wheremybuckgoes.modal.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendsDTO {

    private Long friendsId;
    private List<String> friendsList;

    public Friends convert(User user){
        return Friends.builder().user(user).friendsId(friendsId).friendsList(friendsList).build();
    }
}
