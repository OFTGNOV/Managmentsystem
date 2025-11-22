package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

import shipmentModule.*;
import reportingModule.*;

/**
 * Manager: simple report display + assign shipments to vehicles
 */
public class ManagerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final CoreSystem system;
    private final JTextArea reportArea = new JTextArea();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Tracking Number","Sender","Pickup","Destination","Status","Weight"}, 0);
    private final JTable shipmentTable = new JTable(model);

    public ManagerPanel(CoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout(6,6));

        // Report area
        reportArea.setEditable(false);
        add(new JScrollPane(reportArea), BorderLayout.EAST);

        // Shipment table for assignment
        refreshShipmentTable();
        add(new JScrollPane(shipmentTable), BorderLayout.CENTER);

        // Buttons panel
        JPanel bottom = new JPanel();
        JButton refreshReportBtn = new JButton("Refresh Report");
        JButton refreshTableBtn = new JButton("Refresh Shipments");
        JButton assignVehicleBtn = new JButton("Assign Vehicle");
        JButton addVehicleBtn = new JButton("Add Vehicle");
        JButton generateRevenueReportBtn = new JButton("Generate Revenue Report");
        JButton generateShipmentReportBtn = new JButton("Generate Shipment Report");
        JButton generateDeliveryReportBtn = new JButton("Generate Delivery Report");
        JButton generateVehicleReportBtn = new JButton("Generate Vehicle Report");

        bottom.add(refreshReportBtn);
        bottom.add(refreshTableBtn);
        bottom.add(assignVehicleBtn);
        bottom.add(addVehicleBtn);
        bottom.add(generateRevenueReportBtn);
        bottom.add(generateShipmentReportBtn);
        bottom.add(generateDeliveryReportBtn);
        bottom.add(generateVehicleReportBtn);

        // Add vehicle button action
        addVehicleBtn.addActionListener(e -> {
            try {
                String licensePlate = JOptionPane.showInputDialog(this, "Enter license plate:");
                if (licensePlate == null || licensePlate.trim().isEmpty()) return;

                String maxWeightStr = JOptionPane.showInputDialog(this, "Enter max weight capacity (kg):");
                if (maxWeightStr == null) return;
                double maxWeight = Double.parseDouble(maxWeightStr);

                String maxPackagesStr = JOptionPane.showInputDialog(this, "Enter max package capacity:");
                if (maxPackagesStr == null) return;
                int maxPackages = Integer.parseInt(maxPackagesStr);

                // Optionally select a driver
                java.util.List<userModule.Driver> drivers = system.listDrivers();
                String driverDLN = null;
                if (!drivers.isEmpty()) {
                    String[] driverOptions = new String[drivers.size()];
                    for (int i = 0; i < drivers.size(); i++) {
                        userModule.Driver d = drivers.get(i);
                        driverOptions[i] = d.getdln() + " - " + d.getFirstName() + " " + d.getLastName();
                    }

                    String selectedDriver = (String) JOptionPane.showInputDialog(
                            this,
                            "Select Driver (optional):",
                            "Assign Driver",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            driverOptions,
                            driverOptions[0]
                    );

                    if (selectedDriver != null) {
                        // Extract the driver license number from the selection
                        driverDLN = selectedDriver.split(" - ")[0];
                    }
                }

                vehicleAndRoutingModule.Vehicle vehicle = system.addVehicle(licensePlate, maxWeight, maxPackages, driverDLN);
                if (vehicle != null) {
                    JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for weight and package capacity.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding vehicle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add driver button
        JButton addDriverBtn = new JButton("Add Driver");
        bottom.add(addDriverBtn);
        addDriverBtn.addActionListener(e -> {
            try {
                String firstName = JOptionPane.showInputDialog(this, "Enter driver's first name:");
                if (firstName == null || firstName.trim().isEmpty()) return;

                String lastName = JOptionPane.showInputDialog(this, "Enter driver's last name:");
                if (lastName == null || lastName.trim().isEmpty()) return;

                String email = JOptionPane.showInputDialog(this, "Enter driver's email:");
                if (email == null || email.trim().isEmpty()) return;

                String password = JOptionPane.showInputDialog(this, "Enter driver's password:");
                if (password == null || password.trim().isEmpty()) return;

                String driverLicense = JOptionPane.showInputDialog(this, "Enter driver's license number:");
                if (driverLicense == null || driverLicense.trim().isEmpty()) return;

                userModule.Driver driver = system.addDriver(firstName, lastName, email, password, driverLicense);
                if (driver != null) {
                    JOptionPane.showMessageDialog(this, "Driver added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add driver.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding driver: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        add(bottom, BorderLayout.SOUTH);

        // Actions
        refreshReportBtn.addActionListener(e -> refreshReport());
        refreshTableBtn.addActionListener(e -> refreshShipmentTable());

        assignVehicleBtn.addActionListener(e -> {
            int r = shipmentTable.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Select a shipment", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String trackingNumber = (String) model.getValueAt(r, 0);
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

            // Enter route time
            String routeTime = JOptionPane.showInputDialog(this, "Enter route time (e.g., 09:00-12:00):");
            if (routeTime == null || routeTime.trim().isEmpty()) return;

            // Assign shipment with validation
            boolean assigned = system.assignShipmentToVehicle(trackingNumber, licensePlate, routeTime);
            if (assigned) {
                JOptionPane.showMessageDialog(this, "Shipment assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot assign shipment.\nCheck vehicle weight/quantity limits or route conflict.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            refreshShipmentTable();
        });

        // Report generation buttons
        generateRevenueReportBtn.addActionListener(e -> {
            try {
                java.time.LocalDate startDate = java.time.LocalDate.now().minusDays(30);
                java.time.LocalDate endDate = java.time.LocalDate.now();
                String filePath = "RevenueReport_" + System.currentTimeMillis() + ".pdf";
                ReportManager.generateRevenueSummaryReport(filePath, startDate, endDate);
                JOptionPane.showMessageDialog(this, "Revenue report generated: " + filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating revenue report: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        generateShipmentReportBtn.addActionListener(e -> {
            try {
                java.time.LocalDate startDate = java.time.LocalDate.now().minusDays(30);
                java.time.LocalDate endDate = java.time.LocalDate.now();
                String filePath = "ShipmentReport_" + System.currentTimeMillis() + ".pdf";
                ReportManager.generateShipmentVolumeReport(filePath, startDate, endDate, "daily");
                JOptionPane.showMessageDialog(this, "Shipment report generated: " + filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating shipment report: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        generateDeliveryReportBtn.addActionListener(e -> {
            try {
                java.time.LocalDate startDate = java.time.LocalDate.now().minusDays(30);
                java.time.LocalDate endDate = java.time.LocalDate.now();
                String filePath = "DeliveryReport_" + System.currentTimeMillis() + ".pdf";
                ReportManager.generateDeliveryPerformanceReport(filePath, startDate, endDate);
                JOptionPane.showMessageDialog(this, "Delivery report generated: " + filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating delivery report: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        generateVehicleReportBtn.addActionListener(e -> {
            try {
                java.time.LocalDate startDate = java.time.LocalDate.now().minusDays(30);
                java.time.LocalDate endDate = java.time.LocalDate.now();
                String filePath = "VehicleReport_" + System.currentTimeMillis() + ".pdf";
                ReportManager.generateVehicleUtilizationReport(filePath, startDate, endDate);
                JOptionPane.showMessageDialog(this, "Vehicle report generated: " + filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating vehicle report: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshReport();
    }

    private void refreshReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shipments by status:\n\n");
        for (Map.Entry<String, Long> e : system.shipmentsByStatus().entrySet()) {
            sb.append(String.format("%s: %d\n", e.getKey(), e.getValue()));
        }
        sb.append("\nTotal shipments: " + system.listShipments().size());
        reportArea.setText(sb.toString());
    }

    private void refreshShipmentTable() {
        model.setRowCount(0);
        for (shipmentModule.Shipment s : system.listShipments()) {
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