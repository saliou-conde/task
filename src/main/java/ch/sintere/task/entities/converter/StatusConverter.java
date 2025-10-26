package ch.sintere.task.entities.converter;

import ch.sintere.task.entities.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import static ch.sintere.task.entities.Status.*;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        return switch (status) {
            case ACTIVE -> "A";
            case DONE -> "D";
            case INACTIVE -> "I";
            case IN_PROGRESS -> "IP";
            case OPEN -> "O";
            case PENDING -> "P";
        };
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "A" -> ACTIVE;
            case "D" -> DONE;
            case "I" -> INACTIVE;
            case "IP" -> IN_PROGRESS;
            case "O" -> OPEN;
            case "P" -> PENDING;
            default -> throw new IllegalArgumentException("Unknown dbData: " + dbData);
        };
    }
}
