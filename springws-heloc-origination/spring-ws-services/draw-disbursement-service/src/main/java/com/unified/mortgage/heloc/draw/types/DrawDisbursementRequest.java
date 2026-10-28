package com.unified.mortgage.heloc.draw.types;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "helocId", "creditLineAmount" })
@XmlRootElement(name = "DrawDisbursementRequest", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class DrawDisbursementRequest {

    @XmlElement(required = true)
    private long helocId;
    @XmlElement(required = true)
    private double creditLineAmount;

    public DrawDisbursementRequest() {}

    public long getHelocId() { return helocId; }
    public void setHelocId(long helocId) { this.helocId = helocId; }
    public double getCreditLineAmount() { return creditLineAmount; }
    public void setCreditLineAmount(double creditLineAmount) { this.creditLineAmount = creditLineAmount; }
}
