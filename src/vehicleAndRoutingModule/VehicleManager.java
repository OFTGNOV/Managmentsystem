package vehicleAndRoutingModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VehicleManager handles vehicle fleet management and capacity tracking.
 */
public class VehicleManager {
    private Map<String, Vehicle> vehicles; // key: vehicle ID
    private Map<String, VehicleSchedule> vehicleSchedules; // key: vehicle ID

    /**
     * Constructor initializes the vehicles collection.
     */
    public VehicleManager() {
        this.vehicles = new HashMap<>();
        this.vehicleSchedules = new HashMap<>();
    }

    /**
     * Registers a new vehicle in the fleet.
     */
    public Vehicle addVehicle(String vehicleId, String vehicleType, double maxWeightCapacity, int maxPackageCapacity) {
        if (vehicles.containsKey(vehicleId)) {
            throw new IllegalArgumentException("Vehicle already exists: " + vehicleId);
        }
        
        Vehicle vehicle = new Vehicle(vehicleId, vehicleType, maxWeightCapacity, maxPackageCapacity);
        vehicles.put(vehicleId, vehicle);
        
        // Initialize schedule for this vehicle
        vehicleSchedules.put(vehicleId, new VehicleSchedule(vehicleId));
        
        System.out.println("Vehicle registered: " + vehicleId);
        return vehicle;
    }

    /**
     * Retrieves a vehicle by ID.
     */
    public Vehicle getVehicle(String vehicleId) {
        if (!vehicles.containsKey(vehicleId)) {
            throw new IllegalArgumentException("Vehicle not found: " + vehicleId);
        }
        return vehicles.get(vehicleId);
    }

    /**
     * Checks if a vehicle can accept a package. (capacity check)
     */
    public boolean canAssignPackage(String vehicleId, double packageWeight) {
        Vehicle vehicle = getVehicle(vehicleId);
        return vehicle.canAddPackage(packageWeight);
    }

    /**
     * Assigns a package to a vehicle (reduces available capacity).
     */
    public boolean assignPackageToVehicle(String vehicleId, double packageWeight) {
        Vehicle vehicle = getVehicle(vehicleId);
        boolean assigned = vehicle.addPackage(packageWeight);
        
        if(assigned) {
        	System.out.println("Package: " + packageWeight + "kg) assigned to vehicle" + vehicleId);
        	} else {
        		System.out.println("Error: Cannot assign package - capacity exceed");
        	}
        return assigned;
    }
    
    
    //Assigns multiple packages to a vehicle 
    public boolean assignPackagesToVehicle(String vehicleId, List<Shipment> shipments) {
        Vehicle vehicle = getVehicle(vehicleId);
        
        // First check if all packages can fit
        double totalWeight = 0;
        for (Shipment shipment : shipments) {
            totalWeight += shipment.getWeight();
        }
        
        if (vehicle.getCurrentWeight() + totalWeight > vehicle.getMaxWeightCapacity() ||
            vehicle.getCurrentPackageCount() + shipments.size() > vehicle.getMaxPackageCapacity()) {
            System.out.println("ERROR: Cannot assign " + shipments.size() + 
                             " packages - would exceed vehicle capacity");
            return false;
        }
        
        // Assign all packages
        for (Shipment shipment : shipments) {
            vehicle.addPackage(shipment.getWeight());
        }
        
        System.out.println(shipments.size() + " packages assigned to vehicle " + vehicleId);
        return true;
    }

    /**
     * Removes a package from a vehicle (increases available capacity).
     */
    public void removePackageFromVehicle(String vehicleId, double packageWeight) {
        Vehicle vehicle = getVehicle(vehicleId);
        vehicle.removePackage(packageWeight);
        System.out.println("Package removed from vehicle" +vehicleId);
    }
    
    //Checks if a vehicle is available for a specific time period. 
    public boolean isVehicleAvailable(String vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!vehicleSchedules.containsKey(vehicleId)) {
            throw new IllegalArgumentException("Vehicle schedule not found: " + vehicleId);
        }
        
        VehicleSchedule schedule = vehicleSchedules.get(vehicleId);
        return schedule.isAvailable(startTime, endTime);
    }
    
    //Books a vehicle for a specific route and time period
    //Prevents overlapping schedules.
    
    public boolean bookVehicle(String vehicleId, String routeId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!vehicleSchedules.containsKey(vehicleId)) {
            throw new IllegalArgumentException("Vehicle schedule not found: " + vehicleId);
        }
        
        VehicleSchedule schedule = vehicleSchedules.get(vehicleId);
        boolean booked = schedule.bookSlot(routeId, startTime, endTime);
        
        if (booked) {
            System.out.println("Vehicle " + vehicleId + " booked for route " + routeId);
        } else {
            System.out.println("ERROR: Vehicle " + vehicleId + 
                             " is not available during the requested time period (schedule conflict)");
        }
        
        return booked;
    }
    
    //Cancels a vehicle booking for a route
    public void cancelVehicleBooking(String vehicleId, String routeId) {
        if (!vehicleSchedules.containsKey(vehicleId)) {
            throw new IllegalArgumentException("Vehicle schedule not found: " + vehicleId);
        }
        
        VehicleSchedule schedule = vehicleSchedules.get(vehicleId);
        schedule.cancelSlot(routeId);
        System.out.println("Booking cancelled for vehicle " + vehicleId + ", route " + routeId);
    }
    
    //Gets the vehicle schedule 
    public VehicleSchedule getVehicleSchedule(String vehicleId) {
        if (!vehicleSchedules.containsKey(vehicleId)) {
            throw new IllegalArgumentException("Vehicle schedule not found: " + vehicleId);
        }
        return vehicleSchedules.get(vehicleId);
    }
    

    /**
     * Retrieves all available vehicles.
     */
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> available = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if (v.isAvailable()) {
                available.add(v);
            }
        }
        return available;
    }

    //Retrieves vehicles available for a specific time period (no schedule conflicting) 
    public List<Vehicle> getAvailableVehiclesForTime(LocalDateTime startTime, LocalDateTime endTime) {
        List<Vehicle> available = new ArrayList<>();
        for (String vehicleId : vehicles.keySet()) {
            if (isVehicleAvailable(vehicleId, startTime, endTime)) {
                available.add(vehicles.get(vehicleId));
            }
        }
        return available;
    }
    
    /**
     * Retrieves all vehicles.
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }
    
    //Gets vehicle utilization reports 
    public void printVehicleUtilizationReport() {
        System.out.println("\nVEHICLE UTILIZATION REPORT");
        for (Vehicle vehicle : vehicles.values()) {
            System.out.printf("Vehicle: %s (%s)%n", vehicle.getVehicleId(), vehicle.getVehicleType());
            System.out.printf("  Weight: %.2f / %.2f kg (%.1f%%)%n", 
                            vehicle.getCurrentWeight(), 
                            vehicle.getMaxWeightCapacity(),
                            vehicle.getUtilizationPercentage());
            System.out.printf("  Packages: %d / %d%n", 
                            vehicle.getCurrentPackageCount(),
                            vehicle.getMaxPackageCapacity());
            System.out.printf("  Status: %s%n", vehicle.isAvailable() ? "Available" : "Full");
            System.out.println("  ---");
        }
    }
}