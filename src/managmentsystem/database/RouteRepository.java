package managmentsystem.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import managmentsystem.vehicle_routing.Route;

import org.bson.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * RouteRepository handles all MongoDB operations for Route objects.
 */
public class RouteRepository {
    private MongoCollection<Document> collection;

    public RouteRepository() {
        this.collection = MongoDBConnection.getInstance().getDatabase().getCollection("routes");
    }

    /**
     * Saves a new route to MongoDB.
     */
    public void createRoute(String routeId, String vehicleId, String driverId) {
        try {
            Document doc = new Document()
                .append("routeId", routeId)
                .append("vehicleId", vehicleId)
                .append("driverId", driverId)
                .append("shipmentIds", new ArrayList<>())
                .append("status", "ACTIVE")
                .append("routeDate", LocalDateTime.now());

            collection.insertOne(doc);
            System.out.println("Route saved to MongoDB: " + routeId);
        } catch (Exception e) {
            System.out.println("Error saving route: " + e.getMessage());
        }
    }

    /**
     * Retrieves a route by ID.
     */
    public Document getRouteById(String routeId) {
        try {
            return collection.find(eq("routeId", routeId)).first();
        } catch (Exception e) {
            System.out.println("Error retrieving route: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all routes for a driver.
     */
    public List<Document> getDriverRoutes(String driverId) {
        List<Document> routes = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("driverId", driverId)).iterator();
            while (cursor.hasNext()) {
                routes.add(cursor.next());
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error retrieving driver routes: " + e.getMessage());
        }
        return routes;
    }

    /**
     * Adds a shipment to a route.
     */
    public void addShipmentToRoute(String routeId, String shipmentId) {
        try {
            Document update = new Document("$push", new Document("shipmentIds", shipmentId));
            collection.updateOne(eq("routeId", routeId), update);
        } catch (Exception e) {
            System.out.println("Error adding shipment to route: " + e.getMessage());
        }
    }

    /**
     * Completes a route.
     */
    public void completeRoute(String routeId) {
        try {
            Document update = new Document("$set", new Document("status", "COMPLETED"));
            collection.updateOne(eq("routeId", routeId), update);
        } catch (Exception e) {
            System.out.println("Error completing route: " + e.getMessage());
        }
    }
}

