# PayPilot - Quick Feature Guide

## 🚀 How to Use Each Feature

### 1️⃣ Getting Started

#### First Time User:
```
Launch Application → Click "Sign Up"
→ Enter: Username, Password (6+ chars), Email
→ Click "Create Account"
→ Login with new credentials
```

#### Returning User:
```
Launch Application → Enter credentials → Click "Sign In"
→ Main Dashboard opens
```

---

### 2️⃣ Adding a Simple Expense

```
Dashboard → Click "New Expense" button (top right)
↓
Add Expense Form Opens:
  - Category: "Groceries"
  - Amount: 85.50
  - Description: "Weekly shopping"
→ Click "Save"
↓
Expense appears in table immediately
Statistics update automatically
```

**Visual Result:**
- Table shows new row with expense details
- Total Expenses increases by $85.50
- Average recalculates

---

### 3️⃣ Splitting an Expense (Manual)

```
Dashboard → Click "New Expense"
↓
Enter expense details:
  - Category: "Restaurant"
  - Amount: 120.00
  - Description: "Team dinner"
↓
Click "Split Options" button
↓
Split Dialog Opens:
  Method 1 - Add manually:
    1. Enter "Alice" → Amount: 40.00 → Click "Add"
    2. Enter "Bob" → Amount: 40.00 → Click "Add"
    3. Enter "Charlie" → Amount: 40.00 → Click "Add"
  
  Method 2 - Equal split:
    1. Click "Split Equally"
    2. Enter "3" (number of people)
    3. System creates 3 equal shares automatically
↓
Click "Confirm" → Click "Save"
↓
Expense saved with split details
Type column shows "🔗 Split (Click to view)"
```

**To View Split Details:**
```
Click on the expense row
↓
Popup shows:
  - Total: $120.00
  - Alice: $40.00 (33.3%)
  - Bob: $40.00 (33.3%)
  - Charlie: $40.00 (33.3%)
```

---

### 4️⃣ Managing Groups

#### Create a Group:
```
Dashboard → Sidebar → Click "My Groups"
↓
Group Panel Opens
↓
Click "Create Group" button
→ Enter name: "Roommates 2025"
→ Click "Create"
↓
Group appears in left table
You are automatically first member
```

#### Add Members:
```
Select group from table
↓
Click "Add Member" button
→ Enter username: "john_doe"
→ Click "Add"
↓
Member added to group
Member count updates
```

#### Add Shared Expense:
```
Select group → Click "New Expense"
↓
Dialog opens:
  - Category: "Utilities"
  - Amount: 300.00
  - Description: "Monthly electricity"
  - Paid By: (dropdown with group members)
  - Split Strategy: Equal/Weighted/Percentage
  - Participants: (checkboxes for members)
→ Click "Save"
↓
Expense added to group
Balances recalculated
```

**View Balances:**
```
Select group from table
↓
Right panel shows:
  Transactions table (all expenses)
  Balance text area:
    - Alice: Is owed $50.00
    - Bob: Owes $25.00
    - Carol: Owes $25.00
    - Dave: Settled
```

---

### 5️⃣ Filtering & Sorting

#### Filter by Category:
```
Dashboard → Category dropdown (top left)
→ Select "Food"
↓
Table shows only food expenses
Statistics recalculate for filtered view
```

#### Sort by Amount:
```
Dashboard → Click "Sort Amount" button
↓
Expenses sorted highest to lowest
```

#### Reset:
```
Click "Reset" button
↓
All expenses visible again
```

---

### 6️⃣ Editing & Deleting

#### Edit Expense:
```
Click on expense row to select it
→ Click "Edit Selected" button
↓
Form opens with existing data pre-filled
→ Modify any field
→ Click "Save"
↓
Expense updated in table
```

#### Delete Expense:
```
Select expense row
→ Click "Delete" button
↓
Confirmation dialog: "Delete this expense?"
→ Click "Yes"
↓
Expense removed
Statistics update
```

---

### 7️⃣ Generating Reports

#### Personal Report:
```
Dashboard → Sidebar → Click "Reports"
↓
File dialog opens
→ Choose save location
→ Enter filename: "January_2025_Report"
→ Click "Save"
↓
Text file created with:
  ══════════════════════════════════════
         PERSONAL EXPENSE REPORT
  ══════════════════════════════════════
  
  User: john_doe
  Generated: Dec 10, 2025 14:30
  Total Expenses: 25
  
  SUMMARY STATISTICS
  ──────────────────────────────────────
  Total Amount:    $2,450.00
  Average Expense: $98.00
  Maximum Expense: $300.00
  
  DETAILED EXPENSES
  ──────────────────────────────────────
  [List of all expenses with details]
```

#### Group Report:
```
My Groups → Select group → Click "Export Report"
↓
Report saved to: expense_report_2025-12-10.txt
Contains:
  - Group info
  - Member list
  - All transactions
  - Balance summary
```

---

### 8️⃣ Undo Operations

#### Using Undo:
```
Scenario: Accidentally deleted expense
↓
Press Ctrl+Z (or use undo button if implemented)
↓
Last operation reversed
Expense restored
```

**Note:** Current UI doesn't have visible Undo/Redo buttons, but the system tracks all operations in `UndoManager` for potential keyboard shortcuts or menu items.

---

## 🎯 Real-World Usage Scenarios

### Scenario A: Daily Personal Tracking
```
Morning:
  - Add expense: Coffee $5.00
  
Lunch:
  - Add expense: Lunch $12.50
  
Evening:
  - Add expense: Groceries $85.00
  - Add expense: Gas $45.00
  
End of Day:
  - View statistics: Total spent today
  - Filter by category to see spending patterns
```

### Scenario B: Weekend Trip with Friends
```
Before Trip:
  1. Create group "Lake Trip 2025"
  2. Add members: Alice, Bob, Carol, Dave
  
During Trip:
  Day 1:
    - Alice pays gas: $60 → Add shared expense
    - Bob pays hotel: $200 → Add shared expense
  
  Day 2:
    - Carol pays breakfast: $40 → Add shared expense
    - Dave pays activities: $120 → Add shared expense
  
After Trip:
  1. View group balances
  2. Export report
  3. Share with group
  4. Settle up based on balances
```

### Scenario C: Monthly Budget Review
```
End of Month:
  1. Generate personal report
  2. Review by category:
     - Filter "Food" → Check if under budget
     - Filter "Entertainment" → See trends
     - Filter "Transport" → Calculate commute costs
  3. Use insights for next month's planning
```

### Scenario D: Roommate Expense Management
```
Monthly:
  1. Group: "Apartment 4B"
  2. Members: You, Roommate1, Roommate2
  
  Add shared expenses:
    - Rent: $1,800 (equal split)
    - Utilities: $200 (equal split)
    - Internet: $80 (equal split)
    - Groceries: Various (whoever shops)
  
  End of Month:
    - Check balances
    - One person collects/pays differences
    - Export report for records
```

---

## 💡 Pro Tips

### Tip 1: Quick Split Details
```
Instead of opening edit dialog:
→ Just CLICK the split expense row
→ Instant popup with breakdown
→ No need to edit to view details
```

### Tip 2: Category Naming
```
Use consistent categories:
✅ "Food", "Transport", "Entertainment"
❌ "food", "FOOD", "Food & Drinks"

Why? Easier filtering and better reports
```

### Tip 3: Descriptive Details
```
Good: "Team lunch at Thai Basil"
Bad: "Lunch"

Why? Easier to remember when reviewing later
```

### Tip 4: Regular Reports
```
Export monthly reports for:
  - Tax records
  - Budget tracking
  - Historical reference
  - Sharing with accountant
```

### Tip 5: Group Settlement
```
At end of shared period:
  1. Export group report
  2. Check balances
  3. Use apps like Venmo/PayPal for settlement
  4. Keep report as proof
```

---

## 🔍 Visual Indicators Guide

### Table Indicators:
| Display | Meaning |
|---------|---------|
| `🔗 Split (Click to view)` | Clickable split expense |
| Blue text | Interactive element |
| Green amount | Positive/normal expense |
| Red text | Warning/error |

### Status Messages:
| Message | Meaning |
|---------|---------|
| "Is owed $50" | Person paid more than their share |
| "Owes $50" | Person needs to pay this amount |
| "Settled" | Person's balance is zero |

### Button States:
| Button Text | Meaning |
|-------------|---------|
| "Split Options" | No split active |
| "Split Active (3)" | Split configured with 3 people |

---

## 🎨 UI Navigation Map

```
Main Dashboard
│
├─ Sidebar
│  ├─ Dashboard (current view)
│  ├─ My Groups → Group Panel
│  ├─ Reports → Export dialog
│  └─ Logout → Back to login
│
├─ Top Section (Statistics)
│  ├─ Total Expenses card
│  ├─ Average Spend card
│  └─ Highest Spend card
│
├─ Middle Section (Controls)
│  ├─ Filter dropdown
│  ├─ Reset button
│  ├─ Sort button
│  └─ Action buttons (Edit/Delete)
│
└─ Bottom Section (Table)
   └─ Click row → Split details popup (if split)
```

---

## ⚠️ Important Notes

1. **Data Persistence:** All data saved in `data/` folder
   - Don't delete this folder
   - Backup before reinstalling

2. **Split Expenses:** Must add amount first before clicking "Split Options"

3. **Group Members:** Must be registered users
   - Username must exist in system
   - Case-sensitive

4. **Balances:** Recalculated automatically
   - Based on who paid vs. who owes
   - Updates immediately after adding expense

5. **Reports:** Text files (.txt)
   - Open with any text editor
   - Format preserved for printing

---

## 📞 Common Issues & Solutions

### Issue 1: "Cannot open split details"
**Solution:** Click directly on row with "🔗 Split" text

### Issue 2: "Group member not found"
**Solution:** User must signup first before adding to group

### Issue 3: "Split doesn't match total"
**Solution:** Split dialog shows remaining amount - add until zero

### Issue 4: "Lost expense data"
**Solution:** Check `data/` folder - restore .dat files from backup

### Issue 5: "Can't edit split expense"
**Solution:** Click Edit → Re-configure splits if needed

---

**Quick Reference Card:**

| Action | Steps |
|--------|-------|
| Add Expense | New Expense → Fill form → Save |
| Split Expense | Add → Split Options → Configure → Confirm |
| View Split | Click split expense row |
| Create Group | My Groups → Create Group |
| Add Member | Select group → Add Member |
| View Balance | Select group → Check right panel |
| Export Report | Reports button → Choose location |
| Filter | Category dropdown → Select |
| Edit | Select row → Edit Selected |
| Delete | Select row → Delete → Confirm |

---

**Remember:** PayPilot is designed to make expense tracking effortless. The more you use it, the better insights you'll get into your spending habits!

