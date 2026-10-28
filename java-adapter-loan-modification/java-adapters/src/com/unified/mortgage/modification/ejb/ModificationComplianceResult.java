package com.unified.mortgage.modification.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Compliance Check Result Data Transfer Object
 * Returned by IModificationComplianceRemote.checkModificationCompliance()
 */
public class ModificationComplianceResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean compliant;
    private boolean qmEligible;
    private boolean tridCompliant;
    private List<String> violations;
    private List<String> warnings;
    private String recommendation;

    public ModificationComplianceResult() {
        this.violations = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public ModificationComplianceResult(boolean compliant, boolean qmEligible) {
        this();
        this.compliant = compliant;
        this.qmEligible = qmEligible;
        this.tridCompliant = true;
    }

    // Getters and Setters
    public boolean isCompliant() { return compliant; }
    public void setCompliant(boolean compliant) { this.compliant = compliant; }

    public boolean isQmEligible() { return qmEligible; }
    public void setQmEligible(boolean qmEligible) { this.qmEligible = qmEligible; }

    public boolean isTridCompliant() { return tridCompliant; }
    public void setTridCompliant(boolean tridCompliant) { this.tridCompliant = tridCompliant; }

    public List<String> getViolations() { return violations; }
    public void setViolations(List<String> violations) { this.violations = violations; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public void addViolation(String violation) {
        this.violations.add(violation);
        this.compliant = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    @Override
    public String toString() {
        return "ModificationComplianceResult{compliant=" + compliant + ", qmEligible=" + qmEligible +
               ", violations=" + violations.size() + ", warnings=" + warnings.size() + "}";
    }
}
