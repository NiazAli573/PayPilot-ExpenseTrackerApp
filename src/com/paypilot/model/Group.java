package com.paypilot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Group Model - Represents an expense sharing group
 * Allows multiple users to share expenses and track balances
 */
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String groupName;
    private String createdBy;
    private List<String> members;

    // This is the constructor the Controller needs
    public Group(String groupName, String createdBy) {
        this.groupName = groupName;
        this.createdBy = createdBy;
        this.members = new ArrayList<>();
        this.members.add(createdBy); // Creator is automatically a member
    }

    // Getters and Setters
    public String getGroupName() { return groupName; }
    public String getCreatedBy() { return createdBy; }
    public List<String> getMembers() { return members; }
    
    public void addMember(String username) {
        if (!members.contains(username)) {
            members.add(username);
        }
    }
    
    public void removeMember(String username) {
        members.remove(username);
    }
}