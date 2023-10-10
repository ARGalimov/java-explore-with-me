package ru.practicum.ewm.rating.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.dto.InitiatorRatingDto;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.mapper.RatingMapper;
import ru.practicum.ewm.rating.model.LikeDislike;
import ru.practicum.ewm.rating.model.Rate;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.model.RatingPK;
import ru.practicum.ewm.rating.storage.RatingRepository;
import ru.practicum.ewm.user.model.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public RatingDto addRate(User rater, Event event, Rate rate) {
        Rating rating = ratingRepository.save(RatingMapper.toNewEntity(rater, event, rate));
        log.info("Saved rating {}", rating);
        return getRatingByEvent(event);
    }

    @Override
    public RatingDto deleteRate(User rater, Event event) {
        ratingRepository.deleteById(new RatingPK(event.getId(), rater.getId()));
        log.info("Deleted rating for event={}", event);
        return getRatingByEvent(event);
    }

    @Override
    public RatingDto getRatingByEvent(Event event) {
        log.info("Getting rating for event={}", event);
        return RatingMapper.toDto(event.getId(), getLikes(event), getDislikes(event));
    }

    @Override
    public Map<Integer, RatingDto> getRatingsByEvents(List<Event> events) {
        LikeDislike likeDislike = getLikeDislike(ratingRepository.findAllByEventIn(events));
        log.info("Getting rating for events={}", events);
        return RatingMapper.toDtoMap(events, likeDislike.getLikes(), likeDislike.getDislike());
    }

    @Override
    public List<RatingDto> getRatingsForEvents(Rate rate, PageRequest page) {
        List<RatingDto> ratingDtos = getRatingsForEvents();
        log.info("Getting summary rating of events {}", ratingDtos);

        Comparator<RatingDto> comparator = (rate == Rate.LIKE)
                ? Comparator.comparingInt(RatingDto::getLikes).reversed()
                : Comparator.comparingInt(RatingDto::getDislikes).reversed();

        return ratingDtos.stream()
                .sorted(comparator)
                .skip(page.getPageNumber())
                .limit(page.getPageSize())
                .collect(Collectors.toList());
    }

    @Override
    public List<InitiatorRatingDto> getRatingsForInitiators(Rate rate, PageRequest page) {
        List<Rating> ratings = ratingRepository.findAll();
        log.info("Getting ratings {}", ratings);
        LikeDislike likeDislike = getLikeDislike(ratings);
        log.info("Getting likeDislike {}", likeDislike);
        List<InitiatorRatingDto> initiatorRatingDtos = RatingMapper.toInitiatorDtos(likeDislike);
        log.info("Getting summary rating of initiators {}", initiatorRatingDtos);

        Comparator<InitiatorRatingDto> comparator = (rate == Rate.LIKE)
                ? Comparator.comparingInt(InitiatorRatingDto::getLikes).reversed()
                : Comparator.comparingInt(InitiatorRatingDto::getDislikes).reversed();

        return initiatorRatingDtos.stream()
                .sorted(comparator)
                .skip(page.getPageNumber())
                .limit(page.getPageSize())
                .collect(Collectors.toList());
    }

    private Integer getLikes(Event event) {
        List<Rating> ratings = ratingRepository.findAllByEventAndRate(event, Rate.LIKE);
        return ratings.size();
    }

    private Integer getDislikes(Event event) {
        List<Rating> ratings = ratingRepository.findAllByEventAndRate(event, Rate.DISLIKE);
        return ratings.size();
    }

    private List<RatingDto> getRatingsForEvents() {
        LikeDislike likeDislike = getLikeDislike(ratingRepository.findAll());
        return RatingMapper.toDtoList(likeDislike);
    }

    private LikeDislike getLikeDislike(List<Rating> ratings) {
        Map<Event, Integer> likes = new HashMap<>();
        Map<Event, Integer> dislikes = new HashMap<>();

        for (Rating rating : ratings) {
            Event event = rating.getEvent();
            Rate rate = rating.getRate();

            if (rate == Rate.LIKE) {
                likes.merge(event, 1, Integer::sum);
            } else {
                dislikes.merge(event, 1, Integer::sum);
            }
        }

        return new LikeDislike(likes, dislikes);
    }
}