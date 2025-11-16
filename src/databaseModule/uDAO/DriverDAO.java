package databaseModule.uDAO;

import userModule.Driver;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import databaseModule.DBHelper;

public class DriverDAO {
	//Insert into Driver with parameterized
	public static void insertDriverRecord(String Fname, String Lname, String email, String password, String dln) {
		Driver driver = new Driver(Fname, Lname, email, password, dln);
		insertDriverRecord(driver);
	}
	
	public static void insertDriverRecord(Driver driver) {
		// First insert into user table which will populate driver.ID
		UserDAO.insertUserRecord(driver);
		int userId = driver.getID();
		if (userId <= 0) {
			JOptionPane.showMessageDialog(null, "Failed to insert driver: user ID was not generated.");
			return;
		}

		String sql = "INSERT INTO driver (dln, UserID) VALUES (?, ?)";
		try (Connection conn = DBHelper.getConnection();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, driver.getdln());
			ps.setInt(2, userId);
			int affected = ps.executeUpdate();
			if (affected > 0) {
				JOptionPane.showMessageDialog(null, "Driver record inserted successfully.");
			} else {
				JOptionPane.showMessageDialog(null, "Inserting driver failed, no rows affected.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error inserting driver: " + e.getMessage());
		}
	}

	//Delete Driver Record by driver's license number (dln)
	public static void deleteDriverRecord(String dln) {
		String fetchSql = "SELECT UserID FROM driver WHERE dln = ?";
		String deleteSql = "DELETE FROM driver WHERE dln = ?";
		try (Connection conn = DBHelper.getConnection();
			     PreparedStatement ps = conn.prepareStatement(fetchSql)) {
			ps.setString(1, dln);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt("UserID");
					try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
						psDel.setString(1, dln);
						int affected = psDel.executeUpdate();
						if (affected > 0) {
							UserDAO.deleteUserRecord(userId);
							JOptionPane.showMessageDialog(null, "Driver and associated user deleted successfully.");
						} else {
							JOptionPane.showMessageDialog(null, "Failed to delete driver row.");
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "No driver found with DLN: " + dln);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error deleting driver: " + e.getMessage());
		}
	}

	//Update Driver Record
	public static void updateDriverRecord(Driver driver) {
		// Update user fields first
		UserDAO.updateUserRecord(driver);
		// Then update driver-specific field(s)
		String sql = "UPDATE driver SET dln = ? WHERE UserID = ?";
		try (Connection conn = DBHelper.getConnection();
			     PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, driver.getdln());
			ps.setInt(2, driver.getID());
			int affected = ps.executeUpdate();
			if (affected > 0) {
				JOptionPane.showMessageDialog(null, "Driver record updated successfully.");
			} else {
				JOptionPane.showMessageDialog(null, "No driver record found to update (UserID=" + driver.getID() + ").");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error updating driver: " + e.getMessage());
		}
	}

	//Retrieve Driver by DLN
	public static Driver retrieveDriverByDln(String dln) {
		String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, d.dln " +
				 "FROM driver d JOIN `user` u ON d.UserID = u.ID WHERE d.dln = ?";
		try (Connection conn = DBHelper.getConnection();
			     PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, dln);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Driver d = mapResultSetToDriver(rs);
					return d;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error retrieving driver: " + e.getMessage());
		}
		return null;
	}

	//Read All Drivers
	public static List<Driver> readAllDrivers() {
		List<Driver> list = new ArrayList<>();
		String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, d.dln " +
				 "FROM driver d JOIN `user` u ON d.UserID = u.ID";
		try (Connection conn = DBHelper.getConnection();
			     PreparedStatement ps = conn.prepareStatement(sql);
			     ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Driver d = mapResultSetToDriver(rs);
				list.add(d);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error reading drivers: " + e.getMessage());
		}
		return list;
	}

	//Helper method to map ResultSet to Driver object
	private static Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
		int uid = rs.getInt("ID");
		String fname = rs.getString("Fname");
		String lname = rs.getString("Lname");
		String email = rs.getString("email");
		String passwordHash = rs.getString("password");
		String salt = rs.getString("salt");
		String dln = rs.getString("dln");

		Driver d = new Driver(fname, lname, email, null, dln);
		d.setID(uid);
		d.setPasswordHashAndSalt(passwordHash, salt);
		return d;
	}
}