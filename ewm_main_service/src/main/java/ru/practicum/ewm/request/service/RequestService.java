package ru.practicum.ewm.request.service;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Integer userId, Integer eventId);

    List<RequestDto> getRequestsByUserId(Integer userId);

    RequestDto cancelRequest(Integer userId, Integer requestId);

    List<RequestDto> getRequestsByEventId(Integer eventId);

    RequestsByStatusDto updateRequestsStatusByEvent(RequestStatusUpdateDto statusUpdateDto, Event event);
}