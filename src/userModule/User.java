package userModule;


public class User {
	protected int ID;
    protected String Fname;
    protected String Lname;
    protected String email;
    protected UserType userType;
    protected String address; //Customer Address assigned when a customer signs up
    protected int zone; // The zone a customer belongs to.
    protected String passwordHash;
    // Salt is used for unique hashing. Means even if two users have the same password, their hashes will differ.
    protected String salt; 
    
    // 4-parameter constructor for backward compatibility (when userType is not known)
    public User(String Fname, String Lname, String email, String password) {
        this(Fname, Lname, email, password, UserType.CUSTOMER); // Default to CUSTOMER
    }

    ///Paramatized Contsturctor
    public User(String Fname, String Lname, String email, String password, UserType userType) {
    	this.ID = 0; // default ID, database layer should set this appropriately
        this.Fname = Fname;
        this.Lname = Lname;
        this.email = email;
        this.userType = userType;
        this.address = "";
        this.zone = 0;
        // hash and store the provided password (can be null)
        setPassword(password);
    }
    
    //copy constructor
    public User(User other) {
        this.Fname = other.Fname;
        this.Lname = other.Lname;
        this.email = other.email;
        this.userType = other.userType;
        this.address = other.address;
        this.zone = other.zone;
        this.passwordHash = other.passwordHash;
        this.salt = other.salt;
     
    }
    
    // Simple helpers that use PasswordHasher (PBKDF2) defined in the same package.
    public void setPassword(String password) {
        if (password == null) {
            this.passwordHash = null;
            this.salt = null;
            return;
        }
        // generate salt and hash
        String newSalt = PasswordHasher.generateSaltBase64();
        byte[] saltBytes = java.util.Base64.getDecoder().decode(newSalt);
        String hash = PasswordHasher.hashPassword(password.toCharArray(), saltBytes);
        this.salt = newSalt;
        this.passwordHash = hash;
    }
    
    // Verifies an attempted password against the stored hash
    public boolean verifyPassword(String attemptedPassword) {
        if (attemptedPassword == null || this.passwordHash == null || this.salt == null) return false;
        return PasswordHasher.verifyPassword(attemptedPassword.toCharArray(), this.salt, this.passwordHash);
    }
    

    // Getters and Setters
    public int getID() {
		return ID;
	}
    
    public void setID(int ID) {
    	this.ID = ID;
    }

    // Backwards-compatible lowercase accessor used in some modules
    public int getId() {
        return getID();
    }
    
    public String getFirstName() {
        return Fname;
    }

    public void setFirstName(String name) {
        this.Fname = name;
    }

    public String getLastName() {
        return Lname;
    }

    public void setLastName(String lastName) {
        this.Lname = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public UserType getUserType() {
		return userType;
	}
    
    public void setUserType(UserType userType) {
    			this.userType = userType;
    }

    // Expose only hash (avoid returning raw password)
    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    // Package-private helper so DAO in a different package cannot call it; however still accessible
    // within the module if classes are in same runtime. We make it public for simplicity.
    public void setPasswordHashAndSalt(String passwordHash, String salt) {
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

}