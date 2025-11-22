package reportingModule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import billingAndPaymentModule.Invoice;
import shipmentModule.Shipment;
import vehicleAndRoutingModule.Vehicle;
import databaseModule.bapDAO.InvoiceDAO;
import databaseModule.sDAO.ShipmentDAO;
import databaseModule.varDAO.VehicleDAO;

//ReportManager class provides methods for generating various business reports
public class ReportManager {
    
    /**
     * Export a revenue summary report as PDF
     * @param filePath The path to save the PDF file
     * @param startDate The start date for the report
     * @param endDate The end date for the report
     */
    public static void generateRevenueSummaryReport(String filePath, LocalDate startDate, LocalDate endDate) {
        Pdfreportexporter.exportRevenueSummaryReport(filePath, startDate, endDate);
    }
    
    /**
     * Export a shipment volume report as PDF
     * @param filePath The path to save the PDF file
     * @param startDate The start date for the report
     * @param endDate The end date for the report
     * @param period The period type (daily, weekly, monthly)
     */
    public static void generateShipmentVolumeReport(String filePath, LocalDate startDate, LocalDate endDate, String period) {
        Pdfreportexporter.exportShipmentVolumeReport(filePath, startDate, endDate, period);
    }
    
    /**
     * Export a delivery performance report as PDF
     * @param filePath The path to save the PDF file
     * @param startDate The start date for the report
     * @param endDate The end date for the report
     */
    public static void generateDeliveryPerformanceReport(String filePath, LocalDate startDate, LocalDate endDate) {
        Pdfreportexporter.exportDeliveryPerformanceReport(filePath, startDate, endDate);
    }
    
    /**
     * Export a vehicle utilization report as PDF
     * @param filePath The path to save the PDF file
     * @param startDate The start date for the report
     * @param endDate The end date for the report
     */
    public static void generateVehicleUtilizationReport(String filePath, LocalDate startDate, LocalDate endDate) {
        Pdfreportexporter.exportVehicleUtilizationReport(filePath, startDate, endDate);
    }
    
    /**
     * Export a single invoice as PDF
     * @param invoice The invoice to export
     * @param filePath The path to save the PDF file
     */
    public static void exportInvoiceAsPDF(Invoice invoice, String filePath) {
        Pdfreportexporter.exportInvoiceAsPDF(invoice, filePath);
    }
    
    /**
     * Get a list of all invoices
     * @return List of invoices
     */
    public static List<Invoice> getAllInvoices() {
        return InvoiceDAO.readAllInvoices();
    }
    
    /**
     * Get a list of all shipments
     * @return List of shipments
     */
    public static List<Shipment> getAllShipments() {
        return ShipmentDAO.readAllShipments();
    }
    
    /**
     * Get a list of all vehicles
     * @return List of vehicles
     */
    public static List<Vehicle> getAllVehicles() {
        return VehicleDAO.readAllVehicles();
    }
    
    /**
     * Get invoices by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of invoices within the date range
     */
    public static List<Invoice> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        // This would typically query the database for invoices within the date range
        // For now, we'll filter the full list
        List<Invoice> allInvoices = InvoiceDAO.readAllInvoices();
        List<Invoice> filteredInvoices = new java.util.ArrayList<>();
        
        for (Invoice invoice : allInvoices) {
            if (invoice.getIssueDate() != null) {
                LocalDate issueDate = invoice.getIssueDate().toLocalDate();
                if (!issueDate.isBefore(startDate) && !issueDate.isAfter(endDate)) {
                    filteredInvoices.add(invoice);
                }
            }
        }
        
        return filteredInvoices;
    }
    
    /**
     * Get shipments by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of shipments within the date range
     */
    public static List<Shipment> getShipmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Shipment> allShipments = ShipmentDAO.readAllShipments();
        List<Shipment> filteredShipments = new java.util.ArrayList<>();
        
        for (Shipment shipment : allShipments) {
            if (shipment.getCreatedDate() != null) {
                LocalDate creationDate = shipment.getCreatedDate().toLocalDate();
                if (!creationDate.isBefore(startDate) && !creationDate.isAfter(endDate)) {
                    filteredShipments.add(shipment);
                }
            }
        }
        
        return filteredShipments;
    }
    
    /**
     * Get a summary of revenue by zone for a given date range
     * @param startDate Start date
     * @param endDate End date
     * @return Map of zone to revenue
     */
    public static Map<Integer, Double> getRevenueByZone(LocalDate startDate, LocalDate endDate) {
        return Pdfreportexporter.getRevenueByZone(startDate, endDate);
    }
    
    /**
     * Get shipment count by date range
     * @param startDate Start date
     * @param endDate End date
     * @return Number of shipments within the date range
     */
    public static int getShipmentCountByDateRange(LocalDate startDate, LocalDate endDate) {
        return Pdfreportexporter.getShipmentCountBetweenDates(startDate, endDate);
    }
    
    /**
     * Get total revenue within a date range
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue in the period
     */
    public static double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        return Pdfreportexporter.calculateRevenueBetweenDates(startDate, endDate);
    }
}