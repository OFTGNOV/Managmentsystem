package billingAndPaymentModule;

import java.time.LocalDateTime;

/**
 * Invoice class represents a billing invoice for a shipment.
 * Tracks payment status, amount, and discounts/surcharges.
 */
public class Invoice {
    private String invoiceId;
    private String shipmentId;
    private String customerId;
    private double baseAmount;
    private double discount;
    private double surcharge;
    private double totalAmount;
    private PaymentStatus paymentStatus;
    private LocalDateTime invoiceDate;

    public Invoice(String invoiceId, String shipmentId, String customerId, double baseAmount) {
        this.invoiceId = invoiceId;
        this.shipmentId = shipmentId;
        this.customerId = customerId;
        this.baseAmount = baseAmount;
        this.discount = 0;
        this.surcharge = 0;
        this.paymentStatus = PaymentStatus.UNPAID;
        this.invoiceDate = LocalDateTime.now();
        calculateTotal();
    }

    public void applyDiscount(double discountAmount) {
        if (discountAmount > 0 && discountAmount <= baseAmount) {
            this.discount = discountAmount;
            calculateTotal();
        }
    }

    public void applySurcharge(double surchargeAmount) {
        if (surchargeAmount > 0) {
            this.surcharge = surchargeAmount;
            calculateTotal();
        }
    }

    private void calculateTotal() {
        this.totalAmount = baseAmount - discount + surcharge;
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    // Getters
    public String getInvoiceId() { return invoiceId; }
    public String getShipmentId() { return shipmentId; }
    public String getCustomerId() { return customerId; }
    public double getBaseAmount() { return baseAmount; }
    public double getDiscount() { return discount; }
    public double getSurcharge() { return surcharge; }
    public double getTotalAmount() { return totalAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getInvoiceDate() { return invoiceDate; }

    @Override
    public String toString() {
        return String.format("Invoice #%s - Total: $%.2f [%s]", invoiceId, totalAmount, paymentStatus);
    }
}
