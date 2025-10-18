package ch.sintere.task.entities;

import ch.sintere.task.entities.converter.PriorityConverter;
import ch.sintere.task.entities.converter.StatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tasks {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(insertable = false)
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @Convert(converter = StatusConverter.class)
    @Column(nullable = false)
    private Status status;

    @Convert(converter = PriorityConverter.class)
    @Column(nullable = false)
    private Priority priority;
}
