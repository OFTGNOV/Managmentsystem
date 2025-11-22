package testing;

import userModule.User;
import userModule.Customer;
import javax.swing.JOptionPane;
import databaseModule.sDAO.*;
import databaseModule.uDAO.*;
import shipmentModule.*;
import billingAndPaymentModule.*;
import java.time.LocalDateTime;

// Uncommented test driver for various modules
public class Testdriver {
	public static void main(String[] args) {
		Customer sender = CustomerDAO.retrieveCustomerById("CUST-6062");
		Customer recipient = CustomerDAO.retrieveCustomerById("CUST-5764");
		ShipmentDAO.insertShipmentRecord(sender, recipient, 2.5, 10.0, 5.0, 3.0, PackageType.STANDARD);
	}

	public static void updateUserRecordTest(String Password, String Email) {
		try{
			User u = UserDAO.retrieveUserRecordByEmail(Email);
			if (Password == null || u.verifyPassword(Password) == false) {
				JOptionPane.showMessageDialog(null, "Incorrect Password", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			u.setFirstName("Tamai");
			u.setLastName("Ricahrds");
			UserDAO.updateUserRecord(u);
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
}