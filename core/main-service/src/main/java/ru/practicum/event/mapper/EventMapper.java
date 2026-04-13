package ru.practicum.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static Event from(NewEventDto newEvent, Category category, User initiator) {
        return Event.builder()
                .category(category)
                .initiator(initiator)
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
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    public static EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(0)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
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

    public static void updateEventProperties(UpdateEventUserRequest updatedEvent, Event event, Category category) {
        updateEventFromRequest(updatedEvent, event, category);

        UserEventAction stateAction = updatedEvent.getStateAction();
        if (stateAction != null) {
            event.setState(stateAction == UserEventAction.CANCEL_REVIEW
                    ? EventState.CANCELED
                    : EventState.PENDING);
        }
    }

    public static void updateEventProperties(UpdateEventAdminRequest updatedEvent, Event event, Category category) {
        updateEventFromRequest(updatedEvent, event, category);

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

    private static void updateEventFromRequest(UpdateEventRequest updatedEvent, Event event, Category category) {
        if (updatedEvent.getAnnotation() != null) {
            event.setAnnotation(updatedEvent.getAnnotation());
        }
        if (category != null) {
            event.setCategory(category);
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
