package com.paypilot.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * RecurringExpense - Model for expenses that repeat automatically
 * Supports weekly, monthly, and custom interval recurrence
 */
public class RecurringExpense implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum RecurrenceType {
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        CUSTOM("Custom Days");
        
        private final String displayName;
        
        RecurrenceType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private int id;
    private String username;
    private String category;
    private double amount;
    private String description;
    private RecurrenceType recurrenceType;
    private int customDays; // For custom recurrence
    private LocalDate startDate;
    private LocalDate lastGenerated;
    private boolean active;
    
    public RecurringExpense(String username, String category, double amount, 
                           String description, RecurrenceType recurrenceType, 
                           int customDays, LocalDate startDate) {
        this.username = username;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.recurrenceType = recurrenceType;
        this.customDays = customDays;
        this.startDate = startDate;
        this.lastGenerated = startDate;
        this.active = true;
        this.id = generateId();
    }
    
    private int generateId() {
        return (username + category + startDate.toString()).hashCode();
    }
    
    /**
     * Calculate next occurrence date based on recurrence type
     */
    public LocalDate getNextOccurrence() {
        switch (recurrenceType) {
            case WEEKLY:
                return lastGenerated.plusWeeks(1);
            case MONTHLY:
                return lastGenerated.plusMonths(1);
            case CUSTOM:
                return lastGenerated.plusDays(customDays);
            default:
                return lastGenerated.plusDays(1);
        }
    }
    
    /**
     * Check if expense should be generated today
     */
    public boolean shouldGenerateToday() {
        if (!active) return false;
        LocalDate today = LocalDate.now();
        LocalDate nextDate = getNextOccurrence();
        return !today.isBefore(nextDate);
    }
    
    /**
     * Create expense instance from this recurring template
     */
    public Expense createExpenseInstance(LocalDate date) {
        Expense expense = new Expense(category, amount, description, date, username);
        return expense;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }
    
    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }
    
    public int getCustomDays() {
        return customDays;
    }
    
    public void setCustomDays(int customDays) {
        this.customDays = customDays;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getLastGenerated() {
        return lastGenerated;
    }
    
    public void setLastGenerated(LocalDate lastGenerated) {
        this.lastGenerated = lastGenerated;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}
