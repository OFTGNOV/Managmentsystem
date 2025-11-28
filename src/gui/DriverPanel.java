package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import shipmentModule.*;
import userModule.*;

public class DriverPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private CoreSystem system;
    private JTable deliveryTable;
    private DefaultTableModel tableModel;
    private JButton updateStatusBtn, refreshBtn;

    public DriverPanel(CoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"Tracking Number", "Sender", "Pickup Address", "Delivery Address", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        deliveryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(deliveryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        updateStatusBtn = new JButton("Update Status");
        refreshBtn = new JButton("Refresh");
        JButton signOutBtn = new JButton("Sign Out");

        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(signOutBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // Sign out button action
        signOutBtn.addActionListener(e -> signOut());

        // Button actions
        updateStatusBtn.addActionListener(e -> updateShipmentStatus());
        refreshBtn.addActionListener(e -> loadDeliveries());

        // Load deliveries initially
        loadDeliveries();
    }

    // Load assigned deliveries from CoreSystem
    private void loadDeliveries() {
        tableModel.setRowCount(0); // clear table

        for (shipmentModule.Shipment s : system.getAssignedDeliveries()) {
            tableModel.addRow(new Object[]{
                    s.getTrackingNumber(),
                    s.getSender().getFirstName() + " " + s.getSender().getLastName(),
                    s.getSender().getAddress(),
                    s.getRecipent().getAddress(),
                    s.getStatus()
            });
        }
    }

    private void updateShipmentStatus() {
        int row = deliveryTable.getSelectedRow();
        if (row >= 0) {
            String trackingNumber = (String) tableModel.getValueAt(row, 0);

            // Status options
            String[] statusOptions = {"IN_TRANSIT", "DELIVERED"};
            String newStatusStr = (String) JOptionPane.showInputDialog(
                    this,
                    "Select new status for shipment: " + trackingNumber,
                    "Update Shipment Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statusOptions,
                    tableModel.getValueAt(row, 4) // current status
            );

            if (newStatusStr != null) {
                // Convert string to ShipmentStatus enum
                ShipmentStatus newStatus = ShipmentStatus.PENDING; // default
                try {
                    newStatus = ShipmentStatus.valueOf(newStatusStr.toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Invalid status: " + newStatusStr, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update the shipment status through CoreSystem
                boolean updated = system.updateShipmentStatus(trackingNumber, newStatus.toString());
                if (!updated) {
                    JOptionPane.showMessageDialog(this, "Failed to update shipment status in database", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                loadDeliveries(); // refresh table
                JOptionPane.showMessageDialog(this, "Shipment status updated successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a shipment to update");
        }
    }

    private void signOut() {
        // Clear current user from system
        system.setCurrentUser(null, null);

        // Find the SmartShipGUI frame and call showLoginPanel
        Container parent = getParent();
        while (parent != null && !(parent instanceof SmartShipGUI)) {
            parent = parent.getParent();
        }

        if (parent instanceof SmartShipGUI) {
            SmartShipGUI gui = (SmartShipGUI) parent;
            gui.showLoginPanel();
        }
    }
}