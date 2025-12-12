package com.paypilot.dao;

import com.paypilot.model.Group;
import com.paypilot.model.SharedExpense;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for Group operations
 */
public class GroupDAO {
    private static final String GROUPS_KEY = "groups";
    private static final String SHARED_EXPENSES_KEY = "shared_expenses";
    private DatabaseManager dbManager;
    
    public GroupDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Group> loadGroups() {
        Map<String, Group> groups = dbManager.loadData(GROUPS_KEY, Map.class);
        return groups != null ?  groups : new HashMap<>();
    }
    
    private void saveGroups(Map<String, Group> groups) {
        dbManager.saveData(GROUPS_KEY, groups);
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<SharedExpense> loadSharedExpenses() {
        ArrayList<SharedExpense> expenses = dbManager.loadData(SHARED_EXPENSES_KEY, ArrayList.class);
        return expenses != null ? expenses : new ArrayList<>();
    }
    
    private void saveSharedExpenses(ArrayList<SharedExpense> expenses) {
        dbManager.saveData(SHARED_EXPENSES_KEY, expenses);
    }
    
    public boolean createGroup(Group group) {
        Map<String, Group> groups = loadGroups();
        if (groups.containsKey(group.getGroupName())) {
            return false;
        }
        groups.put(group.getGroupName(), group);
        saveGroups(groups);
        return true;
    }
    
    public Group getGroup(String groupName) {
        return loadGroups().get(groupName);
    }
    
    public ArrayList<Group> getUserGroups(String username) {
        Map<String, Group> groups = loadGroups();
        ArrayList<Group> userGroups = new ArrayList<>();
        for (Group group : groups.values()) {
            if (group.getMembers().contains(username)) {
                userGroups.add(group);
            }
        }
        return userGroups;
    }
    
    public void updateGroup(Group group) {
        Map<String, Group> groups = loadGroups();
        groups.put(group.getGroupName(), group);
        saveGroups(groups);
    }
    
    public void addSharedExpense(SharedExpense expense) {
        ArrayList<SharedExpense> expenses = loadSharedExpenses();
        expenses.add(expense);
        saveSharedExpenses(expenses);
    }
    
    public ArrayList<SharedExpense> getGroupExpenses(String groupName) {
        ArrayList<SharedExpense> allExpenses = loadSharedExpenses();
        ArrayList<SharedExpense> groupExpenses = new ArrayList<>();
        for (SharedExpense expense : allExpenses) {
            if (expense.getGroupName().equals(groupName)) {
                groupExpenses.add(expense);
            }
        }
        return groupExpenses;
    }
    
    public void deleteGroup(String groupName) {
        Map<String, Group> groups = loadGroups();
        groups.remove(groupName);
        saveGroups(groups);
    }
}