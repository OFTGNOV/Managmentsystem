package vehicleAndRoutingModule;

import userModule.Driver;

/* Vehicle class represents a delivery vehicle in the SmartShip fleet.
 * Tracks capacity limits and current load.
*/
public class Vehicle {
	private Driver assignedDriver; // Driver assigned to this vehicle
    private String licensePlate;
    private String vehicleType; // van, truck, car, bike,
    private double maxWeightCapacity; // in kg
    private int maxPackageCapacity; // number of packages
    private double currentWeight;
    private int currentPackageCount;
    private boolean isAvailable;

    // Parametrized Constructor
    public Vehicle(Driver assignedDriver, String licensePlate, String vehicleType,
    				double maxWeightCapacity, int maxPackageCapacity) {
    	
    	this.assignedDriver = assignedDriver;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.maxWeightCapacity = maxWeightCapacity;
        this.maxPackageCapacity = maxPackageCapacity;
        this.currentWeight = 0;
        this.currentPackageCount = 0;
        this.isAvailable = true;
    }
   
    //copy constructor
    public Vehicle(Vehicle other) {
		this.licensePlate = other.licensePlate;
		this.vehicleType = other.vehicleType;
		this.maxWeightCapacity = other.maxWeightCapacity;
		this.maxPackageCapacity = other.maxPackageCapacity;
		this.currentWeight = other.currentWeight;
		this.currentPackageCount = other.currentPackageCount;
		this.isAvailable = other.isAvailable;
		}

    // Checks if a package can be added without exceeding capacity
    public boolean canAddPackage(double packageWeight) {
        return (currentWeight + packageWeight <= maxWeightCapacity) &&
               (currentPackageCount + 1 <= maxPackageCapacity);
    }

    // Adds a package to the vehicle if possible
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

    // Removes a package from the vehicle
    public void removePackage(double packageWeight) {
        currentWeight -= packageWeight;
        currentPackageCount--;
        if (currentPackageCount < maxPackageCapacity) {
            isAvailable = true;
        }
    }
    // Returns the current utilization percentage of the vehicle
    public double getUtilizationPercentage() {
        return (currentWeight / maxWeightCapacity) * 100;
    }

    @Override
    public String toString() {
        return "Assigned Driver: " + assignedDriver.getId() +
        		", \nLicense Plate: " + licensePlate + 
        		", \nVehicle Type: " + vehicleType + 
        		", \nMax Weight Capacity: " + maxWeightCapacity + 
        		"kg, \nMax Package Capacity: " + maxPackageCapacity + 
				"kg, \nCurrent Vehcicle Weight: " + currentWeight + 
				"kg, \nCurrent Package Count: " + currentPackageCount + 
				", \nIs This Vehcile Available: " + isAvailable;
    }
    
    
    // Getters and Setters
	public String getVehicleId() {
		return licensePlate;
	}

	public void setVehicleId(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public double getMaxWeightCapacity() {
		return maxWeightCapacity;
	}

	public void setMaxWeightCapacity(double maxWeightCapacity) {
		this.maxWeightCapacity = maxWeightCapacity;
	}

	public int getMaxPackageCapacity() {
		return maxPackageCapacity;
	}

	public void setMaxPackageCapacity(int maxPackageCapacity) {
		this.maxPackageCapacity = maxPackageCapacity;
	}

	public double getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(double currentWeight) {
		this.currentWeight = currentWeight;
	}

	public int getCurrentPackageCount() {
		return currentPackageCount;
	}

	public void setCurrentPackageCount(int currentPackageCount) {
		this.currentPackageCount = currentPackageCount;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
    
    
}

