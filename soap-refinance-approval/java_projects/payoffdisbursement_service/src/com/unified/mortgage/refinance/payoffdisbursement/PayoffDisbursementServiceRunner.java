package com.unified.mortgage.refinance.payoffdisbursement;

import com.unified.mortgage.refinance.payoffdisbursement.types.PayoffDisbursementRequest;
import com.unified.mortgage.refinance.payoffdisbursement.types.PayoffDisbursementResponse;

public class PayoffDisbursementServiceRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   VERIFYING STANDALONE DISBURSEMENT SERVICE");
        System.out.println("==================================================");

        PayoffDisbursementProcessPort service = new PayoffDisbursementProcessImpl();

        System.out.println("\n--- Test Case 1: Disburse Loan #1003 ($300000.00) ---");
        PayoffDisbursementResponse res = service.processPayoffDisbursement(new PayoffDisbursementRequest(1003L, 300000.0));
        System.out.println("Status: " + res.getStatus());
        System.out.println("Message: " + res.getMessage());

        System.out.println("\n==================================================");
    }
}
