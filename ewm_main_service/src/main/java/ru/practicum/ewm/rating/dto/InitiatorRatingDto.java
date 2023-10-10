package ru.practicum.ewm.rating.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Set;

@Data
@Builder
public class InitiatorRatingDto {
    private UserShortDto initiator;
    private Set<Integer> eventsId;
    private Integer likes;
    private Integer dislikes;
}