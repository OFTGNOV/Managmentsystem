package databaseModule.uDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import userModule.Clerk;
import userModule.User;
import javax.swing.JOptionPane;

import databaseModule.DBHelper;

// Data Access Object for Clerk entity - now mapped to the unified User table
public class ClerkDAO {
    // Inserts a new clerk. On success sets the generated ID on the provided Clerk object and returns true.
    public static void insertClerkRecord(Clerk clerk) {
        User user = clerk; // Since Clerk extends User, we can cast
        UserDAO.insertUserRecord(user);
        // The ID will be set by UserDAO
    }

    // Deletes a clerk by ID
    public static void deleteClerkRecord(int clerkId) {
        UserDAO.deleteUserRecord(clerkId);
    }

    // Update clerk. If passwordHash on the provided clerk is non-null it will update password and salt as well.
    public static void updateClerkRecord(Clerk clerk) {
        User user = clerk; // Since Clerk extends User, we can cast
        UserDAO.updateUserRecord(user);
    }

    // Retrieve clerk by ID
    public static Clerk retrieveClerkById(int clerkId) {
        User user = UserDAO.retrieveUserRecordById(clerkId);
        if (user != null && user.getUserType() == userModule.UserType.CLERK) {
            return new Clerk(user);
        }
        return null;
    }

    // Retrieve clerk by email
    public static Clerk retrieveClerkByEmail(String email) {
        User user = UserDAO.retrieveUserRecordByEmail(email);
        if (user != null && user.getUserType() == userModule.UserType.CLERK) {
            return new Clerk(user);
        }
        return null;
    }

    // Read all clerks
    public static List<Clerk> readAllClerks() {
        List<User> allUsers = UserDAO.readAllUsers();
        List<Clerk> clerks = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUserType() == userModule.UserType.CLERK) {
                clerks.add(new Clerk(user));
            }
        }
        return clerks;
    }
}