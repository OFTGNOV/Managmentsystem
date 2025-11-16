package databaseModule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import userModule.User;
import javax.swing.JOptionPane;

// Data Access Object for User entity
public class UserDAO {
    // Inserts a new user using raw fields. Password will be hashed by User constructor.
    public static void insertUserRecord(String Fname, String Lname, String email, String password) {
        User user = new User(Fname, Lname, email, password);
        insertUserRecord(user);
    }

    // Inserts a new user. On success sets the generated ID on the provided User object and returns true.
    public static void insertUserRecord(User user) {
        String sql = "INSERT INTO `user` (Fname, Lname, email, password, salt) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getSalt());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Inserting user failed, no rows affected.");
                return;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setID(keys.getInt(1));
                }
            }
            JOptionPane.showMessageDialog(null, "User registered successfully with ID: " + user.getID());
            return;
        } catch (SQLException e) {
            // Caller can inspect logs; do not expose SQL details here.
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting user: " + e.getMessage());
            return;
        }
    }

    // Deletes a user by ID
    public static void deleteUserRecord(int userId) {
        String sql = "DELETE FROM `user` WHERE ID = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "User deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No user found with the provided ID.");
            }
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting user: " + e.getMessage());
            return;
        }
    }

    // Update user. If passwordHash on the provided user is non-null it will update password and salt as well.
	public static void updateUserRecord(User user) {
	    // Base update (without password)
	    String sqlBase = "UPDATE `user` SET Fname = ?, Lname = ?, email = ?";
	    boolean updatePassword = user.getPasswordHash() != null && user.getSalt() != null;
	    String sql = updatePassword ? sqlBase + ", password = ?, salt = ? WHERE ID = ?" : sqlBase + " WHERE ID = ?";
	
	    try (Connection conn = DBHelper.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, user.getFirstName());
	        ps.setString(2, user.getLastName());
	        ps.setString(3, user.getEmail());
	        int idx = 4;
	        if (updatePassword) {
	            ps.setString(idx++, user.getPasswordHash());
	            ps.setString(idx++, user.getSalt());
	        }
	        ps.setInt(idx, user.getID());
	        int affected = ps.executeUpdate();
	        if (affected > 0) {
	            JOptionPane.showMessageDialog(null, "User updated successfully.");
	        } else {
	            JOptionPane.showMessageDialog(null, "No user found with the provided ID. Update did not modify any rows.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error updating user: " + e.getMessage());
	    }
	}

	// Retrieve user by ID
    public static User retrieveUserRecordById(int userId) {
        String sql = "SELECT ID, Fname, Lname, email, password, salt FROM `user` WHERE ID = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    return u;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving user by ID: " + e.getMessage());
        }
        return null;
    }

    // Retrieve user by email
    public static User retrieveUserRecordByEmail(String email) {
        String sql = "SELECT ID, Fname, Lname, email, password, salt FROM `user` WHERE email = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                	User u = mapResultSetToUser(rs);
                	return u;
                }
            }
        } catch (SQLException e) {
        	JOptionPane.showMessageDialog(null, "Error retrieving user by email: " + e.getMessage());
        }
        return null;
    }

    // Read all users
    public static List<User> readAllUsers() {
        String sql = "SELECT ID, Fname, Lname, email, password, salt FROM `user`";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Helper Method to map a ResultSet row to a User object
    public static User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID"); //Get
        String fname = rs.getString("Fname");
        String lname = rs.getString("Lname");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password");
        String salt = rs.getString("salt");

        User u = new User(fname, lname, email, null);
        u.setID(id);
        // populate passwordHash and salt using package-private helper
        // keep raw password null for security
        u.setPasswordHashAndSalt(passwordHash, salt);
        return u;
    }
}