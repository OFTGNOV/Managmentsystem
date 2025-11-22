package driver;

import gui.SmartShipGUI;

/**
 * Main driver class for the SmartShip Package Management System
 * This class serves as the entry point for the application
 */
public class Driver {
    public static void main(String[] args) {
        // Initialize and launch the SmartShip GUI
        java.awt.EventQueue.invokeLater(() -> {
            try {
                // Set system look and feel
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                // If system look and feel is not available, continue with default
                e.printStackTrace();
            }
            
            // Create and show the main GUI window
            new SmartShipGUI(new gui.CoreSystem()).setVisible(true);
        });
    }
}