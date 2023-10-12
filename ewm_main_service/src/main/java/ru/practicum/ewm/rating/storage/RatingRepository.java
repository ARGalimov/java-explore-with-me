package ru.practicum.ewm.rating.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.model.Rate;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.model.RatingPK;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingPK> {
    List<Rating> findAllByEventAndRate(Event event, Rate like);

    List<Rating> findAllByEventIn(List<Event> events);

}