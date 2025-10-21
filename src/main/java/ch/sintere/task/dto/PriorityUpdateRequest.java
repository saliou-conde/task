package ch.sintere.task.dto;

import ch.sintere.task.entities.Priority;

public record PriorityUpdateRequest(
        Priority oldPriority, Priority newPriority
) {
}
