package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
    public List<User> findAllByEmailIn(List<String> emails);
}