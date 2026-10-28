package com.unified.mortgage.heloc.underwriting.endpoint;

import com.unified.mortgage.heloc.underwriting.types.HelocUnderwritingRequest;
import com.unified.mortgage.heloc.underwriting.types.HelocUnderwritingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Spring-WS Endpoint for HELOC underwriting Service.
 *
 * RECEIVES SOAP calls FROM Oracle SOA Suite for HELOC underwriting.
 * Calculates DTI (Debt-to-Income) and CLTV (Combined Loan-to-Value) ratios.
 *
 * WSDL Contract: heloc-service-soa/HelocUnderwritingProcess.wsdl
 * Operation: processHelocUnderwriting
 */
@Endpoint
public class HelocUnderwritingEndpoint {

    private static final Logger log = LoggerFactory.getLogger(HelocUnderwritingEndpoint.class);
    private static final String NAMESPACE_URI = "http://xmlns.oracle.com/HelocOrchestration/types";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HelocUnderwritingRequest")
    @ResponsePayload
    public HelocUnderwritingResponse processHelocUnderwriting(@RequestPayload HelocUnderwritingRequest request) {
        log.info("[HelocUnderwritingEndpoint] Received HELOC underwriting request from SOA Suite");
        log.info("[HelocUnderwritingEndpoint] HELOC credit Score: {}", request.getFicoScore());
        log.info("[HelocUnderwritingEndpoint] HELOC Amount: ${}", request.getCreditLineAmount());

        // Calculate DTI (assuming HELOC payment is ~0.6% of HELOC amount monthly)
        double monthlyPayment = request.getCreditLineAmount() * 0.006;
        double debtToIncomeRatio = (monthlyPayment / request.getHouseholdIncome()) * 100;
        debtToIncomeRatio = Math.round(debtToIncomeRatio * 100.0) / 100.0;

        // Calculate CLTV
        double combinedLtv = (request.getCreditLineAmount() / request.getHomeValue()) * 100;
        combinedLtv = Math.round(combinedLtv * 100.0) / 100.0;

        // Determine decision
        String decision;
        String underwritingNotes;
        if (request.getFicoScore() >= 700 && debtToIncomeRatio <= 43 && combinedLtv <= 80) {
            decision = "AUTO_APPROVED";
            underwritingNotes = "Excellent HELOC credit, acceptable DTI and CLTV ratios";
        } else if (request.getFicoScore() >= 620 && debtToIncomeRatio <= 50 && combinedLtv <= 95) {
            decision = "MANUAL_REVIEW";
            underwritingNotes = "Requires manual underwriter review";
        } else {
            decision = "AUTO_REJECTED";
            underwritingNotes = "Does not meet minimum HELOC underwriting criteria";
        }

        log.info("[HelocUnderwritingEndpoint] Decision: {}, DTI: {}%, CLTV: {}%", decision, debtToIncomeRatio, combinedLtv);

        return new HelocUnderwritingResponse(decision, underwritingNotes, debtToIncomeRatio, combinedLtv);
    }
}
