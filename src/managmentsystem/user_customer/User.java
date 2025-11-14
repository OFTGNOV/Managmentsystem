package managmentsystem.user_customer;

public class User {
	protected String id;
    protected String Fname;
    protected String Lname;
    protected String email;
    protected String password;
    protected UserRole role;
    
    
    ///Paramatized Contsturctor
    public User(String id, String Fname, String Lname, String email, String password) {
        this.id = id;
        this.Fname = Fname;
        this.Lname = Lname;
        this.email = email;
        this.password = password;
        this.role = UserRole.UNDEFINED; // Default role
    }
    
    //Copy Constructor
    public User(User other) {
		this.id = other.id;
		this.Fname = other.Fname;
		this.Lname = other.Lname;
		this.email = other.email;
		this.password = other.password;
		this.role = other.role;
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