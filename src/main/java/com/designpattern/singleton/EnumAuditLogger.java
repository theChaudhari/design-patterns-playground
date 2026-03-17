package com.designpattern.singleton;

/**
 * ENUM SINGLETON — Best Practice ⭐
 * Uses Java enum — JVM guarantees only one instance exists.
 * <p>
 * ✅ Thread-safe — JVM handles it
 * ✅ Serialization-safe — enum values are never re-created during deserialization
 * ✅ Reflection-safe — cannot break with reflection
 * ✅ Simplest implementation
 * ✅ Recommended by Joshua Bloch (Effective Java)
 * ❌ Cannot extend another class (enums cannot use extends)
 */
public enum EnumAuditLogger {

    INSTANCE; // ← JVM creates exactly ONE instance of this

    public String getImplementationType() {
        return "ENUM";
    }

    public String getInstanceHash() {
        return Integer.toHexString(System.identityHashCode(this));
    }

}