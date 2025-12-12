package com.paypilot.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Budget Model - Represents user-defined spending limits
 * Supports overall monthly budget and category-wise budgets
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private double monthlyBudget;
    private Map<String, Double> categoryBudgets; // Category -> Budget amount
    
    public Budget(String username, double monthlyBudget) {
        this.username = username;
        this.monthlyBudget = monthlyBudget;
        this.categoryBudgets = new HashMap<>();
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public double getMonthlyBudget() {
        return monthlyBudget;
    }
    
    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }
    
    public Map<String, Double> getCategoryBudgets() {
        return categoryBudgets;
    }
    
    public void setCategoryBudgets(Map<String, Double> categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }
    
    public void setCategoryBudget(String category, double amount) {
        this.categoryBudgets.put(category, amount);
    }
    
    public Double getCategoryBudget(String category) {
        return categoryBudgets.get(category);
    }
    
    public void removeCategoryBudget(String category) {
        categoryBudgets.remove(category);
    }
    
    /**
     * Calculate budget status percentage
     * @param spent Amount already spent
     * @param budget Budget limit
     * @return Percentage (0-100+)
     */
    public static double calculatePercentage(double spent, double budget) {
        if (budget <= 0) return 0;
        return (spent / budget) * 100.0;
    }
    
    /**
     * Get alert level based on percentage
     * @param percentage Spending percentage
     * @return "SAFE", "WARNING", "EXCEEDED"
     */
    public static String getAlertLevel(double percentage) {
        if (percentage >= 100) return "EXCEEDED";
        if (percentage >= 80) return "WARNING";
        return "SAFE";
    }
}
