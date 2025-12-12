package com.paypilot.view;

import com.paypilot.controller.*;
import com.paypilot.controller.report.ReportFactory;
import com.paypilot.controller.report.ReportGenerator;
import com.paypilot.model.Expense;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainDashboard extends JFrame {
    private static final long serialVersionUID = 1L;

    private ExpenseManager expenseManager;
    private GroupController groupController;
    private UndoManager undoManager;
    private RecurringExpenseManager recurringExpenseManager;
    private String currentUser;

    private ArrayList<Expense> expenses;
    private ArrayList<Expense> filteredExpenses;

    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> filterCategoryBox;
    private JLabel totalLabel, avgLabel, maxLabel;

    public MainDashboard(String username) {
        this.currentUser = username;
        this.expenseManager = new ExpenseManager();
        this.groupController = new GroupController();
        this.undoManager = new UndoManager();
        this.recurringExpenseManager = new RecurringExpenseManager(expenseManager);
        this.expenses = new ArrayList<>();
        this.filteredExpenses = new ArrayList<>();

        UITheme.applyLookAndFeel();
        
        // Process any due recurring expenses
        processRecurringExpenses();
        
        initializeUI();
        loadExpenses();
        setVisible(true);
    }
    
    /**
     * Process recurring expenses on startup
     */
    private void processRecurringExpenses() {
        int generated = recurringExpenseManager.processDueRecurringExpenses(currentUser);
        if (generated > 0) {
            System.out.println("Generated " + generated + " recurring expenses");
        }
    }

    private void initializeUI() {
        setTitle("PayPilot | " + currentUser);
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UITheme.BG_COLOR);

        mainContainer.add(createSidebar(), BorderLayout.WEST);
        mainContainer.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(mainContainer);
    }

    // --- SIDEBAR FIX ---
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_COLOR);
        
        // FIX: Increased width to 280 to stop text cutoff
        sidebar.setPreferredSize(new Dimension(280, getHeight())); 
        sidebar.setBorder(new EmptyBorder(40, 0, 30, 0)); 

        // 1. APP TITLE
        JLabel appTitle = new JLabel("PayPilot");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Adjusted font size
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(appTitle);
        sidebar.add(Box.createVerticalStrut(60)); 

        // 2. MENU BUTTONS
        sidebar.add(createSidebarMenuBtn("Dashboard", e -> {}));
        sidebar.add(Box.createVerticalStrut(10));
        
        sidebar.add(createSidebarMenuBtn("📊 Charts", e -> openChartsPanel()));
        sidebar.add(Box.createVerticalStrut(10));
        
        sidebar.add(createSidebarMenuBtn("My Groups", e -> openGroupPanel()));
        sidebar.add(Box.createVerticalStrut(10));
        
        sidebar.add(createSidebarMenuBtn("Reports", e -> exportReport()));

        sidebar.add(Box.createVerticalGlue()); 

        // 3. USER PROFILE
        JLabel userLabel = new JLabel(currentUser);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(new Color(200, 200, 200));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userLabel);
        
        sidebar.add(Box.createVerticalStrut(15));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setForeground(UITheme.DANGER);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> logout());
        
        sidebar.add(logoutBtn);
        
        return sidebar;
    }

    private JButton createSidebarMenuBtn(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 50)); 
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(UITheme.PRIMARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
        });
        
        btn.addActionListener(action);
        return btn;
    }

    // --- MAIN CONTENT ---
    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setBackground(UITheme.BG_COLOR);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        content.add(createHeader(), BorderLayout.NORTH);
        
        JPanel body = new JPanel(new BorderLayout(0, 25));
        body.setOpaque(false);
        body.add(createStatsRow(), BorderLayout.NORTH);
        body.add(createTableSection(), BorderLayout.CENTER);
        
        content.add(body, BorderLayout.CENTER);
        return content;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton undoBtn = UITheme.createSecondaryButton("Undo");
        undoBtn.addActionListener(e -> undo());
        
        JButton redoBtn = UITheme.createSecondaryButton("Redo");
        redoBtn.addActionListener(e -> redo());
        
        JButton addBtn = UITheme.createPrimaryButton("+ Add Expense");
        addBtn.addActionListener(e -> openAddExpenseForm());

        actions.add(undoBtn);
        actions.add(redoBtn);
        actions.add(addBtn);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        
        return header;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 30, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 130));

        totalLabel = new JLabel("$0.00");
        avgLabel = new JLabel("$0.00");
        maxLabel = new JLabel("$0.00");

        row.add(createStatCard("Total Expenses", totalLabel, UITheme.PRIMARY_COLOR));
        row.add(createStatCard("Average Spend", avgLabel, UITheme.SUCCESS));
        row.add(createStatCard("Highest Spend", maxLabel, UITheme.DANGER));

        return row;
    }

    private JPanel createStatCard(String title, JLabel valueLbl, Color accent) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_REGULAR);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLbl.setForeground(UITheme.TEXT_DARK);
        
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(50, 4));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(titleLbl, BorderLayout.NORTH);
        content.add(valueLbl, BorderLayout.CENTER);
        
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableSection() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(15, Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setFont(UITheme.FONT_BOLD);
        
        filterCategoryBox = new JComboBox<>(new String[]{"All Categories"});
        filterCategoryBox.setBackground(Color.WHITE);
        filterCategoryBox.addActionListener(e -> filterByCategory());

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> resetFilter());
        
        JButton sortBtn = UITheme.createSecondaryButton("Sort Amount");
        sortBtn.addActionListener(e -> sortByAmount());

        toolbar.add(filterLbl);
        toolbar.add(filterCategoryBox);
        toolbar.add(resetBtn);
        toolbar.add(sortBtn);
        
        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTools.setBackground(Color.WHITE);
        
        JButton editBtn = UITheme.createSecondaryButton("Edit Selected");
        editBtn.addActionListener(e -> editExpense());
        
        JButton delBtn = UITheme.createDangerButton("Delete");
        delBtn.addActionListener(e -> deleteExpenseWithCommand());
        
        rightTools.add(editBtn);
        rightTools.add(delBtn);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.add(toolbar, BorderLayout.WEST);
        topBar.add(rightTools, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Category", "Amount", "Description", "Date", "Type"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // --- LOGIC ---
    private void openAddExpenseForm() {
        AddExpenseForm form = new AddExpenseForm(this, currentUser);
        form.setVisible(true);
        if (form.isSucceeded()) {
            Command addCommand = new AddExpenseCommand(expenseManager, currentUser, form.getExpense());
            undoManager.executeCommand(addCommand);
            
            // Handle recurring expense
            if (form.isRecurring()) {
                com.paypilot.model.RecurringExpense recurring = form.getRecurringExpense();
                if (recurring != null) {
                    recurringExpenseManager.addRecurringExpense(recurring);
                    UITheme.showMessage(this, 
                        "Expense added and set as recurring!\nIt will auto-generate based on schedule.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            loadExpenses();
        }
    }

    private void editExpense() {
        int selected = table.getSelectedRow();
        if (selected == -1) { UITheme.showMessage(this, "Select an expense to edit", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        Expense old = getExpenseFromTableRow(selected);
        if (old == null) return;
        
        AddExpenseForm form = new AddExpenseForm(this, currentUser, old);
        form.setVisible(true);
        if (form.isSucceeded()) {
            Command editCommand = new EditExpenseCommand(expenseManager, currentUser, old, form.getExpense());
            undoManager.executeCommand(editCommand);
            loadExpenses();
        }
    }

    private void deleteExpenseWithCommand() {
        int selected = table.getSelectedRow();
        if (selected == -1) return;
        if (UITheme.showConfirm(this, "Delete this expense?", "Confirm") == JOptionPane.YES_OPTION) {
            Expense e = getExpenseFromTableRow(selected);
            if(e != null) {
                undoManager.executeCommand(new DeleteExpenseCommand(expenseManager, currentUser, e));
                loadExpenses();
            }
        }
    }

    private void undo() { if(undoManager.undo()) loadExpenses(); }
    private void redo() { if(undoManager.redo()) loadExpenses(); }
    
    private void loadExpenses() {
        expenses = expenseManager.getAllExpenses(currentUser);
        updateCategoryFilter();
        refreshTable();
        updateStatistics();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Expense> list = filteredExpenses.isEmpty() ? expenses : filteredExpenses;
        for (Expense exp : list) {
            tableModel.addRow(new Object[]{
                exp.getCategory(),
                String.format("$%.2f", exp.getAmount()),
                exp.getDescription(),
                exp.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                exp.isSplit() ? "Split" : "Personal"
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
    
    private Expense getExpenseFromTableRow(int row) {
        String category = (String) tableModel.getValueAt(row, 0);
        String amountStr = (String) tableModel.getValueAt(row, 1);
        double amount = Double.parseDouble(amountStr.replace("$", ""));
        String desc = (String) tableModel.getValueAt(row, 2);
        return expenses.stream()
            .filter(e -> e.getCategory().equals(category) && Math.abs(e.getAmount() - amount) < 0.01 && e.getDescription().equals(desc))
            .findFirst().orElse(null);
    }

    private void filterByCategory() {
        String sel = (String) filterCategoryBox.getSelectedItem();
        if (sel == null || "All Categories".equals(sel)) {
            filteredExpenses.clear();
        } else {
            filteredExpenses = expenseManager.filterByCategory(currentUser, sel);
        }
        refreshTable();
        updateStatistics();
    }
    
    private void resetFilter() {
        if(filterCategoryBox.getItemCount() > 0) filterCategoryBox.setSelectedIndex(0);
        filteredExpenses.clear();
        refreshTable();
        updateStatistics();
    }

    private void updateCategoryFilter() {
        String current = (String) filterCategoryBox.getSelectedItem();
        filterCategoryBox.removeAllItems();
        filterCategoryBox.addItem("All Categories");
        for(String cat : expenseManager.getUniqueCategories(currentUser)) {
            filterCategoryBox.addItem(cat);
        }
        if(current != null) filterCategoryBox.setSelectedItem(current);
    }
    
    private void sortByAmount() {
        List<Expense> list = filteredExpenses.isEmpty() ? expenses : filteredExpenses;
        list.sort((e1, e2) -> Double.compare(e2.getAmount(), e1.getAmount()));
        refreshTable();
    }

    private void openGroupPanel() { new GroupPanel(this, currentUser, groupController).setVisible(true); }
    
    private void openChartsPanel() {
        JDialog chartsDialog = new JDialog(this, "Expense Analytics", true);
        chartsDialog.setContentPane(new ChartsDashboardPanel(currentUser, expenseManager));
        chartsDialog.setSize(1000, 700);
        chartsDialog.setLocationRelativeTo(this);
        chartsDialog.setVisible(true);
    }
    
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportGenerator r = ReportFactory.createPersonalReport(currentUser, expenseManager);
                r.generate();
                r.saveToFile(fileChooser.getSelectedFile().getAbsolutePath());
                UITheme.showMessage(this, "Export Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginView(new AuthenticationController()).setVisible(true));
    }
}