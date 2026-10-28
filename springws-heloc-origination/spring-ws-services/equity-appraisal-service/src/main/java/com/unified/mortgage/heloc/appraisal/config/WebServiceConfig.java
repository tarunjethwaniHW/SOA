package com.unified.mortgage.heloc.appraisal.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "EquityAppraisalProcess")
    public DefaultWsdl11Definition equityAppraisalProcessWsdl(XsdSchema equityAppraisalSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("EquityAppraisalProcessPort");
        definition.setServiceName("EquityAppraisalProcess");
        definition.setLocationUri("/ws/equity-appraisal");
        definition.setTargetNamespace("http://xmlns.oracle.com/HelocOrchestration");
        definition.setSchema(equityAppraisalSchema);
        return definition;
    }

    @Bean
    public XsdSchema equityAppraisalSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/HelocWorkflow.xsd"));
    }
}
