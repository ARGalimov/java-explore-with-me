package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.storage.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public List<ViewStats> getStats(String startStr, String endStr, List<String> uris, Boolean unique) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startStr, format);
        LocalDateTime end = LocalDateTime.parse(endStr, format);
        if (Objects.nonNull(uris)) {
            if (unique) {
                return getUniqueStatsByUri(start, end, uris);
            } else {
                return getAllStatsByUri(start, end, uris);
            }
        } else {
            if (unique) {
                return getUniqueStats(start, end);
            } else {
                return getAllStats(start, end);
            }
        }
    }

    @Override
    public EndpointHit createStats(EndpointHit endpointHit) {
        Stats stats = StatsMapper.toEntity(endpointHit);
        return StatsMapper.toRequestDto(statsRepository.save(stats));
    }

    private List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end) {
        List<Stats> statsList = statsRepository.findAllByTimestampIsBetween(start, end);
        statsList = getUniqueStats(statsList);
        return getResponseStatsDtos(statsList);
    }

    private List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end) {
        List<Stats> statsList = statsRepository.findAllByTimestampIsBetween(start, end);
        return getResponseStatsDtos(statsList);
    }

    private List<ViewStats> getUniqueStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stats> statsList = statsRepository.findAllByUriAndTimestampIsBetween(uris, start, end);
        statsList = getUniqueStats(statsList);
        return getResponseStatsDtos(statsList);
    }

    private List<ViewStats> getAllStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stats> statsList = statsRepository.findAllByUriAndTimestampIsBetween(uris, start, end);
        return getResponseStatsDtos(statsList);
    }

    private List<Stats> getUniqueStats(List<Stats> statsList) {
        Set<String> uniqueIps = new HashSet<>();
        List<Stats> uniqueStats = new ArrayList<>();
        for (Stats stats : statsList) {
            String ip = stats.getIp();
            if (uniqueIps.add(ip)) {
                uniqueStats.add(stats);
            }
        }
        return uniqueStats;
    }

    private List<ViewStats> getResponseStatsDtos(List<Stats> statsList) {
        Set<String> uniqueUris = new HashSet<>();
        List<ViewStats> response = new ArrayList<>();
        for (Stats stats : statsList) {
            String uri = stats.getUri();
            if (uniqueUris.add(uri)) {
                response.addAll(getStatByApp(statsList, uri));
            }
        }
        return response.stream()
                .sorted(Comparator.comparingInt(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }

    private List<ViewStats> getStatByApp(List<Stats> statsList, String uri) {
        Map<String, Integer> counter = countByApp(statsList, uri);
        return counter.entrySet().stream()
                .map(entry -> StatsMapper.toResponseDto(entry.getKey(), uri, entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<String, Integer> countByApp(List<Stats> statsList, String uri) {
        return statsList.stream()
                .filter(stats -> Objects.equals(stats.getUri(), uri))
                .collect(Collectors.groupingBy(Stats::getApp, Collectors.summingInt(x -> 1)));
    }
}