package ch.sintere.task.service;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.dto.TaskStatus;
import ch.sintere.task.entities.Task;

public interface TaskStatusValidator {
    void validateOnlyStatusChanged(Task existing, TaskDto dto);
    void validateStatusTransition(TaskStatus oldStatus, TaskStatus newStatus);
}
