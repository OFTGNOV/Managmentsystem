package gui;

import javax.swing.*;
import java.awt.*;

public class SmartShipGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private CoreSystem system;
    private CustomerPanel customerPanel;
    private ClerkPanel clerkPanel;
    private ManagerPanel managerPanel;
    private DriverPanel driverPanel;
    private LoginPanel loginPanel;
    private SignupPanel signupPanel;

    private JTabbedPane tabbedPane;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public SmartShipGUI(CoreSystem system) {
        this.system = system;

        setTitle("SmartShip Ltd");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with CardLayout for switching between login/signup
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        tabbedPane = new JTabbedPane();

        // Initialize panels
        customerPanel = new CustomerPanel(system);
        clerkPanel = new ClerkPanel(system);
        managerPanel = new ManagerPanel(system);
        driverPanel = new DriverPanel(system);

        // Create login and signup panels
        loginPanel = new LoginPanel(system, tabbedPane);
        signupPanel = new SignupPanel(system);

        // Add them to main panel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(signupPanel, "signup");

        // Create the tabbed pane for user panels
        tabbedPane.addTab("Customer", customerPanel);
        tabbedPane.addTab("Clerk", clerkPanel);
        tabbedPane.addTab("Manager", managerPanel);
        tabbedPane.addTab("Driver", driverPanel);

        // Initially disable all tabs
        for(int i=0; i<tabbedPane.getTabCount(); i++) {
            tabbedPane.setEnabledAt(i, false);
        }

        // Show login panel initially
        add(mainPanel, BorderLayout.CENTER);

        // Initially disable all tabs except Login/Signup
        for(int i=0; i<tabbedPane.getTabCount(); i++) {
            tabbedPane.setEnabledAt(i, false);
        }
    }

    public void showLoginPanel() {
        // Clear current user from system
        system.setCurrentUser(null, null);

        // Disable all tabs
        for(int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setEnabledAt(i, false);
        }

        // Switch to login panel
        cardLayout.show(mainPanel, "login");
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CoreSystem system = new CoreSystem();
            new SmartShipGUI(system).setVisible(true);
        });
    }
}
