package com.unified.mortgage.heloc.client;

import com.unified.mortgage.heloc.client.service.HelocApprovalSoapClient;
import com.unified.mortgage.heloc.client.types.HelocApplicationRequest;
import com.unified.mortgage.heloc.client.types.HelocApplicationResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot Application demonstrating WebServiceTemplate SOAP client.
 *
 * This replaces the JAX-WS client from SOA_Suite_Final_Ex1-main.
 *
 * COMPARISON:
 *
 * Original (JAX-WS):
 *   HelocApprovalClient client = new HelocApprovalClient();
 *   HelocApplicationResponse response = client.initiateHeloc(request);
 *   // Uses: port.initiateHeloc(request) internally
 *
 * This project (WebServiceTemplate):
 *   HelocApprovalSoapClient client = ...
 *   HelocApplicationResponse response = client.initiateHeloc(request);
 *   // Uses: webServiceTemplate.marshalSendAndReceive(request) internally
 */
@SpringBootApplication
public class HelocApprovalClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelocApprovalClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(HelocApprovalSoapClient soapClient) {
        return args -> {
            System.out.println("=========================================================================");
            System.out.println("     HELOC APPROVAL CLIENT - Spring-WS WebServiceTemplate");
            System.out.println("=========================================================================");
            System.out.println("Pattern: WebServiceTemplate.marshalSendAndReceive()");
            System.out.println("Target: http://localhost:10081/soap/HelocApprovalProcess");
            System.out.println("WSDL: heloc-service-soa/HelocApprovalProcess.wsdl");
            System.out.println("=========================================================================");

            try {
                // Test Case 1: Standard HELOC application (should be AUTO_APPROVED)
                System.out.println("\n--- TEST CASE 1: Standard Application (Expected: AUTO_APPROVED) ---");
                HelocApplicationRequest request1 = new HelocApplicationRequest(
                    "John Doe",           // homeownerName
                    "123-45-6789",        // taxpayerId
                    250000.0,             // creditLineAmount
                    12000.0,              // householdIncome
                    "123 Main St, VA",    // collateralAddress
                    400000.0              // homeValue
                );

                HelocApplicationResponse response1 = soapClient.initiateHeloc(request1);
                printResponse(response1);

                // Test Case 2: Low HELOC credit score (should be AUTO_REJECTED)
                System.out.println("\n--- TEST CASE 2: Low HELOC credit Score (Expected: AUTO_REJECTED) ---");
                HelocApplicationRequest request2 = new HelocApplicationRequest(
                    "Jane Smith",
                    "999-12-3456",        // Taxpayer ID starting with 999 triggers low HELOC credit
                    250000.0,
                    12000.0,
                    "456 Oak Ave, MD",
                    400000.0
                );

                HelocApplicationResponse response2 = soapClient.initiateHeloc(request2);
                printResponse(response2);

            } catch (Exception e) {
                System.err.println("[ERROR] " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\n=========================================================================");
        };
    }

    private static void printResponse(HelocApplicationResponse response) {
        System.out.println("[RESPONSE] HELOC ID: " + response.getHelocId());
        System.out.println("[RESPONSE] Status: " + response.getStatus());
        System.out.println("[RESPONSE] Decision: " + response.getDecision());
        System.out.println("[RESPONSE] Notes: " + response.getDecisionRemarks());
    }
}
