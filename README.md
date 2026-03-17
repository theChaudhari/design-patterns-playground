# 📝 Singleton Design Pattern — Audit Logger

A Spring Boot implementation of the **Singleton Design Pattern** demonstrating
**all 5 ways** to implement Singleton using a real-world **Audit Logger** use case —
one logger instance records every user action across the entire application.

---

## 📌 What is Singleton Pattern?

The **Singleton Pattern** ensures a class has **only ONE instance** throughout the
entire application lifecycle and provides a **global access point** to it.

> Think of it like the CEO of a company — there is only ONE CEO at any time.
> Everyone who needs to talk to the CEO gets the same person, not a new one.

---

## 💡 Why Audit Logger?

An Audit Logger is a perfect Singleton use case because:

```
Without Singleton:
Controller A → new AuditLogger() → logs to list A
Controller B → new AuditLogger() → logs to list B  ← different instance! ❌
GET /logs    → returns list A only — B's logs lost ❌

With Singleton:
Controller A → AuditLogger.getInstance() → logs to THE list ✅
Controller B → AuditLogger.getInstance() → logs to THE SAME list ✅
GET /logs    → returns ALL logs from one instance ✅
```

---

## 🏗️ Project Structure

```
src/main/java/com/designpattern/
│
├── controller/
│   └── AuditController.java              # REST endpoints + singleton proof endpoint
│
├── singleton/
│   ├── EagerAuditLogger.java             # Implementation 1: Eager
│   ├── LazyAuditLogger.java              # Implementation 2: Lazy
│   ├── SynchronizedAuditLogger.java      # Implementation 3: Synchronized
│   ├── DoubleCheckedAuditLogger.java      # Implementation 4: Double-Checked ⭐
│   └── EnumAuditLogger.java              # Implementation 5: Enum ⭐
│
├── service/
│   └── AuditLoggerService.java           # Real service — Double-Checked Singleton
│
├── model/
│   ├── AuditRequest.java                 # Request: userId + action + resource
│   ├── AuditEntry.java                   # Log entry: id + userId + action + timestamp
│   └── AuditStats.java                   # Stats: total + byAction + byUser
│
├── exception/
│   └── GlobalExceptionHandler.java       # Handles 400, 500 cleanly
│
├── config/
│   └── SwaggerConfig.java                # OpenAPI configuration
│
├── utils/
│   └── Constants.java                    # Action types, messages
│
└── DesignPatternApplication.java         # Spring Boot entry point
```

---

## 🔑 All 5 Singleton Implementations

### 1. Eager Singleton
```java
public class EagerAuditLogger {
    // Created at class loading — before getInstance() is ever called
    private static final EagerAuditLogger INSTANCE = new EagerAuditLogger();

    private EagerAuditLogger() { }

    public static EagerAuditLogger getInstance() {
        return INSTANCE;
    }
}
```
| | |
|--|--|
| ✅ | Simple, thread-safe (JVM handles class loading) |
| ❌ | Created even if never used — wastes memory |
| Use when | Instance is always needed and lightweight |

---

### 2. Lazy Singleton
```java
public class LazyAuditLogger {
    private static LazyAuditLogger instance; // null at start

    private LazyAuditLogger() { }

    public static LazyAuditLogger getInstance() {
        if (instance == null) {
            instance = new LazyAuditLogger(); // created only on first call
        }
        return instance;
    }
}
```
| | |
|--|--|
| ✅ | Memory efficient — created only when needed |
| ❌ | NOT thread-safe — two threads can create two instances |
| Use when | Single-threaded applications only |

---

### 3. Synchronized Singleton
```java
public class SynchronizedAuditLogger {
    private static SynchronizedAuditLogger instance;

    private SynchronizedAuditLogger() { }

    public static synchronized SynchronizedAuditLogger getInstance() {
        if (instance == null) {
            instance = new SynchronizedAuditLogger();
        }
        return instance; // ← lock acquired on EVERY call ❌
    }
}
```
| | |
|--|--|
| ✅ | Thread-safe |
| ❌ | Slow — lock acquired on every call even after instance exists |
| Use when | Low-traffic apps where performance is not critical |

---

### 4. Double-Checked Locking ⭐
```java
public class DoubleCheckedAuditLogger {
    private static volatile DoubleCheckedAuditLogger instance;

    private DoubleCheckedAuditLogger() { }

    public static DoubleCheckedAuditLogger getInstance() {
        if (instance == null) {                         // Check 1 — no lock (fast)
            synchronized (DoubleCheckedAuditLogger.class) {
                if (instance == null) {                 // Check 2 — with lock (safe)
                    instance = new DoubleCheckedAuditLogger();
                }
            }
        }
        return instance;
    }
}
```
| | |
|--|--|
| ✅ | Thread-safe |
| ✅ | Fast — lock only acquired ONCE during creation |
| ✅ | `volatile` prevents CPU instruction reordering |
| Use when | Multi-threaded production code (most common before Enum) |

---

### 5. Enum Singleton ⭐ (Best Practice)
```java
public enum EnumAuditLogger {
    INSTANCE; // JVM guarantees exactly ONE instance

    public void log(String message) { ... }
}

// Usage
EnumAuditLogger.INSTANCE.log("User logged in");
```
| | |
|--|--|
| ✅ | Thread-safe — JVM handles it |
| ✅ | Serialization-safe — enum values never re-created |
| ✅ | Reflection-safe — cannot be broken |
| ✅ | Simplest implementation |
| ✅ | Recommended by Joshua Bloch (Effective Java) |
| ❌ | Cannot extend another class |
| Use when | Always — unless you need to extend a class |

---

## 📊 All 5 Implementations at a Glance

| Implementation | Thread-Safe | Lazy | Performance | Serialization-Safe | Recommended |
|---------------|-------------|------|-------------|-------------------|-------------|
| Eager | ✅ | ❌ | ✅ Fast | ❌ | Small apps |
| Lazy | ❌ | ✅ | ✅ Fast | ❌ | Single thread only |
| Synchronized | ✅ | ✅ | ❌ Slow | ❌ | Low traffic only |
| Double-Checked | ✅ | ✅ | ✅ Fast | ❌ | ✅ Production |
| Enum | ✅ | ❌ | ✅ Fast | ✅ | ✅ Best practice |

---

## ⭐ `AuditLoggerService` — The Real Singleton

Uses **Double-Checked Locking** with `CopyOnWriteArrayList` for thread-safe log storage:

```java
public class AuditLoggerService {

    private static volatile AuditLoggerService instance;

    // Thread-safe list — safe for concurrent reads and writes
    private final List<AuditEntry> auditLogs = new CopyOnWriteArrayList<>();

    private AuditLoggerService() { }

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
}
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application
```bash
./mvnw spring-boot:run
# Windows
mvnw.cmd spring-boot:run
```

App starts on port **2297**.

---

## 📖 Swagger UI

| URL | Description |
|-----|-------------|
| `http://localhost:2297/swagger-ui.html` | Visual API docs + Try it out |
| `http://localhost:2297/api-docs` | Raw OpenAPI JSON |

---

## 🧪 API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/audit/log` | Record a user action |
| `GET` | `/audit/logs` | Get all audit logs |
| `GET` | `/audit/logs/{userId}` | Get logs for a specific user |
| `GET` | `/audit/stats` | Get stats — total, by action, by user |
| `DELETE` | `/audit/logs` | Clear all logs |
| `GET` | `/audit/singleton-proof` | Prove all 5 implementations return same instance |

### Supported Action Types
`LOGIN` `LOGOUT` `CREATE` `UPDATE` `DELETE` `VIEW`

---

### Examples

#### ✅ Log an Action
```json
// POST /audit/log
{ "userId": "user123", "action": "LOGIN", "resource": "AuthService", "details": "User logged in from Pune" }

// Response
{
  "id":        "550e8400-e29b-41d4-a716-446655440000",
  "userId":    "user123",
  "action":    "LOGIN",
  "resource":  "AuthService",
  "details":   "User logged in from Pune",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 📊 Get Stats
```json
// GET /audit/stats
{
  "totalLogs": 5,
  "logsByAction": { "LOGIN": 2, "CREATE": 2, "DELETE": 1 },
  "logsByUser":   { "user123": 3, "admin": 2 }
}
```

#### 🔍 Singleton Proof
```json
// GET /audit/singleton-proof
{
  "description": "All AuditLoggerService hashes must be identical",
  "implementations": {
    "EAGER":          "EAGER | hash: 1b6d3586",
    "LAZY":           "LAZY | hash: 4554617c",
    "SYNCHRONIZED":   "SYNCHRONIZED | hash: 74a14482",
    "DOUBLE_CHECKED": "DOUBLE_CHECKED | hash: 1540e19d",
    "ENUM":           "ENUM | hash: 677327b6"
  },
  "auditLoggerProof": {
    "call1":   "hash: 6d06d69c",
    "call2":   "hash: 6d06d69c",
    "call3":   "hash: 6d06d69c",
    "allSame": true             ← proves ONE instance ✅
  }
}
```

## 🆚 Singleton in Java vs Spring

| | Manual Singleton | Spring `@Service` |
|--|-----------------|-------------------|
| Instance management | You manage it | Spring manages it |
| Thread safety | You implement it | Spring handles it |
| Testability | Hard | Easy with `@MockBean` |
| Reflection-safe | ❌ (except Enum) | ✅ |
| Recommended | Non-Spring apps | ✅ Spring apps |

> In Spring Boot — **every `@Service`, `@Repository`, `@Component` is already a Singleton.**
> You rarely need to implement Singleton manually in Spring apps.

---

## 🛠️ Tech Stack

| Tool | Details |
|------|---------|
| Java | 17+ |
| Spring Boot | REST + DI |
| SpringDoc OpenAPI | Swagger UI |
| Lombok | `@Data`, `@AllArgsConstructor` |
| CopyOnWriteArrayList | Thread-safe log storage |
| SLF4J | Logging |
| Maven | Build tool |

---

## 👤 Author

**theChaudhari**
- GitHub: [@theChaudhari](https://github.com/theChaudhari)
- Repo: [design-patterns-playground](https://github.com/theChaudhari/design-patterns-playground)