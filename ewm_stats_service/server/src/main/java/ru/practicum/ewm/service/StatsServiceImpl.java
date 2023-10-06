package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.storage.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
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

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Некорректные даты в запросе");
        }

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
        log.info("Find non-uniq statsList {}", statsList);
        return getResponseStatsDtos(statsList);
    }

    private List<ViewStats> getUniqueStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stats> statsList = statsRepository.findAllByUrisAndTimestampIsBetween(uris, start, end);
        statsList = getUniqueStats(statsList);
        return getResponseStatsDtos(statsList);
    }

    private List<ViewStats> getAllStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stats> statsList = statsRepository.findAllByUrisAndTimestampIsBetween(uris, start, end);
        return getResponseStatsDtos(statsList);
    }

    private List<Stats> getUniqueStats(List<Stats> statsList) {
        Set<String> uniqueIps = new HashSet<>();
        List<Stats> uniqueStats = new ArrayList<>();
        log.info("Reading uniq statsList {}", statsList);
        for (Stats stats : statsList) {
            String ip = stats.getIp();
            if (uniqueIps.add(ip)) {
                uniqueStats.add(stats);
            }
        }
        return uniqueStats;
    }

    private List<ViewStats> getResponseStatsDtos(List<Stats> statsList) {
        Set<String> uris = new HashSet<>();
        List<ViewStats> response = new ArrayList<>();
        log.info("Getting statsList {}", statsList);
        for (Stats stats : statsList) {
            String uri = stats.getUri();
            if (!uris.contains(uri)) {
                response.addAll(getStatByApp(statsList, uri));
                uris.add(uri);
            }
        }
        return response.stream()
                .sorted(Comparator.comparingInt(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }

    private List<ViewStats> getStatByApp(List<Stats> statsList, String uri) {
        log.info("Getting StatByApp statsList {}", statsList);
        List<ViewStats> statsByApp = new ArrayList<>();
        Map<String, Integer> counter = countByApp(statsList, uri);
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            statsByApp.add(StatsMapper.toResponseDto(entry.getKey(), uri, entry.getValue()));
        }
        return statsByApp;
    }

    private Map<String, Integer> countByApp(List<Stats> statsList, String uri) {
        log.info("Counting StatByApp statsList {}", statsList);
        Map<String, Integer> counter = new HashMap<>();
        for (Stats stats : statsList) {
            if (Objects.equals(stats.getUri(), uri)) {
                String app = stats.getApp();
                if (counter.containsKey(app)) {
                    int count = counter.get(app);
                    counter.put(app, count + 1);
                } else {
                    counter.put(app, 1);
                }
            }
        }
        return counter;
    }
}