package ru.practicum.ewm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConflictException extends ApiError {
    private final HttpStatus status = HttpStatus.CONFLICT;

    public ConflictException(String message, String reason) {
        super(message, reason);
    }
}