package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.service.StatsService;

import java.util.List;

@RestController
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public List<ViewStats> get(@RequestParam(name = "start") String start,
                               @RequestParam(name = "end") String end,
                               @RequestParam(name = "uris", required = false) List<String> uris,
                               @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Getting stats from {} to {}, uris = {}, unique = {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit create(@RequestBody EndpointHit endpointHit) {
        log.info("Creating stats {}", endpointHit);
        return statsService.createStats(endpointHit);
    }
}