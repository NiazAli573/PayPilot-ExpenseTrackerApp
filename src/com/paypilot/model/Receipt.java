package com.paypilot.model;

import java.io.Serializable;

/**
 * Receipt - Represents a receipt attachment for an expense
 * Stores file path and metadata
 */
public class Receipt implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    private String originalFileName;
    private long fileSize;
    private String mimeType;
    
    public Receipt(String filePath, String originalFileName, long fileSize, String mimeType) {
        this.filePath = filePath;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
    }
    
    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getOriginalFileName() {
        return originalFileName;
    }
    
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    /**
     * Get human-readable file size
     */
    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }
}
