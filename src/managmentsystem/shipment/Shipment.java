package managmentsystem.shipment;

import java.time.LocalDateTime;

public class Shipment {
    private String trackingNumber;
    private String senderId;
    private String senderName;        // ADD THIS
    private String senderAddress;     // ADD THIS
    private String recipientName;
    private String recipientAddress;
    private int destinationZone;
    private double weight;
    private double length;            // ADD THIS
    private double width;             // ADD THIS
    private double height;            // ADD THIS
    private String packageType;
    private ShipmentStatus status;
    private double shippingCost;
    private LocalDateTime createdDate;
    private LocalDateTime deliveredDate;


    // Constructor
    public Shipment(String trackingNumber, String senderId, String senderName, String senderAddress,
                    String recipientName, String recipientAddress, int destinationZone, double weight,
                    double length, double width, double height, String packageType) {
        this.trackingNumber = trackingNumber;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.destinationZone = destinationZone;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.packageType = packageType;
        this.status = ShipmentStatus.PENDING;
        this.createdDate = LocalDateTime.now();
    }

    public void calculateShippingCost() {
        double baseCost = 5.0;
        double weightCost = weight * 0.50;
        double zoneCost = getZoneDistance() * 0.01;
        double typeSurcharge = 0;

        if (packageType.equalsIgnoreCase("express")) {
            typeSurcharge = 15.0;
        } else if (packageType.equalsIgnoreCase("fragile")) {
            typeSurcharge = 10.0;
        }

        this.shippingCost = baseCost + weightCost + zoneCost + typeSurcharge;
    }

    private double getZoneDistance() {
        switch (destinationZone) {
            case 1: return 10 + Math.random() * 15;
            case 2: return 25 + Math.random() * 25;
            case 3: return 50 + Math.random() * 50;
            case 4: return 100 + Math.random() * 100;
            default: return 0;
        }
    }

    public void updateStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
        if (newStatus == ShipmentStatus.DELIVERED) {
            this.deliveredDate = LocalDateTime.now();
        }
    }

    // GETTERS - ADD ALL OF THESE
    public String getTrackingNumber() { return trackingNumber; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }           // ADD THIS
    public String getSenderAddress() { return senderAddress; }     // ADD THIS
    public String getRecipientName() { return recipientName; }
    public String getRecipientAddress() { return recipientAddress; }
    public int getDestinationZone() { return destinationZone; }
    public double getWeight() { return weight; }
    public double getLength() { return length; }                   // ADD THIS
    public double getWidth() { return width; }                     // ADD THIS
    public double getHeight() { return height; }                   // ADD THIS
    public String getPackageType() { return packageType; }
    public ShipmentStatus getStatus() { return status; }
    public double getShippingCost() { return shippingCost; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getDeliveredDate() { return deliveredDate; }
}
