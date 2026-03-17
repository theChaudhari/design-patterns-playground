package com.designpattern.singleton;

/**
 * LAZY SINGLETON
 * Instance is created only when first requested — not at class loading.
 * <p>
 * ✅ Memory efficient — created only when needed
 * ❌ NOT thread-safe — two threads can create two instances simultaneously
 * ❌ Never use in multi-threaded applications
 */
public class LazyAuditLogger {

    // null at start — created only when getInstance() is called first time
    private static LazyAuditLogger instance;

    private LazyAuditLogger() {
        // private constructor
    }

    public static LazyAuditLogger getInstance() {
        if (instance == null) {                  // ← Thread A and Thread B can both
            instance = new LazyAuditLogger();    //   pass this check simultaneously ❌
        }
        return instance;
    }

    public String getImplementationType() {
        return "LAZY";
    }

    public String getInstanceHash() {
        return Integer.toHexString(System.identityHashCode(this));
    }

}