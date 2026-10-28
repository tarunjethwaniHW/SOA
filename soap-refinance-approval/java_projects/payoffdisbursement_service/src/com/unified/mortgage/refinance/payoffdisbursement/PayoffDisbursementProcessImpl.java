package com.unified.mortgage.refinance.payoffdisbursement;

import javax.jws.WebService;
import com.unified.mortgage.refinance.payoffdisbursement.types.PayoffDisbursementRequest;
import com.unified.mortgage.refinance.payoffdisbursement.types.PayoffDisbursementResponse;

@WebService(
    serviceName = "PayoffDisbursementProcessService",
    portName = "PayoffDisbursementProcessPort",
    targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/PayoffDisbursementProcess",
    endpointInterface = "com.unified.mortgage.refinance.payoffdisbursement.PayoffDisbursementProcessPort"
)
public class PayoffDisbursementProcessImpl implements PayoffDisbursementProcessPort {

    @Override
    public PayoffDisbursementResponse processPayoffDisbursement(PayoffDisbursementRequest payload) {
        long loanId = payload.getLoanId();
        double loanAmount = payload.getLoanAmount();
        
        System.out.println("[PayoffDisbursementService] Ingesting disbursement request for Loan ID: " + loanId);
        System.out.println("[PayoffDisbursementService] Processing account posting of amount: $" + loanAmount);
        
        String status = "SUCCESS";
        String message = "Funds posted to account. Amount disbursed: $" + loanAmount;
        
        System.out.println("[PayoffDisbursementService] Posting complete. Message: " + message);
        return new PayoffDisbursementResponse(status, message);
    }
}
