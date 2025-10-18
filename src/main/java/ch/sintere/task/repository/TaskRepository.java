package ch.sintere.task.repository;

import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.entities.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Tasks, Integer> {

    List<Tasks> findByStatus(Status status);

    List<Tasks> findByPriority(Priority priority);

    Optional<Tasks> findByTitle(String title);

}
