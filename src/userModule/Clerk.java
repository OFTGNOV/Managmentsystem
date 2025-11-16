package userModule;

public class Clerk extends User {
	private String clkID;
	
	//Parameterized Constructor
	public Clerk(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.generateClerkID();
	}
	
	private void generateClerkID() {
		this.clkID = "CLK-" + (int)Math.floor(Math.random() * 10000);
	}
	
	public void generateClerkID(String userId) {
		if (userId == null) {
			// if null provided, fall back to random idS
			generateClerkID();
			return;
		}
		this.clkID = "clk" + userId + "-" + super.getLastName().toLowerCase();
	}
	
	//Copy Constructor
	public Clerk(Clerk other) {
		super(other);
		this.clkID = other.clkID;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nClerk ID: " + clkID + "\n";
	}
	
	public String getClkId() {
		return clkID;
	}

	public void setClkId(String clkID) {
		this.clkID = clkID;
	}
	

}
