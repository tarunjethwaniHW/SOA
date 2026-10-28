package com.unified.mortgage.heloc.notification.types;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "helocId", "homeownerName", "decision" })
@XmlRootElement(name = "HelocNotificationRequest", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocNotificationRequest {

    @XmlElement(required = true)
    private long helocId;
    @XmlElement(required = true)
    private String homeownerName;
    @XmlElement(required = true)
    private String decision;

    public HelocNotificationRequest() {}

    public long getHelocId() { return helocId; }
    public void setHelocId(long helocId) { this.helocId = helocId; }
    public String getHomeownerName() { return homeownerName; }
    public void setHomeownerName(String homeownerName) { this.homeownerName = homeownerName; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}
