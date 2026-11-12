
package com.freightfox.github_repository_searcher.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleGithubApiException(
            RestClientResponseException exception) {

        int statusCode = exception.getStatusCode().value();

        String rateLimitRemaining = exception.getResponseHeaders() != null
                ? exception.getResponseHeaders()
                .getFirst("X-RateLimit-Remaining")
                : null;

        String responseBody = exception.getResponseBodyAsString();

        boolean rateLimitExceeded =
                statusCode == 429 ||
                        (statusCode == 403 && (
                                "0".equals(rateLimitRemaining) ||
                                        (responseBody != null &&
                                                responseBody.toLowerCase().contains("rate limit"))
                        ));

        if (rateLimitExceeded) {
            return buildResponse(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "GitHub API rate limit exceeded. Please try again later."
            );
        }

        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "GitHub API request failed. Please try again later."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception exception) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", Instant.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }
}
