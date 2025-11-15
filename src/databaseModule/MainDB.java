package databaseModule;

import javax.swing.JOptionPane;
import java.sql.*;

public class MainDB {
	public boolean isDBC() { //DBC means DataBaseConnection or DataBase Connected in this context
		String url = "jdbc:mysql://localhost:3307/smartship_package_management_system";
		// Use try-with-resources to ensure the connection is closed automatically
		try (Connection myconn = DriverManager.getConnection(url, "root", "usbw")) {
			if (myconn != null) {
				JOptionPane.showMessageDialog(null, "Connection Successful!",
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

}