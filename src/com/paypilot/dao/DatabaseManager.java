package com.paypilot.dao;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SINGLETON PATTERN - DatabaseManager
 * Problem Solved: Centralized DB access & single connection management
 * Benefit: Prevents multiple conflicting connection initializations
 * 
 * SRP: Single responsibility - database operations only
 * Thread-safe singleton implementation
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DATA_DIR = "data/";
    private static final Object lock = new Object();
    
    // In-memory cache for better performance
    private Map<String, Object> cache;
    
    private DatabaseManager() {
        cache = new HashMap<>();
        ensureDataDirectory();
    }
    
    /**
     * Thread-safe singleton instance getter (Double-checked locking)
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }
    
    private void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Save data to storage
     */
    public synchronized void saveData(String key, Object data) {
        cache.put(key, data);
        persistToDisk(key, data);
    }
    
    /**
     * Load data from storage
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T loadData(String key, Class<T> type) {
        // Check cache first
        if (cache.containsKey(key)) {
            return (T) cache.get(key);
        }
        // Load from disk
        return loadFromDisk(key, type);
    }
    
    /**
     * Persist data to disk
     */
    private void persistToDisk(String key, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_DIR + key + ".dat"))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error persisting data for key " + key + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load data from disk
     */
    @SuppressWarnings("unchecked")
    private <T> T loadFromDisk(String key, Class<T> type) {
        File file = new File(DATA_DIR + key + ".dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                Object data = ois.readObject();
                cache.put(key, data);
                return (T) data;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data for key " + key + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Check if data exists
     */
    public boolean dataExists(String key) {
        return cache.containsKey(key) || 
               new File(DATA_DIR + key + ".dat").exists();
    }
    
    /**
     * Delete data
     */
    public synchronized void deleteData(String key) {
        cache.remove(key);
        File file = new File(DATA_DIR + key + ".dat");
        if (file.exists()) {
            file.delete();
        }
    }
    
    /**
     * Clear all cache
     */
    public synchronized void clearCache() {
        cache.clear();
    }
}