
package managmentsystem.database;

import static com.mongodb.client.model.Filters.eq;
import java.time.LocalDateTime;
import com.mongodb.client.MongoCollection;

/**
 * PaymentRepository handles all MongoDB operations for Payment objects.
 */
public class PaymentRepository {
    private MongoCollection<Document> collection;

    public PaymentRepository() {
        this.collection = MongoDBConnection.getInstance().getDatabase().getCollection("payments");
    }

    /**
     * Saves a new payment to MongoDB.
     */
    public void createPayment(String paymentId, String invoiceId, double amount, String paymentMethod, String status) {
        try {
            Document doc = new Document()
                .append("paymentId", paymentId)
                .append("invoiceId", invoiceId)
                .append("amount", amount)
                .append("paymentMethod", paymentMethod)
                .append("transactionStatus", status)
                .append("paymentDate", LocalDateTime.now());

            collection.insertOne(doc);
            System.out.println("Payment saved to MongoDB: " + paymentId);
        } catch (Exception e) {
            System.out.println("Error saving payment: " + e.getMessage());
        }
    }

    /**
     * Retrieves a payment by ID.
     */
    public Document getPaymentById(String paymentId) {
        try {
            return collection.find(eq("paymentId", paymentId)).first();
        } catch (Exception e) {
            System.out.println("Error retrieving payment: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates payment status.
     */
    public void updatePaymentStatus(String paymentId, String status) {
        try {
            Document update = new Document("$set", new Document("transactionStatus", status));
            collection.updateOne(eq("paymentId", paymentId), update);
        } catch (Exception e) {
            System.out.println("Error updating payment status: " + e.getMessage());
        }
    }
}
