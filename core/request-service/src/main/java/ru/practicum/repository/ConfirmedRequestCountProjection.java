package ru.practicum.repository;

public interface ConfirmedRequestCountProjection {
    long getEventId();

    long getConfirmedCount();
}