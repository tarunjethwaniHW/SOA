package com.unified.mortgage.refinance.refinancenotification;

import javax.jws.WebService;
import com.unified.mortgage.refinance.refinancenotification.types.RefinanceNotificationRequest;
import com.unified.mortgage.refinance.refinancenotification.types.RefinanceNotificationResponse;

@WebService(
    serviceName = "RefinanceNotificationProcessService",
    portName = "RefinanceNotificationProcessPort",
    targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/RefinanceNotificationProcess",
    endpointInterface = "com.unified.mortgage.refinance.refinancenotification.RefinanceNotificationProcessPort"
)
public class RefinanceNotificationProcessImpl implements RefinanceNotificationProcessPort {

    @Override
    public RefinanceNotificationResponse processRefinanceNotification(RefinanceNotificationRequest payload) {
        long loanId = payload.getLoanId();
        String applicantName = payload.getApplicantName();
        String decision = payload.getDecision();
        
        System.out.println("[RefinanceNotificationService] Ingesting notification task for Loan ID: " + loanId);
        System.out.println("[RefinanceNotificationService] Dispatching email notice to " + applicantName + " about decision: " + decision);
        
        String status = "SUCCESS";
        String message = "Notification email successfully dispatched to applicant: " 
                         + applicantName + ". Subject: Loan Decision: " + decision;
        
        System.out.println("[RefinanceNotificationService] Email dispatched. Message: " + message);
        return new RefinanceNotificationResponse(status, message);
    }
}
