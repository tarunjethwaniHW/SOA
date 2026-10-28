package com.unified.mortgage.heloc.underwriting.types;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ficoScore", "creditLineAmount", "householdIncome", "homeValue" })
@XmlRootElement(name = "HelocUnderwritingRequest", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocUnderwritingRequest {

    @XmlElement(required = true)
    private int ficoScore;
    @XmlElement(required = true)
    private double creditLineAmount;
    @XmlElement(required = true)
    private double householdIncome;
    @XmlElement(required = true)
    private double homeValue;

    public HelocUnderwritingRequest() {}

    public int getFicoScore() { return ficoScore; }
    public void setFicoScore(int ficoScore) { this.ficoScore = ficoScore; }
    public double getCreditLineAmount() { return creditLineAmount; }
    public void setCreditLineAmount(double creditLineAmount) { this.creditLineAmount = creditLineAmount; }
    public double getHouseholdIncome() { return householdIncome; }
    public void setHouseholdIncome(double householdIncome) { this.householdIncome = householdIncome; }
    public double getHomeValue() { return homeValue; }
    public void setHomeValue(double homeValue) { this.homeValue = homeValue; }
}
