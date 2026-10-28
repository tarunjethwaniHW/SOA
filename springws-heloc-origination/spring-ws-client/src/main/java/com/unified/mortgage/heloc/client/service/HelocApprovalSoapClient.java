package com.unified.mortgage.heloc.client.service;

import com.unified.mortgage.heloc.client.types.HelocApplicationRequest;
import com.unified.mortgage.heloc.client.types.HelocApplicationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Spring-WS SOAP Client for Oracle SOA Suite HelocApprovalProcess.
 *
 * COMPARISON WITH JAX-WS:
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  JAX-WS Pattern (Original SOA_Suite_Final_Ex1-main)                     │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  Service service = Service.create(wsdlURL, serviceName);                │
 * │  HelocApprovalProcessPort port = service.getPort(...);                   │
 * │  HelocApplicationResponse response = port.initiateHeloc(request);         │
 * │                                          ^^^^^^^^^^^^                   │
 * │                                          Operation name in code!        │
 * │                                                                         │
 * │  Parser Output:                                                         │
 * │  - calleeClass: "HelocApprovalProcessPort"                               │
 * │  - calleeFunction: "initiateHeloc"                                       │
 * │  - Can link to WSDL via Port suffix stripping                           │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  WebServiceTemplate Pattern (This project)                              │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  HelocApplicationResponse response = (HelocApplicationResponse)           │
 * │      webServiceTemplate.marshalSendAndReceive(request);                 │
 * │                         ^^^^^^^^^^^^^^^^^^^^^^                          │
 * │                         Generic method name!                            │
 * │                                                                         │
 * │  Parser Output:                                                         │
 * │  - calleeClass: "WebServiceTemplate"                                    │
 * │  - calleeFunction: "marshalSendAndReceive"                              │
 * │  - Cannot directly link to WSDL (generic names)                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * WSDL Contract: heloc-service-soa/HelocApprovalProcess.wsdl
 * Operation: initiateHeloc
 * Endpoint: http://localhost:10081/soap/HelocApprovalProcess
 */
@Service
public class HelocApprovalSoapClient {

    private static final Logger log = LoggerFactory.getLogger(HelocApprovalSoapClient.class);

    private final WebServiceTemplate webServiceTemplate;

    @Autowired
    public HelocApprovalSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * Calls the SOA Suite HelocApprovalProcess.initiateHeloc operation.
     *
     * This triggers the BPEL orchestration which calls:
     * - HelocCreditProcess.processHelocCreditCheck
     * - EquityAppraisalProcess.appraise
     * - HelocUnderwritingProcess.processHelocUnderwriting
     * - DrawDisbursementProcess.processDrawDisbursement (if approved)
     * - HelocNotificationProcess.processHelocNotification
     *
     * NOTE: The operation name "initiateHeloc" is NOT in the code!
     * SOA Suite determines the operation from the XML element name (HelocApplicationRequest).
     *
     * @param request The HELOC application request
     * @return The HELOC application response from SOA Suite
     */
    public HelocApplicationResponse initiateHeloc(HelocApplicationRequest request) {
        log.info("[HelocApprovalSoapClient] Calling SOA Suite via WebServiceTemplate");
        log.info("[HelocApprovalSoapClient] Homeowner: {}", request.getHomeownerName());
        log.info("[HelocApprovalSoapClient] HELOC Amount: ${}", request.getCreditLineAmount());

        try {
            // WebServiceTemplate SOAP call
            // - Marshalls HelocApplicationRequest to XML
            // - Validates XML against WSDL schema (via PayloadValidatingInterceptor)
            // - Sends SOAP request to endpoint
            // - Receives SOAP response
            // - Validates response XML against WSDL schema
            // - Unmarshalls XML to HelocApplicationResponse
            HelocApplicationResponse response = (HelocApplicationResponse)
                webServiceTemplate.marshalSendAndReceive(request);

            log.info("[HelocApprovalSoapClient] Response received from SOA Suite");
            log.info("[HelocApprovalSoapClient] HELOC ID: {}", response.getHelocId());
            log.info("[HelocApprovalSoapClient] Decision: {}", response.getDecision());

            return response;

        } catch (SoapFaultClientException e) {
            log.error("[HelocApprovalSoapClient] SOAP Fault: {}", e.getFaultStringOrReason());
            throw e;
        } catch (Exception e) {
            log.error("[HelocApprovalSoapClient] Error calling SOA Suite: {}", e.getMessage());
            throw new RuntimeException("HELOC approval request failed", e);
        }
    }

    /**
     * Alternative method with explicit endpoint URI.
     * Useful when calling different environments (dev, test, prod).
     */
    public HelocApplicationResponse initiateHeloc(String endpointUri, HelocApplicationRequest request) {
        log.info("[HelocApprovalSoapClient] Calling custom endpoint: {}", endpointUri);

        try {
            // marshalSendAndReceive with explicit URI
            HelocApplicationResponse response = (HelocApplicationResponse)
                webServiceTemplate.marshalSendAndReceive(endpointUri, request);

            return response;

        } catch (SoapFaultClientException e) {
            log.error("[HelocApprovalSoapClient] SOAP Fault: {}", e.getFaultStringOrReason());
            throw e;
        }
    }
}
