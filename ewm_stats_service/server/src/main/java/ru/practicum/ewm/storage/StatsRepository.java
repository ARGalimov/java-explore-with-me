package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("select s from Stats s where s.uri in :uris and s.timestamp between :start and :end")
    List<Stats> findAllByUrisAndTimestampIsBetween(List<String> uris, LocalDateTime start, LocalDateTime end);

    List<Stats> findAllByTimestampIsBetween(LocalDateTime start, LocalDateTime end);
}