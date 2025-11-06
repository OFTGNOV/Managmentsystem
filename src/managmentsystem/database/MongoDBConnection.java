package managmentsystem.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDBConnection manages the connection to MongoDB.
 * Uses singleton pattern to ensure only one connection is created.
 */
public class MongoDBConnection {
    private static MongoDBConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private static final String MONGO_URI = "mongodb+srv://localhost/?authMechanism=MONGODB-X509&authSource=%24external";
    private static final String DATABASE_NAME = "smartship_db";
    
    // Private constructor to prevent instantiation.
    private MongoDBConnection() {
        try {
            // Create MongoClient
            this.mongoClient = MongoClients.create(MONGO_URI);

            // Get database
            this.database = mongoClient.getDatabase(DATABASE_NAME);

            System.out.println("MongoDB connected successfully!");
        } catch (Exception e) {
            System.out.println("MongoDB connection failed: " + e.getMessage());
        }
    }

    //Gets singleton instance of MongoDBConnection.
    public static synchronized MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    //Gets the MongoDB database instance.
    public MongoDatabase getDatabase() {
        return database;
    }

    // Closes the MongoDB connection.
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