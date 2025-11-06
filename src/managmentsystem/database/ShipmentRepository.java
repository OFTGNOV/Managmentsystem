package managmentsystem.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import managmentsystem.shipment.Shipment;
import org.bson.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import managmentsystem.shipment.ShipmentStatus;
import static com.mongodb.client.model.Filters.eq;

//ShipmentRepository handles all MongoDB operations for Shipment objects.
public class ShipmentRepository {
    private MongoCollection<Document> collection;

    //Constructor initializes the shipments collection.
    public ShipmentRepository() {
        this.collection = MongoDBConnection.getInstance().getDatabase().getCollection("shipments");
    }

    //Saves a new shipment to MongoDB.
    public void createShipment(Shipment shipment) {
        try {
            Document doc = new Document()
                .append("trackingNumber", shipment.getTrackingNumber())
                .append("senderId", shipment.getSenderId())
                .append("senderName", shipment.getSenderName())
                .append("senderAddress", shipment.getSenderAddress())
                .append("recipientName", shipment.getRecipientName())
                .append("recipientAddress", shipment.getRecipientAddress())
                .append("destinationZone", shipment.getDestinationZone())
                .append("weight", shipment.getWeight())
                .append("length", shipment.getLength())
                .append("width", shipment.getWidth())
                .append("height", shipment.getHeight())
                .append("packageType", shipment.getPackageType())
                .append("status", shipment.getStatus().toString())
                .append("shippingCost", shipment.getShippingCost())
                .append("createdDate", LocalDateTime.now())
                .append("deliveredDate", null);

            collection.insertOne(doc);
            System.out.println("Shipment saved to MongoDB: " + shipment.getTrackingNumber());
        } catch (Exception e) {
            System.out.println("Error saving shipment: " + e.getMessage());
        }
    }

    // Retrieves a shipment by tracking number.
    public Shipment getShipmentByTracking(String trackingNumber) {
        try {
            Document doc = collection.find(eq("trackingNumber", trackingNumber)).first();
            if (doc != null) {
                return mapDocumentToShipment(doc);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving shipment: " + e.getMessage());
        }
        return null;
    }

    //Retrieves all shipments for a customer.
    public List<Shipment> getShipmentsByCustomer(String customerId) {
        List<Shipment> shipments = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("senderId", customerId)).iterator();
            while (cursor.hasNext()) {
                shipments.add(mapDocumentToShipment(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error retrieving customer shipments: " + e.getMessage());
        }
        return shipments;
    }

    //Updates shipment status.
    public void updateShipmentStatus(String trackingNumber, ShipmentStatus status) {
        try {
            Document update = new Document("$set", 
                new Document("status", status.toString())
                    .append("deliveredDate", status == ShipmentStatus.DELIVERED ? LocalDateTime.now() : null)
            );
            collection.updateOne(eq("trackingNumber", trackingNumber), update);
            System.out.println("Shipment status updated: " + trackingNumber);
        } catch (Exception e) {
            System.out.println("Error updating shipment status: " + e.getMessage());
        }
    }

    //Retrieves all shipments.
    public List<Shipment> getAllShipments() {
        List<Shipment> shipments = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                shipments.add(mapDocumentToShipment(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error retrieving all shipments: " + e.getMessage());
        }
        return shipments;
    }

    //Maps a MongoDB Document to a Shipment object.
    private Shipment mapDocumentToShipment(Document doc) {
        Shipment shipment = new Shipment(
            doc.getString("trackingNumber"),
            doc.getString("senderId"),
            doc.getString("senderName"),
            doc.getString("senderAddress"),
            doc.getString("recipientName"),
            doc.getString("recipientAddress"),
            doc.getInteger("destinationZone"),
            doc.getDouble("weight"),
            doc.getDouble("length"),
            doc.getDouble("width"),
            doc.getDouble("height"),
            doc.getString("packageType")
        );
        shipment.updateStatus(ShipmentStatus.valueOf(doc.getString("status")));
        return shipment;
    }
}