package com.unified.mortgage.refinance.creditverification.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ssn"
})
@XmlRootElement(name = "CreditVerificationRequest", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class CreditVerificationRequest {

    @XmlElement(required = true)
    protected String ssn;

    public CreditVerificationRequest() {}

    public CreditVerificationRequest(String ssn) {
        this.ssn = ssn;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
}
