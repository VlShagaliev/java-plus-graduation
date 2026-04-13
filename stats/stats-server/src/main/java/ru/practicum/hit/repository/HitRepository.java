package ru.practicum.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.hit.model.Hit;

public interface HitRepository extends JpaRepository<Hit, Long> {
}