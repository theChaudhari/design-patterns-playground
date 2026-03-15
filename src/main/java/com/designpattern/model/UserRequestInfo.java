package com.designpattern.model;

import lombok.Data;

@Data
public class UserRequestInfo {

    private String userId;
    private int requestCount;
    private long windowStartTime;  // epoch ms — when current window started

    public UserRequestInfo(String userId) {
        this.userId = userId;
        this.requestCount = 0;
        this.windowStartTime = System.currentTimeMillis();
    }

    public boolean isWindowExpired(long windowDurationMs) {
        return System.currentTimeMillis() - windowStartTime >= windowDurationMs;
    }

    public void resetWindow() {
        this.requestCount = 0;
        this.windowStartTime = System.currentTimeMillis();
    }

    public void incrementCount() {
        this.requestCount++;
    }

    public long getRetryAfterMs(long windowDurationMs) {
        return windowDurationMs - (System.currentTimeMillis() - windowStartTime);
    }

}