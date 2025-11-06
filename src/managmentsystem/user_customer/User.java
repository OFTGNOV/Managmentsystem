package managmentsystem.user_customer;

public class User {

	protected String id;
    protected String name;
    protected String email;
    protected String password;
    
    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    // Common user actions
    public void login() {
        System.out.println(name + " logged in successfully.");
    }

    public void logout() {
        System.out.println(name + " logged out.");
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}