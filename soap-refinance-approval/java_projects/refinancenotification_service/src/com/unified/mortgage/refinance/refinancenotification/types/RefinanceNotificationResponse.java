package com.unified.mortgage.refinance.refinancenotification.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "status",
    "message"
})
@XmlRootElement(name = "RefinanceNotificationResponse", namespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class RefinanceNotificationResponse {

    @XmlElement(required = true)
    protected String status;

    @XmlElement(required = true)
    protected String message;

    public RefinanceNotificationResponse() {}

    public RefinanceNotificationResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
