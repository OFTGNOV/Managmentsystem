package billingAndPaymentModule;

import java.time.LocalDateTime;

/**
 * Payment class represents a payment transaction for an invoice.
 */
public class Payment {
    private String paymentId;
    private String invoiceId;
    private double amount;
    private String paymentMethod; // cash, card
    private LocalDateTime paymentDate;
    private PaymentStatus pStatus; // successful, pending, failed

    public Payment(String paymentId, String invoiceId, double amount, String paymentMethod) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.pStatus = pStatus;
    }

    public void markAsSuccessful() {
        this.transactionStatus = "successful";
    }

    public void markAsFailed() {
        this.transactionStatus = "failed";
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getInvoiceId() { return invoiceId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getTransactionStatus() { return transactionStatus; }

    @Override
    public String toString() {
        return String.format("Payment #%s - $%.2f [%s]", paymentId, amount, transactionStatus);
    }
}
