package com.unified.mortgage.escrow.client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * JAX-WS SOAP client that calls EscrowDBProcess SOA composite via WSDL.
 * This is the entry point that triggers the BPEL orchestration which
 * invokes multiple Database Adapters for escrow processing.
 *
 * WSDL Contract: escrow-db-composite/EscrowDBProcess.wsdl
 * Service: escrowdb_client_ep
 * PortType: EscrowDBProcess
 * Operations: submitEscrowAccount, getEscrowStatus, updateEscrowAnalysis
 * Endpoint: http://localhost:11081/soap/EscrowDBProcess
 */
public class EscrowDBClient {

    private static final String WSDL_URL = "http://localhost:11081/soap/EscrowDBProcess?wsdl";
    private static final String NAMESPACE = "http://xmlns.oracle.com/EscrowServicing/EscrowDBProcess";
    private static final String SERVICE_NAME = "escrowdb_client_ep";

    private final EscrowDBProcessPort port;

    public EscrowDBClient() throws Exception {
        URL wsdlURL = new URL(WSDL_URL);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(EscrowDBProcessPort.class);
    }

    public EscrowDBClient(String wsdlUrl) throws Exception {
        URL wsdlURL = new URL(wsdlUrl);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(EscrowDBProcessPort.class);
    }

    /**
     * Submits a escrow application via SOA composite.
     * This triggers BPEL orchestration which:
     * 1. Inserts escrow holder record (EscrowHolderDB.insertEscrowHolder)
     * 2. Inserts escrow application (EscrowAccountDB.insertEscrowAccount)
     * 3. Calculates shortage ratio (EscrowAccountDB.calculateShortage - stored procedure)
     * 4. Runs analysis assessment (EscrowAnalysisDB.runEscrowAnalysis - stored procedure)
     * 5. Inserts analysisOutcome record (EscrowAnalysisDB.insertEscrowAnalysis)
     */
    public EscrowAccountResponse submitEscrowAccount(EscrowAccountRequest request) {
        System.out.println("[EscrowDBClient] Calling SOA Suite: submitEscrowAccount");
        System.out.println("[EscrowDBClient] WSDL: " + WSDL_URL);
        System.out.println("[EscrowDBClient] Holder TAX_ID: " + request.getTaxId());
        System.out.println("[EscrowDBClient] Escrow Amount: $" + request.getAnnualDisbursement());
        System.out.println("[EscrowDBClient] Property Value: $" + request.getEscrowBalance());

        EscrowAccountResponse response = port.submitEscrowAccount(request);

        System.out.println("[EscrowDBClient] Response received from SOA Suite");
        System.out.println("[EscrowDBClient] Escrow ID: " + response.getEscrowAccountId());
        System.out.println("[EscrowDBClient] Escrow analysis: " + response.getAnalysisOutcome());
        return response;
    }

    /**
     * Gets escrow status by ID.
     * Triggers BPEL orchestration which:
     * 1. Selects escrow by ID (EscrowAccountDB.selectEscrowAccountById)
     * 2. Selects analysisOutcome by escrow (EscrowAnalysisDB.selectEscrowAnalysisByAccount)
     */
    public EscrowStatusResponse getEscrowStatus(String escrowAccountId) {
        System.out.println("[EscrowDBClient] Calling SOA Suite: getEscrowStatus");
        System.out.println("[EscrowDBClient] Escrow ID: " + escrowAccountId);

        EscrowStatusResponse response = port.getEscrowStatus(escrowAccountId);

        System.out.println("[EscrowDBClient] Status: " + response.getStatus());
        return response;
    }

    /**
     * Updates escrow analysisOutcome (for manual review cases).
     * Triggers BPEL orchestration which:
     * 1. Updates escrow application (EscrowAccountDB.updateEscrowAccount)
     * 2. Inserts new analysisOutcome (EscrowAnalysisDB.insertEscrowAnalysis)
     */
    public UpdateEscrowAnalysisResponse updateEscrowAnalysis(UpdateEscrowAnalysisRequest request) {
        System.out.println("[EscrowDBClient] Calling SOA Suite: updateEscrowAnalysis");
        System.out.println("[EscrowDBClient] Escrow ID: " + request.getEscrowAccountId());
        System.out.println("[EscrowDBClient] New Escrow analysis: " + request.getAnalysisOutcome());

        UpdateEscrowAnalysisResponse response = port.updateEscrowAnalysis(request);

        System.out.println("[EscrowDBClient] Update Status: " + response.getStatus());
        return response;
    }

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     ESCROW DB CLIENT - Calling SOA Suite Database Adapters via WSDL");
        System.out.println("=========================================================================");
        System.out.println("Target WSDL: " + WSDL_URL);
        System.out.println("Target Namespace: " + NAMESPACE);
        System.out.println("Service Name: " + SERVICE_NAME);
        System.out.println("=========================================================================");

        try {
            EscrowDBClient client = new EscrowDBClient();

            // Test Case 1: Submit new escrow application
            System.out.println("\n--- TEST CASE 1: Submit Escrow Application ---");
            EscrowAccountRequest request1 = new EscrowAccountRequest();
            request1.setHolderName("John Doe");
            request1.setTaxId("123-45-6789");
            request1.setAnnualDisbursement(250000.0);
            request1.setEscrowBalance(400000.0);
            request1.setMonthlyEscrowPayment(12000.0);
            request1.setParcelAddress("123 Main St, VA 22030");

            EscrowAccountResponse response1 = client.submitEscrowAccount(request1);
            printApplicationResponse(response1);

            // Test Case 2: Get escrow status
            System.out.println("\n--- TEST CASE 2: Get Escrow Status ---");
            EscrowStatusResponse response2 = client.getEscrowStatus(response1.getEscrowAccountId());
            printStatusResponse(response2);

            // Test Case 3: Update escrow analysis outcome (manual review)
            System.out.println("\n--- TEST CASE 3: Update Escrow Analysis ---");
            UpdateEscrowAnalysisRequest request3 = new UpdateEscrowAnalysisRequest();
            request3.setEscrowAccountId(response1.getEscrowAccountId());
            request3.setAnalysisOutcome("APPROVED");
            request3.setReviewerNotes("Manual review completed - approved with conditions");

            UpdateEscrowAnalysisResponse response3 = client.updateEscrowAnalysis(request3);
            printUpdateResponse(response3);

        } catch (Exception e) {
            System.err.println("[EscrowDBClient] Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================================================");
    }

    private static void printApplicationResponse(EscrowAccountResponse response) {
        System.out.println("[RESPONSE] Escrow ID: " + response.getEscrowAccountId());
        System.out.println("[RESPONSE] Escrow analysis: " + response.getAnalysisOutcome());
        System.out.println("[RESPONSE] shortage Ratio: " + response.getShortageRatio() + "%");
        System.out.println("[RESPONSE] Analysis Score: " + response.getAnalysisScore());
        System.out.println("[RESPONSE] Notes: " + response.getAnalysisNotes());
    }

    private static void printStatusResponse(EscrowStatusResponse response) {
        System.out.println("[RESPONSE] Status: " + response.getStatus());
        System.out.println("[RESPONSE] Escrow analysis: " + response.getAnalysisOutcome());
        System.out.println("[RESPONSE] Last Updated: " + response.getLastUpdated());
    }

    private static void printUpdateResponse(UpdateEscrowAnalysisResponse response) {
        System.out.println("[RESPONSE] Status: " + response.getStatus());
        System.out.println("[RESPONSE] Updated At: " + response.getUpdatedAt());
    }
}

// Supporting classes (normally generated from WSDL)
interface EscrowDBProcessPort {
    EscrowAccountResponse submitEscrowAccount(EscrowAccountRequest request);
    EscrowStatusResponse getEscrowStatus(String escrowAccountId);
    UpdateEscrowAnalysisResponse updateEscrowAnalysis(UpdateEscrowAnalysisRequest request);
}

class EscrowAccountRequest {
    private String holderName;
    private String taxId;
    private double annualDisbursement;
    private double escrowBalance;
    private double monthlyEscrowPayment;
    private String parcelAddress;

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public double getAnnualDisbursement() { return annualDisbursement; }
    public void setAnnualDisbursement(double annualDisbursement) { this.annualDisbursement = annualDisbursement; }
    public double getEscrowBalance() { return escrowBalance; }
    public void setEscrowBalance(double escrowBalance) { this.escrowBalance = escrowBalance; }
    public double getMonthlyEscrowPayment() { return monthlyEscrowPayment; }
    public void setMonthlyEscrowPayment(double monthlyEscrowPayment) { this.monthlyEscrowPayment = monthlyEscrowPayment; }
    public String getParcelAddress() { return parcelAddress; }
    public void setParcelAddress(String parcelAddress) { this.parcelAddress = parcelAddress; }
}

class EscrowAccountResponse {
    private String escrowAccountId;
    private String analysisOutcome;
    private double shortageRatio;
    private int analysisScore;
    private String analysisNotes;

    public String getEscrowAccountId() { return escrowAccountId; }
    public void setEscrowAccountId(String escrowAccountId) { this.escrowAccountId = escrowAccountId; }
    public String getAnalysisOutcome() { return analysisOutcome; }
    public void setAnalysisOutcome(String analysisOutcome) { this.analysisOutcome = analysisOutcome; }
    public double getShortageRatio() { return shortageRatio; }
    public void setShortageRatio(double shortageRatio) { this.shortageRatio = shortageRatio; }
    public int getAnalysisScore() { return analysisScore; }
    public void setAnalysisScore(int analysisScore) { this.analysisScore = analysisScore; }
    public String getAnalysisNotes() { return analysisNotes; }
    public void setAnalysisNotes(String analysisNotes) { this.analysisNotes = analysisNotes; }
}

class EscrowStatusResponse {
    private String status;
    private String analysisOutcome;
    private String lastUpdated;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAnalysisOutcome() { return analysisOutcome; }
    public void setAnalysisOutcome(String analysisOutcome) { this.analysisOutcome = analysisOutcome; }
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}

class UpdateEscrowAnalysisRequest {
    private String escrowAccountId;
    private String analysisOutcome;
    private String reviewerNotes;

    public String getEscrowAccountId() { return escrowAccountId; }
    public void setEscrowAccountId(String escrowAccountId) { this.escrowAccountId = escrowAccountId; }
    public String getAnalysisOutcome() { return analysisOutcome; }
    public void setAnalysisOutcome(String analysisOutcome) { this.analysisOutcome = analysisOutcome; }
    public String getReviewerNotes() { return reviewerNotes; }
    public void setReviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; }
}

class UpdateEscrowAnalysisResponse {
    private String status;
    private String updatedAt;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
