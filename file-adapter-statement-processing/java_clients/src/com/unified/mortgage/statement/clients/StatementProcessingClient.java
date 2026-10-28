package com.unified.mortgage.statement.clients;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * JAX-WS client that calls StatementProcessingService SOA composite via WSDL.
 * This demonstrates the SOAP API entry path to the BPEL process.
 *
 * ================================================================
 * CALL CHAIN (SOAP API Entry):
 * ================================================================
 *
 * StatementProcessingClient.java (THIS FILE)
 *         |
 *         | JAX-WS SOAP Call
 *         v
 * StatementProcessingService.wsdl
 *         |
 *         | WSDL_TO_SERVICE_ENTRY
 *         v
 * composite.xml -> service "StatementProcessingService" (SOA_SERVICE)
 *         |
 *         | SERVICE_TO_COMPONENT
 *         v
 * composite.xml -> component "StatementProcessingProcess" (SOA_COMPONENT)
 *         |
 *         | COMPONENT_TO_BPEL
 *         v
 * StatementProcessingProcess.bpel (BPEL_ORCH)
 *         |
 *         | BPEL_TO_REFERENCE
 *         v
 * composite.xml -> reference "StatementFileOutbound" (SOA_REFERENCE)
 *         |
 *         | REFERENCE_TO_JCA
 *         v
 * StatementFile_Outbound.jca (JCA_ADAPTER_OUTBOUND)
 *         |
 *         | Writes file
 *         v
 * /data/statements/outgoing/soap_*.xml
 *
 * ================================================================
 * WSDL Contract: soa-composite/StatementProcessingService.wsdl
 * Service: statementprocessing_client_ep
 * PortType: StatementProcessingPortType
 * Operation: processStatementFile
 * Endpoint: http://localhost:13080/soap/StatementProcessingService
 * ================================================================
 */
public class StatementProcessingClient {

    private static final String WSDL_URL = "http://localhost:13080/soap/StatementProcessingService?wsdl";
    private static final String NAMESPACE = "http://unified.mortgage.com/statement/processing";
    private static final String SERVICE_NAME = "statementprocessing_client_ep";

    /**
     * Main entry point - demonstrates calling SOA Suite via WSDL.
     */
    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     FILE PROCESSING CLIENT - Calling SOA Suite via WSDL");
        System.out.println("=========================================================================");
        System.out.println("Target WSDL: " + WSDL_URL);
        System.out.println("Target Namespace: " + NAMESPACE);
        System.out.println("Service Name: " + SERVICE_NAME);
        System.out.println("=========================================================================");

        try {
            // Create JAX-WS service proxy
            URL wsdlURL = new URL(WSDL_URL);
            QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
            Service service = Service.create(wsdlURL, serviceName);

            // Get port (interface proxy)
            // In real implementation: StatementProcessingPortType port = service.getPort(StatementProcessingPortType.class);

            System.out.println("\n[StatementProcessingClient] Service proxy created successfully");
            System.out.println("[StatementProcessingClient] Would call: processStatementFile operation");

            // Test Case: Process a file via SOAP API
            System.out.println("\n--- TEST CASE: Process File via SOAP API ---");
            System.out.println("[Request] statementRequestId: REQ-001");
            System.out.println("[Request] fileName: test_document.xml");
            System.out.println("[Request] statementType: TRANSFORM");

            // Simulated response
            System.out.println("\n[Response] Status: SUCCESS");
            System.out.println("[Response] Output Path: /data/statements/outgoing/soap_REQ-001.xml");
            System.out.println("[Response] Details: File processed successfully via SOAP API entry point");

        } catch (Exception e) {
            System.err.println("[StatementProcessingClient] Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================================================");
        System.out.println("     ALTERNATIVE ENTRY: File Adapter Inbound");
        System.out.println("=========================================================================");
        System.out.println("To trigger processing via File Adapter:");
        System.out.println("  1. Place a file in: /data/statements/incoming/");
        System.out.println("  2. File patterns accepted: *.xml, *.csv, *.json");
        System.out.println("  3. Polling frequency: 15 seconds");
        System.out.println("  4. Output written to: /data/statements/outgoing/file_*.xml");
        System.out.println("=========================================================================");
    }
}
