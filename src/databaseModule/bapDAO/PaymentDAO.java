package databaseModule.bapDAO;

import billingAndPaymentModule.Payment;
import billingAndPaymentModule.PaymentStatus;
import billingAndPaymentModule.PaymentMethod;
import databaseModule.DBHelper;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PaymentDAO {
    // Insert a new payment record
    public static void insertPaymentRecord(Payment payment) {
        String sql = "INSERT INTO payment (amount, paymentDate, paymentMethod, status, referenceNumber, invoiceID) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, payment.getAmount());
            ps.setTimestamp(2, payment.getPaymentDate() == null ? null : Timestamp.valueOf(payment.getPaymentDate()));
            ps.setString(3, payment.getPaymentMethod() == null ? billingAndPaymentModule.PaymentMethod.CARD.name() : payment.getPaymentMethod().name());
            ps.setString(4, payment.getStatus() == null ? PaymentStatus.PENDING.name() : payment.getStatus().name());
            ps.setString(5, payment.getReferenceNumber());
            ps.setInt(6, payment.getInvoiceID());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting payment failed, no rows affected.");
                return;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    payment.setPaymentId(keys.getInt(1));
                }
            }

            JOptionPane.showMessageDialog(null, "Payment processed successfully with ID: " + payment.getPaymentId());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting payment: " + e.getMessage());
        }
    }

    // Update an existing payment record
    public static void updatePaymentRecord(Payment payment) {
        String sql = "UPDATE payment SET amount = ?, paymentDate = ?, paymentMethod = ?, status = ?, referenceNumber = ?, invoiceID = ? WHERE paymentId = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // set parameters in correct order (1-based)
            ps.setDouble(1, payment.getAmount());
            ps.setTimestamp(2, payment.getPaymentDate() == null ? null : Timestamp.valueOf(payment.getPaymentDate()));
            ps.setString(3, payment.getPaymentMethod() == null ? PaymentMethod.CARD.name() : payment.getPaymentMethod().name());
            ps.setString(4, payment.getStatus() == null ? PaymentStatus.PENDING.name() : payment.getStatus().name());
            ps.setString(5, payment.getReferenceNumber());
            ps.setInt(6, payment.getInvoiceID());
            ps.setInt(7, payment.getPaymentId());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Payment updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No payment found with the provided payment ID. Update did not modify any rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating payment: " + e.getMessage());
        }
    }

    // Delete a payment record
    public static void deletePaymentRecord(int paymentId) {
        String sql = "DELETE FROM payment WHERE paymentId = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Payment deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No payment found with the provided Payment ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting payment: " + e.getMessage());
        }
    }

    // Get all payments
    public static List<Payment> readAllPayments() {
        String sql = "SELECT paymentId, amount, paymentDate, paymentMethod, status, referenceNumber, invoiceID FROM payment";
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
    public static Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        int paymentId = rs.getInt("paymentId");
        double amount = rs.getDouble("amount");
        Timestamp paymentDateTs = rs.getTimestamp("paymentDate");
        LocalDateTime paymentDate = paymentDateTs != null ? paymentDateTs.toLocalDateTime() : null;
        String paymentMethodStr = rs.getString("paymentMethod");
        String statusStr = rs.getString("status");
        String referenceNumber = rs.getString("referenceNumber");
        int invoiceID = rs.getInt("invoiceID");

        // Handle enums with default values
        billingAndPaymentModule.PaymentMethod paymentMethod = billingAndPaymentModule.PaymentMethod.CARD;
        //Runs a try catch to avoid illegal argument exception
        try {
            if (paymentMethodStr != null) paymentMethod = billingAndPaymentModule.PaymentMethod.valueOf(paymentMethodStr);
        } catch (IllegalArgumentException ex) {
            // default
        }
        // Handle enums with default values
        PaymentStatus paymentStatus = PaymentStatus.PENDING; // default
        // Runs a try catch to avoid illegal argument exception
        try {
            if (statusStr != null) paymentStatus = PaymentStatus.valueOf(statusStr);
        } catch (IllegalArgumentException ex) {
            // default
        }

        Payment payment = new Payment(paymentId, amount, paymentDate, paymentMethod, paymentStatus, referenceNumber, invoiceID);
        return payment;
    }
}