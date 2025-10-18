package ch.sintere.task.entities;

import ch.sintere.task.entities.converter.PriorityConverter;
import ch.sintere.task.entities.converter.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, unique = true)
    private String title;

    @Convert(converter = StatusConverter.class)
    @Column(nullable = false)
    private Status status;

    @Convert(converter = PriorityConverter.class)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(insertable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDate dueDate;
}
