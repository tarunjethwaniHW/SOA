package com.unified.mortgage.refinance.refinancenotification;

import com.unified.mortgage.refinance.refinancenotification.types.RefinanceNotificationRequest;
import com.unified.mortgage.refinance.refinancenotification.types.RefinanceNotificationResponse;

public class RefinanceNotificationServiceRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   VERIFYING STANDALONE NOTIFICATION SERVICE");
        System.out.println("==================================================");

        RefinanceNotificationProcessPort service = new RefinanceNotificationProcessImpl();

        System.out.println("\n--- Test Case 1: Notify John Doe (APPROVED) ---");
        RefinanceNotificationResponse res = service.processRefinanceNotification(new RefinanceNotificationRequest(1003L, "John Doe", "APPROVED"));
        System.out.println("Status: " + res.getStatus());
        System.out.println("Message: " + res.getMessage());

        System.out.println("\n==================================================");
    }
}
