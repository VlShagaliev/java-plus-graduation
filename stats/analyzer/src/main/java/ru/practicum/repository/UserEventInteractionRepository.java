package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.UserEventInteraction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserEventInteractionRepository extends JpaRepository<UserEventInteraction, Long> {

    Optional<UserEventInteraction> findByUserIdAndEventId(Long userId, Long eventId);

    List<UserEventInteraction> findAllByUserId(Long userId);

    List<UserEventInteraction> findAllByEventIdIn(Collection<Long> eventIds);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}