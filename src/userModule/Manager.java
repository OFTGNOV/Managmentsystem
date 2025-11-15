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
	
	public void generateMngID(String userId) {
		if (userId == null) {
			// if null provided, fall back to random id
			generateMngID();
			return;
		}
		this.mngID = "mng" + userId;
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