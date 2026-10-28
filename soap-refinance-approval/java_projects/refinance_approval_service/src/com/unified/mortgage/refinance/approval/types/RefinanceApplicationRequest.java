package com.unified.mortgage.refinance.approval.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "applicantName",
    "ssn",
    "loanAmount",
    "monthlyIncome",
    "propertyAddress",
    "propertyValue"
})
@XmlRootElement(name = "RefinanceApplicationRequest", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class RefinanceApplicationRequest {

    @XmlElement(required = true)
    protected String applicantName;

    @XmlElement(required = true)
    protected String ssn;

    @XmlElement(required = true)
    protected double loanAmount;

    @XmlElement(required = true)
    protected double monthlyIncome;

    @XmlElement(required = true)
    protected String propertyAddress;

    @XmlElement(required = true)
    protected double propertyValue;

    public RefinanceApplicationRequest() {}

    public RefinanceApplicationRequest(String applicantName, String ssn, double loanAmount, double monthlyIncome, String propertyAddress, double propertyValue) {
        this.applicantName = applicantName;
        this.ssn = ssn;
        this.loanAmount = loanAmount;
        this.monthlyIncome = monthlyIncome;
        this.propertyAddress = propertyAddress;
        this.propertyValue = propertyValue;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
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

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public double getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(double propertyValue) {
        this.propertyValue = propertyValue;
    }
}
