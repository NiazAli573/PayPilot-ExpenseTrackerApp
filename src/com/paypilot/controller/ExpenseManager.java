package com.paypilot.controller;

import com.paypilot.dao.ExpenseDAO;
import com.paypilot.model.Expense;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ExpenseManager - Handles logic for adding, retrieving, and managing expenses.
 * Uses DAO pattern for persistence.
 */
public class ExpenseManager {

    private ExpenseDAO expenseDAO;

    public ExpenseManager() {
        this.expenseDAO = new ExpenseDAO();
    }

    // --- CRUD Operations ---

    public void addExpense(Expense expense) {
        if (expense != null && expense.getUsername() != null) {
            expenseDAO.addExpense(expense.getUsername(), expense);
        }
    }
    
    // Overloaded method to match your specific command pattern needs
    public void addExpense(String username, Expense expense) {
        if (expense != null) {
            expenseDAO.addExpense(username, expense);
        }
    }

    public void deleteExpense(String username, Expense expense) {
        expenseDAO.deleteExpense(username, expense);
    }

    public void updateExpense(String username, Expense oldExpense, Expense newExpense) {
        expenseDAO.updateExpense(username, oldExpense, newExpense);
    }

    // --- Data Retrieval ---

    public ArrayList<Expense> getAllExpenses(String username) {
        return expenseDAO.loadExpenses(username);
    }

    public void clearAllExpenses(String username) {
        expenseDAO.clearAllExpenses(username);
    }

    // --- Filtering & Sorting ---

    public ArrayList<Expense> filterByCategory(String username, String category) {
        return getAllExpenses(username).stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Expense> sortByAmount(String username, boolean descending) {
        ArrayList<Expense> list = getAllExpenses(username);
        list.sort(Comparator.comparingDouble(Expense::getAmount));
        if (descending) Collections.reverse(list);
        return list;
    }

    public ArrayList<Expense> sortByDate(String username, boolean descending) {
        ArrayList<Expense> list = getAllExpenses(username);
        list.sort(Comparator.comparing(Expense::getDate));
        if (descending) Collections.reverse(list);
        return list;
    }

    // --- Statistics ---

    public List<String> getUniqueCategories(String username) {
        return getAllExpenses(username).stream()
                .map(Expense::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public double calculateTotal(String username) {
        return getAllExpenses(username).stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double calculateAverage(String username) {
        ArrayList<Expense> list = getAllExpenses(username);
        return list.isEmpty() ? 0.0 : calculateTotal(username) / list.size();
    }

    public double getMaxExpense(String username) {
        return getAllExpenses(username).stream()
                .mapToDouble(Expense::getAmount)
                .max()
                .orElse(0.0);
    }

    public int getExpenseCount(String username) {
        return getAllExpenses(username).size();
    }

    public ArrayList<Expense> getSplitExpenses(String username) {
        return getAllExpenses(username).stream()
                .filter(Expense::isSplit)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}