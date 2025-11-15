package userModule;

public class Clerk extends User {
	private String clerkID;
	
	//Parameterized Constructor
	public Clerk(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.generateClerkID();
	}
	
	private void generateClerkID() {
		this.clerkID = "CLK-" + (int)Math.floor(Math.random() * 10000);
	}
	
	public void generateClerkID(String userId) {
		if (userId == null) {
			// if null provided, fall back to random idS
			generateClerkID();
			return;
		}
		this.clerkID = "clk" + userId;
	}
	
	//Copy Constructor
	public Clerk(Clerk other) {
		super(other);
		this.clerkID = other.clerkID;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nClerk ID: " + clerkID + "\n";
	}
	
	public String getId() {
		return clerkID;
	}
	
	public void setId(String clerkID) {
		this.clerkID = clerkID;
	}
	

}
