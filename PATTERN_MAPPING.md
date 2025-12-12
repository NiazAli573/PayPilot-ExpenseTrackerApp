# Design Patterns Mapping to Layered Architecture

## Assignment 2 Patterns Implementation in PayPilot's Layered Architecture

---

| Pattern Name | Pattern Type | Architecture Layer | Specific Components | Problem Solved | Architectural Benefit |
|-------------|--------------|-------------------|---------------------|----------------|----------------------|
| **Singleton** | Creational | Data Access Layer | `DatabaseManager` | Multiple conflicting database connections; need centralized data access point | Ensures single point of data management across all DAOs; provides in-memory caching; thread-safe operations |
| **Factory Method** | Creational | Business Logic Layer | `ReportFactory` → `PersonalReportGenerator`, `GroupReportGenerator` | Flexible instantiation of various report formats without tight coupling | Presentation Layer requests reports without knowing implementation details; new report types added without modifying client code (OCP) |
| **Facade** | Structural | Business Logic Layer | `ExpenseManager`, `GroupController` | Complex DAO interactions difficult for UI layer; need simplified high-level API | Reduces coupling between Presentation and Data Access layers; provides clean interface (e.g., `addExpense()`, `createGroup()`) |
| **Adapter** | Structural | Business Logic Layer (planned) | `EncryptionAdapter`, `ExternalRateProviderAdapter` | Integrate external services with unified interface | Isolates external API changes from Business Logic Layer; promotes interchangeability of third-party services |
| **Strategy** | Behavioral | Business Logic Layer | `SplitStrategy` → `EqualSplitStrategy`, `WeightedSplitStrategy`, `PercentageSplitStrategy` | Multiple algorithms for splitting expenses; need runtime selection | Enables dynamic algorithm selection in `GroupController`; easy to add new split types without modifying existing code |
| **Command** | Behavioral | Business Logic Layer | `AddExpenseCommand`, `DeleteExpenseCommand`, `EditExpenseCommand`, `UndoManager` | Need undo/redo capability and audit trail for expense operations | Encapsulates operations as objects; supports history tracking; coordinates between Presentation Layer actions and Data Access Layer persistence |

---

## Layer-by-Layer Pattern Distribution

### Data Access Layer (DAO Package)
**Purpose:** Data persistence, storage abstraction

| Component | Pattern | Responsibility |
|-----------|---------|----------------|
| `DatabaseManager` | Singleton | Centralized data access, caching, thread safety |
| `ExpenseDAO` | DAO | Expense persistence operations |
| `UserDAO` | DAO | User account persistence |
| `GroupDAO` | DAO | Group and shared expense persistence |
| `BudgetDAO` | DAO | Budget tracking persistence |
| `RecurringExpenseDAO` | DAO | Recurring expense persistence |

**Key Benefit:** Business Logic Layer never directly touches file I/O or serialization logic

---

### Business Logic Layer (Controller Package)
**Purpose:** Business rules, orchestration, pattern coordination

| Component | Pattern(s) | Responsibility |
|-----------|-----------|----------------|
| `ExpenseManager` | Facade | Simplifies expense operations for UI |
| `GroupController` | Facade + Strategy | Manages groups and applies split strategies |
| `BudgetManager` | Facade | Handles budget tracking and alerts |
| `AuthenticationController` | Facade | Centralized authentication logic |
| `RecurringExpenseManager` | Facade | Processes recurring expense scheduling |
| `ReportFactory` | Factory Method | Creates appropriate report generator |
| `PersonalReportGenerator` | Factory Product | Generates personal expense reports |
| `GroupReportGenerator` | Factory Product | Generates group expense reports |
| `SplitStrategy` | Strategy (Interface) | Defines split calculation contract |
| `EqualSplitStrategy` | Strategy | Equal division algorithm |
| `WeightedSplitStrategy` | Strategy | Weighted division algorithm |
| `PercentageSplitStrategy` | Strategy | Percentage-based division |
| `AddExpenseCommand` | Command | Encapsulates add expense operation |
| `DeleteExpenseCommand` | Command | Encapsulates delete expense operation |
| `EditExpenseCommand` | Command | Encapsulates edit expense operation |
| `UndoManager` | Command Invoker | Manages command history and undo/redo |
| `SettlementOptimizer` | Algorithm | Optimizes group debt settlements |

**Key Benefit:** All business logic centralized and testable independently from UI and persistence

---

### Presentation Layer (View Package)
**Purpose:** User interface, user input, display

| Component | Architecture Role | Delegates To |
|-----------|------------------|--------------|
| `LoginView` | Authentication UI | `AuthenticationController` |
| `SignupView` | Registration UI | `AuthenticationController` |
| `MainDashboard` | Main application frame | Multiple controllers |
| `PersonalPanel` | Personal expense display | `ExpenseManager` |
| `GroupPanel` | Group expense display | `GroupController` |
| `AddExpenseForm` | Expense input form | `ExpenseManager` + `UndoManager` |
| `BudgetSettingsDialog` | Budget configuration | `BudgetManager` |
| `SharedExpenseDialog` | Shared expense input | `GroupController` |
| `ChartsDashboardPanel` | Analytics visualization | `ExpenseManager` |
| `ReceiptViewerDialog` | Receipt display | `ExpenseManager` |

**Key Benefit:** UI components are thin—they collect input and display output but contain no business logic

---

## Data Flow Example: Adding a Shared Expense with Pattern Interaction

```
USER ACTION: Add $100 shared expense split equally among 3 people
│
├─ PRESENTATION LAYER: SharedExpenseDialog
│  └─ Collects: amount=$100, members=[Alice, Bob, Carol], splitType="EQUAL"
│
├─ BUSINESS LOGIC LAYER: GroupController (FACADE)
│  │
│  ├─ Step 1: Select Strategy (STRATEGY PATTERN)
│  │  └─ SplitStrategy strategy = strategies.get("EQUAL")
│  │  └─ List<SplitDetail> splits = strategy.split(100, members, null)
│  │  └─ Result: Alice=$33.33, Bob=$33.33, Carol=$33.34
│  │
│  ├─ Step 2: Create Command (COMMAND PATTERN)
│  │  └─ AddExpenseCommand cmd = new AddExpenseCommand(expenseManager, sharedExpense)
│  │  └─ undoManager.executeCommand(cmd)
│  │
│  └─ Step 3: Execute Command
│     └─ expenseManager.addExpense(sharedExpense)
│
├─ DATA ACCESS LAYER: ExpenseDAO (DAO PATTERN)
│  │
│  ├─ Step 1: Get DatabaseManager (SINGLETON PATTERN)
│  │  └─ DatabaseManager dbManager = DatabaseManager.getInstance()
│  │
│  ├─ Step 2: Load existing expenses
│  │  └─ List<Expense> expenses = dbManager.loadData("shared_expenses", ArrayList.class)
│  │
│  ├─ Step 3: Add new expense
│  │  └─ expenses.add(sharedExpense)
│  │
│  └─ Step 4: Persist to storage
│     └─ dbManager.saveData("shared_expenses", expenses)
│
└─ DATA STORAGE: data/shared_expenses.dat
   └─ File updated with new expense
```

---

## Pattern Interaction Benefits

### 1. Maintainability
- **Layer Isolation:** Changing split algorithm (Strategy) doesn't affect UI (View) or storage (DAO)
- **Pattern Encapsulation:** Each pattern solves one problem in its appropriate layer
- **Clear Dependencies:** Data flows unidirectionally (View → Controller → DAO → Storage)

### 2. Testability
```java
// Business Logic Layer testing (no UI or file I/O)
@Test
public void testEqualSplitStrategy() {
    SplitStrategy strategy = new EqualSplitStrategy();
    List<SplitDetail> result = strategy.split(100, Arrays.asList("A", "B", "C"), null);
    assertEquals(33.33, result.get(0).getAmount(), 0.01);
}

// Command Pattern testing
@Test
public void testUndoRedoCommands() {
    UndoManager undoManager = new UndoManager();
    AddExpenseCommand cmd = new AddExpenseCommand(mockManager, "user", expense);
    
    undoManager.executeCommand(cmd);
    assertTrue(mockManager.hasExpense(expense));
    
    undoManager.undo();
    assertFalse(mockManager.hasExpense(expense));
}

// Data Access Layer testing with mocked DatabaseManager
@Test
public void testExpenseDAOPersistence() {
    ExpenseDAO dao = new ExpenseDAO();
    dao.addExpense("testuser", new Expense("Lunch", 15.0, "Food"));
    List<Expense> expenses = dao.loadExpenses("testuser");
    assertEquals(1, expenses.size());
}
```

### 3. Extensibility
- **New Report Types:** Create `TaxReportGenerator` implementing `ReportGenerator`, register in `ReportFactory`—no changes to UI or DAOs
- **New Split Algorithms:** Implement `SplitStrategy` (e.g., `CustomWeightedSplit`), add to `GroupController.strategies`—no changes to UI or storage
- **Database Migration:** Replace file-based DAOs with JDBC-based DAOs—Business Logic and Presentation Layers unchanged

### 4. Scalability
```
Current (Desktop):
View → Controller → DAO → File Storage

Future (Client-Server):
[Client] View → HTTP API
                    ↓
[Server] REST Controller → Business Logic → DAO → SQL Database
```
Business Logic Layer (Strategy, Command, Factory) remains unchanged during migration.

---

## Design Pattern Justification Summary

| Pattern | Why Chosen for PayPilot | How It Fits Layered Architecture |
|---------|------------------------|----------------------------------|
| Singleton | PayPilot needs single, controlled database connection with caching | Data Access Layer foundation—all DAOs depend on it |
| Factory Method | Multiple report types (personal, group, budget) with flexible creation | Business Logic Layer—shields Presentation Layer from report implementation details |
| Facade | UI needs simple interface to complex expense/group operations | Business Logic Layer—reduces coupling between layers |
| Adapter | External services (encryption, currency) have varying APIs | Business Logic Layer—isolates external dependencies |
| Strategy | Multiple split algorithms selected at runtime | Business Logic Layer—encapsulates algorithms, enables OCP |
| Command | Undo/redo and audit trail required for expense operations | Business Logic Layer—coordinates between Presentation actions and Data Access persistence |

---

**Conclusion:** PayPilot's Layered Architecture provides the structural foundation, while design patterns solve specific problems within each layer. The combination creates a maintainable, testable, extensible, and scalable expense management system where changes are localized, new features integrate cleanly, and the system can evolve from desktop to distributed architecture without major refactoring.

---

**Document Version:** 1.0  
**Date:** December 12, 2025  
**Project:** PayPilot - Personal & Group Expense Tracker
