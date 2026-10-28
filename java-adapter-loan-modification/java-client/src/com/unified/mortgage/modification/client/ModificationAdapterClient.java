package com.unified.mortgage.modification.client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * JAX-WS client that calls ModificationAdapterProcess SOA composite via WSDL.
 * This is the entry point that triggers the BPEL orchestration with Java embeddings.
 *
 * WSDL Contract: modification-adapter-composite/ModificationAdapterProcess.wsdl
 * Service: modificationadapter_client_ep
 * PortType: ModificationAdapterProcess
 * Operation: processModification
 * Endpoint: http://localhost:12080/soap/ModificationAdapterProcess
 *
 * Call Chain:
 *   ModificationAdapterClient → WSDL → composite.xml → BPEL → <bpelx:exec> → Java Adapter
 */
public class ModificationAdapterClient {

    private static final String WSDL_URL = "http://localhost:12080/soap/ModificationAdapterProcess?wsdl";
    private static final String NAMESPACE = "http://xmlns.oracle.com/LoanModification/ModificationAdapterProcess";
    private static final String SERVICE_NAME = "modificationadapter_client_ep";

    private final ModificationAdapterProcessPort port;

    public ModificationAdapterClient() throws Exception {
        URL wsdlURL = new URL(WSDL_URL);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(ModificationAdapterProcessPort.class);
    }

    public ModificationAdapterClient(String wsdlUrl) throws Exception {
        URL wsdlURL = new URL(wsdlUrl);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(ModificationAdapterProcessPort.class);
    }

    /**
     * Calls the SOA Suite ModificationAdapterProcess.processModification operation.
     * This triggers the BPEL orchestration which uses Java embeddings to call:
     * - ModificationDecisionEngine.evaluateModification() (Underwriting)
     * - PostModDtiCalculator.calculatePostModDti() (Borrower Intake)
     * - HardshipOcrProcessor.processHardshipDocument() (Document Processing)
     * - ModifiedTermsAmortizer.calculateModifiedRate() (Pricing)
     */
    public ModificationProcessResponse processModification(ModificationProcessRequest request) throws ProcessingFaultException {
        System.out.println("[ModificationAdapterClient] Calling SOA Suite: processModification");
        System.out.println("[ModificationAdapterClient] WSDL: " + WSDL_URL);
        System.out.println("[ModificationAdapterClient] Applicant: " + request.getHardshipBorrowerName());
        System.out.println("[ModificationAdapterClient] Modification Amount: $" + request.getUnpaidPrincipalBalance());

        ModificationProcessResponse response = port.processModification(request);

        System.out.println("[ModificationAdapterClient] Response received from SOA Suite");
        return response;
    }

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     JAVA ADAPTER CLIENT - Calling SOA Suite via WSDL");
        System.out.println("     Tests BPEL Java Embedding patterns");
        System.out.println("=========================================================================");
        System.out.println("Target WSDL: " + WSDL_URL);
        System.out.println("Target Namespace: " + NAMESPACE);
        System.out.println("Service Name: " + SERVICE_NAME);
        System.out.println("=========================================================================");

        try {
            ModificationAdapterClient client = new ModificationAdapterClient();

            // Test Case 1: Standard modification application
            System.out.println("\n--- TEST CASE 1: Standard Application ---");
            ModificationProcessRequest request1 = new ModificationProcessRequest();
            request1.setHardshipBorrowerName("John Doe");
            request1.setBorrowerTaxId("123-45-6789");
            request1.setUnpaidPrincipalBalance(250000.0);
            request1.setHardshipMonthlyIncome(12000.0);
            request1.setMonthlyObligations(2000.0);
            request1.setPropertyValue(300000.0);

            ModificationProcessResponse response1 = client.processModification(request1);
            printResponse(response1);

            // Test Case 2: High DTI application
            System.out.println("\n--- TEST CASE 2: High DTI Application ---");
            ModificationProcessRequest request2 = new ModificationProcessRequest();
            request2.setHardshipBorrowerName("Jane Smith");
            request2.setBorrowerTaxId("987-65-4321");
            request2.setUnpaidPrincipalBalance(350000.0);
            request2.setHardshipMonthlyIncome(8000.0);
            request2.setMonthlyObligations(4000.0);
            request2.setPropertyValue(400000.0);

            ModificationProcessResponse response2 = client.processModification(request2);
            printResponse(response2);

        } catch (ProcessingFaultException e) {
            System.out.println("[ModificationAdapterClient] PROCESSING FAULT: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ModificationAdapterClient] Error connecting to SOA Suite: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================================================");
    }

    private static void printResponse(ModificationProcessResponse response) {
        System.out.println("[RESPONSE] Modification ID: " + response.getModificationId());
        System.out.println("[RESPONSE] Status: " + response.getStatus());
        System.out.println("[RESPONSE] Decision: " + response.getModificationDecision());
        System.out.println("[RESPONSE] DTI Ratio: " + response.getPostModDtiRatio() + "%");
        System.out.println("[RESPONSE] LTV Ratio: " + response.getMarkToMarketLtvRatio() + "%");
        System.out.println("[RESPONSE] Risk Score: " + response.getRedefaultRiskScore());
        System.out.println("[RESPONSE] Monthly Payment: $" + response.getModifiedMonthlyPayment());
    }

    // Inner stub classes for compilation (would be generated from WSDL in practice)

    public interface ModificationAdapterProcessPort {
        ModificationProcessResponse processModification(ModificationProcessRequest request) throws ProcessingFaultException;
    }

    public static class ModificationProcessRequest {
        private String hardshipBorrowerName;
        private String borrowerTaxId;
        private double unpaidPrincipalBalance;
        private double hardshipMonthlyIncome;
        private double monthlyObligations;
        private double propertyValue;
        private byte[] documentData;

        public String getHardshipBorrowerName() { return hardshipBorrowerName; }
        public void setHardshipBorrowerName(String hardshipBorrowerName) { this.hardshipBorrowerName = hardshipBorrowerName; }
        public String getBorrowerTaxId() { return borrowerTaxId; }
        public void setBorrowerTaxId(String borrowerTaxId) { this.borrowerTaxId = borrowerTaxId; }
        public double getUnpaidPrincipalBalance() { return unpaidPrincipalBalance; }
        public void setUnpaidPrincipalBalance(double unpaidPrincipalBalance) { this.unpaidPrincipalBalance = unpaidPrincipalBalance; }
        public double getHardshipMonthlyIncome() { return hardshipMonthlyIncome; }
        public void setHardshipMonthlyIncome(double hardshipMonthlyIncome) { this.hardshipMonthlyIncome = hardshipMonthlyIncome; }
        public double getMonthlyObligations() { return monthlyObligations; }
        public void setMonthlyObligations(double monthlyObligations) { this.monthlyObligations = monthlyObligations; }
        public double getPropertyValue() { return propertyValue; }
        public void setPropertyValue(double propertyValue) { this.propertyValue = propertyValue; }
        public byte[] getDocumentData() { return documentData; }
        public void setDocumentData(byte[] documentData) { this.documentData = documentData; }
    }

    public static class ModificationProcessResponse {
        private String modificationId;
        private String status;
        private String modificationDecision;
        private String modificationNotes;
        private double postModDtiRatio;
        private double markToMarketLtvRatio;
        private int redefaultRiskScore;
        private double modifiedMonthlyPayment;
        private String documentClassification;

        public String getModificationId() { return modificationId; }
        public void setModificationId(String modificationId) { this.modificationId = modificationId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getModificationDecision() { return modificationDecision; }
        public void setModificationDecision(String modificationDecision) { this.modificationDecision = modificationDecision; }
        public String getModificationNotes() { return modificationNotes; }
        public void setModificationNotes(String modificationNotes) { this.modificationNotes = modificationNotes; }
        public double getPostModDtiRatio() { return postModDtiRatio; }
        public void setPostModDtiRatio(double postModDtiRatio) { this.postModDtiRatio = postModDtiRatio; }
        public double getMarkToMarketLtvRatio() { return markToMarketLtvRatio; }
        public void setMarkToMarketLtvRatio(double markToMarketLtvRatio) { this.markToMarketLtvRatio = markToMarketLtvRatio; }
        public int getRedefaultRiskScore() { return redefaultRiskScore; }
        public void setRedefaultRiskScore(int redefaultRiskScore) { this.redefaultRiskScore = redefaultRiskScore; }
        public double getModifiedMonthlyPayment() { return modifiedMonthlyPayment; }
        public void setModifiedMonthlyPayment(double modifiedMonthlyPayment) { this.modifiedMonthlyPayment = modifiedMonthlyPayment; }
        public String getDocumentClassification() { return documentClassification; }
        public void setDocumentClassification(String documentClassification) { this.documentClassification = documentClassification; }
    }

    public static class ProcessingFaultException extends Exception {
        private String errorCode;
        private String errorMessage;

        public ProcessingFaultException(String message) { super(message); }
        public String getErrorCode() { return errorCode; }
        public String getErrorMessage() { return errorMessage; }
    }
}
