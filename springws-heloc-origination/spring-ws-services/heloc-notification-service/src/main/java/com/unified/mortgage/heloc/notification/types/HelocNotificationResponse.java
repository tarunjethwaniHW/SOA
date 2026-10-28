package com.unified.mortgage.heloc.notification.types;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "status", "message" })
@XmlRootElement(name = "HelocNotificationResponse", namespace = "http://xmlns.oracle.com/HelocOrchestration/types")
public class HelocNotificationResponse {

    @XmlElement(required = true)
    private String status;
    @XmlElement(required = true)
    private String message;

    public HelocNotificationResponse() {}

    public HelocNotificationResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
