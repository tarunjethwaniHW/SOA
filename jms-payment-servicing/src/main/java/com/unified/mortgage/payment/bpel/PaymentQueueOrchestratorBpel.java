package com.unified.mortgage.payment.bpel;

import com.unified.mortgage.payment.util.JndiHelper;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

/**
 * PaymentQueueOrchestratorBpel simulates the WS-BPEL orchestration engine logic.
 * It coordinates the asynchronous integration logic by invoking the Payment verification queue
 * and waiting for the correlated verification response before preparing the final payment response.
 */
public class PaymentQueueOrchestratorBpel {

    private static final String VERIFICATION_REQUEST_QUEUE = "jms/VerificationRequestQueue";
    private static final String VERIFICATION_RESPONSE_QUEUE = "jms/VerificationResponseQueue";

    /**
     * Orchestrates the workflow: sends request to Payment Verification Service and waits for response
     * using the correlation ID selector, then outputs a consolidated reply XML.
     *
     * @param paymentRequestXml XML string from client
     * @param correlationId unique tracking ID
     * @return PaymentResponse XML string
     * @throws Exception if JMS operations fail
     */
    public String orchestrate(String paymentRequestXml, String correlationId) throws Exception {
        System.out.println("[BPEL] Starting orchestration workflow for ID: " + correlationId);

        // 1. Extract values from PaymentRequest XML
        String accountNumber = extractElementValue(paymentRequestXml, "accountNumber");
        String borrowerName = extractElementValue(paymentRequestXml, "borrowerName");
        String paymentAmountStr = extractElementValue(paymentRequestXml, "paymentAmount");
        double paymentAmount = paymentAmountStr != null ? Double.parseDouble(paymentAmountStr) : 0.0;
        String accountBalanceStr = extractElementValue(paymentRequestXml, "accountBalance");
        double accountBalance = accountBalanceStr != null ? Double.parseDouble(accountBalanceStr) : 0.0;

        System.out.println("[BPEL] Payment Request Extracted Data: Account=" + accountNumber + ", Borrower=" + borrowerName + ", Amount=" + paymentAmount);

        // 2. Prepare Verification Request XML
        String verificationRequestXml = buildVerificationRequestXml(correlationId, accountNumber, accountBalance);

        Context jndiContext = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;

        try {
            jndiContext = JndiHelper.getInitialContext();

            String cfJndi = JndiHelper.getConnectionFactoryJndi();
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup(cfJndi);

            Queue requestQueue = (Queue) jndiContext.lookup(VERIFICATION_REQUEST_QUEUE);
            Queue responseQueue = (Queue) jndiContext.lookup(VERIFICATION_RESPONSE_QUEUE);

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Start connection so we can receive
            connection.start();

            // 3. Publish Verification Request message
            System.out.println("[BPEL] Invoking Payment Verification Reference on " + VERIFICATION_REQUEST_QUEUE);
            producer = session.createProducer(requestQueue);
            TextMessage requestMsg = session.createTextMessage(verificationRequestXml);
            requestMsg.setJMSCorrelationID(correlationId);
            requestMsg.setJMSReplyTo(responseQueue);
            producer.send(requestMsg);

            // 4. Wait for Verification Response using Selector (BPEL Correlation Set simulation)
            String selector = "JMSCorrelationID = '" + correlationId + "'";
            System.out.println("[BPEL] Waiting for Verification Response on " + VERIFICATION_RESPONSE_QUEUE + " using selector: " + selector);
            consumer = session.createConsumer(responseQueue, selector);

            // Wait up to 15 seconds
            TextMessage responseMsg = (TextMessage) consumer.receive(15000);

            if (responseMsg != null) {
                String verificationResponseXml = responseMsg.getText();
                System.out.println("[BPEL] Received Verification Response:\n" + verificationResponseXml);

                String scoreStr = extractElementValue(verificationResponseXml, "verificationScore");
                int score = scoreStr != null ? Integer.parseInt(scoreStr) : 0;
                String risk = extractElementValue(verificationResponseXml, "fraudRiskRating");

                // 5. Evaluate and Assign final payment status (equivalent to BPEL switch/assign)
                String status = "REJECTED";
                double lateFeeRate = 0.0;
                double approvedAmount = 0.0;
                String remarks;

                if (score >= 500) {
                    status = "POSTED";
                    lateFeeRate = 0.045; // 4.5%
                    approvedAmount = paymentAmount;
                    remarks = "Verification score " + score + " (" + risk + ") is sufficient. Payment posted.";
                } else {
                    remarks = "Verification score " + score + " (" + risk + ") is too low. Payment rejected.";
                }

                return buildPaymentResponseXml(correlationId, borrowerName, status, lateFeeRate, approvedAmount, remarks);
            } else {
                System.err.println("[BPEL] Timeout waiting for Verification Response!");
                return buildPaymentResponseXml(correlationId, borrowerName, "REJECTED", 0.0, 0.0, "Payment verification service timeout.");
            }

        } finally {
            if (producer != null) { try { producer.close(); } catch (JMSException e) {} }
            if (consumer != null) { try { consumer.close(); } catch (JMSException e) {} }
            if (session != null) { try { session.close(); } catch (JMSException e) {} }
            if (connection != null) { try { connection.close(); } catch (JMSException e) {} }
            if (jndiContext != null) { try { jndiContext.close(); } catch (Exception e) {} }
        }
    }

    private static String extractElementValue(String xml, String elementName) {
        if (xml == null || elementName == null) return null;
        String openTagPattern = "<(?:[^>:]+:)?" + elementName + ">";
        String closeTagPattern = "</(?:[^>:]+:)?" + elementName + ">";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(openTagPattern + "(.*?)" + closeTagPattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String buildVerificationRequestXml(String correlationId, String accountNumber, double balance) {
        return "<types:VerificationRequest xmlns:types=\"http://xmlns.oracle.com/pcbpel/adapter/jms/PaymentServicing/types\">\n" +
               "  <types:transactionId>" + correlationId + "</types:transactionId>\n" +
               "  <types:accountNumber>" + accountNumber + "</types:accountNumber>\n" +
               "  <types:accountBalance>" + balance + "</types:accountBalance>\n" +
               "</types:VerificationRequest>";
    }

    private static String buildPaymentResponseXml(String correlationId, String name, String status, double rate, double amount, String remarks) {
        return "<types:PaymentResponse xmlns:types=\"http://xmlns.oracle.com/pcbpel/adapter/jms/PaymentServicing/types\">\n" +
               "  <types:transactionId>" + correlationId + "</types:transactionId>\n" +
               "  <types:borrowerName>" + name + "</types:borrowerName>\n" +
               "  <types:status>" + status + "</types:status>\n" +
               "  <types:lateFeeRate>" + rate + "</types:lateFeeRate>\n" +
               "  <types:approvedAmount>" + amount + "</types:approvedAmount>\n" +
               "  <types:remarks>" + remarks + "</types:remarks>\n" +
               "</types:PaymentResponse>";
    }
}
