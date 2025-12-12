package com.paypilot.view;

import com.paypilot.controller.BudgetManager;
import com.paypilot.controller.BudgetManager.BudgetStatus;
import com.paypilot.controller.BudgetManager.CategoryBudgetStatus;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * BudgetProgressPanel - Displays budget progress with visual indicators
 * Shows progress bars and alerts for budget tracking
 */
public class BudgetProgressPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private BudgetStatus budgetStatus;
    
    public BudgetProgressPanel(BudgetStatus budgetStatus) {
        this.budgetStatus = budgetStatus;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        buildUI();
    }
    
    private void buildUI() {
        // Title
        JLabel titleLabel = new JLabel("📊 Budget Tracker");
        titleLabel.setFont(UITheme.FONT_BOLD);
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(15));
        
        // Overall budget progress
        if (budgetStatus.budget.getMonthlyBudget() > 0) {
            add(createOverallBudgetSection());
            add(Box.createVerticalStrut(15));
        } else {
            JLabel noBudgetLabel = new JLabel("No monthly budget set");
            noBudgetLabel.setFont(UITheme.FONT_REGULAR);
            noBudgetLabel.setForeground(UITheme.TEXT_MUTED);
            noBudgetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(noBudgetLabel);
            add(Box.createVerticalStrut(10));
        }
        
        // Category budgets
        Map<String, CategoryBudgetStatus> categoryStatuses = budgetStatus.getCategoryStatuses();
        if (!categoryStatuses.isEmpty()) {
            JLabel categoryTitle = new JLabel("Category Budgets:");
            categoryTitle.setFont(UITheme.FONT_BOLD);
            categoryTitle.setForeground(UITheme.TEXT_DARK);
            categoryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(categoryTitle);
            add(Box.createVerticalStrut(10));
            
            for (CategoryBudgetStatus status : categoryStatuses.values()) {
                add(createCategoryBudgetSection(status));
                add(Box.createVerticalStrut(10));
            }
        }
    }
    
    private JPanel createOverallBudgetSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Header with amount
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel budgetLabel = new JLabel("Monthly Budget");
        budgetLabel.setFont(UITheme.FONT_REGULAR);
        budgetLabel.setForeground(UITheme.TEXT_DARK);
        
        String amountText = String.format("$%.2f / $%.2f", 
            budgetStatus.totalSpent, budgetStatus.budget.getMonthlyBudget());
        JLabel amountLabel = new JLabel(amountText);
        amountLabel.setFont(UITheme.FONT_BOLD);
        amountLabel.setForeground(getColorForAlert(budgetStatus.alertLevel));
        
        headerPanel.add(budgetLabel, BorderLayout.WEST);
        headerPanel.add(amountLabel, BorderLayout.EAST);
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(8));
        
        // Progress bar
        JPanel progressPanel = createProgressBar(
            budgetStatus.percentage, 
            budgetStatus.alertLevel
        );
        panel.add(progressPanel);
        
        // Alert message
        if (!budgetStatus.alertLevel.equals("SAFE")) {
            panel.add(Box.createVerticalStrut(5));
            JLabel alertLabel = new JLabel(getAlertMessage(budgetStatus.alertLevel, budgetStatus.percentage));
            alertLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
            alertLabel.setForeground(getColorForAlert(budgetStatus.alertLevel));
            alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(alertLabel);
        }
        
        return panel;
    }
    
    private JPanel createCategoryBudgetSection(CategoryBudgetStatus status) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(249, 250, 251));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel categoryLabel = new JLabel(status.category);
        categoryLabel.setFont(UITheme.FONT_REGULAR);
        
        String amountText = String.format("$%.2f / $%.2f", status.spent, status.budget);
        JLabel amountLabel = new JLabel(amountText);
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        amountLabel.setForeground(getColorForAlert(status.alertLevel));
        
        headerPanel.add(categoryLabel, BorderLayout.WEST);
        headerPanel.add(amountLabel, BorderLayout.EAST);
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(5));
        
        // Progress bar
        JPanel progressPanel = createProgressBar(status.percentage, status.alertLevel);
        panel.add(progressPanel);
        
        return panel;
    }
    
    private JPanel createProgressBar(double percentage, String alertLevel) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) Math.min(percentage, 100));
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.1f%%", percentage));
        progressBar.setPreferredSize(new Dimension(0, 20));
        
        // Color based on alert level
        Color barColor = getColorForAlert(alertLevel);
        progressBar.setForeground(barColor);
        progressBar.setBackground(new Color(229, 231, 235));
        
        container.add(progressBar, BorderLayout.CENTER);
        
        return container;
    }
    
    private Color getColorForAlert(String alertLevel) {
        switch (alertLevel) {
            case "EXCEEDED":
                return UITheme.DANGER;
            case "WARNING":
                return new Color(255, 159, 10); // Orange
            default:
                return UITheme.SUCCESS;
        }
    }
    
    private String getAlertMessage(String alertLevel, double percentage) {
        if (alertLevel.equals("EXCEEDED")) {
            return String.format("⚠ Budget exceeded by %.1f%%!", percentage - 100);
        } else if (alertLevel.equals("WARNING")) {
            return String.format("⚠ Approaching budget limit (%.1f%%)", percentage);
        }
        return "";
    }
    
    /**
     * Update the budget status and refresh UI
     */
    public void updateBudgetStatus(BudgetStatus newStatus) {
        this.budgetStatus = newStatus;
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }
}
