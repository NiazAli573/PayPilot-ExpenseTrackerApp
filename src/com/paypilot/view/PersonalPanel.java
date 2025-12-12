package com.paypilot.view;

import com.paypilot.controller.*;
import com.paypilot.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PersonalPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Controllers
    private ExpenseManager expenseManager;
    private UndoManager undoManager;
    private BudgetManager budgetManager;
    private JFrame parentFrame;  // Changed to JFrame for compatibility
    private String currentUser;

    // UI Components
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> filterCategoryBox;
    private JLabel totalLabel, avgLabel, maxLabel;
    private BudgetProgressPanel budgetProgressPanel;

    // State
    private ArrayList<Expense> expenses;
    private ArrayList<Expense> filteredExpenses;
    private boolean isUpdatingComboBox = false;

    public PersonalPanel(String username, ExpenseManager expenseManager,
                         UndoManager undoManager, JFrame parent) {
        this.currentUser = username;
        this.expenseManager = expenseManager;
        this.undoManager = undoManager;
        this.budgetManager = new BudgetManager();
        this.parentFrame = parent;
        this.expenses = new ArrayList<>();
        this.filteredExpenses = new ArrayList<>();

        setBackground(UITheme.BG_COLOR);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        // Main content with stats and table
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        
        // --- 1. Stats Cards ---
        mainContent.add(createStatsPanel(), BorderLayout.NORTH);

        // --- 2. Table Section ---
        mainContent.add(createTableSection(), BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        
        // --- 3. Budget Panel (Right Side) ---
        add(createBudgetPanel(), BorderLayout.EAST);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 130));

        totalLabel = new JLabel("$0.00");
        avgLabel = new JLabel("$0.00");
        maxLabel = new JLabel("$0.00");

        panel.add(createStatCard("Total Expenses", totalLabel, UITheme.PRIMARY_COLOR));
        panel.add(createStatCard("Average Spend", avgLabel, UITheme.SUCCESS));
        panel.add(createStatCard("Highest Spend", maxLabel, UITheme.DANGER));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BOLD);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_HUGE); // Uses the large font defined in UITheme
        valueLabel.setForeground(UITheme.TEXT_DARK);

        // Decorative line
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(40, 4));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(titleLbl, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);
        content.add(bar, BorderLayout.SOUTH);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableSection() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(15, Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Toolbar ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setFont(UITheme.FONT_BOLD);
        
        filterCategoryBox = new JComboBox<>(new String[]{"All Categories"});
        filterCategoryBox.setBackground(Color.WHITE);
        filterCategoryBox.addActionListener(e -> {
            if (!isUpdatingComboBox) filterByCategory();
        });

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> resetFilter());
        
        JButton sortBtn = UITheme.createSecondaryButton("Sort Amount");
        sortBtn.addActionListener(e -> sortByAmount());

        toolbar.add(filterLbl);
        toolbar.add(filterCategoryBox);
        toolbar.add(resetBtn);
        toolbar.add(sortBtn);

        // Right side buttons
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightActions.setOpaque(false);
        
        JButton editBtn = UITheme.createSecondaryButton("Edit Selected");
        editBtn.addActionListener(e -> editSelectedExpense());
        
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedExpense());
        
        rightActions.add(editBtn);
        rightActions.add(deleteBtn);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(toolbar, BorderLayout.WEST);
        topBar.add(rightActions, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        // --- Table ---
        tableModel = new DefaultTableModel(new String[]{"Date", "Category", "Amount", "Description", "Type", "📎"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Amount Color
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) {
                    setForeground(UITheme.SUCCESS);
                    setFont(UITheme.FONT_BOLD);
                }
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return this;
            }
        });

        // Type column - highlight split expenses as clickable
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = (String) value;
                if ("Split".equals(type)) {
                    setForeground(UITheme.PRIMARY_COLOR);
                    setFont(UITheme.FONT_BOLD);
                    setText("🔗 " + type + " (Click to view)");
                } else {
                    setForeground(UITheme.TEXT_MUTED);
                    setFont(UITheme.FONT_REGULAR);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // Change cursor to hand when hovering over split expense rows
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String type = (String) tableModel.getValueAt(row, 4);
                    if (type != null && type.startsWith("Split") || "Split".equals(type)) {
                        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else {
                        table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        });

        // Receipt column - clickable icon
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String receipt = (String) value;
                if (receipt != null && !receipt.isEmpty()) {
                    setForeground(UITheme.PRIMARY_COLOR);
                    setFont(new Font("SansSerif", Font.PLAIN, 16));
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setText("");
                }
                return this;
            }
        });
        table.getColumnModel().getColumn(5).setMaxWidth(40);

        // Add mouse listener for showing split details and receipts on click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (row >= 0) {
                    Expense expense = getExpenseFromTableRow(row);
                    if (expense == null) return;
                    
                    // Check if clicked on receipt column
                    if (column == 5 && expense.hasReceipt()) {
                        showReceiptViewer(expense);
                    }
                    // Check if clicked on split expense
                    else if (expense.isSplit()) {
                        showSplitDetailsPopup(expense);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Shows a popup dialog with split details for the selected expense
     */
    private void showSplitDetailsPopup(Expense expense) {
        SplitDetailsDialog dialog = new SplitDetailsDialog(
            SwingUtilities.getWindowAncestor(this), expense);
        dialog.setVisible(true);
    }
    
    /**
     * Shows receipt image viewer for the selected expense
     */
    private void showReceiptViewer(Expense expense) {
        if (expense.hasReceipt()) {
            ReceiptViewerDialog viewer = new ReceiptViewerDialog(
                SwingUtilities.getWindowAncestor(this), 
                expense.getReceipt().getFilePath());
            viewer.setVisible(true);
        }
    }

    // --- Logic ---

    public void refreshData() {
        expenses = expenseManager.getAllExpenses(currentUser);
        filteredExpenses.clear();
        updateCategoryFilter();
        refreshTable();
        updateStatistics();
        updateBudgetDisplay(); // Update budget when expenses change
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Expense> list = filteredExpenses.isEmpty() ? expenses : filteredExpenses;
        
        for (Expense exp : list) {
            tableModel.addRow(new Object[]{
                exp.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                exp.getCategory(),
                String.format("$%.2f", exp.getAmount()),
                exp.getDescription(),
                exp.isSplit() ? "Split" : "Personal",
                exp.hasReceipt() ? "📎" : ""
            });
        }
    }

    private void updateStatistics() {
        List<Expense> source = filteredExpenses.isEmpty() ? expenses : filteredExpenses;
        double total = source.stream().mapToDouble(Expense::getAmount).sum();
        double max = source.stream().mapToDouble(Expense::getAmount).max().orElse(0);
        double avg = source.isEmpty() ? 0 : total / source.size();

        totalLabel.setText(String.format("$%.2f", total));
        avgLabel.setText(String.format("$%.2f", avg));
        maxLabel.setText(String.format("$%.2f", max));
    }

    private void editSelectedExpense() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UITheme.showMessage(this, "Select an expense to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Expense oldExpense = getExpenseFromTableRow(row);
        if (oldExpense == null) return;

        // Open AddExpenseForm in Edit Mode
        AddExpenseForm form = new AddExpenseForm(parentFrame, currentUser, oldExpense);
        form.setVisible(true);

        if (form.isSucceeded()) {
            Expense newExpense = form.getExpense();
            // Use Command Pattern via UndoManager
            Command editCmd = new EditExpenseCommand(expenseManager, currentUser, oldExpense, newExpense);
            undoManager.executeCommand(editCmd);
            refreshData();
        }
    }

    private void deleteSelectedExpense() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UITheme.showMessage(this, "Select an expense to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (UITheme.showConfirm(this, "Delete this expense?", "Confirm") == JOptionPane.YES_OPTION) {
            Expense expense = getExpenseFromTableRow(row);
            if (expense != null) {
                Command delCmd = new DeleteExpenseCommand(expenseManager, currentUser, expense);
                undoManager.executeCommand(delCmd);
                refreshData();
            }
        }
    }

    private Expense getExpenseFromTableRow(int row) {
        String dateStr = (String) tableModel.getValueAt(row, 0);
        String category = (String) tableModel.getValueAt(row, 1);
        String amountStr = (String) tableModel.getValueAt(row, 2);
        String desc = (String) tableModel.getValueAt(row, 3);
        
        double amount = Double.parseDouble(amountStr.replace("$", "").trim());

        return expenses.stream()
            .filter(e -> e.getCategory().equals(category) &&
                         Math.abs(e.getAmount() - amount) < 0.01 &&
                         e.getDescription().equals(desc))
            .findFirst()
            .orElse(null);
    }

    private void filterByCategory() {
        String selected = (String) filterCategoryBox.getSelectedItem();
        if (selected == null || selected.equals("All Categories")) {
            filteredExpenses.clear();
        } else {
            filteredExpenses = expenseManager.filterByCategory(currentUser, selected);
        }
        refreshTable();
        updateStatistics();
    }

    private void updateCategoryFilter() {
        isUpdatingComboBox = true;
        filterCategoryBox.removeAllItems();
        filterCategoryBox.addItem("All Categories");
        for (String cat : expenseManager.getUniqueCategories(currentUser)) {
            filterCategoryBox.addItem(cat);
        }
        isUpdatingComboBox = false;
    }

    private void resetFilter() {
        if(filterCategoryBox.getItemCount() > 0) filterCategoryBox.setSelectedIndex(0);
        filteredExpenses.clear();
        refreshTable();
        updateStatistics();
    }

    private void sortByAmount() {
        List<Expense> list = filteredExpenses.isEmpty() ? expenses : filteredExpenses;
        list.sort((e1, e2) -> Double.compare(e2.getAmount(), e1.getAmount()));
        refreshTable();
    }
    
    /**
     * Create budget panel with settings and progress
     */
    private JPanel createBudgetPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.BG_COLOR);
        container.setPreferredSize(new Dimension(320, 0));
        
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(15, Color.WHITE);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header with settings button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Budget Overview");
        titleLabel.setFont(UITheme.FONT_SUBTITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);
        
        JButton settingsBtn = new JButton("⚙");
        settingsBtn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        settingsBtn.setForeground(UITheme.PRIMARY_COLOR);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setContentAreaFilled(false);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsBtn.setToolTipText("Budget Settings");
        settingsBtn.addActionListener(e -> openBudgetSettings());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(settingsBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Budget progress display
        BudgetManager.BudgetStatus status = budgetManager.getBudgetStatus(currentUser, expenses);
        budgetProgressPanel = new BudgetProgressPanel(status);
        
        JScrollPane scrollPane = new JScrollPane(budgetProgressPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        container.add(panel, BorderLayout.CENTER);
        return container;
    }
    
    /**
     * Open budget settings dialog
     */
    private void openBudgetSettings() {
        BudgetSettingsDialog dialog = new BudgetSettingsDialog(
            SwingUtilities.getWindowAncestor(this), currentUser, budgetManager);
        dialog.setVisible(true);
        
        if (dialog.isSucceeded()) {
            updateBudgetDisplay();
        }
    }
    
    /**
     * Update budget display panel
     */
    private void updateBudgetDisplay() {
        BudgetManager.BudgetStatus status = budgetManager.getBudgetStatus(currentUser, expenses);
        if (budgetProgressPanel != null) {
            budgetProgressPanel.updateBudgetStatus(status);
        }
    }
    
    /**
     * Override refreshData to also update budget
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && budgetProgressPanel != null) {
            updateBudgetDisplay();
        }
    }
}