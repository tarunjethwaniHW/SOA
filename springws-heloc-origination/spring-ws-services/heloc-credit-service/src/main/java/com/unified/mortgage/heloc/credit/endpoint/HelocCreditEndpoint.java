package com.unified.mortgage.heloc.credit.endpoint;

import com.unified.mortgage.heloc.credit.types.HelocCreditScoreRequest;
import com.unified.mortgage.heloc.credit.types.HelocCreditScoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Spring-WS Endpoint for HELOC credit Service.
 *
 * RECEIVES SOAP calls FROM Oracle SOA Suite (downstream pattern).
 *
 * COMPARISON WITH JAX-WS @WebService:
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  JAX-WS @WebService Pattern                                             │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  @WebService(                                                           │
 * │      serviceName = "HelocCreditProcess",      ← For parser detection         │
 * │      portName = "HelocCreditProcessPort",                                    │
 * │      targetNamespace = "..."                                            │
 * │  )                                                                      │
 * │  public class HelocCreditProcessImpl {                                       │
 * │      @WebMethod                                                         │
 * │      public HelocCreditScoreResponse processHelocCreditCheck(HelocCreditScoreRequest)  │
 * │  }                                                                      │
 * │                                                                         │
 * │  Parser extracts: serviceName="HelocCreditProcess" from @WebService          │
 * │  Stores in: inheritsFrom field for linker matching                      │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  Spring-WS @Endpoint Pattern (This class)                               │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  @Endpoint                               ← Marks as SOAP endpoint       │
 * │  public class HelocCreditEndpoint {                                          │
 * │      @PayloadRoot(                       ← Routes by XML element        │
 * │          localPart = "HelocCreditScoreRequest",  ← Request element name      │
 * │          namespace = "http://..."           ← Namespace                 │
 * │      )                                                                  │
 * │      @ResponsePayload                    ← Return value is SOAP body    │
 * │      public HelocCreditScoreResponse processHelocCreditCheck(                     │
 * │          @RequestPayload HelocCreditScoreRequest request                     │
 * │      )                                                                  │
 * │  }                                                                      │
 * │                                                                         │
 * │  Parser would extract: localPart="HelocCreditScoreRequest" from @PayloadRoot │
 * │  Linker would match: localPart vs WSDL message element name             │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * NOTE: The parser currently does NOT detect @Endpoint/@PayloadRoot pattern.
 *       This is a gap that would need to be implemented for full Spring-WS support.
 *
 * WSDL Contract: heloc-service-soa/HelocCreditProcess.wsdl
 * Operation: processHelocCreditCheck
 */
@Endpoint
public class HelocCreditEndpoint {

    private static final Logger log = LoggerFactory.getLogger(HelocCreditEndpoint.class);

    private static final String NAMESPACE_URI = "http://xmlns.oracle.com/HelocOrchestration/types";

    /**
     * Process HELOC credit check request from SOA Suite.
     *
     * The @PayloadRoot annotation routes SOAP requests based on:
     * - localPart: The XML element name in the SOAP body (HelocCreditScoreRequest)
     * - namespace: The XML namespace of that element
     *
     * When SOA Suite calls this service, it sends:
     * <soap:Body>
     *   <HelocCreditScoreRequest xmlns="http://xmlns.oracle.com/HelocOrchestration/types">
     *     <taxpayerId>123-45-6789</taxpayerId>
     *   </HelocCreditScoreRequest>
     * </soap:Body>
     *
     * Spring-WS matches the request to this method via @PayloadRoot.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HelocCreditScoreRequest")
    @ResponsePayload
    public HelocCreditScoreResponse processHelocCreditCheck(@RequestPayload HelocCreditScoreRequest request) {
        log.info("[HelocCreditEndpoint] Received HELOC credit check request from SOA Suite");
        log.info("[HelocCreditEndpoint] Taxpayer ID: {}", maskTaxpayerId(request.getTaxpayerId()));

        // Simulate HELOC credit score lookup
        int ficoScore = calculateFicoScore(request.getTaxpayerId());
        String ficoStatus = determineFicoStatus(ficoScore);

        log.info("[HelocCreditEndpoint] HELOC credit Score: {}, Status: {}", ficoScore, ficoStatus);

        return new HelocCreditScoreResponse(ficoScore, ficoStatus);
    }

    private int calculateFicoScore(String taxpayerId) {
        // Simulated HELOC credit score based on Taxpayer ID pattern
        // In real system, this would call HELOC credit bureau APIs
        if (taxpayerId != null && taxpayerId.startsWith("999")) {
            return 450; // Poor HELOC credit for testing
        } else if (taxpayerId != null && taxpayerId.startsWith("888")) {
            return 620; // Fair HELOC credit
        } else {
            return 750; // Good HELOC credit (default)
        }
    }

    private String determineFicoStatus(int score) {
        if (score >= 700) {
            return "EXCELLENT";
        } else if (score >= 650) {
            return "GOOD";
        } else if (score >= 600) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }

    private String maskTaxpayerId(String taxpayerId) {
        if (taxpayerId == null || taxpayerId.length() < 4) {
            return "***";
        }
        return "***-**-" + taxpayerId.substring(taxpayerId.length() - 4);
    }
}
