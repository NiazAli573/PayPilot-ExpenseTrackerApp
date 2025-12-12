package com.paypilot.view;

import com.paypilot.controller.*;
import com.paypilot.controller.report.*;
import com.paypilot.model.Expense;
import com.paypilot.model.ExpenseCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Modern Professional Dashboard for PayPilot
 * Focuses on core expense tracking functionality
 */
public class ModernMainDashboard extends JFrame {
    private static final long serialVersionUID = 1L;

    // Controllers
    private ExpenseManager expenseManager;
    private UndoManager undoManager;
    private RecurringExpenseManager recurringExpenseManager;
    private BudgetManager budgetManager;
    private String currentUser;

    // UI Components
    private DefaultTableModel tableModel;
    private JTable expenseTable;
    private JComboBox<String> filterCategoryBox;
    private JComboBox<String> filterPeriodBox;
    private JLabel totalExpenseLabel, monthlyExpenseLabel, weeklyExpenseLabel, countLabel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Data
    private ArrayList<Expense> allExpenses;
    private ArrayList<Expense> filteredExpenses;

    public ModernMainDashboard(String username) {
        this.currentUser = username;
        this.expenseManager = new ExpenseManager();
        this.undoManager = new UndoManager();
        this.budgetManager = new BudgetManager();
        this.recurringExpenseManager = new RecurringExpenseManager(expenseManager);
        this.allExpenses = new ArrayList<>();
        this.filteredExpenses = new ArrayList<>();

        UITheme.applyLookAndFeel();
        processRecurringExpenses();
        initializeUI();
        loadExpenses();
        setVisible(true);
    }

    private void processRecurringExpenses() {
        int generated = recurringExpenseManager.processDueRecurringExpenses(currentUser);
        if (generated > 0) {
            System.out.println("✓ Generated " + generated + " recurring expenses");
        }
    }

    private void initializeUI() {
        setTitle("PayPilot - Personal Finance Manager");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UITheme.BG_COLOR);

        // Left Sidebar
        mainContainer.add(createSidebar(), BorderLayout.WEST);

        // Main Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BG_COLOR);
        
        contentPanel.add(createDashboardView(), "DASHBOARD");
        contentPanel.add(new PersonalPanel(currentUser, expenseManager, undoManager, this), "PERSONAL");
        contentPanel.add(new ChartsDashboardPanel(currentUser, expenseManager), "CHARTS");
        
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainContainer);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // App Logo/Title with Icon
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Logo icon - Wallet is more relatable to finance/expense tracking
        JLabel logoIcon = new JLabel("💼");
        logoIcon.setFont(new Font("Roboto", Font.BOLD, 48));
        logoIcon.setForeground(new Color(96, 165, 250)); // Light blue
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel appTitle = new JLabel("PayPilot");
        appTitle.setFont(new Font("Roboto", Font.BOLD, 28));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel appSubtitle = new JLabel("Finance Manager");
        appSubtitle.setFont(new Font("Roboto", Font.PLAIN, 12));
        appSubtitle.setForeground(new Color(156, 163, 175));
        appSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        logoPanel.add(logoIcon);
        logoPanel.add(Box.createVerticalStrut(8));
        logoPanel.add(appTitle);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(appSubtitle);
        
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(40));

        // Navigation Menu
        sidebar.add(createMenuButton("🏠 Dashboard", e -> showView("DASHBOARD")));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuButton("💳 My Expenses", e -> showView("PERSONAL")));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuButton("📊 Analytics", e -> showView("CHARTS")));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuButton("💰 Budgets", e -> openBudgetSettings()));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuButton("🔄 Recurring", e -> openRecurringExpenses()));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuButton("📄 Export Report", e -> exportReport()));

        sidebar.add(Box.createVerticalGlue());

        // User Section
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(55, 65, 81)),
            new EmptyBorder(20, 0, 0, 0)
        ));
        
        JLabel userLabel = new JLabel("👤 " + currentUser);
        userLabel.setFont(UITheme.FONT_BOLD);
        userLabel.setForeground(new Color(209, 213, 219));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton logoutBtn = createTextButton("Logout →", e -> logout());
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(12));
        userPanel.add(logoutBtn);
        
        sidebar.add(userPanel);
        
        return sidebar;
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        RoundedButton btn = new RoundedButton(text);
        btn.setFont(new Font("Roboto", Font.BOLD, 15));
        btn.setForeground(new Color(209, 213, 219)); // Light gray text for dark sidebar
        btn.setBackground(new Color(45, 55, 72)); // Dark background
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(59, 130, 246)); // Light blue on hover
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 55, 72)); // Back to dark
                btn.setForeground(new Color(209, 213, 219));
                btn.repaint();
            }
        });
        
        btn.addActionListener(action);
        return btn;
    }
    
    // Custom rounded button class for sidebar menu
    private class RoundedButton extends JButton {
        private int cornerRadius = 12;
        
        public RoundedButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill rounded rectangle
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JButton createTextButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_SMALL);
        btn.setForeground(UITheme.DANGER);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private JPanel createDashboardView() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 20));
        dashboard.setBackground(UITheme.BG_COLOR);
        dashboard.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Header
        dashboard.add(createDashboardHeader(), BorderLayout.NORTH);

        // Main Content
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        
        mainContent.add(createStatsCards(), BorderLayout.NORTH);
        mainContent.add(createExpenseTable(), BorderLayout.CENTER);
        
        dashboard.add(mainContent, BorderLayout.CENTER);
        
        return dashboard;
    }

    private JPanel createDashboardHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftSection.setOpaque(false);
        
        JLabel title = new JLabel("💰 Expense Dashboard");
        title.setFont(new Font("Roboto", Font.BOLD, 26));
        title.setForeground(UITheme.TEXT_DARK);
        
        leftSection.add(title);
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("+ Add Expense");
        addBtn.setPreferredSize(new Dimension(150, 42));
        addBtn.addActionListener(e -> openAddExpenseDialog());

        JButton undoBtn = UITheme.createSecondaryButton("↶ Undo");
        undoBtn.setPreferredSize(new Dimension(100, 42));
        undoBtn.addActionListener(e -> performUndo());

        actions.add(undoBtn);
        actions.add(addBtn);

        header.add(leftSection, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsCards() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 140));

        totalExpenseLabel = new JLabel("$0.00");
        monthlyExpenseLabel = new JLabel("$0.00");
        weeklyExpenseLabel = new JLabel("$0.00");
        countLabel = new JLabel("0");

        statsPanel.add(createStatCard("Total Expenses", totalExpenseLabel, "📊", UITheme.PRIMARY_COLOR));
        statsPanel.add(createStatCard("This Month", monthlyExpenseLabel, "📅", UITheme.SUCCESS));
        statsPanel.add(createStatCard("This Week", weeklyExpenseLabel, "📆", UITheme.WARNING));
        statsPanel.add(createStatCard("Count", countLabel, "🔢", UITheme.ACCENT_COLOR));

        return statsPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color accentColor) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, Color.WHITE);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel topSection = new JPanel(new BorderLayout(10, 0));
        topSection.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(accentColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Roboto", Font.PLAIN, 13));
        titleLabel.setForeground(UITheme.TEXT_MUTED);
        
        topSection.add(iconLabel, BorderLayout.WEST);
        topSection.add(titleLabel, BorderLayout.CENTER);

        valueLabel.setFont(new Font("Roboto", Font.BOLD, 32));
        valueLabel.setForeground(UITheme.TEXT_DARK);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(0, 4));

        card.add(topSection, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(colorBar, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createExpenseTable() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, Color.WHITE);
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Toolbar
        panel.add(createTableToolbar(), BorderLayout.NORTH);

        // Table
        String[] columns = {"Category", "Amount", "Description", "Date", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        UITheme.styleTable(expenseTable);
        
        // Custom renderer for amount column
        expenseTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setForeground(UITheme.SUCCESS);
                    setFont(UITheme.FONT_BOLD);
                }
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTableToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        // Left side - Filters
        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftTools.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(UITheme.FONT_BOLD);
        filterLabel.setForeground(UITheme.TEXT_DARK);

        filterCategoryBox = new JComboBox<>(new String[]{"All Categories"});
        filterCategoryBox.setFont(UITheme.FONT_REGULAR);
        filterCategoryBox.setPreferredSize(new Dimension(160, 36));
        filterCategoryBox.addActionListener(e -> applyFilters());

        filterPeriodBox = new JComboBox<>(new String[]{"All Time", "This Month", "This Week", "Today"});
        filterPeriodBox.setFont(UITheme.FONT_REGULAR);
        filterPeriodBox.setPreferredSize(new Dimension(140, 36));
        filterPeriodBox.addActionListener(e -> applyFilters());

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> resetFilters());

        leftTools.add(filterLabel);
        leftTools.add(filterCategoryBox);
        leftTools.add(filterPeriodBox);
        leftTools.add(resetBtn);

        // Right side - Actions
        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTools.setOpaque(false);

        JButton editBtn = UITheme.createSecondaryButton("✏️ Edit");
        editBtn.addActionListener(e -> editSelectedExpense());

        JButton deleteBtn = UITheme.createDangerButton("🗑️ Delete");
        deleteBtn.addActionListener(e -> deleteSelectedExpense());

        rightTools.add(editBtn);
        rightTools.add(deleteBtn);

        toolbar.add(leftTools, BorderLayout.WEST);
        toolbar.add(rightTools, BorderLayout.EAST);

        return toolbar;
    }

    // === ACTION METHODS ===

    private void showView(String viewName) {
        cardLayout.show(contentPanel, viewName);
        if ("PERSONAL".equals(viewName) || "DASHBOARD".equals(viewName)) {
            loadExpenses();
        }
    }

    private void openAddExpenseDialog() {
        AddExpenseForm form = new AddExpenseForm(this, currentUser);
        form.setVisible(true);
        if (form.isSucceeded()) {
            Command addCommand = new AddExpenseCommand(expenseManager, currentUser, form.getExpense());
            undoManager.executeCommand(addCommand);
            
            if (form.isRecurring()) {
                com.paypilot.model.RecurringExpense recurring = form.getRecurringExpense();
                if (recurring != null) {
                    recurringExpenseManager.addRecurringExpense(recurring);
                    UITheme.showMessage(this, 
                        "✓ Expense added as recurring!\nIt will auto-generate based on your schedule.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            loadExpenses();
        }
    }

    private void editSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showMessage(this, "Please select an expense to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Expense expense = getExpenseFromRow(selectedRow);
        if (expense != null) {
            AddExpenseForm form = new AddExpenseForm(this, currentUser, expense);
            form.setVisible(true);
            if (form.isSucceeded()) {
                Command editCommand = new EditExpenseCommand(expenseManager, currentUser, expense, form.getExpense());
                undoManager.executeCommand(editCommand);
                loadExpenses();
            }
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showMessage(this, "Please select an expense to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = UITheme.showConfirm(this, "Are you sure you want to delete this expense?", "Confirm Delete");
        if (confirm == JOptionPane.YES_OPTION) {
            Expense expense = getExpenseFromRow(selectedRow);
            if (expense != null) {
                undoManager.executeCommand(new DeleteExpenseCommand(expenseManager, currentUser, expense));
                loadExpenses();
            }
        }
    }

    private void performUndo() {
        if (undoManager.undo()) {
            loadExpenses();
            UITheme.showMessage(this, "Action undone successfully", "Undo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            UITheme.showMessage(this, "Nothing to undo", "Undo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadExpenses() {
        allExpenses = expenseManager.getAllExpenses(currentUser);
        updateCategoryFilter();
        applyFilters();
        
        // Real-time update: Refresh all panels
        refreshAllPanels();
    }
    
    /**
     * Refresh all panels for real-time updates
     */
    private void refreshAllPanels() {
        // Refresh each panel in the content area for real-time updates
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChartsDashboardPanel) {
                ((ChartsDashboardPanel) comp).refreshData();
            } else if (comp instanceof PersonalPanel) {
                ((PersonalPanel) comp).refreshData();
            }
        }
        
        // Force repaint of entire content panel
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void applyFilters() {
        filteredExpenses.clear();
        
        List<Expense> workingList = new ArrayList<>(allExpenses);
        
        // Category filter
        String selectedCategory = (String) filterCategoryBox.getSelectedItem();
        if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
            workingList = workingList.stream()
                .filter(e -> e.getCategory().equals(selectedCategory))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Period filter
        String selectedPeriod = (String) filterPeriodBox.getSelectedItem();
        if (selectedPeriod != null) {
            LocalDate now = LocalDate.now();
            switch (selectedPeriod) {
                case "Today":
                    workingList = workingList.stream()
                        .filter(e -> e.getDate().equals(now))
                        .collect(java.util.stream.Collectors.toList());
                    break;
                case "This Week":
                    LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
                    workingList = workingList.stream()
                        .filter(e -> !e.getDate().isBefore(weekStart))
                        .collect(java.util.stream.Collectors.toList());
                    break;
                case "This Month":
                    workingList = workingList.stream()
                        .filter(e -> e.getDate().getMonth() == now.getMonth() && 
                                   e.getDate().getYear() == now.getYear())
                        .collect(java.util.stream.Collectors.toList());
                    break;
            }
        }
        
        filteredExpenses = new ArrayList<>(workingList);
        refreshTable();
        updateStatistics();
    }

    private void resetFilters() {
        filterCategoryBox.setSelectedIndex(0);
        filterPeriodBox.setSelectedIndex(0);
        applyFilters();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        for (Expense expense : filteredExpenses) {
            tableModel.addRow(new Object[]{
                expense.getCategory(),
                String.format("$%.2f", expense.getAmount()),
                expense.getDescription(),
                expense.getDate().format(formatter),
                expense.isSplit() ? "Split" : "Personal"
            });
        }
    }

    private void updateStatistics() {
        LocalDate now = LocalDate.now();
        
        double total = filteredExpenses.stream().mapToDouble(Expense::getAmount).sum();
        
        double monthly = filteredExpenses.stream()
            .filter(e -> e.getDate().getMonth() == now.getMonth() && 
                        e.getDate().getYear() == now.getYear())
            .mapToDouble(Expense::getAmount).sum();
        
        LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
        double weekly = filteredExpenses.stream()
            .filter(e -> !e.getDate().isBefore(weekStart))
            .mapToDouble(Expense::getAmount).sum();
        
        totalExpenseLabel.setText(String.format("$%.2f", total));
        monthlyExpenseLabel.setText(String.format("$%.2f", monthly));
        weeklyExpenseLabel.setText(String.format("$%.2f", weekly));
        countLabel.setText(String.valueOf(filteredExpenses.size()));
    }

    private void updateCategoryFilter() {
        String currentSelection = (String) filterCategoryBox.getSelectedItem();
        filterCategoryBox.removeAllItems();
        filterCategoryBox.addItem("All Categories");
        
        List<String> categories = expenseManager.getUniqueCategories(currentUser);
        for (String category : categories) {
            filterCategoryBox.addItem(category);
        }
        
        if (currentSelection != null) {
            filterCategoryBox.setSelectedItem(currentSelection);
        }
    }

    private Expense getExpenseFromRow(int row) {
        if (row < 0 || row >= filteredExpenses.size()) return null;
        return filteredExpenses.get(row);
    }

    private void openBudgetSettings() {
        BudgetSettingsDialog dialog = new BudgetSettingsDialog(this, currentUser, budgetManager);
        dialog.setVisible(true);
    }

    private void openRecurringExpenses() {
        // Show dialog with recurring expenses list
        JOptionPane.showMessageDialog(this,
            "Recurring expenses are automatically processed on startup.\n" +
            "Add recurring expenses through 'Add Expense' form.",
            "Recurring Expenses",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        String[] options = {"Personal Report", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "Select report type to export:",
            "Export Report",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        
        if (choice == 0) {
            ReportGenerator generator = ReportFactory.createPersonalReport(currentUser, expenseManager);
            String report = generator.generate();
            
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String filename = "expense_report_" + timestamp + ".txt";
            
            if (generator.saveToFile(filename)) {
                UITheme.showMessage(this,
                    "✓ Report exported successfully!\nSaved to: " + filename,
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                UITheme.showMessage(this,
                    "Failed to export report",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        int confirm = UITheme.showConfirm(this, "Are you sure you want to logout?", "Logout");
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            AuthenticationController authController = new AuthenticationController();
            LoginView loginView = new LoginView(authController);
            loginView.setVisible(true);
        }
    }
}
