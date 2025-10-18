package ch.sintere.task.entities.converter;

import ch.sintere.task.entities.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        switch (status) {
            case ACTIVE: return "A";
            case INACTIVE: return "I";
            case PENDING: return "P";
            default: throw new IllegalArgumentException("Unknown status: " + status);
        }
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        switch (dbData) {
            case "A": return Status.ACTIVE;
            case "I": return Status.INACTIVE;
            case "P": return Status.PENDING;
            default: throw new IllegalArgumentException("Unknown dbData: " + dbData);
        }
    }
}
