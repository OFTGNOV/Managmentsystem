package databaseModule.uDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import userModule.Customer;
import userModule.User;
import javax.swing.JOptionPane;

import databaseModule.DBHelper;

// Data Access Object for Customer entity - now mapped to the unified User table
public class CustomerDAO {
    // Inserts a new customer. On success sets the generated ID on the provided Customer object and returns true.
    public static void insertCustomerRecord(Customer customer) {
        User user = customer; // Since Customer extends User, we can cast
        UserDAO.insertUserRecord(user);
        // The ID will be set by UserDAO
    }

    // Deletes a customer by ID
    public static void deleteCustomerRecord(int customerId) {
        UserDAO.deleteUserRecord(customerId);
    }

    // Update customer. If passwordHash on the provided customer is non-null it will update password and salt as well.
    public static void updateCustomerRecord(Customer customer) {
        User user = customer; // Since Customer extends User, we can cast
        UserDAO.updateUserRecord(user);
    }

    // Retrieve customer by ID
    public static Customer retrieveCustomerById(int customerId) {
        User user = UserDAO.retrieveUserRecordById(customerId);
        if (user != null && user.getUserType() == userModule.UserType.CUSTOMER) {
            return new Customer(user);
        }
        return null;
    }

    // Retrieve customer by email
    public static Customer retrieveCustomerByEmail(String email) {
        User user = UserDAO.retrieveUserRecordByEmail(email);
        if (user != null && user.getUserType() == userModule.UserType.CUSTOMER) {
            return new Customer(user);
        }
        return null;
    }

    // Read all customers
    public static List<Customer> readAllCustomers() {
        List<User> allUsers = UserDAO.readAllUsers();
        List<Customer> customers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUserType() == userModule.UserType.CUSTOMER) {
                customers.add(new Customer(user));
            }
        }
        return customers;
    }
}