package vehicleAndRoutingModule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import shipmentModule.Shipment;
import userModule.Driver;

/**
 * Route class represents a delivery route.
 * Tracks assigned vehicle, driver, shipments, and status.
 * The routes themselves  are just tell where teh vechic
 */
public class Route {
    private int routeNum;
    private List<Vehicle> vehicles;
    private List<Shipment> shipments; //All shipments assigned to this route
    private LocalDateTime startDate; //indicates when driver and vehicle started route
    private LocalDateTime endDate; //indicates when driver and vehicle ended route


    public Route( List<Vehicle> vehicles, List<Driver> drivers, List<Shipment> shipments) {
        this.routeNum = routeNum;
        this.vehicles = new ArrayList<>(vehicles);
        this.shipments = new ArrayList<>(shipments);
        this.startDate = LocalDateTime.now();
    }

    public void addShipment(Shipment shipment) {
        if (!shipments.contains(shipment)) {
            shipments.add(shipment);
        }
    }

    public void removeShipment(Shipment shipment) {
        shipments.remove(shipment);
    }
}
