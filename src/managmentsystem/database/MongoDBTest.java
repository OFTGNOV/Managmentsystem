package managmentsystem.database;

import managmentsystem.billing_payment.InvoiceManager;
import managmentsystem.shipment.Shipment;
import managmentsystem.shipment.ShipmentManager;
import managmentsystem.vehicle_routing.RouteManager;
import managmentsystem.vehicle_routing.Vehicle;
import managmentsystem.vehicle_routing.VehicleManager;
import managmentsystem.shipment.ShipmentStatus;
import org.bson.Document;  // ADD THIS IMPORT
import java.util.List;

/**
 * MongoDBTest demonstrates MongoDB operations with SmartShip system.
 */
public class MongoDBTest {
    public static void main(String[] args) {
        try {
            System.out.println("=== SmartShip MongoDB Integration Test ===\n");

            // Initialize managers
            ShipmentManager shipmentManager = new ShipmentManager();
            VehicleManager vehicleManager = new VehicleManager();
            InvoiceManager invoiceManager = new InvoiceManager();
            RouteManager routeManager = new RouteManager();

            // Initialize repositories
            UserRepository userRepo = new UserRepository();

            // Create users
            System.out.println("--- Creating Users ---");
            userRepo.createUser("C001", "John Doe", "john@email.com", "pass123", "CUSTOMER");
            userRepo.createUser("D001", "Mike Johnson", "mike@email.com", "pass123", "DRIVER");
            userRepo.createUser("CL001", "Jane Smith", "jane@email.com", "pass123", "CLERK");

            // Create vehicles
            System.out.println("\n--- Creating Vehicles ---");
            vehicleManager.addVehicle("V001", "van", 500, 10);
            vehicleManager.addVehicle("V002", "truck", 1000, 20);

            // Create shipment
            System.out.println("\n--- Creating Shipment ---");
            Shipment shipment = shipmentManager.createShipment("C001", "John Doe", "123 Main St",
                                                               "Jane Recipient", "456 Oak Ave", 2, 50, 10, 10, 10, "express");

            // Create invoice
            System.out.println("\n--- Creating Invoice ---");
            invoiceManager.createInvoice(shipment.getTrackingNumber(), "C001", shipment.getShippingCost());

            // Create route
            System.out.println("\n--- Creating Route ---");
            routeManager.createRoute("V001", "D001");
            routeManager.addShipmentToRoute("RT00001", shipment.getTrackingNumber());

            // Test retrieval
            System.out.println("\n--- Testing Retrievals ---");
            Shipment retrieved = shipmentManager.getShipment(shipment.getTrackingNumber());
            System.out.println("Retrieved Shipment: " + retrieved.getTrackingNumber() + " | Status: " + retrieved.getStatus() + " | Cost: $" + String.format("%.2f", retrieved.getShippingCost()));

            // Test status update
            System.out.println("\n--- Updating Shipment Status ---");
            shipmentManager.updateShipmentStatus(shipment.getTrackingNumber(), ShipmentStatus.ASSIGNED);
            retrieved = shipmentManager.getShipment(shipment.getTrackingNumber());
            System.out.println("Updated Status: " + retrieved.getStatus());

            // Test vehicle capacity
            System.out.println("\n--- Testing Vehicle Capacity ---");
            System.out.println("Assigning package to vehicle V001...");
            vehicleManager.assignPackageToVehicle("V001", 50);
            Vehicle vehicle = vehicleManager.getVehicle("V001");
            System.out.println("Vehicle V001 Utilization: " + String.format("%.2f", vehicle.getUtilizationPercentage()) + "%");

            // Test payment
            System.out.println("\n--- Processing Payment ---");
            invoiceManager.processPayment("INV5001", 75.50, "card");

            // Test query operations
            System.out.println("\n--- Query Operations ---");
            System.out.println("Available Vehicles: " + vehicleManager.getAvailableVehicles().size());
            
            // FIX: Properly handle Document list
            List<Document> routes = routeManager.getDriverRoutes("D001");
            System.out.println("Driver Routes: " + routes.size());

            System.out.println("\n=== Test Complete ===");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}