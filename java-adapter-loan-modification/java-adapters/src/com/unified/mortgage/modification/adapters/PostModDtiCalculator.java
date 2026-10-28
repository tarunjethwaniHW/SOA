package com.unified.mortgage.modification.adapters;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTI Calculator - Java Adapter for BPEL Java Embedding
 * Calculates Debt-to-Income (DTI) and Modification-to-Value (LTV) ratios.
 *
 * This class is instantiated from BPEL via:
 * <bpelx:exec name="CalculatePostModDti" language="java">
 *     com.unified.mortgage.modification.adapters.PostModDtiCalculator calculator = new com.unified.mortgage.modification.adapters.PostModDtiCalculator();
 *     double postModDti = calculator.calculatePostModDti(income, debt);
 * </bpelx:exec>
 *
 * JAR Location: SCA-INF/lib/postModDti-calculator.jar
 */
public class PostModDtiCalculator {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    /**
     * Calculates the Debt-to-Income ratio.
     * DTI = (Total Monthly Debt / Gross Monthly Income) * 100
     */
    public double calculatePostModDti(double hardshipMonthlyIncome, double monthlyObligations) {
        if (hardshipMonthlyIncome <= 0) return 0.0;
        BigDecimal income = BigDecimal.valueOf(hardshipMonthlyIncome);
        BigDecimal debt = BigDecimal.valueOf(monthlyObligations);
        return debt.divide(income, SCALE, ROUNDING)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, ROUNDING)
                   .doubleValue();
    }

    /**
     * Calculates the Modification-to-Value ratio.
     * LTV = (Modification Amount / Property Value) * 100
     */
    public double calculateMarkToMarketLtv(double unpaidPrincipalBalance, double propertyValue) {
        if (propertyValue <= 0) return 0.0;
        BigDecimal modification = BigDecimal.valueOf(unpaidPrincipalBalance);
        BigDecimal value = BigDecimal.valueOf(propertyValue);
        return modification.divide(value, SCALE, ROUNDING)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, ROUNDING)
                   .doubleValue();
    }

    /**
     * Calculates the Front-End DTI (Housing Ratio).
     */
    public double calculatePostModFrontEndDti(double hardshipMonthlyIncome, double proposedPITI) {
        if (hardshipMonthlyIncome <= 0) return 0.0;
        BigDecimal income = BigDecimal.valueOf(hardshipMonthlyIncome);
        BigDecimal piti = BigDecimal.valueOf(proposedPITI);
        return piti.divide(income, SCALE, ROUNDING)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, ROUNDING)
                   .doubleValue();
    }

    /**
     * Calculates the Back-End DTI (Total Debt Ratio).
     */
    public double calculatePostModBackEndDti(double hardshipMonthlyIncome, double proposedPITI, double otherMonthlyDebt) {
        if (hardshipMonthlyIncome <= 0) return 0.0;
        BigDecimal income = BigDecimal.valueOf(hardshipMonthlyIncome);
        BigDecimal totalDebt = BigDecimal.valueOf(proposedPITI + otherMonthlyDebt);
        return totalDebt.divide(income, SCALE, ROUNDING)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, ROUNDING)
                        .doubleValue();
    }

    /**
     * Evaluates if the modification meets QM (Qualified Mortgage) DTI requirements.
     */
    public boolean isAffordabilityCompliant(double postModDtiRatio) {
        return postModDtiRatio <= 43.0;
    }
}
