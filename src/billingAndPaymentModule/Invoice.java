package billingAndPaymentModule;

import shipmentModule.Shipment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Invoice {
    private String invoiceNum;
    private Shipment shipment;
    private int senderId;
    private int recipentId;
    private double totalAmount;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private InvoiceStatus status;
    private List<Payment> payments;
    private String notes;

    // Constructor for creating an invoice from a shipment
    public Invoice(Shipment shipment) {
        this.shipment = shipment;
        this.senderId = shipment.getSender().getID();
        this.recipentId = shipment.getRecipent().getID();
        this.totalAmount = shipment.getShippingCost();
        this.issueDate = LocalDateTime.now();
        this.dueDate = this.issueDate.plusDays(30); // Default 30 days due date
        this.status = InvoiceStatus.PENDING;
        this.payments = new ArrayList<>();
        this.invoiceNum = generateInvoiceNumber();
        this.notes = "Invoice for shipment: " + shipment.getTrackingNumber();
    }

    // Parameterized constructor with all fields (matching database schema)
    public Invoice(String invoiceNum, Shipment shipment, int senderID, int recipentId,
                   double totalAmount, LocalDateTime issueDate, LocalDateTime dueDate,
                   InvoiceStatus status, String notes) {
        this.invoiceNum = invoiceNum;
        this.shipment = shipment;
        this.senderId = senderId;
        this.recipentId = recipentId;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.payments = new ArrayList<>();  // Initialize empty list, will be populated when needed
        this.notes = notes;
    }

    // Copy constructor
    public Invoice(Invoice other) {
        this.invoiceNum = other.invoiceNum;
        this.shipment = other.shipment != null ? new Shipment(other.shipment) : null;
        this.senderId = other.senderId;
        this.recipentId = other.recipentId;
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


    // Getters and Setters
    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getRecipentId() {
        return recipentId;
    }

    public void setRecipentId(int recipentId) {
        this.recipentId = recipentId;
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
        return Double.compare(invoice.totalAmount, totalAmount) == 0 &&
               senderId == invoice.senderId &&
               recipentId == invoice.recipentId &&
               Objects.equals(invoiceNum, invoice.invoiceNum) &&
               Objects.equals(shipment, invoice.shipment) &&
               Objects.equals(issueDate, invoice.issueDate) &&
               Objects.equals(dueDate, invoice.dueDate) &&
               status == invoice.status &&
               Objects.equals(payments, invoice.payments) &&
               Objects.equals(notes, invoice.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceNum, shipment, senderId, recipentId, totalAmount,
                           issueDate, dueDate, status, payments, notes);
    }

}