# 🍕 Factory Method Design Pattern — Food Delivery Platform

A Spring Boot implementation of the **Factory Method Design Pattern** using a real-world
**Food Delivery Platform** use case — route orders to Zomato, Swiggy or UberEats
without the client knowing which service handles it.

---

## 📌 What is Factory Method Pattern?

The **Factory Method Pattern** defines an interface for creating an object but lets
subclasses or concrete implementations decide which class to instantiate.
The client never uses `new` directly — it asks the factory for the right object.

> Think of it like a food court — you tell the counter "I want a burger".
> The counter (factory) decides which stall (Zomato/Swiggy/UberEats) handles it.
> You never walk to the stall directly.

---

## 💡 How It's Implemented Here

`DeliveryFactory` holds a `Map<String, IDeliveryService>` — keyed by platform name.
When a request comes in, the factory looks up the platform key and returns the
correct service. The controller never imports `ZomatoDeliveryService` or
`SwiggyDeliveryService` — it only knows `IDeliveryService`.

### Flow

```
POST /delivery/order
{ "platform": "SWIGGY", "item": "Pizza", "address": "Pune" }
        │
        ▼
DeliveryController
        │
        ▼
DeliveryFactory.getDeliveryService("SWIGGY")
        │
        ├── "ZOMATO"    ──→ ZomatoDeliveryService
        ├── "SWIGGY"    ──→ SwiggyDeliveryService   ✅ selected
        ├── "UBEREATS"  ──→ UberEatsDeliveryService
        └── unknown     ──→ 400 Bad Request
                │
                ▼
        SwiggyDeliveryService.placeOrder()
                │
                ▼
        OrderResponse {
          platform:      SWIGGY
          partner:       Swiggy Genie
          estimatedTime: 25 min
          charge:        ₹0.00
        }
```

---

## 🏗️ Project Structure

```
src/main/java/com/designpattern/
│
├── controller/
│   └── DeliveryController.java         # REST endpoints — place order + compare platforms
│
├── factory/
│   └── DeliveryFactory.java            # THE FACTORY — resolves platform → service
│
├── service/
│   ├── IDeliveryService.java           # Product interface — all platforms implement this
│   ├── ZomatoDeliveryService.java      # Concrete Product 1 — Zomato
│   ├── SwiggyDeliveryService.java      # Concrete Product 2 — Swiggy
│   └── UberEatsDeliveryService.java    # Concrete Product 3 — UberEats
│
├── model/
│   ├── OrderRequest.java               # Request: platform + item + address
│   └── OrderResponse.java              # Response: message + partner + time + charge
│
├── exception/
│   └── GlobalExceptionHandler.java     # Handles 400, 500 cleanly
│
├── config/
│   └── SwaggerConfig.java              # OpenAPI / Swagger UI configuration
│
├── utils/
│   └── Constants.java                  # Platform names, charges, times, messages
│
└── DesignPatternApplication.java       # Spring Boot entry point
```

---

## 🔑 Key Classes

### `IDeliveryService` — Product Interface
```java
public interface IDeliveryService {
    OrderResponse placeOrder(OrderRequest request);
    String        getPlatformName();
    int           getEstimatedTime();
    double        getDeliveryCharge();
}
```
All 3 platforms implement this contract — factory and controller only depend on this interface.

---

### `DeliveryFactory` — The Factory ⭐
```java
@Component
public class DeliveryFactory {

    private final Map<String, IDeliveryService> deliveryServiceMap;

    public DeliveryFactory(ZomatoDeliveryService zomato,
                           SwiggyDeliveryService swiggy,
                           UberEatsDeliveryService uberEats) {
        this.deliveryServiceMap = Map.of(
                Constants.ZOMATO,   zomato,
                Constants.SWIGGY,   swiggy,
                Constants.UBEREATS, uberEats
        );
    }

    public IDeliveryService getDeliveryService(String platform) {
        String key = platform.toUpperCase();
        IDeliveryService service = deliveryServiceMap.get(key);

        if (service == null) {
            throw new IllegalArgumentException(
                String.format(Constants.UNSUPPORTED_PLATFORM, platform));
        }

        log.info("Factory resolved: {} → {}", platform, service.getClass().getSimpleName());
        return service;
    }
}
```
Factory holds a map of all services — keyed by platform name.
Returns the right `IDeliveryService` — controller never knows the concrete class.

---

### Concrete Services

| Service | Platform | Partner | Time | Charge |
|---------|----------|---------|------|--------|
| `ZomatoDeliveryService` | `ZOMATO` | Zomato Delivery | 30 min | ₹29.00 |
| `SwiggyDeliveryService` | `SWIGGY` | Swiggy Genie | 25 min | ₹0.00 |
| `UberEatsDeliveryService` | `UBEREATS` | Uber Eats Runner | 35 min | ₹49.00 |

---

### `DeliveryController` — Knows Only the Interface
```java
@Autowired
private DeliveryFactory deliveryFactory;

@PostMapping("/order")
public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
    IDeliveryService service  = deliveryFactory.getDeliveryService(request.getPlatform());
    OrderResponse    response = service.placeOrder(request);
    return ResponseEntity.ok(response);
}
```
Controller never imports `ZomatoDeliveryService`, `SwiggyDeliveryService` or `UberEatsDeliveryService`.
It only talks to `IDeliveryService` — adding a new platform means **zero controller changes**.

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
| `http://localhost:2297/swagger-ui.html` | Visual API documentation + Try it out |
| `http://localhost:2297/api-docs` | Raw OpenAPI JSON |

---

## 🧪 API Reference

### All Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/delivery/order` | Place order — factory picks the platform |
| `GET` | `/delivery/compare` | Compare all platforms side by side |

---

### Examples

#### ✅ Order via Zomato
```json
// POST /delivery/order
{ "platform": "ZOMATO", "item": "Biryani", "address": "Pune, MH" }

// Response
{
  "message":              "Order placed successfully via ZOMATO!",
  "platform":             "ZOMATO",
  "item":                 "Biryani",
  "address":              "Pune, MH",
  "deliveryPartner":      "Zomato Delivery",
  "estimatedTimeMinutes": 30,
  "deliveryCharge":       29.0
}
```

#### ✅ Order via Swiggy
```json
// POST /delivery/order
{ "platform": "SWIGGY", "item": "Pizza", "address": "Mumbai, MH" }

// Response
{
  "message":              "Order placed successfully via SWIGGY!",
  "platform":             "SWIGGY",
  "item":                 "Pizza",
  "address":              "Mumbai, MH",
  "deliveryPartner":      "Swiggy Genie",
  "estimatedTimeMinutes": 25,
  "deliveryCharge":       0.0
}
```

#### ✅ Order via UberEats
```json
// POST /delivery/order
{ "platform": "UBEREATS", "item": "Burger", "address": "Nashik, MH" }

// Response
{
  "message":              "Order placed successfully via UBEREATS!",
  "platform":             "UBEREATS",
  "item":                 "Burger",
  "address":              "Nashik, MH",
  "deliveryPartner":      "Uber Eats Runner",
  "estimatedTimeMinutes": 35,
  "deliveryCharge":       49.0
}
```

#### ❌ Unsupported Platform
```json
// POST /delivery/order
{ "platform": "DUNZO", "item": "Coffee", "address": "Pune" }

// Response
{
  "status":  400,
  "error":   "Bad Request",
  "message": "Unsupported delivery platform: DUNZO. Supported: ZOMATO, SWIGGY, UBEREATS"
}
```

#### 📊 Compare All Platforms
```json
// GET /delivery/compare?item=Pizza

// Response
[
  { "platform": "ZOMATO",   "estimatedTime": "30 min", "deliveryCharge": "₹29.00", "partner": "Zomato Delivery"   },
  { "platform": "SWIGGY",   "estimatedTime": "25 min", "deliveryCharge": "₹0.00",  "partner": "Swiggy Genie"      },
  { "platform": "UBEREATS", "estimatedTime": "35 min", "deliveryCharge": "₹49.00", "partner": "Uber Eats Runner"  }
]
```

---

## ➕ Adding a New Platform

To add **Dunzo** — just 3 steps, zero changes to existing code:

**1. Add constants in `Constants.java`**
```java
public static final String DUNZO         = "DUNZO";
public static final String DUNZO_PARTNER = "Dunzo Rider";
public static final int    DUNZO_TIME    = 20;
public static final double DUNZO_CHARGE  = 15.00;
```

**2. Create the service**
```java
@Service
public class DunzoDeliveryService implements IDeliveryService {

    @Override
    public OrderResponse placeOrder(OrderRequest request) {
        return new OrderResponse(
                String.format(Constants.ORDER_PLACED, getPlatformName()),
                getPlatformName(), request.getItem(), request.getAddress(),
                Constants.DUNZO_PARTNER, getEstimatedTime(), getDeliveryCharge());
    }

    @Override public String getPlatformName()  { return Constants.DUNZO; }
    @Override public int    getEstimatedTime() { return Constants.DUNZO_TIME; }
    @Override public double getDeliveryCharge(){ return Constants.DUNZO_CHARGE; }
}
```

**3. Register in `DeliveryFactory`**
```java
this.deliveryServiceMap = Map.of(
        Constants.ZOMATO,   zomato,
        Constants.SWIGGY,   swiggy,
        Constants.UBEREATS, uberEats,
        Constants.DUNZO,    dunzo    // ← just add here ✅
);
```

Controller = **zero changes** ✅

---

## 🛠️ Tech Stack

| Tool | Details |
|------|---------|
| Java | 17+ |
| Spring Boot | REST + Dependency Injection |
| SpringDoc OpenAPI | Swagger UI + API docs |
| Lombok | `@Data`, `@AllArgsConstructor` |
| SLF4J | Logging |
| Maven | Build tool |

---

## 🎯 One Line Interview Answer

> *"What makes it Factory Pattern is not the Map — it's that the client never uses `new`, never imports concrete classes, and only depends on the interface. The factory centralizes all object creation logic — validation, lookup, error handling — in one place. Adding a new platform requires zero changes to the controller, which proves the client is truly decoupled from the concrete implementations."*

---

## The Golden Rule 🏆

> *"If your client code contains 'new ConcreteClass()' → NOT Factory Pattern
If your client code only knows the interface       → Factory Pattern ✅"*

---

> ## 👤 Author

**theChaudhari**
- GitHub: [@theChaudhari](https://github.com/theChaudhari)