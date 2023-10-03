package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Optional<Event> findById(Integer eventId);

    EventFullDto createEvent(Integer userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Integer userId, PageRequest page);

    Map<Integer, Integer> getStats(List<Event> events);

    EventFullDto getEventsById(Integer userId, Integer eventId);

    EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventDto updateEventUserDto);

    List<RequestDto> getRequestsByEventId(Integer userId, Integer eventId);

    RequestsByStatusDto updateEventRequestsStatus(Integer eventId, Integer userId, RequestStatusUpdateDto statusUpdateDto);

    List<EventFullDto> getEventsByAdminFilters(List<Integer> users, List<String> states, List<Integer> categories,
                                               String rangeStart, String rangeEnd, PageRequest page);

    List<EventShortDto> getEventsByPublicFilters(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                                 String rangeEnd, Boolean onlyAvailable, String sort, PageRequest of,
                                                 HttpServletRequest request);

    EventFullDto getEventById(Integer eventId, HttpServletRequest request);

    List<Event> findByIds(List<Integer> eventsId);
}