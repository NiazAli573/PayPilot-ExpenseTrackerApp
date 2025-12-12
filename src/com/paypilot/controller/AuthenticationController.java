// CREATE THIS FILE: com/paypilot/controller/AuthenticationController.java
package com.paypilot.controller;

import com.paypilot.dao. UserDAO;
import com.paypilot.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AuthenticationController - Handles user authentication
 * SRP: Single responsibility - user authentication only
 * Facade: Simplifies authentication operations for UI
 */
public class AuthenticationController {
    private UserDAO userDAO;
    
    public AuthenticationController() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Register a new user
     */
    public boolean registerUser(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        String passwordHash = hashPassword(password);
        User user = new User(username, passwordHash, email);
        return userDAO.createUser(user);
    }
    
    /**
     * Authenticate user with username and password
     */
    public boolean authenticateUser(String username, String password) {
        User user = userDAO.getUser(username);
        if (user == null) {
            return false;
        }
        return user.getPasswordHash().equals(hashPassword(password));
    }
    
    /**
     * Get user information
     */
    public User getUser(String username) {
        return userDAO.getUser(username);
    }
    
    /**
     * Check if username exists
     */
    public boolean userExists(String username) {
        return userDAO.userExists(username);
    }
    
    /**
     * Hash password using SHA-256
     * Encapsulation: Hides hashing implementation
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (! authenticateUser(username, oldPassword)) {
            return false;
        }
        
        User user = userDAO.getUser(username);
        if (user == null) {
            return false;
        }
        
        user.setPasswordHash(hashPassword(newPassword));
        userDAO.updateUser(user);
        return true;
    }
}