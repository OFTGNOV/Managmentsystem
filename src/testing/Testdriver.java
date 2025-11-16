package testing;

import databaseModule.UserDAO;
import userModule.User;
import javax.swing.JOptionPane;

// Uncommented test driver for various modules
public class Testdriver {
	public static void main(String[] args) {
		updateUserRecordTest("kerrynite09", "taai@gmail.com");
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