package com.unified.mortgage.modification.spring;

import java.util.Map;

/**
 * Risk Scorer Implementation - Spring Bean
 * Bean ID: redefaultRiskScorerBean
 * Exposed as SCA service: RedefaultRiskScorerService
 *
 * This class is instantiated by Spring and exposed to SOA Suite via:
 *   <implementation.spring src="spring/redefault-risk-scorer-context.xml"/>
 */
public class RedefaultRiskScorerImpl implements IRedefaultRiskScorer {

    private int baseRedefaultScore = 500;
    private int maxRedefaultScore = 850;

    @Override
    public int calculateRedefaultRiskScore(Map<String, Object> applicationData) {
        int score = baseRedefaultScore;

        // Credit score factor (major impact)
        Integer hardshipCreditScore = (Integer) applicationData.get("hardshipCreditScore");
        if (hardshipCreditScore != null) {
            if (hardshipCreditScore >= 760) score += 100;
            else if (hardshipCreditScore >= 720) score += 75;
            else if (hardshipCreditScore >= 680) score += 50;
            else if (hardshipCreditScore >= 640) score += 25;
            else score -= 50;
        }

        // DTI factor (debt-to-income ratio)
        Double postModDti = (Double) applicationData.get("postModDti");
        if (postModDti != null) {
            if (postModDti < 28.0) score += 75;
            else if (postModDti < 36.0) score += 50;
            else if (postModDti < 43.0) score += 25;
            else score -= 25;
        }

        // LTV factor (modification-to-value ratio)
        Double markToMarketLtv = (Double) applicationData.get("markToMarketLtv");
        if (markToMarketLtv != null) {
            if (markToMarketLtv < 80.0) score += 50;
            else if (markToMarketLtv < 90.0) score += 25;
            else if (markToMarketLtv > 95.0) score -= 25;
        }

        // Modification amount factor
        Double unpaidPrincipalBalance = (Double) applicationData.get("unpaidPrincipalBalance");
        if (unpaidPrincipalBalance != null && unpaidPrincipalBalance > 500000) {
            score -= 15; // Higher risk for jumbo modifications
        }

        return Math.max(300, Math.min(score, maxRedefaultScore));
    }

    @Override
    public String getRedefaultRiskCategory(int score) {
        if (score >= 750) return "EXCELLENT";
        if (score >= 700) return "GOOD";
        if (score >= 650) return "FAIR";
        return "POOR";
    }

    @Override
    public boolean meetsRedefaultThreshold(int score, int threshold) {
        return score >= threshold;
    }

    // Setters for Spring property injection
    public void setBaseRedefaultScore(int baseRedefaultScore) {
        this.baseRedefaultScore = baseRedefaultScore;
    }

    public void setMaxRedefaultScore(int maxRedefaultScore) {
        this.maxRedefaultScore = maxRedefaultScore;
    }
}
