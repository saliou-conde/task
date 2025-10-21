package ch.sintere.task.dto;

import ch.sintere.task.entities.Status;
import jakarta.validation.constraints.NotNull;

public record TaskStatus(
        @NotNull Status status
        ) {
}
