package shipmentModule;

import java.time.LocalDateTime;
import userModule.Customer;

public class Shipment {
	private String trackingNumber;
    private Customer sender;
    private Customer recipent;            
    private double weight; //weight is in Kilograms
    private double length;     
    private double width;  //width, length and height are in Centimetes       
    private double height;            
    private PackageType pType;
    private ShipmentStatus status;
    private double shippingCost;
    private LocalDateTime createdDate;
    private LocalDateTime deliveredDate;


    // Paramtized Constructor
    public Shipment(Customer sender, Customer recipent, double weight, 
    				double length, double width, double height, PackageType pType) {
    	this.trackingNumber = generateTrackingNumber();
        this.sender = sender; 
        this.recipent = recipent;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.pType = pType;
        this.status = ShipmentStatus.PENDING;
        this.shippingCost = calculateShippingCost();
        this.createdDate = LocalDateTime.now();
    }
    
    // Copy Constructor
    public Shipment(Shipment other) {
    	this.trackingNumber = other.trackingNumber;
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

    /* Calculates shipping cost based on weight, destination zone, 
     * and package type then sets the shippingCost attribute */
    public double calculateShippingCost() {
        double baseCost = 5.0;
        double weightCost = weight * 0.50;
        double zoneCost = getZoneDistance() * 0.10;
        double Upcharge = 0; 
        //Upcharge for express and fragile packages
        if (pType == PackageType.EXPRESS) {
            Upcharge = 15.0;
        } else if (pType == PackageType.FRAGILE) {
            Upcharge = 10.0;
        }

        return baseCost + weightCost + zoneCost + Upcharge;
    }
    
    //Randomly Generate a Tracking Number
    public String generateTrackingNumber() {
		StringBuilder sb = new StringBuilder("TRK");
		// Generate 10 random digits
		for (int i = 0; i < 5; i++) {
			sb.append((int)(Math.random() * 10));
		}
		
		if (pType == PackageType.EXPRESS ) {
			sb.append("EX");
		} else if (pType == PackageType.FRAGILE) {
			sb.append("FR");
		} else {
			sb.append("ST");
		}
			
		return sb.toString();
	}

    
    // Returns a random distance based on the destination zone
    private double getZoneDistance() {
    	int zone = recipent.getZone();
		switch (zone) {
			case 1:
				return Math.random() * 50.0; //
			case 2:
				return Math.random() * 100.0;
			case 3:
				return Math.random() * 200.0;
			case 4:
				return Math.random() * 300.0;
			default:
				return 0.0;
		}
    	
    }

    // Updates the shipment status and sets delivered date if applicable
    public void updateStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
        if (newStatus == ShipmentStatus.DELIVERED) {
            this.deliveredDate = LocalDateTime.now();
        }
    }
    
    @Override
	public String toString() {
		return "Shipment - trackingNumber=" + trackingNumber + 
				", \nSender: " + sender.getId()+ 
				", \nRecipent: " + recipent.getId() +
				", \nweight: " + weight + 
				"kg, \nlength: " + length + 
				"cm, \nwidth: " + width + 
				"cm, \nheight: " + height + 
				"cm, \nPackage Type: " + pType + 
				", \nStatus: " + status + 
				String.format(", \nShipping Cost: $%.2f", shippingCost) +  
				", \nCreated Date: " + createdDate + 
				", \nDelivered Date: " + deliveredDate;
	}
    
    //Getters and Setters
	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	public Customer getSender() {
		return sender;
	}
	
	public void setSender(Customer sender) {
		this.sender = sender;
	}
	
	public Customer getRecipent() {
		return recipent;
	}
	
	public void setRecipent(Customer recipent) {
		this.recipent = recipent;
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
