package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryId(long categoryId);

    Optional<Event> findByIdAndInitiatorId(long eventId, long initiatorId);

    Optional<Event> findByIdAndState(long eventId, EventState state);

    Page<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = 'PUBLISHED'
              AND (:text = ''
                   OR LOWER(e.annotation) LIKE CONCAT('%', LOWER(:text), '%')
                   OR LOWER(e.description) LIKE CONCAT('%', LOWER(:text), '%'))
              AND (:categories IS NULL OR e.categoryId IN :categories)
              AND (:paid IS NULL OR e.paid = :paid)
              AND e.eventDate BETWEEN :start AND :end
            """)
    Page<Event> findAllPublishedByCriteria(
            @Param("text") String text,
            @Param("categories") Iterable<Long> categories,
            @Param("paid") Boolean paid,
            @Param("start") LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("""
            SELECT e
            FROM Event e
            WHERE (:users IS NULL OR e.initiatorId IN :users)
              AND (:states IS NULL OR e.state IN :states)
              AND (:categories IS NULL OR e.categoryId IN :categories)
              AND e.eventDate BETWEEN :start AND :end
            """)
    Page<Event> findAllByCriteria(
            @Param("users") Iterable<Long> users,
            @Param("states") Iterable<EventState> states,
            @Param("categories") Iterable<Long> categories,
            @Param("start") LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd,
            Pageable pageable
    );
}