package databaseModule.bapDAO;

import billingAndPaymentModule.Invoice;
import billingAndPaymentModule.InvoiceStatus;
import billingAndPaymentModule.Payment;
import databaseModule.DBHelper;
import databaseModule.sDAO.ShipmentDAO;
import shipmentModule.Shipment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class InvoiceDAO {

    // Insert Invoice Record with parameters
    public static void insertInvoiceRecord(Shipment shipment, int senderId, int recipentId,
            double totalAmount, LocalDateTime issueDate, LocalDateTime dueDate,
            InvoiceStatus status, String notes) {
    	// Create Invoice object
       String invoiceNum = generateInvoiceNumber();
       Invoice invoice = new Invoice(invoiceNum, shipment, senderId, recipentId, totalAmount, issueDate, dueDate,
    		   status, notes);
       insertInvoiceRecord(invoice); // Call the method to insert Invoice object
    }

    // Insert a new invoice record
    public static void insertInvoiceRecord(Invoice invoice) {
        String sql = "INSERT INTO invoice (invoiceNum, shipment_trackingNumber, senderId, recipentId, totalAmount, issueDate, dueDate, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getInvoiceNum());
            ps.setString(2, invoice.getShipment().getTrackingNumber());
            ps.setInt(3, invoice.getSenderId());
            ps.setInt(4, invoice.getRecipentId());
            ps.setDouble(5, invoice.getTotalAmount());
            ps.setTimestamp(6, invoice.getIssueDate() == null ? null : Timestamp.valueOf(invoice.getIssueDate()));
            ps.setTimestamp(7, invoice.getDueDate() == null ? null : Timestamp.valueOf(invoice.getDueDate()));
            ps.setString(8, invoice.getStatus().name());
            ps.setString(9, invoice.getNotes());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting invoice failed, no rows affected.");
                return;
            }

            JOptionPane.showMessageDialog(null, "Invoice created successfully with number: " + invoice.getInvoiceNum());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting invoice: " + e.getMessage());
        }
    }

    // Update an existing invoice record
    public static void updateInvoiceRecord(Invoice invoice) {
        String sql = "UPDATE invoice SET shipment_trackingNumber = ?, senderId = ?, recipentId = ?, totalAmount = ?, issueDate = ?, dueDate = ?, status = ?, notes = ? WHERE invoiceNum = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getShipment().getTrackingNumber());
            ps.setInt(2, invoice.getSenderId());
            ps.setInt(3, invoice.getRecipentId());
            ps.setDouble(4, invoice.getTotalAmount());
            ps.setTimestamp(5, Timestamp.valueOf(invoice.getIssueDate()));
            ps.setTimestamp(6, Timestamp.valueOf(invoice.getDueDate()));
            ps.setString(7, invoice.getStatus().name());
            ps.setString(8, invoice.getNotes());
            ps.setString(9, invoice.getInvoiceNum());

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
        String sql = "DELETE FROM invoice WHERE invoiceNum = ?";
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
        String sql = "SELECT invoiceNum, shipment_trackingNumber, senderId, recipentId, totalAmount, issueDate, dueDate, status, notes FROM invoice WHERE invoiceNum = ?";
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
        String sql = "SELECT invoiceNum, shipment_trackingNumber, senderId, recipentId, totalAmount, issueDate, dueDate, status, notes FROM invoice";
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
   public static List<Payment> retrievePaymentsByInvoiceNumber(String invoiceNum) {
        String sql = "SELECT paymentId, amount, paymentDate, paymentMethod, status, referenceNumber, invoiceNum FROM payment WHERE invoiceNum = ?";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNum);
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
        String sql = "SELECT invoiceNum, shipment_trackingNumber, senderId, recipentId, totalAmount, issueDate, dueDate, status, notes FROM invoice WHERE shipment_trackingNumber = ?";
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
        String invoiceNum = rs.getString("invoiceNum");
        String shipmentTrackingNumber = rs.getString("shipment_trackingNumber");
        int senderId = rs.getInt("senderId");
        int recipentId = rs.getInt("recipentId");
        double totalAmount = rs.getDouble("totalAmount");
        Timestamp issueDateTs = rs.getTimestamp("issueDate");
        Timestamp dueDateTs = rs.getTimestamp("dueDate");
        String statusStr = rs.getString("status");
        String notes = rs.getString("notes");

        LocalDateTime issueDate = issueDateTs != null ? issueDateTs.toLocalDateTime() : null;
        LocalDateTime dueDate = dueDateTs != null ? dueDateTs.toLocalDateTime() : null;

        // Get shipment details using ShipmentDAO
        Shipment shipment = ShipmentDAO.retrieveShipmentByTrackingNumber(shipmentTrackingNumber);

        InvoiceStatus status = InvoiceStatus.PENDING; // default
        try {
            if (statusStr != null) {
                status = InvoiceStatus.valueOf(statusStr);
            }
        } catch (IllegalArgumentException e) {
            // Keep default value
        }

        // Get associated payments using local helper by invoiceNum
        List<Payment> payments = retrievePaymentsByInvoiceNumber(invoiceNum);

        Invoice invoice = new Invoice(invoiceNum, shipment, senderId, recipentId,
                    totalAmount, issueDate, dueDate, status, notes);
        invoice.setPayments(payments); // Set payments list
        return invoice;
    }

    // Helper method to generate an invoice number
    private static String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}