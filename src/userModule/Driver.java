package userModule;

public class Driver extends User {
	private String id;
	
	//Parameterized Constructor
	public Driver(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.id = "DRV-" + Math.floor(Math.random() * 10000); // Generating a random ID for Driver
	}
	
	//Copy Constructor
	public Driver(Driver other) {
		super(other);
		this.id = other.id;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nDriver ID: " + id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	

}
