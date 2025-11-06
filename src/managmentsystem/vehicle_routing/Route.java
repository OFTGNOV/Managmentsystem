package managmentsystem.vehicle_routing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Route class represents a delivery route.
 * Contains multiple shipments and is assigned to a vehicle and driver.
 */
public class Route {
    private String routeId;
    private String vehicleId;
    private String driverId;
    private List<String> shipmentIds;
    private LocalDateTime routeDate;
    private RouteStatus status;

    public enum RouteStatus {
        ACTIVE, COMPLETED, CANCELLED
    }

    public Route(String routeId, String vehicleId, String driverId) {
        this.routeId = routeId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.shipmentIds = new ArrayList<>();
        this.routeDate = LocalDateTime.now();
        this.status = RouteStatus.ACTIVE;
    }

    public void addShipment(String shipmentId) {
        if (!shipmentIds.contains(shipmentId)) {
            shipmentIds.add(shipmentId);
        }
    }

    public void removeShipment(String shipmentId) {
        shipmentIds.remove(shipmentId);
    }

    public void completeRoute() {
        this.status = RouteStatus.COMPLETED;
    }

    // Getters
    public String getRouteId() { return routeId; }
    public String getVehicleId() { return vehicleId; }
    public String getDriverId() { return driverId; }
    public List<String> getShipmentIds() { return new ArrayList<>(shipmentIds); }
    public LocalDateTime getRouteDate() { return routeDate; }
    public RouteStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("Route #%s - %d Shipments - [%s]",
                routeId, shipmentIds.size(), status);
    }
}
