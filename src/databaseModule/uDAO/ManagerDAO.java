package databaseModule.uDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import userModule.Manager;
import userModule.User;
import javax.swing.JOptionPane;

import databaseModule.DBHelper;

// Data Access Object for Manager entity - now mapped to the unified User table
public class ManagerDAO {
    // Inserts a new manager. On success sets the generated ID on the provided Manager object and returns true.
    public static void insertManagerRecord(Manager manager) {
        User user = manager; // Since Manager extends User, we can cast
        UserDAO.insertUserRecord(user);
        // The ID will be set by UserDAO
    }

    // Deletes a manager by ID
    public static void deleteManagerRecord(int managerId) {
        UserDAO.deleteUserRecord(managerId);
    }

    // Update manager. If passwordHash on the provided manager is non-null it will update password and salt as well.
    public static void updateManagerRecord(Manager manager) {
        User user = manager; // Since Manager extends User, we can cast
        UserDAO.updateUserRecord(user);
    }

    // Retrieve manager by ID
    public static Manager retrieveManagerById(int managerId) {
        User user = UserDAO.retrieveUserRecordById(managerId);
        if (user != null && user.getUserType() == userModule.UserType.MANAGER) {
            return new Manager(user);
        }
        return null;
    }

    // Retrieve manager by email
    public static Manager retrieveManagerByEmail(String email) {
        User user = UserDAO.retrieveUserRecordByEmail(email);
        if (user != null && user.getUserType() == userModule.UserType.MANAGER) {
            return new Manager(user);
        }
        return null;
    }

    // Read all managers
    public static List<Manager> readAllManagers() {
        List<User> allUsers = UserDAO.readAllUsers();
        List<Manager> managers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUserType() == userModule.UserType.MANAGER) {
                managers.add(new Manager(user));
            }
        }
        return managers;
    }
}