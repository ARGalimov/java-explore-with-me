package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.event.model.Event;

import java.util.Map;

@Data
@AllArgsConstructor
public class LikeDislike {
    private Map<Event, Integer> likes;
    private Map<Event, Integer> dislike;
}