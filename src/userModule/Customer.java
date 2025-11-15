package userModule;

public class Customer extends User {
	private String id;
	
	//Parameterized Constructor
	public Customer(String Fname,String Lname, String email, String password) {
		super(Fname, Lname, email, password);
		this.id = "CST-" + Math.floor(Math.random() * 10000); // Generating a random ID for Customer
	}
	
	//Copy Constructor
	public Customer(Customer other) {
		super(other);
		this.id = other.id;
	}
	
	public String ToString() {
		return super.ToString() + 
				", \nCustomer ID: " + id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

}
