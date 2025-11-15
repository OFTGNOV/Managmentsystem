package driver;

import shipmentModule.PackageType;
import shipmentModule.Shipment;
import userModule.*;
import vehicleAndRoutingModule.Vehicle;

public class Testdriver {
	public static void main(String[] args) {
		//userTest();
		//shipmentTest();
		vehicleTest();
	}
	
	public static void managerTest() {
		Manager m1 = new User("Alice", "Smith", "alice1@gmail.com", "alice123");
		System.out.println(u1.ToString());
	}

	public static void shipmentTest() {
		Shipment s1 = new Shipment("S001", "John Doe", "123 Main St, Cityville",
				 5, 10.0, 15.0, 10.0, 5.0, PackageType.EXPRESS);
		
		s1.calculateShippingCost();
		System.out.println(s1.toString());
	}
	
	public static void vehicleTest() {
		Vehicle v1 = new Vehicle("ABC123", "Truck", 1000.0, 10);
		System.out.println("Initial Vehicle State: \n\t" + v1.toString());
		
	}
	

}
