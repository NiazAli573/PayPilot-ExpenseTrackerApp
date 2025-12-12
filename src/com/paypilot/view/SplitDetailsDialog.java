package com.paypilot.view;

import com.paypilot.model.Expense;
import com.paypilot.model.SplitDetail;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * SplitDetailsDialog - Displays split expense breakdown in a popup
 * Shows each participant's name and their share of the expense
 */
public class SplitDetailsDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public SplitDetailsDialog(Window parent, Expense expense) {
        super(parent, "Split Expense Details", ModalityType.APPLICATION_MODAL);
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header Section
        JPanel headerPanel = createHeaderPanel(expense);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Split Details Table
        JPanel tablePanel = createTablePanel(expense);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Footer with close button
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel(Expense expense) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Title
        JLabel titleLabel = new JLabel("💰 Split Expense Breakdown");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Expense Info
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setBackground(new Color(249, 250, 251));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            new EmptyBorder(12, 15, 12, 15)
        ));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel categoryLabel = new JLabel("Category: " + expense.getCategory());
        categoryLabel.setFont(UITheme.FONT_REGULAR);
        
        JLabel dateLabel = new JLabel("Date: " + expense.getDate().format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.setFont(UITheme.FONT_REGULAR);
        
        JLabel totalLabel = new JLabel("Total Amount: $" + String.format("%.2f", expense.getAmount()));
        totalLabel.setFont(UITheme.FONT_BOLD);
        totalLabel.setForeground(UITheme.SUCCESS);
        
        JLabel participantsLabel = new JLabel("Participants: " + 
            (expense.getSplitDetails() != null ? expense.getSplitDetails().size() : 0));
        participantsLabel.setFont(UITheme.FONT_REGULAR);
        
        infoPanel.add(categoryLabel);
        infoPanel.add(dateLabel);
        infoPanel.add(totalLabel);
        infoPanel.add(participantsLabel);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(infoPanel);
        
        return panel;
    }
    
    private JPanel createTablePanel(Expense expense) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Section Title
        JLabel sectionTitle = new JLabel("Individual Shares");
        sectionTitle.setFont(UITheme.FONT_BOLD);
        sectionTitle.setForeground(UITheme.TEXT_DARK);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(sectionTitle, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"#", "Person Name", "Amount to Pay", "Percentage"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ArrayList<SplitDetail> splitDetails = expense.getSplitDetails();
        double totalAmount = expense.getAmount();
        
        if (splitDetails != null && !splitDetails.isEmpty()) {
            int index = 1;
            for (SplitDetail detail : splitDetails) {
                double percentage = (detail.getAmount() / totalAmount) * 100;
                model.addRow(new Object[]{
                    index++,
                    detail.getPersonName(),
                    String.format("$%.2f", detail.getAmount()),
                    String.format("%.1f%%", percentage)
                });
            }
        } else {
            model.addRow(new Object[]{"", "No split details available", "", ""});
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(UITheme.FONT_REGULAR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(UITheme.BORDER_COLOR);
        table.setSelectionBackground(new Color(238, 242, 255));
        
        // Header styling
        table.getTableHeader().setFont(UITheme.FONT_BOLD);
        table.getTableHeader().setBackground(new Color(249, 250, 251));
        table.getTableHeader().setForeground(UITheme.TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        
        // Amount column styling
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(UITheme.SUCCESS);
                setFont(UITheme.FONT_BOLD);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        
        // Percentage column styling
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(UITheme.PRIMARY_COLOR);
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton closeBtn = UITheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        
        panel.add(closeBtn);
        
        return panel;
    }
}
