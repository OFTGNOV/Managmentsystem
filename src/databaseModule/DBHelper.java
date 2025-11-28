package databaseModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import userModule.User;
import userModule.UserType;


/* Database Helper Class
 * This class will contain methods to assist with database operations
 * such as connecting to the database, and closing connections.
 */
public class DBHelper {
	private static final String DB_URL = "jdbc:mysql://localhost:3307/smartship_package_management_system";
	private static final String USER = "root";
	private static final String PASS = "usbw";
	
	//Method to get a database connection
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL, USER, PASS);
	}
	
	//Establishing connection
	public static boolean establishConnection() {
		try (Connection myconn = getConnection()) {
			if (myconn != null) {
				JOptionPane.showMessageDialog(null, "Connection Established Successfully!",
						"JDBC Connection Status", JOptionPane.INFORMATION_MESSAGE);
			return true;
			} else {
				JOptionPane.showMessageDialog(null, "Connection returned null.",
						"JDBC Connection Status", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Connection Failed: " + e.getMessage(),
					"JDBC Connection Status", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	// Method to close the database connection
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("Failed to close connection: " + e.getMessage());
			}
		}
	}
}