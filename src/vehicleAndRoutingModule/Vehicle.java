package vehicleAndRoutingModule;

import userModule.Driver;
import shipmentModule.Shipment;
import shipmentModule.ShipmentStatus;
import shipmentModule.ShipmentStatusListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* Vehicle class represents a delivery vehicle in the SmartShip fleet.
 * Tracks capacity limits and current load.
*/
public class Vehicle implements ShipmentStatusListener {
    private Driver assignedDriver; // Driver assigned to this vehicle
    private String licensePlate;
    private String vehicleType; // van, truck, car, etc.
    private double maxWeightCapacity; // in kg
    private int maxPackageCapacity; // number of packages
    private double currentWeight;
    private int currentPackageCount;
    private boolean isAvailable; // manual availability flag (backwards compatible)
    private List<Shipment> assignedShipments; // List of shipments assigned to this vehicle should be able to remove shipments as they are delivered.

    // Current route this vehicle is executing (null when available)
    private Route currentRoute = null;

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
        this.assignedShipments = new ArrayList<>();
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
        this.assignedShipments = new ArrayList<>(other.assignedShipments);
        // register this vehicle as listener for the copied shipments
        for (Shipment s : this.assignedShipments) {
            s.addStatusListener(this);
        }
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

    // Removes a shipment from the vehicle
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

    // Assigns a shipment to this vehicle (and registers for status updates)
	public boolean assignShipment(Shipment shipment) {
	    if (shipment == null) return false;
	    double w = shipment.getWeight();
	    if (!canAddPackage(w)) return false;
	    assignedShipments.add(shipment);
	    shipment.addStatusListener(this);
	    addPackage(w);
	    return true;
	}

	// Unassigns a shipment from this vehicle (and unregisters listener)
	public boolean unassignShipment(Shipment shipment) {
	    if (shipment == null) return false;
	    if (assignedShipments.remove(shipment)) {
	        shipment.removeStatusListener(this);
	        removePackage(shipment.getWeight());
	        return true;
	    }
	    return false;
	}

	// Implementation of the ShipmentStatusListener
	@Override
	public void onStatusChanged(Shipment shipment, ShipmentStatus newStatus) {
	    if (newStatus == ShipmentStatus.DELIVERED) {
	        // Remove delivered shipment from this vehicle
	        // Use iterator to avoid ConcurrentModificationException if invoked during iteration
	        Iterator<Shipment> it = assignedShipments.iterator();
	        while (it.hasNext()) {
	            Shipment s = it.next();
	            if (s.equals(shipment)) {
	                it.remove();
	                s.removeStatusListener(this);
	                // update capacity counters
	                removePackage(s.getWeight());
	                break;
	            }
	        }
	    }
	}

	@Override
    public String toString() {
        return "Assigned Driver: " + assignedDriver.getdln() +
                ", \nLicense Plate: " + licensePlate + 
                ", \nVehicle Type: " + vehicleType + 
                ", \nMax Weight Capacity: " + maxWeightCapacity + 
                "kg, \nMax Package Capacity: " + maxPackageCapacity + 
                "kg, \nCurrent Vehicle Weight: " + currentWeight + 
                "kg, \nCurrent Package Count: " + currentPackageCount + 
                ", \nIs This Vehicle Available: " + isAvailable;
    }

    
    //
    public Driver getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(Driver assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
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
        // vehicle is available only when not marked busy by a route
        return isAvailable && currentRoute == null;
    }

    public void setAvailable(boolean isAvailable) {
        // preserve manual flag but a running route will still mark the vehicle busy
        this.isAvailable = isAvailable;
    }

    // Route reservation API
    public synchronized boolean assignToRoute(Route r) {
        if (r == null) return false;
        if (this.currentRoute != null) return false; // vehicle already on a route
        this.currentRoute = r;
        this.isAvailable = false;
        return true;
    }

    public synchronized void releaseFromRoute(Route r) {
        // only release if the same route or if null passed
        if (this.currentRoute == null) return;
        if (r == null || this.currentRoute == r) {
            this.currentRoute = null;
            this.isAvailable = true;
        }
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(Route currentRoute) {
        this.currentRoute = currentRoute;
    }

    public List<Shipment> getAssignedShipments() {
        return assignedShipments;
    }

    public void setAssignedShipments(List<Shipment> assignedShipments) {
        // unregister from previous shipments
        if (this.assignedShipments != null) {
            for (Shipment s : this.assignedShipments) {
                s.removeStatusListener(this);
            }
        }
        this.assignedShipments = assignedShipments != null ? new ArrayList<>(assignedShipments) : new ArrayList<>();
        // register for new shipments
        for (Shipment s : this.assignedShipments) {
            s.addStatusListener(this);
        }
    }

}
