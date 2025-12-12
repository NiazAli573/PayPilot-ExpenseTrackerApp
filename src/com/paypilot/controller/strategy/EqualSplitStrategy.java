package com.paypilot.controller.strategy;

import com.paypilot.model.SplitDetail;
import java.util.ArrayList;
import java.util.List;

/**
 * Equal Split Strategy Implementation
 * LSP: Can substitute SplitStrategy without breaking behavior
 * OCP: Extends functionality without modifying interface
 */
public class EqualSplitStrategy implements SplitStrategy {
    
    @Override
    public ArrayList<SplitDetail> computeShares(double totalAmount, 
                                               List<String> participants, 
                                               List<Double> weights) {
        ArrayList<SplitDetail> splits = new ArrayList<>();
        
        if (participants == null || participants.isEmpty()) {
            return splits;
        }
        
        double sharePerPerson = totalAmount / participants.size();
        
        for (String participant : participants) {
            splits.add(new SplitDetail(participant, sharePerPerson));
        }
        
        return splits;
    }
    
    @Override
    public String getStrategyType() {
        return "EQUAL";
    }
}