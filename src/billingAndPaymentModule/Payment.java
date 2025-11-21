package billingAndPaymentModule;

import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
    private int paymentid;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String referenceNumber; // For card transactions or other reference

    // Constructor for card payments (where amount is specified)
    public Payment(int paymentid, double amount, PaymentMethod paymentMethod) {
        this.paymentid = paymentid;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.SUCCESS;
        this.referenceNumber = generateReferenceNumber();
    }

    // Constructor for cash payments (where payment is to be made in store)
    public Payment(int paymentid, PaymentMethod paymentMethod) {
        this.paymentid = paymentid;
        this.paymentMethod = paymentMethod;
        this.amount = 0.0; // Amount will be set when actual payment is made
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING; // Will change to SUCCESS when paid in store
        this.referenceNumber = generateReferenceNumber();
    }

    // Parameterized constructor with all fields
    public Payment(int paymentid, double amount, LocalDateTime paymentDate,
                   PaymentMethod paymentMethod, PaymentStatus status, String referenceNumber) {
        this.paymentid = paymentid;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.referenceNumber = referenceNumber;
    }

    // Copy constructor
    public Payment(Payment other) {
        this.paymentid = other.paymentid;
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

    // Getters and Setters
    public int getPaymentId() {
        return paymentid;
    }

    public void setPaymentId(int paymentid) {
        this.paymentid = paymentid;
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
        return paymentid == payment.paymentid && 
               Double.compare(payment.amount, amount) == 0 && 
               Objects.equals(paymentDate, payment.paymentDate) && 
               paymentMethod == payment.paymentMethod && 
               status == payment.status && 
               Objects.equals(referenceNumber, payment.referenceNumber);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentid=" + paymentid +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }
}