package com.unified.mortgage.heloc.appraisal.types;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "collateralAddress", "estimatedHomeValue" })
@XmlRootElement(name = "EquityAppraisalRequest", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class EquityAppraisalRequest {

    @XmlElement(required = true)
    private String collateralAddress;

    @XmlElement(required = true)
    private double estimatedHomeValue;

    public EquityAppraisalRequest() {}

    public EquityAppraisalRequest(String collateralAddress, double estimatedHomeValue) {
        this.collateralAddress = collateralAddress;
        this.estimatedHomeValue = estimatedHomeValue;
    }

    public String getCollateralAddress() { return collateralAddress; }
    public void setCollateralAddress(String collateralAddress) { this.collateralAddress = collateralAddress; }

    public double getEstimatedHomeValue() { return estimatedHomeValue; }
    public void setEstimatedHomeValue(double estimatedHomeValue) { this.estimatedHomeValue = estimatedHomeValue; }
}
