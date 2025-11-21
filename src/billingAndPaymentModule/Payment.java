package billingAndPaymentModule;

import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
    private int paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String referenceNumber; // For card transactions or other reference

    // Full constructor used by DAOs and mappers
    public Payment(int paymentId, double amount, LocalDateTime paymentDate,
                   PaymentMethod paymentMethod, PaymentStatus status, String referenceNumber) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.referenceNumber = referenceNumber;
    }

    // Copy constructor
    public Payment(Payment other) {
        this.paymentId = other.paymentId;
        this.amount = other.amount;
        this.paymentDate = other.paymentDate;
        this.paymentMethod = other.paymentMethod;
        this.status = other.status;
        this.referenceNumber = other.referenceNumber;
    }

    // Generate reference number for the payment
    private String generateReferenceNumber() {
        return "REF-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    // Select payment method (card or cash). When cash is selected, amount is left 0 and
    // status set to PENDING to indicate payment will be completed in-store.
    public void selectPaymentMethod(PaymentMethod method) {
        this.paymentMethod = method;
        this.paymentDate = LocalDateTime.now();
        if (method == PaymentMethod.CASH) {
            this.amount = 0.0;
            this.status = PaymentStatus.PENDING;
        } else {
            // For card default to pending until processPayment is called with an amount
            if (this.amount > 0) {
                this.status = PaymentStatus.SUCCESS;
            } else {
                this.status = PaymentStatus.PENDING;
            }
        }
        this.referenceNumber = generateReferenceNumber();
    }

    // Process a payment for the invoice
    public boolean processPayment(double amount, PaymentMethod paymentMethod) {
        if (amount <= 0) {
            return false; // Invalid payment amount
        }

        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.SUCCESS;
        this.paymentDate = LocalDateTime.now();
        return true;
    }

    // Process cash payment (to be paid in store)
    public boolean processCashPayment() {
        selectPaymentMethod(PaymentMethod.CASH);
        return true;
    }

    // Complete a cash payment (change status to SUCCESS when paid in store)
    public boolean completeCashPayment(double amount) {
        if (paymentMethod == PaymentMethod.CASH && status == PaymentStatus.PENDING && amount > 0) {
            this.amount = amount;
            this.status = PaymentStatus.SUCCESS;
            return true;
        }
        return false;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    // Override equals method to compare Payment objects.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return paymentId == payment.paymentId &&
               Double.compare(payment.amount, amount) == 0 &&
               Objects.equals(paymentDate, payment.paymentDate) &&
               paymentMethod == payment.paymentMethod &&
               status == payment.status &&
               Objects.equals(referenceNumber, payment.referenceNumber);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }
}