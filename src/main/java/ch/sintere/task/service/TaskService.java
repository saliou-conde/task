package ch.sintere.task.service;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;

import java.util.List;

public interface TaskService {
    TaskDto addTask(TaskDto taskDto);
    TaskDto updateTask(TaskDto taskDto, Integer id);
    TaskDto findTaskById(Integer id);
    Boolean deleteTask(Integer id);
    List<TaskDto> findByStatus(Status status);
    List<TaskDto> findByPriority(Priority priority);
}
