package com.stackdarker.platform.auth.exception;

import com.stackdarker.platform.auth.api.error.ErrorItem;
import com.stackdarker.platform.auth.api.error.ErrorResponse;
import com.stackdarker.platform.auth.service.exceptions.EmailAlreadyExistsException;
import com.stackdarker.platform.auth.service.exceptions.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.stackdarker.platform.auth.service.exceptions.InvalidRefreshTokenException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "AUTH_EMAIL_EXISTS",
                "Email already registered.",
                request,
                List.of(new ErrorItem("EMAIL_EXISTS", ex.getMessage()))
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "AUTH_INVALID_CREDENTIALS",
                "Invalid email or password.",
                request,
                List.of(new ErrorItem("INVALID_CREDENTIALS", "Invalid email or password."))
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorItem> items = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            items.add(new ErrorItem(
                    "VALIDATION_ERROR",
                    fe.getDefaultMessage(),
                    fe.getField(),
                    Map.of("rejectedValue", safeRejectedValue(fe.getRejectedValue()))
            ));
        }

        return build(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "VALIDATION_FAILED",
                "One or more fields are invalid.",
                request,
                items
        );
    }

        @ExceptionHandler(InvalidRefreshTokenException.class)
        public ResponseEntity<ErrorResponse> handleInvalidRefresh(
                InvalidRefreshTokenException ex,
                HttpServletRequest request
        ) {
                return build(
                        HttpStatus.UNAUTHORIZED,
                        "AUTH_INVALID_REFRESH",
                        "Invalid or expired refresh token.",
                        request,
                        List.of(new ErrorItem("INVALID_REFRESH", "Invalid or expired refresh token."))
                );
        }
    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred.",
                request,
                List.of(new ErrorItem("INTERNAL_ERROR", "Unexpected error."))
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<ErrorItem> items
    ) {
        ErrorResponse body = new ErrorResponse();
        body.setStatus(status.value());
        body.setError(status.getReasonPhrase());
        body.setCode(code);
        body.setMessage(message);
        body.setPath(request.getRequestURI());
        body.setRequestId(getRequestId(request));
        body.setErrors(items);

        return ResponseEntity.status(status).body(body);
    }

    private String getRequestId(HttpServletRequest request) {
        String rid = request.getHeader("X-Request-Id");
        if (rid != null && !rid.isBlank()) return rid;
        Object attr = request.getAttribute("requestId");
        return attr == null ? null : attr.toString();        
    }

    private Object safeRejectedValue(Object rejected) {
        // Avoid leaking 
        if (rejected == null) return null;
        String s = rejected.toString();
        if (s.length() > 200) return s.substring(0, 200) + "...";
        return s;
    }

    
}
