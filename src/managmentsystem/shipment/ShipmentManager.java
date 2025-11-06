package managmentsystem.shipment;

import managmentsystem.database.ShipmentRepository;
import java.util.List;

//ShipmentManager handles all shipment-related operations using MongoDB.
public class ShipmentManager {
    private ShipmentRepository shipmentRepository;
    private static int shipmentCounter = 1000;

    public ShipmentManager() {
        this.shipmentRepository = new ShipmentRepository();
    }


    //Creates a new shipment with automatic tracking number generation.
    public Shipment createShipment(String senderId, String senderName, String senderAddress,
                                    String recipientName, String recipientAddress, int destinationZone,
                                    double weight, double length, double width, double height, String packageType) {
        if (weight <= 0 || destinationZone < 1 || destinationZone > 4) {
            throw new IllegalArgumentException("Invalid shipment parameters");
        }

        String trackingNumber = "TRK" + (++shipmentCounter);

        Shipment shipment = new Shipment(trackingNumber, senderId, senderName, senderAddress,
                                         recipientName, recipientAddress, destinationZone,
                                         weight, length, width, height, packageType);

        shipment.calculateShippingCost();
        shipmentRepository.createShipment(shipment);

        System.out.println("Shipment created successfully. Tracking #: " + trackingNumber);
        return shipment;
    }

    // Retrieves a shipment by tracking number.
    public Shipment getShipment(String trackingNumber) {
        Shipment shipment = shipmentRepository.getShipmentByTracking(trackingNumber);
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment not found: " + trackingNumber);
        }
        return shipment;
    }

    // Retrieves all shipments for a specific customer.
    public List<Shipment> getCustomerShipments(String customerId) {
        return shipmentRepository.getShipmentsByCustomer(customerId);
    }

    // Updates shipment status.
    public void updateShipmentStatus(String trackingNumber, ShipmentStatus newStatus) {
        shipmentRepository.updateShipmentStatus(trackingNumber, newStatus);
        System.out.println("Shipment " + trackingNumber + " status updated to " + newStatus);
    }

    //Retrieves all shipments.
    public List<Shipment> getAllShipments() {
        return shipmentRepository.getAllShipments();
    }
}