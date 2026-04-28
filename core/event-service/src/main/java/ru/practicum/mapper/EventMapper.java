package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.AdminEventAction;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.dto.UserEventAction;
import ru.practicum.dto.EventShortDto;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static Event from(NewEventDto newEvent, long categoryId, long initiatorId) {
        return Event.builder()
                .categoryId(categoryId)
                .initiatorId(initiatorId)
                .location(newEvent.getLocation())
                .state(EventState.PENDING)
                .description(newEvent.getDescription())
                .annotation(newEvent.getAnnotation())
                .title(newEvent.getTitle())
                .createdOn(LocalDateTime.now())
                .publishedOn(newEvent.isRequestModeration() ? null : LocalDateTime.now())
                .eventDate(newEvent.getEventDate())
                .paid(newEvent.getPaid())
                .requestModeration(newEvent.isRequestModeration())
                .participantLimit(newEvent.getParticipantLimit())
                .build();
    }

    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    public static EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(0)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .modsComment(event.getModsComment() == null ? null : event.getModsComment())
                .views(0)
                .build();
    }

    public static void updateEventProperties(UpdateEventUserRequest updatedEvent, Event event, Long categoryId) {
        updateEventFromRequest(updatedEvent, event, categoryId);

        UserEventAction stateAction = updatedEvent.getStateAction();
        if (stateAction != null) {
            event.setState(stateAction == UserEventAction.CANCEL_REVIEW
                    ? EventState.CANCELED
                    : EventState.PENDING);
        }
    }

    public static void updateEventProperties(UpdateEventAdminRequest updatedEvent, Event event, Long categoryId) {
        updateEventFromRequest(updatedEvent, event, categoryId);

        AdminEventAction stateAction = updatedEvent.getStateAction();
        if (stateAction != null) {
            event.setState(stateAction == AdminEventAction.REJECT_EVENT
                    ? EventState.CANCELED
                    : EventState.PUBLISHED);
        }

        if (stateAction == AdminEventAction.PUBLISH_EVENT) {
            event.setPublishedOn(LocalDateTime.now());
        }

        if (stateAction == AdminEventAction.REJECT_EVENT && updatedEvent.getModsComment() != null) {
            event.setModsComment(updatedEvent.getModsComment());
        }
    }

    private static void updateEventFromRequest(UpdateEventRequest updatedEvent, Event event, Long categoryId) {
        if (updatedEvent.getAnnotation() != null) {
            event.setAnnotation(updatedEvent.getAnnotation());
        }
        if (categoryId != null) {
            event.setCategoryId(categoryId);
        }
        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getEventDate() != null) {
            event.setEventDate(updatedEvent.getEventDate());
        }
        if (updatedEvent.getLocation() != null) {
            event.setLocation(updatedEvent.getLocation());
        }
        if (updatedEvent.getPaid() != null) {
            event.setPaid(updatedEvent.getPaid());
        }
        if (updatedEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        }
        if (updatedEvent.getRequestModeration() != null) {
            event.setRequestModeration(updatedEvent.getRequestModeration());
        }
        if (updatedEvent.getTitle() != null) {
            event.setTitle(updatedEvent.getTitle());
        }
        if (!event.isRequestModeration()) {
            event.setPublishedOn(LocalDateTime.now());
        }
    }
}