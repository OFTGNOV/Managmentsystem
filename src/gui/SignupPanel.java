package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupPanel extends JPanel {
    private CoreSystem system;
    
    public SignupPanel(CoreSystem system) {
        this.system = system;
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCategory = new JLabel("Account Type:");
        String[] categories = {"Customer", "Clerk", "Manager", "Driver"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        
        // Common fields
        JLabel lblFirstName = new JLabel("First Name:");
        JLabel lblLastName = new JLabel("Last Name:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblPassword = new JLabel("Password:");
        
        JTextField txtFirstName = new JTextField(15);
        JTextField txtLastName = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JPasswordField txtPassword = new JPasswordField(15);
        
        // Specific fields for each user type
        JLabel lblAddress = new JLabel("Address:");  // For Customer
        JTextField txtAddress = new JTextField(15);

        JLabel lblZone = new JLabel("Zone:");  // For Customer
        String[] zones = {"1", "2", "3", "4"};
        JComboBox<String> zoneCombo = new JComboBox<>(zones);

        JLabel lblDLN = new JLabel("Driver License Number:");  // For Driver
        JTextField txtDLN = new JTextField(15);

        // Special panel for type-specific fields
        JPanel typeSpecificPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(10,10,10,10);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        // Initially show customer fields (address and zone)
        gbc2.gridx = 0; gbc2.gridy = 0; typeSpecificPanel.add(lblAddress, gbc2);
        gbc2.gridx = 1; typeSpecificPanel.add(txtAddress, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 1; typeSpecificPanel.add(lblZone, gbc2);
        gbc2.gridx = 1; typeSpecificPanel.add(zoneCombo, gbc2);
        txtDLN.setVisible(false);
        lblDLN.setVisible(false);

        JButton btnSignUp = new JButton("Sign Up");
        JButton btnGoToLogin = new JButton("Back to Login");

        // Layout common components
        gbc.gridx = 0; gbc.gridy = 0; add(lblCategory, gbc);
        gbc.gridx = 1; add(categoryCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(lblFirstName, gbc);
        gbc.gridx = 1; add(txtFirstName, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(lblLastName, gbc);
        gbc.gridx = 1; add(txtLastName, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(lblEmail, gbc);
        gbc.gridx = 1; add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 4; add(lblPassword, gbc);
        gbc.gridx = 1; add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 5; add(typeSpecificPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 6; add(btnSignUp, gbc);
        gbc.gridx = 1; gbc.gridy = 6; add(btnGoToLogin, gbc);

        // Category selection change listener
        categoryCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryCombo.getSelectedItem();

                // Clear type-specific panel
                typeSpecificPanel.removeAll();

                // Add appropriate fields based on category
                GridBagConstraints gbc2Local = new GridBagConstraints();
                gbc2Local.insets = new Insets(10,10,10,10);
                gbc2Local.fill = GridBagConstraints.HORIZONTAL;

                switch(selectedCategory) {
                    case "Customer":
                        gbc2Local.gridx = 0; gbc2Local.gridy = 0; typeSpecificPanel.add(lblAddress, gbc2Local);
                        gbc2Local.gridx = 1; typeSpecificPanel.add(txtAddress, gbc2Local);
                        gbc2Local.gridx = 0; gbc2Local.gridy = 1; typeSpecificPanel.add(lblZone, gbc2Local);
                        gbc2Local.gridx = 1; typeSpecificPanel.add(zoneCombo, gbc2Local);
                        break;
                    case "Driver":
                        gbc2Local.gridx = 0; gbc2Local.gridy = 0; typeSpecificPanel.add(lblDLN, gbc2Local);
                        gbc2Local.gridx = 1; typeSpecificPanel.add(txtDLN, gbc2Local);
                        break;
                    case "Clerk":
                    case "Manager":
                        // No additional fields for clerks and managers
                        break;
                }

                typeSpecificPanel.revalidate();
                typeSpecificPanel.repaint();
            }
        });

        // Sign-Up action
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fname = txtFirstName.getText().trim();
                String lname = txtLastName.getText().trim();
                String email = txtEmail.getText().trim();
                String pwd = new String(txtPassword.getPassword());
                String category = (String) categoryCombo.getSelectedItem();

                if(fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
                    JOptionPane.showMessageDialog(SignupPanel.this, "All common fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean created = false;
                switch(category) {
                    case "Customer":
                        String address = txtAddress.getText().trim();
                        if(address.isEmpty()) {
                            JOptionPane.showMessageDialog(SignupPanel.this, "Address is required for customers", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        int zone = Integer.parseInt((String) zoneCombo.getSelectedItem());
                        created = system.signUpCustomer(fname, lname, email, pwd, address, zone);
                        break;
                    case "Driver":
                        String dln = txtDLN.getText().trim();
                        if(dln.isEmpty()) {
                            JOptionPane.showMessageDialog(SignupPanel.this, "Driver License Number is required for drivers", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        created = system.signUpDriver(fname, lname, email, pwd, dln);
                        break;
                    case "Clerk":
                    case "Manager":
                        // Use generic signup for clerk and manager
                        created = system.signUpUser(email, fname, lname, pwd, category);
                        break;
                }

                if(created) {
                    JOptionPane.showMessageDialog(SignupPanel.this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Switch back to login panel
                    ((CardLayout) getParent().getLayout()).show(getParent(), "login");
                    // Clear fields
                    txtFirstName.setText("");
                    txtLastName.setText("");
                    txtEmail.setText("");
                    txtPassword.setText("");
                    txtAddress.setText("");
                    txtDLN.setText("");
                } else {
                    JOptionPane.showMessageDialog(SignupPanel.this, "User registration failed! Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Go to login action
        btnGoToLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) getParent().getLayout()).show(getParent(), "login");
                // Clear fields
                txtFirstName.setText("");
                txtLastName.setText("");
                txtEmail.setText("");
                txtPassword.setText("");
                txtAddress.setText("");
                txtDLN.setText("");
                zoneCombo.setSelectedIndex(0); // Reset to first zone
            }
        });
    }
}