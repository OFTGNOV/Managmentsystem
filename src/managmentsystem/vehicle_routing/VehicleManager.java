package managmentsystem.vehicle_routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VehicleManager handles vehicle fleet management and capacity tracking.
 */
public class VehicleManager {
    private Map<String, Vehicle> vehicles; // key: vehicle ID

    /**
     * Constructor initializes the vehicles collection.
     */
    public VehicleManager() {
        this.vehicles = new HashMap<>();
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
     * Checks if a vehicle can accept a package.
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
        return vehicle.addPackage(packageWeight);
    }

    /**
     * Removes a package from a vehicle (increases available capacity).
     */
    public void removePackageFromVehicle(String vehicleId, double packageWeight) {
        Vehicle vehicle = getVehicle(vehicleId);
        vehicle.removePackage(packageWeight);
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

    /**
     * Retrieves all vehicles.
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }
}