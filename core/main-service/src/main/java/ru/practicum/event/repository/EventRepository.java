package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryId(long categoryId);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Optional<Event> findByIdAndInitiatorId(long eventId, long initiatorId);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Optional<Event> findByIdAndState(long eventId, EventState state);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Page<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.initiator " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND ((:text) = '' OR LOWER(e.annotation) LIKE CONCAT('%', LOWER(:text), '%') " +
            "OR LOWER(e.description) LIKE CONCAT('%', LOWER(:text), '%')) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND ((:paid) IS NULL OR e.paid = :paid) " +
            "AND e.eventDate BETWEEN :start AND :end " +
            "AND (:onlyAvl = FALSE OR e.participantLimit > (" +
            "    SELECT COUNT(r) FROM Request r " +
            "    WHERE r.event.id = e.id AND r.status = 'CONFIRMED'))")
    Page<Event> findAllPublishedByCriteria(
            @Param("text") String text,
            @Param("categories") Iterable<Long> categories,
            @Param("paid") Boolean paid,
            @Param("start") LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd,
            @Param("onlyAvl") boolean onlyAvailable,
            Pageable pageable
    );

    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.initiator " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate BETWEEN :start AND :end")
    Page<Event> findAllByCriteria(
            @Param("users") Iterable<Long> users,
            @Param("states") Iterable<EventState> states,
            @Param("categories") Iterable<Long> categories,
            @Param("start") LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd,
            Pageable pageable
    );
}