package com.designpattern.utils;


import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    // Rate Limit Config
    public final int MAX_REQUESTS_PER_WINDOW = 5;
    public final long WINDOW_DURATION_MS = 60_000; // 1 minute in ms

    // Response Headers
    public final String HEADER_USER_ID = "X-User-Id";
    public final String HEADER_RATE_LIMIT = "X-RateLimit-Limit";
    public final String HEADER_RATE_REMAINING = "X-RateLimit-Remaining";
    public final String HEADER_RATE_RESET = "X-RateLimit-Reset-After";

    // Messages
    public final String RATE_LIMIT_EXCEEDED = "Rate limit exceeded. Max %d requests per minute allowed.";
    public final String MISSING_USER_ID = "Missing required header: X-User-Id";
    public final String PRODUCT_NOT_FOUND = "Product not found with id: ";

}