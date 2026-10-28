package com.unified.mortgage.refinance.creditverification;

import javax.jws.WebService;
import com.unified.mortgage.refinance.creditverification.types.CreditVerificationRequest;
import com.unified.mortgage.refinance.creditverification.types.CreditVerificationResponse;

@WebService(
    serviceName = "CreditVerificationProcessService",
    portName = "CreditVerificationProcessPort",
    targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/CreditVerificationProcess",
    endpointInterface = "com.unified.mortgage.refinance.creditverification.CreditVerificationProcessPort"
)
public class CreditVerificationProcessImpl implements CreditVerificationProcessPort {

    @Override
    public CreditVerificationResponse processCreditVerification(CreditVerificationRequest payload) {
        String ssn = payload.getSsn();
        int score = 720; // Default mock score from BPEL literal

        System.out.println("[CreditVerificationService] Checking credit for SSN: " + ssn);
        
        if (ssn != null) {
            // Remove dashes for uniform checking
            String cleanSsn = ssn.replace("-", "").trim();
            if (cleanSsn.startsWith("999")) {
                score = 450; // Simulate auto-reject (credit < 500)
                System.out.println("[CreditVerificationService] Low credit profile detected. Score: " + score);
            } else if (cleanSsn.startsWith("888")) {
                score = 620; // Simulate manual review (500 <= credit < 700)
                System.out.println("[CreditVerificationService] Mid-range credit profile detected. Score: " + score);
            } else if (cleanSsn.equals("000000000")) {
                System.out.println("[CreditVerificationService] Simulating Credit Service Down System Fault!");
                throw new RuntimeException("System Fault: CreditServiceDownFault");
            } else {
                System.out.println("[CreditVerificationService] Excellent credit profile detected. Score: " + score);
            }
        }
        
        return new CreditVerificationResponse(score);
    }
}
