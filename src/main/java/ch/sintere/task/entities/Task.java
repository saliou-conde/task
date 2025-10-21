package ch.sintere.task.entities;

import ch.sintere.task.entities.converter.PriorityConverter;
import ch.sintere.task.entities.converter.StatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "tasks")
@NoArgsConstructor
public class Task extends BaseEntity {

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

    private LocalDate dueDate;
}
