
package com.paypilot.model;

/**
 * ExpenseCategory - Enum for predefined expense categories
 * Provides type safety for categories
 */
public enum ExpenseCategory {
    FOOD("Food & Dining"),
    TRANSPORTATION("Transportation"),
    UTILITIES("Utilities"),
    ENTERTAINMENT("Entertainment"),
    SHOPPING("Shopping"),
    HEALTHCARE("Healthcare"),
    EDUCATION("Education"),
    TRAVEL("Travel"),
    RENT("Rent"),
    OTHER("Other");
    
    private final String displayName;
    
    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}