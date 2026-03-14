# 🔐 Chain of Responsibility — User Authentication Pipeline

A Spring Boot implementation of the **Chain of Responsibility Pattern** using a real-world
**User Authentication Pipeline** — API Key → JWT → OAuth.

---

## 📌 What is the Chain of Responsibility Pattern?

The **Chain of Responsibility** is a behavioral design pattern that passes a request along a
**chain of handlers**. Each handler decides either to **process the request** or **pass it
to the next handler** in the chain.

> Instead of one big `if-else` block checking every auth type, each handler owns its own
> logic and simply forwards what it can't handle.

---

## 💡 How It's Implemented Here

Each authentication type (`API_KEY`, `JWT`, `OAUTH`) has its own dedicated handler.
The handlers are chained together via Spring `@Configuration` — the controller only
talks to the **first handler** and the chain does the rest.

### Flow

```
POST /auth/authenticate
        │
        ▼
ApiKeyAuthHandler  ──── type == API_KEY? ──── ✅ Authenticate
        │ ❌ No
        ▼
JwtAuthHandler     ──── type == JWT?     ──── ✅ Authenticate
        │ ❌ No
        ▼
OAuthHandler       ──── type == OAUTH?   ──── ✅ Authenticate
        │ ❌ No
        ▼
DefaultHandler     ──── ❌ Authentication Failed
```

---

## 🏗️ Project Structure

```
src/main/java/com/designpattern/
│
├── controller/
│   └── AuthController.java           # REST endpoint — entry point
│
├── handler/
│   ├── AuthHandler.java              # Chain interface (setNext + authenticate)
│   ├── AbstractAuthHandler.java      # Base class — handles chaining & fallback
│   ├── ApiKeyAuthHandler.java        # Handler 1: API Key authentication
│   ├── JwtAuthHandler.java           # Handler 2: JWT authentication
│   ├── OAuthHandler.java             # Handler 3: OAuth authentication
│   └── AuthChainConfig.java          # Spring config — builds the chain
│
├── model/
│   ├── AuthRequest.java              # Request: type + token
│   └── AuthResponse.java             # Response: status + handler + message
│
├── utils/
│   └── Constants.java                # Auth types, statuses, messages
│
└── DesignPatternApplication.java     # Spring Boot entry point
```

---

## 🔑 Key Classes

### `AuthHandler` — Chain Interface
```java
public interface AuthHandler {
    AuthHandler setNext(AuthHandler nextHandler);
    AuthResponse authenticate(AuthRequest request);
}
```
Every handler in the chain implements this contract.

---

### `AbstractAuthHandler` — Base Handler
```java
public abstract class AbstractAuthHandler implements AuthHandler {
    private AuthHandler nextHandler;

    @Override
    public AuthHandler setNext(AuthHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler; // enables fluent chaining: h1.setNext(h2).setNext(h3)
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (nextHandler != null) return nextHandler.authenticate(request);
        return new AuthResponse(FAILURE, DEFAULT_HANDLER, AUTH_FAILED);
    }
}
```
Handles the **pass-to-next** logic so concrete handlers only focus on their own auth type.

---

### Concrete Handlers

| Handler | Processes | Valid Token Format |
|---------|-----------|-------------------|
| `ApiKeyAuthHandler` | `API_KEY` | Starts with `APIKEY-` |
| `JwtAuthHandler` | `JWT` | Starts with `Bearer ` |
| `OAuthHandler` | `OAUTH` | Starts with `oauth_` |
| `AbstractAuthHandler` (fallback) | anything else | — |

---

### `AuthChainConfig` — Chain Builder
```java
@Bean
public AuthHandler authHandlerChain(ApiKeyAuthHandler apiKeyHandler,
                                    JwtAuthHandler jwtHandler,
                                    OAuthHandler oAuthHandler) {
    apiKeyHandler
        .setNext(jwtHandler)
        .setNext(oAuthHandler);

    return apiKeyHandler; // entry point of the chain
}
```
The chain is assembled once at startup via Spring `@Configuration`. The controller
just autowires `AuthHandler` and calls `authenticate()` — it has no knowledge of
the chain internals.

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

### Endpoint
```
POST http://localhost:2297/auth/authenticate
Content-Type: application/json
```

### Request Body
```json
{
  "type": "API_KEY | JWT | OAUTH",
  "token": "<your-token>"
}
```

---

### Examples

#### ✅ API Key — Success
```json
// Request
{ "type": "API_KEY", "token": "APIKEY-abc123xyz" }

// Response
{
  "status": "SUCCESS",
  "handler": "ApiKeyAuthHandler",
  "message": "Authenticated via API Key successfully."
}
```

#### ✅ JWT — Success
```json
// Request
{ "type": "JWT", "token": "Bearer eyJhbGciOiJIUzI1NiJ9..." }

// Response
{
  "status": "SUCCESS",
  "handler": "JwtAuthHandler",
  "message": "Authenticated via JWT Token successfully."
}
```

#### ✅ OAuth — Success
```json
// Request
{ "type": "OAUTH", "token": "oauth_token_google_xyz" }

// Response
{
  "status": "SUCCESS",
  "handler": "OAuthHandler",
  "message": "Authenticated via OAuth successfully."
}
```

#### ❌ Invalid Token
```json
// Request
{ "type": "JWT", "token": "wrong-token-format" }

// Response
{
  "status": "FAILURE",
  "handler": "JwtAuthHandler",
  "message": "Invalid or missing token."
}
```

#### ❌ Unknown Type — End of Chain
```json
// Request
{ "type": "UNKNOWN", "token": "something" }

// Response
{
  "status": "FAILURE",
  "handler": "DefaultHandler",
  "message": "Authentication failed. No handler could process the request."
}
```

---

## ➕ Adding a New Auth Handler

To add a new type (e.g. **Basic Auth**), follow these 3 steps:

**1. Add constants in `Constants.java`**
```java
public static final String BASIC_AUTH         = "BASIC_AUTH";
public static final String BASIC_AUTH_HANDLER = "BasicAuthHandler";
public static final String BASIC_AUTH_SUCCESS = "Authenticated via Basic Auth successfully.";
```

**2. Create the handler**
```java
@Component
@Order(4)
public class BasicAuthHandler extends AbstractAuthHandler {
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (Constants.BASIC_AUTH.equalsIgnoreCase(request.getType())) {
            if (request.getToken() != null && request.getToken().startsWith("Basic ")) {
                return new AuthResponse(SUCCESS, BASIC_AUTH_HANDLER, BASIC_AUTH_SUCCESS);
            }
            return new AuthResponse(FAILURE, BASIC_AUTH_HANDLER, INVALID_TOKEN);
        }
        return super.authenticate(request);
    }
}
```

**3. Add to the chain in `AuthChainConfig.java`**
```java
apiKeyHandler
    .setNext(jwtHandler)
    .setNext(oAuthHandler)
    .setNext(basicAuthHandler); // just append here ✅
```

No changes needed in the Controller. ✅

---

## 🆚 Strategy vs Chain of Responsibility

| | Strategy Pattern | Chain of Responsibility |
|--|-----------------|------------------------|
| Our example | Car → Manufacturer | Auth Type → Handler |
| Handlers used | **One** selected at runtime | **One or more** in sequence |
| Decision maker | Client / Map | Each handler itself |
| Best for | Swapping algorithms | Pipelines / escalation |

---

## 🛠️ Tech Stack

| Tool | Details |
|------|---------|
| Java | 17+ |
| Spring Boot | REST + DI + Configuration |
| Lombok | `@Data`, `@AllArgsConstructor` |
| Maven | Build tool |

---

## 👤 Author

**theChaudhari**
- GitHub: [@theChaudhari](https://github.com/theChaudhari)