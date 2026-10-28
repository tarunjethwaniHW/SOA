package com.unified.mortgage.refinance.refinanceunderwriting;

import javax.jws.WebService;
import com.unified.mortgage.refinance.refinanceunderwriting.types.RefinanceUnderwritingRequest;
import com.unified.mortgage.refinance.refinanceunderwriting.types.RefinanceUnderwritingResponse;

@WebService(
    serviceName = "RefinanceUnderwritingProcessService",
    portName = "RefinanceUnderwritingProcessPort",
    targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/RefinanceUnderwritingProcess",
    endpointInterface = "com.unified.mortgage.refinance.refinanceunderwriting.RefinanceUnderwritingProcessPort"
)
public class RefinanceUnderwritingProcessImpl implements RefinanceUnderwritingProcessPort {

    @Override
    public RefinanceUnderwritingResponse processRefinanceUnderwriting(RefinanceUnderwritingRequest payload) {
        int creditScore = payload.getCreditScore();
        double loanAmount = payload.getLoanAmount();
        double monthlyIncome = payload.getMonthlyIncome();
        double propertyValue = payload.getPropertyValue();

        double ltv = propertyValue > 0 ? (loanAmount / propertyValue) : 0.0;
        // Estimate monthly housing payment as 0.5% of loan amount for simulated DTI
        double estimatedHousingPayment = loanAmount * 0.005;
        double dti = monthlyIncome > 0 ? (estimatedHousingPayment / monthlyIncome) : 0.0;

        System.out.println("[RefinanceUnderwritingService] Evaluating loan application details:");
        System.out.println("  Credit Score: " + creditScore);
        System.out.printf("  Loan Amount: $%.2f | Property Value: $%.2f (LTV: %.2f)%n", loanAmount, propertyValue, ltv);
        System.out.printf("  Monthly Income: $%.2f (Simulated DTI: %.2f)%n", monthlyIncome, dti);

        String decision;
        String notes;

        // Auto Approved: creditScore >= 700 and LTV < 0.8
        if (creditScore >= 700 && ltv < 0.8) {
            decision = "AUTO_APPROVED";
            notes = "Credit score and Loan-to-Value ratios are optimal. Automatically approved by rules composite.";
        }
        // Auto Rejected: creditScore < 500
        else if (creditScore < 500) {
            decision = "AUTO_REJECTED";
            notes = "Credit score is below minimum threshold (500). Automatically rejected by rules composite.";
        }
        // Borderline -> Manual review queue
        else {
            decision = "MANUAL_REVIEW";
            notes = "Refer to manual underwriter for review due to elevated risk parameters.";
        }

        System.out.println("[RefinanceUnderwritingService] Evaluation Decision: " + decision);
        System.out.println("[RefinanceUnderwritingService] Notes: " + notes);
        
        return new RefinanceUnderwritingResponse(decision, notes, dti, ltv);
    }
}
