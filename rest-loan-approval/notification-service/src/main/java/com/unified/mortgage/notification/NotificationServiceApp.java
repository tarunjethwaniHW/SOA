package com.unified.mortgage.notification;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class NotificationServiceApp {

    private static final int PORT = 8086;
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/notifications", new NotificationHandler());
        server.setExecutor(null);
        System.out.println("[NotificationService] Server started on port " + PORT + " at /api/notifications");
        server.start();
    }

    static class NotificationHandler implements HttpHandler {
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
                NotificationRequest request = gson.fromJson(reader, NotificationRequest.class);
                System.out.println("[NotificationService] Ingesting notification task for Loan ID: " + request.loanId);
                System.out.println("[NotificationService] Dispatching email notice to " + request.applicantName + " about decision: " + request.decision);

                String status = "SUCCESS";
                String message = "Notification email successfully dispatched to applicant: " 
                                 + request.applicantName + ". Subject: Loan Decision: " + request.decision;

                NotificationResponse response = new NotificationResponse(status, message);
                String jsonResponse = gson.toJson(response);
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

                System.out.println("[NotificationService] Email dispatched. Message: " + message);

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

    static class NotificationRequest {
        long loanId;
        String applicantName;
        String decision;
    }

    static class NotificationResponse {
        String status;
        String message;
        public NotificationResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
