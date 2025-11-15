package userModule;

public class Customer extends User {
	private String id;
	private String address;
	private int zone;
	
	
	//Parameterized Constructor
	public Customer(String Fname,String Lname, String email, String password, String address, int zone) {
		super(Fname, Lname, email, password);
		this.id = "CST-" + Math.floor(Math.random() * 10000); // Generating a random ID for Customer
		this.address = address;
		this.zone = zone;
	}
	
	//Copy Constructor
	public Customer(Customer other) {
		super(other);
		this.id = other.id;
		this.address = other.address;
		this.zone = other.zone;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nCustomer ID: " + id + 
				", \nAddress: " + address +
				", \nZone: " + zone + "\n";
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
