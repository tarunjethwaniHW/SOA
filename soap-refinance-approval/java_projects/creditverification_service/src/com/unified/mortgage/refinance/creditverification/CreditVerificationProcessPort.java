package com.unified.mortgage.refinance.creditverification;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.unified.mortgage.refinance.creditverification.types.CreditVerificationRequest;
import com.unified.mortgage.refinance.creditverification.types.CreditVerificationResponse;

@WebService(name = "CreditVerificationProcess", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/CreditVerificationProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CreditVerificationProcessPort {

    @WebMethod(action = "processCreditVerification")
    @WebResult(name = "CreditVerificationResponse", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
    public CreditVerificationResponse processCreditVerification(
        @WebParam(name = "CreditVerificationRequest", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
        CreditVerificationRequest payload
    );
}
