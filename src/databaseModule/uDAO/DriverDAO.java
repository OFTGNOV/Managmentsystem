package databaseModule.uDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import userModule.Driver;
import userModule.User;
import javax.swing.JOptionPane;

import databaseModule.DBHelper;

// Data Access Object for Driver entity - now mapped to the unified User table
public class DriverDAO {
    // Inserts a new driver. On success sets the generated ID on the provided Driver object and returns true.
    public static void insertDriverRecord(Driver driver) {
        User user = driver; // Since Driver extends User, we can cast
        UserDAO.insertUserRecord(user);
        // The ID will be set by UserDAO
    }

    // Deletes a driver by ID
    public static void deleteDriverRecord(int driverId) {
        UserDAO.deleteUserRecord(driverId);
    }

    // Update driver. If passwordHash on the provided driver is non-null it will update password and salt as well.
    public static void updateDriverRecord(Driver driver) {
        User user = driver; // Since Driver extends User, we can cast
        UserDAO.updateUserRecord(user);
    }

    // Retrieve driver by ID
    public static Driver retrieveDriverById(int driverId) {
        User user = UserDAO.retrieveUserRecordById(driverId);
        if (user != null && user.getUserType() == userModule.UserType.DRIVER) {
            // For now, we'll create the driver with a default DLN since it's not stored in the main user table
            return new Driver(user, "DL" + user.getID()); // Default DLN if not stored separately
        }
        return null;
    }

    // Retrieve driver by driver license number
    public static Driver retrieveDriverByDln(String dln) {
        // This is more complex since DLN is not in the main user table in the new model
        // For now, we'll return all drivers and match by DLN in memory
        List<User> allUsers = UserDAO.readAllUsers();
        for (User user : allUsers) {
            if (user.getUserType() == userModule.UserType.DRIVER) {
                Driver driver = new Driver(user, dln); // Create with the provided DLN
                if (driver.getdln().equals(dln)) {
                    return driver;
                }
            }
        }
        return null;
    }

    // Read all drivers
    public static List<Driver> readAllDrivers() {
        List<User> allUsers = UserDAO.readAllUsers();
        List<Driver> drivers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUserType() == userModule.UserType.DRIVER) {
                // For now, create with default DLN
                drivers.add(new Driver(user, "DL" + user.getID()));
            }
        }
        return drivers;
    }
}