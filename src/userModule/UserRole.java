package userModule;

public enum UserRole {
	//Customers can create accounts, log in, and request shipments.
	CUSTOMER,
	
	//Clerks process shipment requests, assign packages to delivery routes, and handle payments.
	Clerk,
	
	//Drivers can view their assigned deliveries and update package status (e.g., In Transit, Delivered). 
	Drivers,
	
	//Managers can manage user accounts and oversee all operations. Also will act as psuedo-admin.	
	MANAGER,
	
	// For User objects that have not been assigned a role yet
	UNDEFINED
}