package com.unified.mortgage.refinance.refinanceunderwriting.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "decision",
    "notes",
    "dti",
    "ltv"
})
@XmlRootElement(name = "RefinanceUnderwritingResponse", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class RefinanceUnderwritingResponse {

    @XmlElement(required = true)
    protected String decision;

    @XmlElement(required = true)
    protected String notes;

    @XmlElement(required = true)
    protected double dti;

    @XmlElement(required = true)
    protected double ltv;

    public RefinanceUnderwritingResponse() {}

    public RefinanceUnderwritingResponse(String decision, String notes, double dti, double ltv) {
        this.decision = decision;
        this.notes = notes;
        this.dti = dti;
        this.ltv = ltv;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getDti() {
        return dti;
    }

    public void setDti(double dti) {
        this.dti = dti;
    }

    public double getLtv() {
        return ltv;
    }

    public void setLtv(double ltv) {
        this.ltv = ltv;
    }
}
