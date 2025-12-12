package com.paypilot.controller.report;

import com.paypilot.model.Group;
import com.paypilot.model.SharedExpense;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Group Report Generator Implementation
 * LSP: Can substitute ReportGenerator interface
 * OCP: Extends functionality without modifying interface
 */
public class GroupReportGenerator implements ReportGenerator {
    private Group group;
    private ArrayList<SharedExpense> expenses;
    private Map<String, Double> balances;
    private String reportContent;
    
    public GroupReportGenerator(Group group, ArrayList<SharedExpense> expenses, 
                               Map<String, Double> balances) {
        this.group = group;
        this.expenses = expenses;
        this.balances = balances;
        this.reportContent = null;
    }
    
    @Override
    public String generate() {
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("═".repeat(60)).append("\n");
        report.append("         GROUP EXPENSE REPORT\n");
        report.append("═".repeat(60)).append("\n\n");
        
        report.append("Group: ").append(group.getGroupName()).append("\n");
        report.append("Created By: ").append(group.getCreatedBy()).append("\n");
        report.append("Members: ").append(group.getMembers().size()).append("\n");
        report.append("Generated: ").append(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        ).append("\n");
        report.append("─".repeat(60)).append("\n\n");
        
        // Group members
        report.append("GROUP MEMBERS\n");
        report.append("─".repeat(60)).append("\n");
        for (String member : group.getMembers()) {
            report.append("  • ").append(member).append("\n");
        }
        report.append("\n");
        
        // Calculate statistics
        double total = expenses.stream().mapToDouble(SharedExpense::getTotalAmount).sum();
        double average = expenses.isEmpty() ? 0 : total / expenses.size();
        
        report.append("SUMMARY STATISTICS\n");
        report.append("─".repeat(60)).append("\n");
        report.append(String.format("Total Expenses:  $%-10.2f\n", total));
        report.append(String.format("Average Expense: $%-10.2f\n", average));
        report.append(String.format("Number of Expenses: %d\n", expenses.size()));
        report.append("\n");
        
        // Balances
        report.append("MEMBER BALANCES\n");
        report.append("─".repeat(60)).append("\n");
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            String status;
            double amount = entry.getValue();
            if (Math.abs(amount) < 0.01) {
                status = "Settled";
            } else if (amount > 0) {
                status = "Is owed";
            } else {
                status = "Owes";
            }
            report.append(String.format("%-20s: $%-10.2f (%s)\n", 
                entry.getKey(), Math.abs(amount), status));
        }
        report.append("\n");
        
        // Detailed expenses
        report.append("DETAILED EXPENSES\n");
        report.append("─".repeat(60)).append("\n\n");
        
        for (SharedExpense exp : expenses) {
            report.append("Date: ").append(
                exp.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            ).append("\n");
            report.append("Category: ").append(exp.getCategory()).append("\n");
            report.append("Amount: $").append(String.format("%.2f", exp.getTotalAmount())).append("\n");
            report.append("Paid By: ").append(exp.getPaidByUsername()).append("\n");
            report.append("Description: ").append(
                exp.getDescription().isEmpty() ? "N/A" : exp.getDescription()
            ).append("\n");
            report.append("Split Type: ").append(exp.getSplitStrategyType()).append("\n");
            report.append("Participants: ").append(exp.getParticipantUserIds().size()).append("\n");
            report.append("─".repeat(60)).append("\n");
        }
        
        report.append("\n═".repeat(60)).append("\n");
        report.append(String.format("TOTAL: $%.2f\n", total));
        report.append("═".repeat(60)).append("\n");
        
        this.reportContent = report.toString();
        return reportContent;
    }
    
    @Override
    public boolean saveToFile(String filepath) {
        if (reportContent == null) {
            generate();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(reportContent);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving report: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getReportType() {
        return "GROUP";
    }
}