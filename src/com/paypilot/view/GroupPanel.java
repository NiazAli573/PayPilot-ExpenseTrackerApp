package com.paypilot.view;

import com.paypilot.controller.GroupController;
import com.paypilot.controller.report.ReportFactory;
import com.paypilot.controller.report.ReportGenerator;
import com.paypilot.model.Group;
import com.paypilot.model.SharedExpense;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class GroupPanel extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private GroupController groupController;
    private String currentUser;
    private DefaultTableModel groupTableModel;
    private DefaultTableModel expenseTableModel;
    private JTable groupTable;
    private JTable expenseTable;
    private JTextArea balanceTextArea;

    public GroupPanel(Frame parent, String currentUser, GroupController groupController) {
        super(parent, "Group Management", true);
        this.currentUser = currentUser;
        this.groupController = groupController;
        
        UITheme.applyLookAndFeel();
        initializeUI();
        loadGroups();
        
        setSize(1000, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout(15, 15));
        mainContainer.setBackground(UITheme.BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainContainer);
        
        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Group Expense Management");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // --- Center Content (Split Pane) ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        
        // 1. LEFT SIDE: Groups List
        UITheme.RoundedPanel leftCard = new UITheme.RoundedPanel(15, Color.WHITE);
        leftCard.setLayout(new BorderLayout(0, 10));
        leftCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel groupLabel = new JLabel("My Groups");
        groupLabel.setFont(UITheme.FONT_SUBTITLE);
        groupLabel.setForeground(UITheme.PRIMARY_COLOR);
        
        groupTableModel = new DefaultTableModel(new String[]{"Group Name", "Members"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        groupTable = new JTable(groupTableModel);
        UITheme.styleTable(groupTable);
        groupTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadGroupExpenses();
        });
        
        JScrollPane groupScroll = new JScrollPane(groupTable);
        groupScroll.setBorder(BorderFactory.createEmptyBorder());
        groupScroll.getViewport().setBackground(Color.WHITE);
        
        leftCard.add(groupLabel, BorderLayout.NORTH);
        leftCard.add(groupScroll, BorderLayout.CENTER);
        
        // 2. RIGHT SIDE: Expenses & Balances
        UITheme.RoundedPanel rightCard = new UITheme.RoundedPanel(15, Color.WHITE);
        rightCard.setLayout(new BorderLayout(0, 10));
        rightCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel expenseLabel = new JLabel("Transactions & Balances");
        expenseLabel.setFont(UITheme.FONT_SUBTITLE);
        expenseLabel.setForeground(UITheme.PRIMARY_COLOR);
        
        expenseTableModel = new DefaultTableModel(new String[]{"Category", "Amount", "Paid By", "Date"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        expenseTable = new JTable(expenseTableModel);
        UITheme.styleTable(expenseTable);
        
        JScrollPane expenseScroll = new JScrollPane(expenseTable);
        expenseScroll.setBorder(BorderFactory.createEmptyBorder());
        expenseScroll.getViewport().setBackground(Color.WHITE);
        expenseScroll.setPreferredSize(new Dimension(0, 250));
        
        // Balance Area
        balanceTextArea = new JTextArea();
        balanceTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        balanceTextArea.setEditable(false);
        balanceTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        balanceTextArea.setBackground(new Color(249, 250, 251));
        
        JPanel rightSplit = new JPanel(new BorderLayout(0, 10));
        rightSplit.setOpaque(false);
        rightSplit.add(expenseScroll, BorderLayout.CENTER);
        rightSplit.add(new JScrollPane(balanceTextArea), BorderLayout.SOUTH);
        
        rightCard.add(expenseLabel, BorderLayout.NORTH);
        rightCard.add(rightSplit, BorderLayout.CENTER);
        
        centerPanel.add(leftCard);
        centerPanel.add(rightCard);
        mainContainer.add(centerPanel, BorderLayout.CENTER);
        
        // --- Bottom Actions ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton createBtn = UITheme.createPrimaryButton("+ Create Group");
        createBtn.addActionListener(e -> createGroup());
        
        JButton addMemBtn = UITheme.createSecondaryButton("Add Member");
        addMemBtn.addActionListener(e -> addMember());

        JButton addExpBtn = UITheme.createSecondaryButton("Add Shared Expense");
        addExpBtn.addActionListener(e -> addSharedExpense());
        
        JButton settleBtn = UITheme.createSecondaryButton("💸 Optimize Settlement");
        settleBtn.addActionListener(e -> optimizeSettlement());
        
        JButton reportBtn = UITheme.createSecondaryButton("Report");
        reportBtn.addActionListener(e -> exportGroupReport());
        
        JButton closeBtn = UITheme.createDangerButton("Close");
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(createBtn);
        buttonPanel.add(addMemBtn);
        buttonPanel.add(addExpBtn);
        buttonPanel.add(settleBtn);
        buttonPanel.add(reportBtn);
        buttonPanel.add(closeBtn);
        
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadGroups() {
        groupTableModel.setRowCount(0);
        ArrayList<Group> groups = groupController.getUserGroups(currentUser);
        for (Group group : groups) {
            groupTableModel.addRow(new Object[]{ group.getGroupName(), group.getMembers().size() + " members" });
        }
    }
    
    private void loadGroupExpenses() {
        int selectedRow = groupTable.getSelectedRow();
        if (selectedRow == -1) {
            expenseTableModel.setRowCount(0);
            balanceTextArea.setText("");
            return;
        }
        
        String groupName = (String) groupTableModel.getValueAt(selectedRow, 0);
        expenseTableModel.setRowCount(0);
        
        ArrayList<SharedExpense> expenses = groupController.getGroupExpenses(groupName);
        for (SharedExpense exp : expenses) {
            expenseTableModel.addRow(new Object[]{
                exp.getCategory(),
                String.format("$%.2f", exp.getTotalAmount()),
                exp.getPaidByUsername(),
                exp.getDate().format(DateTimeFormatter.ofPattern("MMM dd"))
            });
        }
        
        Map<String, Double> balances = groupController.calculateGroupBalances(groupName);
        StringBuilder sb = new StringBuilder("NET BALANCES:\n");
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            double val = entry.getValue();
            String status = Math.abs(val) < 0.01 ? "Settled" : (val > 0 ? "Gets back" : "Owes");
            sb.append(String.format("%-15s : $%-8.2f (%s)\n", entry.getKey(), Math.abs(val), status));
        }
        balanceTextArea.setText(sb.toString());
    }
    
    private void createGroup() {
        String name = JOptionPane.showInputDialog(this, "Enter Group Name:");
        if (name != null && !name.trim().isEmpty()) {
            if (groupController.createGroup(name.trim(), currentUser)) {
                loadGroups();
            } else {
                UITheme.showMessage(this, "Group exists or invalid", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addMember() {
        int row = groupTable.getSelectedRow();
        if (row == -1) { UITheme.showMessage(this, "Select a group first", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        String groupName = (String) groupTableModel.getValueAt(row, 0);
        
        String user = JOptionPane.showInputDialog(this, "Enter username to add:");
        if (user != null && !user.trim().isEmpty()) {
            if (groupController.addMemberToGroup(groupName, user.trim())) {
                loadGroups();
                loadGroupExpenses();
                UITheme.showMessage(this, "Member Added", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                UITheme.showMessage(this, "Failed to add member", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addSharedExpense() {
        int row = groupTable.getSelectedRow();
        if (row == -1) { UITheme.showMessage(this, "Select a group first", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        
        String groupName = (String) groupTableModel.getValueAt(row, 0);
        Group group = groupController.getGroup(groupName);
        
        SharedExpenseDialog dialog = new SharedExpenseDialog(this, group, groupController, currentUser);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) loadGroupExpenses();
    }
    
    private void exportGroupReport() {
        int row = groupTable.getSelectedRow();
        if (row == -1) return;
        String groupName = (String) groupTableModel.getValueAt(row, 0);
        
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Group_" + groupName + ".txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportGenerator gen = ReportFactory.createGroupReport(groupName, groupController);
                gen.generate();
                gen.saveToFile(fc.getSelectedFile().getAbsolutePath());
                UITheme.showMessage(this, "Report Saved", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    private void optimizeSettlement() {
        int row = groupTable.getSelectedRow();
        if (row == -1) {
            UITheme.showMessage(this, "Please select a group first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String groupName = (String) groupTableModel.getValueAt(row, 0);
        Map<String, Double> balances = groupController.calculateGroupBalances(groupName);
        
        SettlementDialog dialog = new SettlementDialog(this, groupName, balances);
        dialog.setVisible(true);
    }
}