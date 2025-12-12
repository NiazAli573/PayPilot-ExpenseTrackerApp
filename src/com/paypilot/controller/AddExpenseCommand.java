package com.paypilot.controller;

import com.paypilot.model. Expense;

/**
 * Command to add an expense
 */
public class AddExpenseCommand implements Command {
    private ExpenseManager expenseManager;
    private Expense expense;
    private String username;
    
    public AddExpenseCommand(ExpenseManager expenseManager, String username, Expense expense) {
        this.expenseManager = expenseManager;
        this.username = username;
        this.expense = expense;
    }
    
    @Override
    public void execute() {
        expenseManager.addExpense(username, expense);
    }
    
    @Override
    public void undo() {
        expenseManager.deleteExpense(username, expense);
    }
    
    @Override
    public String getDescription() {
        return "Add expense: " + expense.getCategory() + " - $" + 
               String.format("%.2f", expense.getAmount());
    }
}