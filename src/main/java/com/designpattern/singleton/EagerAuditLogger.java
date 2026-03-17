package com.designpattern.singleton;

/**
 * EAGER SINGLETON
 * Instance is created at class loading time — before it is even needed.
 * <p>
 * ✅ Simple, thread-safe (JVM handles class loading)
 * ❌ Instance created even if never used — wastes memory
 */
public class EagerAuditLogger {

    // Created immediately when class is loaded by JVM
    private static final EagerAuditLogger INSTANCE = new EagerAuditLogger();

    private EagerAuditLogger() {
        // private constructor — prevents new EagerAuditLogger()
    }

    public static EagerAuditLogger getInstance() {
        return INSTANCE;
    }

    public String getImplementationType() {
        return "EAGER";
    }

    public String getInstanceHash() {
        return Integer.toHexString(System.identityHashCode(this));
    }

}