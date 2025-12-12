package com.paypilot.view;

import com.paypilot.controller.GroupController;
import com.paypilot.model.Group;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class SharedExpenseDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField categoryField;
    private JTextField amountField;
    private JTextArea descriptionArea;
    private JComboBox<String> strategyComboBox;
    private JComboBox<String> paidByComboBox;
    private JList<String> memberList;
    private boolean succeeded = false;
    
    private Group group;
    private GroupController groupController;
    private String currentUser;

    public SharedExpenseDialog(Window parent, Group group, GroupController groupController, String currentUser) {
        super(parent, "Add Shared Expense", ModalityType.APPLICATION_MODAL);
        this.group = group;
        this.groupController = groupController;
        this.currentUser = currentUser;
        
        UITheme.applyLookAndFeel();
        initializeUI();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(main);
        
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel title = new JLabel("Add Group Expense");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_COLOR);
        card.add(title, BorderLayout.NORTH);
        
        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        
        // Fields
        addFormRow(form, gbc, 0, "Category", categoryField = UITheme.createStyledTextField());
        addFormRow(form, gbc, 2, "Amount ($)", amountField = UITheme.createStyledTextField());
        
        // Description
        gbc.gridy = 4; form.add(new JLabel("Description"), gbc);
        gbc.gridy = 5; 
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        form.add(new JScrollPane(descriptionArea), gbc);
        
        // Paid By
        gbc.gridy = 6; form.add(new JLabel("Paid By"), gbc);
        gbc.gridy = 7;
        paidByComboBox = new JComboBox<>(group.getMembers().toArray(new String[0]));
        paidByComboBox.setSelectedItem(currentUser);
        paidByComboBox.setBackground(Color.WHITE);
        form.add(paidByComboBox, gbc);
        
        // Strategy
        gbc.gridy = 8; form.add(new JLabel("Split Method"), gbc);
        gbc.gridy = 9;
        strategyComboBox = new JComboBox<>(new String[]{"EQUAL", "WEIGHTED", "PERCENTAGE"});
        strategyComboBox.setBackground(Color.WHITE);
        form.add(strategyComboBox, gbc);
        
        // Participants
        gbc.gridy = 10; form.add(new JLabel("Participants"), gbc);
        gbc.gridy = 11;
        memberList = new JList<>(group.getMembers().toArray(new String[0]));
        memberList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // Select all by default
        int[] indices = new int[group.getMembers().size()];
        for(int i=0; i<indices.length; i++) indices[i]=i;
        memberList.setSelectedIndices(indices);
        form.add(new JScrollPane(memberList), gbc);
        
        card.add(form, BorderLayout.CENTER);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton cancel = UITheme.createSecondaryButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton add = UITheme.createPrimaryButton("Add Expense");
        add.addActionListener(e -> submit());
        
        btnPanel.add(cancel);
        btnPanel.add(add);
        card.add(btnPanel, BorderLayout.SOUTH);
        
        main.add(card, BorderLayout.CENTER);
    }
    
    private void addFormRow(JPanel p, GridBagConstraints gbc, int y, String lbl, JComponent cmp) {
        gbc.gridy = y;
        JLabel label = new JLabel(lbl);
        label.setFont(UITheme.FONT_BOLD);
        p.add(label, gbc);
        gbc.gridy = y+1;
        cmp.setPreferredSize(new Dimension(300, 35));
        p.add(cmp, gbc);
    }
    
    private void submit() {
        try {
            String cat = categoryField.getText();
            double amt = Double.parseDouble(amountField.getText());
            String paidBy = (String) paidByComboBox.getSelectedItem();
            String strat = (String) strategyComboBox.getSelectedItem();
            List<String> parts = memberList.getSelectedValuesList();
            
            if(parts.isEmpty()) throw new Exception("Select participants");
            
            groupController.addSharedExpense(cat, amt, descriptionArea.getText(), paidBy, group.getGroupName(), strat, parts, null);
            succeeded = true;
            dispose();
        } catch (Exception e) {
            UITheme.showMessage(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSucceeded() { return succeeded; }
}