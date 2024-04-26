package kc.wheremybuckgoes.repositories;

import kc.wheremybuckgoes.constants.ApplicationConstant.TaskStatus;
import kc.wheremybuckgoes.constants.ApplicationConstant.TaskType;
import kc.wheremybuckgoes.modal.Task;
import kc.wheremybuckgoes.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<List<Task>> findAllByUserAndStatusAndType(User user, TaskStatus status, TaskType type);
    Optional<List<Task>> findAllByUserAndStatus(User user, TaskStatus status);
    Optional<List<Task>> findAllByUserAndType(User user, TaskType type);
    Optional<List<Task>> findAllByUser(User user);
    Optional<List<Task>> findAllByStatusAndType(TaskStatus status, TaskType type);
}
