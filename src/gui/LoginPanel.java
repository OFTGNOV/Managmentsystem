package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private CoreSystem system;
    private JTabbedPane tabbedPane;
    
    public LoginPanel(CoreSystem system, JTabbedPane tabbedPane) {
        this.system = system;
        this.tabbedPane = tabbedPane; //this .tabbedPane will be used to switch tabs after login
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblEmail = new JLabel("Email:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblCategory = new JLabel("Category:");

        JTextField txtEmail = new JTextField(15);
        JPasswordField txtPassword = new JPasswordField(15);

        String[] categories = {"Customer", "Clerk", "Manager", "Driver"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);

        JButton btnLogin = new JButton("Login");
        JButton btnGoToSignup = new JButton("Sign Up");

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; add(lblEmail, gbc);
        gbc.gridx = 1; add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(lblPassword, gbc);
        gbc.gridx = 1; add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(lblCategory, gbc);
        gbc.gridx = 1; add(categoryCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(btnLogin, gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(btnGoToSignup, gbc);

        // Login action
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText().trim();
                String pwd = new String(txtPassword.getPassword());
                String category = (String) categoryCombo.getSelectedItem();

                boolean authenticated = system.authenticate(email, pwd, category);
                if(authenticated) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Replace the main panel with the tabbed pane after successful login
                    Container parent = getParent();
                    parent.removeAll();
                    parent.setLayout(new BorderLayout());

                    // Enable only the selected category tab and switch
                    for(int i=0; i<tabbedPane.getTabCount(); i++) {
                        tabbedPane.setEnabledAt(i, false);
                    }

                    switch(category) {
                        case "Customer":
                            tabbedPane.setEnabledAt(0, true);
                            tabbedPane.setSelectedIndex(0);
                            break;
                        case "Clerk":
                            tabbedPane.setEnabledAt(1, true);
                            tabbedPane.setSelectedIndex(1);
                            break;
                        case "Manager":
                            tabbedPane.setEnabledAt(2, true);
                            tabbedPane.setSelectedIndex(2);
                            break;
                        case "Driver":
                            tabbedPane.setEnabledAt(3, true);
                            tabbedPane.setSelectedIndex(3);
                            break;
                    }

                    parent.add(tabbedPane, BorderLayout.CENTER);
                    parent.revalidate();
                    parent.repaint();
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Go to signup action
        btnGoToSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) getParent().getLayout()).show(getParent(), "signup");
            }
        });
    }
}