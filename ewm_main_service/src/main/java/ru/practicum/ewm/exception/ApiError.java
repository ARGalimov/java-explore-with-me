package ru.practicum.ewm.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiError extends RuntimeException {
    private final String reason;
    private final LocalDateTime timeStamp;

    public ApiError(String message, String reason) {
        super(message);
        this.reason = reason;
        this.timeStamp = LocalDateTime.now();
    }
}