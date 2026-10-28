package com.unified.mortgage.modification.ejb;

import java.io.Serializable;
import java.util.Date;

/**
 * Credit Report Data Transfer Object
 * Returned by IHardshipCreditReviewRemote.reviewHardshipCredit()
 */
public class HardshipCreditReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private String borrowerTaxId;
    private int score;
    private String status;
    private Date reportDate;
    private String bureauName;
    private int accountCount;
    private double totalDebt;
    private int latePayments;

    public HardshipCreditReport() {
    }

    public HardshipCreditReport(String borrowerTaxId, int score, String status) {
        this.borrowerTaxId = borrowerTaxId;
        this.score = score;
        this.status = status;
        this.reportDate = new Date();
        this.bureauName = "TestBureau";
    }

    // Getters and Setters
    public String getBorrowerTaxId() { return borrowerTaxId; }
    public void setBorrowerTaxId(String borrowerTaxId) { this.borrowerTaxId = borrowerTaxId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

    public String getBureauName() { return bureauName; }
    public void setBureauName(String bureauName) { this.bureauName = bureauName; }

    public int getAccountCount() { return accountCount; }
    public void setAccountCount(int accountCount) { this.accountCount = accountCount; }

    public double getTotalDebt() { return totalDebt; }
    public void setTotalDebt(double totalDebt) { this.totalDebt = totalDebt; }

    public int getLatePayments() { return latePayments; }
    public void setLatePayments(int latePayments) { this.latePayments = latePayments; }

    @Override
    public String toString() {
        return "HardshipCreditReport{borrowerTaxId='" + borrowerTaxId + "', score=" + score +
               ", status='" + status + "', bureau='" + bureauName + "'}";
    }
}
