
package managmentsystem.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import managmentsystem.vehicle_routing.Vehicle;

import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

// VehicleRepository handles all MongoDB operations for Vehicle objects.
public class VehicleRepository {
    private MongoCollection<Document> collection;

    public VehicleRepository() {
        this.collection = MongoDBConnection.getInstance().getDatabase().getCollection("vehicles");
    }

    // Saves a new vehicle to MongoDB
    public void createVehicle(Vehicle vehicle) {
        try {
            Document doc = new Document()
                .append("vehicleId", vehicle.getVehicleId())
                .append("vehicleType", vehicle.getVehicleType())
                .append("maxWeightCapacity", vehicle.getMaxWeightCapacity())
                .append("maxPackageCapacity", vehicle.getMaxPackageCapacity())
                .append("currentWeight", vehicle.getCurrentWeight())
                .append("currentPackageCount", vehicle.getCurrentPackageCount())
                .append("isAvailable", vehicle.isAvailable());

            collection.insertOne(doc);
            System.out.println("Vehicle saved to MongoDB: " + vehicle.getVehicleId());
        } catch (Exception e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    // Retrieves a vehicle by ID.
    public Vehicle getVehicleById(String vehicleId) {
        try {
            Document doc = collection.find(eq("vehicleId", vehicleId)).first();
            if (doc != null) {
                return mapDocumentToVehicle(doc);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving vehicle: " + e.getMessage());
        }
        return null;
    }

    //Retrieves all available vehicles.
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("isAvailable", true)).iterator();
            while (cursor.hasNext()) {
                vehicles.add(mapDocumentToVehicle(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error retrieving available vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    // Retrieves all vehicles.
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                vehicles.add(mapDocumentToVehicle(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error retrieving all vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    // Updates vehicle capacity information.
    public void updateVehicleCapacity(String vehicleId, double currentWeight, int currentPackageCount) {
        try {
            Document update = new Document("$set",
                new Document("currentWeight", currentWeight)
                    .append("currentPackageCount", currentPackageCount)
            );
            collection.updateOne(eq("vehicleId", vehicleId), update);
        } catch (Exception e) {
            System.out.println("Error updating vehicle capacity: " + e.getMessage());
        }
    }

    // Maps a MongoDB Document to a Vehicle object.
    private Vehicle mapDocumentToVehicle(Document doc) {
        return new Vehicle(
            doc.getString("vehicleId"),
            doc.getString("vehicleType"),
            doc.getDouble("maxWeightCapacity"),
            doc.getInteger("maxPackageCapacity")
        );
    }
}

