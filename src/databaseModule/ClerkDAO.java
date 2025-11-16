package databaseModule;

import userModule.Clerk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ClerkDAO {
	//Insert Clerk Record with parameters
    public static void insertClerkRecord(String Fname, String Lname, String email, String password) {
        Clerk clerk = new Clerk(Fname, Lname, email, password);
        insertClerkRecord(clerk);
    }
    
	//Insert Clerk Record with Clerk object
    public static void insertClerkRecord(Clerk clerk) {
        // Insert into user table first
        UserDAO.insertUserRecord(clerk);
        int userId = clerk.getID();
        if (userId <= 0) {
            JOptionPane.showMessageDialog(null, "Failed to insert clerk: user ID was not generated.");
            return;
        }

        String sql = "INSERT INTO clerk (clerkID, UserID) VALUES (?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clerk.getClkId());
            ps.setInt(2, userId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Clerk record inserted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Inserting clerk failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting clerk: " + e.getMessage());
        }
    }

    //Delete Clerk Record
    public static void deleteClerkRecord(String clkID) {
	    String fetchSql = "SELECT UserID FROM clerk WHERE clerkID = ?";
	    String deleteSql = "DELETE FROM clerk WHERE clerkID = ?";
	    try (Connection conn = DBHelper.getConnection();
	         PreparedStatement ps = conn.prepareStatement(fetchSql)) {
	        ps.setString(1, clkID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                int userId = rs.getInt("UserID");
	                try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
	                    psDel.setString(1, clkID);
	                    int affected = psDel.executeUpdate();
	                    if (affected > 0) {
	                        UserDAO.deleteUserRecord(userId);
	                        JOptionPane.showMessageDialog(null, "Clerk and associated user deleted successfully.");
	                    } else {
	                        JOptionPane.showMessageDialog(null, "Failed to delete clerk row.");
	                    }
	                }
	            } else {
	                JOptionPane.showMessageDialog(null, "No clerk found with clerkID: " + clkID);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error deleting clerk: " + e.getMessage());
	    }
	}

    //Update Clerk Record
	public static void updateClerkRecord(Clerk clerk) {
        // Update user fields
        UserDAO.updateUserRecord(clerk);
        // Update clerk-specific field
        String sql = "UPDATE clerk SET clerkID = ? WHERE UserID = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clerk.getClkId()); //getClerkID()
            ps.setInt(2, clerk.getID()); // UserID
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Clerk record updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No clerk record found to update (UserID=" + clerk.getID() + ").");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating clerk: " + e.getMessage());
        }
    }

	//Retrieve Clerk by Clerk ID
    public static Clerk retrieveClerkById(String clkID) {
	    String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, c.clerkID " +
	            "FROM clerk c JOIN `user` u ON c.UserID = u.ID WHERE c.clerkID = ?";
	    try (Connection conn = DBHelper.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, clkID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Clerk c = mapResultSetToClerk(rs);
					return c;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error retrieving clerk: " + e.getMessage());
	    }
	    return null;
	}

    //Read All Clerks
	public static List<Clerk> readAllClerks() {
        List<Clerk> list = new ArrayList<>();
        String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, c.clerkID " +
                "FROM clerk c JOIN `user` u ON c.UserID = u.ID";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Clerk c = mapResultSetToClerk(rs);
            	list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading clerks: " + e.getMessage());
        }
        return list;
    }
    
	//Map ResultSet to Clerk Object
    private static Clerk mapResultSetToClerk(ResultSet rs) throws SQLException {
    	int uid = rs.getInt("ID");
		String fname = rs.getString("Fname");
		String lname = rs.getString("Lname");
		String email = rs.getString("email");
		String passwordHash = rs.getString("password");
		String salt = rs.getString("salt");
		String clkID  = rs.getString("clkID");
		Clerk c = new Clerk(fname, lname, email, null);
		c.setID(uid);
		c.setPasswordHashAndSalt(passwordHash, salt);
		c.setClkId(clkID);
		return c;
	}
}
