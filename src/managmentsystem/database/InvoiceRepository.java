
package managmentsystem.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import managmentsystem.billing_payment.PaymentStatus;
import managmentsystem.billing_payment.Invoice;
import org.bson.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

//InvoiceRepository handles all MongoDB operations for Invoice objects.
public class InvoiceRepository {
    private MongoCollection<Document> collection;

    public InvoiceRepository() {
        this.collection = MongoDBConnection.getInstance().getDatabase().getCollection("invoices");
    }

    /**
     * Saves a new invoice to MongoDB.
     */
    public void createInvoice(Invoice invoice) {
        try {
            Document doc = new Document()
                .append("invoiceId", invoice.getInvoiceId())
                .append("shipmentId", invoice.getShipmentId())
                .append("customerId", invoice.getCustomerId())
                .append("baseAmount", invoice.getBaseAmount())
                .append("discount", invoice.getDiscount())
                .append("surcharge", invoice.getSurcharge())
                .append("totalAmount", invoice.getTotalAmount())
                .append("paymentStatus", invoice.getPaymentStatus().toString())
                .append("invoiceDate", LocalDateTime.now());

            collection.insertOne(doc);
            System.out.println("Invoice saved to MongoDB: " + invoice.getInvoiceId());
        } catch (Exception e) {
            System.out.println("Error saving invoice: " + e.getMessage());
        }
    }

    /**
     * Retrieves an invoice by ID.
     */
    public Invoice getInvoiceById(String invoiceId) {
        try {
            Document doc = collection.find(eq("invoiceId", invoiceId)).first();
            if (doc != null) {
                return mapDocumentToInvoice(doc);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving invoice: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all invoices for a customer.
     */
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        List<Invoice> invoices = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("customerId", customerId)).iterator();
            while (cursor.hasNext()) {
                invoices.add(mapDocumentToInvoice(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error retrieving customer invoices: " + e.getMessage());
        }
        return invoices;
    }

    //Updates invoice payment status.
    public void updatePaymentStatus(String invoiceId, PaymentStatus status) {
        try {
            Document update = new Document("$set", new Document("paymentStatus", status.toString()));
            collection.updateOne(eq("invoiceId", invoiceId), update);
        } catch (Exception e) {
            System.out.println("Error updating payment status: " + e.getMessage());
        }
    }

    // Maps a MongoDB Document to an Invoice object.
    private Invoice mapDocumentToInvoice(Document doc) {
        Invoice invoice = new Invoice(
            doc.getString("invoiceId"),
            doc.getString("shipmentId"),
            doc.getString("customerId"),
            doc.getDouble("baseAmount")
        );
        return invoice;
    }
}

