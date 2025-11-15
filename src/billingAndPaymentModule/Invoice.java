package billingAndPaymentModule;

import java.time.LocalDateTime;
import shipmentModule.Shipment;
import userModule.Customer;

/**
 * Invoice class represents a billing invoice for a shipment.
 * Tracks payment status, amount, and discounts/surcharges.
 */
public class Invoice {
    private String invoiceId;
    private String trackingNumber;
    private String customerId;
    private double baseAmount;
    private double discount;
    private double totalAmount;
    private PaymentStatus pStatus;
    private LocalDateTime invoiceDate;
    //Helpers
    private Shipment shipment;
    private Customer customer;

    public Invoice(PaymentStatus pStatus,double totalAmount, Shipment shipment, Customer customer) {
        this.trackingNumber = shipment.generateTrackingNumber();
        this.customerId = customer.getId();
        this.invoiceId = "INV" + Math.floor(Math.random() * 100000); // Generating a random Invoice ID
        this.baseAmount = shipment.getShippingCost();
        this.discount = 0;
        this.pStatus = PaymentStatus.UNPAID;
        this.invoiceDate = LocalDateTime.now();
        this.totalAmount = calculateTotal();
    }
    
    // Apply discount to the invoice
    public void applyDiscount(double discountAmount) {
        if (discountAmount > 0 && discountAmount <= baseAmount) {
            this.discount = discountAmount;
            calculateTotal();
        }
    }

    // Calculate total amount after discount
    private double calculateTotal() {
        return baseAmount - discount;
    }

    public void updatePaymentStatus(PaymentStatus pStatus) {
        this.pStatus = pStatus;
    }
 
    @Override
    public String toString() {
        return String.format("Invoice #%s - Total: $%.2f [%s]", invoiceId, totalAmount, pStatus);
    }
    
    //Getters and Setters
	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public double getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(double baseAmount) {
		this.baseAmount = baseAmount;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public PaymentStatus getpStatus() {
		return pStatus;
	}

	public void setpStatus(PaymentStatus pStatus) {
		this.pStatus = pStatus;
	}

	public LocalDateTime getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDateTime invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
    
}
