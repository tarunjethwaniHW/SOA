package com.unified.mortgage.modification.ejb;

import javax.ejb.Remote;

/**
 * Credit Check EJB Remote Interface
 * JNDI: ejb/HardshipCreditReview
 * Referenced from: composite.xml binding.ejb
 *
 * Call Chain:
 *   SOA_REFERENCE (HardshipCreditReviewEJB)
 *       → REFERENCE_TO_EJB (binding.ejb jndi-name="ejb/HardshipCreditReview")
 *       → IHardshipCreditReviewRemote.java (@Remote interface)
 *       → HardshipCreditReviewEJB.java (@Stateless implementation)
 *
 * This interface is invoked from SOA Suite via:
 *   <binding.ejb uri="HardshipCreditReviewEJB#com.unified.mortgage.modification.ejb.IHardshipCreditReviewRemote"
 *                jndi-name="ejb/HardshipCreditReview"/>
 */
@Remote
public interface IHardshipCreditReviewRemote {

    /**
     * Check credit with external bureau.
     * @param borrowerTaxId Social Security Number (format: XXX-XX-XXXX)
     * @param firstName Applicant first name
     * @param lastName Applicant last name
     * @return Credit report data
     */
    HardshipCreditReport reviewHardshipCredit(String borrowerTaxId, String firstName, String lastName);

    /**
     * Get credit score only (faster, no full report).
     * @param borrowerTaxId Social Security Number
     * @return Credit score (300-850)
     */
    int getHardshipCreditScore(String borrowerTaxId);

    /**
     * Check if credit meets minimum threshold.
     * @param borrowerTaxId Social Security Number
     * @param minScore Minimum required score
     * @return true if credit score >= minScore
     */
    boolean meetsMinimumHardshipScore(String borrowerTaxId, int minScore);
}
