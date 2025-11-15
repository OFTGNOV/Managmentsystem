package userModule;

public class Clerk extends User {
	private String id;
	
	//Parameterized Constructor
	public Clerk(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.id = "CLK-" + Math.floor(Math.random() * 10000); // Generating a random ID for Clerk
	}
	
	//Copy Constructor
	public Clerk(Clerk other) {
		super(other);
		this.id = other.id;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nClerk ID: " + id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	

}
