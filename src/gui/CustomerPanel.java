package gui;

import javax.swing.*;
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

    private final DefaultListModel<String> createdModel = new DefaultListModel<>();

    // Track Shipment Fields
    private final JTextField trackingSearchField = new JTextField(15);
    private final JTextArea trackingResultArea = new JTextArea(8, 30);

    public CustomerPanel(CoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));

        // Tabs for Create + Track
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Create Shipment", buildCreatePanel());
        tabs.add("Track Shipment", buildTrackPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // ---------------- CREATE SHIPMENT TAB ------------------
    private JPanel buildCreatePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

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

        panel.add(form, BorderLayout.NORTH);
        panel.add(createBtn, BorderLayout.CENTER);

        JList<String> list = new JList<>(createdModel);
        panel.add(new JScrollPane(list), BorderLayout.SOUTH);

        return panel;
    }

    // ---------------- SHIPMENT CREATION + PAYMENT ------------------
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

            // Get current logged-in customer as sender
            Customer sender = (Customer) system.getCurrentUser();
            if (sender == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a customer to create shipments.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Look up recipient in database by email
            Customer recipient = databaseModule.uDAO.CustomerDAO.retrieveCustomerByEmail(recipientEmail);
            if (recipient == null) {
                JOptionPane.showMessageDialog(this, "Recipient with email " + recipientEmail + " not found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
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

            if (invoice == null || invoice.getInvoiceNum() == null) {
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
                            invoice.getInvoiceNum()); // Use the invoice number

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
                            invoice.getInvoiceNum()); // Use the invoice number

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

            createdModel.addElement("Created: " + shipment.getTrackingNumber() + " | Cost: $" + String.format("%.2f", totalCost));
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

    public CoreSystem getSystem() { return system; }
}