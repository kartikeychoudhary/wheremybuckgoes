package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.Friends;
import kc.wheremybuckgoes.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
    Optional<Friends> findByUser(User user);
}
