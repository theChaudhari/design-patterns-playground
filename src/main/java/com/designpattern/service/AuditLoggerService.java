package com.designpattern.service;

import com.designpattern.model.AuditEntry;
import com.designpattern.model.AuditRequest;
import com.designpattern.model.AuditStats;
import com.designpattern.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * AuditLoggerService — Double-Checked Locking Singleton
 * Single instance holds all audit logs for the entire application.
 * CopyOnWriteArrayList ensures thread-safe reads and writes.
 */
@Slf4j
public class AuditLoggerService {


    private static final Set<String> VALID_ACTIONS = Set.of(Constants.ACTION_LOGIN, Constants.ACTION_LOGOUT, Constants.ACTION_CREATE, Constants.ACTION_UPDATE, Constants.ACTION_DELETE, Constants.ACTION_VIEW);

    // volatile — ensures visibility across threads
    private static volatile AuditLoggerService instance;

    // CopyOnWriteArrayList — thread-safe list for audit entries
    private final List<AuditEntry> auditLogs = new CopyOnWriteArrayList<>();

    private AuditLoggerService() {
        log.info("AuditLoggerService instance created - hash: {}", Integer.toHexString(System.identityHashCode(this)));
    }

    // Double-Checked Locking
    public static AuditLoggerService getInstance() {
        if (instance == null) {
            synchronized (AuditLoggerService.class) {
                if (instance == null) {
                    instance = new AuditLoggerService();
                }
            }
        }
        return instance;
    }

    public AuditEntry log(AuditRequest request) {
        validateAction(request.getAction());

        AuditEntry entry = new AuditEntry(UUID.randomUUID().toString(), request.getUserId(), request.getAction().toUpperCase(), request.getResource(), request.getDetails(), LocalDateTime.now());

        auditLogs.add(entry);
        log.info("Audit log recorded - userId: {}, action: {}, resource: {}", entry.getUserId(), entry.getAction(), entry.getResource());

        return entry;
    }

    public List<AuditEntry> getAllLogs() {
        log.info("Fetching all audit logs - total: {}", auditLogs.size());
        return new ArrayList<>(auditLogs);
    }

    public List<AuditEntry> getLogsByUser(String userId) {
        log.info("Fetching audit logs for userId: {}", userId);
        return auditLogs.stream().filter(e -> e.getUserId().equals(userId)).collect(Collectors.toList());
    }

    public AuditStats getStats() {
        Map<String, Long> byAction = auditLogs.stream().collect(Collectors.groupingBy(AuditEntry::getAction, Collectors.counting()));

        Map<String, Long> byUser = auditLogs.stream().collect(Collectors.groupingBy(AuditEntry::getUserId, Collectors.counting()));

        log.info("Fetching audit stats - total: {}", auditLogs.size());
        return new AuditStats(auditLogs.size(), byAction, byUser);
    }

    public void clearLogs() {
        int size = auditLogs.size();
        auditLogs.clear();
        log.info("Audit logs cleared - {} entries removed", size);
    }

    public String getInstanceHash() {
        return Integer.toHexString(System.identityHashCode(this));
    }

    private void validateAction(String action) {
        if (!VALID_ACTIONS.contains(action.toUpperCase())) {
            log.warn("Invalid action type: {}", action);
            throw new IllegalArgumentException(String.format(Constants.INVALID_ACTION, action));
        }
    }

}