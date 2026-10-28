package com.unified.mortgage.heloc.underwriting.types;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "decision", "underwritingNotes", "debtToIncomeRatio", "combinedLtv" })
@XmlRootElement(name = "HelocUnderwritingResponse", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocUnderwritingResponse {

    @XmlElement(required = true)
    private String decision;
    @XmlElement(required = true)
    private String underwritingNotes;
    @XmlElement(required = true)
    private double debtToIncomeRatio;
    @XmlElement(required = true)
    private double combinedLtv;

    public HelocUnderwritingResponse() {}

    public HelocUnderwritingResponse(String decision, String underwritingNotes, double debtToIncomeRatio, double combinedLtv) {
        this.decision = decision;
        this.underwritingNotes = underwritingNotes;
        this.debtToIncomeRatio = debtToIncomeRatio;
        this.combinedLtv = combinedLtv;
    }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getUnderwritingNotes() { return underwritingNotes; }
    public void setUnderwritingNotes(String underwritingNotes) { this.underwritingNotes = underwritingNotes; }
    public double getDebtToIncomeRatio() { return debtToIncomeRatio; }
    public void setDebtToIncomeRatio(double debtToIncomeRatio) { this.debtToIncomeRatio = debtToIncomeRatio; }
    public double getCombinedLtv() { return combinedLtv; }
    public void setCombinedLtv(double combinedLtv) { this.combinedLtv = combinedLtv; }
}
