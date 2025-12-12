# Section 4: Architectural Style and Rationale - SUBMISSION VERSION

---

## Architectural Style Choice

**Layered (3-Tier) Architecture with MVC Pattern Integration**

PayPilot implements a Layered Architecture consisting of three primary tiers:
- **Presentation Layer** (View Package) - User interface components
- **Business Logic Layer** (Controller Package) - Business rules and operations
- **Data Access Layer** (DAO Package) - Data persistence and retrieval

---

## Rationale (2-3 Paragraphs)

### Why This Style Fits PayPilot's Specific Needs

PayPilot is a desktop expense tracking application that manages personal expenses, group expenses, budgets, and generates various financial reports. We chose a Layered Architecture because our application requires clear separation between user interactions, complex business logic, and data persistence. The Presentation Layer (view package) contains all UI components like LoginView, MainDashboard, GroupPanel, and AddExpenseForm, which handle user input and display. The Business Logic Layer (controller package) includes ExpenseManager, GroupController, BudgetManager, and AuthenticationController, which implement expense calculations, budget tracking, group management, and split strategies. The Data Access Layer (dao package) contains ExpenseDAO, UserDAO, GroupDAO, and the Singleton DatabaseManager, which handle file serialization and data storage. This structure ensures that when we need to modify split calculation algorithms or add new report types, we only update the Business Logic Layer without touching the UI or data persistence code. Similarly, migrating from file-based storage (.dat files) to a SQL database requires changes only to the Data Access Layer, demonstrating the architecture's flexibility and maintainability.

### How This Style Achieves Non-Functional Requirements

The Layered Architecture directly supports PayPilot's critical non-functional requirements. **Maintainability** is enhanced because each layer has well-defined responsibilities—developers can modify the ChartsDashboardPanel without understanding DatabaseManager's serialization logic, and teams can work on different layers simultaneously without conflicts. **Testability** is improved through layer independence: business logic in ExpenseManager and split strategies (EqualSplitStrategy, WeightedSplitStrategy, PercentageSplitStrategy) can be unit tested independently of the UI, and DAOs can be mocked during controller testing to avoid file system dependencies. **Scalability and extensibility** are achieved because new features integrate seamlessly—additional expense categories, split strategies, and report formats can be added by extending the appropriate layer without modifying existing code. The architecture also provides natural **security boundaries**: AuthenticationController centralizes authentication logic in the Business Logic Layer, the Data Access Layer can implement encryption for sensitive data, and the Presentation Layer never directly accesses data files, preventing unauthorized manipulation. Finally, **performance** is optimized through the Singleton DatabaseManager with in-memory caching, which reduces disk I/O while providing thread-safe operations for concurrent access.

### Relationship to Design Patterns from Assignment 2

The Layered Architecture provides an ideal foundation for implementing our chosen design patterns cohesively. In the **Data Access Layer**, the **Singleton pattern** (DatabaseManager) ensures centralized data access with caching and thread safety, while the **DAO pattern** (ExpenseDAO, UserDAO, GroupDAO, BudgetDAO) encapsulates persistence logic, isolating the Business Logic Layer from storage implementation details. In the **Business Logic Layer**, the **Facade pattern** (ExpenseManager, GroupController) simplifies the interface for the Presentation Layer by hiding complex DAO interactions and business operations behind clean APIs like addExpense() and createGroup(). The **Strategy pattern** is used in GroupController for expense splitting, where SplitStrategy implementations (EqualSplitStrategy, WeightedSplitStrategy, PercentageSplitStrategy) can be selected at runtime, enabling flexible calculation algorithms that adhere to the Open/Closed Principle. The **Command pattern** (AddExpenseCommand, DeleteExpenseCommand, EditExpenseCommand, UndoManager) encapsulates operations as objects, providing undo/redo functionality and audit trails while coordinating between layers. The **Factory Method pattern** (ReportFactory creating PersonalReportGenerator, GroupReportGenerator) resides in the controller.report sub-package, allowing new report types to be added without modifying client code. For example, when a user adds a shared expense: (1) GroupPanel calls GroupController.addSharedExpense() (Facade), (2) GroupController uses a SplitStrategy to calculate shares, (3) an AddExpenseCommand is executed via UndoManager for undo capability, (4) the command invokes the appropriate DAO, and (5) the DAO uses DatabaseManager (Singleton) for persistence. This seamless integration demonstrates how the Layered Architecture organizes design patterns into a maintainable, extensible, and robust expense management system where data flows cleanly through layers (View → Controller → DAO → DatabaseManager) while maintaining flexibility and minimizing coupling.

---

## Visual Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│              PRESENTATION LAYER (View)                  │
│  LoginView, MainDashboard, GroupPanel,                  │
│  AddExpenseForm, BudgetSettingsDialog                   │
└────────────────────┬────────────────────────────────────┘
                     │ User Actions
                     ↓
┌─────────────────────────────────────────────────────────┐
│           BUSINESS LOGIC LAYER (Controller)             │
│  Facades: ExpenseManager, GroupController               │
│  Patterns: Strategy (Split), Command (Undo),            │
│           Factory (Reports)                             │
└────────────────────┬────────────────────────────────────┘
                     │ Data Operations
                     ↓
┌─────────────────────────────────────────────────────────┐
│            DATA ACCESS LAYER (DAO)                      │
│  DAO: ExpenseDAO, UserDAO, GroupDAO                     │
│  Singleton: DatabaseManager (caching)                   │
└────────────────────┬────────────────────────────────────┘
                     │ File I/O
                     ↓
┌─────────────────────────────────────────────────────────┐
│              DATA STORAGE (data/ directory)             │
│  users.dat, expenses_*.dat, groups.dat                  │
└─────────────────────────────────────────────────────────┘
```

---

**Word Count:** ~680 words (within typical assignment limits)  
**Date:** December 12, 2025  
**Project:** PayPilot - Personal & Group Expense Tracker
