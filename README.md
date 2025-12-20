# 💰 PayPilot - Personal Finance Manager

<div align="center">
  <h3>A modern, feature-rich expense tracking application built with Java Swing</h3>
  
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![Swing](https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white)
  ![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)
</div>

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Design Patterns](#design-patterns)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

**PayPilot** is a comprehensive personal finance management application that helps users track their expenses, set budgets, generate reports, and visualize spending patterns. Built with Java Swing, it features a modern, professional UI with real-time updates and persistent data storage.

### Key Highlights

✅ **Modern UI** - Professional design with Roboto fonts and intuitive navigation  
✅ **Real-Time Updates** - Instant synchronization across all views  
✅ **Database Persistence** - All data saved permanently using Java serialization  
✅ **Beautiful Reports** - Professional formatted expense reports with visual elements  
✅ **Budget Tracking** - Set and monitor category-based budgets with alerts  
✅ **Analytics Dashboard** - Visual charts showing spending trends and patterns  
✅ **Recurring Expenses** - Automate tracking of regular payments  
✅ **Split Expenses** - Share costs with friends and groups  

---

## ✨ Features

### 🏠 Core Functionality

- **Expense Management**
  - Add, edit, and delete expenses
  - Categorize expenses (Food, Transport, Entertainment, etc.)
  - Add descriptions and receipts
  - Real-time table updates

- **Budget Tracking**
  - Set monthly budgets per category
  - Visual progress bars
  - Color-coded alerts (Green/Yellow/Red)
  - Budget vs. actual spending comparison

- **Recurring Expenses**
  - Weekly, monthly, or custom intervals
  - Auto-generation on app startup
  - Active/inactive toggle
  - Last generated date tracking

### 📊 Analytics & Reporting

- **Visual Charts**
  - Pie chart - Category distribution
  - Bar chart - Top 5 categories
  - Line chart - 6-month spending trend
  - Quick stats overview

- **Report Generation**
  - Beautiful formatted text reports
  - Category breakdowns with percentages
  - Summary statistics
  - Export to .txt files

### 👥 Group Features

- **Split Expenses**
  - Equal split
  - Percentage-based split
  - Custom weighted split
  - Track who owes whom

- **Settlement Optimization**
  - Minimize number of transactions
  - Greedy algorithm implementation
  - Visual comparison of direct vs optimized settlements

### 🔄 Advanced Features

- **Undo/Redo** - Command pattern implementation
- **Real-Time Sync** - Automatic updates across all views
- **Smart Filters** - Filter by category and time period
- **Multi-User Support** - Secure authentication system
- **Data Persistence** - File-based database with caching

---

## 📸 Screenshots

### Dashboard
Modern dashboard with real-time statistics and expense tracking.
<img width="1357" height="714" alt="image" src="https://github.com/user-attachments/assets/703829ce-73ef-4315-8507-a480a8a4d84e" />


### Analytics
Visual charts showing spending patterns and category distribution.
<img width="1365" height="720" alt="image" src="https://github.com/user-attachments/assets/93ae1646-ce39-46a0-bd80-f3e8ac6ff7d3" />


### Budget Tracker
Monitor your spending against set budgets with color-coded progress bars.
<img width="1365" height="722" alt="image" src="https://github.com/user-attachments/assets/e03b4a63-d083-4ee5-a5f7-b39c92df6bd0" />


### Reports
Generate professional formatted reports with category breakdowns.

---

## 🏗️ Design Patterns

PayPilot implements multiple design patterns for maintainability and scalability:

### 1. **Singleton Pattern**
- `DatabaseManager` - Single database connection instance
- Thread-safe implementation with double-checked locking

### 2. **Command Pattern**
- `AddExpenseCommand`, `EditExpenseCommand`, `DeleteExpenseCommand`
- Enables undo/redo functionality
- Decouples UI from business logic

### 3. **DAO (Data Access Object) Pattern**
- `ExpenseDAO`, `UserDAO`, `BudgetDAO`, `GroupDAO`
- Abstracts data persistence layer
- Centralizes database operations

### 4. **Factory Pattern**
- `ReportFactory` - Creates appropriate report generators
- Supports multiple report types (Personal, Group)

### 5. **Strategy Pattern**
- `SplitStrategy` - Equal, Percentage, Weighted splits
- Flexible expense splitting algorithms

### 6. **MVC (Model-View-Controller) Pattern**
- Clear separation of concerns
- Models: `Expense`, `Budget`, `User`, `Group`
- Views: `MainDashboard`, `PersonalPanel`, `ChartsDashboardPanel`
- Controllers: `ExpenseManager`, `BudgetManager`, `AuthenticationController`

---

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Eclipse IDE or any Java IDE
- Git (for cloning the repository)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/NiazAli573/PayPilot-ExpenseTrackerApp.git
   cd PayPilot-ExpenseTrackerApp
   ```

2. **Open in Eclipse**
   - File → Open Projects from File System
   - Select the PayPilot directory
   - Wait for Eclipse to build the project

3. **Run the application**
   - Right-click `Main.java` in `src/com/paypilot/`
   - Run As → Java Application

### First Time Setup

1. Click "Sign Up" to create a new account
2. Enter username, email, and password
3. Login with your credentials
4. Start tracking expenses!

---

## 📖 Usage

### Adding an Expense

1. Click the **"+ Add Expense"** button
2. Fill in:
   - Category (Food, Transport, etc.)
   - Amount
   - Description (optional)
3. Optionally mark as recurring
4. Click **"Add"**
5. See real-time updates across all views!

### Setting Up Budgets

1. Click **"💰 Budgets"** in the sidebar
2. Click **"+ Add Budget"**
3. Select category and set monthly limit
4. Monitor progress in "My Expenses" view

### Viewing Analytics

1. Click **"📊 Analytics"** in the sidebar
2. View pie chart, bar chart, and line chart
3. Check Quick Stats for overview
4. Charts update automatically with new data

### Exporting Reports

1. Click **"📄 Export Report"** in the sidebar
2. Select "Personal Report"
3. Report saves as `expense_report_YYYY-MM-DD.txt`
4. Open the file to view beautifully formatted report

### Filtering Expenses

1. Use the category dropdown to filter by category
2. Use the period dropdown to filter by time:
   - Today
   - This Week
   - This Month
   - All Time
3. Click **"Reset"** to clear filters

---

## 📁 Project Structure

```
PayPilot/
├── src/
│   └── com/
│       └── paypilot/
│           ├── Main.java                    # Application entry point
│           ├── controller/                  # Business logic
│           │   ├── ExpenseManager.java
│           │   ├── BudgetManager.java
│           │   ├── AuthenticationController.java
│           │   ├── GroupController.java
│           │   ├── UndoManager.java
│           │   ├── RecurringExpenseManager.java
│           │   ├── AddExpenseCommand.java
│           │   ├── EditExpenseCommand.java
│           │   ├── DeleteExpenseCommand.java
│           │   ├── Command.java
│           │   ├── SettlementOptimizer.java
│           │   ├── report/
│           │   │   ├── ReportFactory.java
│           │   │   ├── ReportGenerator.java
│           │   │   ├── PersonalReportGenerator.java
│           │   │   └── GroupReportGenerator.java
│           │   └── strategy/
│           │       ├── SplitStrategy.java
│           │       ├── EqualSplitStrategy.java
│           │       ├── PercentageSplitStrategy.java
│           │       └── WeightedSplitStrategy.java
│           ├── dao/                         # Data access layer
│           │   ├── DatabaseManager.java
│           │   ├── ExpenseDAO.java
│           │   ├── UserDAO.java
│           │   ├── BudgetDAO.java
│           │   ├── GroupDAO.java
│           │   └── RecurringExpenseDAO.java
│           ├── model/                       # Data models
│           │   ├── Expense.java
│           │   ├── Budget.java
│           │   ├── User.java
│           │   ├── Group.java
│           │   ├── SharedExpense.java
│           │   ├── RecurringExpense.java
│           │   ├── Receipt.java
│           │   ├── SplitDetail.java
│           │   └── ExpenseCategory.java
│           └── view/                        # User interface
│               ├── ModernMainDashboard.java
│               ├── LoginView.java
│               ├── SignupView.java
│               ├── PersonalPanel.java
│               ├── ChartsDashboardPanel.java
│               ├── GroupPanel.java
│               ├── AddExpenseForm.java
│               ├── BudgetSettingsDialog.java
│               ├── BudgetProgressPanel.java
│               ├── UITheme.java
│               └── ... (other view components)
├── data/                                    # Data storage directory
│   ├── users.dat
│   ├── expenses_[username].dat
│   ├── budgets_[username].dat
│   └── ... (other data files)
├── docs/                                    # Documentation
│   ├── IMPROVEMENTS_README.md
│   ├── QUICK_START.md
│   ├── LATEST_UPDATES.md
│   ├── FINAL_SUMMARY.md
│   └── ... (other documentation)
├── .gitignore
├── README.md
└── LICENSE
```

---

## 🛠️ Technologies

- **Language:** Java 11+
- **UI Framework:** Java Swing
- **Persistence:** Java Serialization (File-based)
- **Architecture:** MVC Pattern
- **Design Patterns:** Singleton, Command, DAO, Factory, Strategy
- **Build Tool:** Eclipse IDE

---

## 💡 Key Technical Features

### Real-Time Updates
All views automatically refresh when data changes:
- Add/Edit/Delete expense → Instant UI update
- Statistics recalculate immediately
- Charts refresh automatically
- No manual refresh needed

### Data Persistence
- File-based serialization using ObjectOutputStream
- Thread-safe singleton database manager
- In-memory caching for performance
- Automatic save on every operation

### Professional UI
- Modern Roboto font throughout
- Card-based layout with rounded corners
- Professional color scheme (Blue, Green, Orange)
- Icons and emojis for visual appeal
- Responsive design

### Report Formatting
- Box-drawing characters for structure
- Category breakdowns with progress bars
- Emoji icons for sections
- Professional header and footer
- Summary statistics

---

## 📝 Future Enhancements

- [ ] Cloud synchronization
- [ ] Mobile companion app
- [ ] Dark mode support
- [ ] Multi-currency support
- [ ] Receipt OCR scanning
- [ ] Export to CSV/Excel
- [ ] Budget forecasting
- [ ] Category customization
- [ ] Expense search functionality
- [ ] Email report scheduling

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Niaz Ali**

- GitHub: [@NiazAli573](https://github.com/NiazAli573)
- Repository: [PayPilot-ExpenseTrackerApp](https://github.com/NiazAli573/PayPilot-ExpenseTrackerApp)

---

## 🙏 Acknowledgments

- Design inspiration from modern fintech applications
- Icons and emojis for visual enhancement
- Java Swing community for UI patterns
- Design pattern implementations from Gang of Four

---

## 📞 Support

If you encounter any issues or have questions:

1. Check the [documentation](docs/)
2. Review existing [issues](https://github.com/NiazAli573/PayPilot-ExpenseTrackerApp/issues)
3. Create a new issue with detailed information

---

<div align="center">
  <p>Made by Niaz Ali</p>
  <p>⭐ Star this repository if you find it helpful!</p>
</div>

---

## 🎉 Version History

### v4.2 (Current) - December 2025
- ✅ Changed all fonts to Roboto
- ✅ Fixed sidebar visibility
- ✅ Added professional logo
- ✅ Beautiful report formatting
- ✅ Icons throughout the app
- ✅ Quick Stats in Analytics
- ✅ Real-time updates enabled

### v4.1 - December 2025
- ✅ Fixed database persistence
- ✅ Professional UI redesign
- ✅ Enhanced filtering
- ✅ Improved navigation

### v4.0 - Previous
- Initial feature-complete version
- Budgets, recurring expenses, charts
- Group management, split expenses

---

**Happy Expense Tracking! 💰📊**
