package com.paypilot.view;

import com.paypilot.model.SplitDetail;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SplitExpenseDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private DefaultTableModel tableModel;
    private JTextField nameField, amountField;
    private JLabel remainingLbl;
    private double totalAmount;
    private ArrayList<SplitDetail> splits;
    private boolean succeeded = false;
    private DecimalFormat df = new DecimalFormat("#0.00");

    public SplitExpenseDialog(Window parent, double totalAmount, ArrayList<SplitDetail> existing) {
        super(parent, "Split Expense", ModalityType.APPLICATION_MODAL);
        this.totalAmount = totalAmount;
        this.splits = existing != null ? new ArrayList<>(existing) : new ArrayList<>();
        
        UITheme.applyLookAndFeel();
        initializeUI();
        refresh();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(UITheme.BG_COLOR);
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(main);
        
        // Info Card
        UITheme.RoundedPanel top = new UITheme.RoundedPanel(10, Color.WHITE);
        top.setLayout(new BorderLayout());
        top.setBorder(new EmptyBorder(10, 15, 10, 15));
        remainingLbl = new JLabel("Remaining: $" + df.format(totalAmount));
        remainingLbl.setFont(UITheme.FONT_TITLE);
        remainingLbl.setForeground(UITheme.PRIMARY_COLOR);
        top.add(remainingLbl, BorderLayout.CENTER);
        JButton equalBtn = UITheme.createSecondaryButton("Split Equally");
        equalBtn.addActionListener(e -> splitEqually());
        top.add(equalBtn, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);
        
        // Content Card
        UITheme.RoundedPanel content = new UITheme.RoundedPanel(10, Color.WHITE);
        content.setLayout(new BorderLayout(0, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Input
        JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT));
        input.setOpaque(false);
        nameField = UITheme.createStyledTextField(); nameField.setPreferredSize(new Dimension(100, 30));
        amountField = UITheme.createStyledTextField(); amountField.setPreferredSize(new Dimension(80, 30));
        JButton addBtn = UITheme.createPrimaryButton("Add");
        addBtn.addActionListener(e -> addSplit());
        
        input.add(new JLabel("Name:")); input.add(nameField);
        input.add(new JLabel("Amount:")); input.add(amountField);
        input.add(addBtn);
        content.add(input, BorderLayout.NORTH);
        
        // Table
        tableModel = new DefaultTableModel(new String[]{"Person", "Amount", "%"}, 0);
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        
        main.add(content, BorderLayout.CENTER);
        
        // Bottom
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setOpaque(false);
        JButton save = UITheme.createPrimaryButton("Confirm");
        save.addActionListener(e -> { succeeded=true; dispose(); });
        bot.add(save);
        main.add(bot, BorderLayout.SOUTH);
    }
    
    private void addSplit() {
        try {
            double amt = Double.parseDouble(amountField.getText());
            splits.add(new SplitDetail(nameField.getText(), amt));
            refresh();
        } catch(Exception e) {}
    }
    
    private void splitEqually() {
        String s = JOptionPane.showInputDialog("How many people?");
        try {
            int n = Integer.parseInt(s);
            double share = totalAmount / n;
            splits.clear();
            for(int i=1; i<=n; i++) splits.add(new SplitDetail("Person "+i, share));
            refresh();
        } catch(Exception e) {}
    }
    
    private void refresh() {
        tableModel.setRowCount(0);
        double sum = 0;
        for(SplitDetail s : splits) {
            sum += s.getAmount();
            tableModel.addRow(new Object[]{s.getPersonName(), df.format(s.getAmount()), df.format(s.getAmount()/totalAmount*100)+"%"});
        }
        double rem = totalAmount - sum;
        remainingLbl.setText("Remaining: $" + df.format(rem));
        remainingLbl.setForeground(Math.abs(rem) < 0.01 ? UITheme.SUCCESS : UITheme.DANGER);
    }
    
    public boolean isSucceeded() { return succeeded; }
    public ArrayList<SplitDetail> getSplitDetails() { return splits; }
}