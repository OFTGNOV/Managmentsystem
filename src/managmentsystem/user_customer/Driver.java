package managmentsystem.user_customer;

import managmentsystem.database.RouteRepository;
import managmentsystem.shipment.ShipmentManager;
import managmentsystem.vehicle_routing.RouteManager;

import org.bson.Document;  // ADD THIS IMPORT
import java.util.List;

/**
 * Driver class manages delivery status updates.
 */
public class Driver extends User {
    private RouteManager routeManager;
    private ShipmentManager shipmentManager;

    public Driver(String id, String name, String email, String password,
                  RouteManager routeManager, ShipmentManager shipmentManager) {
        super(id, name, email, password);
        this.routeManager = routeManager;
        this.shipmentManager = shipmentManager;
    }

    /**
     * Driver views their assigned routes and deliveries.
     */
    public void viewAssignedDeliveries() {
        List<Document> routes = routeManager.getDriverRoutes(this.id);
        if (routes.isEmpty()) {
            System.out.println("No routes assigned.");
        } else {
            System.out.println("\n--- YOUR ASSIGNED ROUTES ---");
            for (Document route : routes) {
                System.out.println("Route ID: " + route.getString("routeId") +
                                 " | Vehicle: " + route.getString("vehicleId") +
                                 " | Status: " + route.getString("status") +
                                 " | Packages: " + route.getList("shipmentIds", String.class).size());
            }
            System.out.println("----------------------------\n");
        }
    }

    /**
     * Updates delivery status for a package.
     */
    public void updateDeliveryStatus(String trackingNumber, String status) {
        try {
            Shipment.ShipmentStatus newStatus = Shipment.ShipmentStatus.valueOf(status.toUpperCase());
            shipmentManager.updateShipmentStatus(trackingNumber, newStatus);
            System.out.println(name + " updated package " + trackingNumber + " to " + newStatus);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status. Use: PENDING, ASSIGNED, IN_TRANSIT, DELIVERED, or CANCELLED");
        }
    }
}