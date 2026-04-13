package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    long countByEventIdAndStatus(long eventId, RequestStatus status);

    long countByEventIdAndStatusNot(long eventId, RequestStatus status);

    @EntityGraph(attributePaths = {"event", "requester"})
    Optional<Request> findByIdAndRequesterId(long id, long requesterId);

    boolean existsByEventIdAndRequesterId(long eventId, long requesterId);

    @Modifying
    @Query("UPDATE Request r " +
            "SET r.status = :newStatus " +
            "WHERE r.status = :oldStatus AND r.event.id = :eventId")
    void updateStatusesByEventAndCurrentStatus(
            @Param("oldStatus") RequestStatus oldStatus,
            @Param("newStatus") RequestStatus newStatus,
            @Param("eventId") long eventId
    );

    @EntityGraph(attributePaths = {"event", "requester"})
    List<Request> findAllByRequesterId(long requesterId);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<Request> findAllByEventIdAndEventInitiatorId(long eventId, long initiatorId);
}