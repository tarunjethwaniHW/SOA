package com.unified.mortgage.refinance.reappraisal;

import javax.jws.WebService;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRequest;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalResponse;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRefundRequest;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRefundResponse;

@WebService(
    serviceName = "ReappraisalProcessService",
    portName = "ReappraisalProcessPort",
    targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/ReappraisalProcess",
    endpointInterface = "com.unified.mortgage.refinance.reappraisal.ReappraisalProcessPort"
)
public class ReappraisalProcessImpl implements ReappraisalProcessPort {

    @Override
    public ReappraisalResponse processReappraisal(ReappraisalRequest payload) {
        double estimatedValue = payload.getEstimatedValue();
        String address = payload.getPropertyAddress();
        
        System.out.println("[ReappraisalService] Running appraisal for property: " + address);
        System.out.println("[ReappraisalService] Estimated value: $" + estimatedValue);
        
        // BPEL assigns appraisedValue = estimatedValue
        double appraisedValue = estimatedValue;
        String status = "APPRAISED";
        
        System.out.println("[ReappraisalService] Appraisal completed. Value: $" + appraisedValue);
        return new ReappraisalResponse(appraisedValue, status);
    }

    @Override
    public ReappraisalRefundResponse processReappraisalRefund(ReappraisalRefundRequest payload) {
        String address = payload.getPropertyAddress();
        System.out.println("[ReappraisalService] Processing refund for property: " + address);
        
        String status = "REFUNDED";
        String message = "Appraisal fee refunded for address: " + address;
        
        System.out.println("[ReappraisalService] Saga Compensation Refund completed: " + message);
        return new ReappraisalRefundResponse(status, message);
    }
}
