package com.stackdarker.platform.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stackdarker.platform.auth.api.dto.ErrorItem;
import com.stackdarker.platform.auth.api.dto.ErrorResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<ErrorItem> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorItem(
                        "FIELD_INVALID",
                        fe.getDefaultMessage(),
                        fe.getField(),
                        null,
                        null
                ))
                .collect(Collectors.toList());

        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed", "AUTH_VALIDATION_FAILED", req, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "AUTH_INTERNAL_ERROR", req, null);
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            String code,
            HttpServletRequest req,
            List<ErrorItem> details
    ) {
        String requestId = (String) req.getAttribute("requestId");
        String path = req.getRequestURI();

        ErrorResponse body = new ErrorResponse(
                requestId,
                OffsetDateTime.now(),
                status.value(),
                status.name(),
                message,
                path,
                code,
                details
        );

        return ResponseEntity.status(status).body(body);
    }
}
