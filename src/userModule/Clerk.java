package userModule;

/**
 * Clerk class that extends User for backward compatibility.
 * This class exists to maintain compatibility with existing code that expects Clerk objects.
 */
public class Clerk extends User {
    
    public Clerk(String fname, String lname, String email, String password) {
        super(fname, lname, email, password, UserType.CLERK);
    }
    
    // Constructor to convert from User object
    public Clerk(User user) {
        super(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getUserType());
        this.ID = user.getID();
        this.address = user.getAddress();
        this.zone = user.getZone();
        this.passwordHash = user.getPasswordHash();
        this.salt = user.getSalt();
    }
}