package com.paypilot.model;

import java.io.Serializable;

/**
 * SplitDetail Model - Represents individual share in split expense
 */
public class SplitDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String personName;
    private double amount;

    public SplitDetail(String personName, double amount) {
        this.personName = personName;
        this.amount = amount;
    }

    // Getters and Setters
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}