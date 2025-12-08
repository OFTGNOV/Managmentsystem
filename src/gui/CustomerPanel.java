package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import shipmentModule.*;
import userModule.*;
import billingAndPaymentModule.*;
import databaseModule.bapDAO.*;

public class CustomerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final CoreSystem system;

    // Create Shipment Fields
    private final JTextField recipientEmailField = new JTextField(20);
    private final JTextField weightField = new JTextField(10);
    private final JTextField lengthField = new JTextField(5);
    private final JTextField widthField = new JTextField(5);
    private final JTextField heightField = new JTextField(5);

    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"STANDARD", "EXPRESS", "FRAGILE"});

    // Table for displaying shipments
    private JTable shipmentTable;
    private DefaultTableModel shipmentTableModel;

    // Track Shipment Fields
    private final JTextField trackingSearchField = new JTextField(15);
    private final JTextArea trackingResultArea = new JTextArea(8, 30);

    private SmartShipGUI guiParent;

    public CustomerPanel(CoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));

        // Create signout button
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.addActionListener(e -> signOut());

        // Create top panel with signout button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(signOutButton);

        // Tabs for Create + Track
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Create Shipment", buildCreatePanel());
        tabs.add("Track Shipment", buildTrackPanel());

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    public void setGuiParent(SmartShipGUI gui) {
        this.guiParent = gui;
    }

    private void signOut() {
        // Call signout to clear the user from the system
        system.signOut();

        // Call the SmartShipGUI's showLoginPanel method
        if (guiParent != null) {
            guiParent.showLoginPanel();
        }
    }

    // ---------------- CREATE SHIPMENT TAB ------------------
    private JPanel buildCreatePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Creation form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Recipient Email:"), gbc);
        gbc.gridx = 1; form.add(recipientEmailField, gbc);

        y++; gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1; form.add(weightField, gbc);

        y++; gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Dimensions (L x W x H):"), gbc);
        gbc.gridx = 1;
        JPanel dimPanel = new JPanel();
        dimPanel.add(lengthField); dimPanel.add(new JLabel("x"));
        dimPanel.add(widthField); dimPanel.add(new JLabel("x"));
        dimPanel.add(heightField);
        form.add(dimPanel, gbc);

        y++; gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Package Type:"), gbc);
        gbc.gridx = 1; form.add(typeBox, gbc);

        JButton createBtn = new JButton("Create Shipment");
        createBtn.addActionListener(this::createShipment);

        // Add form to top section
        JPanel creationPanel = new JPanel(new BorderLayout());
        creationPanel.add(form, BorderLayout.CENTER);
        creationPanel.add(createBtn, BorderLayout.SOUTH);

        // Create shipment table
        String[] columnNames = {"Tracking Number", "Recipient", "Destination", "Status", "Cost", "Created Date"};
        shipmentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        shipmentTable = new JTable(shipmentTableModel);
        JScrollPane tableScrollPane = new JScrollPane(shipmentTable);

        // Add export invoice button
        JPanel buttonPanel = new JPanel();
        JButton exportInvoiceBtn = new JButton("Export Invoice for Selected");
        exportInvoiceBtn.addActionListener(e -> exportInvoiceForSelected());
        buttonPanel.add(exportInvoiceBtn);

        // Refresh button to reload shipments
        JButton refreshBtn = new JButton("Refresh Shipments");
        refreshBtn.addActionListener(e -> loadCustomerShipments());
        buttonPanel.add(refreshBtn);

        // Add the creation form and buttons to top
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(creationPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH); // Add form and buttons at the top
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Load initial shipments
        loadCustomerShipments();

        return panel;
    }

    // ---------------- SHIPMENT CREATION + PAYMENT ------------------
    @SuppressWarnings("unused")
	private void createShipment(ActionEvent e) {
        try {
            String recipientEmail = recipientEmailField.getText().trim();
            String typeStr = (String) typeBox.getSelectedItem();
            PackageType type = PackageType.valueOf(typeStr); // Convert to proper enum

            if (recipientEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Recipient email must be provided!", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double weight = Double.parseDouble(weightField.getText().trim());
            double length = Double.parseDouble(lengthField.getText().trim());
            double width = Double.parseDouble(widthField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());

            // Validate input values to prevent database errors
            if (weight <= 0 || weight > 999.99) {
                JOptionPane.showMessageDialog(this, "Weight must be between 0.01 and 999.99 kg", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (length <= 0 || length > 999.99) {
                JOptionPane.showMessageDialog(this, "Length must be between 0.01 and 999.99 cm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (width <= 0 || width > 999.99) {
                JOptionPane.showMessageDialog(this, "Width must be between 0.01 and 999.99 cm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (height <= 0 || height > 999.99) {
                JOptionPane.showMessageDialog(this, "Height must be between 0.01 and 999.99 cm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get current logged-in user as sender (should be of type CUSTOMER)
            User sender = system.getCurrentUser();
            if (sender == null || sender.getUserType() != UserType.CUSTOMER) {
                JOptionPane.showMessageDialog(this, "Please log in as a customer to create shipments.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Look up recipient in database by email
            User recipient = databaseModule.uDAO.UserDAO.retrieveUserRecordByEmail(recipientEmail);
            if (recipient == null || recipient.getUserType() != UserType.CUSTOMER) {
                JOptionPane.showMessageDialog(this, "Recipient with email " + recipientEmail + " not found in the system or not a customer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create shipment with all proper parameters using the shipment constructor
            shipmentModule.Shipment shipment = new shipmentModule.Shipment(sender, recipient, weight, length, width, height, type);

            // Calculate shipping cost and set it
            double totalCost = shipment.calculateShippingCost();
            shipment.setShippingCost(totalCost);

            // Save the shipment to the database
            databaseModule.sDAO.ShipmentDAO.insertShipmentRecord(shipment);

            // Refresh the shipment from the database to ensure all data is properly saved
            shipmentModule.Shipment savedShipment = system.getShipment(shipment.getTrackingNumber());

            if (savedShipment == null) {
                JOptionPane.showMessageDialog(this, "Failed to save shipment to database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create invoice for the shipment through the system using the saved shipment
            Invoice invoice = new Invoice(savedShipment);
            databaseModule.bapDAO.InvoiceDAO.insertInvoiceRecord(invoice);

            if (invoice == null) {
                JOptionPane.showMessageDialog(this, "Failed to create invoice", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ask if paying now or later
            int payNow = JOptionPane.showConfirmDialog(this,
                    "Total cost: $" + String.format("%.2f", totalCost) + "\nDo you want to pay now?",
                    "Payment", JOptionPane.YES_NO_OPTION);

            if (payNow == JOptionPane.YES_OPTION) {
                // Ask cash or card
                String[] options = {"Cash", "Card"};
                String method = (String) JOptionPane.showInputDialog(this,
                        "Select Payment Method:", "Payment Method",
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (method != null) {
                    PaymentMethod paymentMethod = method.equals("Cash") ?
                        billingAndPaymentModule.PaymentMethod.CASH :
                        billingAndPaymentModule.PaymentMethod.CARD;

                    if (method.equals("Cash")) {
                        String cashStr = JOptionPane.showInputDialog(this,
                                "Enter cash amount:");
                        double cashAmount = Double.parseDouble(cashStr);

                        // Create cash payment
                        Payment payment = new Payment(0, // paymentId to be set by DAO
                            cashAmount,
                            java.time.LocalDateTime.now(),
                            paymentMethod,
                            billingAndPaymentModule.PaymentStatus.PENDING,
                            null, // referenceNumber will be generated
                            invoice.getInvoiceID()); // Use the invoice number

                        // Process cash payment
                        payment.processCashPayment();

                        if (cashAmount >= totalCost) {
                            payment.completeCashPayment(cashAmount);
                            JOptionPane.showMessageDialog(this,
                                    "Payment successful! Change: $" + String.format("%.2f", (cashAmount - totalCost)));
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Partial payment recorded. Remaining balance: $" + String.format("%.2f", (totalCost - cashAmount)));
                        }

                        // Add payment to invoice and save
                        invoice.addPayment(payment);
                        InvoiceDAO.updateInvoiceRecord(invoice);
                        PaymentDAO.insertPaymentRecord(payment);
                    } else { // Card
                        String cardNum = JOptionPane.showInputDialog(this, "Enter 16-digit card number:");
                        if (cardNum.length() != 16) throw new Exception("Card number must be 16 digits");

                        String cvv = JOptionPane.showInputDialog(this, "Enter 3-digit CVV:");
                        if (cvv.length() != 3) throw new Exception("CVV must be 3 digits");

                        String expDate = JOptionPane.showInputDialog(this, "Enter expiration date (MM/YYYY):");
                        if (!expDate.matches("(0[1-9]|1[0-2])/\\d{4}"))
                            throw new Exception("Expiration date must be in MM/YYYY format");

                        // Create card payment
                        Payment payment = new Payment(0, // paymentId to be set by DAO
                            totalCost,
                            java.time.LocalDateTime.now(),
                            paymentMethod,
                            billingAndPaymentModule.PaymentStatus.SUCCESS,
                            null, // referenceNumber will be generated
                            invoice.getInvoiceID()); // Use the invoice number

                        payment.processPayment(totalCost, billingAndPaymentModule.PaymentMethod.CARD);

                        // Add payment to invoice and save
                        invoice.addPayment(payment);
                        InvoiceDAO.updateInvoiceRecord(invoice);
                        PaymentDAO.insertPaymentRecord(payment);

                        JOptionPane.showMessageDialog(this, "Card payment successful!");
                    }
                }
            } else {
                // Even if not paying now, we still need to save the invoice
                InvoiceDAO.updateInvoiceRecord(invoice);
            }

            // Add the new shipment to the table
            shipmentTableModel.addRow(new Object[]{
                    shipment.getTrackingNumber(),
                    shipment.getRecipent().getFirstName() + " " + shipment.getRecipent().getLastName(),
                    shipment.getRecipent().getAddress(),
                    shipment.getStatus(),
                    "$" + String.format("%.2f", totalCost),
                    shipment.getCreatedDate() != null ? shipment.getCreatedDate() : "N/A"
            });

            JOptionPane.showMessageDialog(this, "Shipment Created!\nTracking Number: " + shipment.getTrackingNumber());

            // Clear fields
            recipientEmailField.setText("");
            weightField.setText("");
            lengthField.setText("");
            widthField.setText("");
            heightField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Weight and dimensions must be numbers!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- TRACK SHIPMENT TAB ------------------
    private JPanel buildTrackPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        top.add(new JLabel("Enter Tracking Number:"));
        top.add(trackingSearchField);

        JButton searchBtn = new JButton("Track");
        top.add(searchBtn);
        searchBtn.addActionListener(this::trackShipment);

        trackingResultArea.setEditable(false);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(trackingResultArea), BorderLayout.CENTER);

        return panel;
    }

    private void trackShipment(ActionEvent e) {
        String trackingNumber = trackingSearchField.getText().trim();

        if (trackingNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a tracking number!");
            return;
        }

        shipmentModule.Shipment s = system.getShipment(trackingNumber);
        if (s == null) {
            trackingResultArea.setText("⚠ No shipment found with ID: " + trackingNumber);
            return;
        }

        trackingResultArea.setText(
                "📦 Shipment Details\n" +
                "-----------------------------------\n" +
                "Tracking Number: " + s.getTrackingNumber() + "\n" +
                "Sender: " + s.getSender().getFirstName() + " " + s.getSender().getLastName() + "\n" +
                "Recipient: " + s.getRecipent().getFirstName() + " " + s.getRecipent().getLastName() + "\n" +
                "Pickup Address: " + s.getSender().getAddress() + "\n" +
                "Destination: " + s.getRecipent().getAddress() + "\n" +
                "Status: " + s.getStatus() + "\n" +
                "Weight: " + s.getWeight() + "kg\n" +
                "Package Type: " + s.getpType() + "\n" +
                "Cost: $" + String.format("%.2f", s.getShippingCost())
        );
    }

    // Load all shipments for the current customer
    private void loadCustomerShipments() {
        // Clear current table
        shipmentTableModel.setRowCount(0);

        // Get all shipments and filter for current customer
        User currentUser = system.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        java.util.List<shipmentModule.Shipment> allShipments = system.listShipments();
        for (shipmentModule.Shipment s : allShipments) {
            // Only show shipments where the current user is the sender
            if (s.getSender().getID() == currentUser.getID()) {
                shipmentTableModel.addRow(new Object[]{
                        s.getTrackingNumber(),
                        s.getRecipent().getFirstName() + " " + s.getRecipent().getLastName(),
                        s.getRecipent().getAddress(),
                        s.getStatus(),
                        "$" + String.format("%.2f", s.getShippingCost()),
                        s.getCreatedDate() != null ? s.getCreatedDate() : "N/A"
                });
            }
        }
    }

    // Export invoice for selected shipment
    private void exportInvoiceForSelected() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to export invoice", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the tracking number from the selected row
        String trackingNumber = (String) shipmentTableModel.getValueAt(selectedRow, 0);

        // Get the shipment from the system using tracking number
        shipmentModule.Shipment shipment = system.getShipment(trackingNumber);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this, "Shipment not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find the invoice for this shipment
        java.util.List<billingAndPaymentModule.Invoice> invoices =
            databaseModule.bapDAO.InvoiceDAO.retrieveInvoicesByShipment(trackingNumber);

        if (invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No invoice found for this shipment", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use the first invoice (there should typically be one invoice per shipment)
        billingAndPaymentModule.Invoice invoice = invoices.get(0);

        // Generate file name based on invoice and shipment
        String fileName = "Invoice_" + invoice.getInvoiceID() + "_" + trackingNumber + ".pdf";

        // Try to save the invoice using the reporting module
        try {
            reportingModule.ReportManager.exportInvoiceAsPDF(invoice, fileName);
            JOptionPane.showMessageDialog(this, "Invoice exported successfully to: " + fileName, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting invoice: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public CoreSystem getSystem() { return system; }
}