package com.unified.mortgage.orchestrator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OrchestratorApp {

    private static final int PORT = 8081;
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/loans/apply", new LoanOrchestrationHandler());
        server.createContext("/api/loans/composite/info", new CompositeInfoHandler());
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(null);
        System.out.println("[Orchestrator] Service started on port " + PORT);
        System.out.println("  Routes: / (dashboard), /api/loans/apply, /api/loans/composite/info");
        server.start();
    }

    static class CompositeInfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            JsonObject info = new JsonObject();
            info.addProperty("compositeName", "LoanServiceSOAEngine");
            info.addProperty("revision", "1.0");
            info.addProperty("CreditProcessRef", "http://localhost:8082/api/credit-check");
            info.addProperty("ValuationProcessRef", "http://localhost:8083/api/appraisals");
            info.addProperty("UnderwritingProcessRef", "http://localhost:8084/api/underwrite");
            info.addProperty("DisbursementProcessRef", "http://localhost:8085/api/disbursements");
            info.addProperty("NotificationProcessRef", "http://localhost:8086/api/notifications");

            byte[] bytes = gson.toJson(info).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static class LoanOrchestrationHandler implements HttpHandler {
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
                LoanRequest loanReq = gson.fromJson(reader, LoanRequest.class);
                System.out.println("[Orchestrator] Ingested new loan application for: " + loanReq.applicantName);

                List<StepLog> stepLogs = new ArrayList<>();
                int stepCounter = 1;

                // --- 1. OFAC Sanction List Check (Pre-Orchestration Block) ---
                String nameLower = loanReq.applicantName == null ? "" : loanReq.applicantName.toLowerCase();
                String cleanSsn = loanReq.ssn == null ? "" : loanReq.ssn.replace("-", "");
                if (nameLower.contains("voldemort") || "000006666".equals(cleanSsn) || "000000000".equals(cleanSsn)) {
                    if (!"000000000".equals(cleanSsn)) {
                        System.out.println("[Orchestrator] Blocked by OFAC Sanction Watchlist: " + loanReq.applicantName);
                        JsonObject errorResponse = new JsonObject();
                        errorResponse.addProperty("errorCode", "OFAC-403");
                        errorResponse.addProperty("errorMessage", "Security Exit: Applicant matches Office of Foreign Assets Control sanction list.");
                        byte[] responseBytes = gson.toJson(errorResponse).getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(403, responseBytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(responseBytes);
                        }
                        return;
                    }
                }

                // --- 2. Credit check bureau call ---
                int creditScore = 0;
                long startCredit = System.currentTimeMillis();
                String creditInput = gson.toJson(new CreditRequest(loanReq.ssn));
                String creditOutput = "";
                String creditStatus = "SUCCESS";
                try {
                    HttpResponse<String> response = post("http://localhost:8082/api/credit-check", creditInput);
                    creditOutput = response.body();
                    if (response.statusCode() == 503 || response.statusCode() == 500) {
                        creditStatus = "FAULT";
                        throw new RuntimeException("Credit check offline");
                    }
                    JsonObject resObj = gson.fromJson(creditOutput, JsonObject.class);
                    creditScore = resObj.get("creditScore").getAsInt();
                } catch (Exception e) {
                    creditStatus = "FAULT";
                    creditOutput = "{\"fault\":\"CreditServiceDownFault\",\"message\":\"" + e.getMessage() + "\"}";
                    stepLogs.add(new StepLog(stepCounter++, "Credit check bureau call", creditStatus,
                            System.currentTimeMillis() - startCredit, "http://localhost:8082/api/credit-check",
                            creditInput, creditOutput));

                    // Return credit failure response to client
                    sendResponse(exchange, "REJECTED", "REJECTED",
                            "System fault: Credit check service is offline. BPEL caught CreditServiceDownFault.", stepLogs);
                    return;
                }
                stepLogs.add(new StepLog(stepCounter++, "Credit check bureau call", creditStatus,
                        System.currentTimeMillis() - startCredit, "http://localhost:8082/api/credit-check",
                        creditInput, creditOutput));

                // --- 3. Property Valuation call ---
                double appraisedValue = 0;
                long startValuation = System.currentTimeMillis();
                String valInput = gson.toJson(new ValuationRequest(loanReq.propertyAddress, loanReq.propertyValue));
                String valOutput = "";
                String valStatus = "SUCCESS";
                try {
                    HttpResponse<String> response = post("http://localhost:8083/api/appraisals", valInput);
                    valOutput = response.body();
                    JsonObject resObj = gson.fromJson(valOutput, JsonObject.class);
                    appraisedValue = resObj.get("appraisedValue").getAsDouble();
                } catch (Exception e) {
                    valStatus = "FAULT";
                    valOutput = "{\"fault\":\"ValuationFault\",\"message\":\"" + e.getMessage() + "\"}";
                }
                stepLogs.add(new StepLog(stepCounter++, "Property Valuation appraisal", valStatus,
                        System.currentTimeMillis() - startValuation, "http://localhost:8083/api/appraisals",
                        valInput, valOutput));

                // --- 4. Watchlist Fraud check (Saga compensation trigger) ---
                if (cleanSsn.endsWith("9999")) {
                    System.out.println("[Orchestrator] Fraud Alert watch-list match for SSN: " + loanReq.ssn);
                    stepLogs.add(new StepLog(stepCounter++, "Fraud Watchlist validation", "FAULT",
                            1, "InternalBPELWatchlist",
                            "{\"ssn\":\"" + loanReq.ssn + "\"}", "{\"fault\":\"FraudAlertFault\"}"));

                    // Saga compensation refund:
                    long startRefund = System.currentTimeMillis();
                    String refundInput = gson.toJson(new RefundRequest(loanReq.propertyAddress));
                    String refundOutput = "";
                    String refundStatus = "SUCCESS";
                    try {
                        HttpResponse<String> response = post("http://localhost:8083/api/appraisals/refund", refundInput);
                        refundOutput = response.body();
                    } catch (Exception e) {
                        refundStatus = "FAULT";
                        refundOutput = "{\"fault\":\"RefundFault\",\"message\":\"" + e.getMessage() + "\"}";
                    }
                    stepLogs.add(new StepLog(stepCounter++, "Valuation Refund (Saga Compensation)", refundStatus,
                            System.currentTimeMillis() - startRefund, "http://localhost:8083/api/appraisals/refund",
                            refundInput, refundOutput));

                    sendResponse(exchange, "REJECTED", "REJECTED",
                            "Saga compensated: Fraud watchlist match triggered valuation fee refund.", stepLogs);
                    return;
                }

                // --- 5. Underwriting Rules Decision ---
                String underwritingInput = gson.toJson(new UnderwritingRequest(creditScore, loanReq.loanAmount, loanReq.monthlyIncome, appraisedValue));
                long startUW = System.currentTimeMillis();
                String underwritingOutput = "";
                String uwStatus = "SUCCESS";
                String decision = "";
                String notes = "";
                try {
                    HttpResponse<String> response = post("http://localhost:8084/api/underwrite", underwritingInput);
                    underwritingOutput = response.body();
                    JsonObject resObj = gson.fromJson(underwritingOutput, JsonObject.class);
                    decision = resObj.get("decision").getAsString();
                    notes = resObj.get("notes").getAsString();
                } catch (Exception e) {
                    uwStatus = "FAULT";
                    underwritingOutput = "{\"fault\":\"UnderwritingFault\",\"message\":\"" + e.getMessage() + "\"}";
                }
                stepLogs.add(new StepLog(stepCounter++, "Underwriting Rules Decision", uwStatus,
                        System.currentTimeMillis() - startUW, "http://localhost:8084/api/underwrite",
                        underwritingInput, underwritingOutput));

                // --- 6. Routing Decision Flow ---
                String finalStatus = "PENDING_MANUAL_REVIEW";
                String finalDecision = "PENDING";
                if ("AUTO_APPROVED".equalsIgnoreCase(decision)) {
                    // Call Disbursement Service
                    long startDisburse = System.currentTimeMillis();
                    String disburseInput = gson.toJson(new DisbursementRequest(1003L, loanReq.loanAmount));
                    String disburseOutput = "";
                    String disburseStatus = "SUCCESS";
                    try {
                        HttpResponse<String> response = post("http://localhost:8085/api/disbursements", disburseInput);
                        disburseOutput = response.body();
                    } catch (Exception e) {
                        disburseStatus = "FAULT";
                        disburseOutput = "{\"fault\":\"DisbursementFault\",\"message\":\"" + e.getMessage() + "\"}";
                    }
                    stepLogs.add(new StepLog(stepCounter++, "Funds disbursement booking", disburseStatus,
                            System.currentTimeMillis() - startDisburse, "http://localhost:8085/api/disbursements",
                            disburseInput, disburseOutput));

                    // Call Notification Service
                    long startNotify = System.currentTimeMillis();
                    String notifyInput = gson.toJson(new NotificationRequest(1003L, loanReq.applicantName, "APPROVED"));
                    String notifyOutput = "";
                    String notifyStatus = "SUCCESS";
                    try {
                        HttpResponse<String> response = post("http://localhost:8086/api/notifications", notifyInput);
                        notifyOutput = response.body();
                    } catch (Exception e) {
                        notifyStatus = "FAULT";
                        notifyOutput = "{\"fault\":\"NotificationFault\",\"message\":\"" + e.getMessage() + "\"}";
                    }
                    stepLogs.add(new StepLog(stepCounter++, "Dispatch email notification", notifyStatus,
                            System.currentTimeMillis() - startNotify, "http://localhost:8086/api/notifications",
                            notifyInput, notifyOutput));

                    finalStatus = "DISBURSED";
                    finalDecision = "APPROVED";

                } else if ("AUTO_REJECTED".equalsIgnoreCase(decision)) {
                    // Call Notification Service (rejection notice)
                    long startNotify = System.currentTimeMillis();
                    String notifyInput = gson.toJson(new NotificationRequest(1003L, loanReq.applicantName, "REJECTED"));
                    String notifyOutput = "";
                    String notifyStatus = "SUCCESS";
                    try {
                        HttpResponse<String> response = post("http://localhost:8086/api/notifications", notifyInput);
                        notifyOutput = response.body();
                    } catch (Exception e) {
                        notifyStatus = "FAULT";
                        notifyOutput = "{\"fault\":\"NotificationFault\",\"message\":\"" + e.getMessage() + "\"}";
                    }
                    stepLogs.add(new StepLog(stepCounter++, "Dispatch rejection notification", notifyStatus,
                            System.currentTimeMillis() - startNotify, "http://localhost:8086/api/notifications",
                            notifyInput, notifyOutput));

                    finalStatus = "REJECTED";
                    finalDecision = "REJECTED";
                }

                sendResponse(exchange, finalStatus, finalDecision, notes, stepLogs);

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }

        private HttpResponse<String> post(String url, String jsonBody) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        private void sendResponse(HttpExchange exchange, String status, String decision, String notes, List<StepLog> stepLogs) throws IOException {
            OrchestrationResponse finalRes = new OrchestrationResponse(1003L, status, decision, notes, stepLogs);
            String json = gson.toJson(finalRes);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            // Load static resources from classpath
            String resourcePath = "/static" + path;
            try (InputStream is = OrchestratorApp.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    // Fallback to local files under src/main/resources/static just in case (for local dev run)
                    File fallbackFile = new File("src/main/resources/static" + path);
                    if (fallbackFile.exists()) {
                        serveFile(exchange, new FileInputStream(fallbackFile), path);
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                    return;
                }
                serveFile(exchange, is, path);
            }
        }

        private void serveFile(HttpExchange exchange, InputStream is, String path) throws IOException {
            byte[] fileBytes = is.readAllBytes();

            String contentType = "text/html";
            if (path.endsWith(".css")) {
                contentType = "text/css";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript";
            }

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }
    }

    // --- DTO payloads ---

    static class LoanRequest {
        String applicantName;
        String ssn;
        double monthlyIncome;
        double loanAmount;
        String propertyAddress;
        double propertyValue;
    }

    static class OrchestrationResponse {
        long loanId;
        String loanStatus;
        String loanDecision;
        String decisionRemarks;
        List<StepLog> stepLogs;

        public OrchestrationResponse(long loanId, String status, String decision, String remarks, List<StepLog> stepLogs) {
            this.loanId = loanId;
            this.loanStatus = status;
            this.loanDecision = decision;
            this.decisionRemarks = remarks;
            this.stepLogs = stepLogs;
        }
    }

    static class StepLog {
        int stepNumber;
        String serviceName;
        String status;
        long durationMs;
        String endpointUrl;
        String payloadInput;
        String payloadOutput;

        public StepLog(int stepNumber, String serviceName, String status, long durationMs, String endpointUrl, String payloadInput, String payloadOutput) {
            this.stepNumber = stepNumber;
            this.serviceName = serviceName;
            this.status = status;
            this.durationMs = durationMs;
            this.endpointUrl = endpointUrl;
            this.payloadInput = payloadInput;
            this.payloadOutput = payloadOutput;
        }
    }

    static class CreditRequest {
        String ssn;
        public CreditRequest(String ssn) { this.ssn = ssn; }
    }

    static class ValuationRequest {
        String propertyAddress;
        double estimatedValue;
        public ValuationRequest(String addr, double val) {
            this.propertyAddress = addr;
            this.estimatedValue = val;
        }
    }

    static class RefundRequest {
        String propertyAddress;
        public RefundRequest(String addr) { this.propertyAddress = addr; }
    }

    static class UnderwritingRequest {
        int creditScore;
        double loanAmount;
        double monthlyIncome;
        double propertyValue;

        public UnderwritingRequest(int cs, double la, double mi, double pv) {
            this.creditScore = cs;
            this.loanAmount = la;
            this.monthlyIncome = mi;
            this.propertyValue = pv;
        }
    }

    static class DisbursementRequest {
        long loanId;
        double loanAmount;
        public DisbursementRequest(long id, double la) {
            this.loanId = id;
            this.loanAmount = la;
        }
    }

    static class NotificationRequest {
        long loanId;
        String applicantName;
        String decision;

        public NotificationRequest(long id, String name, String decision) {
            this.loanId = id;
            this.applicantName = name;
            this.decision = decision;
        }
    }
}
