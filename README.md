# 🚦 Proxy Design Pattern — API Rate Limiting

A Spring Boot implementation of the **Proxy Design Pattern** using a real-world
**API Rate Limiting** use case — control how many requests a user can make per minute,
with in-memory caching to avoid repeated DB hits.

---

## 📌 What is Rate Limiting?

Rate limiting **controls how many requests** a user/client can make within a time window.
Without it, a single user can flood your API and bring the server down.

> Think of it like a toll booth — only N cars can pass per minute.
> Cars that exceed the limit must wait for the next window.

---

## 💡 How It's Implemented Here

`RateLimitProxy` sits in front of `ProductServiceImpl`. It intercepts every request,
checks the user's request count within the current 1-minute window, checks the cache,
and only hits the real service on a cache miss — the real service never knows any of this exists.

### Flow

```
GET /api/product/{id}
Header: X-User-Id: user123
        │
        ▼
RateLimitProxy (@Primary)
        │
        ├── userId missing?          ──→ 400 Bad Request
        │
        ├── window expired?          ──→ reset count, start new window
        │
        ├── count >= 5 per minute?   ──→ 429 Too Many Requests
        │                                 + X-RateLimit-Reset-After: Ns
        │
        └── count < 5? ──→ increment count
                                │
                                ▼
                        cache.containsKey(id)?
                                │
                    ┌───────────┴───────────┐
                   YES                      NO
                    │                       │
               Cache HIT ⚡         ProductServiceImpl
               return cached        store in cache
               product              return product
                    │                       │
                    └───────────┬───────────┘
                                │
                         Product Response
                    + X-RateLimit-Limit: 5
                    + X-RateLimit-Remaining: N
```

---

## 🏗️ Project Structure

```
src/main/java/com/designpattern/
│
├── controller/
│   └── ProductController.java          # REST endpoints + rate limit headers in response
│
├── proxy/
│   └── RateLimitProxy.java             # PROXY — rate limit + cache (@Primary)
│
├── service/
│   ├── IProductService.java            # Common interface
│   └── ProductServiceImpl.java         # REAL SERVICE — fetches product from DB
│
├── repository/
│   └── ProductRepository.java          # Simulated DB
│
├── model/
│   ├── Product.java                    # Product entity
│   └── UserRequestInfo.java            # Tracks count + window start per user
│
├── exception/
│   ├── RateLimitExceededException.java # Custom 429 exception
│   └── GlobalExceptionHandler.java     # Handles 429, 400, 500 cleanly
│
├── config/
│   └── SwaggerConfig.java              # OpenAPI / Swagger UI configuration
│
├── utils/
│   └── Constants.java                  # Rate limit config, header names, messages
│
└── DesignPatternApplication.java       # Spring Boot entry point
```

---

## 🔑 Key Classes

### `UserRequestInfo` — Tracks Per User State
```java
@Data
public class UserRequestInfo {
    private String userId;
    private int    requestCount;
    private long   windowStartTime;

    public boolean isWindowExpired(long windowDurationMs) {
        return System.currentTimeMillis() - windowStartTime >= windowDurationMs;
    }

    public void resetWindow() {
        this.requestCount    = 0;
        this.windowStartTime = System.currentTimeMillis();
    }

    public long getRetryAfterMs(long windowDurationMs) {
        return windowDurationMs - (System.currentTimeMillis() - windowStartTime);
    }
}
```
Each user gets their own `UserRequestInfo` — stored in `ConcurrentHashMap` inside the proxy.

---

### `RateLimitProxy` — The Proxy ⭐
```java
@Service
@Primary
public class RateLimitProxy implements IProductService {

    private final ConcurrentHashMap<String, UserRequestInfo> userRequestMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Product>           productCache   = new ConcurrentHashMap<>();

    public Product getProductById(Long id, String userId) {
        validateUserId(userId);       // step 1 - validate header
        enforceRateLimit(userId);     // step 2 - check rate limit
        return getFromCacheOrReal(id); // step 3 - cache or real service
    }

    private Product getFromCacheOrReal(Long id) {
        if (productCache.containsKey(id)) {
            log.info("Cache HIT - productId: {}", id);
            return productCache.get(id);                  // ⚡ no DB call
        }
        log.info("Cache MISS - productId: {}", id);
        Product product = realService.getProductById(id); // DB call only on miss
        productCache.put(id, product);
        return product;
    }

    private void enforceRateLimit(String userId) {
        UserRequestInfo info = userRequestMap.computeIfAbsent(userId, UserRequestInfo::new);

        if (info.isWindowExpired(Constants.WINDOW_DURATION_MS)) {
            info.resetWindow();
        }

        if (info.getRequestCount() >= Constants.MAX_REQUESTS_PER_WINDOW) {
            throw new RateLimitExceededException(...);  // 429
        }

        info.incrementCount();
    }
}
```

Two `ConcurrentHashMap`s — one for rate limiting state, one for product cache — both thread-safe.

---

### `GlobalExceptionHandler` — Clean Error Responses
```java
@ExceptionHandler(RateLimitExceededException.class)
public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException ex) {
    return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .header(Constants.HEADER_RATE_RESET, retryAfterSec + "s")
            .body(Map.of(
                    "status",     429,
                    "error",      "Too Many Requests",
                    "message",    ex.getMessage(),
                    "retryAfter", retryAfterSec + "s"
            ));
}
```

---

### Rate Limit Config in `Constants.java`
```java
public static final int  MAX_REQUESTS_PER_WINDOW = 5;
public static final long WINDOW_DURATION_MS      = 60_000; // 1 minute
```
Change these two values to adjust rate limit — no other code changes needed.

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
| `http://localhost:2297/api-docs` | Raw OpenAPI JSON — importable in Postman |


### How to Test Rate Limiting in Swagger
1. Open `http://localhost:2297/swagger-ui.html`
2. Click `GET /api/product/{id}` → already in **Try it out** mode
3. Enter `id: 1` and `X-User-Id: user123`
4. Hit **Execute** 5 times
5. Watch `X-RateLimit-Remaining` drop from 4 → 3 → 2 → 1 → 0
6. 6th hit → `429 Too Many Requests` ✅
7. Notice `Request duration` drops after 1st call — cache working ⚡

---

## 🧪 API Reference

### All Endpoints

| Method | Endpoint | Header Required | Description |
|--------|----------|-----------------|-------------|
| `GET` | `/api/product/{id}` | `X-User-Id` | Fetch product — rate limited + cached |
| `GET` | `/api/product/rate-limit-status` | `X-User-Id` | Check current rate limit status |

### Response Headers

| Header | Description |
|--------|-------------|
| `X-RateLimit-Limit` | Max requests allowed per window |
| `X-RateLimit-Remaining` | Requests remaining in current window |
| `X-RateLimit-Reset-After` | Seconds until window resets (on 429 only) |

### Available Products

| ID | Name | Category | Price |
|----|------|----------|-------|
| 1 | iPhone 15 | Electronics | ₹79,999 |
| 2 | MacBook Pro | Electronics | ₹1,99,999 |
| 3 | Nike Air Max | Footwear | ₹12,999 |
| 4 | Sony Headphones | Electronics | ₹29,999 |
| 5 | Levi's Jeans | Clothing | ₹4,999 |

---

### Examples

#### ✅ Request 1 — Cache MISS + Rate Limit Starts
```
GET /api/product/1
X-User-Id: user123

HTTP 200 OK
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 4

{ "id": 1, "name": "iPhone 15", "category": "Electronics", "price": 79999.0 }
```

#### ✅ Request 2 — Cache HIT ⚡ + Rate Limit Decrements
```
GET /api/product/1
X-User-Id: user123

HTTP 200 OK
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 3     ← decremented
                              ← no DB call, served from cache ⚡
```

#### ❌ Request 6 — Rate Limit Exceeded
```
GET /api/product/1
X-User-Id: user123

HTTP 429 Too Many Requests
X-RateLimit-Reset-After: 45s

{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Max 5 requests per minute allowed.",
  "retryAfter": "45s"
}
```

#### 📊 Check Rate Limit Status
```
GET /api/product/rate-limit-status
X-User-Id: user123

HTTP 200 OK
{
  "userId": "user123",
  "requestCount": 3,
  "windowStartTime": 1704067200000
}
```

#### ❌ Missing Header
```
GET /api/product/1
(no X-User-Id header)

HTTP 400 Bad Request
{
  "status": 400,
  "error": "Bad Request",
  "message": "Missing required header: X-User-Id"
}
```

---

## 🛠️ Tech Stack

| Tool | Details |
|------|---------|
| Java | 17+ |
| Spring Boot | REST + `@Primary` for proxy injection |
| SpringDoc OpenAPI | Swagger UI + API docs |
| Lombok | `@Data`, `@Getter`, `@AllArgsConstructor` |
| SLF4J | Logging |
| ConcurrentHashMap | Thread-safe in-memory store |
| Maven | Build tool |

---

**theChaudhari**
- GitHub: [@theChaudhari](https://github.com/theChaudhari)
- Repo: [design-patterns-playground](https://github.com/theChaudhari/design-patterns-playground)