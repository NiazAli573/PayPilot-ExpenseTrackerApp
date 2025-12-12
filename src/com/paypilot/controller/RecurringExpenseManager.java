package com.paypilot.controller;

import com.paypilot.dao.RecurringExpenseDAO;
import com.paypilot.model.Expense;
import com.paypilot.model.RecurringExpense;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * RecurringExpenseManager - Controller for recurring expense operations
 * Handles auto-generation of recurring expenses
 */
public class RecurringExpenseManager {
    private RecurringExpenseDAO recurringDAO;
    private ExpenseManager expenseManager;
    
    public RecurringExpenseManager(ExpenseManager expenseManager) {
        this.recurringDAO = new RecurringExpenseDAO();
        this.expenseManager = expenseManager;
    }
    
    /**
     * Get all recurring expenses for user
     */
    public ArrayList<RecurringExpense> getRecurringExpenses(String username) {
        return recurringDAO.loadRecurringExpenses(username);
    }
    
    /**
     * Add new recurring expense
     */
    public void addRecurringExpense(RecurringExpense expense) {
        recurringDAO.addRecurringExpense(expense.getUsername(), expense);
    }
    
    /**
     * Update recurring expense
     */
    public void updateRecurringExpense(RecurringExpense expense) {
        recurringDAO.updateRecurringExpense(expense.getUsername(), expense);
    }
    
    /**
     * Delete recurring expense
     */
    public void deleteRecurringExpense(String username, int id) {
        recurringDAO.deleteRecurringExpense(username, id);
    }
    
    /**
     * Toggle active status
     */
    public void toggleRecurringExpense(String username, int id) {
        ArrayList<RecurringExpense> expenses = recurringDAO.loadRecurringExpenses(username);
        for (RecurringExpense expense : expenses) {
            if (expense.getId() == id) {
                expense.setActive(!expense.isActive());
                break;
            }
        }
        recurringDAO.saveRecurringExpenses(username, expenses);
    }
    
    /**
     * Process all due recurring expenses for a user
     * Should be called on app startup
     */
    public int processDueRecurringExpenses(String username) {
        ArrayList<RecurringExpense> recurringExpenses = recurringDAO.loadRecurringExpenses(username);
        int generated = 0;
        
        for (RecurringExpense recurring : recurringExpenses) {
            if (!recurring.isActive()) continue;
            
            // Generate all missed occurrences
            LocalDate today = LocalDate.now();
            while (recurring.shouldGenerateToday()) {
                LocalDate nextDate = recurring.getNextOccurrence();
                if (nextDate.isAfter(today)) break;
                
                // Create expense instance
                Expense expense = recurring.createExpenseInstance(nextDate);
                expenseManager.addExpense(username, expense);
                
                // Update last generated date
                recurring.setLastGenerated(nextDate);
                generated++;
                
                // Safety check to avoid infinite loops
                if (generated > 100) break;
            }
        }
        
        // Save updated recurring expenses
        if (generated > 0) {
            recurringDAO.saveRecurringExpenses(username, recurringExpenses);
        }
        
        return generated;
    }
}
