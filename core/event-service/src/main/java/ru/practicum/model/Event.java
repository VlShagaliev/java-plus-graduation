package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "category_id", nullable = false)
    private long categoryId;

    @Column(name = "initiator_id", nullable = false)
    private long initiatorId;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    private boolean paid;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "mods_comment", columnDefinition = "TEXT")
    private String modsComment;
}