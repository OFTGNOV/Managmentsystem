package userModule;

public class Manager extends User {
	private String id;
	
	//Parameterized Constructor
	public Manager(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.id = "MNG-" + Math.floor(Math.random() * 10000); // Generating a random ID for Manager
	}
	
	//Copy Constructor
	public Manager(Manager other) {
		super(other);
		this.id = other.id;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nManager ID: " + id + "\n";
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}