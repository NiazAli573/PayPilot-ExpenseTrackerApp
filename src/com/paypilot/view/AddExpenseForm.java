package com.paypilot.view;

import com.paypilot.model.Expense;
import com.paypilot.model.SplitDetail;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class AddExpenseForm extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField categoryField, amountField, descriptionField;
    private JButton splitButton;
    private JCheckBox recurringCheckbox;
    private JComboBox<String> recurrenceTypeBox;
    private JTextField customDaysField;
    private JPanel recurringPanel;
    private JButton attachReceiptBtn;
    private JLabel receiptLabel;
    
    private boolean succeeded = false;
    private Expense expense;
    private String username;
    private boolean isSplitMode = false;
    private boolean isRecurring = false;
    private ArrayList<SplitDetail> splitDetails = new ArrayList<>();
    private String receiptFilePath = null;

    // Constructor for New Expense
    public AddExpenseForm(Frame parent, String username) { 
        this(parent, username, null); 
    }

    // Constructor for Editing
    public AddExpenseForm(Frame parent, String username, Expense existing) {
        super(parent, existing == null ? "New Expense" : "Edit Expense", true);
        this.username = username;
        this.expense = existing;
        
        if(existing != null) {
            isSplitMode = existing.isSplit();
            if(existing.getSplitDetails() != null) {
                splitDetails = new ArrayList<>(existing.getSplitDetails());
            }
        }
        
        UITheme.applyLookAndFeel();
        initializeUI();
        if(existing != null) populate();
        
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UITheme.BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainContainer);

        // Modern Card
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(15, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0;
        
        // --- Header ---
        gbc.gridy = 0;
        JLabel title = new JLabel(expense == null ? "Add Expense" : "Edit Expense");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_COLOR);
        card.add(title, gbc);
        
        // --- Fields ---
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 0);
        card.add(createLabel("Category"), gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        categoryField = UITheme.createStyledTextField();
        categoryField.setPreferredSize(new Dimension(300, 40));
        card.add(categoryField, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createLabel("Amount ($)"), gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        amountField = UITheme.createStyledTextField();
        amountField.setPreferredSize(new Dimension(300, 40));
        card.add(amountField, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createLabel("Description"), gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        descriptionField = UITheme.createStyledTextField();
        descriptionField.setPreferredSize(new Dimension(300, 40));
        card.add(descriptionField, gbc);
        
        // --- Recurring Expense Section ---
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 5, 0);
        recurringCheckbox = new JCheckBox("Mark as Recurring");
        recurringCheckbox.setFont(UITheme.FONT_BOLD);
        recurringCheckbox.setForeground(UITheme.PRIMARY_COLOR);
        recurringCheckbox.setOpaque(false);
        recurringCheckbox.addActionListener(e -> toggleRecurringPanel());
        card.add(recurringCheckbox, gbc);
        
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 20, 0);
        recurringPanel = createRecurringPanel();
        recurringPanel.setVisible(false);
        card.add(recurringPanel, gbc);
        
        mainContainer.add(card, BorderLayout.CENTER);
        
        // --- Footer Actions ---
        JPanel footerContainer = new JPanel(new BorderLayout());
        footerContainer.setOpaque(false);
        footerContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Left side - Receipt attachment
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftActions.setOpaque(false);
        
        attachReceiptBtn = UITheme.createSecondaryButton("📎 Attach Receipt");
        attachReceiptBtn.addActionListener(e -> attachReceipt());
        
        receiptLabel = new JLabel("");
        receiptLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        receiptLabel.setForeground(UITheme.SUCCESS);
        
        leftActions.add(attachReceiptBtn);
        leftActions.add(receiptLabel);
        
        // Right side - Action buttons
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightActions.setOpaque(false);
        
        splitButton = UITheme.createSecondaryButton("Split Options");
        splitButton.addActionListener(e -> openSplitDialog());
        
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        
        JButton saveBtn = UITheme.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> submit());
        
        rightActions.add(splitButton);
        rightActions.add(cancelBtn);
        rightActions.add(saveBtn);
        
        footerContainer.add(leftActions, BorderLayout.WEST);
        footerContainer.add(rightActions, BorderLayout.EAST);
        
        mainContainer.add(footerContainer, BorderLayout.SOUTH);
        
        updateSplitButton();
    }
    
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BOLD);
        // This fixed your TEXT_SECONDARY error
        l.setForeground(UITheme.TEXT_MUTED); 
        return l;
    }
    
    private void populate() {
        categoryField.setText(expense.getCategory());
        amountField.setText(String.valueOf(expense.getAmount()));
        descriptionField.setText(expense.getDescription());
    }
    
    private void openSplitDialog() {
        try {
            String txt = amountField.getText().trim();
            if(txt.isEmpty()) throw new NumberFormatException();
            double amt = Double.parseDouble(txt);
            
            SplitExpenseDialog d = new SplitExpenseDialog(this, amt, splitDetails);
            d.setVisible(true);
            if(d.isSucceeded()) {
                isSplitMode = true;
                splitDetails = d.getSplitDetails();
                updateSplitButton();
            }
        } catch (NumberFormatException e) {
            UITheme.showMessage(this, "Enter a valid amount first", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void updateSplitButton() {
        if(isSplitMode && !splitDetails.isEmpty()) {
            splitButton.setText("Split Active (" + splitDetails.size() + ")");
            splitButton.setForeground(UITheme.PRIMARY_COLOR);
        } else {
            splitButton.setText("Split Options");
            splitButton.setForeground(UITheme.TEXT_DARK);
        }
    }
    
    private JPanel createRecurringPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel typeLabel = new JLabel("Frequency:");
        typeLabel.setFont(UITheme.FONT_REGULAR);
        
        recurrenceTypeBox = new JComboBox<>(new String[]{"Weekly", "Monthly", "Custom Days"});
        recurrenceTypeBox.setFont(UITheme.FONT_REGULAR);
        recurrenceTypeBox.addActionListener(e -> toggleCustomDaysField());
        
        JLabel daysLabel = new JLabel("Every");
        daysLabel.setFont(UITheme.FONT_REGULAR);
        
        customDaysField = UITheme.createStyledTextField();
        customDaysField.setPreferredSize(new Dimension(60, 30));
        customDaysField.setText("7");
        customDaysField.setVisible(false);
        
        JLabel daysLabel2 = new JLabel("days");
        daysLabel2.setFont(UITheme.FONT_REGULAR);
        daysLabel2.setVisible(false);
        
        panel.add(typeLabel);
        panel.add(recurrenceTypeBox);
        panel.add(daysLabel);
        panel.add(customDaysField);
        panel.add(daysLabel2);
        
        return panel;
    }
    
    private void toggleRecurringPanel() {
        isRecurring = recurringCheckbox.isSelected();
        recurringPanel.setVisible(isRecurring);
        pack();
    }
    
    private void toggleCustomDaysField() {
        boolean isCustom = "Custom Days".equals(recurrenceTypeBox.getSelectedItem());
        customDaysField.setVisible(isCustom);
        Component daysLabel = recurringPanel.getComponent(4); // "days" label
        daysLabel.setVisible(isCustom);
        pack();
    }
    
    private void attachReceipt() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Receipt Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            receiptFilePath = selectedFile.getAbsolutePath();
            receiptLabel.setText("✓ " + selectedFile.getName());
            attachReceiptBtn.setText("📎 Change Receipt");
        }
    }

    private void submit() {
        try {
            String cat = categoryField.getText().trim();
            String desc = descriptionField.getText().trim();
            double amt = Double.parseDouble(amountField.getText().trim());
            
            if(cat.isEmpty() || amt <= 0) throw new IllegalArgumentException();
            
            if(expense == null) expense = new Expense(cat, amt, desc, LocalDate.now(), username);
            else { expense.setCategory(cat); expense.setAmount(amt); expense.setDescription(desc); }
            
            expense.setSplit(isSplitMode);
            if(isSplitMode) expense.setSplitDetails(splitDetails);
            
            // Handle receipt attachment
            if (receiptFilePath != null) {
                saveReceiptForExpense(expense);
            }
            
            succeeded = true;
            dispose();
        } catch (Exception e) {
            UITheme.showMessage(this, "Please check your inputs", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveReceiptForExpense(Expense expense) {
        try {
            // Create receipts directory
            java.io.File receiptsDir = new java.io.File("data/receipts");
            if (!receiptsDir.exists()) {
                receiptsDir.mkdirs();
            }
            
            // Generate unique ID for expense receipt
            String expenseId = String.valueOf(System.currentTimeMillis());
            java.io.File sourceFile = new java.io.File(receiptFilePath);
            String extension = receiptFilePath.substring(receiptFilePath.lastIndexOf('.'));
            
            // Copy file to receipts directory
            java.io.File destFile = new java.io.File(receiptsDir, expenseId + extension);
            java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // Create receipt object
            com.paypilot.model.Receipt receipt = new com.paypilot.model.Receipt(
                destFile.getAbsolutePath(),
                sourceFile.getName(),
                sourceFile.length(),
                "image/" + extension.substring(1)
            );
            
            expense.setReceipt(receipt);
            
        } catch (Exception e) {
            System.err.println("Error saving receipt: " + e.getMessage());
        }
    }
    
    public boolean isSucceeded() { return succeeded; }
    public Expense getExpense() { return expense; }
    public boolean isRecurring() { return isRecurring; }
    
    public com.paypilot.model.RecurringExpense getRecurringExpense() {
        if (!isRecurring) return null;
        
        String cat = categoryField.getText().trim();
        String desc = descriptionField.getText().trim();
        double amt = Double.parseDouble(amountField.getText().trim());
        
        String selectedType = (String) recurrenceTypeBox.getSelectedItem();
        com.paypilot.model.RecurringExpense.RecurrenceType type;
        int customDays = 7;
        
        switch (selectedType) {
            case "Weekly":
                type = com.paypilot.model.RecurringExpense.RecurrenceType.WEEKLY;
                break;
            case "Monthly":
                type = com.paypilot.model.RecurringExpense.RecurrenceType.MONTHLY;
                break;
            default:
                type = com.paypilot.model.RecurringExpense.RecurrenceType.CUSTOM;
                try {
                    customDays = Integer.parseInt(customDaysField.getText().trim());
                } catch (Exception e) {
                    customDays = 7;
                }
        }
        
        return new com.paypilot.model.RecurringExpense(
            username, cat, amt, desc, type, customDays, LocalDate.now()
        );
    }
}