package ch.sintere.task.repository;

import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByStatus(Status status);

    List<Task> findByPriority(Priority priority);

    Optional<Task> findByTitle(String title);

    @Modifying
    @Query("""
            UPDATE Task tk
            SET tk.priority = :newPriority
            WHERE tk.priority = :oldPriority
            """)
    int updatePriorityForAll(@Param("oldPriority") Priority oldPriority,
                              @Param("newPriority") Priority newPriority);

}
