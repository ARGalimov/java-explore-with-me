package ru.practicum.ewm.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequesterId(Integer userId);

    List<Request> findAllByEventId(Integer eventId);

    @Query("select r from Request r where r.status = :status and r.event = :event")
    List<Request> getRequestByStatusIs(RequestStatus status, Event event);

    Optional<Request> findByRequesterIdAndEventId(Integer requesterId, Integer eventId);
}