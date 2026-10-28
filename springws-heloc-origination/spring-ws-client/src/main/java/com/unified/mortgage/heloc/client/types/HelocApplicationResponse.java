package com.unified.mortgage.heloc.client.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * JAXB-annotated response type for HelocApprovalProcess SOAP service.
 *
 * Namespace matches WSDL: http://xmlns.oracle.com/HelocOrchestration/types
 * Element name: HelocApplicationResponse
 *
 * Used by WebServiceTemplate.marshalSendAndReceive() to deserialize
 * the SOAP response from Oracle SOA Suite.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "helocId",
    "status",
    "decision",
    "decisionRemarks"
})
@XmlRootElement(name = "HelocApplicationResponse", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocApplicationResponse {

    @XmlElement(required = true)
    private long helocId;

    @XmlElement(required = true)
    private String status;

    @XmlElement(required = true)
    private String decision;

    @XmlElement(required = true)
    private String decisionRemarks;

    public HelocApplicationResponse() {}

    public HelocApplicationResponse(long helocId, String status, String decision, String decisionRemarks) {
        this.helocId = helocId;
        this.status = status;
        this.decision = decision;
        this.decisionRemarks = decisionRemarks;
    }

    public long getHelocId() { return helocId; }
    public void setHelocId(long helocId) { this.helocId = helocId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getDecisionRemarks() { return decisionRemarks; }
    public void setDecisionRemarks(String decisionRemarks) { this.decisionRemarks = decisionRemarks; }

    @Override
    public String toString() {
        return "HelocApplicationResponse{" +
                "helocId=" + helocId +
                ", status='" + status + '\'' +
                ", decision='" + decision + '\'' +
                ", decisionRemarks='" + decisionRemarks + '\'' +
                '}';
    }
}
