package userModule;

public class Customer extends User {
	private String custID;
	private String address;
	private int zone;
	
	
	//Parameterized Constructor
	public Customer(String Fname,String Lname, String email, String password, String address, int zone) {
		super(Fname, Lname, email, password);
		// generate a id by default
		this.generateRandomCustID();
		this.address = address;
		this.zone = zone;
	}
	
	//Copy Constructor
	public Customer(Customer other) {
		super(other);
		this.custID = other.custID;
		this.address = other.address;
		this.zone = other.zone;
	}
	
	private void generateRandomCustID() {
		// fallback id generation similar to other user subclasses
		this.custID = "CUST-" + (int)Math.floor(Math.random() * 10000);
	}

	
	@Override
	public String ToString() {
		return super.ToString() + 
				", \nCustomer ID: " + custID + 
				", \nAddress: " + address +
				", \nZone: " + zone + "\n";
	}
	
	//getters and setters
	public String getCustId() {
		return custID;
	}
	
	public void setCustId(String id) {
		this.custID = id;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getZone() {
		return zone;
	}
	
	public void setZone(int zone) {
		this.zone = zone;
	}

}