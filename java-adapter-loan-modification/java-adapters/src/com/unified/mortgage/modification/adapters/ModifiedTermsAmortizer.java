package com.unified.mortgage.modification.adapters;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Amortization Calculator - Java Adapter for BPEL Java Embedding
 * Calculates modification pricing, interest rates, and monthly payments.
 *
 * This class is instantiated from BPEL via:
 * <bpelx:exec name="CalculateModifiedTerms" language="java">
 *     com.unified.mortgage.modification.adapters.ModifiedTermsAmortizer calculator = new com.unified.mortgage.modification.adapters.ModifiedTermsAmortizer();
 *     double rate = calculator.calculateModifiedRate(markToMarketLtv, hardshipCreditScore, termYears);
 *     double payment = calculator.calculateModifiedPayment(principal, rate, termMonths);
 * </bpelx:exec>
 *
 * JAR Location: SCA-INF/lib/amortization-calculator.jar
 */
public class ModifiedTermsAmortizer {

    private static final double BASE_RATE = 6.5; // Base interest rate
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    /**
     * Calculates interest rate based on LTV, credit score, and term.
     *
     * @param markToMarketLtv Modification-to-Value ratio as percentage
     * @param hardshipCreditScore Borrower's credit score
     * @param termYears Modification term in years
     * @return Annual interest rate as percentage
     */
    public double calculateModifiedRate(double markToMarketLtv, int hardshipCreditScore, int termYears) {
        double rate = BASE_RATE;

        // LTV adjustment
        if (markToMarketLtv > 95) rate += 0.75;
        else if (markToMarketLtv > 90) rate += 0.50;
        else if (markToMarketLtv > 80) rate += 0.25;

        // Credit score adjustment
        if (hardshipCreditScore >= 760) rate -= 0.25;
        else if (hardshipCreditScore >= 720) rate -= 0.125;
        else if (hardshipCreditScore < 680) rate += 0.25;
        else if (hardshipCreditScore < 640) rate += 0.50;

        // Term adjustment (15-year gets better rate)
        if (termYears <= 15) rate -= 0.25;

        return BigDecimal.valueOf(rate)
                         .setScale(3, ROUNDING)
                         .doubleValue();
    }

    /**
     * Calculates monthly Principal and Interest payment.
     * Uses standard amortization formula: M = P[c(1+c)^n]/[(1+c)^n-1]
     *
     * @param principal The modification principal amount
     * @param annualRate Annual interest rate as percentage
     * @param termMonths Modification term in months
     * @return Monthly P&I payment
     */
    public double calculateModifiedPayment(double principal, double annualRate, int termMonths) {
        if (principal <= 0 || termMonths <= 0) return 0.0;
        if (annualRate <= 0) return principal / termMonths;

        double monthlyRate = (annualRate / 100) / 12;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        double payment = principal * (monthlyRate * factor) / (factor - 1);

        return BigDecimal.valueOf(payment)
                         .setScale(2, ROUNDING)
                         .doubleValue();
    }

    /**
     * Calculates total PITI payment including taxes and insurance.
     *
     * @param principal The modification principal amount
     * @param annualRate Annual interest rate as percentage
     * @param termMonths Modification term in months
     * @param propertyValue Property value for tax/insurance estimates
     * @return Monthly PITI payment
     */
    public double calculateModifiedPITI(double principal, double annualRate, int termMonths, double propertyValue) {
        double monthlyPI = calculateModifiedPayment(principal, annualRate, termMonths);
        double monthlyTaxes = (propertyValue * 0.012) / 12; // 1.2% annual tax rate
        double monthlyInsurance = (propertyValue * 0.005) / 12; // 0.5% annual insurance

        return BigDecimal.valueOf(monthlyPI + monthlyTaxes + monthlyInsurance)
                         .setScale(2, ROUNDING)
                         .doubleValue();
    }

    /**
     * Generates an amortization schedule entry.
     */
    public ModifiedTermsEntry calculateModifiedEntry(double principal, double annualRate, int termMonths, int paymentNumber) {
        double modifiedMonthlyPayment = calculateModifiedPayment(principal, annualRate, termMonths);
        double monthlyRate = (annualRate / 100) / 12;

        double balance = principal;
        for (int i = 1; i < paymentNumber; i++) {
            double interest = balance * monthlyRate;
            double principalPaid = modifiedMonthlyPayment - interest;
            balance -= principalPaid;
        }

        double interest = balance * monthlyRate;
        double principalPaid = modifiedMonthlyPayment - interest;
        double newBalance = balance - principalPaid;

        return new ModifiedTermsEntry(paymentNumber, modifiedMonthlyPayment, principalPaid, interest, newBalance);
    }

    /**
     * Amortization schedule entry.
     */
    public static class ModifiedTermsEntry {
        private final int paymentNumber;
        private final double payment;
        private final double principal;
        private final double interest;
        private final double balance;

        public ModifiedTermsEntry(int paymentNumber, double payment, double principal, double interest, double balance) {
            this.paymentNumber = paymentNumber;
            this.payment = payment;
            this.principal = principal;
            this.interest = interest;
            this.balance = balance;
        }

        public int getPaymentNumber() { return paymentNumber; }
        public double getPayment() { return payment; }
        public double getPrincipal() { return principal; }
        public double getInterest() { return interest; }
        public double getBalance() { return balance; }
    }
}
