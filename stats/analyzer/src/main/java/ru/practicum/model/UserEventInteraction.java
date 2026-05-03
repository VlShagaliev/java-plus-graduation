package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "user_event_interactions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_event_interactions_user_event",
                columnNames = {"user_id", "event_id"}
        )
)
public class UserEventInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}