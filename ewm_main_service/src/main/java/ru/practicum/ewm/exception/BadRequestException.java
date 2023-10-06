package ru.practicum.ewm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends ApiError {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public BadRequestException(String message, String reason) {
        super(message, reason);
    }
}