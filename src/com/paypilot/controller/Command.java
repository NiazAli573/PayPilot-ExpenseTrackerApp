package com.paypilot.controller;

/**
 * COMMAND PATTERN - Command Interface
 * Problem Solved: Need undo/redo capability and audit-friendly changes
 * Benefit: Encapsulates operations, supports history, undo, and future scripting
 * 
 * Abstraction: Defines contract for executable commands
 */
public interface Command {
    /**
     * Executes the command
     */
    void execute();
    
    /**
     * Undoes the command
     */
    void undo();
    
    /**
     * Returns description of the command for logging
     */
    String getDescription();
}