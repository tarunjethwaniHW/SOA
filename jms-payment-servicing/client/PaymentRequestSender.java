package com.unified.mortgage.payment.client;

import java.util.Hashtable;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * PaymentRequestSender is responsible for sending mortgage payment requests to the SOA composite.
 *
 * This class uses STANDARD JMS API methods for queue-based communication:
 * - InitialContext.lookup() - resolves queue JNDI
 * - Session.createProducer() - creates producer bound to queue
 * - MessageProducer.send() - sends message (PRODUCER SIGNAL for linking)
 *
 * Call chain: sendPaymentRequest() → ConsumePaymentRequest_jms.jca [JCA_ADAPTER_INBOUND]
 */
public class PaymentRequestSender {

    // WebLogic JNDI configuration
    private static final String INITIAL_CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";
    private static final String PROVIDER_URL = "t3://localhost:7001";
    private static final String CONNECTION_FACTORY_JNDI = "eis/wls/Queue";

    // Queue JNDI names - must match JCA adapter DestinationName
    private static final String REQUEST_QUEUE_JNDI = "jms/PaymentRequestQueue";
    private static final String RESPONSE_QUEUE_JNDI = "jms/PaymentResponseQueue";

    private Context jndiContext;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private Queue responseQueue;
    private boolean initialized = false;

    /**
     * Initialize JMS resources using standard JMS API.
     * Uses direct InitialContext creation without any wrapper classes.
     */
    public void initialize() throws NamingException, JMSException {
        // Standard JNDI InitialContext creation
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, PROVIDER_URL);

        jndiContext = new InitialContext(env);

        // Standard JNDI lookup for ConnectionFactory
        ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup(CONNECTION_FACTORY_JNDI);

        // Standard JNDI lookup for Queues - FIXED pattern for queue resolution
        Queue requestQueue = (Queue) jndiContext.lookup(REQUEST_QUEUE_JNDI);
        responseQueue = (Queue) jndiContext.lookup(RESPONSE_QUEUE_JNDI);

        // Create JMS Connection and Session
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Standard: Session.createProducer(Destination) - FIXED pattern
        producer = session.createProducer(requestQueue);

        connection.start();
        initialized = true;
        System.out.println("[Sender] Initialized - Producer bound to: " + REQUEST_QUEUE_JNDI);
    }

    /**
     * Send a mortgage payment request to the SOA composite via JMS queue.
     *
     * This method creates a JAVA_JMS_PRODUCE callee that links to:
     * ConsumePaymentRequest_jms.jca [JCA_ADAPTER_INBOUND] via jmsQueueJndi match.
     *
     * STANDARD JMS API: MessageProducer.send(Message) - PRODUCER SIGNAL
     *
     * @param accountNumber Mortgage servicing account number
     * @param borrowerName Borrower's name
     * @param paymentAmount Payment amount submitted
     * @param accountBalance Current funding account balance
     * @return The correlation ID for tracking the request
     */
    public String sendPaymentRequest(String accountNumber, String borrowerName, double paymentAmount, double accountBalance) throws JMSException, NamingException {
        if (!initialized) {
            initialize();
        }

        // Generate correlation ID for request-response matching
        String correlationId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Build payment request XML
        String requestXml = buildPaymentRequestXml(correlationId, accountNumber, borrowerName, paymentAmount, accountBalance);

        System.out.println("[Sender] Correlation ID: " + correlationId);
        System.out.println("[Sender] Request XML:\n" + requestXml);

        // Create JMS TextMessage
        TextMessage requestMessage = session.createTextMessage(requestXml);
        requestMessage.setJMSCorrelationID(correlationId);
        requestMessage.setJMSReplyTo(responseQueue);

        // STANDARD JMS API: MessageProducer.send(Message)
        // This is the FIXED PRODUCER SIGNAL that triggers linking to JCA_ADAPTER_INBOUND
        producer.send(requestMessage);

        System.out.println("[Sender] Message sent to " + REQUEST_QUEUE_JNDI);

        return correlationId;
    }

    /**
     * Get the JMS session for creating consumers.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Get the response queue destination.
     */
    public Queue getResponseQueue() {
        return responseQueue;
    }

    /**
     * Clean up JMS resources.
     */
    public void close() {
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            if (jndiContext != null) jndiContext.close();
        } catch (Exception e) {
            System.err.println("[Sender] Error during cleanup: " + e.getMessage());
        }
        initialized = false;
        System.out.println("[Sender] Resources closed.");
    }

    private String buildPaymentRequestXml(String correlationId, String accountNumber, String name, double amount, double balance) {
        return "<types:PaymentRequest xmlns:types=\"http://xmlns.oracle.com/pcbpel/adapter/jms/PaymentServicing/types\">\n" +
               "  <types:transactionId>" + correlationId + "</types:transactionId>\n" +
               "  <types:accountNumber>" + accountNumber + "</types:accountNumber>\n" +
               "  <types:borrowerName>" + name + "</types:borrowerName>\n" +
               "  <types:paymentAmount>" + amount + "</types:paymentAmount>\n" +
               "  <types:accountBalance>" + balance + "</types:accountBalance>\n" +
               "</types:PaymentRequest>";
    }
}
