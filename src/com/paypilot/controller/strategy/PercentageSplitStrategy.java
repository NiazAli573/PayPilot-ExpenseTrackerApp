package com.paypilot.controller.strategy;

import com.paypilot.model.SplitDetail;
import java.util.ArrayList;
import java.util.List;

/**
 * Percentage Split Strategy - Divides based on percentages
 * LSP: Properly substitutes parent interface
 */
public class PercentageSplitStrategy implements SplitStrategy {
    
    @Override
    public ArrayList<SplitDetail> computeShares(double totalAmount, 
                                               List<String> participants, 
                                               List<Double> percentages) {
        ArrayList<SplitDetail> splits = new ArrayList<>();
        
        if (participants == null || participants.isEmpty() || 
            percentages == null || percentages.size() != participants.size()) {
            throw new IllegalArgumentException("Participants and percentages must match in size");
        }
        
        // Validate percentages sum to 100 (with tolerance)
        double totalPercentage = percentages.stream()
                                           .mapToDouble(Double::doubleValue)
                                           .sum();
        
        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new IllegalArgumentException("Percentages must sum to 100%.  Current sum: " + totalPercentage);
        }
        
        // Calculate shares based on percentage
        for (int i = 0; i < participants.size(); i++) {
            String participant = participants.get(i);
            double percentage = percentages.get(i);
            double share = (percentage / 100.0) * totalAmount;
            splits.add(new SplitDetail(participant, share));
        }
        
        return splits;
    }
    
    @Override
    public String getStrategyType() {
        return "PERCENTAGE";
    }
}