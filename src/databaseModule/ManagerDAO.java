package databaseModule;

import userModule.Manager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class ManagerDAO {
	//Insert Manager Record with parameters
	public static void insertManagerRecord(String Fname, String Lname, String email, String password) {
		Manager manager = new Manager(Fname, Lname, email, password);
		insertManagerRecord(manager);
	}
	
	//Insert Manager Record using Manager object
	public static void insertManagerRecord(Manager manager) {
		// First insert into user table which will populate manager.ID
		UserDAO.insertUserRecord(manager);
		int userId = manager.getID();
		if (userId <= 0) {
			JOptionPane.showMessageDialog(null, "Failed to insert manager: user ID was not generated.");
			return;
		}

		String sql = "INSERT INTO manager (mngID, UserID) VALUES (?, ?)";
		try (Connection conn = DBHelper.getConnection();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, manager.getMngID());
			ps.setInt(2, userId);
			int affected = ps.executeUpdate();
			if (affected > 0) {
				JOptionPane.showMessageDialog(null, "Manager record inserted successfully.");
			} else {
				JOptionPane.showMessageDialog(null, "Inserting manager failed, no rows affected.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error inserting manager: " + e.getMessage());
		}
		
	}
	
	//Delete Manager Record
	public static void deleteManagerRecord(String mngID) {
		String fetchSql = "SELECT UserID FROM manager WHERE mngID = ?";
		String deleteManagerSql = "DELETE FROM manager WHERE mngID = ?";
		try (Connection conn = DBHelper.getConnection();
			 PreparedStatement ps = conn.prepareStatement(fetchSql)) {
			ps.setString(1, mngID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt("UserID");
					// delete manager row first due to FK
					try (PreparedStatement psDel = conn.prepareStatement(deleteManagerSql)) {
						psDel.setString(1, mngID);
						int affected = psDel.executeUpdate();
						if (affected > 0) {
							// now delete user
							UserDAO.deleteUserRecord(userId);
							JOptionPane.showMessageDialog(null, "Manager and associated user deleted successfully.");
						} else {
							JOptionPane.showMessageDialog(null, "Failed to delete manager row.");
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "No manager found with mngID: " + mngID);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error deleting manager: " + e.getMessage());
		}
	}

	//Update Manager Record
	public static void updateManagerRecord(Manager manager) {
		// Update user fields first
		UserDAO.updateUserRecord(manager);
		// Then update manager-specific fields (only mngID exists in manager table)
		String sql = "UPDATE manager SET mngID = ? WHERE UserID = ?";
		try (Connection conn = DBHelper.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, manager.getMngID());
			ps.setInt(2, manager.getID());
			int affected = ps.executeUpdate();
			if (affected > 0) {
				JOptionPane.showMessageDialog(null, "Manager record updated successfully.");
			} else {
				// It's possible the manager row doesn't exist; inform the caller
				JOptionPane.showMessageDialog(null, "No manager record found to update (UserID=" + manager.getID() + ").");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error updating manager: " + e.getMessage());
		}
	}

	//Retrieve Manager by mngID
	public static Manager retrieveManagerById(String mngID) {
		String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, m.mngID " +
				 "FROM manager m JOIN `user` u ON m.UserID = u.ID WHERE m.mngID = ?";
		try (Connection conn = DBHelper.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, mngID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Manager m = mapResultSetToManager(rs);
					return m;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error retrieving manager: " + e.getMessage());
		}
		return null;
	}
	
	//Read All Managers
	public static List<Manager> readAllManagers() {
		List<Manager> list = new ArrayList<>();
		String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, m.mngID " +
				 "FROM manager m JOIN `user` u ON m.UserID = u.ID";
		try (Connection conn = DBHelper.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Manager m = mapResultSetToManager(rs);
				list.add(m);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error reading managers: " + e.getMessage());
		}
		return list;
	}
	
	//Helper method to map ResultSet to Manager object
	private static Manager mapResultSetToManager(ResultSet rs) throws SQLException {
		int uid = rs.getInt("ID");
		String fname = rs.getString("Fname");
		String lname = rs.getString("Lname");
		String email = rs.getString("email");
		String passwordHash = rs.getString("password");
		String salt = rs.getString("salt");
		String mngID = rs.getString("mngID");
		Manager m = new Manager(fname, lname, email, null);
		m.setID(uid);
		m.setPasswordHashAndSalt(passwordHash, salt);
		m.setMngID(mngID);
		return m;
	}
    
}