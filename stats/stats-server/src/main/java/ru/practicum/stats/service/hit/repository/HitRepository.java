package ru.practicum.stats.service.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.service.hit.model.Hit;

public interface HitRepository extends JpaRepository<Hit, Long> {
}