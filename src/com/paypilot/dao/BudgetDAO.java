package com.paypilot.dao;

import com.paypilot.model.Budget;

/**
 * BudgetDAO - Data Access Object for Budget operations
 * Handles persistence of user budget settings
 */
public class BudgetDAO {
    private DatabaseManager dbManager;
    
    public BudgetDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    private String getBudgetKey(String username) {
        return "budgets_" + username;
    }
    
    /**
     * Load budget for a user
     * @param username User identifier
     * @return Budget object or null if not found
     */
    public Budget loadBudget(String username) {
        String key = getBudgetKey(username);
        Budget budget = dbManager.loadData(key, Budget.class);
        return budget;
    }
    
    /**
     * Save budget for a user
     * @param budget Budget to save
     */
    public void saveBudget(Budget budget) {
        String key = getBudgetKey(budget.getUsername());
        dbManager.saveData(key, budget);
    }
    
    /**
     * Check if user has budget configured
     * @param username User identifier
     * @return true if budget exists
     */
    public boolean hasBudget(String username) {
        String key = getBudgetKey(username);
        return dbManager.dataExists(key);
    }
    
    /**
     * Delete budget for a user
     * @param username User identifier
     */
    public void deleteBudget(String username) {
        String key = getBudgetKey(username);
        dbManager.deleteData(key);
    }
}
