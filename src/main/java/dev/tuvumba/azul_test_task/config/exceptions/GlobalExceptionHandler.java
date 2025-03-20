package dev.tuvumba.azul_test_task.config.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *  Handles exceptions.
 *  For now, I am directly exposing the errors with their messages.
 *  If given more time, we can greatly expand this to a more robust and feature-rich exception handling system.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex) {
        ApiErrorResponse ApiErrorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getClass().toString(), ex);
        return new ResponseEntity<>(ApiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiErrorResponse ApiErrorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND, ex.getClass().toString(), ex);
        return new ResponseEntity<>(ApiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        ApiErrorResponse ApiErrorResponse = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex);
        return new ResponseEntity<>(ApiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
