package com.paypilot.view;

import com.paypilot.controller.AuthenticationController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticationController authController;

    public LoginView(AuthenticationController authController) {
        this.authController = authController;
        setTitle("PayPilot Login");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        UITheme.applyLookAndFeel();
        initializeUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Use the new RoundedPanel from the latest UITheme
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(380, 450));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0); 
        gbc.gridx = 0;
        
        // Header
        gbc.gridy = 0;
        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, gbc);
        
        gbc.gridy = 1;
        JLabel sub = new JLabel("Enter your details to sign in");
        sub.setFont(UITheme.FONT_REGULAR);
        // Updated to use TEXT_MUTED instead of TEXT_SECONDARY
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(sub, gbc);

        // Inputs
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 8, 0);
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.TEXT_DARK);
        card.add(lblUser, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        usernameField = UITheme.createStyledTextField();
        usernameField.setPreferredSize(new Dimension(300, 45));
        card.add(usernameField, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);
        card.add(lblPass, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 30, 0);
        
        // Manual styling for PasswordField since helper was removed
        passwordField = new JPasswordField();
        passwordField.setFont(UITheme.FONT_REGULAR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setPreferredSize(new Dimension(300, 45));
        card.add(passwordField, gbc);
        
        // Buttons
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.setPreferredSize(new Dimension(300, 45));
        loginBtn.addActionListener(e -> attemptLogin());
        card.add(loginBtn, gbc);
        
        gbc.gridy = 7;
        JButton signupBtn = new JButton("Don't have an account? Sign Up");
        signupBtn.setFont(UITheme.FONT_BOLD);
        signupBtn.setForeground(UITheme.PRIMARY_COLOR);
        signupBtn.setBorderPainted(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupBtn.addActionListener(e -> openSignup());
        card.add(signupBtn, gbc);

        mainPanel.add(card);
        setContentPane(mainPanel);
    }
    
    private void attemptLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        if(u.isEmpty() || p.isEmpty()) {
            UITheme.showMessage(this, "Fields cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (authController.authenticateUser(u, p)) {
            dispose();
            // Launch the Modern Dashboard
            SwingUtilities.invokeLater(() -> new ModernMainDashboard(u).setVisible(true));
        } else {
            UITheme.showMessage(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openSignup() {
        SignupView s = new SignupView(this, authController);
        s.setVisible(true);
        if(s.isSucceeded()) {
            usernameField.setText(s.getUsername());
            passwordField.requestFocus();
        }
    }
}