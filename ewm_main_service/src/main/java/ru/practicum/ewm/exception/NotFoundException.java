package ru.practicum.ewm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends ApiError {
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public NotFoundException(String message, String reason) {
        super(message, reason);
    }
}