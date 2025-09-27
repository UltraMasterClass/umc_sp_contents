package com.umc.sp.contents.exception;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public final ResponseEntity<Object> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                       .getFieldErrors()
                       .stream()
                       .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (s, s2) -> s2));
        //TODO: return a ErrorResponse that supports multiple errors
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.error("Internal error: ", ex);
        return new ResponseEntity<>(new ErrorResponse("Please try again"), INTERNAL_SERVER_ERROR);
    }
}
