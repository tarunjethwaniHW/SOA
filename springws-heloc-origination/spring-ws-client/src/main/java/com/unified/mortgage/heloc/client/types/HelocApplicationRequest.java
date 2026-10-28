package com.unified.mortgage.heloc.client.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * JAXB-annotated request type for HelocApprovalProcess SOAP service.
 *
 * Namespace matches WSDL: http://xmlns.oracle.com/HelocOrchestration/types
 * Element name: HelocApplicationRequest
 *
 * Used by WebServiceTemplate.marshalSendAndReceive() to serialize
 * this object to XML when calling Oracle SOA Suite.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "homeownerName",
    "taxpayerId",
    "creditLineAmount",
    "householdIncome",
    "collateralAddress",
    "homeValue"
})
@XmlRootElement(name = "HelocApplicationRequest", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocApplicationRequest {

    @XmlElement(required = true)
    private String homeownerName;

    @XmlElement(required = true)
    private String taxpayerId;

    @XmlElement(required = true)
    private double creditLineAmount;

    @XmlElement(required = true)
    private double householdIncome;

    @XmlElement(required = true)
    private String collateralAddress;

    @XmlElement(required = true)
    private double homeValue;

    public HelocApplicationRequest() {}

    public HelocApplicationRequest(String homeownerName, String taxpayerId, double creditLineAmount,
                                   double householdIncome, String collateralAddress, double homeValue) {
        this.homeownerName = homeownerName;
        this.taxpayerId = taxpayerId;
        this.creditLineAmount = creditLineAmount;
        this.householdIncome = householdIncome;
        this.collateralAddress = collateralAddress;
        this.homeValue = homeValue;
    }

    public String getHomeownerName() { return homeownerName; }
    public void setHomeownerName(String homeownerName) { this.homeownerName = homeownerName; }

    public String getTaxpayerId() { return taxpayerId; }
    public void setTaxpayerId(String taxpayerId) { this.taxpayerId = taxpayerId; }

    public double getCreditLineAmount() { return creditLineAmount; }
    public void setCreditLineAmount(double creditLineAmount) { this.creditLineAmount = creditLineAmount; }

    public double getHouseholdIncome() { return householdIncome; }
    public void setHouseholdIncome(double householdIncome) { this.householdIncome = householdIncome; }

    public String getCollateralAddress() { return collateralAddress; }
    public void setCollateralAddress(String collateralAddress) { this.collateralAddress = collateralAddress; }

    public double getHomeValue() { return homeValue; }
    public void setHomeValue(double homeValue) { this.homeValue = homeValue; }

    @Override
    public String toString() {
        return "HelocApplicationRequest{" +
                "homeownerName='" + homeownerName + '\'' +
                ", taxpayerId='" + taxpayerId + '\'' +
                ", creditLineAmount=" + creditLineAmount +
                ", householdIncome=" + householdIncome +
                ", collateralAddress='" + collateralAddress + '\'' +
                ", homeValue=" + homeValue +
                '}';
    }
}
