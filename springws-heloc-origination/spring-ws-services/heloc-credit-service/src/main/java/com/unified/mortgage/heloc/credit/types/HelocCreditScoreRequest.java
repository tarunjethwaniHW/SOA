package com.unified.mortgage.heloc.credit.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * JAXB-annotated request type for HelocCreditProcess SOAP service.
 *
 * Namespace matches WSDL: http://xmlns.oracle.com/HelocOrchestration/types
 * Element name: HelocCreditScoreRequest
 *
 * This is the request that Oracle SOA Suite sends to this endpoint
 * when calling the processHelocCreditCheck operation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "taxpayerId" })
@XmlRootElement(name = "HelocCreditScoreRequest", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocCreditScoreRequest {

    @XmlElement(required = true)
    private String taxpayerId;

    public HelocCreditScoreRequest() {}

    public HelocCreditScoreRequest(String taxpayerId) {
        this.taxpayerId = taxpayerId;
    }

    public String getTaxpayerId() { return taxpayerId; }
    public void setTaxpayerId(String taxpayerId) { this.taxpayerId = taxpayerId; }

    @Override
    public String toString() {
        return "HelocCreditScoreRequest{taxpayerId='" + taxpayerId + "'}";
    }
}
