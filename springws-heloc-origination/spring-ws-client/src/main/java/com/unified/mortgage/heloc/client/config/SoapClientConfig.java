package com.unified.mortgage.heloc.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.client.support.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * Spring-WS Client Configuration for calling Oracle SOA Suite.
 *
 * This replaces the JAX-WS pattern:
 *   Service service = Service.create(wsdlURL, serviceName);
 *   HelocApprovalProcessPort port = service.getPort(HelocApprovalProcessPort.class);
 *   port.initiateHeloc(request);
 *
 * With WebServiceTemplate pattern:
 *   webServiceTemplate.marshalSendAndReceive(request);
 *
 * KEY DIFFERENCE:
 * - JAX-WS: Uses Port interface with operation-specific methods (linkable to WSDL)
 * - WebServiceTemplate: Uses generic marshalSendAndReceive (NOT directly linkable to WSDL)
 *
 * WSDL Reference:
 * - heloc-service-soa/HelocApprovalProcess.wsdl
 * - Operation: initiateHeloc
 * - Endpoint: http://localhost:10081/soap/HelocApprovalProcess
 */
@Configuration
public class SoapClientConfig {

    /**
     * WSDL file location for schema validation.
     * This WSDL defines the contract that requests/responses must follow.
     */
    private static final String WSDL_LOCATION = "wsdl/HelocApprovalProcess.wsdl";

    /**
     * SOA Suite endpoint URL.
     * This matches the <soap:address> in the WSDL.
     */
    private static final String ENDPOINT_URL = "http://localhost:10081/soap/HelocApprovalProcess";

    /**
     * JAXB Marshaller for converting Java objects to/from XML.
     *
     * The contextPath points to the package containing JAXB-annotated classes:
     * - HelocApplicationRequest (with @XmlRootElement)
     * - HelocApplicationResponse (with @XmlRootElement)
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        // Package containing JAXB-annotated request/response classes
        marshaller.setContextPath("com.unified.mortgage.heloc.client.types");

        return marshaller;
    }

    /**
     * SOAP Message Factory configured for SOAP 1.1.
     * Oracle SOA Suite typically uses SOAP 1.1.
     */
    @Bean
    public SaajSoapMessageFactory messageFactory() {
        SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
        factory.setSoapVersion(SoapVersion.SOAP_11);
        return factory;
    }

    /**
     * Payload Validating Interceptor.
     *
     * THIS IS WHERE WSDL CONTRACT VALIDATION HAPPENS:
     * - Validates outgoing requests against XSD schema in WSDL
     * - Validates incoming responses against XSD schema in WSDL
     * - Throws exception if XML doesn't match schema
     *
     * This makes WebServiceTemplate use WSDL for contract validation,
     * similar to how JAX-WS validates against the Port interface.
     */
    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        PayloadValidatingInterceptor interceptor = new PayloadValidatingInterceptor();

        // Load schema from WSDL file for validation
        interceptor.setSchema(new ClassPathResource(WSDL_LOCATION));

        // Validate outgoing requests (client → SOA Suite)
        interceptor.setValidateRequest(true);

        // Validate incoming responses (SOA Suite → client)
        interceptor.setValidateResponse(true);

        return interceptor;
    }

    /**
     * WebServiceTemplate - THE MAIN SOAP CLIENT.
     *
     * This is equivalent to the JAX-WS Service/Port pattern, but:
     * - Does NOT use Port interface (no operation-specific methods)
     * - Uses generic marshalSendAndReceive() for all operations
     * - Operation is determined by the XML element name (HelocApplicationRequest)
     *
     * For parser linking, the key difference is:
     * - JAX-WS: calleeClass="HelocApprovalProcessPort", calleeFunction="initiateHeloc"
     * - WebServiceTemplate: calleeClass="WebServiceTemplate", calleeFunction="marshalSendAndReceive"
     */
    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();

        // Set SOAP message factory
        template.setMessageFactory(messageFactory());

        // Set JAXB marshaller/unmarshaller
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());

        // Set default endpoint URI (from WSDL <soap:address>)
        template.setDefaultUri(ENDPOINT_URL);

        // Add validation interceptor for WSDL contract enforcement
        template.setInterceptors(new ClientInterceptor[] {
            payloadValidatingInterceptor()
        });

        return template;
    }
}
