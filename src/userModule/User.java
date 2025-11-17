package userModule;


public class User {
	protected int ID;
    protected String Fname;
    protected String Lname;
    protected String email;
    protected String passwordHash;
    // Salt is used for unique hashing. Means even if two users have the same password, their hashes will differ.
    protected String salt; 
    
    ///Paramatized Contsturctor
    public User(String Fname, String Lname, String email, String password) {
    	this.ID = ID;
        this.Fname = Fname;
        this.Lname = Lname;
        this.email = email;
        // hash and store the provided password (can be null)
        setPassword(password);
    }
    
    //copy constructor
    public User(User other) {
        this.Fname = other.Fname;
        this.Lname = other.Lname;
        this.email = other.email;
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
    
    public String ToString() {
        return  ", \nFirst Name: " + Fname + 
                ", \nLast Name " + Lname + 
                ", \nEmail: " + email + 
                ", \nPasswordHash: " + passwordHash +
                ", \nSalt: " + salt;
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
    
}