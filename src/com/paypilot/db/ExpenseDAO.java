package com.paypilot.db;

import com.paypilot.model.Expense;
import java.util.ArrayList;

/**
 * ExpenseDAO - Data Access Object for Expense operations
 * SRP: Handles only expense persistence
 * Encapsulation: Hides storage implementation details
 */
public class ExpenseDAO {
    private DatabaseManager dbManager;
    
    public ExpenseDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    private String getUserExpenseKey(String username) {
        return "expenses_" + username;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<Expense> loadExpenses(String username) {
        String key = getUserExpenseKey(username);
        ArrayList<Expense> expenses = dbManager.loadData(key, ArrayList.class);
        return expenses != null ? expenses : new ArrayList<>();
    }
    
    public void saveExpenses(String username, ArrayList<Expense> expenses) {
        String key = getUserExpenseKey(username);
        dbManager.saveData(key, expenses);
    }
    
    public void addExpense(String username, Expense expense) {
        ArrayList<Expense> expenses = loadExpenses(username);
        expenses.add(expense);
        saveExpenses(username, expenses);
    }
    
    public void deleteExpense(String username, Expense expense) {
        ArrayList<Expense> expenses = loadExpenses(username);
        expenses.removeIf(e -> 
            e.getCategory().equals(expense.getCategory()) &&
            Math.abs(e.getAmount() - expense.getAmount()) < 0.01 &&
            e.getDate().equals(expense.getDate())
        );
        saveExpenses(username, expenses);
    }
    
    public void updateExpense(String username, Expense oldExpense, Expense newExpense) {
        ArrayList<Expense> expenses = loadExpenses(username);
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            if (e.getCategory().equals(oldExpense.getCategory()) &&
                Math.abs(e.getAmount() - oldExpense.getAmount()) < 0.01 &&
                e.getDate().equals(oldExpense.getDate())) {
                expenses.set(i, newExpense);
                break;
            }
        }
        saveExpenses(username, expenses);
    }
    
    public void clearAllExpenses(String username) {
        String key = getUserExpenseKey(username);
        dbManager.deleteData(key);
    }
}