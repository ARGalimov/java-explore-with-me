package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingPK implements Serializable {
    private Integer event;
    private Integer rater;
}