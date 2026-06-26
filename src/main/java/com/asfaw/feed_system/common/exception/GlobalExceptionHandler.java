package com.asfaw.feed_system.common.exception;

import com.asfaw.feed_system.common.rate.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RateLimitExceededException.class)
	public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException ex) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("timestamp", Instant.now());
		errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
		errorResponse.put("error", "Too Many Requests");
		errorResponse.put("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("timestamp", Instant.now());
		errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
		errorResponse.put("error", "Bad Request");
		errorResponse.put("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
}
