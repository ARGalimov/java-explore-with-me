package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.util.List;

public interface StatsService {
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);

    EndpointHit createStats(EndpointHit endpointHit);
}