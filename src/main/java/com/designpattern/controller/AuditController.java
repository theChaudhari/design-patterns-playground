package com.designpattern.controller;

import com.designpattern.model.AuditEntry;
import com.designpattern.model.AuditRequest;
import com.designpattern.model.AuditStats;
import com.designpattern.service.AuditLoggerService;
import com.designpattern.singleton.*;
import com.designpattern.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/audit")
@Tag(name = "Audit Logger API", description = "Singleton Pattern — single audit logger instance for entire app")
public class AuditController {


    // Always fetches the SAME instance — Singleton in action
    private final AuditLoggerService auditLogger = AuditLoggerService.getInstance();

    @Operation(summary = "Log an audit entry", description = "Records a user action. Supported actions: LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Audit log recorded", content = @Content(schema = @Schema(implementation = AuditEntry.class))), @ApiResponse(responseCode = "400", description = "Invalid action or missing fields")})
    @PostMapping("/log")
    public ResponseEntity<AuditEntry> log(@Valid @RequestBody AuditRequest request) {
        log.info("Request received - POST /audit/log - userId: {}, action: {}", request.getUserId(), request.getAction());
        return ResponseEntity.ok(auditLogger.log(request));
    }

    @Operation(summary = "Get all audit logs")
    @ApiResponse(responseCode = "200", description = "All audit logs fetched successfully")
    @GetMapping("/logs")
    public ResponseEntity<List<AuditEntry>> getAllLogs() {
        log.info("Request received - GET /audit/logs");
        return ResponseEntity.ok(auditLogger.getAllLogs());
    }

    @Operation(summary = "Get audit logs by user")
    @ApiResponse(responseCode = "200", description = "User audit logs fetched successfully")
    @GetMapping("/logs/{userId}")
    public ResponseEntity<List<AuditEntry>> getLogsByUser(@Parameter(description = "User ID to filter logs", example = "user123") @PathVariable String userId) {
        log.info("Request received - GET /audit/logs/{}", userId);
        return ResponseEntity.ok(auditLogger.getLogsByUser(userId));
    }

    @Operation(summary = "Get audit statistics", description = "Returns total logs, breakdown by action type and by user.")
    @ApiResponse(responseCode = "200", description = "Stats fetched successfully", content = @Content(schema = @Schema(implementation = AuditStats.class)))
    @GetMapping("/stats")
    public ResponseEntity<AuditStats> getStats() {
        log.info("Request received - GET /audit/stats");
        return ResponseEntity.ok(auditLogger.getStats());
    }

    @Operation(summary = "Clear all audit logs")
    @ApiResponse(responseCode = "200", description = "All logs cleared successfully")
    @DeleteMapping("/logs")
    public ResponseEntity<String> clearLogs() {
        log.info("Request received - DELETE /audit/logs");
        auditLogger.clearLogs();
        return ResponseEntity.ok(Constants.LOGS_CLEARED);
    }

    @Operation(summary = "Prove Singleton — all 5 implementations return same instance", description = "Calls getInstance() on all 5 Singleton types and returns their hash codes. " + "All hashes for AuditLoggerService should be identical — proving ONE instance exists.")
    @ApiResponse(responseCode = "200", description = "Singleton proof fetched successfully")
    @GetMapping("/singleton-proof")
    public ResponseEntity<Map<String, Object>> singletonProof() {
        log.info("Request received - GET /audit/singleton-proof");

        String auditHash1 = AuditLoggerService.getInstance().getInstanceHash();
        String auditHash2 = AuditLoggerService.getInstance().getInstanceHash();
        String auditHash3 = AuditLoggerService.getInstance().getInstanceHash();

        return ResponseEntity.ok(Map.of("description", "All AuditLoggerService hashes must be identical — proves ONE instance", "implementations", Map.of("EAGER", EagerAuditLogger.getInstance().getImplementationType() + " | hash: " + EagerAuditLogger.getInstance().getInstanceHash(), "LAZY", LazyAuditLogger.getInstance().getImplementationType() + " | hash: " + LazyAuditLogger.getInstance().getInstanceHash(), "SYNCHRONIZED", SynchronizedAuditLogger.getInstance().getImplementationType() + " | hash: " + SynchronizedAuditLogger.getInstance().getInstanceHash(), "DOUBLE_CHECKED", DoubleCheckedAuditLogger.getInstance().getImplementationType() + " | hash: " + DoubleCheckedAuditLogger.getInstance().getInstanceHash(), "ENUM", EnumAuditLogger.INSTANCE.getImplementationType() + " | hash: " + EnumAuditLogger.INSTANCE.getInstanceHash()), "auditLoggerProof", Map.of("call1", "hash: " + auditHash1, "call2", "hash: " + auditHash2, "call3", "hash: " + auditHash3, "allSame", auditHash1.equals(auditHash2) && auditHash2.equals(auditHash3))));
    }

}