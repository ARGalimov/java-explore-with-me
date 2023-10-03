package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

import static ru.practicum.ewm.constant.Constant.FORMAT;

@RestControllerAdvice("ru.practicum.ewm")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(final BadRequestException e) {
        return new ResponseEntity<>((Map.of(
                "error", e.getMessage(),
                "reason", e.getReason(),
                "timestamp", e.getTimeStamp().format(FORMAT),
                "status", e.getStatus())),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleConflictException(final ConflictException e) {
        return new ResponseEntity<>((Map.of(
                "error", e.getMessage(),
                "reason", e.getReason(),
                "timestamp", e.getTimeStamp().format(FORMAT),
                "status", e.getStatus())),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>((Map.of(
                "error", e.getMessage(),
                "reason", e.getReason(),
                "timestamp", e.getTimeStamp().format(FORMAT),
                "status", e.getStatus())),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException e) {
        return new ResponseEntity<>((Map.of(
                "status", "400",
                "reason", e.getMessage())),
                HttpStatus.BAD_REQUEST);
    }
}