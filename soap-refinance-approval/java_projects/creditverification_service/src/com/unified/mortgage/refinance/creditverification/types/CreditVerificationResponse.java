package com.unified.mortgage.refinance.creditverification.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "creditScore"
})
@XmlRootElement(name = "CreditVerificationResponse", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class CreditVerificationResponse {

    @XmlElement(required = true)
    protected int creditScore;

    public CreditVerificationResponse() {}

    public CreditVerificationResponse(int creditScore) {
        this.creditScore = creditScore;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }
}
