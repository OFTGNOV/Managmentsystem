package userModule;

public class Manager extends User {
	private String mngID; // Manager ID
	
	//Parameterized Constructor
	public Manager(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.generateMngID();
	}
	
	private void generateMngID() {
		this.mngID = "MNG-" + (int)Math.floor(Math.random() * 10000);
	}
	
	// Overloaded method to generate ID based on userId and last name
	public void generateMngID(String userId) {
		if (userId == null) {
			// if null provided, fall back to random id
			generateMngID();
			return;
		}
		this.mngID = "mng" + userId + "-" + super.getLastName().toLowerCase();
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
	
	public String getMngID() {
		return mngID;
	}
	public void setMngID(String mngID) {
		this.mngID = mngID;
	}
}