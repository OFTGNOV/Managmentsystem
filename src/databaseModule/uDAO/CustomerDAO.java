package databaseModule.uDAO;

import userModule.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import databaseModule.DBHelper;

public class CustomerDAO {
	//Insert Customer Record with parameters
    public static void insertCustomerRecord(String Fname, String Lname, String email, String password, String address, int zone) {
        Customer customer = new Customer(Fname, Lname, email, password, address, zone);
        insertCustomerRecord(customer);
    }

    //Insert Customer Record
    public static void insertCustomerRecord(Customer customer) {
        // Insert into user table first
        UserDAO.insertUserRecord(customer);
        int userId = customer.getID();
        if (userId <= 0) {
            JOptionPane.showMessageDialog(null, "Failed to insert customer: user ID was not generated.");
            return;
        }

        String sql = "INSERT INTO customer (custID, UserID, address, zone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustId());
            ps.setInt(2, userId);
            ps.setString(3, customer.getAddress());
            ps.setInt(4, customer.getZone());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Customer record inserted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Inserting customer failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting customer: " + e.getMessage());
        }
    }

    //Delete Customer Record
	public static void deleteCustomerRecord(String custID) {
	    String fetchSql = "SELECT UserID FROM customer WHERE custID = ?";
	    String deleteSql = "DELETE FROM customer WHERE custID = ?";
	    try (Connection conn = DBHelper.getConnection();
	         PreparedStatement ps = conn.prepareStatement(fetchSql)) {
	        ps.setString(1, custID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                int userId = rs.getInt("UserID");
	                try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
	                    psDel.setString(1, custID);
	                    int affected = psDel.executeUpdate();
	                    if (affected > 0) {
	                        UserDAO.deleteUserRecord(userId);
	                        JOptionPane.showMessageDialog(null, "Customer and associated user deleted successfully.");
	                    } else {
	                        JOptionPane.showMessageDialog(null, "Failed to delete customer row.");
	                    }
	                }
	            } else {
	                JOptionPane.showMessageDialog(null, "No customer found with custID: " + custID);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error deleting customer: " + e.getMessage());
	    }
	}

	//Update Customer Record
	public static void updateCustomerRecord(Customer customer) {
        // Update user fields
        UserDAO.updateUserRecord(customer);
        // Update customer-specific fields
        String sql = "UPDATE customer SET custID = ?, address = ?, zone = ? WHERE UserID = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustId());
            ps.setString(2, customer.getAddress());
            ps.setInt(3, customer.getZone());
            ps.setInt(4, customer.getID());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(null, "Customer record updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No customer record found to update (UserID=" + customer.getID() + ").");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating customer: " + e.getMessage());
        }
    }

	//Retrieve Customer by custID
    public static Customer retrieveCustomerById(String custID) {
	    String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, c.custID, c.address, c.zone " +
	            "FROM customer c JOIN `user` u ON c.UserID = u.ID WHERE c.custID = ?";
	    try (Connection conn = DBHelper.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, custID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Customer c = mapResultSetToCustomer(rs);
	                return c;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error retrieving customer: " + e.getMessage());
	    }
	    return null;
	}

	//Read All Customers
    public static List<Customer> readAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT u.ID, u.Fname, u.Lname, u.email, u.password, u.salt, c.custID, c.address, c.zone " +
                "FROM customer c JOIN `user` u ON c.UserID = u.ID";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer c = mapResultSetToCustomer(rs);
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading customers: " + e.getMessage());
        }
        return list;
    }

    //Helper method to map ResultSet to Customer object
    private static Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
    	int uid = rs.getInt("ID");
		String fname = rs.getString("Fname");
		String lname = rs.getString("Lname");
		String email = rs.getString("email");
		String passwordHash = rs.getString("password");
		String salt = rs.getString("salt");
		String custID = rs.getString("custID");
		String address = rs.getString("address");
		int zone = rs.getInt("zone");

		Customer c = new Customer(fname, lname, email, null, address, zone);
		c.setID(uid);
		c.setCustId(custID);
		c.setPasswordHashAndSalt(passwordHash, salt);
		return c;
	}
}
