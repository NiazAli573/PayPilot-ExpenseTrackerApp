package com.paypilot.controller.report;

import com.paypilot.model.Expense;
import com.paypilot.model.SplitDetail;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Personal Report Generator Implementation
 * LSP: Can substitute ReportGenerator interface
 * OCP: Extends functionality without modifying interface
 */
public class PersonalReportGenerator implements ReportGenerator {
    private String username;
    private ArrayList<Expense> expenses;
    private String reportContent;
    
    public PersonalReportGenerator(String username, ArrayList<Expense> expenses) {
        this.username = username;
        this.expenses = expenses;
        this.reportContent = null;
    }
    
    @Override
    public String generate() {
        StringBuilder report = new StringBuilder();
        
        // Beautiful Header
        report.append("\n");
        report.append("╔═══════════════════════════════════════════════════════════════════════════╗\n");
        report.append("║                                                                           ║\n");
        report.append("║                    💰 PAYPILOT EXPENSE REPORT 💰                         ║\n");
        report.append("║                      Personal Finance Overview                            ║\n");
        report.append("║                                                                           ║\n");
        report.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        report.append("\n");
        
        // Report Info
        report.append("┌─────────────────────────────────────────────────────────────────────────┐\n");
        report.append("│  📊 REPORT INFORMATION                                                  │\n");
        report.append("├─────────────────────────────────────────────────────────────────────────┤\n");
        report.append(String.format("│  👤 User:          %-50s │\n", username));
        report.append(String.format("│  📅 Generated:     %-50s │\n", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"))));
        report.append(String.format("│  📋 Total Records: %-50d │\n", expenses.size()));
        report.append("└─────────────────────────────────────────────────────────────────────────┘\n");
        report.append("\n");
        
        // Calculate statistics
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double average = expenses.isEmpty() ? 0 : total / expenses.size();
        double max = expenses.stream().mapToDouble(Expense::getAmount).max().orElse(0);
        double min = expenses.stream().mapToDouble(Expense::getAmount).min().orElse(0);
        
        // Beautiful Summary Statistics
        report.append("┌─────────────────────────────────────────────────────────────────────────┐\n");
        report.append("│  📈 SUMMARY STATISTICS                                                  │\n");
        report.append("├─────────────────────────────────────────────────────────────────────────┤\n");
        report.append(String.format("│  💵 Total Expenses:        $%-44.2f │\n", total));
        report.append(String.format("│  📊 Average Expense:       $%-44.2f │\n", average));
        report.append(String.format("│  📈 Highest Expense:       $%-44.2f │\n", max));
        report.append(String.format("│  📉 Lowest Expense:        $%-44.2f │\n", min));
        report.append("└─────────────────────────────────────────────────────────────────────────┘\n");
        report.append("\n");
        
        // Category breakdown
        java.util.Map<String, Double> categoryTotals = new java.util.HashMap<>();
        for (Expense exp : expenses) {
            categoryTotals.merge(exp.getCategory(), exp.getAmount(), Double::sum);
        }
        
        if (!categoryTotals.isEmpty()) {
            report.append("┌─────────────────────────────────────────────────────────────────────────┐\n");
            report.append("│  🏷️  EXPENSES BY CATEGORY                                              │\n");
            report.append("├─────────────────────────────────────────────────────────────────────────┤\n");
            
            categoryTotals.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> {
                    double percentage = (entry.getValue() / total) * 100;
                    String bar = "█".repeat((int)(percentage / 2));
                    report.append(String.format("│  %-20s $%-12.2f (%5.1f%%)  %-20s │\n", 
                        entry.getKey(), entry.getValue(), percentage, bar));
                });
            
            report.append("└─────────────────────────────────────────────────────────────────────────┘\n");
            report.append("\n");
        }
        
        // Detailed Expenses
        report.append("┌─────────────────────────────────────────────────────────────────────────┐\n");
        report.append("│  📝 DETAILED EXPENSE LIST                                               │\n");
        report.append("└─────────────────────────────────────────────────────────────────────────┘\n");
        report.append("\n");
        
        int expenseNumber = 1;
        for (Expense exp : expenses) {
            report.append("┌─────────────────────────────────────────────────────────────────────────┐\n");
            report.append(String.format("│  Expense #%-3d                                                          │\n", expenseNumber++));
            report.append("├─────────────────────────────────────────────────────────────────────────┤\n");
            report.append(String.format("│  📅 Date:        %-56s │\n", 
                exp.getDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))));
            report.append(String.format("│  🏷️  Category:   %-56s │\n", exp.getCategory()));
            report.append(String.format("│  💰 Amount:      $%-55.2f │\n", exp.getAmount()));
            report.append(String.format("│  📝 Description: %-56s │\n", 
                exp.getDescription().isEmpty() ? "No description" : 
                (exp.getDescription().length() > 56 ? exp.getDescription().substring(0, 53) + "..." : exp.getDescription())));
            report.append(String.format("│  📊 Type:        %-56s │\n", exp.isSplit() ? "Split Expense" : "Personal"));
            
            // Split details if present
            if (exp.isSplit() && exp.getSplitDetails() != null && !exp.getSplitDetails().isEmpty()) {
                report.append("│  ├─ Split Details:                                                     │\n");
                for (SplitDetail detail : exp.getSplitDetails()) {
                    double percentage = (detail.getAmount() / exp.getAmount()) * 100;
                    report.append(String.format("│  │  👤 %-20s → $%-10.2f (%.1f%%)                      │\n",
                        detail.getPersonName(), detail.getAmount(), percentage));
                }
            }
            
            report.append("└─────────────────────────────────────────────────────────────────────────┘\n");
            report.append("\n");
        }
        
        // Beautiful Footer
        report.append("\n");
        report.append("╔═══════════════════════════════════════════════════════════════════════════╗\n");
        report.append(String.format("║                        GRAND TOTAL: $%-37.2f║\n", total));
        report.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        report.append("\n");
        report.append("                    Generated by PayPilot Finance Manager\n");
        report.append("                    Thank you for tracking with PayPilot! 💰\n");
        report.append("\n");
        
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
        return "PERSONAL";
    }
}