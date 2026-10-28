package com.unified.mortgage.credit;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class CreditServiceApp {

    private static final int PORT = 8082;
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/credit-check", new CreditCheckHandler());
        server.setExecutor(null); // default executor
        System.out.println("[CreditService] Server started on port " + PORT + " at /api/credit-check");
        server.start();
    }

    static class CreditCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                CreditRequest request = gson.fromJson(reader, CreditRequest.class);
                System.out.println("[CreditService] Received credit check request for SSN: " + request.ssn);

                String cleanSsn = request.ssn == null ? "" : request.ssn.replace("-", "");

                // Simulate credit check offline system fault
                if ("000000000".equals(cleanSsn) || cleanSsn.startsWith("000")) {
                    System.out.println("[CreditService] Triggered CreditServiceDownFault simulation");
                    String errorResponse = gson.toJson(new ErrorResponse("System Fault: CreditServiceDownFault"));
                    byte[] responseBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(503, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                    return;
                }

                int creditScore = 720; // Default optimal credit score
                if (cleanSsn.startsWith("999")) {
                    creditScore = 450; // Low credit score trigger auto-rejection
                } else if (cleanSsn.startsWith("888")) {
                    creditScore = 620; // Mid credit score trigger manual review
                }

                CreditResponse response = new CreditResponse(creditScore);
                String jsonResponse = gson.toJson(response);
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

                System.out.println("[CreditService] Credit Score evaluated to: " + creditScore);
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

    static class CreditRequest {
        String ssn;
    }

    static class CreditResponse {
        int creditScore;
        public CreditResponse(int creditScore) {
            this.creditScore = creditScore;
        }
    }

    static class ErrorResponse {
        String message;
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
