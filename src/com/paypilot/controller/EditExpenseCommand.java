// CREATE THIS FILE: com/paypilot/controller/EditExpenseCommand.java
package com.paypilot.controller;

import com.paypilot.model. Expense;

/**
 * Command to edit an expense with undo support
 * Encapsulates edit operation as an object
 */
public class EditExpenseCommand implements Command {
    private ExpenseManager expenseManager;
    private Expense oldExpense;
    private Expense newExpense;
    private String username;
    
    public EditExpenseCommand(ExpenseManager expenseManager, String username, 
                             Expense oldExpense, Expense newExpense) {
        this.expenseManager = expenseManager;
        this.username = username;
        this.oldExpense = oldExpense;
        this.newExpense = newExpense;
    }
    
    @Override
    public void execute() {
        expenseManager.updateExpense(username, oldExpense, newExpense);
    }
    
    @Override
    public void undo() {
        expenseManager.updateExpense(username, newExpense, oldExpense);
    }
    
    @Override
    public String getDescription() {
        return "Edit expense: " + oldExpense.getCategory() + " -> " + newExpense.getCategory();
    }
}