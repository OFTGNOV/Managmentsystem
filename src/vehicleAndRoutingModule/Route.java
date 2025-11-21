package vehicleAndRoutingModule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import shipmentModule.Shipment;
import shipmentModule.ShipmentStatus;
import userModule.Driver;

/**
 * Route class represents a delivery route.
 * It stores the vehicle that runs the route, the zone the vehicle is in for the route,
 * the list of shipments planned for this route (kept even after deliveries complete),
 * and the start/end times of the route.
 */
public class Route {
    private String routeNum; // unique route identifier
    private Vehicle vehicle; // vehicle assigned to this route
    private int zone = -1; // route zone (-1 = unset)
    private final List<Shipment> shipments;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Route(Vehicle vehicle) {
        this.routeNum = generateRouteNumber();
        this.vehicle = vehicle;
        this.shipments = new ArrayList<>();
        this.startTime = null;
        this.endTime = null;
        // If vehicle already has assigned shipments, adopt their zones/shipping list
        if (vehicle != null && vehicle.getAssignedShipments() != null && !vehicle.getAssignedShipments().isEmpty()) {
            // adopt shipments into route (route should reflect planned deliveries)
            for (Shipment s : vehicle.getAssignedShipments()) {
                if (s != null && !shipments.contains(s)) shipments.add(s);
            }
            // determine zone from first shipment if not already set
            if (!shipments.isEmpty()) {
                int z = shipments.get(0).getRecipent().getZone();
                this.zone = z;
            }
        }
    }

    // Simple route number generator (could be improved to ensure uniqueness)
    private String generateRouteNumber() {
    	//Generates a random route number from 0 to 10000
    	return "ROUTE-" + (int)(Math.random() * 10000);
    }

    // Copy constructor
    public Route(Route other) {
        this.routeNum = other.routeNum;
        this.vehicle = other.vehicle;
        this.zone = other.zone;
        this.shipments = new ArrayList<>(other.shipments);
        this.startTime = other.startTime;
        this.endTime = other.endTime;
    }

    /**
     * Add a shipment to the route's planned list.
     * The route's zone will be set by the first shipment added.
     * Only shipments matching the route zone are allowed.
     * If a vehicle is assigned, we will also attempt to assign the shipment to the vehicle.
     * Returns true if the shipment was added to the route's planned list.
     */
    public boolean addShipment(Shipment s) {
        if (s == null) return false;
        // Do not allow adding shipments from a different zone
        int sZone = s.getRecipent().getZone();
        if (this.zone == -1) {
            // first added shipment determines the route's zone
            this.zone = sZone;
        } else if (this.zone != sZone) {
            return false; // different zone, reject
        }

        if (!shipments.contains(s)) {
            shipments.add(s);
        }

        // Attempt to assign to vehicle if present and not already assigned
        if (vehicle != null) {
            // If shipment not delivered and vehicle can take it, assign it
            if (s.getStatus() != ShipmentStatus.DELIVERED && !vehicle.getAssignedShipments().contains(s)) {
                vehicle.assignShipment(s); // assignShipment will return false if capacity prevents it
            }
        }

        return true;
    }

    //Remove a shipment from the route (cancels it from this route). Also unassigns from vehicle.
    public boolean removeShipment(Shipment s) {
        if (s == null) return false;
        boolean removed = shipments.remove(s);
        if (vehicle != null) {
            vehicle.unassignShipment(s);
        }
        return removed;
    }

    // Internal start logic extracted so both tryStartRoute and startRoute can use it
    private void doStart() {
        this.startTime = LocalDateTime.now();

        if (vehicle != null) {
            for (Shipment s : new ArrayList<>(shipments)) {
                if (s == null) continue;
                if (s.getStatus() == ShipmentStatus.DELIVERED) continue; // already delivered
                int sZone = s.getRecipent().getZone();
                if (this.zone == -1) {
                    this.zone = sZone; // fallback
                }
                if (sZone == this.zone) {
                    // try to assign; failures (capacity) will leave shipment planned but unassigned
                    if (!vehicle.getAssignedShipments().contains(s)) {
                        vehicle.assignShipment(s);
                    }
                }
            }
        }
    }

    /**
     * Attempt to start the route. Returns true if the route started successfully.
     * The route will only start when the assigned vehicle is available and can be reserved.
     */
    public boolean tryStartRoute() {
        if (vehicle == null) return false;
        // Attempt to reserve the vehicle atomically
        boolean reserved = vehicle.assignToRoute(this);
        if (!reserved) return false;
        // vehicle reserved; perform start logic
        doStart();
        return true;
    }

    //Start the route; throws IllegalStateException if the vehicle isn't available.
    public void startRoute() {
        boolean started = tryStartRoute();
        if (!started) {
            throw new IllegalStateException("Cannot start route: assigned vehicle is not available.");
        }
    }

    //End the route: record end time and release vehicle reservation.
    public void endRoute() {
        this.endTime = LocalDateTime.now();
        if (vehicle != null) vehicle.releaseFromRoute(this);
    }

    // Getters & setters
    public String getRouteNum() { 
    	return routeNum; 
    }
    
    public void setRouteNum(String routeNum) { 
    	this.routeNum = routeNum; 
    }

    public Vehicle getVehicle() { return vehicle; }

    public void setVehicle(Vehicle vehicle) {
        // Unassign shipments from previous vehicle
        if (this.vehicle != null) {
            for (Shipment s : new ArrayList<>(this.vehicle.getAssignedShipments())) {
                // only unassign shipments that belong to this route
                if (this.shipments.contains(s)) {
                    this.vehicle.unassignShipment(s);
                }
            }
            // if previous vehicle was reserved for this route, release it
            this.vehicle.releaseFromRoute(this);
        }

        this.vehicle = vehicle;

        // Attempt to assign planned shipments to the new vehicle
        if (this.vehicle != null) {
            for (Shipment s : new ArrayList<>(shipments)) {
                if (s == null) continue;
                if (s.getStatus() == ShipmentStatus.DELIVERED) continue;
                int sZone = s.getRecipent().getZone();
                if (this.zone != -1 && sZone != this.zone) continue; // mismatch
                if (!this.vehicle.getAssignedShipments().contains(s)) {
                    this.vehicle.assignShipment(s);
                }
            }
        }
    }
    
    

    public void setZone(int zone) {
		this.zone = zone;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public int getZone() {
    	return zone;
    }
    
    public List<Shipment> getShipments() {
    	return shipments;
    }
    
    public LocalDateTime getStartTime() {
    	return startTime;
    }
    
    public LocalDateTime getEndTime() {
    	return endTime;
    }

    /**
     * Count of planned shipments (keeps delivered ones as well).
     */
    public int getPlannedShipmentCount() {
    	return shipments.size();
    }

    /** Total planned weight for the route (includes delivered shipments) */
    public double getTotalPlannedWeight() {
        double total = 0.0;
        for (Shipment s : shipments) if (s != null) total += s.getWeight();
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route #").append(routeNum).append(" (Zone: ").append(zone).append(")\n");
        if (vehicle != null && vehicle.getAssignedDriver() != null) {
            Driver d = vehicle.getAssignedDriver();
            sb.append("Driver: ").append(d.getdln()).append("\n");
        }
        sb.append("Planned Shipments: ").append(getPlannedShipmentCount()).append("\n");
        sb.append("Total Planned Weight: ").append(getTotalPlannedWeight()).append(" kg\n");
        sb.append("Start: ").append(startTime).append(" End: ").append(endTime);
        return sb.toString();
    }

}