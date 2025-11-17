package testing;

import vehicleAndRoutingModule.Vehicle;
import vehicleAndRoutingModule.VehicleManager;
import shipmentModule.Shipment;
import shipmentModule.PackageType;
import userModule.Customer;
import userModule.Driver;

import java.time.LocalDateTime;

public class TestVehicleManager {
    public static void main(String[] args) {
        VehicleManager vm = new VehicleManager();
        Driver driver = new Driver("John", "Doe", "j@x.com", "pass", "DLN123");
        Customer sender = new Customer("S", "One", "s1@x.com", null, "addr", 1);
        Customer recv = new Customer("R", "Two", "r2@x.com", null, "addr2", 2);
        Vehicle v = new Vehicle(driver, "ABC-123", "Van", 1000.0, 100);
        vm.registerVehicle(v);

        Shipment s = new Shipment(sender, recv, 10.0, 30.0, 20.0, 10.0, PackageType.STANDARD);

        boolean assigned = vm.assignShipmentToVehicle(v.getVehicleId(), s, LocalDateTime.now(), LocalDateTime.now().plusHours(4), "ROUTE-1");
        System.out.println("Assigned: " + assigned);
        System.out.println("Vehicle utilization: " + v.getUtilizationPercentage() + "%");
    }
}
