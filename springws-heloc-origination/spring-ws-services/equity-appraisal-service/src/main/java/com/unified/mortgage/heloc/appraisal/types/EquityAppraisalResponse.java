package com.unified.mortgage.heloc.appraisal.types;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "appraisedHomeValue", "status" })
@XmlRootElement(name = "EquityAppraisalResponse", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class EquityAppraisalResponse {

    @XmlElement(required = true)
    private double appraisedHomeValue;

    @XmlElement(required = true)
    private String status;

    public EquityAppraisalResponse() {}

    public EquityAppraisalResponse(double appraisedHomeValue, String status) {
        this.appraisedHomeValue = appraisedHomeValue;
        this.status = status;
    }

    public double getAppraisedHomeValue() { return appraisedHomeValue; }
    public void setAppraisedHomeValue(double appraisedHomeValue) { this.appraisedHomeValue = appraisedHomeValue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
