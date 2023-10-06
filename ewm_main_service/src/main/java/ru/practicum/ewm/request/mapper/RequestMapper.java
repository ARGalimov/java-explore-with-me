package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestMapper {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Request toNewEntity(User user, Event event) {
        LocalDateTime current = LocalDateTime.now();
        return Request.builder()
                .status(event.getParticipantLimit() == 0 ? RequestStatus.CONFIRMED : (event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED))
                .created(current)
                .event(event)
                .requester(user)
                .build();
    }

    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .created(request.getCreated().format(FORMAT))
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public static List<RequestDto> toRequestsDto(List<Request> requests) {
        List<RequestDto> requestsDto = new ArrayList<>();
        for (Request request : requests) {
            requestsDto.add(toDto(request));
        }
        return requestsDto;
    }

    public static RequestsByStatusDto toRequestsByStatusDto(List<Request> requests) {
        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();
        for (Request request : requests) {
            if (Objects.equals(request.getStatus(), RequestStatus.CONFIRMED)) {
                confirmedRequests.add(toDto(request));
            } else {
                rejectedRequests.add(toDto(request));
            }
        }
        return new RequestsByStatusDto(confirmedRequests, rejectedRequests);
    }
}