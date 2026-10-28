package com.unified.mortgage.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ClientRunner {

    private static final String ORCHESTRATOR_URL = "http://localhost:8081/api/loans/apply";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("  LEGACY JAVA REST SOA CLIENT RUNNER");
        System.out.println("  Demonstrating Java -> REST API -> BPEL Orchestrator call chain");
        System.out.println("=========================================================================");
        System.out.println();

        // Scenario 1: Auto-Approval
        runScenario(1, "Alice Johnson", "123-45-6789", 12000, 250000, "456 Elm St, Reston, VA", 400000,
                "Auto-Approval (Expected: APPROVED / DISBURSED)");

        // Scenario 2: OFAC Watchlist Block
        runScenario(2, "Lord Voldemort", "000-00-6666", 15000, 300000, "789 Dark Forest Rd", 500000,
                "OFAC Sanction Watchlist Block (Expected: HTTP 403 Security Exit)");

        // Scenario 3: Watchlist Fraud and Saga Compensation
        runScenario(3, "Bob Miller", "123-45-9999", 8000, 150000, "123 Maple Ave, Arlington, VA", 200000,
                "Watchlist Fraud & Saga Compensation (Expected: REJECTED with refund step log)");

        // Scenario 4: Credit Service Down
        runScenario(4, "Charlie Brown", "000-00-0000", 10000, 200000, "555 Pine Rd, Reston, VA", 300000,
                "Credit Bureau Offline System Fault (Expected: CreditServiceDownFault Caught)");

        // Scenario 5: Low Credit Rejection
        runScenario(5, "David Jones", "999-11-2222", 9000, 300000, "888 Oak Lane, McLean, VA", 350000,
                "Low Credit Auto-Rejection (Expected: REJECTED via rules check)");

        // Scenario 6: Manual Underwriting Review
        runScenario(6, "Emily Watson", "888-11-2222", 9000, 300000, "999 Cedar Rd, Fairfax, VA", 350000,
                "Borderline Ratios Manual Review Routing (Expected: PENDING)");

        System.out.println("=========================================================================");
        System.out.println("  ALL REST SOA INTEGRATION SCENARIOS COMPLETED");
        System.out.println("=========================================================================");
    }

    private static void runScenario(int num, String name, String ssn, double income, double loan, String addr, double val, String desc) {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(" SCENARIO " + num + ": " + desc);
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Input applicantName: " + name);
        System.out.println("Input ssn: " + ssn);
        System.out.println("Input loanAmount: $" + loan);
        System.out.println("Input propertyAddress: " + addr);
        System.out.println();

        JsonObject payload = new JsonObject();
        payload.addProperty("applicantName", name);
        payload.addProperty("ssn", ssn);
        payload.addProperty("monthlyIncome", income);
        payload.addProperty("loanAmount", loan);
        payload.addProperty("propertyAddress", addr);
        payload.addProperty("propertyValue", val);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ORCHESTRATOR_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("Response Payload:");
            
            try {
                // Pretty-print response JSON
                Object json = gson.fromJson(response.body(), Object.class);
                System.out.println(gson.toJson(json));
            } catch (Exception parseEx) {
                // If not JSON, print raw
                System.out.println(response.body());
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to execute integration request: " + e.getMessage());
        }
        System.out.println();
    }
}
