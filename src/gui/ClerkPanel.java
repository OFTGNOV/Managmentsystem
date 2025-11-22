package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

import shipmentModule.*;
import userModule.*;
import databaseModule.sDAO.*;

/**
 * Clerk view: list shipments, change status, and assign to vehicles
 */
public class ClerkPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final CoreSystem system;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Tracking Number","Sender","Pickup","Destination","Status","Weight"}, 0);

    public ClerkPanel(CoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout(6,6));

        JTable table = new JTable(model);
        refresh();

        JPanel top = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton setInTransit = new JButton("Set In Transit");
        JButton setDelivered = new JButton("Set Delivered");
        JButton assignVehicle = new JButton("Assign Vehicle");

        top.add(refreshBtn);
        top.add(setInTransit);
        top.add(setDelivered);
        top.add(assignVehicle);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button actions
        refreshBtn.addActionListener((ActionEvent e) -> refresh());

        setInTransit.addActionListener((ActionEvent e) -> {
            int r = table.getSelectedRow();
            if (r < 0) { 
                JOptionPane.showMessageDialog(this,"Select a shipment","Error",JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            String trackingNumber = (String) model.getValueAt(r,0);
            system.updateShipmentStatus(trackingNumber, "IN_TRANSIT");
            refresh();
        });

        setDelivered.addActionListener((ActionEvent e) -> {
            int r = table.getSelectedRow();
            if (r < 0) { 
                JOptionPane.showMessageDialog(this,"Select a shipment","Error",JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            String trackingNumber = (String) model.getValueAt(r,0);
            system.updateShipmentStatus(trackingNumber, "DELIVERED");
            refresh();
        });

        assignVehicle.addActionListener((ActionEvent e) -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this,"Select a shipment","Error",JOptionPane.WARNING_MESSAGE);
                return;
            }

            String trackingNumber = (String) model.getValueAt(r,0);
            shipmentModule.Shipment shipment = system.getShipment(trackingNumber);

            if (shipment == null) return;

            // Choose vehicle
            java.util.List<vehicleAndRoutingModule.Vehicle> vehicles = system.listVehicles();
            
            if (vehicles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No vehicles available", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String[] vehicleNames = new String[vehicles.size()];
            for (int i = 0; i < vehicles.size(); i++) {
                vehicleNames[i] = vehicles.get(i).getLicensePlate() + " - " + vehicles.get(i).getVehicleType();
            }

            String vehicleSelection = (String) JOptionPane.showInputDialog(
                    this,
                    "Select Vehicle:",
                    "Assign Vehicle",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    vehicleNames,
                    vehicleNames[0]
            );
            if (vehicleSelection == null) return;

            // Extract license plate from selection
            String[] parts = vehicleSelection.split(" - ", 2); // Limit to 2 parts to handle cases where license plate has " - " in it
            String licensePlate = parts[0];
            
            // Enter route time - in a real system this would be more sophisticated
            String routeTime = JOptionPane.showInputDialog(this, "Enter route time (e.g., 09:00-12:00):");
            if (routeTime == null || routeTime.trim().isEmpty()) return;

            // Assign shipment with validation
            boolean assigned = system.assignShipmentToVehicle(trackingNumber, licensePlate, routeTime);
            if (assigned) {
                JOptionPane.showMessageDialog(this,"Shipment assigned to vehicle successfully.","Success",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot assign shipment.\nCheck vehicle weight/quantity limits or route conflict.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            refresh();
        });
    }

    private void refresh() {
        model.setRowCount(0);
        java.util.List<shipmentModule.Shipment> shipments = system.listShipments();
        for (shipmentModule.Shipment s : shipments) {
            model.addRow(new Object[]{
                    s.getTrackingNumber(),
                    s.getSender().getFirstName() + " " + s.getSender().getLastName(),
                    s.getSender().getAddress(),
                    s.getRecipent().getAddress(),
                    s.getStatus(),
                    s.getWeight()
            });
        }
    }
}