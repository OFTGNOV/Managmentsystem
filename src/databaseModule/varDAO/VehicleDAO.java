package databaseModule.varDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import databaseModule.DBHelper;
import databaseModule.sDAO.ShipmentDAO;
import userModule.Driver;
import databaseModule.uDAO.DriverDAO;
import vehicleAndRoutingModule.Vehicle;

/* Data Access Object for Vehicle entity
 * Provides methods to perform CRUD operations on Vehicle data in the database
 */
public class VehicleDAO {
	
	public static void insertVehicleRecord(Driver assignedDriver, String licensePlate, String vehicleType,
			double maxWeightCapacity, int maxPackageCapacity) {
		
		Vehicle vehicle = new Vehicle(assignedDriver, licensePlate, vehicleType, maxWeightCapacity, maxPackageCapacity);
		insertVehicleRecord(vehicle);
	}

    // Inserts a new vehicle record into the database
    public static void insertVehicleRecord(Vehicle vehicle) {
        String sql = "INSERT INTO `vehicle` (licensePlate, vehicleType, maxWeightCapacity, maxPackageCapacity, driverID, currentWeight, currentPackageCount, isAvailable) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicle.getLicensePlate());
            ps.setString(2, vehicle.getVehicleType());
            ps.setDouble(3, vehicle.getMaxWeightCapacity());
            ps.setInt(4, vehicle.getMaxPackageCapacity());

            if (vehicle.getAssignedDriver() != null) {
                ps.setInt(5, vehicle.getAssignedDriver().getID());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setDouble(6, vehicle.getCurrentWeight());
            ps.setInt(7, vehicle.getCurrentPackageCount());
            ps.setBoolean(8, vehicle.isAvailable());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting vehicle failed, no rows affected.");
                return;
            }

            JOptionPane.showMessageDialog(null, "Vehicle registered successfully with license plate: " + vehicle.getLicensePlate());
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting vehicle: " + e.getMessage());
            return;
        }
    }

    // Updates an existing vehicle record in the database
    public static void updateVehicleRecord(Vehicle vehicle) {
        String sql = "UPDATE `vehicle` SET vehicleType = ?, maxWeightCapacity = ?, maxPackageCapacity = ?, driverID = ?, currentWeight = ?, currentPackageCount = ?, isAvailable = ? WHERE licensePlate = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicle.getVehicleType());
            ps.setDouble(2, vehicle.getMaxWeightCapacity());
            ps.setInt(3, vehicle.getMaxPackageCapacity());

            if (vehicle.getAssignedDriver() != null) {
                ps.setInt(4, vehicle.getAssignedDriver().getID());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setDouble(5, vehicle.getCurrentWeight());
            ps.setInt(6, vehicle.getCurrentPackageCount());
            ps.setBoolean(7, vehicle.isAvailable());
            ps.setString(8, vehicle.getLicensePlate());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Vehicle updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No vehicle found with the provided license plate. Update did not modify any rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating vehicle: " + e.getMessage());
        }
    }

    // Deletes a vehicle record by license plate
    public static void deleteVehicleRecord(String licensePlate) {
        String sql = "DELETE FROM `vehicle` WHERE licensePlate = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Vehicle deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No vehicle found with the provided license plate.");
            }
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting vehicle: " + e.getMessage());
            return;
        }
    }

    

    // Retrieves a vehicle by license plate (primary key in the database)
    public static Vehicle retrieveVehicleByLicensePlate(String licensePlate) {
        String sql = "SELECT licensePlate, vehicleType, maxWeightCapacity, maxPackageCapacity, driverID, currentWeight, currentPackageCount, isAvailable FROM `vehicle` WHERE licensePlate = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = mapResultSetToVehicle(rs);
                    return vehicle;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving vehicle by license plate: " + e.getMessage());
        }
        return null;
    }

    // Retrieves all vehicles from the database
    public static List<Vehicle> readAllVehicles() {
        String sql = "SELECT licensePlate, vehicleType, maxWeightCapacity, maxPackageCapacity, driverID, currentWeight, currentPackageCount, isAvailable FROM `vehicle`";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // Helper method to map a ResultSet row to a Vehicle object
    private static Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        String licensePlate = rs.getString("licensePlate");
        String vehicleType = rs.getString("vehicleType");
        double maxWeightCapacity = rs.getDouble("maxWeightCapacity");
        int maxPackageCapacity = rs.getInt("maxPackageCapacity");
        int driverID = rs.getInt("driverID");
        double currentWeight = rs.getDouble("currentWeight");
        int currentPackageCount = rs.getInt("currentPackageCount");
        boolean isAvailable = rs.getBoolean("isAvailable");

        Driver assignedDriver = null;
        if (rs.wasNull()) {
            // No assigned driver
        } else {
            // Retrieve driver by ID instead of DLN
            User user = databaseModule.uDAO.UserDAO.retrieveUserRecordById(driverID);
            if (user != null && user.getUserType() == userModule.UserType.DRIVER) {
                assignedDriver = new Driver(user, "DL" + user.getID()); // Use default DLN
            }
        }

        Vehicle vehicle = new Vehicle(assignedDriver, licensePlate, vehicleType, maxWeightCapacity, maxPackageCapacity);
        vehicle.setCurrentWeight(currentWeight);
        vehicle.setCurrentPackageCount(currentPackageCount);
        vehicle.setAvailable(isAvailable);

        return vehicle;
    }

    // Helper method to check if vehicle exists by license plate
    public static boolean vehicleExists(String licensePlate) {
        String sql = "SELECT COUNT(*) FROM `vehicle` WHERE licensePlate = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if not found
    }

    // Assign a shipment to a vehicle
    public static boolean assignShipmentToVehicle(String licensePlate, String shipmentTrackingNumber) {
        String sql = "INSERT INTO `vehicle_shipments` (licensePlate, shipmentTrackingNumber) VALUES (?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            ps.setString(2, shipmentTrackingNumber);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Shipment assigned to vehicle successfully.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to assign shipment to vehicle.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error assigning shipment to vehicle: " + e.getMessage());
            return false;
        }
    }

    // Remove a shipment from a vehicle
    public static boolean removeShipmentFromVehicle(String licensePlate, String shipmentTrackingNumber) {
        String sql = "DELETE FROM `vehicle_shipments` WHERE licensePlate = ? AND shipmentTrackingNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            ps.setString(2, shipmentTrackingNumber);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Shipment removed from vehicle successfully.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No matching shipment-vehicle assignment found.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error removing shipment from vehicle: " + e.getMessage());
            return false;
        }
    }

    // Get all shipments assigned to a vehicle
    public static List<shipmentModule.Shipment> getShipmentsForVehicle(String licensePlate) {
        List<shipmentModule.Shipment> shipments = new ArrayList<>();
        String sql = "SELECT vs.shipmentTrackingNumber FROM `vehicle_shipments` vs WHERE vs.licensePlate = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String trackingNumber = rs.getString("shipmentTrackingNumber");
                    shipmentModule.Shipment shipment = ShipmentDAO.retrieveShipmentByTrackingNumber(trackingNumber);
                    if (shipment != null) {
                        shipments.add(shipment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shipments;
    }
}
