package com.unified.mortgage.heloc.draw.endpoint;

import com.unified.mortgage.heloc.draw.types.DrawDisbursementRequest;
import com.unified.mortgage.heloc.draw.types.DrawDisbursementResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Spring-WS Endpoint for Draw disbursement Service.
 *
 * RECEIVES SOAP calls FROM Oracle SOA Suite to process HELOC draw disbursement.
 * Called only when HELOC is approved.
 *
 * WSDL Contract: heloc-service-soa/DrawDisbursementProcess.wsdl
 * Operation: processDrawDisbursement
 */
@Endpoint
public class DrawDisbursementEndpoint {

    private static final Logger log = LoggerFactory.getLogger(DrawDisbursementEndpoint.class);
    private static final String NAMESPACE_URI = "http://xmlns.oracle.com/HelocOrchestration/types";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DrawDisbursementRequest")
    @ResponsePayload
    public DrawDisbursementResponse processDrawDisbursement(@RequestPayload DrawDisbursementRequest request) {
        log.info("[DrawDisbursementEndpoint] Received draw disbursement request from SOA Suite");
        log.info("[DrawDisbursementEndpoint] HELOC ID: {}", request.getHelocId());
        log.info("[DrawDisbursementEndpoint] Amount: ${}", request.getCreditLineAmount());

        // Simulate draw disbursement processing
        String status = "DISBURSED";
        String message = String.format("HELOC %d disbursed successfully. Amount: $%.2f transferred to escrow.",
            request.getHelocId(), request.getCreditLineAmount());

        log.info("[DrawDisbursementEndpoint] Status: {}", status);

        return new DrawDisbursementResponse(status, message);
    }
}
