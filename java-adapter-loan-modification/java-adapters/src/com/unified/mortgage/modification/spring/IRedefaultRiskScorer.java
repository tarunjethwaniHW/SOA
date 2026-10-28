package com.unified.mortgage.modification.spring;

import java.util.Map;

/**
 * Risk Scorer Interface - Exposed via Spring SCA
 * Referenced from: composite.xml → spring/redefault-risk-scorer-context.xml
 *
 * Call Chain:
 *   SOA_COMPONENT (RedefaultRiskScorerComponent)
 *       → COMPONENT_TO_SPRING → redefault-risk-scorer-context.xml
 *       → SPRING_BEAN (redefaultRiskScorerBean)
 *       → SPRING_BEAN_TO_CLASS → RedefaultRiskScorerImpl.java
 */
public interface IRedefaultRiskScorer {

    /**
     * Calculate risk score for a modification application.
     * @param applicationData Map containing modification application data
     *        - hardshipCreditScore: Integer
     *        - postModDti: Double (debt-to-income ratio)
     *        - markToMarketLtv: Double (modification-to-value ratio)
     *        - unpaidPrincipalBalance: Double
     * @return Risk score between 300-850
     */
    int calculateRedefaultRiskScore(Map<String, Object> applicationData);

    /**
     * Get risk category based on score.
     * @param score Risk score (300-850)
     * @return Category: "EXCELLENT", "GOOD", "FAIR", or "POOR"
     */
    String getRedefaultRiskCategory(int score);

    /**
     * Check if score meets minimum threshold.
     * @param score Risk score
     * @param threshold Minimum required score
     * @return true if score >= threshold
     */
    boolean meetsRedefaultThreshold(int score, int threshold);
}
