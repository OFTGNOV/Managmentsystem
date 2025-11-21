package databaseModule.bapDAO;

import billingAndPaymentModule.Invoice;
import databaseModule.DBHelper;
import databaseModule.sDAO.ShipmentDAO;
import databaseModule.uDAO.CustomerDAO;
import shipmentModule.Shipment;
import userModule.Customer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class InvoiceDAO {

    // Insert a new invoice record
    public static void insertInvoiceRecord(Invoice invoice) {
        String sql = "INSERT INTO invoice (invoiceNumber, shipment_trackingNumber, totalAmount, issueDate, dueDate, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, invoice.getInvoiceNumber());
            ps.setString(2, invoice.getShipment().getTrackingNumber());
            ps.setDouble(3, invoice.getTotalAmount());
            ps.setTimestamp(4, Timestamp.valueOf(invoice.getIssueDate()));
            ps.setTimestamp(5, Timestamp.valueOf(invoice.getDueDate()));
            ps.setString(6, invoice.getStatus().name());
            ps.setString(7, invoice.getNotes());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting invoice failed, no rows affected.");
                return;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    invoice.setId(keys.getInt(1));
                }
            }
            
            JOptionPane.showMessageDialog(null, "Invoice created successfully with number: " + invoice.getInvoiceNumber());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting invoice: " + e.getMessage());
        }
    }

    // Update an existing invoice record
    public static void updateInvoiceRecord(Invoice invoice) {
        String sql = "UPDATE invoice SET invoiceNumber = ?, shipment_trackingNumber = ?, totalAmount = ?, issueDate = ?, dueDate = ?, status = ?, notes = ? WHERE id = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getInvoiceNumber());
            ps.setString(2, invoice.getShipment().getTrackingNumber());
            ps.setDouble(3, invoice.getTotalAmount());
            ps.setTimestamp(4, Timestamp.valueOf(invoice.getIssueDate()));
            ps.setTimestamp(5, Timestamp.valueOf(invoice.getDueDate()));
            ps.setString(6, invoice.getStatus().name());
            ps.setString(7, invoice.getNotes());
            ps.setInt(8, invoice.getId());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Invoice updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No invoice found with the provided ID. Update did not modify any rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating invoice: " + e.getMessage());
        }
    }

    // Delete an invoice record
    public static void deleteInvoiceRecord(int invoiceId) {
        String sql = "DELETE FROM invoice WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Invoice deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No invoice found with the provided ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting invoice: " + e.getMessage());
        }
    }

    // Retrieve an invoice by ID
    public static Invoice retrieveInvoiceById(int invoiceId) {
        String sql = "SELECT id, invoiceNumber, shipment_trackingNumber, totalAmount, issueDate, dueDate, status, notes FROM invoice WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving invoice by ID: " + e.getMessage());
        }
        return null;
    }

    // Retrieve an invoice by invoice number
    public static Invoice retrieveInvoiceByNumber(String invoiceNumber) {
        String sql = "SELECT id, invoiceNumber, shipment_trackingNumber, totalAmount, issueDate, dueDate, status, notes FROM invoice WHERE invoiceNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNumber);
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
        int id = rs.getInt("id");
        String invoiceNumber = rs.getString("invoiceNumber");
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

        Invoice.InvoiceStatus status = Invoice.InvoiceStatus.PENDING; // default
        try {
            if (statusStr != null) {
                status = Invoice.InvoiceStatus.valueOf(statusStr);
            }
        } catch (IllegalArgumentException e) {
            // Keep default value
        }

        // Get associated payments using PaymentDAO
        List<billingAndPaymentModule.Payment> payments = PaymentDAO.retrievePaymentsByInvoiceId(id);

        Invoice invoice = new Invoice(id, invoiceNumber, shipment, sender, recipient, 
                    totalAmount, issueDate, dueDate, status, payments, notes);
        return invoice;
    }
}