package com.unified.mortgage.heloc.credit.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * JAXB-annotated response type for HelocCreditProcess SOAP service.
 *
 * Namespace matches WSDL: http://xmlns.oracle.com/HelocOrchestration/types
 * Element name: HelocCreditScoreResponse
 *
 * This is the response sent back to Oracle SOA Suite
 * from the processHelocCreditCheck operation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ficoScore", "ficoStatus" })
@XmlRootElement(name = "HelocCreditScoreResponse", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocCreditScoreResponse {

    @XmlElement(required = true)
    private int ficoScore;

    @XmlElement(required = true)
    private String ficoStatus;

    public HelocCreditScoreResponse() {}

    public HelocCreditScoreResponse(int ficoScore, String ficoStatus) {
        this.ficoScore = ficoScore;
        this.ficoStatus = ficoStatus;
    }

    public int getFicoScore() { return ficoScore; }
    public void setFicoScore(int ficoScore) { this.ficoScore = ficoScore; }

    public String getFicoStatus() { return ficoStatus; }
    public void setFicoStatus(String ficoStatus) { this.ficoStatus = ficoStatus; }

    @Override
    public String toString() {
        return "HelocCreditScoreResponse{ficoScore=" + ficoScore + ", ficoStatus='" + ficoStatus + "'}";
    }
}
