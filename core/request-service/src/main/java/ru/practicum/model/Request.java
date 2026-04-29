package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime created;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "requester_id", nullable = false)
    private long requesterId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}