package com.unified.mortgage.refinance.payoffdisbursement;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.unified.mortgage.refinance.payoffdisbursement.types.PayoffDisbursementRequest;
import com.unified.mortgage.refinance.payoffdisbursement.types.PayoffDisbursementResponse;

@WebService(name = "PayoffDisbursementProcess", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/PayoffDisbursementProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface PayoffDisbursementProcessPort {

    @WebMethod(action = "processPayoffDisbursement")
    @WebResult(name = "PayoffDisbursementResponse", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
    public PayoffDisbursementResponse processPayoffDisbursement(
        @WebParam(name = "PayoffDisbursementRequest", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
        PayoffDisbursementRequest payload
    );
}
