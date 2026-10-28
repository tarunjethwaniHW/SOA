package com.unified.mortgage.refinance.refinancenotification;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.unified.mortgage.refinance.refinancenotification.types.RefinanceNotificationRequest;
import com.unified.mortgage.refinance.refinancenotification.types.RefinanceNotificationResponse;

@WebService(name = "RefinanceNotificationProcess", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/RefinanceNotificationProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface RefinanceNotificationProcessPort {

    @WebMethod(action = "processRefinanceNotification")
    @WebResult(name = "RefinanceNotificationResponse", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
    public RefinanceNotificationResponse processRefinanceNotification(
        @WebParam(name = "RefinanceNotificationRequest", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
        RefinanceNotificationRequest payload
    );
}
