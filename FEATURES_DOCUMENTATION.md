# PayPilot - Feature Documentation

## Project Overview
PayPilot is a comprehensive **Personal & Group Expense Management System** built with Java Swing. It helps users track personal expenses, manage shared group expenses, and generate detailed financial reports.

---

## 🎯 Core Features

### 1. **User Authentication System**
**Location:** `AuthenticationController.java`, `LoginView.java`, `SignupView.java`

#### How it Works:
- **Login:** Users enter username and password
- **Password Security:** Passwords are hashed using SHA-256 encryption before storage
- **Signup:** New users can register with username, password (min 6 chars), and email
- **Validation:** Input validation for empty fields and password strength
- **Storage:** User credentials stored in `data/users.dat` using file-based serialization

#### User Flow:
1. Application launches → LoginView appears
2. User can either:
   - **Login** with existing credentials
   - **Sign Up** to create new account
3. After successful authentication → MainDashboard opens

---

### 2. **Personal Expense Management** 
**Location:** `PersonalPanel.java`, `ExpenseManager.java`, `AddExpenseForm.java`

#### Features:

##### A. Add Expense
- **Fields:** Category, Amount, Description, Date (auto-set to today)
- **Split Option:** Can split expense among multiple people
- **Access:** Click "New Expense" button in dashboard

##### B. View Expenses
- **Table Display:** Shows all expenses in a modern table
- **Columns:** Date, Category, Amount, Description, Type (Personal/Split)
- **Statistics Dashboard:** 
  - Total Expenses
  - Average Spend
  - Highest Spend

##### C. Edit Expense
- Select any expense row → Click "Edit Selected" button
- Opens AddExpenseForm with pre-filled data
- Update any field and save

##### D. Delete Expense
- Select expense → Click "Delete" button
- Confirmation dialog appears
- Expense is removed from system

##### E. Filter & Sort
- **Filter by Category:** Dropdown to show expenses from specific category only
- **Sort by Amount:** Descending order (highest first)
- **Reset Filter:** Return to viewing all expenses

##### F. Split Expense Details Popup ⭐ **NEW**
- **How it Works:** Click on any row with "🔗 Split" in Type column
- **Shows:**
  - Expense details (category, date, total amount)
  - Breakdown table showing each participant's:
    - Name
    - Amount to pay
    - Percentage of total
- **Visual Feedback:**
  - Split expenses show "🔗 Split (Click to view)" in blue
  - Cursor changes to hand pointer on hover
  - Professional styled popup dialog

---

### 3. **Split Expense Feature**
**Location:** `AddExpenseForm.java`, `SplitExpenseDialog.java`, `SplitStrategy` classes

#### How it Works:

##### A. Manual Split
1. In AddExpenseForm, enter total amount
2. Click "Split Options" button
3. SplitExpenseDialog opens
4. Add each person:
   - Enter name
   - Enter their share amount
   - Click "Add"
5. System tracks:
   - Remaining amount to allocate
   - Percentage each person pays
6. Click "Confirm" to save split details

##### B. Equal Split
1. In SplitExpenseDialog
2. Click "Split Equally" button
3. Enter number of people
4. System automatically:
   - Divides total amount by number of people
   - Creates person1, person2, etc.
   - Assigns equal shares

##### C. Strategy Pattern Implementation
Three splitting strategies available:
- **Equal Split:** Divides amount equally
- **Weighted Split:** Based on custom weights
- **Percentage Split:** Based on percentage shares

#### Visual Features:
- Split button shows "Split Active (X)" when splits exist
- Expense table shows "Split" in Type column
- Click row to see detailed breakdown (NEW)

---

### 4. **Group Expense Management**
**Location:** `GroupPanel.java`, `GroupController.java`

#### Features:

##### A. Create Group
- Click "My Groups" in sidebar
- Enter group name
- Creator is automatically first member
- Groups stored in `data/groups.dat`

##### B. Manage Members
- **Add Members:** 
  - Select group from list
  - Click "Add Member" button
  - Enter username
  - User added to group
- **Remove Members:** Select and click "Remove Member"

##### C. Add Shared Expense
- Select active group
- Click "New Expense" button
- Enter:
  - Category (e.g., "Dinner", "Trip")
  - Amount
  - Description
  - Who paid (from group members)
- Select split strategy:
  - Equal split among all members
  - Weighted split
  - Percentage split

##### D. View Balances
- **Balance Calculation:** System tracks:
  - How much each member paid
  - How much each member owes
  - Net balance per member
- **Display:** Shows in text area:
  - "Is owed $X" - member paid more than share
  - "Owes $X" - member owes money
  - "Settled" - member's balance is zero

##### E. View Transactions
- Table shows all group expenses:
  - Category
  - Amount
  - Who Paid
  - Date
- Sorted by date (newest first)

---

### 5. **Report Generation**
**Location:** `ReportFactory.java`, `PersonalReportGenerator.java`, `GroupReportGenerator.java`

#### How it Works:

##### Personal Report
1. From MainDashboard, click "Reports" in sidebar
2. System generates comprehensive report including:
   - **Header:** User name, date, total expenses
   - **Summary Statistics:**
     - Total amount spent
     - Average expense
     - Maximum expense
   - **Detailed Expenses:** Each expense with:
     - Date, category, amount, description
     - Split details if applicable
3. Choose save location
4. Report saved as `.txt` file

##### Group Report
1. In GroupPanel, select group
2. Click "Export Report" button
3. Report includes:
   - **Group Info:** Name, creator, members
   - **Summary:** Total expenses, average, count
   - **Member Balances:** Who owes whom
   - **Detailed Transactions:** All group expenses
4. Saved to `expense_report_[date].txt`

#### Report Format:
- Professional ASCII formatting
- Clear sections with separators
- Human-readable layout
- Includes all calculations

---

### 6. **Undo/Redo System** ⚡ (Command Pattern)
**Location:** `UndoManager.java`, Command classes

#### How it Works:
- **Command Pattern:** Every expense operation wrapped as a command
- **Commands Available:**
  - AddExpenseCommand
  - EditExpenseCommand
  - DeleteExpenseCommand
- **Features:**
  - Maintains history stack (default: 50 operations)
  - Can undo any operation
  - Can redo undone operations
  - Clears redo stack when new command executed

#### Usage:
```java
// Add expense with undo capability
Command cmd = new AddExpenseCommand(expenseManager, username, expense);
undoManager.executeCommand(cmd);

// Undo last action
undoManager.undo();

// Redo
undoManager.redo();
```

---

## 🏗️ Design Patterns Used

### 1. **Singleton Pattern**
- **Where:** `DatabaseManager` (both dao and db packages)
- **Why:** Single instance for file I/O operations
- **Benefit:** Thread-safe, centralized data access

### 2. **Command Pattern**
- **Where:** `Command` interface, `AddExpenseCommand`, `EditExpenseCommand`, `DeleteExpenseCommand`
- **Why:** Encapsulate operations for undo/redo
- **Benefit:** Decouples UI from business logic, supports operation history

### 3. **Strategy Pattern**
- **Where:** `SplitStrategy` interface with 3 implementations
- **Why:** Multiple algorithms for splitting expenses
- **Benefit:** Runtime algorithm selection, easy to extend

### 4. **Factory Pattern**
- **Where:** `ReportFactory`
- **Why:** Create different report types
- **Benefit:** Centralized object creation, follows Open/Closed Principle

### 5. **DAO Pattern**
- **Where:** `ExpenseDAO`, `GroupDAO`, `UserDAO`
- **Why:** Abstract data persistence
- **Benefit:** Separates business logic from data access

### 6. **MVC Architecture**
- **Model:** `Expense`, `User`, `Group`, `SharedExpense`, `SplitDetail`
- **View:** All classes in `view` package
- **Controller:** All classes in `controller` package

---

## 💾 Data Persistence

### Storage Mechanism:
- **Type:** File-based serialization
- **Location:** `data/` folder
- **Files:**
  - `users.dat` - User credentials
  - `groups.dat` - Group information
  - `shared_expenses.dat` - Group expenses
  - `expenses_[username].dat` - Personal expenses per user

### Why File-Based?
- No database setup required
- Portable across systems
- Simple to understand and maintain
- Suitable for desktop application

---

## 🎨 User Interface Features

### Modern Design System (`UITheme.java`)
- **Color Palette:**
  - Primary: Indigo (#6366F1)
  - Success: Green (#10B981)
  - Danger: Red (#EF4444)
  - Background: Light Grey (#F3F4F6)
  - Sidebar: Dark Blue (#111827)

### UI Components:
1. **RoundedPanel:** Card-style containers with rounded corners
2. **ModernButton:** Hover effects, rounded corners, hand cursor
3. **Styled Tables:** Tall rows, subtle borders, highlighted selection
4. **Consistent Spacing:** Using EmptyBorder for padding
5. **Professional Typography:** Multiple font weights and sizes

### Navigation:
- **Sidebar Menu:**
  - Dashboard (main view)
  - My Groups (group management)
  - Reports (export data)
  - Logout
- **Breadcrumb-style:** Always shows current user

---

## 📊 Statistics & Analytics

### Real-time Calculations:
1. **Total Expenses:** Sum of all expenses
2. **Average Expense:** Total ÷ Count
3. **Maximum Expense:** Highest single expense
4. **Category Breakdown:** (in reports)
5. **Group Balances:** Complex calculation of who owes whom

### Visual Indicators:
- **Color-coded amounts:** Green for expenses, Red for warnings
- **Split indicators:** Blue "🔗" icon for split expenses
- **Balance status:** Text shows "Owes", "Is owed", or "Settled"

---

## 🔄 Workflow Examples

### Scenario 1: Track Daily Expenses
1. Login to account
2. Click "New Expense" button
3. Enter: Category="Food", Amount=25.50, Description="Lunch"
4. Click "Save"
5. Expense appears in table immediately
6. Statistics update automatically

### Scenario 2: Split Restaurant Bill
1. Click "New Expense"
2. Enter: Category="Dining", Amount=120.00
3. Click "Split Options"
4. Click "Split Equally", enter 4 people
5. Click "Confirm"
6. Save expense
7. Click on expense row to see each person owes $30.00

### Scenario 3: Manage Trip Expenses
1. Click "My Groups" in sidebar
2. Create group "Vegas Trip 2025"
3. Add members: Alice, Bob, Carol
4. Someone pays hotel: Add shared expense $300, paid by Alice
5. Someone pays dinner: Add expense $150, paid by Bob
6. View balances to see who owes whom
7. Export report at end of trip

### Scenario 4: Monthly Financial Review
1. Filter expenses by category "Food"
2. Check statistics to see total food spending
3. Click "Reports" → Generate personal report
4. Review detailed breakdown
5. Make budget adjustments

---

## 🔧 Technical Specifications

### Technologies:
- **Language:** Java 8+
- **GUI Framework:** Swing
- **Persistence:** Java Serialization
- **Architecture:** MVC with Design Patterns
- **Thread Safety:** Synchronized database operations

### Key Classes Count:
- **Models:** 6 classes
- **Views:** 10+ dialog/panel classes
- **Controllers:** 9 classes
- **DAO Layer:** 3 classes
- **Strategy Implementations:** 3 classes
- **Report Generators:** 3 classes

### Data Flow:
```
User Input (View) 
    → Controller validates & processes
    → Manager/DAO persists data
    → Model objects updated
    → View refreshed with new data
```

---

## 🚀 Future Enhancements Possible

Based on current architecture, easy to add:
1. ✅ Budget limits and alerts
2. ✅ Expense categories with icons
3. ✅ Date range filtering
4. ✅ Charts and graphs (using JFreeChart)
5. ✅ Export to CSV/Excel
6. ✅ Multi-currency support
7. ✅ Recurring expenses
8. ✅ Receipt attachment (image storage)
9. ✅ Email notifications for balances
10. ✅ Database migration (from files to SQL)

---

## 📝 Summary

**PayPilot** is a feature-rich expense management system that goes beyond simple tracking:

### ✨ Core Strengths:
1. **Dual Mode:** Personal + Group expense management
2. **Smart Splitting:** Multiple algorithms with visual feedback
3. **Professional Reports:** Comprehensive financial documentation
4. **Modern UI:** Clean, intuitive, responsive design
5. **Solid Architecture:** Design patterns ensure maintainability
6. **Undo/Redo:** Mistake-proof operation
7. **Balance Tracking:** Complex group settlement calculations
8. **Interactive Details:** Click to see split breakdowns (NEW)

### 📈 Complexity Level:
- **Basic Features:** File I/O, CRUD operations
- **Intermediate:** Design patterns, MVC architecture
- **Advanced:** Strategy pattern, Command pattern, Balance calculations

This project demonstrates **production-quality code structure** suitable for:
- Academic portfolios
- Job interviews
- Real-world usage
- Further commercial development

---

**Last Updated:** December 10, 2025  
**Version:** 2.0 (with Split Details Popup feature)
