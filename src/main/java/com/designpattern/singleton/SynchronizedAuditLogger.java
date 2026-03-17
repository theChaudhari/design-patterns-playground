package com.designpattern.singleton;

/**
 * SYNCHRONIZED SINGLETON
 * Adds 'synchronized' to getInstance() — only one thread can enter at a time.
 * <p>
 * ✅ Thread-safe — no two instances created
 * ❌ SLOW — every call to getInstance() acquires a lock, even after instance exists
 * ❌ Performance bottleneck in high-traffic applications
 */
public class SynchronizedAuditLogger {

    private static SynchronizedAuditLogger instance;

    private SynchronizedAuditLogger() {
        // private constructor
    }

    // synchronized = only one thread executes this at a time
    public static synchronized SynchronizedAuditLogger getInstance() {
        if (instance == null) {
            instance = new SynchronizedAuditLogger();
        }
        return instance;         // ← lock acquired even after instance exists ❌
    }

    public String getImplementationType() {
        return "SYNCHRONIZED";
    }

    public String getInstanceHash() {
        return Integer.toHexString(System.identityHashCode(this));
    }

}