package ch.sintere.task.controller;

import ch.sintere.task.dto.PriorityUpdateRequest;
import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ch.sintere.task.entities.Priority.HIGH;
import static ch.sintere.task.entities.Status.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        openMocks(this);

        taskDto = new TaskDto(
                "My Task",
                OPEN,
                HIGH,
                LocalDateTime.now(),
                null,
                LocalDate.now().plusDays(1),
                "user1"
        );
    }

    @Test
    void addTask_shouldReturnCreatedTask() {
        when(taskService.addTask(taskDto)).thenReturn(taskDto);

        ResponseEntity<TaskDto> response = taskController.addTask(taskDto);

        assertAll("Add Task Assertions",
                () -> assertThat(response).isNotNull(),
                () -> {
                    Assertions.assertNotNull(response);
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                },
                () -> {
                    Assertions.assertNotNull(response);
                    assertThat(response.getBody()).isNotNull();
                },
                () -> {
                    Assertions.assertNotNull(response);
                    assertThat(response.getBody())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", OPEN, HIGH);
                }
        );

        verify(taskService).addTask(taskDto);
    }

    @Test
    void findTaskById_shouldReturnTask() {
        when(taskService.findTaskById(1)).thenReturn(taskDto);

        ResponseEntity<TaskDto> response = taskController.findTaskById(1);

        assertAll("Find Task Assertions",
                () -> assertThat(response.getStatusCode()).isEqualTo(OK),
                () -> assertThat(response.getBody())
                        .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                        .containsExactly("My Task", OPEN, HIGH)
        );

        verify(taskService).findTaskById(1);
    }

    @Test
    void findByStatus_shouldReturnList() {
        when(taskService.findByStatus(OPEN)).thenReturn(List.of(taskDto));

        ResponseEntity<List<TaskDto>> response = taskController.findByStatus(OPEN);

        assertAll("Find By Status Assertions",
                () -> assertThat(response.getStatusCode()).isEqualTo(OK),
                () -> assertThat(response.getBody()).hasSize(1),
                () -> {
                    Assertions.assertNotNull(response.getBody());
                    assertThat(response.getBody().getFirst())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", OPEN, HIGH);
                }
        );

        verify(taskService).findByStatus(OPEN);
    }

    @Test
    void findByPriority_shouldReturnList() {
        when(taskService.findByPriority(HIGH)).thenReturn(List.of(taskDto));

        ResponseEntity<List<TaskDto>> response = taskController.findByPriority(HIGH);

        assertAll("Find By Priority Assertions",
                () -> assertThat(response.getStatusCode()).isEqualTo(OK),
                () -> assertThat(response.getBody()).hasSize(1),
                () -> {
                    Assertions.assertNotNull(response.getBody());
                    assertThat(response.getBody().getFirst())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", OPEN, HIGH);
                }
        );

        verify(taskService).findByPriority(HIGH);
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() {
        when(taskService.updateTask(taskDto, 1)).thenReturn(taskDto);

        ResponseEntity<TaskDto> response = taskController.updateTask(taskDto, 1);

        assertAll("Update Task Assertions",
                () -> assertThat(response.getStatusCode()).isEqualTo(OK),
                () -> assertThat(response.getBody())
                        .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                        .containsExactly("My Task", OPEN, HIGH)
        );

        verify(taskService).updateTask(taskDto, 1);
    }

    @Test
    void updateStatus_shouldReturnUpdatedStatus() {
        TaskDto updatedDto = new TaskDto(
                "My Task",
                Status.DONE,
                HIGH,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDate.now().plusDays(2),
                "user1"
        );

        when(taskService.updateStatus(1, updatedDto)).thenReturn(updatedDto);

        ResponseEntity<TaskDto> response = taskController.updateStatus(1, updatedDto);

        assertAll("Update Status Assertions",
                () -> assertThat(response.getStatusCode()).isEqualTo(OK),
                () -> assertThat(response.getBody())
                        .extracting(TaskDto::title, TaskDto::status)
                        .containsExactly("My Task", Status.DONE)
        );

        verify(taskService).updateStatus(1, updatedDto);
    }

    @Test
    void updatePriorityForAll_shouldReturnUpdatedTasks() {
        PriorityUpdateRequest request = new PriorityUpdateRequest(Priority.LOW, Priority.MEDIUM);

        when(taskService.updatePriorityForAll(Priority.LOW, Priority.MEDIUM))
                .thenReturn(List.of(taskDto));

        ResponseEntity<List<TaskDto>> response = taskController.updatePriorityForAll(request);

        assertAll("Update Priority Assertions",
                () -> assertThat(response.getStatusCode()).isEqualTo(OK),
                () -> assertThat(response.getBody()).hasSize(1),
                () -> {
                    Assertions.assertNotNull(response.getBody());
                    assertThat(response.getBody().getFirst())
                            .extracting(TaskDto::title, TaskDto::priority)
                            .containsExactly("My Task", HIGH);
                }
        );

        verify(taskService).updatePriorityForAll(Priority.LOW, Priority.MEDIUM);
    }

    @Test
    void deleteTaskById_shouldReturnNoContent() {
        when(taskService.deleteTask(1)).thenReturn(true);

        ResponseEntity<Void> response = taskController.deleteTaskById(1);

        assertAll("Delete Task Assertions",
                () -> assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT)
        );

        verify(taskService).deleteTask(1);
    }
}
