package databaseModule.sDAO;

import shipmentModule.PackageType;
import shipmentModule.Shipment;
import shipmentModule.ShipmentStatus;
import userModule.User;
import databaseModule.DBHelper;
import databaseModule.uDAO.UserDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ShipmentDAO {
    // Insert a shipment record into DB using parameters
	public static void insertShipmentRecord(User sender, User recipent, double weight,
			double length, double width, double height, PackageType pType) {
		Shipment s = new Shipment(sender, recipent, weight, length, width, height, pType);
		insertShipmentRecord(s);
	}
	
	// Insert a shipment record into DB using Shipment object
    public static void insertShipmentRecord(Shipment s) {
        String sql = "INSERT INTO shipment (trackingNumber, senderId, recipentId, weight, length, width, height, PackageType, ShipmentType, shippingCost, createdDate, deliveredDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getTrackingNumber());
            ps.setInt(2, s.getSender().getID());
            ps.setInt(3, s.getRecipent().getID());
            ps.setDouble(4, s.getWeight());
            ps.setDouble(5, s.getLength());
            ps.setDouble(6, s.getWidth());
            ps.setDouble(7, s.getHeight());
            ps.setString(8, s.getpType().name());
            ps.setString(9, s.getStatus() == null ? ShipmentStatus.PENDING.name() : s.getStatus().name());
            ps.setDouble(10, s.getShippingCost());
            ps.setTimestamp(11, Timestamp.valueOf(s.getCreatedDate()));
            // deliveredDate may be null
            if (s.getDeliveredDate() != null) {
                ps.setTimestamp(12, Timestamp.valueOf(s.getDeliveredDate()));
            } else {
                ps.setTimestamp(12, null);
            }
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Shipment inserted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Inserting shipment failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting shipment: " + e.getMessage());
        }
    }

    // Delete shipment by tracking number
    public static void deleteShipmentRecord(String trackingNumber) {
        String sql = "DELETE FROM shipment WHERE trackingNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingNumber);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Shipment deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No shipment found with tracking number: " + trackingNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting shipment: " + e.getMessage());
        }
    }

    // Update shipment record
    public static void updateShipmentRecord(Shipment s) {
        String sql = "UPDATE shipment SET senderId = ?, recipentId = ?, weight = ?, length = ?, width = ?, height = ?, PackageType = ?, ShipmentType = ?, shippingCost = ?, createdDate = ?, deliveredDate = ? WHERE trackingNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getSender().getID());
            ps.setInt(2, s.getRecipent().getID());
            ps.setDouble(3, s.getWeight());
            ps.setDouble(4, s.getLength());
            ps.setDouble(5, s.getWidth());
            ps.setDouble(6, s.getHeight());
            ps.setString(7, s.getpType().name());
            ps.setString(8, s.getStatus() == null ? ShipmentStatus.PENDING.name() : s.getStatus().name());
            ps.setDouble(9, s.getShippingCost());
            ps.setTimestamp(10, Timestamp.valueOf(s.getCreatedDate()));
            if (s.getDeliveredDate() != null) {
                ps.setTimestamp(11, Timestamp.valueOf(s.getDeliveredDate()));
            } else {
                ps.setTimestamp(11, null);
            }
            ps.setString(12, s.getTrackingNumber());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Shipment updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No shipment found to update (trackingNumber=" + s.getTrackingNumber() + ").");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating shipment: " + e.getMessage());
        }
    }

    // Retrieve shipment by tracking number
    public static Shipment retrieveShipmentByTrackingNumber(String trackingNumber) {
        String sql = "SELECT trackingNumber, senderId, recipentId, weight, length, width, height, PackageType, ShipmentType, shippingCost, createdDate, deliveredDate FROM shipment WHERE trackingNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShipment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving shipment: " + e.getMessage());
        }
        return null;
    }

    // Read all shipments
    public static List<Shipment> readAllShipments() {
        List<Shipment> list = new ArrayList<>();
        String sql = "SELECT trackingNumber, senderId, recipentId, weight, length, width, height, PackageType, ShipmentType, shippingCost, createdDate, deliveredDate FROM shipment";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Shipment s = mapResultSetToShipment(rs);
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading shipments: " + e.getMessage());
        }
        return list;
    }

    // Helper to map a ResultSet row to a Shipment object
    private static Shipment mapResultSetToShipment(ResultSet rs) throws SQLException {
        String trackingNumber = rs.getString("trackingNumber");
        int senderId = rs.getInt("senderId");
        int recipentId = rs.getInt("recipentId");
        double weight = rs.getDouble("weight");
        double length = rs.getDouble("length");
        double width = rs.getDouble("width");
        double height = rs.getDouble("height");
        String packageTypeStr = rs.getString("PackageType");
        String shipmentTypeStr = rs.getString("ShipmentType");
        double shippingCost = rs.getDouble("shippingCost");
        Timestamp createdTs = rs.getTimestamp("createdDate");
        Timestamp deliveredTs = null;
        try {
            deliveredTs = rs.getTimestamp("deliveredDate");
        } catch (SQLException e) {
            // column may not exist or be malformed; treat as null
        }

        // Load User objects using UserDAO (which returns User by ID)
        User sender = UserDAO.retrieveUserRecordById(senderId);
        User recipent = UserDAO.retrieveUserRecordById(recipentId);

        PackageType pType = PackageType.STANDARD;
        try {
            if (packageTypeStr != null) pType = PackageType.valueOf(packageTypeStr);
        } catch (IllegalArgumentException ex) {
            // leave as STANDARD by default
        }

        ShipmentStatus status = ShipmentStatus.PENDING;
        try {
            if (shipmentTypeStr != null) status = ShipmentStatus.valueOf(shipmentTypeStr);
        } catch (IllegalArgumentException ex) {
            // default to PENDING
        }

        // Build Shipment object
        Shipment s = new Shipment(sender, recipent, weight, length, width, height, pType);
        s.setTrackingNumber(trackingNumber);
        s.setShippingCost(shippingCost);
        s.setStatus(status);
        if (createdTs != null) s.setCreatedDate(createdTs.toLocalDateTime());
        if (deliveredTs != null) s.setDeliveredDate(deliveredTs.toLocalDateTime());
        return s;
    }
}
