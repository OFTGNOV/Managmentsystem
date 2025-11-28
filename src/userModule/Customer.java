package userModule;

/**
 * Customer class that extends User for backward compatibility.
 * This class exists to maintain compatibility with existing code that expects Customer objects.
 */
public class Customer extends User {
    
    public Customer(String fname, String lname, String email, String password, String address, int zone) {
        super(fname, lname, email, password, UserType.CUSTOMER);
        this.address = address;
        this.zone = zone;
    }
    
    // Constructor to convert from User object
    public Customer(User user) {
        super(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getUserType());
        this.ID = user.getID();
        this.address = user.getAddress();
        this.zone = user.getZone();
        this.passwordHash = user.getPasswordHash();
        this.salt = user.getSalt();
    }

    // Additional methods specific to Customer if needed
    public int getCustId() {
        return this.ID; // Compatibility method - maps to ID
    }
    
    // Getters and setters can be inherited from User class
}