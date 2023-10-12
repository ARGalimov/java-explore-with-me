package ru.practicum.ewm.rating.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.dto.InitiatorRatingDto;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.model.Rate;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Map;

public interface RatingService {
    RatingDto addRate(User rater, Event event, Rate rate);

    RatingDto deleteRate(User rater, Event event);

    RatingDto getRatingByEvent(Event event);

    Map<Integer, RatingDto> getRatingsByEvents(List<Event> events);

    List<RatingDto> getRatingsForEvents(Rate rate, PageRequest page);

    List<InitiatorRatingDto> getRatingsForInitiators(Rate rate, PageRequest of);
}
