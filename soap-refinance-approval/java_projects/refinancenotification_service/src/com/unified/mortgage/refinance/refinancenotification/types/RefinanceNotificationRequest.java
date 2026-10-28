package com.unified.mortgage.refinance.refinancenotification.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "loanId",
    "applicantName",
    "decision"
})
@XmlRootElement(name = "RefinanceNotificationRequest", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class RefinanceNotificationRequest {

    @XmlElement(required = true)
    protected long loanId;

    @XmlElement(required = true)
    protected String applicantName;

    @XmlElement(required = true)
    protected String decision;

    public RefinanceNotificationRequest() {}

    public RefinanceNotificationRequest(long loanId, String applicantName, String decision) {
        this.loanId = loanId;
        this.applicantName = applicantName;
        this.decision = decision;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
