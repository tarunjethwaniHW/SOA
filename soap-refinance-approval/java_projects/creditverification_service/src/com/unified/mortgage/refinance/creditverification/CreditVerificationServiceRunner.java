package com.unified.mortgage.refinance.creditverification;

import com.unified.mortgage.refinance.creditverification.types.CreditVerificationRequest;
import com.unified.mortgage.refinance.creditverification.types.CreditVerificationResponse;

public class CreditVerificationServiceRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   VERIFYING STANDALONE CREDIT SERVICE");
        System.out.println("==================================================");

        CreditVerificationProcessPort service = new CreditVerificationProcessImpl();

        // Scenario 1: Standard / Excellent Credit score
        System.out.println("\n--- Test Case 1: Standard SSN (Expected: 720) ---");
        CreditVerificationResponse res1 = service.processCreditVerification(new CreditVerificationRequest("123-45-6789"));
        System.out.println("Result Score: " + res1.getCreditScore());

        // Scenario 2: Bad Credit score
        System.out.println("\n--- Test Case 2: SSN starting with 999 (Expected: 450) ---");
        CreditVerificationResponse res2 = service.processCreditVerification(new CreditVerificationRequest("999-12-3456"));
        System.out.println("Result Score: " + res2.getCreditScore());

        // Scenario 3: Mid Credit score
        System.out.println("\n--- Test Case 3: SSN starting with 888 (Expected: 620) ---");
        CreditVerificationResponse res3 = service.processCreditVerification(new CreditVerificationRequest("888-12-3456"));
        System.out.println("Result Score: " + res3.getCreditScore());

        System.out.println("\n==================================================");
    }
}
