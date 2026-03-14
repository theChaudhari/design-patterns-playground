package com.designpattern.utils;

public class Constants {

    // Auth Types
    public static final String API_KEY  = "API_KEY";
    public static final String JWT      = "JWT";
    public static final String OAUTH    = "OAUTH";

    // Status
    public static final String SUCCESS  = "SUCCESS";
    public static final String FAILURE  = "FAILURE";

    // Handler Names
    public static final String API_KEY_HANDLER  = "ApiKeyAuthHandler";
    public static final String JWT_HANDLER      = "JwtAuthHandler";
    public static final String OAUTH_HANDLER    = "OAuthHandler";
    public static final String DEFAULT_HANDLER  = "DefaultHandler";

    // Messages
    public static final String API_KEY_SUCCESS  = "Authenticated via API Key successfully.";
    public static final String JWT_SUCCESS      = "Authenticated via JWT Token successfully.";
    public static final String OAUTH_SUCCESS    = "Authenticated via OAuth successfully.";
    public static final String AUTH_FAILED      = "Authentication failed. No handler could process the request.";
    public static final String INVALID_TOKEN    = "Invalid or missing token.";
}
