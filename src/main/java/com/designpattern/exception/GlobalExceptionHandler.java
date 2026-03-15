package com.designpattern.exception;

import com.designpattern.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException ex) {
        long retryAfterSec = ex.getRetryAfterMs() / 1000;
        log.warn("Rate limit exceeded - retryAfter: {}s", retryAfterSec);

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).header(Constants.HEADER_RATE_RESET, retryAfterSec + "s").body(Map.of("status", 429, "error", "Too Many Requests", "message", ex.getMessage(), "retryAfter", retryAfterSec + "s"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request - {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "error", "Bad Request", "message", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Unexpected error - {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", 500, "error", "Internal Server Error", "message", ex.getMessage()));
    }

}