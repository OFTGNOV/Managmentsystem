package shipmentModule;

import java.time.LocalDateTime;

//Assignment class represents the assignment of a shipment to a vehicle.
public class Assignment {
    private String assignmentId;
    private String shipmentId;
    private String vehicleId;
    private LocalDateTime assignmentDate;
    private String routeId;

    public Assignment(String assignmentId, String shipmentId, String vehicleId, String routeId) {
        this.assignmentId = assignmentId;
        this.shipmentId = shipmentId;
        this.vehicleId = vehicleId;
        this.routeId = routeId;
        this.assignmentDate = LocalDateTime.now();
    }

    // Getters
    public String getAssignmentId() { return assignmentId; }
    public String getShipmentId() { return shipmentId; }
    public String getVehicleId() { return vehicleId; }
    public String getRouteId() { return routeId; }
    public LocalDateTime getAssignmentDate() { return assignmentDate; }

    @Override
    public String toString() {
        return String.format("Assignment #%s: Shipment %s → Vehicle %s on Route %s",
                assignmentId, shipmentId, vehicleId, routeId);
    }
}
