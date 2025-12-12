package com.paypilot;

import com.paypilot.controller.AuthenticationController;
import com.paypilot.view.LoginView;
import com.paypilot.view.UITheme;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the PayPilot application.
 */
public class Main {
    public static void main(String[] args) {
        // Ensure UI updates happen on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Apply the custom UI Theme
                UITheme.applyLookAndFeel();

                // 2. Initialize necessary controllers
                // (Ensure you have this class in com.paypilot.controller)
                AuthenticationController authController = new AuthenticationController();

                // 3. Launch the Login View
                LoginView loginView = new LoginView(authController);
                loginView.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to launch application: " + e.getMessage());
            }
        });
    }
}