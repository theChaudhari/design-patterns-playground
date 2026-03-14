# 🚗 Strategy Design Pattern — Car Manufacturer Lookup

A Spring Boot implementation of the **Strategy Design Pattern** using a real-world **car model → manufacturer** lookup use case.

---

## 📌 What is the Strategy Pattern?

The **Strategy Pattern** is a behavioral design pattern that:
- Defines a **family of algorithms** (strategies)
- **Encapsulates** each one independently
- Makes them **interchangeable at runtime** without changing the client code

> Instead of writing `if-else` or `switch` blocks to pick behavior, the Strategy Pattern lets you plug in the right implementation dynamically.

---

## 💡 How It's Implemented Here

Spring Boot's dependency injection is used to implement this pattern **without a single if-else statement**.

Each car service (`BMWService`, `MercedesService`, `FordService`) is registered as a **named Spring bean** using the car model name as the bean identifier. These beans are then auto-injected into a `Map<String, IProcessor>` in the controller — Spring resolves the correct strategy automatically based on the path variable.

### Flow

```
POST /strategy-pattern/{carName}
        │
        ▼
Map<String, IProcessor>
        │
        ├── "X7"      → BMWService       → "BMW"
        ├── "GLS"     → MercedesService  → "MERCEDES"
        ├── "MUSTANG" → FordService      → "FORD"
        └── default   → DefaultService   → "Invalid Service"
```

---

## 🏗️ Project Structure

```
src/main/java/com/designpattern/
│
├── controller/
│   └── Controller.java           # REST endpoint, resolves strategy via Map
│
├── service/
│   ├── IProcessor.java           # Strategy interface
│   ├── BMWService.java           # Strategy: BMW X7
│   ├── MercedesService.java      # Strategy: Mercedes GLS
│   ├── FordService.java          # Strategy: Ford Mustang
│   └── DefaultService.java       # Fallback strategy
│
├── entity/
│   └── Car.java                  # Car entity
│
├── utils/
│   └── Constants.java            # Car names & company name constants
│
└── DesignPatternApplication.java # Spring Boot entry point
```

---

## 🔑 Key Classes

### `IProcessor` — Strategy Interface
```java
public interface IProcessor {
    String getCompanyDetails(String carName);
}
```
All strategies implement this single contract.

---

### `BMWService`, `MercedesService`, `FordService` — Concrete Strategies
Each service is registered as a Spring bean named after the car model:

```java
@Service(Constants.BMW_X7)          // bean name = "X7"
public class BMWService implements IProcessor {
    public String getCompanyDetails(String carName) {
        return Constants.BMW;       // returns "BMW"
    }
}
```

| Bean Name | Service Class | Returns |
|-----------|--------------|---------|
| `X7` | `BMWService` | `BMW` |
| `GLS` | `MercedesService` | `MERCEDES` |
| `MUSTANG` | `FordService` | `FORD` |
| `DEFAULT` | `DefaultService` | `Invalid Service` |

---

### `Controller` — Strategy Resolver
```java
@Autowired
private Map<String, IProcessor> getService;

@PostMapping("/{carName}")
public String getCompanyDetails(@PathVariable String carName) {
    return getService.getOrDefault(carName, getService.get(Constants.DEFAULT))
                     .getCompanyDetails(carName);
}
```

Spring automatically populates `Map<String, IProcessor>` where the **key = bean name** and **value = service instance**. No `if-else`, no `switch` — pure Strategy Pattern.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application

```bash
./mvnw spring-boot:run
```

App starts on port **2297**.

---

## 🧪 API Reference

### Endpoint
```
POST http://localhost:2297/strategy-pattern/{carName}
```

### Examples

| Request | Response |
|---------|----------|
| `POST /strategy-pattern/GLS` | `MERCEDES` |
| `POST /strategy-pattern/X7` | `BMW` |
| `POST /strategy-pattern/MUSTANG` | `FORD` |
| `POST /strategy-pattern/UNKNOWN` | `Invalid Service` |

### Postman

Import and hit directly:
```
POST http://localhost:2297/strategy-pattern/GLS
POST http://localhost:2297/strategy-pattern/X7
POST http://localhost:2297/strategy-pattern/MUSTANG
```

---

## ➕ Adding a New Car Strategy

To add a new car (e.g. **Audi A8**), follow these 2 steps:

**1. Add constants in `Constants.java`**
```java
public static final String AUDI = "AUDI";
public static final String AUDI_A8 = "A8";
```

**2. Create a new service**
```java
@Service(Constants.AUDI_A8)
public class AudiService implements IProcessor {
    @Override
    public String getCompanyDetails(String carName) {
        return Constants.AUDI;
    }
}
```

That's it — no changes needed in the Controller. ✅

---

## 🛠️ Tech Stack

| Tool | Details |
|------|---------|
| Java | 17+ |
| Spring Boot | REST + DI |
| Lombok | `@Data` for boilerplate reduction |
| Maven | Build tool |

---