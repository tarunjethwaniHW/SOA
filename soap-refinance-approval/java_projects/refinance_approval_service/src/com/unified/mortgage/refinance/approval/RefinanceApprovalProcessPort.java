package com.unified.mortgage.refinance.approval;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.unified.mortgage.refinance.approval.types.RefinanceApplicationRequest;
import com.unified.mortgage.refinance.approval.types.RefinanceApplicationResponse;

@WebService(name = "RefinanceApprovalProcess", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/RefinanceApprovalProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface RefinanceApprovalProcessPort {

    @WebMethod(action = "initiateRefinance")
    @WebResult(name = "RefinanceApplicationResponse", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
    public RefinanceApplicationResponse initiateRefinance(
        @WebParam(name = "RefinanceApplicationRequest", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
        RefinanceApplicationRequest payload
    ) throws SanctionListFaultException;
}
