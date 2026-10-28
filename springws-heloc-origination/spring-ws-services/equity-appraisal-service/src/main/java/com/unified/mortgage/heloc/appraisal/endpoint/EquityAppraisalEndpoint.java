package com.unified.mortgage.heloc.appraisal.endpoint;

import com.unified.mortgage.heloc.appraisal.types.EquityAppraisalRequest;
import com.unified.mortgage.heloc.appraisal.types.EquityAppraisalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Spring-WS Endpoint for Equity appraisal Service.
 *
 * RECEIVES SOAP calls FROM Oracle SOA Suite for property appraisal.
 * Replaces JAX-WS @WebService pattern with @Endpoint + @PayloadRoot.
 *
 * WSDL Contract: heloc-service-soa/EquityAppraisalProcess.wsdl
 * Operation: appraise
 */
@Endpoint
public class EquityAppraisalEndpoint {

    private static final Logger log = LoggerFactory.getLogger(EquityAppraisalEndpoint.class);
    private static final String NAMESPACE_URI = "http://xmlns.oracle.com/HelocOrchestration/types";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "EquityAppraisalRequest")
    @ResponsePayload
    public EquityAppraisalResponse processEquityAppraisal(@RequestPayload EquityAppraisalRequest request) {
        log.info("[EquityAppraisalEndpoint] Received appraisal request from SOA Suite");
        log.info("[EquityAppraisalEndpoint] Property: {}", request.getCollateralAddress());
        log.info("[EquityAppraisalEndpoint] Estimated Value: ${}", request.getEstimatedHomeValue());

        // Simulate property appraisal (typically +/- 10% of estimated value)
        double variance = (Math.random() - 0.5) * 0.2; // -10% to +10%
        double appraisedHomeValue = request.getEstimatedHomeValue() * (1 + variance);
        appraisedHomeValue = Math.round(appraisedHomeValue * 100.0) / 100.0;

        String status = appraisedHomeValue >= request.getEstimatedHomeValue() * 0.9 ? "APPROVED" : "BELOW_ESTIMATE";

        log.info("[EquityAppraisalEndpoint] Appraised Value: ${}, Status: {}", appraisedHomeValue, status);

        return new EquityAppraisalResponse(appraisedHomeValue, status);
    }
}
