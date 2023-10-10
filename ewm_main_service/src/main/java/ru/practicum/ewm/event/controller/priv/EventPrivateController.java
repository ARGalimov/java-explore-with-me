package ru.practicum.ewm.event.controller.priv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.rating.model.Rate;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @Autowired
    public EventPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Integer userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Creating event={} from user={}", newEventDto, userId);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Integer userId,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting events for user={} from={} size={}", userId, from, size);
        return eventService.getEventsByUserId(userId, PageRequest.of(from, size));
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("Getting event={} fo user={}", eventId, userId);
        return eventService.getEventsById(userId, eventId);
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto updateEvent(@PathVariable Integer userId, @PathVariable Integer eventId,
                                    @Valid @RequestBody UpdateEventDto updateEventUserDto) {
        log.info("Updating event={} from user={} by following data = {}", eventId, userId, updateEventUserDto);
        return eventService.updateEvent(userId, eventId, updateEventUserDto);
    }

    @GetMapping(value = "/{eventId}/requests")
    public List<RequestDto> getRequestsByEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("Getting requests for event={} fo user={}", eventId, userId);
        return eventService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping(value = "/{eventId}/requests")
    public RequestsByStatusDto updateStatusRequests(@PathVariable Integer userId, @PathVariable Integer eventId,
                                                    @Valid @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        log.info("Updating status for requests={} for event={} from user={}", requestStatusUpdateDto, eventId, userId);
        return eventService.updateEventRequestsStatus(eventId, userId, requestStatusUpdateDto);
    }
    @PostMapping(value = "/{eventId}/rating")
    @ResponseStatus(HttpStatus.CREATED)
    public EventShortDto addRateToEvent(@PathVariable Integer userId, @PathVariable Integer eventId,
                                        @RequestParam Rate rate) {
        log.info("Adding rate={} event={} from user={}", rate, eventId, userId);
        return eventService.addRateToEvent(userId, eventId, rate);
    }

    @DeleteMapping(value = "/{eventId}/rating")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EventShortDto deleteRateFromEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        log.info("Deleting rate from event={} from user={}", eventId, userId);
        return eventService.deleteRateFromEvent(userId, eventId);
    }
}