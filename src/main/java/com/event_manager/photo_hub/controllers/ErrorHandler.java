package com.event_manager.photo_hub.controllers;

import com.event_manager.photo_hub.exceptions.*;
import com.event_manager.photo_hub.models.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.ValidationException;
import java.net.URI;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      @NonNull Exception ex,
      @Nullable Object body,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    if (body == null || isDefaultProblemDetail(body)) {
      body = createBodyFromException(ex, status);
    }
    log.error("Exception handled: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  @ExceptionHandler({
    ValidationException.class,
    MessagingException.class,
    ExpiredRegistrationCodeException.class,
    InvalidRegistrationCodeException.class,
    IllegalArgumentException.class,
    BadRequestException.class
  })
  public ResponseEntity<Object> handleBadRequestExceptions(Exception ex, WebRequest request) {
    return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({InvalidJwtTokenException.class, AuthenticationException.class})
  public ResponseEntity<Object> handleUnauthorizedExceptions(Exception ex, WebRequest request) {
    return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
    return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<Object> handleRateLimitExceededException(
      RateLimitExceededException ex, WebRequest request) {
    return buildResponseEntity(ex, HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
    return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private boolean isDefaultProblemDetail(Object body) {
    return body instanceof ProblemDetail detail
        && detail.getType().equals(URI.create("about:blank"));
  }

  private ApiResponse<Object> createBodyFromException(Exception ex, HttpStatusCode status) {
    String message = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.";
    return new ApiResponse<>(HttpStatus.valueOf(status.value()), null, message);
  }

  private ResponseEntity<Object> buildResponseEntity(Exception ex, HttpStatus status) {
    ApiResponse<Object> apiResponse = createBodyFromException(ex, status);
    log.error("Exception handled: {} - {}.", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return new ResponseEntity<>(apiResponse, status);
  }
}
