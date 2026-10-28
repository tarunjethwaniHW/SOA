package com.unified.mortgage.valuation;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ValuationServiceApp {

    private static final int PORT = 8083;
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/appraisals", new AppraiseHandler());
        server.createContext("/api/appraisals/refund", new RefundHandler());
        server.setExecutor(null);
        System.out.println("[ValuationService] Server started on port " + PORT);
        System.out.println("  Routes: /api/appraisals , /api/appraisals/refund");
        server.start();
    }

    static class AppraiseHandler implements HttpHandler {
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
                ValuationRequest request = gson.fromJson(reader, ValuationRequest.class);
                System.out.println("[ValuationService] Running appraisal for property: " + request.propertyAddress);
                System.out.println("[ValuationService] Estimated value: $" + request.estimatedValue);

                double appraisedValue = request.estimatedValue;
                String status = "APPRAISED";

                ValuationResponse response = new ValuationResponse(appraisedValue, status);
                String jsonResponse = gson.toJson(response);
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

                System.out.println("[ValuationService] Appraisal completed. Appraised Value: $" + appraisedValue);
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

    static class RefundHandler implements HttpHandler {
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
                RefundRequest request = gson.fromJson(reader, RefundRequest.class);
                System.out.println("[ValuationService] Processing refund for property: " + request.propertyAddress);

                String status = "REFUNDED";
                String message = "Appraisal fee refunded for address: " + request.propertyAddress;

                RefundResponse response = new RefundResponse(status, message);
                String jsonResponse = gson.toJson(response);
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

                System.out.println("[ValuationService] Saga Compensation Refund completed: " + message);
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

    static class ValuationRequest {
        String propertyAddress;
        double estimatedValue;
    }

    static class ValuationResponse {
        double appraisedValue;
        String status;
        public ValuationResponse(double appraisedValue, String status) {
            this.appraisedValue = appraisedValue;
            this.status = status;
        }
    }

    static class RefundRequest {
        String propertyAddress;
    }

    static class RefundResponse {
        String status;
        String message;
        public RefundResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
