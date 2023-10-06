package ru.practicum.ewm.location.service;

import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.model.Location;

public interface LocationService {
    Location saveLocation(LocationDto location);
}