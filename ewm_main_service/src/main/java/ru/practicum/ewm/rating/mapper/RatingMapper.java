package ru.practicum.ewm.rating.mapper;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.dto.InitiatorRatingDto;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.model.LikeDislike;
import ru.practicum.ewm.rating.model.Rate;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RatingMapper {
    public static Rating toNewEntity(User rater, Event event, Rate rate) {
        return Rating.builder()
                .rater(rater)
                .event(event)
                .rate(rate)
                .build();
    }

    public static RatingDto toDto(Integer id, Integer likes, Integer dislikes) {
        return RatingDto.builder()
                .eventId(id)
                .likes(likes)
                .dislikes(dislikes)
                .build();
    }

    public static Map<Integer, RatingDto> toDtoMap(List<Event> events, Map<Event, Integer> likes,
                                                   Map<Event, Integer> dislikes) {
        return events.stream()
                .collect(Collectors.toMap(Event::getId, event -> toDto(event.getId(),
                        likes.get(event), dislikes.get(event))));
    }

    public static List<RatingDto> toDtoList(LikeDislike likeDislike) {
        Set<Event> events = getEvents(likeDislike);
        return events.stream()
                .map(event -> toDto(event.getId(), likeDislike.getLikes().get(event), likeDislike.getDislike().get(event)))
                .collect(Collectors.toList());
    }

    public static List<InitiatorRatingDto> toInitiatorDtos(LikeDislike likeDislike) {
        Set<Event> events = getEvents(likeDislike);
        Map<User, InitiatorRatingDto> dtoMap = new HashMap<>();
        for (Event event : events) {
            User initiator = event.getInitiator();
            if (dtoMap.containsKey(initiator)) {
                InitiatorRatingDto dto = dtoMap.get(initiator);
                dto.getEventsId().add(event.getId());
                dto.setLikes(dto.getLikes() + likeDislike.getLikes().get(event));
                dto.setDislikes(dto.getDislikes() + likeDislike.getDislike().get(event));
            } else {
                dtoMap.put(initiator, toInitiatorDto(likeDislike, event));
            }
        }
        return new ArrayList<>(dtoMap.values());
    }

    private static InitiatorRatingDto toInitiatorDto(LikeDislike likeDislike, Event event) {
        return InitiatorRatingDto.builder()
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .eventsId(new HashSet<>(Set.of(event.getId())))
                .likes(likeDislike.getLikes().get(event))
                .dislikes(likeDislike.getDislike().get(event))
                .build();
    }

    private static Set<Event> getEvents(LikeDislike likeDislike) {
        return Stream.concat(likeDislike.getLikes().keySet().stream(), likeDislike.getDislike().keySet().stream())
                .collect(Collectors.toSet());
    }
}