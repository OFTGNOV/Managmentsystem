package databaseModule.bapDAO;

import billingAndPaymentModule.Invoice;
import billingAndPaymentModule.InvoiceStatus;
import billingAndPaymentModule.Payment;
import databaseModule.DBHelper;
import databaseModule.sDAO.ShipmentDAO;
import shipmentModule.Shipment;
import userModule.Customer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class InvoiceDAO {

    // Insert Invoice Record with parameters
    public static void insertInvoiceRecord(Shipment shipment, Customer sender, Customer recipient,
            double totalAmount, LocalDateTime issueDate, LocalDateTime dueDate, 
            InvoiceStatus status, List<Payment> payments, String notes) {
    	// Create Invoice object
       Invoice invoice = new Invoice(shipment, sender, recipient,totalAmount, issueDate, dueDate, 
    		   status, payments, notes);
       insertInvoiceRecord(invoice); // Call the method to insert Invoice object
    }

    // Insert a new invoice record
    public static void insertInvoiceRecord(Invoice invoice) {
        String sql = "INSERT INTO invoice (invoiceNumber, shipment_trackingNumber, totalAmount, issueDate, dueDate, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, invoice.getInvoiceNum());
            ps.setString(2, invoice.getShipment().getTrackingNumber());
            ps.setDouble(3, invoice.getTotalAmount());
            ps.setTimestamp(4, invoice.getIssueDate() == null ? null : Timestamp.valueOf(invoice.getIssueDate()));
            ps.setTimestamp(5, invoice.getDueDate() == null ? null : Timestamp.valueOf(invoice.getDueDate()));
            ps.setString(6, invoice.getStatus().name());
            ps.setString(7, invoice.getNotes());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting invoice failed, no rows affected.");
                return;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    // Since Invoice doesn't have an id field anymore, we can't set it
                    // The invoice number is used as the identifier now
                }
            }

            JOptionPane.showMessageDialog(null, "Invoice created successfully with number: " + invoice.getInvoiceNum());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting invoice: " + e.getMessage());
        }
    }

    // Update an existing invoice record
    public static void updateInvoiceRecord(Invoice invoice) {
        String sql = "UPDATE invoice SET invoiceNumber = ?, shipment_trackingNumber = ?, totalAmount = ?, issueDate = ?, dueDate = ?, status = ?, notes = ? WHERE invoiceNumber = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getInvoiceNum());
            ps.setString(2, invoice.getShipment().getTrackingNumber());
            ps.setDouble(3, invoice.getTotalAmount());
            ps.setTimestamp(4, Timestamp.valueOf(invoice.getIssueDate()));
            ps.setTimestamp(5, Timestamp.valueOf(invoice.getDueDate()));
            ps.setString(6, invoice.getStatus().name());
            ps.setString(7, invoice.getNotes());
            ps.setString(8, invoice.getInvoiceNum());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Invoice updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No invoice found with the provided invoice number. Update did not modify any rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating invoice: " + e.getMessage());
        }
    }

    // Delete an invoice record
    public static void deleteInvoiceRecord(String invoiceNum) {
        String sql = "DELETE FROM invoice WHERE invoiceNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNum);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Invoice deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No invoice found with the provided invoice number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting invoice: " + e.getMessage());
        }
    }

    // Retrieve an invoice by invoice number
    public static Invoice retrieveInvoiceByNumber(String invoiceNum) {
        String sql = "SELECT id, invoiceNumber, shipment_trackingNumber, totalAmount, issueDate, dueDate, status, notes FROM invoice WHERE invoiceNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving invoice by number: " + e.getMessage());
        }
        return null;
    }

    // Get all invoices
    public static List<Invoice> readAllInvoices() {
        String sql = "SELECT id, invoiceNumber, shipment_trackingNumber, totalAmount, issueDate, dueDate, status, notes FROM invoice";
        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }
    
 // Retrieve all payments for a specific invoice
   public static List<Payment> retrievePaymentsByInvoiceNumber(String invoiceNumber) {
        String sql = "SELECT id, amount, paymentDate, paymentMethod, status, referenceNumber FROM payment WHERE invoiceNumber = ?";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payments.add(PaymentDAO.mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving payments for invoice: " + e.getMessage());
        }
        return payments;
    }

    // Get all invoices for a specific shipment
    public static List<Invoice> retrieveInvoicesByShipment(String trackingNumber) {
        String sql = "SELECT id, invoiceNumber, shipment_trackingNumber, totalAmount, issueDate, dueDate, status, notes FROM invoice WHERE shipment_trackingNumber = ?";
        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving invoices for shipment: " + e.getMessage());
        }
        return invoices;
    }

    // Helper method to map ResultSet to Invoice object
    private static Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        String invoiceNum = rs.getString("invoiceNumber");
        String shipmentTrackingNumber = rs.getString("shipment_trackingNumber");
        double totalAmount = rs.getDouble("totalAmount");
        Timestamp issueDateTs = rs.getTimestamp("issueDate");
        Timestamp dueDateTs = rs.getTimestamp("dueDate");
        String statusStr = rs.getString("status");
        String notes = rs.getString("notes");

        LocalDateTime issueDate = issueDateTs != null ? issueDateTs.toLocalDateTime() : null;
        LocalDateTime dueDate = dueDateTs != null ? dueDateTs.toLocalDateTime() : null;

        // Get shipment details using ShipmentDAO
        Shipment shipment = ShipmentDAO.retrieveShipmentByTrackingNumber(shipmentTrackingNumber);
        Customer sender = shipment != null ? shipment.getSender() : null;
        Customer recipient = shipment != null ? shipment.getRecipent() : null;

        InvoiceStatus status = InvoiceStatus.PENDING; // default
        try {
            if (statusStr != null) {
                status = InvoiceStatus.valueOf(statusStr);
            }
        } catch (IllegalArgumentException e) {
            // Keep default value
        }

        // Get associated payments using local helper by invoiceNumber
        List<Payment> payments = retrievePaymentsByInvoiceNumber(invoiceNum);

        Invoice invoice = new Invoice(shipment, sender, recipient,
                    totalAmount, issueDate, dueDate, status, payments, notes);
        return invoice;
    }

}