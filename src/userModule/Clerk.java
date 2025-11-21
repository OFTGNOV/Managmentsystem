package userModule;

public class Clerk extends User {
	private String clkID;
	
	//Parameterized Constructor
	public Clerk(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.generateRandomClerkID();
	}
	
	private void generateRandomClerkID() {
		this.clkID = "CLK-" + System.currentTimeMillis() + "-" + (int)Math.floor(Math.random() * 10000);
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
