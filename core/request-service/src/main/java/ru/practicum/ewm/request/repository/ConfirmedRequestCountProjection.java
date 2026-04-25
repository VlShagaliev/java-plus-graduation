package ru.practicum.ewm.request.repository;

public interface ConfirmedRequestCountProjection {
    long getEventId();

    long getConfirmedCount();
}