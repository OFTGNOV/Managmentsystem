package VehicleAndRoutingModule;

/*
 * Vehicle class represents a delivery vehicle in the SmartShip fleet.
 * Tracks capacity limits and current load.
*/
public class Vehicle {
    private String vehicleId;
    private String vehicleType; // van, truck, motorcycle
    private double maxWeightCapacity; // in kg
    private int maxPackageCapacity; // number of packages
    private double currentWeight;
    private int currentPackageCount;
    private boolean isAvailable;

    public Vehicle(String vehicleId, String vehicleType, double maxWeightCapacity, int maxPackageCapacity) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.maxWeightCapacity = maxWeightCapacity;
        this.maxPackageCapacity = maxPackageCapacity;
        this.currentWeight = 0;
        this.currentPackageCount = 0;
        this.isAvailable = true;
    }

    public boolean canAddPackage(double packageWeight) {
        return (currentWeight + packageWeight <= maxWeightCapacity) &&
               (currentPackageCount + 1 <= maxPackageCapacity);
    }

    public boolean addPackage(double packageWeight) {
        if (canAddPackage(packageWeight)) {
            currentWeight += packageWeight;
            currentPackageCount++;
            if (currentPackageCount >= maxPackageCapacity) {
                isAvailable = false;
            }
            return true;
        }
        return false;
    }

    public void removePackage(double packageWeight) {
        currentWeight -= packageWeight;
        currentPackageCount--;
        if (currentPackageCount < maxPackageCapacity) {
            isAvailable = true;
        }
    }

    public double getUtilizationPercentage() {
        return (currentWeight / maxWeightCapacity) * 100;
    }

    // Getters
    public String getVehicleId() { return vehicleId; }
    public String getVehicleType() { return vehicleType; }
    public double getMaxWeightCapacity() { return maxWeightCapacity; }
    public int getMaxPackageCapacity() { return maxPackageCapacity; }
    public double getCurrentWeight() { return currentWeight; }
    public int getCurrentPackageCount() { return currentPackageCount; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return String.format("%s [%s] - %.2f/%.2f kg (%.1f%% used)",
                vehicleId, vehicleType, currentWeight, maxWeightCapacity, getUtilizationPercentage());
    }
}

