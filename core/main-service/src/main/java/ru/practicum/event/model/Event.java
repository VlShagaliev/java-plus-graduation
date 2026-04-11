package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    private EventState state;

    private String description;

    private String annotation;

    private String title;

    @JoinColumn(name = "published_on")
    private LocalDateTime publishedOn;

    @JoinColumn(name = "created_on")
    private LocalDateTime createdOn;

    @JoinColumn(name = "event_date")
    private LocalDateTime eventDate;

    private boolean paid;

    @JoinColumn(name = "request_moderation")
    private boolean requestModeration;

    @JoinColumn(name = "participant_limit")
    private int participantLimit;

    @Column(name = "mods_comment")
    private String modsComment;
}
