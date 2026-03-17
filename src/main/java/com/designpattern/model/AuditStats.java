package com.designpattern.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class AuditStats {

    private int totalLogs;
    private Map<String, Long> logsByAction;
    private Map<String, Long> logsByUser;

}