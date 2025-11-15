package userModule;

public class Driver extends User {
	private String dln; // Driver license number
	
	//Parameterized Constructor
	public Driver(String Fname,String Lname, String email, String password, String dln) {
		super(Fname, Lname, email, password);
		this.dln = dln;
	}
	
	//Copy Constructor
	public Driver(Driver other) {
		super(other);
		this.dln = other.dln;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nDrivers Licesne Number: " + dln + "\n";
	}
	
	public String getdln() {
		return dln;
	}
	
	public void setDln(String dln) {
		this.dln = dln;
	}
	

}
