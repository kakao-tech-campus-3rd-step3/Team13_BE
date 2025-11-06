package com.b4f2.pting.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("[IllegalArgumentException] {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, convertErrorToString(e));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("[EntityNotFoundException] {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, convertErrorToString(e));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException e) {
        log.error("[IllegalStateException] {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, convertErrorToString(e));
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException e) {
        log.error("[RuntimeException] {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, convertErrorToString(e));
    }

    @ExceptionHandler(UncheckedIOException.class)
    public ProblemDetail handleUncheckedIOException(UncheckedIOException e) {
        log.error("[UncheckedIOException] {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, convertErrorToString(e));
    }

    @ExceptionHandler(FirebaseMessagingException.class)
    public ProblemDetail handleFirebaseMessagingException(FirebaseMessagingException e) {
        log.error("[FirebaseMessagingException] {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, convertErrorToString(e));
    }

    private String convertErrorToString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
