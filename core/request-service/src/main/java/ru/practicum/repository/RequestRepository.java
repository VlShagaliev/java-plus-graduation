package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    long countByEventIdAndStatus(long eventId, RequestStatus status);

    long countByEventIdAndStatusNot(long eventId, RequestStatus status);

    Optional<Request> findByIdAndRequesterId(long id, long requesterId);

    boolean existsByEventIdAndRequesterId(long eventId, long requesterId);

    @Modifying
    @Query("UPDATE Request r " +
            "SET r.status = :newStatus " +
            "WHERE r.status = :oldStatus AND r.eventId = :eventId")
    void updateStatusesByEventAndCurrentStatus(
            @Param("oldStatus") RequestStatus oldStatus,
            @Param("newStatus") RequestStatus newStatus,
            @Param("eventId") long eventId
    );

    @Query("""
            SELECT r.eventId AS eventId, COUNT(r) AS confirmedCount
            FROM Request r
            WHERE r.eventId IN :eventIds
              AND r.status = :status
            GROUP BY r.eventId
            """)
    List<ConfirmedRequestCountProjection> countConfirmedRequestsByEventIds(
            @Param("eventIds") Collection<Long> eventIds,
            @Param("status") RequestStatus status
    );

    List<Request> findAllByRequesterId(long requesterId);

    List<Request> findAllByEventId(long eventId);
}