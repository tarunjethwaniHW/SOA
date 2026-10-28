package com.unified.mortgage.modification.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Credit Check Stateless Session Bean
 * Deployed to WebLogic, accessed via binding.ejb from SOA Composite
 *
 * JNDI Name: ejb/HardshipCreditReview
 * EJB URI: HardshipCreditReviewEJB#com.unified.mortgage.modification.ejb.IHardshipCreditReviewRemote
 *
 * This EJB is invoked by SOA Suite via:
 *   <reference name="HardshipCreditReviewEJB">
 *       <interface.java interface="com.unified.mortgage.modification.ejb.IHardshipCreditReviewRemote"/>
 *       <binding.ejb uri="HardshipCreditReviewEJB#com.unified.mortgage.modification.ejb.IHardshipCreditReviewRemote"
 *                    jndi-name="ejb/HardshipCreditReview">
 *           <property name="java.naming.factory.initial">
 *               weblogic.jndi.WLInitialContextFactory
 *           </property>
 *           <property name="java.naming.provider.url">
 *               t3://localhost:12001
 *           </property>
 *       </binding.ejb>
 *   </reference>
 */
@Stateless(name = "HardshipCreditReviewEJB", mappedName = "ejb/HardshipCreditReview")
public class HardshipCreditReviewEJB implements IHardshipCreditReviewRemote {

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public HardshipCreditReport reviewHardshipCredit(String borrowerTaxId, String firstName, String lastName) {
        // Simulate credit bureau call
        HardshipCreditReport report = new HardshipCreditReport();
        report.setBorrowerTaxId(maskBorrowerTaxId(borrowerTaxId));
        report.setScore(calculateHardshipScore(borrowerTaxId));
        report.setStatus("VERIFIED");
        report.setReportDate(new java.util.Date());
        report.setBureauName("TransUnion");
        report.setAccountCount(5 + Math.abs(borrowerTaxId.hashCode() % 10));
        report.setTotalDebt(10000 + Math.abs(borrowerTaxId.hashCode() % 50000));
        report.setLatePayments(Math.abs(borrowerTaxId.hashCode() % 3));

        return report;
    }

    @Override
    public int getHardshipCreditScore(String borrowerTaxId) {
        return calculateHardshipScore(borrowerTaxId);
    }

    @Override
    public boolean meetsMinimumHardshipScore(String borrowerTaxId, int minScore) {
        return calculateHardshipScore(borrowerTaxId) >= minScore;
    }

    /**
     * Calculate credit score based on SSN hash (demo only).
     */
    private int calculateHardshipScore(String borrowerTaxId) {
        if (borrowerTaxId == null || borrowerTaxId.isEmpty()) {
            return 650; // Default score
        }
        // Generate score between 580-820 based on SSN hash
        int baseScore = 580;
        int variation = Math.abs(borrowerTaxId.hashCode() % 240);
        return baseScore + variation;
    }

    /**
     * Mask SSN for security (show only last 4 digits).
     */
    private String maskBorrowerTaxId(String borrowerTaxId) {
        if (borrowerTaxId == null || borrowerTaxId.length() < 4) {
            return "XXX-XX-XXXX";
        }
        return "XXX-XX-" + borrowerTaxId.substring(borrowerTaxId.length() - 4);
    }
}
