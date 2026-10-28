package com.unified.mortgage.disbursement;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class DisbursementServiceApp {

    private static final int PORT = 8085;
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/disbursements", new DisbursementHandler());
        server.setExecutor(null);
        System.out.println("[DisbursementService] Server started on port " + PORT + " at /api/disbursements");
        server.start();
    }

    static class DisbursementHandler implements HttpHandler {
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
                DisbursementRequest request = gson.fromJson(reader, DisbursementRequest.class);
                System.out.println("[DisbursementService] Ingesting disbursement request for Loan ID: " + request.loanId);
                System.out.println("[DisbursementService] Processing account posting of amount: $" + request.loanAmount);

                String status = "SUCCESS";
                String message = "Funds posted to account. Amount disbursed: $" + request.loanAmount;

                DisbursementResponse response = new DisbursementResponse(status, message);
                String jsonResponse = gson.toJson(response);
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

                System.out.println("[DisbursementService] Posting complete. Message: " + message);

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

    static class DisbursementRequest {
        long loanId;
        double loanAmount;
    }

    static class DisbursementResponse {
        String status;
        String message;
        public DisbursementResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
