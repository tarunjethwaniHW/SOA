package com.unified.mortgage.refinance.clients;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

import com.unified.mortgage.refinance.approval.RefinanceApprovalProcessPort;
import com.unified.mortgage.refinance.approval.SanctionListFaultException;
import com.unified.mortgage.refinance.approval.types.RefinanceApplicationRequest;
import com.unified.mortgage.refinance.approval.types.RefinanceApplicationResponse;

/**
 * JAX-WS client that calls RefinanceApprovalProcess SOA composite via WSDL.
 * This is the main entry point that triggers the entire BPEL orchestration.
 *
 * WSDL Contract: refinance-service-soa/RefinanceApprovalProcess.wsdl
 * Service: refinanceapproval_client_ep
 * PortType: RefinanceApprovalProcess
 * Operation: initiateRefinance
 * Endpoint: http://localhost:9081/soap/RefinanceApprovalProcess
 */
public class RefinanceApprovalClient {

    private static final String WSDL_URL = "http://localhost:9081/soap/RefinanceApprovalProcess?wsdl";
    private static final String NAMESPACE = "http://xmlns.oracle.com/RefinanceOrchestration/RefinanceApprovalProcess";
    private static final String SERVICE_NAME = "refinanceapproval_client_ep";

    private final RefinanceApprovalProcessPort port;

    public RefinanceApprovalClient() throws Exception {
        URL wsdlURL = new URL(WSDL_URL);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(RefinanceApprovalProcessPort.class);
    }

    public RefinanceApprovalClient(String wsdlUrl) throws Exception {
        URL wsdlURL = new URL(wsdlUrl);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(RefinanceApprovalProcessPort.class);
    }

    /**
     * Calls the SOA Suite RefinanceApprovalProcess.initiateRefinance operation.
     * This triggers the BPEL orchestration which calls:
     * - CreditVerificationProcess.processCreditVerification
     * - ReappraisalProcess.processReappraisal
     * - RefinanceUnderwritingProcess.processRefinanceUnderwriting
     * - PayoffDisbursementProcess.processPayoffDisbursement (if approved)
     * - RefinanceNotificationProcess.processRefinanceNotification
     */
    public RefinanceApplicationResponse initiateRefinance(RefinanceApplicationRequest request)
            throws SanctionListFaultException {
        System.out.println("[RefinanceApprovalClient] Calling SOA Suite: initiateRefinance");
        System.out.println("[RefinanceApprovalClient] WSDL: " + WSDL_URL);
        System.out.println("[RefinanceApprovalClient] Applicant: " + request.getApplicantName());
        System.out.println("[RefinanceApprovalClient] Loan Amount: $" + request.getLoanAmount());

        RefinanceApplicationResponse response = port.initiateRefinance(request);

        System.out.println("[RefinanceApprovalClient] Response received from SOA Suite");
        return response;
    }

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     LOAN APPROVAL CLIENT - Calling SOA Suite via WSDL");
        System.out.println("=========================================================================");
        System.out.println("Target WSDL: " + WSDL_URL);
        System.out.println("Target Namespace: " + NAMESPACE);
        System.out.println("Service Name: " + SERVICE_NAME);
        System.out.println("=========================================================================");

        try {
            RefinanceApprovalClient client = new RefinanceApprovalClient();

            // Test Case 1: Standard loan application (should be AUTO_APPROVED)
            System.out.println("\n--- TEST CASE 1: Standard Application (Expected: AUTO_APPROVED) ---");
            RefinanceApplicationRequest request1 = new RefinanceApplicationRequest(
                "John Doe",           // applicantName
                "123-45-6789",        // ssn
                250000.0,             // loanAmount
                12000.0,              // monthlyIncome
                "123 Main St, VA",    // propertyAddress
                400000.0              // propertyValue
            );

            RefinanceApplicationResponse response1 = client.initiateRefinance(request1);
            printResponse(response1);

            // Test Case 2: Low credit score (should be AUTO_REJECTED)
            System.out.println("\n--- TEST CASE 2: Low Credit Score (Expected: AUTO_REJECTED) ---");
            RefinanceApplicationRequest request2 = new RefinanceApplicationRequest(
                "Jane Smith",
                "999-12-3456",        // SSN starting with 999 triggers low credit
                250000.0,
                12000.0,
                "456 Oak Ave, MD",
                400000.0
            );

            RefinanceApplicationResponse response2 = client.initiateRefinance(request2);
            printResponse(response2);

        } catch (SanctionListFaultException e) {
            System.out.println("[RefinanceApprovalClient] SANCTION FAULT: " + e.getMessage());
            System.out.println("  Error Code: " + e.getFaultInfo().getErrorCode());
            System.out.println("  Error Message: " + e.getFaultInfo().getErrorMessage());
        } catch (Exception e) {
            System.err.println("[RefinanceApprovalClient] Error connecting to SOA Suite: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================================================");
    }

    private static void printResponse(RefinanceApplicationResponse response) {
        System.out.println("[RESPONSE] Loan ID: " + response.getLoanId());
        System.out.println("[RESPONSE] Status: " + response.getStatus());
        System.out.println("[RESPONSE] Decision: " + response.getDecision());
        System.out.println("[RESPONSE] Notes: " + response.getDecisionNotes());
    }
}
