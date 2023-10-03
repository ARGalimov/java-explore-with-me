package ru.practicum.ewm.event.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @Autowired
    public EventAdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> getEventsByFilters(@RequestParam(required = false) List<Integer> users,
                                                 @RequestParam(required = false) List<String> states,
                                                 @RequestParam(required = false) List<Integer> categories,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting events by following filters userIds={}, states={}, categories={}, rangeStart={}, " +
                "rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByAdminFilters(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from, size));
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto updateEvent(@PathVariable Integer eventId,
                                    @Valid @RequestBody UpdateEventDto updateEventAdminDto) {
        log.info("Updating event={}  by following data = {}", eventId, updateEventAdminDto);
        return eventService.updateEvent(null, eventId, updateEventAdminDto);
    }
}