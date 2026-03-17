package com.designpattern.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuditEntry {

    private String id;
    private String userId;
    private String action;
    private String resource;
    private String details;
    private LocalDateTime timestamp;

}