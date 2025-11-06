package managmentsystem.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import managmentsystem.user_customer.User;
import org.bson.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

//UserRepository handles all MongoDB operations for User objects.
public class UserRepository {
    private MongoCollection<Document> collection;

    public UserRepository() {
        this.collection = MongoDBConnection.getInstance().getDatabase().getCollection("users");
    }

    // Saves a new user to MongoDB.
    public void createUser(String userId, String name, String email, String password, String role) {
        try {
            Document doc = new Document()
                .append("userId", userId)
                .append("name", name)
                .append("email", email)
                .append("password", password) // Hash in production!
                .append("role", role)
                .append("createdDate", LocalDateTime.now());

            collection.insertOne(doc);
            System.out.println("User saved to MongoDB: " + userId);
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    //Retrieves a user by ID.
    public User getUserById(String userId) {
        try {
            Document doc = collection.find(eq("userId", userId)).first();
            if (doc != null) {
                return mapDocumentToUser(doc);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    // Authenticates a user by email and password.
    public User authenticateUser(String email, String password) {
        try {
            Document doc = collection.find(and(eq("email", email), eq("password", password))).first();
            if (doc != null) {
                return mapDocumentToUser(doc);
            }
        } catch (Exception e) {
            System.out.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    //Retrieves all users by role.
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("role", role)).iterator();
            while (cursor.hasNext()) {
                users.add(mapDocumentToUser(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error retrieving users by role: " + e.getMessage());
        }
        return users;
    }

    //Maps a MongoDB Document to a User object.
    private User mapDocumentToUser(Document doc) {
        return new User(
            doc.getString("userId"),
            doc.getString("name"),
            doc.getString("email"),
            doc.getString("password")
        );
    }
}