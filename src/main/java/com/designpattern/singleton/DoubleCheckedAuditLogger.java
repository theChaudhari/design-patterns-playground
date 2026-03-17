package com.designpattern.singleton;

/**
 * DOUBLE-CHECKED LOCKING SINGLETON
 * Checks instance twice — once without lock, once with lock.
 * Lock is only acquired when instance is null (first time only).
 * <p>
 * ✅ Thread-safe
 * ✅ Fast — lock only acquired once during first creation
 * ✅ Most used in production code (before Enum was introduced)
 * ⚠️ 'volatile' is MANDATORY — prevents CPU instruction reordering
 */
public class DoubleCheckedAuditLogger {

    // volatile = ensures visibility across threads + prevents reordering
    private static volatile DoubleCheckedAuditLogger instance;

    private DoubleCheckedAuditLogger() {
        // private constructor
    }

    public static DoubleCheckedAuditLogger getInstance() {
        if (instance == null) {                          // Check 1 — no lock (fast path)
            synchronized (DoubleCheckedAuditLogger.class) {
                if (instance == null) {                  // Check 2 — with lock (safe path)
                    instance = new DoubleCheckedAuditLogger();
                }
            }
        }
        return instance;
    }

    public String getImplementationType() {
        return "DOUBLE_CHECKED";
    }

    public String getInstanceHash() {
        return Integer.toHexString(System.identityHashCode(this));
    }

}