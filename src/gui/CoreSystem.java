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
            switch (category.toLowerCase()) {
                case "customer":
                    Customer customer = new Customer(fname, lname, uid, pwd, "Default Address", 1);
                    CustomerDAO.insertCustomerRecord(customer);
                    return true;
                case "clerk":
                    // Create a clerk user - in the original design, this would be a subclass of User
                    // For now, using the base User class with the email as identifier
                    UserDAO.insertUserRecord(fname, lname, uid, pwd);
                    return true;
                case "manager":
                    // Create a manager user
                    UserDAO.insertUserRecord(fname, lname, uid, pwd);
                    return true;
                case "driver":
                    // Create a driver user
                    Driver driver = new Driver(fname, lname, uid, pwd, "DL" + System.currentTimeMillis());
                    // Note: In a complete implementation, you would have DriverDAO.insertDriverRecord(driver)
                    // For now, we'll use the base UserDAO since a Driver extends User
                    UserDAO.insertUserRecord(driver);
                    return true;
                default:
                    return false;
            }
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

            Customer customer = new Customer(fname, lname, email, password, address, zone);
            CustomerDAO.insertCustomerRecord(customer);
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

            Driver driver = new Driver(fname, lname, email, password, driverLicenseNumber);
            DriverDAO.insertDriverRecord(driver);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticate(String uid, String pwd, String category) {
        try {
            User user = null;

            // Look up user based on category
            switch (category.toLowerCase()) {
                case "customer":
                    // For customers, we need to look them up by email (which is the uid in the GUI)
                    // The customer ID format is different from email, so we look up by email
                    user = UserDAO.retrieveUserRecordByEmail(uid);
                    if (user != null && user.verifyPassword(pwd)) {
                        // Load full customer details using the email to find the Customer record
                        Customer customer = CustomerDAO.retrieveCustomerByEmail(uid);
                        if (customer != null) {
                            this.currentUser = customer;
                            this.currentUserCategory = category;
                            return true;
                        } else {
                            // If customer record not found in customer table, use the user record
                            this.currentUser = user;
                            this.currentUserCategory = category;
                            return true;
                        }
                    }
                    break;
                case "clerk":
                    user = UserDAO.retrieveUserRecordByEmail(uid);
                    if (user != null && user.verifyPassword(pwd)) {
                        this.currentUser = user;
                        this.currentUserCategory = category;
                        return true;
                    }
                    break;
                case "manager":
                    user = UserDAO.retrieveUserRecordByEmail(uid);
                    if (user != null && user.verifyPassword(pwd)) {
                        this.currentUser = user;
                        this.currentUserCategory = category;
                        return true;
                    }
                    break;
                case "driver":
                    user = UserDAO.retrieveUserRecordByEmail(uid);
                    if (user != null && user.verifyPassword(pwd)) {
                        // Load full driver details using the user ID to find the Driver record
                        Driver driver = DriverDAO.retrieveDriverById(user.getID());
                        if (driver != null) {
                            this.currentUser = driver;
                            this.currentUserCategory = category;
                            return true;
                        } else {
                            // If driver record not found in driver table, use the user record
                            this.currentUser = user;
                            this.currentUserCategory = category;
                            return true;
                        }
                    }
                    break;
                default:
                    return false;
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
            Customer sender;
            if (currentUser != null && currentUserCategory.equalsIgnoreCase("customer")) {
                // If current user is a customer, use their information
                sender = (Customer) currentUser;
                sender.setAddress(pickupAddress); // Update address for this shipment
            } else {
                // Create a new customer object with provided info
                String senderFirstName = senderName.split(" ")[0];
                String senderLastName = senderName.split(" ").length > 1 ? senderName.split(" ")[1] : "";
                String senderEmail = senderName.toLowerCase().replace(" ", "") + System.currentTimeMillis() + "@customer.com";

                sender = new Customer(senderFirstName, senderLastName, senderEmail, "default", pickupAddress, 1);
                // Insert the new customer to the database
                CustomerDAO.insertCustomerRecord(sender);
            }

            // For recipient, create a customer object using the destination info
            String recipientFirstName = destAddress.split(" ").length > 0 ? destAddress.split(" ")[0] : "Recipient";
            String recipientLastName = destAddress.split(" ").length > 1 ? destAddress.split(" ")[1] : "Customer";
            String recipientEmail = recipientFirstName.toLowerCase() + System.currentTimeMillis() + "@recipient.com";

            Customer recipient = new Customer(recipientFirstName, recipientLastName, recipientEmail, "default", destAddress, 1);
            // Insert recipient as a customer to the database
            CustomerDAO.insertCustomerRecord(recipient);

            // Create shipment with default values (will be updated by caller)
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
            Driver driver = null;
            if (driverDLN != null && !driverDLN.isEmpty()) {
                // Look up the driver from the database
                driver = DriverDAO.retrieveDriverByDln(driverDLN);
                if (driver == null) {
                    JOptionPane.showMessageDialog(null, "Driver with DLN " + driverDLN + " not found.");
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
    public Driver addDriver(String firstName, String lastName, String email, String password, String driverLicenseNumber) {
        try {
            Driver driver = new Driver(firstName, lastName, email, password, driverLicenseNumber);
            DriverDAO.insertDriverRecord(driver);
            return driver;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Driver> listDrivers() {
        try {
            return DriverDAO.readAllDrivers();
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
        Driver currentDriver = (Driver) currentUser;

        // Get all vehicles and find those assigned to the current driver
        List<Vehicle> allVehicles = listVehicles();
        for (Vehicle v : allVehicles) {
            if (v.getAssignedDriver() != null &&
                v.getAssignedDriver().getdln().equals(currentDriver.getdln())) {
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

        return result;
    }
}