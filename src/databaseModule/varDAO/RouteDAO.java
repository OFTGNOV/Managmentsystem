package databaseModule.varDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import databaseModule.DBHelper;
import databaseModule.sDAO.ShipmentDAO;
import vehicleAndRoutingModule.Route;
import vehicleAndRoutingModule.Vehicle;

/* Data Access Object for Route entity
 * Provides methods to perform CRUD operations on Route data in the database
 */
public class RouteDAO {
	public static void insertRouteRecord(Vehicle vehicle) {
		Route route = new Route(vehicle);
		insertRouteRecord(route);
	}

    // Inserts a new route record into the database
    public static void insertRouteRecord(Route route) {
        String sql = "INSERT INTO `route` (routeNum, VehiclePlateNum, zone, startTime, endTime) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, route.getRouteNum());
            if (route.getVehicle() != null && route.getVehicle().getLicensePlate() != null) {
                ps.setString(2, route.getVehicle().getLicensePlate());
            } else {
                ps.setNull(2, Types.VARCHAR); // Use VARCHAR since we're storing license plate
            }
            ps.setInt(3, route.getZone());
            if (route.getStartTime() != null) {
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(route.getStartTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            if (route.getEndTime() != null) {
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(route.getEndTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting route failed, no rows affected.");
                return;
            }

            JOptionPane.showMessageDialog(null, "Route created successfully with route number: " + route.getRouteNum());
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting route: " + e.getMessage());
            return;
        }
    }

    // Updates an existing route record in the database
    public static void updateRouteRecord(Route route) {
        String sql = "UPDATE `route` SET VehiclePlateNum = ?, zone = ?, startTime = ?, endTime = ? WHERE routeNum = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (route.getVehicle() != null && route.getVehicle().getLicensePlate() != null) {
                ps.setString(1, route.getVehicle().getLicensePlate());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            ps.setInt(2, route.getZone());
            if (route.getStartTime() != null) {
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(route.getStartTime()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            if (route.getEndTime() != null) {
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(route.getEndTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            ps.setString(5, route.getRouteNum());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Route updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No route found with the provided route number. Update did not modify any rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating route: " + e.getMessage());
        }
    }

    // Deletes a route record by route number
    public static void deleteRouteRecord(String routeNum) {
        String sql = "DELETE FROM `route` WHERE routeNum = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, routeNum);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Route deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No route found with the provided route number.");
            }
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting route: " + e.getMessage());
            return;
        }
    }

    // Retrieves a route by route number
    public static Route retrieveRouteByRouteNum(String routeNum) {
        String sql = "SELECT routeNum, VehiclePlateNum, zone, startTime, endTime FROM `route` WHERE routeNum = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, routeNum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Route route = mapResultSetToRoute(rs);
                    return route;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving route by route number: " + e.getMessage());
        }
        return null;
    }

    // Retrieves all routes from the database
    public static List<Route> readAllRoutes() {
        String sql = "SELECT routeNum, VehiclePlateNum, zone, startTime, endTime FROM `route`";
        List<Route> routes = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                routes.add(mapResultSetToRoute(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Helper method to map a ResultSet row to a Route object
    private static Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        String routeNum = rs.getString("routeNum");
        String vehiclePlateNum = rs.getString("VehiclePlateNum");
        int zone = rs.getInt("zone");
        LocalDateTime startTime = null;
        if (rs.getTimestamp("startTime") != null) {
            startTime = rs.getTimestamp("startTime").toLocalDateTime();
        }
        LocalDateTime endTime = null;
        if (rs.getTimestamp("endTime") != null) {
            endTime = rs.getTimestamp("endTime").toLocalDateTime();
        }

        // Retrieve Vehicle by license plate
        Vehicle vehicle = null;
        if (vehiclePlateNum != null && !vehiclePlateNum.isEmpty()) {
            vehicle = VehicleDAO.retrieveVehicleByLicensePlate(vehiclePlateNum);
        }

        Route route = new Route(vehicle);
        route.setVehicle(vehicle);
        route.setRouteNum(routeNum);
        route.setStartTime(startTime);
        route.setEndTime(endTime);
        route.setZone(zone);

        return route;
    }

    // Assign a shipment to a route
    public static boolean assignShipmentToRoute(String routeNum, String shipmentTrackingNumber) {
        String sql = "INSERT INTO `route_shipments` (routeNum, shipmentTrackingNumber) VALUES (?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, routeNum);
            ps.setString(2, shipmentTrackingNumber);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Shipment assigned to route successfully.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to assign shipment to route.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error assigning shipment to route: " + e.getMessage());
            return false;
        }
    }

    // Remove a shipment from a route
    public static boolean removeShipmentFromRoute(String routeNum, String shipmentTrackingNumber) {
        String sql = "DELETE FROM `route_shipments` WHERE routeNum = ? AND shipmentTrackingNumber = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, routeNum);
            ps.setString(2, shipmentTrackingNumber);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Shipment removed from route successfully.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No matching shipment-route assignment found.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error removing shipment from route: " + e.getMessage());
            return false;
        }
    }

    // Get all shipments assigned to a route
    public static List<shipmentModule.Shipment> getShipmentsForRoute(String routeNum) {
        List<shipmentModule.Shipment> shipments = new ArrayList<>();
        String sql = "SELECT rs.shipmentTrackingNumber FROM `route_shipments` rs WHERE rs.routeNum = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, routeNum);
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
