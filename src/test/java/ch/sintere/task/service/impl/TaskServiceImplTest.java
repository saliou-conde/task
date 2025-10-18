package ch.sintere.task.service.impl;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.entities.Tasks;
import ch.sintere.task.exception.TaskNotFoundException;
import ch.sintere.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskServiceImpl(taskRepository);
    }

    @Test
    void testAddTask() {
        TaskDto dto = new TaskDto("Test Title", "OPEN", "HIGH", null, null);

        Tasks savedTask = Tasks.builder()
                .id(1)
                .title("Test Title")
                .status(Status.OPEN)
                .priority(Priority.HIGH)
                .createAt(LocalDateTime.now())
                .build();

        when(taskRepository.save(any())).thenReturn(savedTask);

        TaskDto result = taskService.addTask(dto);

        assertThat(result.title()).isEqualTo(dto.title());
        assertThat(result.status()).isEqualTo(dto.status());
        assertThat(result.priority()).isEqualTo(dto.priority());
        verify(taskRepository).save(any(Tasks.class));
    }

    @Test
    void testUpdateTask() {
        Integer taskId = 1;
        LocalDateTime oldCreateDate = LocalDateTime.now().minusDays(1);
        Tasks existingTask = Tasks.builder()
                .id(taskId)
                .title("Old Title")
                .status(Status.IN_PROGRESS)
                .priority(Priority.LOW)
                .createAt(oldCreateDate)
                .build();

        TaskDto updateDto = new TaskDto("New Title", "DONE", "MEDIUM", null, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        TaskDto result = taskService.updateTask(updateDto, taskId);

        assertThat(result.title()).isEqualTo("New Title");
        assertThat(result.status()).isEqualTo("DONE");
        assertThat(result.priority()).isEqualTo("MEDIUM");
        assertThat(result.createdAt()).isEqualTo(oldCreateDate);
        assertNotNull(result.updatedAt());
    }

    @Test
    void testFindTaskById() {
        Tasks task = Tasks.builder()
                .id(1)
                .title("Title")
                .status(Status.OPEN)
                .priority(Priority.HIGH)
                .createAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskDto dto = taskService.findTaskById(1);

        assertThat(dto.title()).isEqualTo("Title");
    }

    @Test
    void testFindTaskById_NotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(1));
    }

    @Test
    void testDeleteTask() {
        Tasks task = Tasks.builder()
                .id(1)
                .title("Title")
                .status(Status.OPEN)
                .priority(Priority.LOW)
                .build();

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        Boolean result = taskService.deleteTask(1);

        assertTrue(result);
        verify(taskRepository).delete(task);
    }

    @Test
    void testFindByStatus() {
        List<Tasks> tasks = List.of(
                Tasks.builder().title("Task1").status(Status.OPEN).priority(Priority.LOW).createAt(LocalDateTime.now()).build()
        );

        when(taskRepository.findByStatus(Status.OPEN)).thenReturn(tasks);

        List<TaskDto> dtos = taskService.findByStatus(Status.OPEN);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().title()).isEqualTo("Task1");
    }

    @Test
    void testFindByPriority() {
        List<Tasks> tasks = List.of(
                Tasks.builder().title("Task2").status(Status.OPEN).priority(Priority.HIGH).createAt(LocalDateTime.now()).build()
        );

        when(taskRepository.findByPriority(Priority.HIGH)).thenReturn(tasks);

        List<TaskDto> dtos = taskService.findByPriority(Priority.HIGH);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().title()).isEqualTo("Task2");
    }
}
