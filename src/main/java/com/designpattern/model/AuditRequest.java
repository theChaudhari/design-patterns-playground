package com.designpattern.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuditRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "action is required. Supported: LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW")
    private String action;

    @NotBlank(message = "resource is required")
    private String resource;

    private String details;

}