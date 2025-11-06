#MongoDB Setup for Java Project "SmartShip Management System"

This guide walks through setting up MongoDB for the **SmartShip Management System** Java project — including database creation, collection setup, and Java integration.

---

##1. Database and Collections Setup

### Create the Database

Open your MongoDB shell (`mongosh`) and run:

```js
use smartship_db;
```

---

### Create Collections and Indexes

Run the following script to create all required collections and indexes:

```js
// Users collection
db.createCollection("users");
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "userId": 1 }, { unique: true });

// Shipments collection
db.createCollection("shipments");
db.shipments.createIndex({ "trackingNumber": 1 }, { unique: true });
db.shipments.createIndex({ "senderId": 1 });

// Vehicles collection
db.createCollection("vehicles");
db.vehicles.createIndex({ "vehicleId": 1 }, { unique: true });

// Invoices collection
db.createCollection("invoices");
db.invoices.createIndex({ "invoiceId": 1 }, { unique: true });
db.invoices.createIndex({ "shipmentId": 1 });
db.invoices.createIndex({ "customerId": 1 });

// Payments collection
db.createCollection("payments");
db.payments.createIndex({ "paymentId": 1 }, { unique: true });
db.payments.createIndex({ "invoiceId": 1 });

// Routes collection
db.createCollection("routes");
db.routes.createIndex({ "routeId": 1 }, { unique: true });

// Assignments collection
db.createCollection("assignments");
db.assignments.createIndex({ "assignmentId": 1 }, { unique: true });
db.assignments.createIndex({ "shipmentId": 1 });
```

Each collection has unique and reference indexes to improve query performance and maintain data integrity.

---

##2. MongoDB Connection Class

Create a new Java class named `MongoDBConnection.java` inside the `managmentsystem.database` package.

```java
package managmentsystem.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDBConnection manages the connection to MongoDB.
 * Uses singleton pattern to ensure only one connection instance exists.
 */
public class MongoDBConnection {
    private static MongoDBConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    // MongoDB configuration
    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "smartship_db";

    // Example for MongoDB Atlas (cloud):
    // private static final String MONGO_URI = "mongodb+srv://username:password@cluster.mongodb.net/?retryWrites=true&w=majority";

    private MongoDBConnection() {
        try {
            this.mongoClient = MongoClients.create(MONGO_URI);
            this.database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("MongoDB connected successfully!");
        } catch (Exception e) {
            System.out.println("MongoDB connection failed: " + e.getMessage());
        }
    }

    public static synchronized MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void closeConnection() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("MongoDB connection closed.");
            }
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
```

**Notes:**

- Uses the **Singleton pattern** to ensure one shared connection across your app.
- Works locally and can be adapted for **MongoDB Atlas**.
- Make sure MongoDB is running on `localhost:27017` (or update the URI).

---

##3. Shipment Repository (Data Access Layer)

This repository handles all CRUD operations for the `shipments` collection.

**Create a class:** `ShipmentRepository.java`  
**Location:** `managmentsystem.database`

```java
package managmentsystem.database;

import managmentsystem.Shipment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;

/**
 * ShipmentRepository handles all MongoDB operations for Shipment objects.
 */
public class ShipmentRepository {
    private MongoCollection<Document> collection;

    public ShipmentRepository() {
        this.collection = MongoDBConnection.getInstance().getDatabase().getCollection("shipments");
    }

    // Create
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

    // Read
    public Shipment getShipmentByTracking(String trackingNumber) {
        try {
            Document doc = collection.find(eq("trackingNumber", trackingNumber)).first();
            if (doc != null) return mapDocumentToShipment(doc);
        } catch (Exception e) {
            System.out.println("Error retrieving shipment: " + e.getMessage());
        }
        return null;
    }

    public List<Shipment> getShipmentsByCustomer(String customerId) {
        List<Shipment> shipments = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(eq("senderId", customerId)).iterator()) {
            while (cursor.hasNext()) {
                shipments.add(mapDocumentToShipment(cursor.next()));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving customer shipments: " + e.getMessage());
        }
        return shipments;
    }

    // Update
    public void updateShipmentStatus(String trackingNumber, Shipment.ShipmentStatus status) {
        try {
            Document update = new Document("$set",
                new Document("status", status.toString())
                    .append("deliveredDate", status == Shipment.ShipmentStatus.DELIVERED ? LocalDateTime.now() : null)
            );
            collection.updateOne(eq("trackingNumber", trackingNumber), update);
            System.out.println("Shipment status updated: " + trackingNumber);
        } catch (Exception e) {
            System.out.println("Error updating shipment status: " + e.getMessage());
        }
    }

    // Retrieve all
    public List<Shipment> getAllShipments() {
        List<Shipment> shipments = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                shipments.add(mapDocumentToShipment(cursor.next()));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving all shipments: " + e.getMessage());
        }
        return shipments;
    }

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
        shipment.updateStatus(Shipment.ShipmentStatus.valueOf(doc.getString("status")));
        return shipment;
    }
}
```

---

##4. Key Takeaways

| Component              | Description                                                    |
| ---------------------- | -------------------------------------------------------------- |
| **MongoDBConnection**  | Manages a single MongoDB client across the entire application. |
| **ShipmentRepository** | Handles CRUD operations for shipments.                         |
| **Indexes**            | Improve query speed and enforce uniqueness.                    |
| **LocalDateTime**      | Tracks creation and delivery timestamps.                       |

---

##5. Next Steps

- Add repositories for other collections (users, invoices, payments, etc.) following the same pattern.
- Implement unit tests using JUnit or TestNG.
- Integrate with your service layer for business logic.
- Optionally, migrate to **MongoDB Atlas** for production hosting.
