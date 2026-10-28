package com.unified.mortgage.refinance.refinanceunderwriting;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.unified.mortgage.refinance.refinanceunderwriting.types.RefinanceUnderwritingRequest;
import com.unified.mortgage.refinance.refinanceunderwriting.types.RefinanceUnderwritingResponse;

@WebService(name = "RefinanceUnderwritingProcess", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/RefinanceUnderwritingProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface RefinanceUnderwritingProcessPort {

    @WebMethod(action = "processRefinanceUnderwriting")
    @WebResult(name = "RefinanceUnderwritingResponse", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
    public RefinanceUnderwritingResponse processRefinanceUnderwriting(
        @WebParam(name = "RefinanceUnderwritingRequest", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
        RefinanceUnderwritingRequest payload
    );
}
