package com.unified.mortgage.heloc.notification.endpoint;

import com.unified.mortgage.heloc.notification.types.HelocNotificationRequest;
import com.unified.mortgage.heloc.notification.types.HelocNotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Spring-WS Endpoint for HELOC notification Service.
 *
 * RECEIVES SOAP calls FROM Oracle SOA Suite to send HELOC decision notifications.
 * Called at the end of the HELOC workflow regardless of approval/rejection.
 *
 * WSDL Contract: heloc-service-soa/HelocNotificationProcess.wsdl
 * Operation: processHelocNotification
 */
@Endpoint
public class HelocNotificationEndpoint {

    private static final Logger log = LoggerFactory.getLogger(HelocNotificationEndpoint.class);
    private static final String NAMESPACE_URI = "http://xmlns.oracle.com/HelocOrchestration/types";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HelocNotificationRequest")
    @ResponsePayload
    public HelocNotificationResponse processHelocNotification(@RequestPayload HelocNotificationRequest request) {
        log.info("[HelocNotificationEndpoint] Received HELOC notification request from SOA Suite");
        log.info("[HelocNotificationEndpoint] HELOC ID: {}", request.getHelocId());
        log.info("[HelocNotificationEndpoint] Homeowner: {}", request.getHomeownerName());
        log.info("[HelocNotificationEndpoint] Decision: {}", request.getDecision());

        // Simulate HELOC notification sending (email, SMS, etc.)
        String status = "SENT";
        String message = String.format(
            "HELOC notification sent to %s regarding HELOC %d. Decision: %s",
            request.getHomeownerName(),
            request.getHelocId(),
            request.getDecision()
        );

        log.info("[HelocNotificationEndpoint] HELOC notification sent successfully");

        return new HelocNotificationResponse(status, message);
    }
}
