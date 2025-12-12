# PayPilot - Architecture Implementation Examples

## Supplementary Documentation for Section 4

This document provides concrete code examples demonstrating how the Layered Architecture is implemented in PayPilot and how design patterns integrate within each layer.

---

## Layer Implementation Examples

### 1. Presentation Layer (View Package)

**Responsibility:** Handle user interactions, display data, and delegate business logic to controllers.

**Example: LoginView.java**
```java
public class LoginView extends JDialog {
    private AuthenticationController authController; // Reference to Business Logic Layer
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Delegate authentication to Business Logic Layer
        if (authController.authenticate(username, password)) {
            // UI handling only - no business logic here
            dispose();
            new MainDashboard(username).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        }
    }
}
```

**Key Observations:**
- View only handles UI rendering and user input collection
- Business logic (authentication) is delegated to AuthenticationController
- No direct database access - maintains layer separation

---

### 2. Business Logic Layer (Controller Package)

**Responsibility:** Implement business rules, coordinate between layers, apply design patterns.

#### Example 2A: Facade Pattern (ExpenseManager.java)

```java
public class ExpenseManager {
    private ExpenseDAO expenseDAO; // Reference to Data Access Layer
    
    // Simplified interface for Presentation Layer
    public void addExpense(Expense expense) {
        if (expense != null && expense.getUsername() != null) {
            expenseDAO.addExpense(expense.getUsername(), expense);
        }
    }
    
    public ArrayList<Expense> getAllExpenses(String username) {
        return expenseDAO.loadExpenses(username);
    }
    
    // Business logic for analytics
    public Map<String, Double> getCategoryBreakdown(String username) {
        ArrayList<Expense> expenses = expenseDAO.loadExpenses(username);
        // Complex calculation logic encapsulated here
        return expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.summingDouble(Expense::getAmount)
            ));
    }
}
```

**Benefits:**
- Presentation Layer doesn't need to know about ExpenseDAO implementation
- Complex operations like category breakdown are hidden behind simple method calls
- Easy to add validation, logging, or caching without affecting UI

#### Example 2B: Strategy Pattern (GroupController.java)

```java
public class GroupController {
    private Map<String, SplitStrategy> strategies;
    
    private void initializeStrategies() {
        strategies = new HashMap<>();
        strategies.put("EQUAL", new EqualSplitStrategy());
        strategies.put("WEIGHTED", new WeightedSplitStrategy());
        strategies.put("PERCENTAGE", new PercentageSplitStrategy());
    }
    
    public List<SplitDetail> splitExpense(
            double amount, 
            List<String> members, 
            String strategyType,
            Map<String, Double> parameters) {
        
        // Runtime strategy selection
        SplitStrategy strategy = strategies.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy: " + strategyType);
        }
        
        // Delegate calculation to selected strategy
        return strategy.split(amount, members, parameters);
    }
}
```

**Benefits:**
- Open/Closed Principle: New strategies can be added without modifying GroupController
- Runtime flexibility: Users can switch splitting methods dynamically
- Testability: Each strategy can be unit tested independently

#### Example 2C: Command Pattern (AddExpenseCommand.java)

```java
public class AddExpenseCommand implements Command {
    private ExpenseManager expenseManager;
    private String username;
    private Expense expense;
    
    @Override
    public void execute() {
        expenseManager.addExpense(username, expense);
    }
    
    @Override
    public void undo() {
        expenseManager.deleteExpense(username, expense);
    }
}

// UndoManager coordinates commands
public class UndoManager {
    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();
    
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo history on new action
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }
}
```

**Benefits:**
- Undo/Redo functionality without complex state management
- Audit trail: All operations are logged as command objects
- Transactional operations: Commands can be grouped for atomic execution

#### Example 2D: Factory Method Pattern (ReportFactory.java)

```java
public class ReportFactory {
    public static ReportGenerator createReportGenerator(String reportType) {
        switch (reportType.toUpperCase()) {
            case "PERSONAL":
                return new PersonalReportGenerator();
            case "GROUP":
                return new GroupReportGenerator();
            case "BUDGET":
                return new BudgetReportGenerator();
            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }
    }
}

// Usage in controller
public String generateReport(String username, String reportType) {
    ReportGenerator generator = ReportFactory.createReportGenerator(reportType);
    return generator.generate(username);
}
```

**Benefits:**
- Centralized creation logic
- Easy to add new report types (e.g., TaxReportGenerator)
- Client code (UI) doesn't need to know about concrete report classes

---

### 3. Data Access Layer (DAO Package)

**Responsibility:** Handle data persistence, implement DAO pattern, manage DatabaseManager singleton.

#### Example 3A: Singleton Pattern (DatabaseManager.java)

```java
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final Object lock = new Object();
    private Map<String, Object> cache;
    
    private DatabaseManager() {
        cache = new HashMap<>();
        ensureDataDirectory();
    }
    
    // Thread-safe Singleton with double-checked locking
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }
    
    public synchronized void saveData(String key, Object data) {
        cache.put(key, data); // In-memory cache
        persistToDisk(key, data); // File persistence
    }
}
```

**Benefits:**
- Single point of data access across entire application
- Built-in caching improves performance
- Thread-safe operations prevent data corruption
- Centralized error handling and logging

#### Example 3B: DAO Pattern (ExpenseDAO.java)

```java
public class ExpenseDAO {
    private DatabaseManager dbManager;
    
    public ExpenseDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public void addExpense(String username, Expense expense) {
        ArrayList<Expense> expenses = loadExpenses(username);
        expenses.add(expense);
        dbManager.saveData("expenses_" + username, expenses);
    }
    
    public ArrayList<Expense> loadExpenses(String username) {
        return dbManager.loadData("expenses_" + username, ArrayList.class);
    }
    
    // DAO handles all persistence details
    public void deleteExpense(String username, Expense expense) {
        ArrayList<Expense> expenses = loadExpenses(username);
        expenses.removeIf(e -> e.getId().equals(expense.getId()));
        dbManager.saveData("expenses_" + username, expenses);
    }
}
```

**Benefits:**
- Business Logic Layer never touches file I/O directly
- Easy to switch from file storage to SQL database by updating DAO only
- Consistent data access patterns across all entities

---

## Data Flow Example: Adding a Shared Expense

This example demonstrates how all layers and patterns work together:

### User Action Flow:

```
1. PRESENTATION LAYER (GroupPanel.java)
   ↓
   User clicks "Add Shared Expense"
   ↓
   Opens SharedExpenseDialog
   User enters: Amount=$100, Members=[Alice, Bob, Carol], Split=EQUAL
   ↓
   
2. BUSINESS LOGIC LAYER (GroupController.java - Facade)
   ↓
   groupController.addSharedExpense(groupName, sharedExpense)
   ↓
   
   2a. Strategy Pattern Applied:
   SplitStrategy strategy = strategies.get("EQUAL");
   List<SplitDetail> splits = strategy.split(100, [Alice, Bob, Carol], null);
   // Result: Alice=$33.33, Bob=$33.33, Carol=$33.34
   ↓
   
   2b. Command Pattern Applied:
   AddExpenseCommand command = new AddExpenseCommand(expenseManager, sharedExpense);
   undoManager.executeCommand(command);
   ↓
   
3. DATA ACCESS LAYER (GroupDAO.java)
   ↓
   groupDAO.addSharedExpense(groupName, sharedExpense);
   ↓
   
   3a. DAO uses Singleton:
   DatabaseManager dbManager = DatabaseManager.getInstance();
   ↓
   
   3b. Data Persistence:
   dbManager.saveData("shared_expenses_" + groupName, expensesList);
   ↓
   
4. DATA STORAGE
   ↓
   File written: data/shared_expenses_TripToEurope.dat
   ↓
   
5. RETURN TO PRESENTATION LAYER
   ↓
   GroupPanel refreshes expense list
   Displays success message to user
```

---

## Testing Strategy per Layer

### Presentation Layer Testing
```java
// Manual UI testing or use frameworks like AssertJ Swing
@Test
public void testLoginButtonEnablesOnInput() {
    LoginView view = new LoginView(mockController);
    view.usernameField.setText("testuser");
    view.passwordField.setText("password");
    assertTrue(view.loginButton.isEnabled());
}
```

### Business Logic Layer Testing
```java
@Test
public void testEqualSplitStrategy() {
    SplitStrategy strategy = new EqualSplitStrategy();
    List<String> members = Arrays.asList("Alice", "Bob", "Carol");
    
    List<SplitDetail> result = strategy.split(100.0, members, null);
    
    assertEquals(3, result.size());
    assertEquals(33.33, result.get(0).getAmount(), 0.01);
    assertEquals(33.33, result.get(1).getAmount(), 0.01);
    assertEquals(33.34, result.get(2).getAmount(), 0.01);
}

@Test
public void testUndoCommand() {
    ExpenseManager manager = new ExpenseManager();
    Expense expense = new Expense("Lunch", 15.0, "Food");
    
    AddExpenseCommand command = new AddExpenseCommand(manager, "user1", expense);
    command.execute();
    
    assertTrue(manager.getAllExpenses("user1").contains(expense));
    
    command.undo();
    assertFalse(manager.getAllExpenses("user1").contains(expense));
}
```

### Data Access Layer Testing
```java
@Test
public void testExpenseDAOAddAndRetrieve() {
    ExpenseDAO dao = new ExpenseDAO();
    Expense expense = new Expense("Coffee", 5.0, "Food");
    
    dao.addExpense("testuser", expense);
    ArrayList<Expense> expenses = dao.loadExpenses("testuser");
    
    assertTrue(expenses.contains(expense));
}
```

---

## Migration Path: From Desktop to Client-Server

The Layered Architecture enables future scalability:

### Current Desktop Architecture:
```
[Presentation Layer] → [Business Logic Layer] → [Data Access Layer] → [File Storage]
```

### Future Client-Server Architecture:
```
CLIENT:
[Presentation Layer] → [API Client Layer]
                            ↓ HTTP/REST
                            ↓
SERVER:
[API Controller Layer] → [Business Logic Layer] → [Data Access Layer] → [SQL Database]
```

**Migration Steps:**
1. Replace FileDAO implementations with JdbcDAO implementations
2. Create REST API endpoints wrapping existing controllers (ExpenseManager, GroupController)
3. Update Presentation Layer to call API endpoints instead of local controllers
4. Business Logic Layer remains unchanged - demonstrating architecture flexibility

---

## Performance Optimizations Enabled by Architecture

1. **Caching in DatabaseManager (Singleton):**
   - First access: Load from disk (slow)
   - Subsequent accesses: Retrieve from cache (fast)
   - Transparent to Business Logic Layer

2. **Lazy Loading in DAOs:**
   - Load data only when requested
   - Controllers don't need to manage data lifecycle

3. **Async Operations in Presentation Layer:**
   - UI remains responsive during long operations
   - Controllers handle business logic on background threads
   - Separation of concerns enables easy threading

---

## Conclusion

PayPilot's implementation demonstrates how a **Layered Architecture** creates a solid foundation for applying multiple design patterns cohesively. Each layer has clear responsibilities, patterns solve specific problems within their appropriate layers, and the entire system remains maintainable, testable, and extensible.

The architecture's true strength is revealed in:
- **Parallel development:** UI designers, business logic developers, and database specialists can work independently
- **Incremental enhancement:** New features (e.g., currency conversion, tax reports) can be added without major refactoring
- **Risk mitigation:** Bugs are isolated to specific layers, simplifying debugging
- **Future-proofing:** The system can evolve from desktop to web or mobile with minimal architectural changes

---

**Document Version:** 1.0  
**Date:** December 12, 2025  
**Project:** PayPilot - Personal & Group Expense Tracker
