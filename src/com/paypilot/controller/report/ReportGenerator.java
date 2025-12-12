package com.paypilot.controller.report;

/**
 * FACTORY PATTERN - ReportGenerator Interface
 * Problem Solved: Flexible instantiation of various report formats
 * Benefit: Open for new report types without modifying client code (OCP)
 * 
 * Abstraction: Defines contract for report generation
 * ISP: Clients depend only on methods they use
 */
public interface ReportGenerator {
    /**
     * Generate report content as string
     * @return Formatted report string
     */
    String generate();
    
    /**
     * Save report to file
     * @param filepath Path where to save the report
     * @return true if successful, false otherwise
     */
    boolean saveToFile(String filepath);
    
    /**
     * Get report type identifier
     */
    String getReportType();
}