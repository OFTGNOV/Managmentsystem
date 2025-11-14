package managmentsystem.user_customer;

import managmentsystem.database.RouteRepository;
import org.bson.Document;  // ADD THIS IMPORT
import java.util.List;

/**
 * Driver class for viewing assigning routes and packages
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
            System.out.println("\n" + name + ", you have NO routes assigned.");
            return;
        } 
        
        System.out.println("\n--- YOUR ASSIGNED ROUTES ---");
        System.out.println("Driver:" +name + "(ID:" + this.id + ")");
        
        for (Document route : routes) {
        	String routeId = route.getString("routeId");
        	String vehicleId = route.getString("vehicleId");
        	String status = route.getString("status");
        	List<String> shipmentIds = route.getList("shipmentId", String.class);
        	
        	System.out.println("\nRoute ID: " + routeId);
            System.out.println("Vehicle: " + vehicleId);
            System.out.println("Status: " + status);
            System.out.println("Total Packages: " + shipmentIds.size());
            System.out.println("Deliveries:");
            
            //Display details of each package
            int packageNum = 1;
            for (String trackingNum : shipmentIds) {
            	try {
                    Shipment shipment = shipmentManager.getShipment(trackingNum);
                    System.out.printf("  %d. [%s] %s%n", 
                                    packageNum++, 
                                    trackingNum,
                                    shipment.getRecipientAddress());
                    System.out.printf("     Recipient: %s | Weight: %.2f kg | Status: %s%n",
                                    shipment.getRecipientName(),
                                    shipment.getWeight(),
                                    shipment.getStatus());
                } catch (IllegalArgumentException e) {
                    System.out.println("  " + packageNum++ + ". [" + trackingNum + "] - Details unavailable");
                }
            
            }
            System.out.println("----------------------------\n");
        }
    }
    
    /**
     * Driver views details for a specific route
     */
    public void viewRouteDetails(String routeId) {
        try {
            Document route = routeManager.getRoute(routeId);
            
            if (route == null) {
                System.out.println("Route not found: " + routeId);
                return;
            }

            // Verify this route is assigned to this driver
            String assignedDriverId = route.getString("driverId");
            if (!assignedDriverId.equals(this.id)) {
                System.out.println("ERROR: This route is not assigned to you");
                return;
            }

            String vehicleId = route.getString("vehicleId");
            String status = route.getString("status");
            List<String> shipmentIds = route.getList("shipmentIds", String.class);

            System.out.println("\n========== ROUTE DETAILS ==========");
            System.out.println("Route ID: " + routeId);
            System.out.println("Vehicle: " + vehicleId);
            System.out.println("Driver: " + name);
            System.out.println("Status: " + status);
            System.out.println("Total Packages: " + shipmentIds.size());
            System.out.println("\nPackage List:");
            
            int packageNum = 1;
            for (String trackingNum : shipmentIds) {
                try {
                    Shipment shipment = shipmentManager.getShipment(trackingNum);
                    System.out.println("\n" + packageNum++ + ". Tracking #: " + trackingNum);
                    System.out.println("   Recipient: " + shipment.getRecipientName());
                    System.out.println("   Address: " + shipment.getRecipientAddress());
                    System.out.println("   Weight: " + shipment.getWeight() + " kg");
                    System.out.println("   Type: " + shipment.getPackageType());
                    System.out.println("   Status: " + shipment.getStatus());
                    System.out.println("   Zone: " + shipment.getDestinationZone());
                } catch (IllegalArgumentException e) {
                    System.out.println(packageNum++ + ". [" + trackingNum + "] - Details unavailable");
                }
            }
            System.out.println("===================================\n");
        } catch (Exception e) {
            System.out.println("Error viewing route details: " + e.getMessage());
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
    
    
    /**
     * Marks a delivery as in transit.
     */
    public void startDelivery(String trackingNumber) {
        updateDeliveryStatus(trackingNumber, "IN_TRANSIT");
    }

    /**
     * Marks a delivery as completed.
     */
    public void completeDelivery(String trackingNumber) {
        updateDeliveryStatus(trackingNumber, "DELIVERED");
    }

    /**
     * Completes an entire route.
     */
    public void completeRoute(String routeId) {
        try {
            Document route = routeManager.getRoute(routeId);
            
            if (route == null) {
                System.out.println("Route not found: " + routeId);
                return;
            }

            // Verify this route is assigned to this driver
            String assignedDriverId = route.getString("driverId");
            if (!assignedDriverId.equals(this.id)) {
                System.out.println("ERROR: This route is not assigned to you");
                return;
            }

            routeManager.completeRoute(routeId);
            System.out.println(name + " completed route " + routeId);
        } catch (Exception e) {
            System.out.println("Error completing route: " + e.getMessage());
        }
    }

    /**
     * Gets the count of assigned routes.
     */
    public int getAssignedRouteCount() {
        return routeManager.getDriverRouteCount(this.id);
    }

    /**
     * Views summary of today's deliveries.
     */
    public void viewDeliverySummary() {
        List<Document> routes = routeManager.getDriverRoutes(this.id);
        
        int totalRoutes = routes.size();
        int totalPackages = 0;
        int completedRoutes = 0;

        for (Document route : routes) {
            List<String> shipmentIds = route.getList("shipmentIds", String.class);
            totalPackages += shipmentIds.size();
            
            if ("COMPLETED".equals(route.getString("status"))) {
                completedRoutes++;
            }
        }

        System.out.println("\n DELIVERY SUMMARY ");
        System.out.println("Driver: " + name);
        System.out.println("Total Routes: " + totalRoutes);
        System.out.println("Completed Routes: " + completedRoutes);
        System.out.println("Total Packages: " + totalPackages);
        
    }
    
    
    
    
}