package ch.sintere.task.service.impl;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.entities.Task;
import ch.sintere.task.exception.TaskAlreadyExistException;
import ch.sintere.task.exception.TaskDueDateInvalidException;
import ch.sintere.task.exception.TaskNotFoundException;
import ch.sintere.task.mapper.TaskMapper;
import ch.sintere.task.repository.TaskRepository;
import ch.sintere.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto addTask(TaskDto taskDto) {
        log.info("addTask(TaskDto taskDto) start");
        findByTitle(taskDto.title());
        Task tasks = Task.builder()
                .status(taskDto.status())
                .priority(taskDto.priority())
                .title(taskDto.title())
                .build();
        var savedTask = taskRepository.save(tasks);
        log.info("Task created at: {}", savedTask.getCreatedAt());
        log.info("addTask(TaskDto taskDto) end");
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDto updateTask(TaskDto taskDto, Integer id) {
        log.info("updateTask(TaskDto taskDto, Integer id) start");
        var existing  = findById(id);
        mergeTask(taskDto, existing );
        var updatedTask = taskRepository.save(existing );
        log.info("Task updated at: {}", updatedTask.getUpdatedAt());
        log.info("updateTask(TaskDto taskDto, Integer id) end");
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public TaskDto updateStatus(Integer id, TaskDto taskDto) {
        log.info("updateStatus(Integer id, TaskDto taskDto) start...");
        var existing = findById(id);
        validateTaskDto(existing, taskDto);
        existing.setStatus(taskDto.status());
        var updatedTask = taskRepository.save(existing );
        log.info("updatedAt: {}", updatedTask.getUpdatedAt());
        log.info("updateStatus(Integer id, TaskDto taskDto) started");
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public List<TaskDto> updatePriorityForAll(Priority oldPriority, Priority newPriority) {
        taskRepository.updatePriorityForAll(oldPriority, newPriority);
        return taskRepository.findByPriority(newPriority)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskDto findTaskById(Integer id) {
        log.info("findTaskById(Integer id) start");
        var task = findById(id);
        log.info("Task title is: {}", task.getTitle());
        log.info("findTaskById(Integer id) end");
        return taskMapper.toDto(task);
    }

    @Override
    public Boolean deleteTask(Integer id) {
        log.info("deleteTask(Integer id) start");
        var task = findById(id);
        log.info("Delete Task by id: {}", task.getId());
        taskRepository.delete(task);
        log.info("deleteTask(Integer id) end");
        return Boolean.TRUE;
    }

    @Override
    public List<TaskDto> findByStatus(Status status) {
        var taskDtoList = taskRepository.findByStatus(status)
                .stream()
                .map(taskMapper::toDto).toList();
        log.info("Number of status is:{}", taskDtoList.size());
        return taskDtoList;
    }

    @Override
    public List<TaskDto> findByPriority(Priority priority) {
        return taskRepository.findByPriority(priority)
                .stream()
                .map(taskMapper::toDto).toList();
    }

    private Task findById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found by the provided Id::{}", id);
                    return new TaskNotFoundException(format("Task not found by the provided Id:: %s", id));
                });
    }

    private void mergeTask(TaskDto taskDto, Task task) {
        task.setTitle(taskDto.title());
        task.setStatus(taskDto.status());
        task.setPriority(taskDto.priority());
        validateDueDate(taskDto.dueDate(), task);
    }

    private void findByTitle(String title) {
        if(taskRepository.findByTitle(title).isPresent()) {
            log.warn("Task already exists by the provided title: {}", title);
            throw  new TaskAlreadyExistException(format("Task already exists by the provided title:: %s", title));
        }
    }

    private void validateTaskDto(Task task, TaskDto taskDto) {
        if(!task.getTitle().equals(taskDto.title())) {
            throw new IllegalArgumentException(format("Task title %s is not equal to TaskDto title %s", task.getTitle(), taskDto.title()));
        }
        if(!task.getPriority().equals(taskDto.priority())) {
            throw new IllegalArgumentException(format("Task priority %s is not equal to TaskDto priority %s", task.getPriority(), taskDto.priority()));
        }
    }

    private void validateDueDate(LocalDate dueDate, Task task) {
        if(dueDate != null && !checkDueDate(dueDate)) {
            throw new TaskDueDateInvalidException(format("Due date shall be in present or in future:: dueDate is %s", dueDate));
        }
        if(dueDate !=null) {
            task.setDueDate(dueDate);
        }
    }

    private boolean checkDueDate(LocalDate dueDate) {
        var now = LocalDate.now();
        return dueDate.isEqual(now) || now.isBefore(dueDate);
    }


}
