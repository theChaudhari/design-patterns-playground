# 📈 Observer Design Pattern — Stock Price Alert System

A Spring Boot implementation of the **Observer Design Pattern** using a real-world
**Stock Price Alert System** — when a stock price changes, all subscribers get notified instantly.

---

## 📌 What is the Observer Pattern?

The **Observer Pattern** is a behavioral design pattern where:
- A **Subject** (Publisher) maintains a list of **Observers** (Subscribers)
- When the Subject's state changes, it **automatically notifies all Observers**
- Observers react independently without the Subject knowing what they do

> Think of it like a YouTube channel — when you upload a video (Subject changes state),
> all subscribers (Observers) get notified automatically.

---

## 💡 How It's Implemented Here

All three observer beans (`EmailAlertObserver`, `SMSAlertObserver`, `MobileAlertObserver`)
are auto-injected into `StockService` via **constructor injection** as a `List<StockObserver>`.

When `POST /stock/update` is called → `StockService` detects the price change →
builds a `StockEvent` → loops through all observers and calls `update()` on each.

### Flow

```
POST /stock/update { symbol: "TATA", price: 150.00 }
        │
        ▼
StockService (Subject)
  detects price change → builds StockEvent → notifyObservers()
        │
        ├── EmailAlertObserver.update()   → "Email Alert Sent   → TATA | ₹150.00 | 📉 DOWN"
        ├── SMSAlertObserver.update()     → "SMS Alert Sent     → TATA | ₹150.00 | 📉 DOWN"
        └── MobileAlertObserver.update()  → "Push Notification  → TATA | ₹150.00 | 📉 DOWN"
```

---

## 🏗️ Project Structure

```
src/main/java/com/designpattern/
│
├── controller/
│   └── StockController.java          # REST endpoints — update price, register & remove observers
│
├── observer/
│   ├── StockObserver.java            # Observer interface (update + getObserverName)
│   ├── StockSubject.java             # Subject interface (register + remove + notify)
│   ├── EmailAlertObserver.java       # Observer 1: Email notification
│   ├── SMSAlertObserver.java         # Observer 2: SMS notification
│   └── MobileAlertObserver.java      # Observer 3: Push notification
│
├── service/
│   └── StockService.java             # Subject — manages observers & notifies on change
│
├── model/
│   ├── StockUpdateRequest.java       # Request: symbol + price
│   ├── StockEvent.java               # Event: symbol + oldPrice + newPrice + change
│   └── StockResponse.java            # Response: message + event + notifications
│
├── utils/
│   └── Constants.java                # Stock symbols, messages, observer names
│
└── DesignPatternApplication.java     # Spring Boot entry point
```

---

## 🔑 Key Classes

### `StockObserver` — Observer Interface
```java
public interface StockObserver {
    String update(StockEvent event);   // react to price change
    String getObserverName();          // unique observer identity
}
```

### `StockSubject` — Subject Interface
```java
public interface StockSubject {
    void registerObserver(StockObserver observer);
    void removeObserver(StockObserver observer);
    List<String> notifyObservers(StockEvent event);
}
```

### `StockService` — The Subject (Publisher)
```java
private static final Logger log = LoggerFactory.getLogger(StockService.class);

private final Map<String, StockObserver> observerRegistry = new HashMap<>();
private final List<StockObserver>        activeObservers  = new ArrayList<>();
private final Map<String, Double>        stockPrices      = new HashMap<>();

public StockService(List<StockObserver> allObservers) {
    allObservers.forEach(observer -> {
        observerRegistry.put(observer.getObserverName(), observer);
        activeObservers.add(observer);
        log.info("Observer registered at startup: {}", observer.getObserverName());
    });
}

@Override
public List<String> notifyObservers(StockEvent event) {
    log.info("Notifying {} active observer(s) for stock: {}", activeObservers.size(), event.getSymbol());
    return activeObservers.stream()
            .map(observer -> observer.update(event))
            .toList();
}

public String registerObserverByName(String name) {
    StockObserver observer = observerRegistry.get(name);
    if (observer == null) {
        log.warn("Register failed - observer not found: {}", name);
        return "Observer not found: " + name;
    }
    if (activeObservers.contains(observer)) {
        log.warn("Register skipped - observer already active: {}", name);
        return name + " is already registered.";
    }
    activeObservers.add(observer);
    log.info("Observer activated at runtime: {}", name);
    return Constants.OBSERVER_ADDED + " → " + name;
}

public String removeObserverByName(String name) {
    StockObserver observer = observerRegistry.get(name);
    if (observer == null) {
        log.warn("Remove failed - observer not found: {}", name);
        return "Observer not found: " + name;
    }
    activeObservers.remove(observer);
    log.info("Observer deactivated at runtime: {}", name);
    return Constants.OBSERVER_REMOVED + " → " + name;
}
```

Two lists work together:

| List | Purpose |
|------|---------|
| `observerRegistry` | Pool of ALL available observers — never changes |
| `activeObservers` | Currently listening observers — changes at runtime |

Logging levels used:

| Level | When |
|-------|------|
| `INFO` | Normal operations — startup, activate, deactivate, price update |
| `WARN` | Unexpected but non-breaking — observer not found, already registered |

### Concrete Observers

| Observer | Bean | Simulates |
|----------|------|-----------|
| `EmailAlertObserver` | `@Component` | Sending email alert |
| `SMSAlertObserver` | `@Component` | Sending SMS alert |
| `MobileAlertObserver` | `@Component` | Sending push notification |

Each observer is completely **independent** — they don't know about each other.

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

## 🧪 API Reference

### All Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/stock/update` | Update stock price → notifies all active observers |
| `POST` | `/stock/observer/register/{observerName}` | Register an observer at runtime |
| `DELETE` | `/stock/observer/remove/{observerName}` | Remove an observer at runtime |

### Available Observer Names
| Name | Description |
|------|-------------|
| `EmailAlertObserver` | Email notifications |
| `SMSAlertObserver` | SMS notifications |
| `MobileAlertObserver` | Push notifications |

---

### 1️⃣ Update Stock Price
```
POST http://localhost:2297/stock/update
Content-Type: application/json
```

### Request Body
```json
{
  "symbol": "TATA",
  "price": 150.00
}
```

---

### Examples

#### 📉 Price DROP — All 3 Observers Active
```json
// Request
{ "symbol": "TATA", "price": 150.00 }

// Response
{
  "message": "Stock price updated & all observers notified.",
  "event": {
    "symbol": "TATA",
    "oldPrice": 175.00,
    "newPrice": 150.00,
    "change": "📉 DOWN"
  },
  "notifications": [
    "Email Alert Sent   → Stock: TATA | Price: ₹150.00 | Change: 📉 DOWN",
    "SMS Alert Sent     → Stock: TATA | Price: ₹150.00 | Change: 📉 DOWN",
    "Push Notification  → Stock: TATA | Price: ₹150.00 | Change: 📈 DOWN"
  ]
}
```

#### 📈 Price RISE — All 3 Observers Active
```json
// Request
{ "symbol": "TATA", "price": 900.00 }

// Response
{
  "message": "Stock price updated & all observers notified.",
  "event": {
    "symbol": "TATA",
    "oldPrice": 850.00,
    "newPrice": 900.00,
    "change": "📈 UP"
  },
  "notifications": [
    "Email Alert Sent   → Stock: TATA | Price: ₹900.00 | Change: 📈 UP",
    "SMS Alert Sent     → Stock: TATA | Price: ₹900.00 | Change: 📈 UP",
    "Push Notification  → Stock: TATA | Price: ₹900.00 | Change: 📈 UP"
  ]
}
```

---

### 2️⃣ Remove an Observer at Runtime
```
DELETE http://localhost:2297/stock/observer/remove/SMSAlertObserver
```
```json
// Response
"Observer unregistered successfully. → SMSAlertObserver"
```

Now update stock price — SMS won't fire:
```json
// POST /stock/update → { "symbol": "TATA", "price": 140.00 }
{
  "notifications": [
    "Email Alert Sent   → Stock: TATA | Price: ₹140.00 | Change: 📉 DOWN",
    "Push Notification  → Stock: TATA | Price: ₹140.00 | Change: 📉 DOWN"
    // ✅ SMSAlertObserver removed — not notified
  ]
}
```

---

### 3️⃣ Re-Register an Observer at Runtime
```
POST http://localhost:2297/stock/observer/register/SMSAlertObserver
```
```json
// Response
"Observer registered successfully. → SMSAlertObserver"
```

Now update stock price — SMS fires again:
```json
// POST /stock/update → { "symbol": "TATA", "price": 160.00 }
{
  "notifications": [
    "Email Alert Sent   → Stock: TATA | Price: ₹160.00 | Change: 📈 UP",
    "SMS Alert Sent     → Stock: TATA | Price: ₹160.00 | Change: 📈 UP",  // ✅ back!
    "Push Notification  → Stock: TATA | Price: ₹160.00 | Change: 📈 UP"
  ]
}
```

---

## ➕ Adding a New Observer

To add a **WhatsApp Alert**, just 2 steps:

**1. Add constant in `Constants.java`**
```java
public static final String WHATSAPP_OBSERVER = "WhatsAppAlertObserver";
public static final String WHATSAPP_ALERT    = "WhatsApp Alert Sent → Stock: %s | Price: ₹%.2f | Change: %s";
```

**2. Create the observer**
```java
@Component
public class WhatsAppAlertObserver implements StockObserver {
    @Override
    public String update(StockEvent event) {
        return String.format(Constants.WHATSAPP_ALERT,
                event.getSymbol(), event.getNewPrice(), event.getChange());
    }

    @Override
    public String getObserverName() {
        return Constants.WHATSAPP_OBSERVER;
    }
}
```

Spring auto-injects it into `StockService` — **zero changes** to existing code. ✅

---

## 🛠️ Tech Stack

| Tool | Details |
|------|---------|
| Java | 17+ |
| Spring Boot | REST + Constructor Injection |
| Lombok | `@Data`, `@AllArgsConstructor` |
| Maven | Build tool |

---

## 👤 Author

**theChaudhari**
- GitHub: [@theChaudhari](https://github.com/theChaudhari)
