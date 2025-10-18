package ch.sintere.task.entities.converter;

import ch.sintere.task.entities.Priority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PriorityConverter implements AttributeConverter<Priority, String> {

    @Override
    public String convertToDatabaseColumn(Priority priority) {
        return switch (priority) {
            case LOW -> "L";
            case MEDIUM -> "M";
            case HIGH -> "H";
        };
    }

    @Override
    public Priority convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "L" -> Priority.LOW;
            case "M" -> Priority.MEDIUM;
            case "H" -> Priority.HIGH;
            default -> throw new IllegalArgumentException("Unknown priority code: " + dbData);
        };
    }
}
