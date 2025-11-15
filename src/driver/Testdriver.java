package driver;

import shipmentModule.PackageType;
import shipmentModule.Shipment;
import userModule.*;
import vehicleAndRoutingModule.Vehicle;
import billingAndPaymentModule.*;

// Uncommented test driver for various modules
public class Testdriver {
	public static void main(String[] args) {
		// Shipment Module Tests
		//Customer sender = new Customer("Gem", "Gray", "graygem@gmail.com", "gem123", "789 Gotham St", 3);
		//Customer recipent = new Customer("Harry", "Potter", "hpotter@gmail.com", "harry123", "101 Hogwarts Rd", 4);
		//shipmentTest(sender, recipent);
	
		
		// Vehicle Module Tests
		//Driver assignedDriver = new Driver("Claude", "Denver", "cldenver@gmail.com", "claude123");
		//vehicleTest(assignedDriver);
		
		// User Module Tests
//		managerTest();
//		driverTest();
//		clerkTest();
//		customerTest();
		
		// Billing and Payment Module Tests
		Customer c1 = new Customer("Earl", "Gray", "earl@gmail.com", "earl123", "123 Baker St", 1);
		Customer c2 = new Customer("Nevil", "Long", "nevil@gmail.com", "nevil123", "234 Diagon Alley", 2);
		Shipment s1 = new Shipment(c1, c2, 3.0, 10.0, 5.0, 2.0, PackageType.STANDARD);
		Invoice inv1 = new Invoice(PaymentStatus.UNPAID, 100.0, s1, c1);
		System.out.println(inv1.toString());
		
	}
	
	public static void managerTest() {
		Manager m1 = new Manager("Alice", "Smith", "alice1@gmail.com", "alice123");
		System.out.println(m1.ToString());
	}
	
	public static void driverTest() {
		Driver d1 = new Driver("Bob", "Johnson", "bob@gmail.com", "bob123");
		System.out.println(d1.ToString());
	}
	
	public static void clerkTest() {
		Clerk c1 = new Clerk("Charlie", "Brown", "charlieb@gmail.com", "charlie123");
		System.out.println(c1.ToString());
		}
	
	public static void customerTest() {
		Customer c1 = new Customer("Diana", "Prince", "Prince@gmail.com", "diana123", "123 Themyscira St", 1);
		Customer c2 = new Customer("Blue", "Beetle", "beeetke@gmail.com", "blue123", "456 El Paso Rd", 2);
		System.out.println(c1.ToString());
	}

	public static void shipmentTest(Customer sender, Customer recipent) {
		/*Shipment(Customer sender, Customer recipent, double weight, 
		 * double length, double width, double height, PackageType pType)
		 */
		Shipment s1 = new Shipment(sender, recipent, 2.5, 1.2, 1.7, 12, PackageType.FRAGILE);
		
		System.out.println(s1.toString());
	}
	
	public static void vehicleTest(Driver assignedDriver) {
		Vehicle v1 = new Vehicle(assignedDriver, "ABC123", "Truck", 1000.0, 10);
		System.out.println("Initial Vehicle State: \n" + v1.toString());	
	}
	

}
