package ru.practicum.ewm.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    private static final String INCORRECT_REQUEST_STATUS_MSG = "The request must have the status pending";
    private static final String INCORRECT_REQUEST_STATUS_REASON = "Incorrect id of request for modification";
    private static final String INCORRECT_REQUEST_MSG = "The request can't be created";
    private static final String INCORRECT_REQUEST_UPDATE_MSG = "The request can't be modified";
    private static final String INCORRECT_REQUEST_REASON = "Such request already exist";
    private static final String INCORRECT_REQUEST_EVENT_LIMIT_REASON = "Event already reached the limit";
    private static final String INCORRECT_REQUESTER_REASON = "Requester can't be initiator of event";
    private static final String INCORRECT_REQUEST_EVENT_STATE_REASON = "Event is not published";
    private static final String NOT_FOUND_REQUEST_MSG = "Request not found";
    private static final String NOT_FOUND_EVENT_MSG = "Event not found";
    private static final String NOT_FOUND_USER_MSG = "User not found";
    private static final String NOT_FOUND_ID_REASON = "Incorrect Id";

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, @Lazy EventService eventService,
                              UserService userService) {
        this.requestRepository = requestRepository;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public RequestDto createRequest(Integer userId, Integer eventId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        log.info("Creating request for user {}", user);
        if (eventId == null) {
            throw new BadRequestException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON);
        }
        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        log.info("Creating request for event {}", event);
        checkRequest(user, event);
        Request request = requestRepository.save(RequestMapper.toNewEntity(user, event));
        log.info("Created request {} and reverting to controller as dto", request);
        return RequestMapper.toDto(request);
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Integer userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Getting requests {} and reverting to controller as dto", requests);
        return RequestMapper.toRequestsDto(requests);
    }

    @Override
    public RequestDto cancelRequest(Integer userId, Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_REQUEST_MSG, NOT_FOUND_ID_REASON));
        request.setStatus(RequestStatus.CANCELED);
        log.info("Cancel request {}", request);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Integer eventId) {
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.info("Getting requests {}", requests);
        return RequestMapper.toRequestsDto(requests);
    }

    @Override
    @Transactional
    public RequestsByStatusDto updateRequestsStatusByEvent(RequestStatusUpdateDto statusUpdateDto, Event event) {
        int space = getAvailableSpace(event);
        RequestStatus requestStatus = RequestStatus.valueOf(statusUpdateDto.getStatus().toString());
        List<Integer> requestIds = statusUpdateDto.getRequestIds();
        List<Request> requests = requestRepository.findAllById(requestIds);
        requests = requestRepository.saveAll(modifyStatusRequests(requests, space, requestStatus));
        log.info("Changed status for requests {}", requests);
        return RequestMapper.toRequestsByStatusDto(requests);
    }

    private List<Request> modifyStatusRequests(List<Request> requests, int space, RequestStatus requestStatus) {
        for (Request request : requests) {
            if (!Objects.equals(request.getStatus(), RequestStatus.PENDING)) {
                throw new ConflictException(INCORRECT_REQUEST_STATUS_MSG, INCORRECT_REQUEST_STATUS_REASON);
            }
            if (space > 0) {
                request.setStatus(requestStatus);
                if (Objects.equals(requestStatus, RequestStatus.CONFIRMED)) {
                    space = space - 1;
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
            }
        }
        return requests;
    }

    private Integer getAvailableSpace(Event event) {
        Integer limit = event.getParticipantLimit();
        if (limit == 0) {
            return 1;
        }
        List<Request> confirmedRequest = requestRepository.getRequestByStatusIs(RequestStatus.CONFIRMED, event);
        int availableSpace = limit - confirmedRequest.size();
        if (availableSpace == 0) {
            throw new ConflictException(INCORRECT_REQUEST_UPDATE_MSG, INCORRECT_REQUEST_EVENT_LIMIT_REASON);
        }
        return availableSpace;
    }

    private void checkRequest(User user, Event event) {
        Optional<Request> request = requestRepository.findByRequesterIdAndEventId(user.getId(), event.getId());
        if (request.isPresent()) {
            throw new ConflictException(INCORRECT_REQUEST_MSG, INCORRECT_REQUEST_REASON);
        }
        if (Objects.equals(user.getId(), event.getInitiator().getId())) {
            throw new ConflictException(INCORRECT_REQUEST_MSG, INCORRECT_REQUESTER_REASON);
        }
        if (!Objects.equals(event.getState(), State.PUBLISHED)) {
            throw new ConflictException(INCORRECT_REQUEST_MSG, INCORRECT_REQUEST_EVENT_STATE_REASON);
        }
        if (getAvailableSpace(event) < 1) {
            throw new ConflictException(INCORRECT_REQUEST_MSG, INCORRECT_REQUEST_EVENT_LIMIT_REASON);
        }
    }
}