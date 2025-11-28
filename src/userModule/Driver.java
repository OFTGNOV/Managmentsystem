package userModule;

/**
 * Driver class that extends User for backward compatibility.
 * This class exists to maintain compatibility with existing code that expects Driver objects.
 */
public class Driver extends User {
    private String dln; // Driver's License Number

    public Driver(String fname, String lname, String email, String password, String dln) {
        super(fname, lname, email, password, UserType.DRIVER);
        this.dln = dln;
    }
    
    // Constructor to convert from User object
    public Driver(User user, String dln) {
        super(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getUserType());
        this.ID = user.getID();
        this.address = user.getAddress();
        this.zone = user.getZone();
        this.passwordHash = user.getPasswordHash();
        this.salt = user.getSalt();
        this.dln = dln;
    }
    
    // Constructor to convert from User object without dln (will use email or other fields)
    public Driver(User user) {
        super(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getUserType());
        this.ID = user.getID();
        this.address = user.getAddress();
        this.zone = user.getZone();
        this.passwordHash = user.getPasswordHash();
        this.salt = user.getSalt();
        // dln might need to be stored differently in the new design
        this.dln = "DL" + user.getID(); // Default if not available
    }

    public String getdln() {
        return dln;
    }

    public void setDln(String dln) {
        this.dln = dln;
    }
}