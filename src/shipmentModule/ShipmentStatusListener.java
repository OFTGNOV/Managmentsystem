package shipmentModule;

public interface ShipmentStatusListener {
    void onStatusChanged(Shipment shipment, ShipmentStatus newStatus);
}
