package com.paypilot.controller;

import com.paypilot.dao.BudgetDAO;
import com.paypilot.model.Budget;
import com.paypilot.model.Expense;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * BudgetManager - Controller for budget management
 * Handles budget CRUD operations and spending calculations
 */
public class BudgetManager {
    private BudgetDAO budgetDAO;
    
    public BudgetManager() {
        this.budgetDAO = new BudgetDAO();
    }
    
    /**
     * Get budget for user (creates default if not exists)
     */
    public Budget getBudget(String username) {
        Budget budget = budgetDAO.loadBudget(username);
        if (budget == null) {
            budget = new Budget(username, 0); // No budget set initially
        }
        return budget;
    }
    
    /**
     * Save or update budget
     */
    public void saveBudget(Budget budget) {
        budgetDAO.saveBudget(budget);
    }
    
    /**
     * Calculate current month's spending
     */
    public double calculateMonthlySpending(String username, ArrayList<Expense> expenses) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        return expenses.stream()
            .filter(e -> {
                LocalDate expenseDate = e.getDate();
                return expenseDate.getMonthValue() == currentMonth && 
                       expenseDate.getYear() == currentYear;
            })
            .mapToDouble(Expense::getAmount)
            .sum();
    }
    
    /**
     * Calculate category-wise spending for current month
     */
    public Map<String, Double> calculateCategorySpending(String username, ArrayList<Expense> expenses) {
        Map<String, Double> categoryTotals = new HashMap<>();
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        expenses.stream()
            .filter(e -> {
                LocalDate expenseDate = e.getDate();
                return expenseDate.getMonthValue() == currentMonth && 
                       expenseDate.getYear() == currentYear;
            })
            .forEach(e -> {
                String category = e.getCategory();
                categoryTotals.put(category, 
                    categoryTotals.getOrDefault(category, 0.0) + e.getAmount());
            });
        
        return categoryTotals;
    }
    
    /**
     * Get budget status with alert level
     */
    public BudgetStatus getBudgetStatus(String username, ArrayList<Expense> expenses) {
        Budget budget = getBudget(username);
        double spent = calculateMonthlySpending(username, expenses);
        Map<String, Double> categorySpending = calculateCategorySpending(username, expenses);
        
        return new BudgetStatus(budget, spent, categorySpending);
    }
    
    /**
     * Inner class to hold budget status information
     */
    public static class BudgetStatus {
        public final Budget budget;
        public final double totalSpent;
        public final Map<String, Double> categorySpending;
        public final double percentage;
        public final String alertLevel;
        
        public BudgetStatus(Budget budget, double totalSpent, Map<String, Double> categorySpending) {
            this.budget = budget;
            this.totalSpent = totalSpent;
            this.categorySpending = categorySpending;
            this.percentage = Budget.calculatePercentage(totalSpent, budget.getMonthlyBudget());
            this.alertLevel = Budget.getAlertLevel(percentage);
        }
        
        public Map<String, CategoryBudgetStatus> getCategoryStatuses() {
            Map<String, CategoryBudgetStatus> statuses = new HashMap<>();
            
            for (Map.Entry<String, Double> entry : budget.getCategoryBudgets().entrySet()) {
                String category = entry.getKey();
                double categoryBudget = entry.getValue();
                double spent = categorySpending.getOrDefault(category, 0.0);
                
                statuses.put(category, new CategoryBudgetStatus(
                    category, categoryBudget, spent
                ));
            }
            
            return statuses;
        }
    }
    
    /**
     * Category budget status
     */
    public static class CategoryBudgetStatus {
        public final String category;
        public final double budget;
        public final double spent;
        public final double percentage;
        public final String alertLevel;
        
        public CategoryBudgetStatus(String category, double budget, double spent) {
            this.category = category;
            this.budget = budget;
            this.spent = spent;
            this.percentage = Budget.calculatePercentage(spent, budget);
            this.alertLevel = Budget.getAlertLevel(percentage);
        }
    }
}
