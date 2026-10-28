package com.unified.mortgage.modification.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Compliance Check Stateless Session Bean
 * Deployed to WebLogic, accessed via binding.ejb from SOA Composite
 *
 * JNDI Name: ejb/ModificationCompliance
 * EJB URI: ModificationComplianceEJB#com.unified.mortgage.modification.ejb.IModificationComplianceRemote
 *
 * This EJB is invoked by SOA Suite via:
 *   <reference name="ModificationComplianceEJB">
 *       <interface.java interface="com.unified.mortgage.modification.ejb.IModificationComplianceRemote"/>
 *       <binding.ejb uri="ModificationComplianceEJB#com.unified.mortgage.modification.ejb.IModificationComplianceRemote"
 *                    jndi-name="ejb/ModificationCompliance">
 *           <property name="java.naming.factory.initial">
 *               weblogic.jndi.WLInitialContextFactory
 *           </property>
 *           <property name="java.naming.provider.url">
 *               t3://compliance-server:12001
 *           </property>
 *       </binding.ejb>
 *   </reference>
 */
@Stateless(name = "ModificationComplianceEJB", mappedName = "ejb/ModificationCompliance")
public class ModificationComplianceEJB implements IModificationComplianceRemote {

    private static final double QM_DTI_THRESHOLD = 43.0;
    private static final double HIGH_COST_LTV_THRESHOLD = 80.0;
    private static final double JUMBO_LOAN_THRESHOLD = 726200.0; // 2023 conforming limit
    private static final int TRID_WAITING_PERIOD_DAYS = 3;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ModificationComplianceResult checkModificationCompliance(double unpaidPrincipalBalance, double postModDti, double markToMarketLtv) {
        ModificationComplianceResult result = new ModificationComplianceResult();
        result.setCompliant(true);

        // QM eligibility check
        result.setQmEligible(postModDti <= QM_DTI_THRESHOLD);
        if (!result.isQmEligible()) {
            result.addWarning("DTI exceeds QM threshold of " + QM_DTI_THRESHOLD + "%");
        }

        // High-cost modification check (HPML rules)
        if (markToMarketLtv > HIGH_COST_LTV_THRESHOLD) {
            result.addWarning("LTV > 80% - may require PMI or additional documentation");
        }

        // Jumbo modification check
        if (unpaidPrincipalBalance > JUMBO_LOAN_THRESHOLD) {
            result.addWarning("Modification exceeds conforming limit - jumbo modification rules apply");
        }

        // Extreme DTI violation
        if (postModDti > 50.0) {
            result.addViolation("DTI exceeds maximum allowable limit of 50%");
        }

        // Extreme LTV violation
        if (markToMarketLtv > 97.0) {
            result.addViolation("LTV exceeds maximum allowable limit of 97%");
        }

        // Set recommendation
        if (result.isCompliant() && result.isQmEligible()) {
            result.setRecommendation("APPROVE - All compliance checks passed");
        } else if (result.isCompliant()) {
            result.setRecommendation("APPROVE WITH CONDITIONS - Review warnings");
        } else {
            result.setRecommendation("DECLINE - Compliance violations found");
        }

        return result;
    }

    @Override
    public boolean isQualifiedModification(double postModDti) {
        return postModDti <= QM_DTI_THRESHOLD;
    }

    @Override
    public boolean meetsModificationNoticeTiming(Date disclosureDate, Date closingDate) {
        if (disclosureDate == null || closingDate == null) {
            return false;
        }

        long diffInMillis = closingDate.getTime() - disclosureDate.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        // TRID requires at least 3 business days between disclosure and closing
        return diffInDays >= TRID_WAITING_PERIOD_DAYS;
    }
}
