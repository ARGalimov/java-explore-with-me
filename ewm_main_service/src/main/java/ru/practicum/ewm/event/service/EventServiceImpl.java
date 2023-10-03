package ru.practicum.ewm.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.service.LocationService;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.constant.Constant.*;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final StatsClient statsClient;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, CategoryService categoryService,
                            UserService userService, LocationService locationService, RequestService requestService,
                            StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.categoryService = categoryService;
        this.userService = userService;
        this.locationService = locationService;
        this.requestService = requestService;
        this.statsClient = statsClient;
    }

    @Override
    public Optional<Event> findById(Integer eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    public EventFullDto createEvent(Integer userId, NewEventDto newEventDto) {
        Category category = categoryService.getCategoryEntity(newEventDto.getCategory());
        User initiator = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        Location location = locationService.saveLocation(newEventDto.getLocation());
        Event event = EventMapper.toNewEntity(newEventDto, category, initiator, location);
        checkTime(event);
        if (event.getAnnotation() == null) {
            throw new BadRequestException(INCORRECT_EVENT_ANNOTATION, NOT_FOUND_ID_REASON);
        }
        if (event.getDescription() == null) {
            throw new BadRequestException(INCORRECT_EVENT_DECSRIPTION, NOT_FOUND_ID_REASON);
        }
        event = eventRepository.save(event);
        Integer views = 0;
        log.info("Created event {}", event);
        return EventMapper.toFullDto(event, views);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Integer userId, PageRequest page) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        Map<Integer, Integer> views = getStats(events);
        log.info("Getting events {}", events);
        return EventMapper.toShortDtos(events, views);
    }

    @Override
    public EventFullDto getEventsById(Integer userId, Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        Integer views = getStats(event.getId());
        log.info("Getting event {}", event);
        return EventMapper.toFullDto(event, views);
    }

    @Override
    public EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventDto updateEventUserDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        checkState(userId, event);
        Category category = Objects.nonNull(updateEventUserDto.getCategory())
                ? categoryService.getCategoryEntity(updateEventUserDto.getCategory()) : null;
        Location location = Objects.nonNull(updateEventUserDto.getLocation())
                ? locationService.saveLocation(updateEventUserDto.getLocation()) : null;
        event = EventMapper.toUpdatedEntity(event, updateEventUserDto, category, location);
        checkTime(event);
        Integer views = getStats(event.getId());
        log.info("Found views {}", views);
        event = eventRepository.save(event);
        log.info("Updated event {}", event);
        views = getStats(event.getId());
        log.info("Found views {}", views);
        return EventMapper.toFullDto(event, views);
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Integer userId, Integer eventId) {
        return requestService.getRequestsByEventId(eventId);
    }

    @Override
    public RequestsByStatusDto updateEventRequestsStatus(Integer eventId, Integer userId,
                                                         RequestStatusUpdateDto statusUpdateDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        return requestService.updateRequestsStatusByEvent(statusUpdateDto, event);
    }

    @Override
    public List<EventFullDto> getEventsByAdminFilters(List<Integer> users, List<String> statesStr, List<Integer> categories,
                                                      String rangeStart, String rangeEnd, PageRequest page) {
        LocalDateTime start = Objects.isNull(rangeStart) ? null : LocalDateTime.parse(rangeStart, FORMAT);
        LocalDateTime end = Objects.isNull(rangeEnd) ? null : LocalDateTime.parse(rangeEnd, FORMAT);
        List<Event> events = getEventsByFilters(null, null, users, statesStr, categories, start, end, page);
        Map<Integer, Integer> views = getStats(events);
        log.info("Getting events {}", events);
        return EventMapper.toFullDtos(events, views);
    }

    @Override
    public List<EventShortDto> getEventsByPublicFilters(String text, List<Integer> categories, Boolean paid,
                                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                        String sort, PageRequest page, HttpServletRequest request) {
        LocalDateTime start = Objects.isNull(rangeStart)
                ? LocalDateTime.now() : LocalDateTime.parse(rangeStart, FORMAT);
        LocalDateTime end = Objects.isNull(rangeEnd) ? null : LocalDateTime.parse(rangeEnd, FORMAT);
        if (start != null && end != null) {
            if (end.isBefore(start)) {
                log.info("Date is incorrect");
                throw new BadRequestException(NOT_FOUND_EVENT_MSG, INCORRECT_DATA_INPUT_MSG);
            }
        }
        List<Event> events = getEventsByFilters(text, paid, null, List.of(State.PUBLISHED.toString()), categories,
                start, end, page);
        Map<Integer, Integer> views = getStats(events);
        saveStats(request);
        log.info("Getting events {}", events);
        List<EventShortDto> shortDtos = EventMapper.toShortDtos(events, views);
        return sortDto(sort, shortDtos);
    }

    @Override
    public EventFullDto getEventById(Integer eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        Integer views = getStats(event.getId());
        log.info("Found views {}", views);
        saveStats(request);
        log.info("Getting event {}", event);
        return EventMapper.toFullDto(event, views);
    }

    @Override
    public List<Event> findByIds(List<Integer> eventsId) {
        return eventRepository.findAllById(eventsId);
    }

    @Override
    public Map<Integer, Integer> getStats(List<Event> events) {
        Map<Integer, Integer> views = new HashMap<>();
        for (Event event : events) {
            Integer id = event.getId();
            Integer view = getStats(id);
            views.put(id, view);
        }
        return views;
    }

    private Integer getStats(Integer id) {
        ResponseEntity<List<ViewStats>> response = statsClient.getStats(START, END, URI + id, UNIQUE);
        List<ViewStats> responseStatsDtos = response.getBody();
        assert responseStatsDtos != null;
        return responseStatsDtos.size() > 0 ? responseStatsDtos.get(0).getHits() : 0;
    }

    private void saveStats(HttpServletRequest request) {
        EndpointHit requestStatsDto = EndpointHit.builder()
                .app(APPLICATION_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(FORMAT))
                .build();
        statsClient.createStats(requestStatsDto);
    }

    private void checkState(Integer userId, Event event) {
        if (Objects.nonNull(userId)) {
            if (Objects.equals(event.getState(), State.PUBLISHED)) {
                throw new ConflictException(INCORRECT_STATE_MSG, INCORRECT_STATE_REASON);
            }
        }
    }

    private void checkTime(Event event) {
        if (event.getEventDate().minusHours(HOURS_BEFORE_EVENT).isBefore(event.getCreatedOn())) {
            throw new BadRequestException(INCORRECT_TIME_MSG, INCORRECT_TIME_REASON);
        }
        if (Objects.nonNull(event.getPublishedOn())) {
            if (event.getEventDate().minusHours(HOUR_BEFORE_EVENT).isBefore(event.getPublishedOn())) {
                throw new BadRequestException(INCORRECT_TIME_MSG, INCORRECT_TIME_REASON);
            }
        }
    }

    private List<EventShortDto> sortDto(String sort, List<EventShortDto> shortDtos) {
        if (Objects.isNull(sort)) {
            return shortDtos;
        } else if (Objects.equals(sort, "EVENT_DATE")) {
            return shortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        } else if (Objects.equals(sort, "VIEWS")) {
            return shortDtos.stream()
                    .sorted(Comparator.comparingInt(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<Event> getEventsByFilters(String text, Boolean paid, List<Integer> users, List<String> statesStr,
                                           List<Integer> categories, LocalDateTime start, LocalDateTime end,
                                           PageRequest page) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        Predicate criteria = builder.conjunction();
        if (Objects.nonNull(users)) {
            Predicate inUsers = event.get("initiator").in(users);
            criteria = builder.and(criteria, inUsers);
        }
        if (Objects.nonNull(statesStr)) {
            List<State> states = new ArrayList<>();
            for (String state : statesStr) {
                states.add(State.valueOf(state));
            }
            Predicate inStates = event.get("state").in(states);
            criteria = builder.and(criteria, inStates);
        }
        if (Objects.nonNull(categories)) {
            Predicate inCategory = event.get("category").in(categories);
            criteria = builder.and(criteria, inCategory);
        }
        if (Objects.nonNull(end)) {
            Predicate beforeEnd = builder.lessThanOrEqualTo(event.get("eventDate"), end);
            criteria = builder.and(criteria, beforeEnd);
        }
        if (Objects.nonNull(start)) {
            Predicate afterStart = builder.greaterThanOrEqualTo(event.get("eventDate"), start);
            criteria = builder.and(criteria, afterStart);
        }
        if (Objects.nonNull(text)) {
            Predicate annotation = builder.like(builder.lower(event.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate description = builder.like(builder.lower(event.get("description")),
                    "%" + text.toLowerCase() + "%");
            Predicate hasText = builder.or(annotation, description);
            criteria = builder.and(criteria, hasText);
        }
        if (Objects.nonNull(paid)) {
            Predicate isPaid = event.get("paid").in(paid);
            criteria = builder.and(criteria, isPaid);
        }
        query.select(event).where(criteria);
        return entityManager.createQuery(query).setFirstResult(page.getPageNumber()).setMaxResults(page.getPageSize())
                .getResultList();
    }
}