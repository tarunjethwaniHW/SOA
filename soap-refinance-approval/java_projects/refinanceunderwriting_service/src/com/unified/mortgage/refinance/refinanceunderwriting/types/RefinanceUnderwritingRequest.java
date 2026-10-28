package com.unified.mortgage.refinance.refinanceunderwriting.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "creditScore",
    "loanAmount",
    "monthlyIncome",
    "propertyValue"
})
@XmlRootElement(name = "RefinanceUnderwritingRequest", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class RefinanceUnderwritingRequest {

    @XmlElement(required = true)
    protected int creditScore;

    @XmlElement(required = true)
    protected double loanAmount;

    @XmlElement(required = true)
    protected double monthlyIncome;

    @XmlElement(required = true)
    protected double propertyValue;

    public RefinanceUnderwritingRequest() {}

    public RefinanceUnderwritingRequest(int creditScore, double loanAmount, double monthlyIncome, double propertyValue) {
        this.creditScore = creditScore;
        this.loanAmount = loanAmount;
        this.monthlyIncome = monthlyIncome;
        this.propertyValue = propertyValue;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public double getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(double propertyValue) {
        this.propertyValue = propertyValue;
    }
}
