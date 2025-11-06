package managmentsystem.vehicle_routing;

import managmentsystem.database.RouteRepository;
import org.bson.Document;
import java.util.List;

/**
 * RouteManager handles delivery route creation and management using MongoDB.
 */
public class RouteManager {
    private RouteRepository routeRepository;
    private static int routeCounter = 1;

    /**
     * Constructor initializes the route repository.
     */
    public RouteManager() {
        this.routeRepository = new RouteRepository();
    }

    /**
     * Creates a new delivery route and returns the route ID.
     */
    public String createRoute(String vehicleId, String driverId) {
        String routeId = "RT" + String.format("%05d", ++routeCounter);
        routeRepository.createRoute(routeId, vehicleId, driverId);
        System.out.println("Route created: " + routeId);
        return routeId;
    }

    /**
     * Retrieves a route document by ID.
     */
    public Document getRoute(String routeId) {
        Document route = routeRepository.getRouteById(routeId);
        if (route == null) {
            System.out.println("Route not found: " + routeId);
            return null;
        }
        return route;
    }

    /**
     * Adds a shipment to a route.
     */
    public void addShipmentToRoute(String routeId, String shipmentId) {
        try {
            routeRepository.addShipmentToRoute(routeId, shipmentId);
            System.out.println("Shipment " + shipmentId + " added to route " + routeId);
        } catch (Exception e) {
            System.out.println("Error adding shipment to route: " + e.getMessage());
        }
    }

    /**
     * Retrieves all routes for a specific driver as Document list.
     */
    public List<Document> getDriverRoutes(String driverId) {
        return routeRepository.getDriverRoutes(driverId);
    }

    /**
     * Gets route count for a driver (helper method).
     */
    public int getDriverRouteCount(String driverId) {
        return getDriverRoutes(driverId).size();
    }

    /**
     * Completes a route by updating its status.
     */
    public void completeRoute(String routeId) {
        try {
            routeRepository.completeRoute(routeId);
            System.out.println("Route " + routeId + " marked as completed");
        } catch (Exception e) {
            System.out.println("Error completing route: " + e.getMessage());
        }
    }
}