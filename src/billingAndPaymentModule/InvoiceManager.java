package billingAndPaymentModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InvoiceManager handles billing and payment tracking.
 */
public class InvoiceManager {
    private Map<String, Invoice> invoices; // key: invoice ID
    private Map<String, Payment> payments; // key: payment ID
    private static int invoiceCounter = 5000;
    private static int paymentCounter = 9000;

    /**
     * Constructor initializes the invoices and payments collections.
     */
    public InvoiceManager() {
        this.invoices = new HashMap<>();
        this.payments = new HashMap<>();
    }

    /**
     * Creates an invoice for a shipment.
     */
    public Invoice createInvoice(String shipmentId, String customerId, double shippingCost) {
        String invoiceId = "INV" + (++invoiceCounter);
        Invoice invoice = new Invoice(invoiceId, shipmentId, customerId, shippingCost);
        invoices.put(invoiceId, invoice);
        System.out.println("Invoice created: " + invoiceId + " for shipment " + shipmentId);
        return invoice;
    }

    /**
     * Retrieves an invoice by ID.
     */
    public Invoice getInvoice(String invoiceId) {
        if (!invoices.containsKey(invoiceId)) {
            throw new IllegalArgumentException("Invoice not found: " + invoiceId);
        }
        return invoices.get(invoiceId);
    }

    /**
     * Processes a payment for an invoice.
     */
    public Payment processPayment(String invoiceId, double amount, String paymentMethod) {
        Invoice invoice = getInvoice(invoiceId);

        // Validate payment method
        if (!paymentMethod.equalsIgnoreCase("cash") && !paymentMethod.equalsIgnoreCase("card")) {
            throw new IllegalArgumentException("Invalid payment method. Use 'cash' or 'card'");
        }

        // Validate amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        // Create payment
        String paymentId = "PAY" + (++paymentCounter);
        Payment payment = new Payment(paymentId, invoiceId, amount, paymentMethod);

        // Simulate payment processing
        if (paymentMethod.equalsIgnoreCase("card")) {
            // Card payment (assume successful for now)
            payment.markAsSuccessful();
        } else {
            // Cash payment
            payment.markAsSuccessful();
        }

        payments.put(paymentId, payment);

        // Update invoice payment status
        if (amount >= invoice.getTotalAmount()) {
            invoice.updatePaymentStatus(PaymentStatus.PAID);
            System.out.println("Invoice fully paid: " + invoiceId);
        } else if (amount > 0) {
            invoice.updatePaymentStatus(PaymentStatus.PARTIALLY_PAID);
            System.out.println("Partial payment received for invoice: " + invoiceId);
        }

        return payment;
    }

    /**
     * Retrieves all invoices for a customer.
     */
    public List<Invoice> getCustomerInvoices(String customerId) {
        List<Invoice> customerInvoices = new ArrayList<>();
        for (Invoice inv : invoices.values()) {
            if (inv.getCustomerId().equals(customerId)) {
                customerInvoices.add(inv);
            }
        }
        return customerInvoices;
    }

    /**
     * Retrieves all invoices.
     */
    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices.values());
    }
}