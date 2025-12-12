package com.paypilot.db;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern: Ensures single instance for centralized database management
 * Problem Solved: Prevents multiple conflicting connection initializations
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DATA_DIR = "data/";
    private static final Object lock = new Object();
    
    // In-memory storage (simulating database)
    private Map<String, Object> storage;
    
    private DatabaseManager() {
        storage = new HashMap<>();
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
    
    public synchronized void saveData(String key, Object data) {
        storage.put(key, data);
        persistToDisk(key, data);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized <T> T loadData(String key, Class<T> type) {
        if (storage.containsKey(key)) {
            return (T) storage.get(key);
        }
        return loadFromDisk(key, type);
    }
    
    private void persistToDisk(String key, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_DIR + key + ".dat"))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error persisting data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> T loadFromDisk(String key, Class<T> type) {
        File file = new File(DATA_DIR + key + ".dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                Object data = ois.readObject();
                storage.put(key, data);
                return (T) data;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data: " + e.getMessage());
            }
        }
        return null;
    }
    
    public boolean dataExists(String key) {
        return storage.containsKey(key) || 
               new File(DATA_DIR + key + ".dat").exists();
    }
    
    public synchronized void deleteData(String key) {
        storage.remove(key);
        File file = new File(DATA_DIR + key + ".dat");
        if (file.exists()) {
            file.delete();
        }
    }
}