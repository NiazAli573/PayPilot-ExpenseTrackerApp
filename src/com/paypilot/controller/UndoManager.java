package com.paypilot.controller;

import java.util.Stack;

/**
 * Manages command history for undo/redo operations
 * Maintains two stacks for executed and undone commands
 * 
 * Behavioral Pattern: Manages command execution flow
 */
public class UndoManager {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private int maxHistorySize;
    
    public UndoManager() {
        this(50); // Default history size
    }
    
    public UndoManager(int maxHistorySize) {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.maxHistorySize = maxHistorySize;
    }
    
    /**
     * Executes a command and adds it to history
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack when new command is executed
        
        // Maintain max history size
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0);
        }
    }
    
    /**
     * Undoes the last command
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }
    
    /**
     * Redoes the last undone command
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return true;
    }
    
    /**
     * Checks if undo is available
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Checks if redo is available
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Gets description of last command that can be undone
     */
    public String getUndoDescription() {
        if (undoStack.isEmpty()) {
            return null;
        }
        return undoStack.peek().getDescription();
    }
    
    /**
     * Gets description of last command that can be redone
     */
    public String getRedoDescription() {
        if (redoStack.isEmpty()) {
            return null;
        }
        return redoStack.peek().getDescription();
    }
    
    /**
     * Clears all command history
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
    
    /**
     * Get current history size
     */
    public int getHistorySize() {
        return undoStack.size();
    }
}