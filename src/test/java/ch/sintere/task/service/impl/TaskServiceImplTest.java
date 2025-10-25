package ch.sintere.task.service.impl;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.dto.TaskStatus;
import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.entities.Task;
import ch.sintere.task.exception.TaskDueDateInvalidException;
import ch.sintere.task.exception.TaskNotFoundException;
import ch.sintere.task.mapper.TaskMapperImpl;
import ch.sintere.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ch.sintere.task.entities.Priority.*;
import static ch.sintere.task.entities.Status.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapperImpl taskMapper;
    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Nested
    class AddTask {

        @Test
        void addTask_shouldInsertTask_whenValidDataGiven() {
            //Given
            var expectedTaskDto = new TaskDto("Test Title", Status.OPEN, Priority.HIGH, null, null, LocalDate.now(), "SYSTEM");

            var savedTask = Task.builder()
                    .id(1)
                    .title("Test Title")
                    .status(Status.OPEN)
                    .priority(Priority.HIGH)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
            when(taskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            //When
            var result = taskService.addTask(expectedTaskDto);

            //
            assertAll(
                    () -> assertThat(result.title()).isEqualTo(expectedTaskDto.title()),
                    () -> assertThat(result.status()).isEqualTo(expectedTaskDto.status()),
                () -> assertThat(result.priority()).isEqualTo(expectedTaskDto.priority())
            );

            //Verify
            verify(taskRepository).save(any(Task.class));
        }
    }

    @Nested
    class UpdateTask {

        @Test
        void updateTask_shouldModifyTask_whenValidDataGiven() {
            //Given
            var taskId = 1;
            var oldCreateDate = LocalDateTime.now().minusDays(1);
            var existingTask = Task.builder()
                    .id(taskId)
                    .title("Old Title")
                    .status(DONE)
                    .priority(MEDIUM)
                    .createdAt(oldCreateDate)
                    .build();

            var expectedTaskDto = createTaskDto("New Title", DONE, MEDIUM, oldCreateDate, null);

            when(taskRepository.findById(taskId))
                    .thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class)))
                    .thenReturn(existingTask);
            when(taskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            //When
            var result = taskService.updateTask(expectedTaskDto, taskId);

            //Then
            assertAll(
                    () -> assertThat(result.title()).isEqualTo("New Title"),
                    () -> assertThat(result.status()).isEqualTo(DONE),
                    () -> assertThat(result.priority()).isEqualTo(MEDIUM),
                    () -> assertThat(result.createdAt()).isEqualTo(oldCreateDate)
            );
        }

        @Test
        void updateTask_shouldNotModifyTask_whenDueDateIsBeforeNow() {
            //Given
            var taskId = 1;
            var oldCreateDate = LocalDateTime.now();

            var dueDate = LocalDate.now().minusDays(1);
            var existingTask = Task.builder()
                    .id(taskId)
                    .title("Old Title")
                    .status(DONE)
                    .priority(MEDIUM)
                    .createdAt(oldCreateDate)
                    .dueDate(dueDate)
                    .build();

            var expectedTaskDto = createTaskDto("New Title", DONE, MEDIUM, oldCreateDate, dueDate);

            when(taskRepository.findById(taskId))
                    .thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class)))
                    .thenReturn(existingTask);
            when(taskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            // When & Then TaskDueDateInvalidException("Due date shall be in present or in future")

            assertThatThrownBy(() -> taskService.updateTask(expectedTaskDto, taskId))
                    .isInstanceOf(TaskDueDateInvalidException.class)
                    .hasMessageContaining(format("Due date shall be in present or in future:: dueDate is %s", dueDate));
        }

        @Test
        void updateStatus_shouldModifyOnlyStatus_whenValidDataGiven() {
            //Given
            var taskId = 1;
            var title = "Title";
            var createDate = LocalDateTime.now();
            var existingTask = Task.builder()
                    .id(taskId)
                    .title(title)
                    .status(DONE)
                    .priority(MEDIUM)
                    .createdAt(createDate)
                    .build();

            var taskDto = createTaskDto(title, IN_PROGRESS, MEDIUM, createDate, null);

            when(taskRepository.findById(taskId))
                    .thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class)))
                    .thenReturn(existingTask);
            when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

            //When
            var updatedTask = taskService.updateStatus(taskId, taskDto);

            //Then
            assertAll(
                    () -> assertThat(updatedTask.title()).isEqualTo(taskDto.title()),
                    () -> assertThat(updatedTask.status()).isEqualTo(taskDto.status())
            );
        }

        @Test
        void updateStatus_shouldNotModifyStatus_whenTitleAreNotEqual() {
            //Given
            var taskId = 1;
            var oldTitle = "Old Title";
            var newTitle = "New Title";
            var createDate = LocalDateTime.now();
            var existingTask = Task.builder()
                    .id(taskId)
                    .title(oldTitle)
                    .status(DONE)
                    .priority(MEDIUM)
                    .createdAt(createDate)
                    .build();

            var taskDto = createTaskDto(newTitle, IN_PROGRESS, MEDIUM, createDate, null);

            when(taskRepository.findById(taskId))
                    .thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class)))
                    .thenReturn(existingTask);
            when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

            // When & Then
            assertThatThrownBy(() -> taskService.updateStatus(taskId, taskDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(format("Task title %s is not equal to TaskDto title %s", existingTask.getTitle(), taskDto.title()));
        }

        @Test
        void updateStatus_shouldNotModifyStatus_whenPriorityAreNotEqual() {
            //Given
            var taskId = 1;
            var oldTitle = "Old Title";
            var createDate = LocalDateTime.now();
            var existingTask = Task.builder()
                    .id(taskId)
                    .title(oldTitle)
                    .status(DONE)
                    .priority(MEDIUM)
                    .createdAt(createDate)
                    .build();

            var taskDto = createTaskDto(oldTitle, DONE, LOW, createDate, null);

            when(taskRepository.findById(taskId))
                    .thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class)))
                    .thenReturn(existingTask);
            when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

            // When & Then
            assertThatThrownBy(() -> taskService.updateStatus(taskId, taskDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(format("Task priority %s is not equal to TaskDto priority %s", existingTask.getPriority(), taskDto.priority()));
        }

        @Test
        void updatePriorityForAll_shouldUpdatePriorityForAll_whenListOfTaskIsNotEmpty() {
            //Given
            var oldPriority = HIGH;
            var newPriority = LOW;
            List<Task> taskList = List.of(
                    Task.builder().priority(oldPriority).title("Title 1").status(DONE).createdAt(LocalDateTime.now()).build(),
                    Task.builder().priority(oldPriority).title("Title 2").status(DONE).createdAt(LocalDateTime.now()).build(),
                    Task.builder().priority(oldPriority).title("Title 3").status(DONE).createdAt(LocalDateTime.now()).build(),
                    Task.builder().priority(oldPriority).title("Title 3").status(DONE).createdAt(LocalDateTime.now()).build(),
                    Task.builder().priority(MEDIUM).title("Title 5").status(DONE).createdAt(LocalDateTime.now()).build(),
                    Task.builder().priority(MEDIUM).title("Title 5").status(DONE).createdAt(LocalDateTime.now()).build()
                    );

            TaskDto expectedTaskDto = new TaskDto(
                    "Title 1",
                    DONE,
                    newPriority,
                    LocalDateTime.now(),
                    null,
                    LocalDate.now(),
                    "SYSTEM");


            when(taskRepository.updatePriorityForAll(oldPriority, newPriority)).thenReturn(1);
            when(taskRepository.findByPriority(newPriority)).thenReturn(taskList);
            when(taskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            //When
            var priorityList = taskService.updatePriorityForAll(oldPriority, newPriority);

            //Then
            assertAll(
                    () -> assertThat(priorityList).isNotNull()
            );
        }
    }


    @Nested
    class FindTaskById {

        @Test
        void findTaskById_shouldFindTask_whenTaskIsPresent() {
            //Given
            var id = 1;
            var expectedTitle = "Title";
            Task task = Task.builder()
                    .id(id)
                    .title(expectedTitle)
                    .status(OPEN)
                    .priority(HIGH)
                    .createdAt(LocalDateTime.now())
                    .build();
            var expectedTaskDto = createTaskDto(task.getTitle(), task.getStatus(), task.getPriority(), task.getCreatedAt(), null);

            when(taskRepository.findById(id)).thenReturn(Optional.of(task));
            when(taskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            //When
            var dto = taskService.findTaskById(id);

            //Then
            assertThat(dto.title()).isEqualTo(expectedTitle);
        }

        @Test
        void findTaskById_shouldNotFoundById_whenTaskIsAbsent() {
            when(taskRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(1));
        }

        @Test
        void testDeleteTask() {
            //Given
            var id = 1;
            Task task = Task.builder()
                    .id(id)
                    .title("Title")
                    .status(OPEN)
                    .priority(LOW)
                    .build();

            when(taskRepository.findById(id)).thenReturn(Optional.of(task));

            //When
            Boolean result = taskService.deleteTask(id);

            //Then
            assertTrue(result);

            //Verify
            verify(taskRepository).delete(task);
        }
    }

    @Nested
    class FindTaskByStatus {

        @Test
        void findTaskByStatus_shouldFindTaskByStatus_whenTaskIsPresent() {
            //Given
            var id = 1;
            var expectedTitle = "Task1";
            var createdAt = LocalDateTime.now();
            List<Task> tasks = List.of(
                    Task.builder().title(expectedTitle).status(OPEN).priority(LOW).createdAt(createdAt).createdBy("SYSTEM").build()
            );
            var expectedTaskDto = createTaskDto(expectedTitle, OPEN, LOW, createdAt, null);

            when(taskRepository.findByStatus(OPEN)).thenReturn(tasks);
            when(taskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            //When
            var taskDtoList = taskService.findByStatus(OPEN);

            //Then
            assertAll(
                    () -> assertThat(taskDtoList).hasSize(id),
                    () -> {
                        assertNotNull(taskDtoList);
                        assertThat(taskDtoList.getFirst().title()).isEqualTo(expectedTaskDto.title());
                    }
            );
        }

        @Nested
        class ValidateOnlyStatusChanged {

            @Test
            void validateOnlyStatusChanged_shouldThrowIllegalArgumentException_whenEntityAndDTOFieldsNotEqual() {
                //Given
                var title = "My Title";
                var status = IN_PROGRESS;
                var createdAt = LocalDateTime.now();
                var updatedAt = LocalDateTime.now();
                var dueDate = LocalDate.now();
                var createdBy = "My User";
                Task existing = Task.builder()
                        .title(title)
                        .dueDate(dueDate)
                        .status(status)
                        .priority(MEDIUM)
                        .createdAt(createdAt)
                        .createdBy(createdBy)
                        .build();
                TaskDto taskDto = new TaskDto(title, status, LOW, createdAt, updatedAt, dueDate, createdBy);

                //When & Then
                assertThatThrownBy(() -> taskService.validateOnlyStatusChanged(existing, taskDto))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Only 'status' field is allowed to change (violated: priority)" );
            }


            @Test
            void validateOnlyStatusChanged_shouldValidateStatusChanged_whenEntityAndDTOFieldsEqual() {
                //Given
                var title = "My Title";
                var status = OPEN;
                var priority =  MEDIUM;
                var createdAt = LocalDateTime.now();
                var updatedAt = LocalDateTime.now();
                var dueDate = LocalDate.now();
                var createdBy = "My User";
                Task existing = Task.builder()
                        .title(title)
                        .dueDate(dueDate)
                        .status(status)
                        .priority(priority)
                        .createdAt(createdAt)
                        .createdBy(createdBy)
                        .build();
                TaskDto taskDto = new TaskDto(title, status, priority, createdAt, updatedAt, dueDate, createdBy);

                //When
                taskService.validateOnlyStatusChanged(existing, taskDto);

                //Then
                assertAll(
                        () -> {

                        }
                );
            }
        }

        @Nested
        class TaskServiceImplStatusValidationTest {

            @Test
            void shouldAllowTransitionFromOpenToInProgress() {
                //Given
                var oldStatus = new TaskStatus(OPEN);
                var newStatus = new TaskStatus(IN_PROGRESS);

                assertAll(
                        () -> assertThatCode(() ->
                                taskService.validateStatusTransition(
                                        oldStatus,
                                        newStatus
                                )
                        ).doesNotThrowAnyException()
                );
            }

            @Test
            void shouldAllowTransitionFromInProgressToDone() {
                //Given
                var oldStatus = new TaskStatus(IN_PROGRESS);
                var newStatus = new TaskStatus(DONE);

                //When & Then
                assertAll(
                        () -> assertThatCode(() ->
                                taskService.validateStatusTransition(oldStatus, newStatus)
                        ).doesNotThrowAnyException()
                );
            }

            @Test
            void shouldNotAllowTransitionFromDoneToOpen() {
                //Given
                var oldStatus = new TaskStatus(Status.DONE);
                var newStatus =new TaskStatus(Status.OPEN);

                //When & Then
                assertAll(
                        () -> assertThatThrownBy(() ->
                                taskService.validateStatusTransition(
                                        oldStatus, newStatus
                                )
                        )
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("Cannot change status of a completed task.")
                );
            }

            @Test
            void shouldNotAllowTransitionFromDoneToInProgress() {
                //Given
                var oldStatus = new TaskStatus(Status.DONE);
                var newStatus = new TaskStatus(Status.IN_PROGRESS);

                //When & Then
                assertAll(
                        () -> assertThatThrownBy(() ->
                                taskService.validateStatusTransition(oldStatus, newStatus)
                        )
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("Cannot change status of a completed task.")
                );
            }

            @Test
            void shouldAllowTransitionFromDoneToDone() {
                //
                var oldStatus = new TaskStatus(Status.DONE);
                var newStatus = new TaskStatus(Status.DONE);

                //When & Then
                assertAll(
                        () -> assertThatCode(() ->
                                taskService.validateStatusTransition(oldStatus, newStatus)
                        ).doesNotThrowAnyException()
                );
            }
        }

    }


    @Nested
    class FindTaskByPriority {

        @Test
        void findByPriority_shouldFindTaskByPriority_whenTaskIsPresent() {
            //Given
            var id = 1;
            var createdAt = LocalDateTime.now();
            List<Task> tasks = List.of(
                    Task.builder().title("Task2").status(OPEN).priority(HIGH).createdAt(createdAt).build()
            );
            var expectedTaskDto = createTaskDto("Task2", OPEN, HIGH, createdAt, null);

            when(taskRepository.findByPriority(HIGH)).thenReturn(tasks);
            when(taskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            //When
            var taskDtoList = taskService.findByPriority(HIGH);

            //Then
            assertAll(
                    () -> assertThat(taskDtoList).hasSize(id),
                    () -> {
                        assertNotNull(taskDtoList);
                        assertThat(taskDtoList.getFirst().title()).isEqualTo("Task2");
                    }
            );
        }
    }

    private TaskDto createTaskDto(String title, Status status, Priority priority, LocalDateTime createdAt, LocalDate dueDate) {
        return new TaskDto(title, status, priority, createdAt, null, dueDate, "SYSTEM");
    }

    private List<TaskDto> mapToTaskDto(List<Task> taskList) {
        return taskList.stream().map(task ->
                new TaskDto(
                        task.getTitle(),
                        task.getStatus(),
                        task.getPriority(),
                        task.getCreatedAt(),
                        task.getUpdatedAt(),
                        task.getDueDate(),
                        task.getCreatedBy()
                        )).toList();
    }
}
