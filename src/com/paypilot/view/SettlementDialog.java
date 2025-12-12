package com.paypilot.view;

import com.paypilot.controller.SettlementOptimizer;
import com.paypilot.controller.SettlementOptimizer.Transaction;
import com.paypilot.controller.SettlementOptimizer.SettlementSavings;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * SettlementDialog - Shows optimized settlement plan for group expenses
 * Displays minimal transactions needed to settle all balances
 */
public class SettlementDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private Map<String, Double> balances;
    private List<Transaction> transactions;
    private SettlementSavings savings;
    
    public SettlementDialog(Window parent, String groupName, Map<String, Double> balances) {
        super(parent, "Settlement Plan - " + groupName, ModalityType.APPLICATION_MODAL);
        this.balances = balances;
        this.transactions = SettlementOptimizer.optimizeSettlement(balances);
        this.savings = SettlementOptimizer.calculateSavings(balances);
        
        initializeUI();
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(UITheme.BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        
        // Savings card
        contentPanel.add(createSavingsCard(), BorderLayout.NORTH);
        
        // Transactions table
        contentPanel.add(createTransactionsPanel(), BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(UITheme.BG_COLOR);
        
        JButton closeBtn = UITheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        
        JButton copyBtn = UITheme.createSecondaryButton("Copy to Clipboard");
        copyBtn.addActionListener(e -> copyToClipboard());
        
        footerPanel.add(copyBtn);
        footerPanel.add(closeBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("💸 Optimized Settlement Plan");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel("Minimal transactions to settle all balances");
        subtitleLabel.setFont(UITheme.FONT_REGULAR);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private UITheme.RoundedPanel createSavingsCard() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, new Color(240, 253, 244));
        card.setLayout(new GridLayout(1, 3, 20, 0));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Direct transactions
        JPanel directPanel = createStatPanel(
            String.valueOf(savings.directTransactions),
            "Direct Transactions",
            UITheme.TEXT_MUTED
        );
        
        // Optimized transactions
        JPanel optimizedPanel = createStatPanel(
            String.valueOf(savings.optimizedTransactions),
            "Optimized Transactions",
            UITheme.SUCCESS
        );
        
        // Saved
        JPanel savedPanel = createStatPanel(
            String.valueOf(savings.transactionsSaved),
            "Transactions Saved",
            UITheme.PRIMARY_COLOR
        );
        
        card.add(directPanel);
        card.add(optimizedPanel);
        card.add(savedPanel);
        
        return card;
    }
    
    private JPanel createStatPanel(String value, String label, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(UITheme.FONT_REGULAR);
        textLabel.setForeground(UITheme.TEXT_DARK);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(textLabel);
        
        return panel;
    }
    
    private UITheme.RoundedPanel createTransactionsPanel() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, Color.WHITE);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Settlement Instructions");
        titleLabel.setFont(UITheme.FONT_BOLD);
        titleLabel.setForeground(UITheme.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        if (transactions.isEmpty()) {
            JLabel noTransLabel = new JLabel("All balances are settled!");
            noTransLabel.setFont(UITheme.FONT_REGULAR);
            noTransLabel.setForeground(UITheme.SUCCESS);
            noTransLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noTransLabel, BorderLayout.CENTER);
        } else {
            // Create table
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"#", "From", "To", "Amount"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            int index = 1;
            for (Transaction trans : transactions) {
                model.addRow(new Object[]{
                    index++,
                    trans.from,
                    "→ " + trans.to,
                    String.format("$%.2f", trans.amount)
                });
            }
            
            JTable table = new JTable(model);
            table.setRowHeight(45);
            table.setFont(UITheme.FONT_REGULAR);
            table.setShowVerticalLines(false);
            table.getTableHeader().setFont(UITheme.FONT_BOLD);
            table.getTableHeader().setBackground(new Color(249, 250, 251));
            table.setSelectionBackground(new Color(238, 242, 255));
            
            // Column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(40);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            
            // Amount column styling
            table.getColumnModel().getColumn(3).setCellRenderer(
                (tbl, value, isSelected, hasFocus, row, column) -> {
                    JLabel label = new JLabel(value.toString());
                    label.setFont(UITheme.FONT_BOLD);
                    label.setForeground(UITheme.SUCCESS);
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    label.setOpaque(true);
                    label.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    label.setBorder(new EmptyBorder(0, 10, 0, 10));
                    return label;
                }
            );
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
            panel.add(scrollPane, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private void copyToClipboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("Settlement Plan\n");
        sb.append("═══════════════\n\n");
        
        for (Transaction trans : transactions) {
            sb.append(trans.toString()).append("\n");
        }
        
        sb.append("\nTransactions: ").append(transactions.size());
        sb.append("\nSaved: ").append(savings.transactionsSaved).append(" transactions");
        
        java.awt.datatransfer.StringSelection selection = 
            new java.awt.datatransfer.StringSelection(sb.toString());
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        
        UITheme.showMessage(this, "Settlement plan copied to clipboard!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
