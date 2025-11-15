package userModule;


public class User {
    protected String Fname;
    protected String Lname;
    protected String email;
    protected String password;
    
    ///Paramatized Contsturctor
    public User(String Fname, String Lname, String email, String password) {
        this.Fname = Fname;
        this.Lname = Lname;
        this.email = email;
        this.password = password;
    }
    
    //copy constructor
    public User(User other) {
		this.Fname = other.Fname;
		this.Lname = other.Lname;
		this.email = other.email;
		this.password = other.password;
	}
    
    public String ToString() {
		return 	", \nFirst Name: " + Fname + 
				", \nLast Name " + Lname + 
				", \nEmail: " + email + 
				", \nPassword: " + password;
	}

    // Getters and Setters
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
   	
}