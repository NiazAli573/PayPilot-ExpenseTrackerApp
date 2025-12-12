// CREATE THIS FILE: com/paypilot/controller/DeleteExpenseCommand.java
package com.paypilot.controller;

import com.paypilot.model. Expense;

/**
 * Command to delete an expense with undo support
 * Encapsulates delete operation as an object
 */
public class DeleteExpenseCommand implements Command {
    private ExpenseManager expenseManager;
    private Expense expense;
    private String username;
    
    public DeleteExpenseCommand(ExpenseManager expenseManager, String username, Expense expense) {
        this.expenseManager = expenseManager;
        this.username = username;
        this.expense = expense;
    }
    
    @Override
    public void execute() {
        expenseManager.deleteExpense(username, expense);
    }
    
    @Override
    public void undo() {
        expenseManager.addExpense(username, expense);
    }
    
    @Override
    public String getDescription() {
        return "Delete expense: " + expense.getCategory() + " - $" + 
               String.format("%. 2f", expense.getAmount());
    }
}