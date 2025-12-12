package com.paypilot.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Expense Model - Represents a personal expense
 * Encapsulation: Private fields with controlled access
 */
public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String category;
    private double amount;
    private String description;
    private LocalDate date;
    private String username;
    private boolean isSplit;
    private ArrayList<SplitDetail> splitDetails;
    private Receipt receipt; // Receipt attachment

    public Expense(String category, double amount, String description, LocalDate date, String username) {
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.username = username;
        this.isSplit = false;
        this.splitDetails = new ArrayList<>();
        this.receipt = null;
    }

    // Getters and Setters
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSplit() {
        return isSplit;
    }

    public void setSplit(boolean split) {
        isSplit = split;
    }

    public ArrayList<SplitDetail> getSplitDetails() {
        return splitDetails;
    }

    public void setSplitDetails(ArrayList<SplitDetail> splitDetails) {
        this.splitDetails = splitDetails;
    }
    
    public Receipt getReceipt() {
        return receipt;
    }
    
    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
    
    public boolean hasReceipt() {
        return receipt != null;
    }
}