package com.paypilot.controller.strategy;

import com.paypilot.model.SplitDetail;
import java.util.ArrayList;
import java.util.List;

/**
 * Weighted Split Strategy - Divides based on custom weights
 * LSP: Properly substitutes parent interface
 */
public class WeightedSplitStrategy implements SplitStrategy {
    
    @Override
    public ArrayList<SplitDetail> computeShares(double totalAmount, 
                                               List<String> participants, 
                                               List<Double> weights) {
        ArrayList<SplitDetail> splits = new ArrayList<>();
        
        if (participants == null || participants.isEmpty() || 
            weights == null || weights.size() != participants.size()) {
            throw new IllegalArgumentException("Participants and weights must match in size");
        }
        
        // Calculate total weight
        double totalWeight = weights.stream()
                                   .mapToDouble(Double::doubleValue)
                                   .sum();
        
        if (totalWeight == 0) {
            throw new IllegalArgumentException("Total weight cannot be zero");
        }
        
        // Calculate proportional shares
        for (int i = 0; i < participants.size(); i++) {
            String participant = participants.get(i);
            double weight = weights.get(i);
            double share = (weight / totalWeight) * totalAmount;
            splits.add(new SplitDetail(participant, share));
        }
        
        return splits;
    }
    
    @Override
    public String getStrategyType() {
        return "WEIGHTED";
    }
}