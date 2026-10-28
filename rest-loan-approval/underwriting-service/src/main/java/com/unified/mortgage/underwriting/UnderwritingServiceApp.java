package com.unified.mortgage.underwriting;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class UnderwritingServiceApp {

    private static final int PORT = 8084;
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/underwrite", new UnderwriteHandler());
        server.setExecutor(null);
        System.out.println("[UnderwritingService] Server started on port " + PORT + " at /api/underwrite");
        server.start();
    }

    static class UnderwriteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                UnderwritingRequest request = gson.fromJson(reader, UnderwritingRequest.class);
                
                int creditScore = request.creditScore;
                double loanAmount = request.loanAmount;
                double monthlyIncome = request.monthlyIncome;
                double propertyValue = request.propertyValue;

                double ltv = propertyValue > 0 ? (loanAmount / propertyValue) : 0.0;
                double estimatedHousingPayment = loanAmount * 0.005;
                double dti = monthlyIncome > 0 ? (estimatedHousingPayment / monthlyIncome) : 0.0;

                System.out.println("[UnderwritingService] Evaluating loan details:");
                System.out.println("  Credit Score: " + creditScore);
                System.out.printf("  Loan Amount: $%.2f | Property Value: $%.2f (LTV: %.2f)%n", loanAmount, propertyValue, ltv);
                System.out.printf("  Monthly Income: $%.2f (Simulated DTI: %.2f)%n", monthlyIncome, dti);

                String decision;
                String notes;

                if (creditScore >= 700 && ltv < 0.8) {
                    decision = "AUTO_APPROVED";
                    notes = "Credit score (" + creditScore + ") and Loan-to-Value ratio (LTV: " + String.format("%.2f", ltv) + ") are optimal. Automatically approved by rules composite.";
                } else if (creditScore < 500) {
                    decision = "AUTO_REJECTED";
                    notes = "Credit score (" + creditScore + ") is below minimum threshold (500). Automatically rejected by rules composite.";
                } else {
                    decision = "MANUAL_REVIEW";
                    notes = "Refer to manual underwriter for review due to elevated risk parameters (Credit Score: " + creditScore + ", LTV: " + String.format("%.2f", ltv) + ").";
                }

                UnderwritingResponse response = new UnderwritingResponse(decision, notes, dti, ltv);
                String jsonResponse = gson.toJson(response);
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

                System.out.println("[UnderwritingService] Evaluation Decision: " + decision);
                System.out.println("[UnderwritingService] Notes: " + notes);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    static class UnderwritingRequest {
        int creditScore;
        double loanAmount;
        double monthlyIncome;
        double propertyValue;
    }

    static class UnderwritingResponse {
        String decision;
        String notes;
        double dti;
        double ltv;
        public UnderwritingResponse(String decision, String notes, double dti, double ltv) {
            this.decision = decision;
            this.notes = notes;
            this.dti = dti;
            this.ltv = ltv;
        }
    }
}
