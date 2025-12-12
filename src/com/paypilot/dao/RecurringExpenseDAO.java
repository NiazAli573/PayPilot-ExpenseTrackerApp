package com.paypilot.dao;

import com.paypilot.model.RecurringExpense;
import java.util.ArrayList;

/**
 * RecurringExpenseDAO - Data Access Object for recurring expenses
 * Handles persistence of recurring expense rules
 */
public class RecurringExpenseDAO {
    private DatabaseManager dbManager;
    
    public RecurringExpenseDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    private String getRecurringKey(String username) {
        return "recurring_" + username;
    }
    
    /**
     * Load recurring expenses for a user
     */
    @SuppressWarnings("unchecked")
    public ArrayList<RecurringExpense> loadRecurringExpenses(String username) {
        String key = getRecurringKey(username);
        ArrayList<RecurringExpense> expenses = dbManager.loadData(key, ArrayList.class);
        return expenses != null ? expenses : new ArrayList<>();
    }
    
    /**
     * Save recurring expenses for a user
     */
    public void saveRecurringExpenses(String username, ArrayList<RecurringExpense> expenses) {
        String key = getRecurringKey(username);
        dbManager.saveData(key, expenses);
    }
    
    /**
     * Add a new recurring expense
     */
    public void addRecurringExpense(String username, RecurringExpense expense) {
        ArrayList<RecurringExpense> expenses = loadRecurringExpenses(username);
        expenses.add(expense);
        saveRecurringExpenses(username, expenses);
    }
    
    /**
     * Update a recurring expense
     */
    public void updateRecurringExpense(String username, RecurringExpense expense) {
        ArrayList<RecurringExpense> expenses = loadRecurringExpenses(username);
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == expense.getId()) {
                expenses.set(i, expense);
                break;
            }
        }
        saveRecurringExpenses(username, expenses);
    }
    
    /**
     * Delete a recurring expense
     */
    public void deleteRecurringExpense(String username, int id) {
        ArrayList<RecurringExpense> expenses = loadRecurringExpenses(username);
        expenses.removeIf(e -> e.getId() == id);
        saveRecurringExpenses(username, expenses);
    }
}
