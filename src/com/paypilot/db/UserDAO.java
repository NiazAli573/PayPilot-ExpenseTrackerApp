package com.paypilot.db;

import com.paypilot.model.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    private static final String USERS_KEY = "users";
    private DatabaseManager dbManager;
    
    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, User> loadUsers() {
        Map<String, User> users = dbManager.loadData(USERS_KEY, Map.class);
        return users != null ? users : new HashMap<>();
    }
    
    private void saveUsers(Map<String, User> users) {
        dbManager.saveData(USERS_KEY, users);
    }
    
    public boolean createUser(User user) {
        Map<String, User> users = loadUsers();
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        saveUsers(users);
        return true;
    }
    
    public User getUser(String username) {
        Map<String, User> users = loadUsers();
        return users.get(username);
    }
    
    public boolean userExists(String username) {
        return loadUsers().containsKey(username);
    }
    
    public void updateUser(User user) {
        Map<String, User> users = loadUsers();
        users.put(user.getUsername(), user);
        saveUsers(users);
    }
    
    public void deleteUser(String username) {
        Map<String, User> users = loadUsers();
        users.remove(username);
        saveUsers(users);
    }
}