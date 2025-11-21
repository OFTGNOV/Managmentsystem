package userModule;

public class Manager extends User {
	private String mngID; // Manager ID
	
	//Parameterized Constructor
	public Manager(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.generateRandomID();
	}
	
	private void generateRandomID() {
		// Generate a random manager ID as a fallback
		this.mngID = "MNG-" + (int)Math.floor(Math.random() * 10000);
	}
	
	//Copy Constructor
	public Manager(Manager other) {
		super(other);
		this.mngID = other.mngID;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nManager ID: " + mngID + "\n";
	}
	
	//getters and setters
	public String getMngID() {
		return mngID;
	}
	public void setMngID(String mngID) {
		this.mngID = mngID;
	}
}