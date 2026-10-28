package com.unified.mortgage.refinance.reappraisal;

import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRequest;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalResponse;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRefundRequest;
import com.unified.mortgage.refinance.reappraisal.types.ReappraisalRefundResponse;

public class ReappraisalServiceRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   VERIFYING STANDALONE VALUATION SERVICE");
        System.out.println("==================================================");

        ReappraisalProcessPort service = new ReappraisalProcessImpl();

        // 1. Test appraise
        System.out.println("\n--- Test Case 1: Appraise Property (Expected: APPRAISED, $450000.00) ---");
        ReappraisalResponse valRes = service.processReappraisal(new ReappraisalRequest("123 Maple Dr, McLean, VA", 450000.0));
        System.out.println("Status: " + valRes.getStatus());
        System.out.println("Appraised Value: $" + valRes.getAppraisedValue());

        // 2. Test refund
        System.out.println("\n--- Test Case 2: Refund Property Fee (Expected: REFUNDED) ---");
        ReappraisalRefundResponse refRes = service.processReappraisalRefund(new ReappraisalRefundRequest("123 Maple Dr, McLean, VA"));
        System.out.println("Status: " + refRes.getStatus());
        System.out.println("Message: " + refRes.getMessage());

        System.out.println("\n==================================================");
    }
}
