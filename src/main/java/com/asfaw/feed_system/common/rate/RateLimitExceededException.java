package com.asfaw.feed_system.common.rate;

public class RateLimitExceededException extends RuntimeException {

	public RateLimitExceededException(String message) {
		super(message);
	}

	public RateLimitExceededException(String message, Throwable cause) {
		super(message, cause);
	}
}
