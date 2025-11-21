package billingAndPaymentModule;

import shipmentModule.Shipment;
import userModule.Customer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Invoice {
    private String invoiceNum;
    private Shipment shipment;
    private Customer sender;
    private Customer recipient;
    private double totalAmount;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private InvoiceStatus status;
    private List<Payment> payments;
    private String notes;

    // Constructor for creating an invoice from a shipment
    public Invoice(Shipment shipment) {
        this.shipment = shipment;
        this.sender = shipment.getSender();
        this.recipient = shipment.getRecipent();
        this.totalAmount = shipment.getShippingCost();
        this.issueDate = LocalDateTime.now();
        this.dueDate = this.issueDate.plusDays(30); // Default 30 days due date
        this.status = InvoiceStatus.PENDING;
        this.payments = new ArrayList<>();
        this.invoiceNumber = generateInvoiceNumber();
        this.notes = "Invoice for shipment: " + shipment.getTrackingNumber();
    }

    // Parameterized constructor with all fields
    public Invoice(String invoiceNum, Shipment shipment, Customer sender, Customer recipient, 
                   double totalAmount, LocalDateTime issueDate, LocalDateTime dueDate, 
                   InvoiceStatus status, List<Payment> payments, String notes) {
        this.invoiceNum = invoiceNum;
        this.shipment = shipment;
        this.sender = sender;
        this.recipient = recipient;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.payments = payments != null ? new ArrayList<>(payments) : new ArrayList<>();
        this.notes = notes;
    }

    // Copy constructor
    public Invoice(Invoice other) {
        this.invoiceNum = other.invoiceNum;
        this.shipment = other.shipment != null ? new Shipment(other.shipment) : null;
        this.sender = other.sender != null ? new Customer(other.sender) : null;
        this.recipient = other.recipient != null ? new Customer(other.recipient) : null;
        this.totalAmount = other.totalAmount;
        this.issueDate = other.issueDate;
        this.dueDate = other.dueDate;
        this.status = other.status;
        this.payments = other.payments != null ? new ArrayList<>(other.payments) : new ArrayList<>();
        this.notes = other.notes;
    }

    // Generate invoice number
    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    // Add a payment to this invoice
    public void addPayment(Payment payment) {
        if (payment != null) {
            payments.add(payment);
            updateInvoiceStatus();
        }
    }

    // Remove a payment from this invoice
    public void removePayment(Payment payment) {
        if (payment != null && payments.remove(payment)) {
            updateInvoiceStatus();
        }
    }

    // Calculate total paid amount
    public double getTotalPaid() {
        return payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    // Calculate remaining balance
    public double getRemainingBalance() {
        return totalAmount - getTotalPaid();
    }

    // Update invoice status based on payments
    private void updateInvoiceStatus() {
        double totalPaid = getTotalPaid();
        if (totalPaid >= totalAmount) {
            status = InvoiceStatus.PAID;
        } else if (totalPaid > 0 && totalPaid < totalAmount) {
            status = InvoiceStatus.PARTIAL;
        } else {
            status = InvoiceStatus.PENDING;
        }

        // Check if invoice is overdue
        if (status != InvoiceStatus.PAID && LocalDateTime.now().isAfter(dueDate)) {
            if (status == InvoiceStatus.PENDING) {
                status = InvoiceStatus.OVERDUE;
            } else if (status == InvoiceStatus.PARTIAL) {
                status = InvoiceStatus.OVERDUE; // Could be a separate status if needed
            }
        }
    }

    // Process a payment for this invoice
    public boolean processPayment(double amount,PaymentMethod paymentMethod) {
        if (amount <= 0 || amount > getRemainingBalance()) {
            return false; // Invalid payment amount
        }

        Payment payment = new Payment(amount, paymentMethod);
        addPayment(payment);
        return true;
    }

    // Process cash payment (to be paid in store)
    public boolean processCashPayment() {
        Payment payment = new Payment(this.invoiceNum, PaymentMethod.CASH);
        addPayment(payment);
        return true;
    }

    // Getters and Setters
    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNumber) {
        this.invoiceNum = invoiceNumber;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Customer getSender() {
        return sender;
    }

    public void setSender(Customer sender) {
        this.sender = sender;
    }

    public Customer getRecipient() {
        return recipient;
    }

    public void setRecipient(Customer recipient) {
        this.recipient = recipient;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        updateInvoiceStatus();
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public List<Payment> getPayments() {
        return new ArrayList<>(payments);
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments != null ? new ArrayList<>(payments) : new ArrayList<>();
        updateInvoiceStatus();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return id == invoice.id && 
               Double.compare(invoice.totalAmount, totalAmount) == 0 && 
               Objects.equals(invoiceNumber, invoice.invoiceNumber) && 
               Objects.equals(shipment, invoice.shipment) && 
               Objects.equals(sender, invoice.sender) && 
               Objects.equals(recipient, invoice.recipient) && 
               Objects.equals(issueDate, invoice.issueDate) && 
               Objects.equals(dueDate, invoice.dueDate) && 
               status == invoice.status && 
               Objects.equals(payments, invoice.payments) && 
               Objects.equals(notes, invoice.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, invoiceNumber, shipment, sender, recipient, totalAmount, 
                           issueDate, dueDate, status, payments, notes);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", shipment=" + (shipment != null ? shipment.getTrackingNumber() : "null") +
                ", sender=" + (sender != null ? sender.getCustId() : "null") +
                ", recipient=" + (recipient != null ? recipient.getCustId() : "null") +
                ", totalAmount=" + totalAmount +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", numberOfPayments=" + (payments != null ? payments.size() : 0) +
                ", notes='" + notes + '\'' +
                '}';
    }
}