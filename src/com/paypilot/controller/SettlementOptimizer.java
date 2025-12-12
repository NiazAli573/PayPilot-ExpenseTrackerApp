package com.paypilot.controller;

import java.util.*;

/**
 * SettlementOptimizer - Implements minimal transaction settlement algorithm
 * Uses greedy approach to minimize number of transactions needed to settle group balances
 * 
 * Algorithm: Min-Cash-Flow
 * 1. Calculate net balance for each person
 * 2. Find person with max credit and max debit
 * 3. Settle between them
 * 4. Repeat until all settled
 */
public class SettlementOptimizer {
    
    /**
     * Transaction represents a payment from one person to another
     */
    public static class Transaction {
        public final String from;
        public final String to;
        public final double amount;
        
        public Transaction(String from, String to, double amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
        
        @Override
        public String toString() {
            return String.format("%s → %s: $%.2f", from, to, amount);
        }
    }
    
    /**
     * Optimize settlement transactions to minimize number of payments
     * @param balances Map of username to net balance (positive = owed money, negative = owes money)
     * @return List of optimal transactions to settle all balances
     */
    public static List<Transaction> optimizeSettlement(Map<String, Double> balances) {
        List<Transaction> transactions = new ArrayList<>();
        
        // Filter out zero balances and create working copy
        Map<String, Double> workingBalances = new HashMap<>();
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            if (Math.abs(entry.getValue()) > 0.01) { // Ignore tiny amounts
                workingBalances.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Process until all balances are settled
        while (!workingBalances.isEmpty()) {
            // Find person with maximum credit (is owed most)
            String maxCreditor = null;
            double maxCredit = 0;
            
            for (Map.Entry<String, Double> entry : workingBalances.entrySet()) {
                if (entry.getValue() > maxCredit) {
                    maxCredit = entry.getValue();
                    maxCreditor = entry.getKey();
                }
            }
            
            // Find person with maximum debit (owes most)
            String maxDebtor = null;
            double maxDebit = 0;
            
            for (Map.Entry<String, Double> entry : workingBalances.entrySet()) {
                if (entry.getValue() < maxDebit) {
                    maxDebit = entry.getValue();
                    maxDebtor = entry.getKey();
                }
            }
            
            // If no creditor or debtor found, we're done
            if (maxCreditor == null || maxDebtor == null) {
                break;
            }
            
            // Settle between max creditor and max debtor
            double settlementAmount = Math.min(maxCredit, Math.abs(maxDebit));
            
            // Create transaction: debtor pays creditor
            transactions.add(new Transaction(maxDebtor, maxCreditor, settlementAmount));
            
            // Update balances
            workingBalances.put(maxCreditor, maxCredit - settlementAmount);
            workingBalances.put(maxDebtor, maxDebit + settlementAmount);
            
            // Remove settled accounts
            if (Math.abs(workingBalances.get(maxCreditor)) < 0.01) {
                workingBalances.remove(maxCreditor);
            }
            if (Math.abs(workingBalances.get(maxDebtor)) < 0.01) {
                workingBalances.remove(maxDebtor);
            }
        }
        
        return transactions;
    }
    
    /**
     * Calculate savings compared to direct settlement
     * @param balances Original balances
     * @return Savings information
     */
    public static SettlementSavings calculateSavings(Map<String, Double> balances) {
        List<Transaction> optimized = optimizeSettlement(balances);
        
        // Count direct transactions (everyone with negative balance pays everyone with positive)
        int directTransactions = 0;
        for (double balance : balances.values()) {
            if (balance < -0.01) directTransactions++;
        }
        for (double balance : balances.values()) {
            if (balance > 0.01) directTransactions++;
        }
        // Actually it's debtors * creditors
        long debtors = balances.values().stream().filter(b -> b < -0.01).count();
        long creditors = balances.values().stream().filter(b -> b > 0.01).count();
        directTransactions = (int)(debtors * creditors);
        
        int optimizedTransactions = optimized.size();
        int saved = directTransactions - optimizedTransactions;
        
        return new SettlementSavings(directTransactions, optimizedTransactions, saved);
    }
    
    /**
     * Savings information
     */
    public static class SettlementSavings {
        public final int directTransactions;
        public final int optimizedTransactions;
        public final int transactionsSaved;
        
        public SettlementSavings(int direct, int optimized, int saved) {
            this.directTransactions = direct;
            this.optimizedTransactions = optimized;
            this.transactionsSaved = saved;
        }
    }
}
