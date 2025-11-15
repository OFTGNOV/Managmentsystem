package userModule;

import java.util.UUID;

public class User {
	protected String id;
    protected String Fname;
    protected String Lname;
    protected String email;
    protected String password;
    protected UserRole role;
    
    
    ///Paramatized Contsturctor
    public User(String Fname, String Lname, String email, String password, UserRole role) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.Fname = Fname;
        this.Lname = Lname;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    //copy constructor
    public User(User other) {
		this.id = other.id;
		this.Fname = other.Fname;
		this.Lname = other.Lname;
		this.email = other.email;
		this.password = other.password;
		this.role = other.role;
	}
    
    public String ToString() {
		return "User ID: " + id + 
				", \nFirst Name: " + Fname + 
				", \nLast Name " + Lname + 
				", \nEmail: " + email + 
				", \nPassword: " + password +
				", \nRole: " + role;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return Fname;
	}

	public void setName(String name) {
		this.Fname = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
   	public UserRole getRole() {
		return role;
	}
   	
   	public void setRole(UserRole role) {
   		this.role = role;
   	}
   	
}