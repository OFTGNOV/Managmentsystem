package managmentsystem.shipment;

import managmentsystem.user_customer.Clerk;
import managmentsystem.user_customer.Customer;
import managmentsystem.user_customer.Driver;

/**
 * Main class to demonstrate the SmartShip system.
 */
public class SmartShipTest {
    public static void main(String[] args) {
        // Initialize managers
        ShipmentManager shipmentManager = new ShipmentManager();
        VehicleManager vehicleManager = new VehicleManager();
        InvoiceManager invoiceManager = new InvoiceManager();
        RouteManager routeManager = new RouteManager();

        // Create users
        Customer customer = new Customer("C001", "John Doe", "john@email.com", "pass123",
                                        shipmentManager, invoiceManager);
        Clerk clerk = new Clerk("CL001", "Jane Smith", "jane@email.com", "pass123",
                               shipmentManager, vehicleManager, routeManager);
        Driver driver = new Driver("D001", "Mike Johnson", "mike@email.com", "pass123",
                                  routeManager, shipmentManager);

        // Add vehicles
        vehicleManager.addVehicle("V001", "van", 500, 10);
        vehicleManager.addVehicle("V002", "truck", 1000, 20);

        // Customer creates shipment
        customer.login();
        customer.createShipmentRequest("123 Main St", "Jane Recipient", "456 Oak Ave", 2, 50, 10, 10, 10, "express");

        // Clerk processes shipment
        clerk.login();
        clerk.checkVehicleCapacity("V001");
        clerk.processShipment("TRK1001", "V001", "RT00001");

        // Driver views deliveries
        driver.login();
        driver.viewAssignedDeliveries();
        driver.updateDeliveryStatus("TRK1001", "IN_TRANSIT");

        // Customer tracks and pays
        customer.trackPackage("TRK1001");
        customer.viewInvoices();
        customer.payInvoice("INV5001", 75.50, "card");
    }
}