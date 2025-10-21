package ch.sintere.task.dto;

import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDto(
        @NotBlank(message = "Task shall have a title") String title,
        @NotNull(message = "Task shall have a status") Status status,
        @NotNull(message = "Task shall have a priority") Priority priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @FutureOrPresent LocalDate dueDate,
        @NotBlank String createdBy
) {
}
