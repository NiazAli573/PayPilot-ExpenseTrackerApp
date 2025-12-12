package com.paypilot.view;

import com.paypilot.controller.AuthenticationController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SignupView extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean succeeded = false;
    private AuthenticationController authController;

    public SignupView(Dialog parent, AuthenticationController authController) {
        super(parent, "Sign Up - PayPilot", true);
        this.authController = authController;

        UITheme.applyLookAndFeel();
        initializeUI();
        
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeUI() {
        // Main Container matching the Theme Background
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(UITheme.BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainContainer);
        
        // Create the Card
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(400, 620));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0;

        // --- Header ---
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel title = new JLabel("Create Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        JLabel subtitle = new JLabel("Join PayPilot to track your expenses");
        subtitle.setFont(UITheme.FONT_REGULAR);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(subtitle, gbc);

        // --- Fields ---
        // Username
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createLabel("Username"), gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        usernameField = UITheme.createStyledTextField();
        usernameField.setPreferredSize(new Dimension(340, 40));
        card.add(usernameField, gbc);

        // Email
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createLabel("Email"), gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        emailField = UITheme.createStyledTextField();
        emailField.setPreferredSize(new Dimension(340, 40));
        card.add(emailField, gbc);

        // Password
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createLabel("Password"), gbc);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 15, 0);
        passwordField = new JPasswordField(); // Manual styling since helper removed
        stylePasswordField(passwordField);
        card.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createLabel("Confirm Password"), gbc);
        
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 25, 0);
        confirmPasswordField = new JPasswordField();
        stylePasswordField(confirmPasswordField);
        card.add(confirmPasswordField, gbc);

        // --- Buttons ---
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 10, 0);
        JButton signupBtn = UITheme.createPrimaryButton("Create Account");
        signupBtn.setPreferredSize(new Dimension(340, 45));
        signupBtn.addActionListener(e -> signup());
        card.add(signupBtn, gbc);

        gbc.gridy = 11;
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UITheme.FONT_BOLD);
        cancelBtn.setForeground(UITheme.TEXT_MUTED);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());
        card.add(cancelBtn, gbc);

        mainContainer.add(card);
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);
        return lbl;
    }
    
    private void stylePasswordField(JPasswordField field) {
        field.setFont(UITheme.FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(340, 40));
    }

    private void signup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UITheme.showMessage(this, "Please fill in all fields", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            UITheme.showMessage(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            UITheme.showMessage(this, "Password must be at least 6 characters", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (authController.registerUser(username, password, email)) {
                succeeded = true;
                dispose();
            } else {
                UITheme.showMessage(this, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            UITheme.showMessage(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSucceeded() { return succeeded; }
    public String getUsername() { return usernameField.getText().trim(); }
}