package ru.practicum.ewm.rating.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingDto {
    private Integer eventId;
    private Integer likes;
    private Integer dislikes;
}