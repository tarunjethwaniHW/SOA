package com.unified.mortgage.refinance.reappraisal;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRequest;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalResponse;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRefundRequest;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRefundResponse;

@WebService(name = "ReappraisalProcess", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/ReappraisalProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ReappraisalProcessPort {

    @WebMethod(action = "processReappraisal")
    @WebResult(name = "ReappraisalResponse", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
    public ReappraisalResponse processReappraisal(
        @WebParam(name = "ReappraisalRequest", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
        ReappraisalRequest payload
    );

    @WebMethod(action = "processReappraisalRefund")
    @WebResult(name = "ReappraisalRefundResponse", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
    public ReappraisalRefundResponse processReappraisalRefund(
        @WebParam(name = "ReappraisalRefundRequest", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types", partName = "payload")
        ReappraisalRefundRequest payload
    );
}
