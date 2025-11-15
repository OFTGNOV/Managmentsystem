package shipmentModule;

import java.time.LocalDateTime;

public class Shipment {
	private String trackingNumber;
    private String senderId;
    private String senderName;        
    private String senderAddress;     
    private int destinationZone;
    private double weight; //grams
    private double length;     
    private double width;  //width, length and height are in Centimetes       
    private double height;            
    private PackageType pType;
    private ShipmentStatus status;
    private double shippingCost;
    private LocalDateTime createdDate;
    private LocalDateTime deliveredDate;

    // Paramtized Constructor
    public Shipment(String senderId, String senderName, String senderAddress,
                    int destinationZone, double weight, double length, double width, 
                    double height, PackageType pType) {
    	
    	this.trackingNumber = generateTrackingNumber();
        this.senderId = senderId; 
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.destinationZone = destinationZone;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.pType = pType;
        this.status = ShipmentStatus.PENDING;
        this.createdDate = LocalDateTime.now();
    }
    
    // Copy Constructor
    public Shipment(Shipment other) {
    	this.trackingNumber = other.trackingNumber;
    	this.senderId = other.senderId; 
		this.senderName = other.senderName;
		this.senderAddress = other.senderAddress;
		this.destinationZone = other.destinationZone;
		this.weight = other.weight;
		this.length = other.length;
		this.width = other.width;
		this.height = other.height;
		this.pType = other.pType;
		this.status = other.status;
		this.shippingCost = other.shippingCost;
		this.createdDate = other.createdDate;
		this.deliveredDate = other.deliveredDate;
    }

    /* 
     * Calculates shipping cost based on weight, destination zone, 
     * and package type then sets the shippingCost attribute
     */
    public void calculateShippingCost() {
        double baseCost = 5.0;
        double weightCost = weight * 0.50;
        double zoneCost = getZoneDistance() * 0.01;
        double Upcharge = 0; //Upcharge for express and fragile packages

        if (pType == PackageType.EXPRESS) {
            Upcharge = 15.0;
        } else if (pType == PackageType.FRAGILE) {
            Upcharge = 10.0;
        }

        this.shippingCost = baseCost + weightCost + zoneCost + Upcharge;
    }
    
    //Randomly Generate a Tracking Number
    public static String generateTrackingNumber() {
		StringBuilder sb = new StringBuilder("TRK");
		for (int i = 0; i < 10; i++) {
			sb.append((int)(Math.random() * 10));
		}
		return sb.toString();
	}

    
    // Returns a random distance based on the destination zone
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
    
    @Override
	public String toString() {
		return "Shipment - trackingNumber=" + trackingNumber + 
				", \nsenderId=" + senderId + 
				", \nsenderName=" + senderName +
				", \nsenderAddress=" + senderAddress + 
				", \ndestinationZone=" + destinationZone + 
				", \nweight=" + weight + 
				", \nlength=" + length + 
				", \nwidth=" + width + 
				", \nheight=" + height + 
				", \nPackage Type=" + pType + 
				", \nStatus=" + status + 
				", \nShipping Cost=" + shippingCost + 
				", \nCreated Date=" + createdDate + 
				", \nDelivered Date=" + deliveredDate;
	}
    
    //Getters and Setters
	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public int getDestinationZone() {
		return destinationZone;
	}

	public void setDestinationZone(int destinationZone) {
		this.destinationZone = destinationZone;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public PackageType getpType() {
		return pType;
	}

	public void setpType(PackageType pType) {
		this.pType = pType;
	}

	public ShipmentStatus getStatus() {
		return status;
	}

	public void setStatus(ShipmentStatus status) {
		this.status = status;
	}

	public double getShippingCost() {
		return shippingCost;
	}
	
	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getDeliveredDate() {
		return deliveredDate;
	}

	public void setDeliveredDate(LocalDateTime deliveredDate) {
		this.deliveredDate = deliveredDate;
	}
}