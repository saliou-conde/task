package ch.sintere.task.service.impl;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.entities.Tasks;
import ch.sintere.task.exception.TaskAlreadyExistException;
import ch.sintere.task.exception.TaskNotFoundException;
import ch.sintere.task.repository.TaskRepository;
import ch.sintere.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskDto addTask(TaskDto taskDto) {
        log.info("addTask(TaskDto taskDto) start");
        findByTitle(taskDto.title());
        Tasks tasks = Tasks.builder()
                .status(taskDto.status())
                .priority(taskDto.priority())
                .title(taskDto.title())
                .build();
        var savedTask = taskRepository.save(tasks);
        log.info("Task created at: {}", savedTask.getCreateAt());
        log.info("addTask(TaskDto taskDto) end");
        return new TaskDto(savedTask.getTitle(), savedTask.getStatus(), savedTask.getPriority(), savedTask.getCreateAt(), savedTask.getUpdateAt());
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto, Integer id) {
        log.info("updateTask(TaskDto taskDto, Integer id) start");
        var task = findById(id);
        mergeTask(taskDto, task);
        taskRepository.save(task);
        log.info("Task updated at: {}", task.getUpdateAt());
        log.info("updateTask(TaskDto taskDto, Integer id) end");
        return new TaskDto(task.getTitle(), task.getStatus(), task.getPriority(), task.getCreateAt(), task.getUpdateAt());
    }

    @Override
    public TaskDto findTaskById(Integer id) {
        log.info("findTaskById(Integer id) start");
        var task = findById(id);
        log.info("Task title is: {}", task.getTitle());
        log.info("findTaskById(Integer id) end");
        return new TaskDto(task.getTitle(), task.getStatus(), task.getPriority(), task.getCreateAt(), task.getUpdateAt());
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
        return taskRepository.findByStatus(status)
                .stream()
                .map(task -> new TaskDto(task.getTitle(), task.getStatus(), task.getPriority(), task.getCreateAt(), task.getUpdateAt())).toList();
    }

    @Override
    public List<TaskDto> findByPriority(Priority priority) {
        return taskRepository.findByPriority(priority)
                .stream()
                .map(task -> new TaskDto(task.getTitle(), task.getStatus(), task.getPriority(), task.getCreateAt(), task.getUpdateAt())).toList();
    }

    private Tasks findById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found by the provided Id::{}", id);
                    return new TaskNotFoundException(format("Task not found by the provided Id:: %s", id));
                });
    }

    private void mergeTask(TaskDto taskDto, Tasks task) {
        task.setTitle(taskDto.title());
        task.setStatus(taskDto.status());
        task.setPriority(taskDto.priority());
    }

    private void findByTitle(String title) {
        if(taskRepository.findByTitle(title).isPresent()) {
            log.warn("Task already exists by the provided title: {}", title);
            throw  new TaskAlreadyExistException(format("Task already exists by the provided title:: %s", title));
        }
    }
}
