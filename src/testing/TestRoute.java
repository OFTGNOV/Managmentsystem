package testing;

import userModule.Customer;
import userModule.Driver;
import vehicleAndRoutingModule.Vehicle;
import vehicleAndRoutingModule.Route;
import shipmentModule.Shipment;
import shipmentModule.PackageType;
import shipmentModule.ShipmentStatus;

public class TestRoute {
    public static void main(String[] args) {
        Customer sender = new Customer("Send","One","s@ex.com","pass","AddrS",1);
        Customer recip1 = new Customer("Rec","One","r1@ex.com","pass","Addr1",2);
        Customer recip2 = new Customer("Rec","Two","r2@ex.com","pass","Addr2",2);
        Driver d = new Driver("Drv","One","d@ex.com","pass","DL123");
        Vehicle v = new Vehicle(d,"ABC-123","Van",1000.0,10);

        Shipment s1 = new Shipment(sender, recip1, 10.0, 10, 10, 10, PackageType.STANDARD);
        Shipment s2 = new Shipment(sender, recip2, 20.0, 10, 10, 10, PackageType.STANDARD);

        Route route1 = new Route(1, v);
        System.out.println("Add s1: " + route1.addShipment(s1));
        System.out.println("Add s2: " + route1.addShipment(s2));

        System.out.println("Route planned shipments count: " + route1.getPlannedShipmentCount());

        boolean started1 = route1.tryStartRoute();
        System.out.println("Started route1: " + started1);

        // try to create and start a second route with the same vehicle
        Route route2 = new Route(2, v);
        System.out.println("Attempt to start route2 while route1 is active: " + route2.tryStartRoute());

        // end route1 and try route2 again
        route1.endRoute();
        System.out.println("Ended route1. Now attempt to start route2: " + route2.tryStartRoute());

        // Deliver s1 and ensure vehicle removes it but route still contains it
        s1.updateStatus(ShipmentStatus.DELIVERED);
        System.out.println("After delivering s1 -> Vehicle assigned shipments: " + v.getAssignedShipments().size());
        System.out.println("Route1 planned shipments (should still include delivered): " + route1.getPlannedShipmentCount());

        // Print route and vehicle summaries
        System.out.println(route1);
        System.out.println(route2);
        System.out.println(v);
    }
}