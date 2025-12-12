package com.paypilot.controller.strategy;

import com.paypilot.model.SplitDetail;
import java.util.ArrayList;
import java.util.List;

/**
 * STRATEGY PATTERN - SplitStrategy Interface
 * Problem Solved: Multiple algorithms for splitting expenses
 * Benefit: Runtime selection, easy extension, OCP compliance
 * 
 * Abstraction: Defines contract for split algorithms
 * Polymorphism: Different implementations can be used interchangeably
 */
public interface SplitStrategy {
    /**
     * Computes individual shares from total amount
     * @param totalAmount The total expense amount
     * @param participants List of participant names
     * @param weights Optional weights/percentages (can be null for equal split)
     * @return List of SplitDetail objects with computed shares
     */
    ArrayList<SplitDetail> computeShares(double totalAmount, 
                                        List<String> participants, 
                                        List<Double> weights);
    
    /**
     * Returns the strategy type identifier
     */
    String getStrategyType();
}