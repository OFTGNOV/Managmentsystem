package managmentsystem.user_customer;

import java.util.List;

import managmentsystem.shipment.ShipmentManager;
import managmentsystem.vehicle_routing.RouteManager;
import managmentsystem.vehicle_routing.VehicleManager;

/**
 * Clerk class handles shipment processing and route assignment.
 */
public class Clerk extends User {
    private ShipmentManager shipmentManager;
    private VehicleManager vehicleManager;
    private RouteManager routeManager;

    public Clerk(String id, String name, String email, String password,
                 ShipmentManager shipmentManager, VehicleManager vehicleManager, RouteManager routeManager) {
        super(id, name, email, password);
        this.shipmentManager = shipmentManager;
        this.vehicleManager = vehicleManager;
        this.routeManager = routeManager;
    }

    /**
     * Processes a shipment and assigns it to a vehicle/route.
     */
    public void processShipment(String trackingNumber, String vehicleId, String routeId) {
        try {
            Shipment shipment = shipmentManager.getShipment(trackingNumber);
            Vehicle vehicle = vehicleManager.getVehicle(vehicleId);

            // Check if vehicle has capacity
            if (!vehicleManager.canAssignPackage(vehicleId, shipment.getWeight())) {
                System.out.println("ERROR: Vehicle " + vehicleId + " does not have sufficient capacity.");
                return;
            }

            // Assign package to vehicle
            vehicleManager.assignPackageToVehicle(vehicleId, shipment.getWeight());

            // Add shipment to route
            routeManager.addShipmentToRoute(routeId, trackingNumber);

            // Update shipment status
            shipmentManager.updateShipmentStatus(trackingNumber, Shipment.ShipmentStatus.ASSIGNED);

            System.out.println(name + " processed shipment " + trackingNumber + " and assigned to vehicle " + vehicleId);
        } catch (IllegalArgumentException e) {
            System.out.println("Error processing shipment: " + e.getMessage());
        }
    }

    /**
     * Views vehicle capacity and utilization.
     */
    public void checkVehicleCapacity(String vehicleId) {
        try {
            Vehicle vehicle = vehicleManager.getVehicle(vehicleId);
            System.out.println("\n--- VEHICLE STATUS ---");
            System.out.println("Vehicle ID: " + vehicle.getVehicleId());
            System.out.println("Current Weight: " + vehicle.getCurrentWeight() + " kg");
            System.out.println("Max Weight: " + vehicle.getMaxWeightCapacity() + " kg");
            System.out.println("Packages: " + vehicle.getCurrentPackageCount() + " / " + vehicle.getMaxPackageCapacity());
            System.out.println("Utilization: " + String.format("%.2f", vehicle.getUtilizationPercentage()) + "%");
            System.out.println("----------------------\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}