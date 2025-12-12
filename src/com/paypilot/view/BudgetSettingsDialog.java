package com.paypilot.view;

import com.paypilot.controller.BudgetManager;
import com.paypilot.model.Budget;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * BudgetSettingsDialog - UI for configuring budgets
 * Allows users to set monthly and category-wise budgets
 */
public class BudgetSettingsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private BudgetManager budgetManager;
    private String username;
    private Budget budget;
    private boolean succeeded = false;
    
    private JTextField monthlyBudgetField;
    private DefaultTableModel categoryTableModel;
    private JTable categoryTable;
    private JTextField newCategoryField;
    private JTextField newAmountField;
    
    public BudgetSettingsDialog(Window parent, String username, BudgetManager budgetManager) {
        super(parent, "Budget Settings", ModalityType.APPLICATION_MODAL);
        this.username = username;
        this.budgetManager = budgetManager;
        this.budget = budgetManager.getBudget(username);
        
        initializeUI();
        loadBudgetData();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(UITheme.BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel("💰 Budget Configuration");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UITheme.BG_COLOR);
        
        // Monthly Budget Section
        UITheme.RoundedPanel monthlyPanel = createMonthlyBudgetPanel();
        contentPanel.add(monthlyPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Category Budgets Section
        UITheme.RoundedPanel categoryPanel = createCategoryBudgetPanel();
        contentPanel.add(categoryPanel);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(UITheme.BG_COLOR);
        
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        
        JButton saveBtn = UITheme.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> saveBudget());
        
        footerPanel.add(cancelBtn);
        footerPanel.add(saveBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private UITheme.RoundedPanel createMonthlyBudgetPanel() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, Color.WHITE);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel sectionLabel = new JLabel("Monthly Budget");
        sectionLabel.setFont(UITheme.FONT_BOLD);
        sectionLabel.setForeground(UITheme.TEXT_DARK);
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(Color.WHITE);
        
        JLabel dollarLabel = new JLabel("$");
        dollarLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        monthlyBudgetField = UITheme.createStyledTextField();
        monthlyBudgetField.setPreferredSize(new Dimension(200, 35));
        
        inputPanel.add(dollarLabel);
        inputPanel.add(monthlyBudgetField);
        
        panel.add(sectionLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private UITheme.RoundedPanel createCategoryBudgetPanel() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, Color.WHITE);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(0, 300));
        
        JLabel sectionLabel = new JLabel("Category Budgets (Optional)");
        sectionLabel.setFont(UITheme.FONT_BOLD);
        sectionLabel.setForeground(UITheme.TEXT_DARK);
        panel.add(sectionLabel, BorderLayout.NORTH);
        
        // Table
        categoryTableModel = new DefaultTableModel(
            new String[]{"Category", "Budget Amount", ""}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only delete button column
            }
        };
        
        categoryTable = new JTable(categoryTableModel);
        categoryTable.setRowHeight(35);
        categoryTable.setFont(UITheme.FONT_REGULAR);
        categoryTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        categoryTable.getTableHeader().setBackground(new Color(249, 250, 251));
        
        // Add delete button renderer
        categoryTable.getColumn("").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("✕");
            btn.setForeground(UITheme.DANGER);
            btn.setFont(UITheme.FONT_BOLD);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            return btn;
        });
        
        categoryTable.getColumn("").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                JButton btn = new JButton("✕");
                btn.addActionListener(e -> {
                    categoryTableModel.removeRow(row);
                    stopCellEditing();
                });
                return btn;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add category section
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBackground(Color.WHITE);
        
        newCategoryField = UITheme.createStyledTextField();
        newCategoryField.setPreferredSize(new Dimension(150, 30));
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(UITheme.FONT_REGULAR);
        
        newAmountField = UITheme.createStyledTextField();
        newAmountField.setPreferredSize(new Dimension(100, 30));
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(UITheme.FONT_REGULAR);
        
        JButton addBtn = UITheme.createSecondaryButton("Add");
        addBtn.addActionListener(e -> addCategoryBudget());
        
        addPanel.add(categoryLabel);
        addPanel.add(newCategoryField);
        addPanel.add(amountLabel);
        addPanel.add(newAmountField);
        addPanel.add(addBtn);
        
        panel.add(addPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadBudgetData() {
        // Load monthly budget
        if (budget.getMonthlyBudget() > 0) {
            monthlyBudgetField.setText(String.valueOf(budget.getMonthlyBudget()));
        }
        
        // Load category budgets
        for (Map.Entry<String, Double> entry : budget.getCategoryBudgets().entrySet()) {
            categoryTableModel.addRow(new Object[]{
                entry.getKey(),
                String.format("$%.2f", entry.getValue()),
                "Delete"
            });
        }
    }
    
    private void addCategoryBudget() {
        try {
            String category = newCategoryField.getText().trim();
            double amount = Double.parseDouble(newAmountField.getText().trim());
            
            if (category.isEmpty() || amount <= 0) {
                throw new IllegalArgumentException("Invalid input");
            }
            
            categoryTableModel.addRow(new Object[]{
                category,
                String.format("$%.2f", amount),
                "Delete"
            });
            
            newCategoryField.setText("");
            newAmountField.setText("");
            
        } catch (Exception e) {
            UITheme.showMessage(this, "Please enter valid category and amount", 
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void saveBudget() {
        try {
            // Save monthly budget
            String monthlyText = monthlyBudgetField.getText().trim();
            double monthlyBudget = monthlyText.isEmpty() ? 0 : Double.parseDouble(monthlyText);
            budget.setMonthlyBudget(monthlyBudget);
            
            // Save category budgets
            budget.getCategoryBudgets().clear();
            for (int i = 0; i < categoryTableModel.getRowCount(); i++) {
                String category = (String) categoryTableModel.getValueAt(i, 0);
                String amountStr = (String) categoryTableModel.getValueAt(i, 1);
                double amount = Double.parseDouble(amountStr.replace("$", "").trim());
                budget.setCategoryBudget(category, amount);
            }
            
            budgetManager.saveBudget(budget);
            succeeded = true;
            UITheme.showMessage(this, "Budget settings saved successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (Exception e) {
            UITheme.showMessage(this, "Please check your inputs: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSucceeded() {
        return succeeded;
    }
}
