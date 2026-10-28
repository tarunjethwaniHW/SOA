package com.unified.mortgage.refinance.reappraisal.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "propertyAddress"
})
@XmlRootElement(name = "ReappraisalRefundRequest", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class ReappraisalRefundRequest {

    @XmlElement(required = true)
    protected String propertyAddress;

    public ReappraisalRefundRequest() {}

    public ReappraisalRefundRequest(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }
}
