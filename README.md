# 🧩 design-patterns-playground

A structured reference repository for **Gang of Four (GoF) Design Patterns** implemented in Java using Spring Boot.
Each pattern lives on its own **categorized branch**, making it easy to study, clone, and experiment with patterns individually.

---

## 🚀 Tech Stack

| Tool | Details |
|------|---------|
| Java | 17+ |
| Spring Boot | REST + Dependency Injection |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

---

## 📂 Branch Naming Convention

Every pattern branch follows this structure:

```
{category}/{pattern-name}
```

| Category | Prefix |
|----------|--------|
| Behavioral | `behavioral/` |
| Creational | `creational/` |
| Structural | `structural/` |

**Example:**
```
behavioral/strategy-pattern
behavioral/chain-of-responsibility-pattern
creational/singleton-pattern
structural/adapter-pattern
```

> 💡 GitHub groups branches by `/` prefix — so all behavioral patterns appear together in the branch dropdown automatically.

---

## 📖 Pattern Index

### 🟢 Behavioral Patterns
> Deal with communication and responsibility between objects.

| Pattern | Branch | Status    |
|---------|--------|-----------|
| Strategy | `behavioral/strategy-pattern` | ✅ Done    |
| Chain of Responsibility | `behavioral/chain-of-responsibility-pattern` | ✅ Done    |
| Command | `behavioral/command-pattern` | 🔜 Planned |
| Iterator | `behavioral/iterator-pattern` | 🔜 Planned |
| Mediator | `behavioral/mediator-pattern` | 🔜 Planned |
| Memento | `behavioral/memento-pattern` | 🔜 Planned |
| Observer | `behavioral/observer-pattern` |✅ Done  |
| State | `behavioral/state-pattern` | 🔜 Planned |
| Template Method | `behavioral/template-method-pattern` | 🔜 Planned |
| Visitor | `behavioral/visitor-pattern` | 🔜 Planned |

---

### 🔵 Creational Patterns
> Deal with object creation mechanisms.

| Pattern | Branch | Status |
|---------|--------|--------|
| Singleton | `creational/singleton-pattern` | 🔜 Planned |
| Factory Method | `creational/factory-method-pattern` |✅ Done|
| Abstract Factory | `creational/abstract-factory-pattern` | 🔜 Planned |
| Builder | `creational/builder-pattern` | 🔜 Planned |
| Prototype | `creational/prototype-pattern` | 🔜 Planned |

---

### 🟠 Structural Patterns
> Deal with object composition and structure.

| Pattern | Branch | Status |
|---------|--------|--------|
| Adapter | `structural/adapter-pattern` | 🔜 Planned |
| Bridge | `structural/bridge-pattern` | 🔜 Planned |
| Composite | `structural/composite-pattern` | 🔜 Planned |
| Decorator | `structural/decorator-pattern` | 🔜 Planned |
| Facade | `structural/facade-pattern` | 🔜 Planned |
| Flyweight | `structural/flyweight-pattern` | 🔜 Planned |
| Proxy | `structural/proxy-pattern` | 🔜 Planned |

---

## ⚡ Getting Started

### Clone the repository

```bash
git clone https://github.com/theChaudhari/design-patterns-playground.git
cd design-patterns-playground
```

### Switch to a pattern branch

```bash
# Behavioral
git checkout behavioral/strategy-pattern

```

### Build & Run

```bash
./mvnw spring-boot:run
# Windows
mvnw.cmd spring-boot:run
```

---

## 🗂️ How This Repo Is Organized

```
master                          → README, parent pom, .gitignore (index only)
│
├── behavioral/
│   ├── strategy-pattern        → Car Manufacturer Lookup
│   ├── chain-of-responsibility → (coming soon)
│   └── observer-pattern        → (coming soon)
│
├── creational/
│   ├── singleton-pattern       → (coming soon)
│   └── factory-method-pattern  → (coming soon)
│
└── structural/
    ├── adapter-pattern         → (coming soon)
    └── decorator-pattern       → (coming soon)
```

> `master` acts as the **table of contents** — all pattern source code lives on its own branch.

---

## 👤 Author

**theChaudhari**
- GitHub: [@theChaudhari](https://github.com/theChaudhari)
- Repo: [design-patterns-playground](https://github.com/theChaudhari/design-patterns-playground)