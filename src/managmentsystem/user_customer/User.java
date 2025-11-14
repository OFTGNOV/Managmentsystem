package managmentsystem.user_customer;

public class User {
	protected String id;
    protected String name;
    protected String email;
    protected String password;
    
    // Paramatized Contsturctor
    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
    
//    // Common user actions
//    public void login() {
//        System.out.println(name + " logged in successfully.");
//    }
//
//    public void logout() {
//        System.out.println(name + " logged out.");
//    }

    // Getters and setters
    
}