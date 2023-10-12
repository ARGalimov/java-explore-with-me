package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.StateAction;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventMapper {
    private static final String INCORRECT_EVENT_STATE_MSG = "Impossible to public";
    private static final String INCORRECT_EVENT_STATE_REASON = "Event is published or canceled";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toNewEntity(NewEventDto newEventDto, Category category, User initiator, Location location) {
        LocalDateTime current = LocalDateTime.now();
        boolean requestModeration = newEventDto.getRequestModeration();
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .requests(new ArrayList<>())
                .createdOn(current)
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), FORMAT))
                .initiator(initiator)
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(requestModeration)
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toFullDto(Event event, Integer views, RatingDto rating) {
        return EventFullDto.childBuilder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(sumConfirmedRequests(event.getRequests()))
                .createdOn(event.getCreatedOn().format(FORMAT))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMAT))
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(LocationMapper.toDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(Objects.isNull(event.getPublishedOn()) ? null : event.getPublishedOn().format(FORMAT))
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .likes(Objects.isNull(rating.getLikes()) ? 0 : rating.getLikes())
                .dislikes(Objects.isNull(rating.getDislikes()) ? 0 : rating.getDislikes())
                .build();
    }

    public static EventShortDto toShortDto(Event event, Integer views, RatingDto rating) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(sumConfirmedRequests(event.getRequests()))
                .eventDate(event.getEventDate().format(FORMAT))
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .likes(firstOrDefault(rating.getLikes(), 0))
                .dislikes(firstOrDefault(rating.getDislikes(), 0))
                .build();
    }

    public static Event toUpdatedEntity(Event event, UpdateEventDto updateEventDto, Category category,
                                        Location location) {
        State state = Objects.nonNull(updateEventDto.getStateAction())
                ? getState(updateEventDto.getStateAction(), event) : event.getState();
        return Event.builder()
                .id(event.getId())
                .annotation(firstOrDefault(updateEventDto.getAnnotation(), event.getAnnotation()))
                .category(firstOrDefault(category, event.getCategory()))
                .requests(event.getRequests())
                .createdOn(event.getCreatedOn())
                .description(firstOrDefault(updateEventDto.getDescription(), event.getDescription()))
                .eventDate(Objects.nonNull(updateEventDto.getEventDate())
                        ? LocalDateTime.parse(updateEventDto.getEventDate(), FORMAT) : event.getEventDate())
                .initiator(event.getInitiator())
                .location(firstOrDefault(location, event.getLocation()))
                .paid(Objects.nonNull(updateEventDto.getPaid()) ? updateEventDto.getPaid() : event.getPaid())
                .participantLimit(firstOrDefault(updateEventDto.getParticipantLimit(), event.getParticipantLimit()))
                .publishedOn(Objects.nonNull(event.getPublishedOn())
                        ? event.getPublishedOn() : Objects.equals(state, State.PUBLISHED)
                        ? LocalDateTime.now() : null)
                .requestModeration(firstOrDefault(updateEventDto.getRequestModeration(), event.getRequestModeration()))
                .state(state)
                .title(firstOrDefault(updateEventDto.getTitle(), event.getTitle()))
                .build();
    }

    public static List<EventShortDto> toShortDtos(List<Event> events, Map<Integer, Integer> views, Map<Integer, RatingDto> ratings) {
        List<EventShortDto> shortsDtos = new ArrayList<>();
        for (Event event : events) {
            shortsDtos.add(toShortDto(event, views.get(event.getId()), ratings.get(event.getId())));
        }
        return shortsDtos;
    }

    public static List<EventFullDto> toFullDtos(List<Event> events, Map<Integer, Integer> views, Map<Integer, RatingDto> ratings) {
        List<EventFullDto> fullDtos = new ArrayList<>();
        for (Event event : events) {
            fullDtos.add(toFullDto(event, views.get(event.getId()), ratings.get(event.getId())));
        }
        return fullDtos;
    }

    private static State getState(StateAction stateAction, Event event) {
        switch (stateAction) {
            case SEND_TO_REVIEW:
                return State.PENDING;
            case CANCEL_REVIEW:
            case REJECT_EVENT:
                checkEventState(event);
                return State.CANCELED;
            case PUBLISH_EVENT:
                checkEventState(event);
                return State.PUBLISHED;
            default:
                return null;
        }
    }

    private static Integer sumConfirmedRequests(List<Request> requests) {
        int confirmedRequests = 0;
        for (Request request : requests) {
            if (Objects.equals(request.getStatus(), RequestStatus.CONFIRMED))
                confirmedRequests = confirmedRequests + 1;
        }
        return confirmedRequests;
    }

    private static void checkEventState(Event event) {
        if (Objects.equals(event.getState(), State.PUBLISHED) || Objects.equals(event.getState(), State.CANCELED)) {
            throw new ConflictException(INCORRECT_EVENT_STATE_MSG, INCORRECT_EVENT_STATE_REASON);
        }
    }

    private static <T> T firstOrDefault(T value, T defaultValue) {
        return Objects.nonNull(value) ? value : defaultValue;
    }
}
