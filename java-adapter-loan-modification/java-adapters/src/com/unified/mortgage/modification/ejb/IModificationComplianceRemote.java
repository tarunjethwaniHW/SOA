package com.unified.mortgage.modification.ejb;

import javax.ejb.Remote;

/**
 * Compliance Check EJB Remote Interface
 * JNDI: ejb/ModificationCompliance
 * Referenced from: composite.xml binding.ejb
 *
 * Call Chain:
 *   SOA_REFERENCE (ModificationComplianceEJB)
 *       → REFERENCE_TO_EJB (binding.ejb jndi-name="ejb/ModificationCompliance")
 *       → IModificationComplianceRemote.java (@Remote interface)
 *       → ModificationComplianceEJB.java (@Stateless implementation)
 *
 * This interface is invoked from SOA Suite via:
 *   <binding.ejb uri="ModificationComplianceEJB#com.unified.mortgage.modification.ejb.IModificationComplianceRemote"
 *                jndi-name="ejb/ModificationCompliance"/>
 */
@Remote
public interface IModificationComplianceRemote {

    /**
     * Check regulatory compliance for a modification application.
     * @param unpaidPrincipalBalance Requested modification amount
     * @param postModDti Debt-to-income ratio
     * @param markToMarketLtv Modification-to-value ratio
     * @return Compliance check result
     */
    ModificationComplianceResult checkModificationCompliance(double unpaidPrincipalBalance, double postModDti, double markToMarketLtv);

    /**
     * Check if modification meets QM (Qualified Mortgage) requirements.
     * @param postModDti Debt-to-income ratio
     * @return true if meets QM requirements (DTI <= 43%)
     */
    boolean isQualifiedModification(double postModDti);

    /**
     * Check for TRID compliance (TILA-RESPA Integrated Disclosure).
     * @param disclosureDate Date disclosures were provided
     * @param closingDate Scheduled closing date
     * @return true if timing requirements are met
     */
    boolean meetsModificationNoticeTiming(java.util.Date disclosureDate, java.util.Date closingDate);
}
