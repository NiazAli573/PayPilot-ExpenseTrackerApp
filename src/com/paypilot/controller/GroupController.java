package com.paypilot.controller;

import com.paypilot.controller.strategy.*;
import com.paypilot.dao.GroupDAO;
import com.paypilot.model.Group;
import com.paypilot.model.SharedExpense;
import com.paypilot.model.SplitDetail;
import java.time.LocalDate; // Ensure this import is here!
import java.util.*;

public class GroupController {
    private GroupDAO groupDAO;
    private Map<String, SplitStrategy> strategies;
    
    public GroupController() {
        this.groupDAO = new GroupDAO();
        initializeStrategies();
    }
    
    private void initializeStrategies() {
        strategies = new HashMap<>();
        strategies.put("EQUAL", new EqualSplitStrategy());
        strategies.put("WEIGHTED", new WeightedSplitStrategy());
        strategies.put("PERCENTAGE", new PercentageSplitStrategy());
    }
    
    public boolean createGroup(String groupName, String createdBy) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty");
        }
        Group group = new Group(groupName, createdBy);
        return groupDAO.createGroup(group);
    }
    
    public boolean addMemberToGroup(String groupName, String username) {
        Group group = groupDAO.getGroup(groupName);
        if (group == null) return false;
        group.addMember(username);
        groupDAO.updateGroup(group);
        return true;
    }
    
    public boolean removeMemberFromGroup(String groupName, String username) {
        Group group = groupDAO.getGroup(groupName);
        if (group == null) return false;
        group.removeMember(username);
        groupDAO.updateGroup(group);
        return true;
    }
    
    public ArrayList<Group> getUserGroups(String username) {
        return groupDAO.getUserGroups(username);
    }
    
    public Group getGroup(String groupName) {
        return groupDAO.getGroup(groupName);
    }
    
    public void addSharedExpense(String category, double totalAmount, String description,
                                String paidBy, String groupName, String strategyType,
                                List<String> participants, List<Double> weights) {
        
        Group group = groupDAO.getGroup(groupName);
        if (group == null) {
            throw new IllegalArgumentException("Group not found: " + groupName);
        }
        
        SplitStrategy strategy = strategies.get(strategyType);
        if (strategy == null) strategy = strategies.get("EQUAL");
        
        ArrayList<SplitDetail> splitDetails = strategy.computeShares(totalAmount, participants, weights);
        
        // FIX: Added LocalDate.now() here to fix the red line error
        SharedExpense expense = new SharedExpense(category, totalAmount, description,
                                                 LocalDate.now(), paidBy, groupName);
        expense.setParticipantUserIds(participants);
        expense.setSplitStrategyType(strategyType);
        
        groupDAO.addSharedExpense(expense);
    }
    
    public ArrayList<SharedExpense> getGroupExpenses(String groupName) {
        return groupDAO.getGroupExpenses(groupName);
    }
    
    public Map<String, Double> calculateGroupBalances(String groupName) {
        ArrayList<SharedExpense> expenses = groupDAO.getGroupExpenses(groupName);
        Map<String, Double> balances = new HashMap<>();
        
        Group group = groupDAO.getGroup(groupName);
        if (group == null) return balances;
        
        for (String member : group.getMembers()) {
            balances.put(member, 0.0);
        }
        
        for (SharedExpense expense : expenses) {
            String paidBy = expense.getPaidByUsername();
            double totalAmount = expense.getTotalAmount();
            List<String> participants = expense.getParticipantUserIds();
            
            SplitStrategy strategy = strategies.get(expense.getSplitStrategyType());
            if (strategy == null) strategy = strategies.get("EQUAL");
            
            ArrayList<SplitDetail> shares = strategy.computeShares(totalAmount, participants, null);
            
            balances.put(paidBy, balances.getOrDefault(paidBy, 0.0) + totalAmount);
            
            for (SplitDetail share : shares) {
                String person = share.getPersonName();
                double amount = share.getAmount();
                balances.put(person, balances.getOrDefault(person, 0.0) - amount);
            }
        }
        return balances;
    }
    
    public List<String> getAvailableStrategies() {
        return new ArrayList<>(strategies.keySet());
    }
    
    public void deleteGroup(String groupName) {
        groupDAO.deleteGroup(groupName);
    }
}