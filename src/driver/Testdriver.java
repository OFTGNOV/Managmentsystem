package driver;

import userModule.*;
import shipmentModule.PackageType;
import shipmentModule.Shipment;

public class Testdriver {
	public static void main(String[] args) {
		userTest();
		//shipmentTest();
	}
	
	public static void userTest() {
		User u1 = new User("Alice", "Smith", "alice1@gmail.com", "alice123", UserRole.CUSTOMER);
		System.out.println(u1.ToString());
	}

	public static void shipmentTest() {
		Shipment s1 = new Shipment("S001", "John Doe", "123 Main St, Cityville",
				 5, 10.0, 15.0, 10.0, 5.0, PackageType.EXPRESS);
		
		s1.calculateShippingCost();
		System.out.println(s1.toString());
	}
	

}
