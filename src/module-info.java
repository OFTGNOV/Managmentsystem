module Managmentsystem {
	//
	exports managmentsystem.billing_payment;
	exports managmentsystem.shipment;
	exports managmentsystem.user_customer;
	exports managmentsystem.vehicle_routing;
	exports managmentsystem.database;
	requires org.mongodb.driver.sync.client;
	requires org.mongodb.bson;
	requires org.mongodb.driver.core;
	
}