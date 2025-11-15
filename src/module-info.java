module Managmentsystem {
	exports billingAndPaymentModule;
	exports shipmentModule;
	exports userModule;
	exports VehicleAndRoutingModule;
	exports databaseModule;
	requires org.mongodb.driver.sync.client;
	requires org.mongodb.bson;
	requires org.mongodb.driver.core;
}