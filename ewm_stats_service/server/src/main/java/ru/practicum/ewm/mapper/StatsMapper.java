package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stats toEntity(EndpointHit endpointHit) {
        return Stats.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(LocalDateTime.parse(endpointHit.getTimestamp(), format))
                .build();
    }

    public static ViewStats toResponseDto(String app, String uri, Integer hits) {
        return ViewStats.builder()
                .app(app)
                .uri(uri)
                .hits(hits)
                .build();
    }

    public static EndpointHit toRequestDto(Stats stats) {
        return EndpointHit.builder()
                .id(stats.getId())
                .app(stats.getApp())
                .ip(stats.getIp())
                .uri(stats.getUri())
                .timestamp(stats.getTimestamp().format(format))
                .build();
    }
}