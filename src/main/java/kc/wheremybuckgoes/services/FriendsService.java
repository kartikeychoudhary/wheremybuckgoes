package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.modal.Friends;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.repositories.FriendsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendsService {

    private final FriendsRepository friendsRepo;

    public Friends addFriends(Friends friends){
        log.info("FriendsService: addFriends()");
        Friends savedFriends = friendsRepo.findByUser(friends.getUser()).orElse(null);
        if(savedFriends == null){
            return friendsRepo.save(friends);
        }
        List<String> saved = savedFriends.getFriendsList();
        saved.addAll(friends.getFriendsList().stream().filter(friend->!saved.contains(friend)).toList());
        savedFriends.setFriendsList(saved);
        return friendsRepo.save(savedFriends);
    }

    public Friends updateFriends(Friends friends){
        log.info("FriendsService: updateFriends() - " + friends.getFriendsId());
        Friends savedFriends = friendsRepo.findByUser(friends.getUser()).orElse(null);
        if(savedFriends == null){
            return friendsRepo.save(friends);
        }
        savedFriends.setFriendsList(friends.getFriendsList());
        return friendsRepo.save(savedFriends);
    }

    public void deleteFriends(User user){
        log.info("FriendsService: deleteFriends()");
        friendsRepo.findByUser(user).ifPresent(friendsRepo::delete);
    }

    public Friends getAllFriendsForUser(User user){
        return friendsRepo.findByUser(user).orElse(null);
    }

}
