package com.unified.mortgage.refinance.approval.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "loanId",
    "status",
    "decision",
    "decisionNotes"
})
@XmlRootElement(name = "RefinanceApplicationResponse", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class RefinanceApplicationResponse {

    @XmlElement(required = true)
    protected long loanId;

    @XmlElement(required = true)
    protected String status;

    @XmlElement(required = true)
    protected String decision;

    @XmlElement(required = true)
    protected String decisionNotes;

    public RefinanceApplicationResponse() {}

    public RefinanceApplicationResponse(long loanId, String status, String decision, String decisionNotes) {
        this.loanId = loanId;
        this.status = status;
        this.decision = decision;
        this.decisionNotes = decisionNotes;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(String decisionNotes) {
        this.decisionNotes = decisionNotes;
    }
}
