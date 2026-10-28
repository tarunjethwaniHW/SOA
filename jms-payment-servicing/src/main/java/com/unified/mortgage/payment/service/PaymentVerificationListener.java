package com.unified.mortgage.payment.service;

import com.unified.mortgage.payment.util.JndiHelper;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

/**
 * PaymentVerificationListener simulates a downstream Payment Verification microservice.
 * It consumes request XML payloads from jms/VerificationRequestQueue, evaluates the servicing account number,
 * calculates a mock verification score/fraud risk, and returns a VerificationResponse to the reply queue.
 * The response copies the original request's JMSCorrelationID to align with the BPEL Correlation Set.
 */
public class PaymentVerificationListener {

    private static final String REQUEST_QUEUE_JNDI = "jms/VerificationRequestQueue";
    private static final String RESPONSE_QUEUE_JNDI = "jms/VerificationResponseQueue";
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("=== Starting Payment Verification JMS Listener ===");

        Context jndiContext = null;
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        MessageProducer producer = null;

        try {
            // 1. Obtain JNDI InitialContext
            jndiContext = JndiHelper.getInitialContext();

            // 2. Lookup ConnectionFactory
            String cfJndi = JndiHelper.getConnectionFactoryJndi();
            System.out.println("[Service] Looking up ConnectionFactory: " + cfJndi);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup(cfJndi);

            // 3. Lookup Queues
            System.out.println("[Service] Looking up request queue: " + REQUEST_QUEUE_JNDI);
            Queue requestQueue = (Queue) jndiContext.lookup(REQUEST_QUEUE_JNDI);

            System.out.println("[Service] Looking up fallback response queue: " + RESPONSE_QUEUE_JNDI);
            Queue fallbackResponseQueue = (Queue) jndiContext.lookup(RESPONSE_QUEUE_JNDI);

            // 4. Create JMS Connection & Session
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 5. Create Consumer on request queue and anonymous Producer
            consumer = session.createConsumer(requestQueue);
            producer = session.createProducer(null); // Destination will be specified per-message sent

            // Start connection to receive messages
            connection.start();
            System.out.println("[Service] Successfully listening on: " + REQUEST_QUEUE_JNDI);
            System.out.println("Press Ctrl+C to terminate the service.");

            // Register JVM shutdown hook to close loop gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[Service] Shutdown signal received. Exiting...");
                running = false;
            }));

            // 6. Polling loop to consume messages
            while (running) {
                Message message = consumer.receive(1000); // 1-second timeout to allow graceful exit
                if (message == null) {
                    continue;
                }

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String requestXml = textMessage.getText();
                    String correlationId = textMessage.getJMSCorrelationID();

                    System.out.println("\n[Service] Received Request with Correlation ID: " + correlationId);
                    System.out.println("[Service] XML Payload:\n" + requestXml);

                    // Parse request (account number) using simple regex/string extraction helper
                    String accountNumber = extractElementValue(requestXml, "accountNumber");
                    System.out.println("[Service] Extracted Account Number: " + accountNumber);

                    // Business Logic: account numbers starting with 999 are mock high fraud risk, others are low
                    int verificationScore = 720;
                    String fraudRiskRating = "LOW";
                    if (accountNumber != null && accountNumber.startsWith("999")) {
                        verificationScore = 450;
                        fraudRiskRating = "HIGH";
                    }

                    // Build VerificationResponse XML
                    String responseXml = buildVerificationResponseXml(correlationId, verificationScore, fraudRiskRating);

                    // Target reply queue is either the JMSReplyTo header (standard practice) or fallback queue
                    Destination replyDestination = textMessage.getJMSReplyTo();
                    if (replyDestination == null) {
                        replyDestination = fallbackResponseQueue;
                    }

                    System.out.println("[Service] Publishing VerificationResponse (Score=" + verificationScore + ", Risk=" + fraudRiskRating + ") to: " + replyDestination);

                    // Create and publish response message
                    TextMessage replyMessage = session.createTextMessage(responseXml);
                    replyMessage.setJMSCorrelationID(correlationId);
                    producer.send(replyDestination, replyMessage);

                    System.out.println("[Service] Response sent successfully.");
                } else {
                    System.out.println("[Service] Received non-TextMessage: " + message.getClass().getName() + ", ignoring.");
                }
            }

        } catch (Exception e) {
            System.err.println("[Service] Exception in Payment Verification Listener: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources cleanly
            if (producer != null) {
                try { producer.close(); } catch (JMSException e) { /* ignored */ }
            }
            if (consumer != null) {
                try { consumer.close(); } catch (JMSException e) { /* ignored */ }
            }
            if (session != null) {
                try { session.close(); } catch (JMSException e) { /* ignored */ }
            }
            if (connection != null) {
                try { connection.close(); } catch (JMSException e) { /* ignored */ }
            }
            if (jndiContext != null) {
                try { jndiContext.close(); } catch (Exception e) { /* ignored */ }
            }
            System.out.println("[Service] Payment Verification shutdown finished.");
        }
    }

    private static String extractElementValue(String xml, String elementName) {
        if (xml == null || elementName == null) {
            return null;
        }
        // Matches <elementName>value</elementName> or with prefix <ns:elementName>value</ns:elementName>
        String openTagPattern = "<(?:[^>:]+:)?" + elementName + ">";
        String closeTagPattern = "</(?:[^>:]+:)?" + elementName + ">";

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                openTagPattern + "(.*?)" + closeTagPattern,
                java.util.regex.Pattern.DOTALL
        );
        java.util.regex.Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String buildVerificationResponseXml(String correlationId, int score, String risk) {
        return "<types:VerificationResponse xmlns:types=\"http://xmlns.oracle.com/pcbpel/adapter/jms/PaymentServicing/types\">\n" +
               "  <types:transactionId>" + correlationId + "</types:transactionId>\n" +
               "  <types:verificationScore>" + score + "</types:verificationScore>\n" +
               "  <types:fraudRiskRating>" + risk + "</types:fraudRiskRating>\n" +
               "</types:VerificationResponse>";
    }
}
