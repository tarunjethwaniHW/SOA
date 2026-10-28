package com.unified.mortgage.modification.adapters;

import java.util.Map;

/**
 * Decision Engine - Java Adapter for BPEL Java Embedding
 * Automated Underwriting System (AUS) modificationDecision engine for modification evaluation.
 *
 * This class is instantiated from BPEL via:
 * <bpelx:exec name="RunModificationDecisionEngine" language="java">
 *     com.unified.mortgage.modification.adapters.ModificationDecisionEngine engine = new com.unified.mortgage.modification.adapters.ModificationDecisionEngine();
 *     ModificationDecision modificationDecision = engine.evaluateModification(modificationData);
 * </bpelx:exec>
 *
 * JAR Location: SCA-INF/lib/modificationDecision-engine.jar
 */
public class ModificationDecisionEngine {

    public static final String APPROVE = "APPROVE";
    public static final String APPROVE_WITH_CONDITIONS = "APPROVE_WITH_CONDITIONS";
    public static final String REFER = "REFER";
    public static final String DECLINE = "DECLINE";

    private static final int MIN_CREDIT_SCORE = 620;
    private static final double MAX_DTI = 43.0;
    private static final double MAX_LTV = 97.0;

    /**
     * Evaluates a modification application and returns a modificationDecision.
     */
    public ModificationDecision evaluateModification(Map<String, Object> modificationData) {
        double unpaidPrincipalBalance = getDouble(modificationData, "unpaidPrincipalBalance", 0);
        double propertyValue = getDouble(modificationData, "propertyValue", 0);
        double hardshipMonthlyIncome = getDouble(modificationData, "hardshipMonthlyIncome", 0);
        double monthlyObligations = getDouble(modificationData, "monthlyObligations", 0);
        int hardshipCreditScore = getInt(modificationData, "hardshipCreditScore", 0);

        double markToMarketLtv = (propertyValue > 0) ? (unpaidPrincipalBalance / propertyValue) * 100 : 0;
        double postModDti = (hardshipMonthlyIncome > 0) ? (monthlyObligations / hardshipMonthlyIncome) * 100 : 0;

        int redefaultRiskScore = calculateRedefaultRiskScore(hardshipCreditScore, postModDti, markToMarketLtv);
        String modificationDecision = makeModificationDecision(hardshipCreditScore, postModDti, markToMarketLtv, redefaultRiskScore);

        return new ModificationDecision(modificationDecision, redefaultRiskScore, markToMarketLtv, postModDti);
    }

    private int calculateRedefaultRiskScore(int hardshipCreditScore, double postModDti, double markToMarketLtv) {
        double creditFactor = Math.min(100, Math.max(0, (hardshipCreditScore - 500) / 3.5));
        double dtiFactor = Math.min(100, Math.max(0, (60 - postModDti) * 2.5));
        double ltvFactor = Math.min(100, Math.max(0, (100 - markToMarketLtv) * 1.25));
        return (int) ((creditFactor * 0.4) + (dtiFactor * 0.35) + (ltvFactor * 0.25));
    }

    private String makeModificationDecision(int hardshipCreditScore, double postModDti, double markToMarketLtv, int redefaultRiskScore) {
        if (hardshipCreditScore < MIN_CREDIT_SCORE) return DECLINE;
        if (postModDti > MAX_DTI) return DECLINE;
        if (markToMarketLtv > MAX_LTV) return DECLINE;
        if (redefaultRiskScore >= 75) return APPROVE;
        if (redefaultRiskScore >= 60) return APPROVE_WITH_CONDITIONS;
        if (redefaultRiskScore >= 40) return REFER;
        return DECLINE;
    }

    private double getDouble(Map<String, Object> map, String key, double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        return defaultValue;
    }

    private int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        return defaultValue;
    }

    /**
     * Modification decision result containing the modification decision, re-default risk score, and ratios.
     */
    public static class ModificationDecision {
        private final String modificationDecision;
        private final int redefaultRiskScore;
        private final double markToMarketLtv;
        private final double postModDti;

        public ModificationDecision(String modificationDecision, int redefaultRiskScore, double markToMarketLtv, double postModDti) {
            this.modificationDecision = modificationDecision;
            this.redefaultRiskScore = redefaultRiskScore;
            this.markToMarketLtv = markToMarketLtv;
            this.postModDti = postModDti;
        }

        public String getModificationDecision() { return modificationDecision; }
        public int getRedefaultRiskScore() { return redefaultRiskScore; }
        public double getMarkToMarketLtv() { return markToMarketLtv; }
        public double getPostModDti() { return postModDti; }
    }
}
