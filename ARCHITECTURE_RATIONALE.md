# PayPilot - Architectural Style and Rationale

## Section 4: Architectural Style and Rationale

---

## Chosen Architectural Style

**Layered (3-Tier) Architecture with MVC Pattern Integration**

PayPilot implements a **Layered Architecture** consisting of three primary tiers:
1. **Presentation Layer** (View)
2. **Business Logic Layer** (Controller)
3. **Data Access Layer** (DAO/Model)

This architecture is enhanced with **Model-View-Controller (MVC)** pattern principles to achieve clear separation of concerns between user interface, business logic, and data management.

---

## Rationale for Architectural Choice

### 1. Alignment with Project-Specific Needs

PayPilot is a desktop expense tracking and group expense management application that requires:
- **Complex business logic** for expense calculations, budget management, recurring expenses, and split strategies
- **Multiple user interaction points** including personal expense tracking, group management, budget settings, and report generation
- **Data persistence** for users, expenses, groups, budgets, and shared expenses
- **Independent layer evolution** allowing UI improvements without affecting business logic or data storage

We chose a **Layered Architecture** because PayPilot's expense management system requires clear separation between:
- **User interactions** (LoginView, MainDashboard, AddExpenseForm, GroupPanel, etc.)
- **Business operations** (ExpenseManager, GroupController, BudgetManager, AuthenticationController)
- **Data persistence** (ExpenseDAO, UserDAO, GroupDAO, BudgetDAO, DatabaseManager)

This structure ensures that when we need to modify the split calculation algorithms or add new report types, we only need to update the Business Logic Layer without touching the Presentation or Data Access Layers. Similarly, if we decide to migrate from file-based storage (.dat files) to a SQL database, we only modify the Data Access Layer while keeping the rest of the system intact.

### 2. Achievement of Non-Functional Requirements

The Layered Architecture directly supports PayPilot's critical non-functional requirements:

**Maintainability:** Each layer has well-defined responsibilities. For example:
- The Presentation Layer (view package) handles all UI rendering and user input
- The Business Logic Layer (controller package) contains all expense processing, split calculations, and report generation
- The Data Access Layer (dao package) manages all file I/O and data serialization

This separation means developers can work on different layers simultaneously without conflicts. Adding a new chart type in ChartsDashboardPanel doesn't require understanding the DatabaseManager's serialization logic.

**Testability:** Layered architecture enables unit testing at each level:
- Business logic in ExpenseManager and GroupController can be tested independently of the UI
- Split strategies (EqualSplitStrategy, WeightedSplitStrategy, PercentageSplitStrategy) can be validated with test data
- DAOs can be mocked during controller testing, avoiding file system dependencies

**Scalability and Extensibility:** The architecture allows PayPilot to grow incrementally:
- New expense categories can be added by extending the Business Logic Layer
- Additional split strategies can be integrated into GroupController without modifying existing code
- New report formats can be added through ReportFactory without changing the UI layer
- The system can evolve from a desktop application to a client-server architecture by introducing a service layer between controllers and DAOs

**Security:** The layered approach provides natural security boundaries:
- AuthenticationController in the Business Logic Layer centralizes all authentication logic
- Data Access Layer can implement encryption for sensitive user data
- Presentation Layer never directly accesses data files, preventing unauthorized data manipulation

**Performance:** The Singleton DatabaseManager with caching (shown in our Data Access Layer) optimizes performance by:
- Maintaining a single connection point for all data operations
- Implementing in-memory caching to reduce disk I/O
- Thread-safe operations to handle concurrent access without data corruption

### 3. Integration with Design Patterns from Assignment 2

The Layered Architecture provides an ideal foundation for implementing our chosen design patterns, creating a cohesive and robust system:

#### **Data Access Layer (DAO Package):**

- **Singleton Pattern (DatabaseManager):** The DatabaseManager uses the Singleton pattern to ensure a single, centralized point of data access across all DAOs. This prevents multiple conflicting connection initializations and provides controlled lifecycle management. All DAO classes (ExpenseDAO, UserDAO, GroupDAO, BudgetDAO) interact with the DatabaseManager singleton, ensuring consistent data handling and caching.

- **DAO Pattern (ExpenseDAO, UserDAO, GroupDAO, BudgetDAO):** Each DAO encapsulates the persistence logic for its respective model entity. This abstraction isolates the Business Logic Layer from the details of file serialization, allowing us to switch storage mechanisms (e.g., from .dat files to SQL database) without impacting controllers.

#### **Business Logic Layer (Controller Package):**

- **Facade Pattern (ExpenseManager, GroupController):** ExpenseManager and GroupController act as facades, providing simplified high-level interfaces to the Presentation Layer. Instead of the UI directly interacting with multiple DAOs and complex business logic, these facades expose clean methods like `addExpense()`, `createGroup()`, and `generateReport()`. This reduces coupling between layers and makes the API easier to use.

- **Strategy Pattern (Split Strategies):** The GroupController uses the Strategy pattern for expense splitting algorithms. The SplitStrategy interface has three implementations: EqualSplitStrategy, WeightedSplitStrategy, and PercentageSplitStrategy. The controller can dynamically select the appropriate strategy at runtime based on user preference, promoting flexibility and adhering to the Open/Closed Principle.

- **Command Pattern (AddExpenseCommand, DeleteExpenseCommand, EditExpenseCommand, UndoManager):** The Command pattern encapsulates expense operations as objects, enabling undo/redo functionality and providing an audit trail. The UndoManager maintains a history of commands, allowing users to reverse actions. This pattern sits naturally in the Business Logic Layer, coordinating between the Presentation Layer's user actions and the Data Access Layer's persistence operations.

- **Factory Method Pattern (ReportFactory, ReportGenerator):** The ReportFactory creates different types of report generators (PersonalReportGenerator, GroupReportGenerator) based on user requirements. This pattern resides in the controller.report sub-package and allows the system to be open for extension (adding new report types) without modifying existing code. The Presentation Layer requests reports through the factory without knowing implementation details.

#### **Cross-Layer Patterns:**

- **Adapter Pattern (Planned for External Services):** As PayPilot evolves to integrate external services (e.g., EncryptionAdapter for data security, ExternalRateProviderAdapter for currency conversion), adapters will sit between the Business Logic Layer and external APIs. This isolates changes in third-party services and promotes interchangeability.

#### **Architectural Synergy:**

The Layered Architecture acts as the "skeleton" that organizes these patterns into a coherent structure:

1. **Data flows cleanly through layers:** View → Controller (Facade) → DAO → DatabaseManager (Singleton)
2. **Business logic is encapsulated:** Split strategies, commands, and report factories operate within the Business Logic Layer
3. **Flexibility is maximized:** Each pattern solves a specific problem while respecting layer boundaries
4. **Maintenance is simplified:** Changes to one pattern or component have minimal ripple effects due to layer isolation

For example, when a user adds a shared expense in the GroupPanel (Presentation Layer):
1. The UI calls `GroupController.addSharedExpense()` (Facade Pattern)
2. GroupController uses a SplitStrategy (Strategy Pattern) to calculate individual shares
3. An AddExpenseCommand (Command Pattern) is created and executed, allowing undo
4. The command invokes ExpenseDAO (DAO Pattern) to persist the data
5. ExpenseDAO uses DatabaseManager (Singleton Pattern) to write to disk

This seamless integration demonstrates how the Layered Architecture and design patterns work together to create a maintainable, extensible, and robust expense management system.

---

## Visual Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  (view package)                                              │
│  - LoginView, SignupView, MainDashboard                      │
│  - PersonalPanel, GroupPanel, AddExpenseForm                 │
│  - BudgetSettingsDialog, ChartsDashboardPanel                │
│  - Split/SharedExpenseDialogs, ReceiptViewerDialog           │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ↓ User Actions
┌─────────────────────────────────────────────────────────────┐
│                  BUSINESS LOGIC LAYER                        │
│  (controller package)                                        │
│                                                              │
│  Facades:                                                    │
│  - ExpenseManager, GroupController, BudgetManager            │
│  - AuthenticationController, RecurringExpenseManager         │
│                                                              │
│  Patterns:                                                   │
│  - Strategy: SplitStrategy hierarchy                         │
│  - Command: AddExpenseCommand, DeleteExpenseCommand, etc.    │
│  - Factory: ReportFactory → ReportGenerator                  │
│  - Controller: UndoManager, SettlementOptimizer              │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ↓ Data Operations
┌─────────────────────────────────────────────────────────────┐
│                   DATA ACCESS LAYER                          │
│  (dao package & model package)                               │
│                                                              │
│  DAO Pattern:                                                │
│  - ExpenseDAO, UserDAO, GroupDAO, BudgetDAO                  │
│  - RecurringExpenseDAO                                       │
│                                                              │
│  Singleton:                                                  │
│  - DatabaseManager (centralized data access & caching)       │
│                                                              │
│  Models:                                                     │
│  - Expense, User, Group, Budget, SharedExpense               │
│  - Receipt, SplitDetail, RecurringExpense                    │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ↓ File I/O
┌─────────────────────────────────────────────────────────────┐
│                      DATA STORAGE                            │
│  (data/ directory)                                           │
│  - users.dat, expenses_*.dat, budgets_*.dat                  │
│  - groups.dat, shared_expenses.dat                           │
│  - paypilot.db (SQLite for advanced features)                │
└─────────────────────────────────────────────────────────────┘
```

---

## Conclusion

PayPilot's **Layered Architecture with MVC integration** provides the optimal balance of structure, flexibility, and maintainability for an expense management application. By clearly separating concerns across three distinct layers and strategically implementing design patterns within each layer, the system achieves:

- **Clear separation of concerns** enabling parallel development and easier debugging
- **High maintainability** through isolated layer responsibilities
- **Enhanced testability** with independent layer testing
- **Future-proof extensibility** allowing seamless addition of new features
- **Strong pattern integration** where Singleton, DAO, Facade, Strategy, Command, and Factory patterns work harmoniously within the layered structure

This architectural approach positions PayPilot for long-term success, whether it remains a desktop application or evolves into a distributed client-server system.

---

**Document Version:** 1.0  
**Date:** December 12, 2025  
**Project:** PayPilot - Personal & Group Expense Tracker  
**Course:** Software Architecture & Design
