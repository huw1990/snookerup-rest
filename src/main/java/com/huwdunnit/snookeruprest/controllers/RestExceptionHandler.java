package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.exceptions.RoutineNotFoundException;
import com.huwdunnit.snookeruprest.exceptions.ScoreNotFoundException;
import com.huwdunnit.snookeruprest.exceptions.UserNotFoundException;
import com.huwdunnit.snookeruprest.model.errors.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * A Spring Controller Advice for handling certain types of exceptions thrown by REST Controllers.
 *
 * @author Huwdunnit
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex, WebRequest request) {
        log.error("handleDuplicateKeyException ex={}, request={}", ex, request);

        ErrorResponse errorResponse = ErrorResponse.createDuplicateValueErrorResponse();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.error("handleUserNotFound ex={}, request={}", ex, request);

        ErrorResponse errorResponse = ErrorResponse.createUserNotFoundErrorResponse();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({RoutineNotFoundException.class})
    public ResponseEntity<Object> handleRoutineNotFound(RoutineNotFoundException ex, WebRequest request) {
        log.error("handleRoutineNotFound ex={}, request={}", ex, request);

        ErrorResponse errorResponse = ErrorResponse.createRoutineNotFoundErrorResponse();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({ScoreNotFoundException.class})
    public ResponseEntity<Object> handleScoreNotFound(ScoreNotFoundException ex, WebRequest request) {
        log.error("handleScoreNotFound ex={}, request={}", ex, request);

        ErrorResponse errorResponse = ErrorResponse.createScoreNotFoundErrorResponse();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
