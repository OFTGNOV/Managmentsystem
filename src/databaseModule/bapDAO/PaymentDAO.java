package databaseModule.bapDAO;

import billingAndPaymentModule.Payment;
import databaseModule.DBHelper;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PaymentDAO {

    // Insert a new payment record
    public static void insertPaymentRecord(Payment payment) {
        String sql = "INSERT INTO payment (invoice_id, amount, paymentDate, paymentMethod, status, referenceNumber) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.getInvoiceId());
            ps.setDouble(2, payment.getAmount());
            ps.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            ps.setString(4, payment.getPaymentMethod().name());
            ps.setString(5, payment.getStatus().name());
            ps.setString(6, payment.getReferenceNumber());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting payment failed, no rows affected.");
                return;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    payment.setId(keys.getInt(1));
                }
            }
            
            JOptionPane.showMessageDialog(null, "Payment processed successfully with ID: " + payment.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting payment: " + e.getMessage());
        }
    }

    // Update an existing payment record
    public static void updatePaymentRecord(Payment payment) {
        String sql = "UPDATE payment SET invoice_id = ?, amount = ?, paymentDate = ?, paymentMethod = ?, status = ?, referenceNumber = ? WHERE id = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payment.getInvoiceId());
            ps.setDouble(2, payment.getAmount());
            ps.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            ps.setString(4, payment.getPaymentMethod().name());
            ps.setString(5, payment.getStatus().name());
            ps.setString(6, payment.getReferenceNumber());
            ps.setInt(7, payment.getId());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Payment updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No payment found with the provided ID. Update did not modify any rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating payment: " + e.getMessage());
        }
    }

    // Delete a payment record
    public static void deletePaymentRecord(int paymentId) {
        String sql = "DELETE FROM payment WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Payment deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No payment found with the provided ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting payment: " + e.getMessage());
        }
    }

    // Retrieve a payment by ID
    public static Payment retrievePaymentById(int paymentId) {
        String sql = "SELECT id, invoice_id, amount, paymentDate, paymentMethod, status, referenceNumber FROM payment WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving payment by ID: " + e.getMessage());
        }
        return null;
    }

    // Retrieve all payments for a specific invoice
    public static List<Payment> retrievePaymentsByInvoiceId(int invoiceId) {
        String sql = "SELECT id, invoice_id, amount, paymentDate, paymentMethod, status, referenceNumber FROM payment WHERE invoice_id = ?";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving payments for invoice: " + e.getMessage());
        }
        return payments;
    }

    // Get all payments
    public static List<Payment> readAllPayments() {
        String sql = "SELECT id, invoice_id, amount, paymentDate, paymentMethod, status, referenceNumber FROM payment";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // Helper method to map ResultSet to Payment object
    private static Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int invoiceId = rs.getInt("invoice_id");
        double amount = rs.getDouble("amount");
        Timestamp paymentDateTs = rs.getTimestamp("paymentDate");
        LocalDateTime paymentDate = paymentDateTs != null ? paymentDateTs.toLocalDateTime() : null;
        String paymentMethodStr = rs.getString("paymentMethod");
        String statusStr = rs.getString("status");
        String referenceNumber = rs.getString("referenceNumber");

        Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.CARD; // default
        try {
            if (paymentMethodStr != null) {
                paymentMethod = Payment.PaymentMethod.valueOf(paymentMethodStr);
            }
        } catch (IllegalArgumentException e) {
            // Keep default value
        }

        PaymentStatus paymentStatus = PaymentStatus.PENDING; // default
        try {
            if (statusStr != null) {
                paymentStatus = PaymentStatus.valueOf(statusStr);
            }
        } catch (IllegalArgumentException e) {
            // Keep default value
        }

        Payment payment = new Payment(id, invoiceId, amount, paymentDate, paymentMethod, paymentStatus, referenceNumber);
        return payment;
    }
}