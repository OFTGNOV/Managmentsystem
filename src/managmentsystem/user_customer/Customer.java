package managmentsystem.user_customer;

import java.util.List;
import managmentsystem.shipment.Shipment;
import managmentsystem.billing_payment.Invoice;
import managmentsystem.billing_payment.InvoiceManager;
import managmentsystem.shipment.ShipmentManager;

/**
 * Customer class represents a customer user in the SmartShip system.
 * Customers can create shipment requests and track packages.
 */
public class Customer extends User {
    private ShipmentManager shipmentManager;
    private InvoiceManager invoiceManager;

    public Customer(String id, String name, String email, String password, 
                    ShipmentManager shipmentManager, InvoiceManager invoiceManager) {
        super(id, name, email, password);
        this.shipmentManager = shipmentManager;
        this.invoiceManager = invoiceManager;
    }

    // Creates a new shipment request.S
    public void createShipmentRequest(String senderAddress, String recipientName, String recipientAddress,
                                      int destinationZone, double weight, double length, double width,
                                      double height, String packageType) {
        try {
            Shipment shipment = shipmentManager.createShipment(this.id, this.name, senderAddress,
                                                               recipientName, recipientAddress, destinationZone,
                                                               weight, length, width, height, packageType);

            // Automatically create invoice
            invoiceManager.createInvoice(shipment.getTrackingNumber(), this.id, shipment.getShippingCost());

            System.out.println(name + " created shipment request with tracking #: " + shipment.getTrackingNumber());
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating shipment: " + e.getMessage());
        }
    }

    // Tracks a package by tracking number.
    public void trackPackage(String trackingNumber) {
        try {
            Shipment shipment = shipmentManager.getShipment(trackingNumber);
            System.out.println("\n--- PACKAGE TRACKING ---");
            System.out.println("Tracking #: " + shipment.getTrackingNumber());
            System.out.println("Status: " + shipment.getStatus());
            System.out.println("Recipient: " + shipment.getRecipientName());
            System.out.println("Destination Zone: " + shipment.getDestinationZone());
            System.out.println("Created: " + shipment.getCreatedDate());
            if (shipment.getDeliveredDate() != null) {
                System.out.println("Delivered: " + shipment.getDeliveredDate());
            }
            System.out.println("------------------------\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error tracking package: " + e.getMessage());
        }
    }

    // Views customer's invoices.
    public void viewInvoices() {
        List<Invoice> invoices = invoiceManager.getCustomerInvoices(this.id);
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
        } else {
            System.out.println("\n--- YOUR INVOICES ---");
            for (Invoice inv : invoices) {
                System.out.println("Invoice ID: " + inv.getInvoiceId() +
                                 " | Amount: $" + String.format("%.2f", inv.getTotalAmount()) +
                                 " | Status: " + inv.getPaymentStatus());
            }
            System.out.println("---------------------\n");
        }
    }

    // Pays an invoice.
    public void payInvoice(String invoiceId, double amount, String paymentMethod) {
        try {
            invoiceManager.processPayment(invoiceId, amount, paymentMethod);
            System.out.println(name + " paid $" + amount + " towards invoice " + invoiceId);
        } catch (IllegalArgumentException e) {
            System.out.println("Payment error: " + e.getMessage());
        }
    }
}