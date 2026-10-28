package com.unified.mortgage.refinance.approval;

import com.unified.mortgage.refinance.approval.types.RefinanceApplicationRequest;
import com.unified.mortgage.refinance.approval.types.RefinanceApplicationResponse;

public class RefinanceApprovalServiceRunner {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     VERIFYING OSB SOA SUITE EX5 BPEL ORCHESTRATION IN JAVA");
        System.out.println("=========================================================================");

        RefinanceApprovalProcessPort orchestrator = new RefinanceApprovalProcessImpl();

        // SCENARIO 1: AUTO-APPROVAL
        System.out.println("\n--- RUNNING SCENARIO 1: AUTO-APPROVAL (Excellent Credit, Low LTV) ---");
        RefinanceApplicationRequest req1 = new RefinanceApplicationRequest(
            "Alice Smith", "111-222-3333", 250000.0, 12000.0, "123 Maple Dr, McLean, VA", 400000.0
        );
        try {
            RefinanceApplicationResponse res1 = orchestrator.initiateRefinance(req1);
            printResponse(res1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SCENARIO 2: SECURITY SANCTION LIST WATCHLIST BLOCK
        System.out.println("\n--- RUNNING SCENARIO 2: SECURITY WATCHLIST HIT (OFAC Block) ---");
        RefinanceApplicationRequest req2 = new RefinanceApplicationRequest(
            "Lord Voldemort", "111-222-3333", 250000.0, 12000.0, "123 Maple Dr, McLean, VA", 400000.0
        );
        try {
            orchestrator.initiateRefinance(req2);
        } catch (SanctionListFaultException e) {
            System.out.println("[RESULT] Caught Expected WebFault: " + e.getMessage());
            System.out.println("[RESULT] Fault Code: " + e.getFaultInfo().getErrorCode());
            System.out.println("[RESULT] Fault Message: " + e.getFaultInfo().getErrorMessage());
            System.out.println("[RESULT] Timestamp: " + e.getFaultInfo().getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SCENARIO 3: FRAUD WATCHLIST MATCH & SAGA COMPENSATION (Appraisal Refund)
        System.out.println("\n--- RUNNING SCENARIO 3: FRAUD WATCHLIST MATCH (Ends in 9999 -> Saga Compensation) ---");
        RefinanceApplicationRequest req3 = new RefinanceApplicationRequest(
            "Bob Miller", "111-222-9999", 250000.0, 12000.0, "123 Maple Dr, McLean, VA", 400000.0
        );
        try {
            RefinanceApplicationResponse res3 = orchestrator.initiateRefinance(req3);
            printResponse(res3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SCENARIO 4: CREDIT SERVICE DOWN SYSTEM FAULT
        System.out.println("\n--- RUNNING SCENARIO 4: SYSTEM FAULT (Credit Service Down) ---");
        RefinanceApplicationRequest req4 = new RefinanceApplicationRequest(
            "Charlie Brown", "000-00-0000", 250000.0, 12000.0, "123 Maple Dr, McLean, VA", 400000.0
        );
        try {
            RefinanceApplicationResponse res4 = orchestrator.initiateRefinance(req4);
            printResponse(res4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SCENARIO 5: AUTO REJECTION (Low Credit score)
        System.out.println("\n--- RUNNING SCENARIO 5: AUTO-REJECTION (Credit Score < 500) ---");
        RefinanceApplicationRequest req5 = new RefinanceApplicationRequest(
            "David Jones", "999-12-3456", 250000.0, 12000.0, "123 Maple Dr, McLean, VA", 400000.0
        );
        try {
            RefinanceApplicationResponse res5 = orchestrator.initiateRefinance(req5);
            printResponse(res5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SCENARIO 6: PENDING MANUAL UNDERWRITING REVIEW (Borderline case)
        System.out.println("\n--- RUNNING SCENARIO 6: MANUAL UNDERWRITING REVIEW (Credit Score 620) ---");
        RefinanceApplicationRequest req6 = new RefinanceApplicationRequest(
            "Emily Watson", "888-12-3456", 250000.0, 12000.0, "123 Maple Dr, McLean, VA", 400000.0
        );
        try {
            RefinanceApplicationResponse res6 = orchestrator.initiateRefinance(req6);
            printResponse(res6);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n=========================================================================");
    }

    private static void printResponse(RefinanceApplicationResponse res) {
        System.out.println("[RESULT] Loan ID: " + res.getLoanId());
        System.out.println("[RESULT] Process Status: " + res.getStatus());
        System.out.println("[RESULT] Rules Decision: " + res.getDecision());
        System.out.println("[RESULT] Decision Notes: " + res.getDecisionNotes());
    }
}
