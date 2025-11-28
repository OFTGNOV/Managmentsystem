package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JOptionPane;
import shipmentModule.*;
import userModule.*;
import billingAndPaymentModule.*;
import databaseModule.sDAO.*;
import databaseModule.uDAO.*;
import databaseModule.bapDAO.*;
import databaseModule.varDAO.*;
import vehicleAndRoutingModule.*;

public class CoreSystem {

    // Use the existing domain classes and DAOs instead of in-memory storage
    // This will connect to the real database and use proper data structures

    // Track the currently logged-in user
    private User currentUser;
    private String currentUserCategory;

    public CoreSystem() {
        // Constructor - no sample data needed, rely on actual database content
    }

    private UserType mapCategoryToUserType(String category) {
        if (category == null) return UserType.CUSTOMER;

        switch (category.toLowerCase()) {
            case "customer":
                return UserType.CUSTOMER;
            case "clerk":
                return UserType.CLERK;
            case "manager":
                return UserType.MANAGER;
            case "driver":
                return UserType.DRIVER;
            default:
                return UserType.CUSTOMER; // default to customer
        }
    }

    // ------------------------------------------------------
    // USER AUTHENTICATION
    // ------------------------------------------------------
    public boolean signUpUser(String uid, String fname, String lname, String pwd, String category) {
        try {
            // Check if user already exists
            User existingUser = UserDAO.retrieveUserRecordByEmail(uid);
            if (existingUser != null) {
                return false;
            }

            // Create user based on category
            UserType userType = mapCategoryToUserType(category);
            User user = new User(fname, lname, uid, pwd, userType);

            // Set default values for customer-specific fields if needed
            if (userType == UserType.CUSTOMER) {
                user.setAddress("Default Address");
                user.setZone(1);
            }

            UserDAO.insertUserRecord(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Specialized signup methods for different user types
    public boolean signUpCustomer(String fname, String lname, String email, String password, String address, int zone) {
        try {
            // Check if user already exists
            User existingUser = UserDAO.retrieveUserRecordByEmail(email);
            if (existingUser != null) {
                return false;
            }

            User customer = new User(fname, lname, email, password, UserType.CUSTOMER);
            customer.setAddress(address);
            customer.setZone(zone);
            UserDAO.insertUserRecord(customer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean signUpDriver(String fname, String lname, String email, String password, String driverLicenseNumber) {
        try {
            // Check if user already exists
            User existingUser = UserDAO.retrieveUserRecordByEmail(email);
            if (existingUser != null) {
                return false;
            }

            User driver = new User(fname, lname, email, password, UserType.DRIVER);
            // Note: In the new design, we might need to store driver license in address field or create another field
            // For now, we'll use the address field to store the license if needed,
            // but in a proper refactored implementation, we could expand the User class
            UserDAO.insertUserRecord(driver);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticate(String uid, String pwd, String category) {
        try {
            // Look up user by email
            User user = UserDAO.retrieveUserRecordByEmail(uid);

            if (user != null && user.verifyPassword(pwd)) {
                // Verify the user type matches the category
                UserType expectedType = mapCategoryToUserType(category);
                if (user.getUserType() == expectedType) {
                    this.currentUser = user;
                    this.currentUserCategory = category;
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setCurrentUser(User user, String category) {
        this.currentUser = user;
        this.currentUserCategory = category;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public String getCurrentUserCategory() {
        return this.currentUserCategory;
    }

    // ------------------------------------------------------
    // SHIPMENT OPERATIONS
    // ------------------------------------------------------
    public Shipment createShipment(String senderName, String pickupAddress, String destAddress,
                                   String description) {
        try {
            // Use the current logged-in customer as sender, or create a new customer if needed
            User sender;
            if (currentUser != null && currentUserCategory.equalsIgnoreCase("customer")) {
                // If current user is a customer, use their information
                sender = currentUser;
                sender.setAddress(pickupAddress); // Update address for this shipment
                // Update the user in the database
                UserDAO.updateUserRecord(sender);
            } else {
                // Create a new customer object with provided info
                String senderFirstName = senderName.split(" ")[0];
                String senderLastName = senderName.split(" ").length > 1 ? senderName.split(" ")[1] : "";
                String senderEmail = senderName.toLowerCase().replace(" ", "") + System.currentTimeMillis() + "@customer.com";

                sender = new User(senderFirstName, senderLastName, senderEmail, "default", UserType.CUSTOMER);
                sender.setAddress(pickupAddress);
                sender.setZone(1);
                // Insert the new customer to the database
                UserDAO.insertUserRecord(sender);
            }

            // For recipient, create a customer object using the destination info
            String recipientFirstName = destAddress.split(" ").length > 0 ? destAddress.split(" ")[0] : "Recipient";
            String recipientLastName = destAddress.split(" ").length > 1 ? destAddress.split(" ")[1] : "Customer";
            String recipientEmail = recipientFirstName.toLowerCase() + System.currentTimeMillis() + "@recipient.com";

            User recipient = new User(recipientFirstName, recipientLastName, recipientEmail, "default", UserType.CUSTOMER);
            recipient.setAddress(destAddress);
            recipient.setZone(1);
            // Insert recipient as a customer to the database
            UserDAO.insertUserRecord(recipient);

            // Create shipment with default values (will be updated by caller)
            // Use User objects directly since Shipment now works with User class
            Shipment s = new Shipment(sender, recipient, 1.0, 10.0, 10.0, 10.0, PackageType.STANDARD);

            // Save to database
            ShipmentDAO.insertShipmentRecord(s);

            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    public Shipment getShipment(String id) {
        try {
            return ShipmentDAO.retrieveShipmentByTrackingNumber(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Shipment> listShipments() {
        try {
            return ShipmentDAO.readAllShipments();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean updateShipmentStatus(String id, String newStatus) {
        try {
            Shipment s = ShipmentDAO.retrieveShipmentByTrackingNumber(id);
            if (s != null) {
                ShipmentStatus status = ShipmentStatus.PENDING;
                try {
                    status = ShipmentStatus.valueOf(newStatus.toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    // If mapping fails, default to pending
                    status = ShipmentStatus.PENDING;
                }
                s.updateStatus(status);
                ShipmentDAO.updateShipmentRecord(s);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteShipment(String id) {
        try {
            ShipmentDAO.deleteShipmentRecord(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------------------------------------------
    // VEHICLE OPERATIONS
    // ------------------------------------------------------
    public Vehicle addVehicle(String licensePlate, double maxWeight, int maxPackages, String driverDLN) {
        try {
            User driver = null;
            if (driverDLN != null && !driverDLN.isEmpty()) {
                // Look up the driver by email (since we no longer have a DLN field)
                // We can search for the user by email instead
                driver = UserDAO.retrieveUserRecordByEmail(driverDLN);
                if (driver == null || driver.getUserType() != UserType.DRIVER) {
                    JOptionPane.showMessageDialog(null, "Driver with email " + driverDLN + " not found or not a driver.");
                    return null;
                }
            }

            Vehicle v = new Vehicle(driver, licensePlate, "Van", maxWeight, maxPackages);
            VehicleDAO.insertVehicleRecord(v);
            return v;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Vehicle> listVehicles() {
        try {
            return VehicleDAO.readAllVehicles();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Assign shipment to vehicle
    public boolean assignShipmentToVehicle(String shipmentId, String vehicleId, String routeTime) {
        try {
            Shipment shipment = ShipmentDAO.retrieveShipmentByTrackingNumber(shipmentId);
            Vehicle vehicle = VehicleDAO.retrieveVehicleByLicensePlate(vehicleId);

            if (shipment == null || vehicle == null) return false;

            // Check if vehicle can accommodate this shipment
            if (!vehicle.canAddPackage(shipment.getWeight())) {
                JOptionPane.showMessageDialog(null,
                    "Cannot assign shipment to vehicle: exceeds vehicle capacity.\n" +
                    "Current load: " + vehicle.getCurrentWeight() + "kg, Max: " + vehicle.getMaxWeightCapacity() + "kg\n" +
                    "Current packages: " + vehicle.getCurrentPackageCount() + ", Max: " + vehicle.getMaxPackageCapacity());
                return false;
            }

            // Update shipment status to assigned
            shipment.updateStatus(ShipmentStatus.ASSIGNED);

            // Assign the shipment to the vehicle
            boolean assigned = vehicle.assignShipment(shipment);
            if (assigned) {
                ShipmentDAO.updateShipmentRecord(shipment);
                VehicleDAO.updateVehicleRecord(vehicle);

                // Also update the vehicle-shipment relation
                VehicleDAO.assignShipmentToVehicle(vehicleId, shipmentId);
            }

            return assigned;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------------------------------------------
    // DRIVER OPERATIONS
    // ------------------------------------------------------
    public User addDriver(String firstName, String lastName, String email, String password, String driverLicenseNumber) {
        try {
            User user = new User(firstName, lastName, email, password, UserType.DRIVER);
            UserDAO.insertUserRecord(user);
            // Return User object since Driver class no longer exists as separate model
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> listDrivers() {
        try {
            List<User> allUsers = UserDAO.readAllUsers();
            List<User> drivers = new ArrayList<>();
            for (User user : allUsers) {
                if (user.getUserType() == UserType.DRIVER) {
                    // Return User objects since Driver class no longer exists as separate model
                    drivers.add(user);
                }
            }
            return drivers;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ------------------------------------------------------
    // INVOICE OPERATIONS
    // ------------------------------------------------------
    public Invoice createInvoice(Shipment shipment) {
        try {
            Invoice invoice = new Invoice(shipment);
            InvoiceDAO.insertInvoiceRecord(invoice);
            return invoice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Invoice> listInvoices() {
        try {
            return InvoiceDAO.readAllInvoices();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ------------------------------------------------------
    // REPORTS
    // ------------------------------------------------------
    public Map<String, Long> shipmentsByStatus() {
        Map<String, Long> map = new HashMap<>();
        List<Shipment> shipments = listShipments();
        
        for (Shipment s : shipments) {
            String status = s.getStatus().toString();
            map.merge(status, 1L, Long::sum);
        }
        return map;
    }

    // ------------------------------------------------------
    // DRIVER: GET ASSIGNED DELIVERIES
    // ------------------------------------------------------
    public List<Shipment> getAssignedDeliveries() {
        List<Shipment> result = new ArrayList<>();
        if (currentUser == null || !currentUserCategory.equalsIgnoreCase("driver")) return result;

        // Get the current driver
        User currentDriver = currentUser;

        // Get all vehicles and find those assigned to the current driver
        List<Vehicle> allVehicles = listVehicles();
        for (Vehicle v : allVehicles) {
            if (v.getAssignedDriver() != null) {
                // Compare by ID since we're using a unified User model
                if (v.getAssignedDriver().getID() == currentDriver.getID()) {
                    // Get shipments assigned to this vehicle
                    List<shipmentModule.Shipment> shipmentsForVehicle =
                        databaseModule.varDAO.VehicleDAO.getShipmentsForVehicle(v.getLicensePlate());
                    for (shipmentModule.Shipment s : shipmentsForVehicle) {
                        // Only add shipments that are in transit or assigned
                        if (s.getStatus() == ShipmentStatus.ASSIGNED || s.getStatus() == ShipmentStatus.IN_TRANSIT) {
                            result.add(s);
                        }
                    }
                }
            }
        }

        return result;
    }
}