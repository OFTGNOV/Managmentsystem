package managmentsystem.user_customer;

import java.util.List;
import java.time.LocalDateTime;

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
                System.out.println("Current: " + vehicle.getCurrentWeight() + " kg / " + 
                        vehicle.getCurrentPackageCount() + " packages");
                System.out.println("Max: " + vehicle.getMaxWeightCapacity() + " kg / " + 
                        vehicle.getMaxPackageCapacity() + " packages");
                System.out.println("Package weight: " + shipment.getWeight() + " kg");
                
                return;
            }

            // Assign package to vehicle
			boolean assigned = vehicleManager.assignPackageToVehicle(vehicleId, shipment.getWeight());
			            
			            if (!assigned) {
			                System.out.println("ERROR: Failed to assign package to vehicle");
			            return;
			        }

            // Add shipment to route
            routeManager.addShipmentToRoute(routeId, trackingNumber);

            // Update shipment status
            shipmentManager.updateShipmentStatus(trackingNumber, Shipment.ShipmentStatus.ASSIGNED);

            System.out.println(name + " processed shipment " + trackingNumber + " and assigned to vehicle " + vehicleId + "on route" + routeId);
        } catch (IllegalArgumentException e) {
            System.out.println("Error processing shipment: " + e.getMessage());
        }
    }

    //Assigns route to a vehicle with schedule validation 
    public boolean assignRouteToVehicle(String routeId, String vehicleId, 
    		LocalDateTime startTime, LocalDateTime endTime) {
    	try {
			// Check if vehicle is available during the time period
    		if (!vehicleManager.isVehicleAvailable(vehicleId, startTime, endTime)) {
			System.out.println("ERROR: Vehicle " + vehicleId + 
			      " has a schedule conflict during the requested time period");
			return false;
			}
			
			// Book the vehicle for this route
			boolean booked = vehicleManager.bookVehicle(vehicleId, routeId, startTime, endTime);
			
			if (booked) {
			System.out.println(name + " assigned route " + routeId + " to vehicle " + vehicleId);
			System.out.println("Scheduled from " + startTime + " to " + endTime);
			return true;
			} else {
			return false;
			}
    	} catch (IllegalArgumentException e) {
    		System.out.println("Error assigning route: " + e.getMessage());
			return false;
			}
    }
    
    
    //Assigns multiple shipments to a vehicle and route (bulk assignments)
    public void assignMultipleShipmentsToRoute(List<String> trackingNumbers, String vehicleId, String routeId) {
        try {
            Vehicle vehicle = vehicleManager.getVehicle(vehicleId);
            
            // Calculate total weight and validate capacity
            double totalWeight = 0;
            for (String trackingNum : trackingNumbers) {
                Shipment shipment = shipmentManager.getShipment(trackingNum);
                totalWeight += shipment.getWeight();
            }

            if (vehicle.getCurrentWeight() + totalWeight > vehicle.getMaxWeightCapacity() ||
                vehicle.getCurrentPackageCount() + trackingNumbers.size() > vehicle.getMaxPackageCapacity()) {
                System.out.println("ERROR: Cannot assign " + trackingNumbers.size() + 
                                 " packages - would exceed vehicle capacity");
                System.out.println("Total weight: " + totalWeight + " kg");
                System.out.println("Available capacity: " + 
                                 (vehicle.getMaxWeightCapacity() - vehicle.getCurrentWeight()) + " kg");
                return;
            }
            
         // Assign all shipments
            int successCount = 0;
            for (String trackingNum : trackingNumbers) {
                Shipment shipment = shipmentManager.getShipment(trackingNum);
                
                if (vehicleManager.assignPackageToVehicle(vehicleId, shipment.getWeight())) {
                    routeManager.addShipmentToRoute(routeId, trackingNum);
                    shipmentManager.updateShipmentStatus(trackingNum, Shipment.ShipmentStatus.ASSIGNED);
                    successCount++;
                }
            }

            System.out.println(name + " assigned " + successCount + " shipments to vehicle " + 
                             vehicleId + " on route " + routeId);
        } catch (IllegalArgumentException e) {
            System.out.println("Error in bulk assignment: " + e.getMessage());
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
            System.out.println("Type: " + vehicle.getVehicleType());
            System.out.println("Current Weight: " + vehicle.getCurrentWeight() + " kg");
            System.out.println("Max Weight: " + vehicle.getMaxWeightCapacity() + " kg");
            System.out.println("Available Weight: " + 
                    (vehicle.getMaxWeightCapacity() - vehicle.getCurrentWeight()) + " kg");
            System.out.println("Packages: " + vehicle.getCurrentPackageCount() + " / " + vehicle.getMaxPackageCapacity());
            System.out.println("Utilization: " + String.format("%.2f", vehicle.getUtilizationPercentage()) + "%");
            System.out.println("Status: " + (vehicle.isAvailable() ? "Available" : "Full"));
            System.out.println("----------------------\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Views vehicle schedule.
     */
    public void viewVehicleSchedule(String vehicleId) {
        try {
            VehicleSchedule schedule = vehicleManager.getVehicleSchedule(vehicleId);
            List<VehicleSchedule.ScheduleSlot> slots = schedule.getScheduleSlots();

            System.out.println("\n========== VEHICLE SCHEDULE ==========");
            System.out.println("Vehicle ID: " + vehicleId);
            
            if (slots.isEmpty()) {
                System.out.println("No scheduled routes");
            } else {
                System.out.println("Scheduled Routes:");
                for (VehicleSchedule.ScheduleSlot slot : slots) {
                    System.out.println("  " + slot);
                }
            }
            
            System.out.println("Next Available: " + schedule.getNextAvailableTime());
            System.out.println("======================================\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Lists all available vehicles with their capacity status.
     */
    public void listAvailableVehicles() {
        List<Vehicle> available = vehicleManager.getAvailableVehicles();
        
        System.out.println("\n========== AVAILABLE VEHICLES ==========");
        if (available.isEmpty()) {
            System.out.println("No vehicles available");
        } else {
            for (Vehicle vehicle : available) {
                System.out.println(vehicle);
            }
        }
        System.out.println("========================================\n");
    }

    /**
     * Lists vehicles available for a specific time period.
     */
    public void listAvailableVehiclesForTime(LocalDateTime startTime, LocalDateTime endTime) {
        List<Vehicle> available = vehicleManager.getAvailableVehiclesForTime(startTime, endTime);
        
        System.out.println("\n========== VEHICLES AVAILABLE FOR TIME PERIOD ==========");
        System.out.println("From: " + startTime);
        System.out.println("To: " + endTime);
        
        if (available.isEmpty()) {
            System.out.println("No vehicles available during this time period");
        } else {
            for (Vehicle vehicle : available) {
                System.out.println(vehicle);
            }
        }
    }
    
    
}