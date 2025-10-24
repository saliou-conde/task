package ch.sintere.task.mapper;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static ch.sintere.task.entities.Priority.MEDIUM;
import static ch.sintere.task.entities.Status.IN_PROGRESS;
import static ch.sintere.task.entities.Status.OPEN;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    public void setUp() {
        taskMapper = new TaskMapperImpl();
    }

    @Test
    void shouldMapTaskDtoToTaskD_whenValidDataGiven() {
        //Given
        var title = "My Title";
        var status = OPEN;
        var priority = MEDIUM;
        var createdAt = now();
        var createdBy = "My User";
        TaskDto taskDto = new TaskDto(
                title,
                status,
                priority,
                createdAt,
                null,
                null,
                createdBy
        );

        //When
        var actualTask = taskMapper.toEntity(taskDto);

        //Then
        assertAll(
                () -> {
                    assertThat(actualTask)
                            .isNotNull()
                            .extracting(
                                    "title",
                                    "status",
                                    "priority",
                                    "createdAt",
                                    "updatedAt",
                                    "dueDate",
                                    "createdBy"
                                    )
                            .containsExactly(title, status, priority, createdAt, null, null, createdBy);
                }
        );
    }

    @Test
    void shouldMapTaskToTaskDto_whenValidDataGiven() {
        //Given
        var title = "My Title";
        var status = IN_PROGRESS;
        var priority = MEDIUM;
        var createdAt = now();
        var updatedAt = now().plusHours(1);
        var dueDate = LocalDate.now();
        var createdBy = "My User";
        var task = Task.builder()
                .createdBy(createdBy)
                .createdAt(createdAt)
                .dueDate(dueDate)
                .updatedAt(updatedAt)
                .priority(priority)
                .status(status)
                .title(title)
                .build();

        //When
        var actualTask = taskMapper.toDto(task);

        //Then
        assertAll(
                () -> {
                    assertThat(actualTask)
                            .isNotNull()
                            .extracting(
                                    "title",
                                    "status",
                                    "priority",
                                    "createdAt",
                                    "updatedAt",
                                    "dueDate",
                                    "createdBy"
                            )
                            .containsExactly(title, status, priority, createdAt, updatedAt, dueDate, createdBy);
                });
    }
}