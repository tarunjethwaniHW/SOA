package com.unified.mortgage.refinance.reappraisal.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "appraisedValue",
    "status"
})
@XmlRootElement(name = "ReappraisalResponse", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class ReappraisalResponse {

    @XmlElement(required = true)
    protected double appraisedValue;

    @XmlElement(required = true)
    protected String status;

    public ReappraisalResponse() {}

    public ReappraisalResponse(double appraisedValue, String status) {
        this.appraisedValue = appraisedValue;
        this.status = status;
    }

    public double getAppraisedValue() {
        return appraisedValue;
    }

    public void setAppraisedValue(double appraisedValue) {
        this.appraisedValue = appraisedValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
