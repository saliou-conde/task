package ch.sintere.task.controller;

import ch.sintere.task.dto.PriorityUpdateRequest;
import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Status;
import ch.sintere.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static ch.sintere.task.entities.Priority.*;
import static ch.sintere.task.entities.Status.OPEN;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.*;

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
                now(),
                null,
                LocalDate.now().plusDays(1),
                "user1"
        );
    }

    @Test
    void addTask_shouldReturnCreatedTask() {
        when(taskService.addTask(taskDto)).thenReturn(taskDto);

        //When
        var response = taskController.addTask(taskDto);

        //Then
        assertAll("Add Task Assertions",
                () -> {
                    assertThat(response.getStatusCode()).isEqualTo(CREATED);
                    assertThat(response.getBody())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", OPEN, HIGH);
                }
        );

        //Verify interaction
        verify(taskService).addTask(taskDto);
    }

    @Test
    void findTaskById_shouldReturnTask() {
        //Given
        Integer taskId = 1;

        when(taskService.findTaskById(taskId)).thenReturn(taskDto);

        //When
        var response = taskController.findTaskById(taskId);

        //Then
        assertAll("Find Task Assertions",
                () -> {
                    assertThat(response.getStatusCode()).isEqualTo(OK);
                    assertThat(response.getBody())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", OPEN, HIGH);

                }
        );

        //Verify interaction
        verify(taskService).findTaskById(taskId);
    }

    @Test
    void findByStatus_shouldReturnList() {
        //Given
        var status = OPEN;
        when(taskService.findByStatus(status)).thenReturn(List.of(taskDto));

        //When
        var response = taskController.findByStatus(status);

        //Then
        assertAll("Find By Status Assertions",
                () -> {
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody()).hasSize(1);
                    assertThat(response.getStatusCode()).isEqualTo(OK);
                    assertThat(response.getBody().getFirst())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", status, HIGH);
                }
        );

        //Verify interaction
        verify(taskService).findByStatus(status);
    }

    @Test
    void findByPriority_shouldReturnList() {
        //Given
        var priority = HIGH;

        when(taskService.findByPriority(priority)).thenReturn(List.of(taskDto));

        //When
        var response = taskController.findByPriority(priority);

        //Then
        assertAll("Find By Priority Assertions",
                () -> {
                    assertThat(response.getStatusCode()).isEqualTo(OK);
                    assertThat(response.getBody()).hasSize(1);
                    assertThat(response.getBody().getFirst())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", OPEN, priority);
                }
        );

        //Verify interaction
        verify(taskService).findByPriority(priority);
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() {
        //Given
        var taskId = 1;
        when(taskService.updateTask(taskDto, taskId)).thenReturn(taskDto);

        //When
        var response = taskController.updateTask(taskDto, taskId);

        //Then
        assertAll("Update Task Assertions",
                () -> {
                    assertThat(response.getStatusCode()).isEqualTo(OK);
                    assertThat(response.getBody())
                            .extracting(TaskDto::title, TaskDto::status, TaskDto::priority)
                            .containsExactly("My Task", OPEN, HIGH);
                }
        );

        //Verify interaction
        verify(taskService).updateTask(taskDto, taskId);
    }

    @Test
    void updateStatus_shouldReturnUpdatedStatus() {
        //Given
        var updatedDto = new TaskDto(
                "My Task",
                Status.DONE,
                HIGH,
                now(),
                now(),
                LocalDate.now().plusDays(2),
                "user1"
        );

        when(taskService.updateStatus(1, updatedDto)).thenReturn(updatedDto);

        //When
        var response = taskController.updateStatus(1, updatedDto);

        //Then
        assertAll("Update Status Assertions",
                () -> {
                    assertThat(response.getStatusCode()).isEqualTo(OK);
                    assertThat(response.getBody())
                            .extracting(TaskDto::title, TaskDto::status)
                            .containsExactly("My Task", Status.DONE);
                }
        );

        //Verify interaction
        verify(taskService).updateStatus(1, updatedDto);
    }

    @Test
    void updatePriorityForAll_shouldReturnUpdatedTasks() {
        //Given
        PriorityUpdateRequest request = new PriorityUpdateRequest(LOW, MEDIUM);

        when(taskService.updatePriorityForAll(LOW, MEDIUM))
                .thenReturn(List.of(taskDto));

        //When
        var response = taskController.updatePriorityForAll(request);

        //Then
        assertAll("Update Priority Assertions",
                () -> {
                    assertThat(response.getStatusCode()).isEqualTo(OK);
                    assertThat(response.getBody()).hasSize(1);
                    assertThat(response.getBody().getFirst())
                            .extracting(TaskDto::title, TaskDto::priority)
                            .containsExactly("My Task", HIGH);
                }
        );

        //Verify interaction
        verify(taskService).updatePriorityForAll(LOW, MEDIUM);
    }

    @Test
    void deleteTaskById_shouldReturnNoContent() {
        //Given
        var id = 1;

        when(taskService.deleteTask(id)).thenReturn(true);

        //When
        var response = taskController.deleteTaskById(id);

        //Then
        assertAll("Delete Task Assertions",
                () -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
                }
        );



        //Verify interaction
        verify(taskService).deleteTask(id);
    }
}
