package managmentsystem.user_customer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Manager class with comprehensive reporting capabilities.
 * Managers can generate and export various reports to PDF.
 */
public class Manager extends User {
    private ReportGenerator reportGenerator;
    private PDFReportExporter pdfExporter;

    public Manager(String id, String name, String email, String password,
                   ReportGenerator reportGenerator) {
        super(id, name, email, password);
        this.reportGenerator = reportGenerator;
        this.pdfExporter = new PDFReportExporter();
    }

    /**
     * Generates a daily shipment report.
     */
    public void generateDailyShipmentReport(LocalDateTime date) {
        LocalDateTime startOfDay = ReportGenerator.getStartOfDay(date);
        LocalDateTime endOfDay = ReportGenerator.getEndOfDay(date);

        ShipmentReport report = reportGenerator.generateShipmentReport(
            startOfDay, endOfDay, ReportGenerator.ReportPeriod.DAILY);

        displayShipmentReport(report);
    }

    /**
     * Generates a weekly shipment report.
     */
    public void generateWeeklyShipmentReport(LocalDateTime startOfWeek) {
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        ShipmentReport report = reportGenerator.generateShipmentReport(
            startOfWeek, endOfWeek, ReportGenerator.ReportPeriod.WEEKLY);

        displayShipmentReport(report);
    }

    /**
     * Generates a monthly shipment report.
     */
    public void generateMonthlyShipmentReport(LocalDateTime startOfMonth) {
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        ShipmentReport report = reportGenerator.generateShipmentReport(
            startOfMonth, endOfMonth, ReportGenerator.ReportPeriod.MONTHLY);

        displayShipmentReport(report);
    }

    /**
     * Generates a custom shipment report for a specific date range.
     */
    public void generateCustomShipmentReport(LocalDateTime startDate, LocalDateTime endDate) {
        ShipmentReport report = reportGenerator.generateShipmentReport(
            startDate, endDate, ReportGenerator.ReportPeriod.CUSTOM);

        displayShipmentReport(report);
    }

    /**
     * Displays shipment report in console.
     */
    private void displayShipmentReport(ShipmentReport report) {
        System.out.println("\n========== SHIPMENT REPORT ==========");
        System.out.println("Period: " + report.getPeriod());
        System.out.println("From: " + report.getStartDate());
        System.out.println("To: " + report.getEndDate());
        System.out.println("-------------------------------------");
        System.out.println("Total Shipments: " + report.getTotalShipments());
        System.out.println("  - Pending: " + report.getPendingCount());
        System.out.println("  - Assigned: " + report.getAssignedCount());
        System.out.println("  - In Transit: " + report.getInTransitCount());
        System.out.println("  - Delivered: " + report.getDeliveredCount());
        System.out.println("  - Cancelled: " + report.getCancelledCount());
        System.out.println("Average Weight: " + String.format("%.2f kg", report.getAverageWeight()));
        
        if (report.getPackageTypeCounts() != null && !report.getPackageTypeCounts().isEmpty()) {
            System.out.println("\nPackage Types:");
            report.getPackageTypeCounts().forEach((type, count) -> 
                System.out.println("  - " + type + ": " + count));
        }
        
    }

    /**
     * Generates and exports shipment report to PDF.
     */
    public void exportShipmentReportToPDF(LocalDateTime startDate, LocalDateTime endDate, 
                                         ReportGenerator.ReportPeriod period, String filename) {
        ShipmentReport report = reportGenerator.generateShipmentReport(startDate, endDate, period);
        pdfExporter.exportShipmentReport(report, filename);
        System.out.println(name + " exported shipment report to: " + filename);
    }

    /**
     * Generates a delivery performance report.
     */
    public void generateDeliveryPerformanceReport(LocalDateTime startDate, LocalDateTime endDate) {
        DeliveryPerformanceReport report = reportGenerator.generateDeliveryPerformanceReport(
            startDate, endDate);

        System.out.println("\n========== DELIVERY PERFORMANCE REPORT ==========");
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("------------------------------------------------");
        System.out.println("Total Deliveries: " + report.getTotalDeliveries());
        System.out.println("On-Time Deliveries: " + report.getOnTimeDeliveries());
        System.out.println("Delayed Deliveries: " + report.getDelayedDeliveries());
        System.out.println("On-Time Percentage: " + String.format("%.2f%%", report.getOnTimePercentage()));
        System.out.println("Average Delivery Time: " + String.format("%.2f hours", report.getAverageDeliveryTime()));
        
    }

    /**
     * Exports delivery performance report to PDF.
     */
    public void exportDeliveryPerformanceReportToPDF(LocalDateTime startDate, LocalDateTime endDate, 
                                                    String filename) {
        DeliveryPerformanceReport report = reportGenerator.generateDeliveryPerformanceReport(
            startDate, endDate);
        pdfExporter.exportDeliveryPerformanceReport(report, filename);
        System.out.println(name + " exported delivery performance report to: " + filename);
    }

    /**
     * Generates a revenue report.
     */
    public void generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        RevenueReport report = reportGenerator.generateRevenueReport(startDate, endDate);

        System.out.println("\n========== REVENUE REPORT ==========");
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("------------------------------------");
        System.out.println("Total Invoices: " + report.getTotalInvoices());
        System.out.println("Total Revenue: $" + String.format("%.2f", report.getTotalRevenue()));
        System.out.println("Paid Revenue: $" + String.format("%.2f", report.getPaidRevenue()));
        System.out.println("Unpaid Revenue: $" + String.format("%.2f", report.getUnpaidRevenue()));
        System.out.println("Paid Invoices: " + report.getPaidInvoices());
        System.out.println("Unpaid Invoices: " + report.getUnpaidInvoices());
        System.out.println("Average Invoice: $" + String.format("%.2f", report.getAverageInvoiceAmount()));
        
    }

    /**
     * Exports revenue report to PDF.
     */
    public void exportRevenueReportToPDF(LocalDateTime startDate, LocalDateTime endDate, String filename) {
        RevenueReport report = reportGenerator.generateRevenueReport(startDate, endDate);
        pdfExporter.exportRevenueReport(report, filename);
        System.out.println(name + " exported revenue report to: " + filename);
    }

    /**
     * Generates a vehicle utilization report.
     */
    public void generateVehicleUtilizationReport() {
        VehicleUtilizationReport report = reportGenerator.generateVehicleUtilizationReport();

        System.out.println("\n========== VEHICLE UTILIZATION REPORT ==========");
        System.out.println("Report Date: " + report.getReportDate());
        System.out.println("-----------------------------------------------");
        System.out.println("Total Vehicles: " + report.getTotalVehicles());
        System.out.println("Active Vehicles: " + report.getActiveVehicles());
        System.out.println("Average Utilization: " + String.format("%.2f%%", report.getAverageUtilization()));
        System.out.println("\nVehicle Details:");
        
        for (VehicleUtilization util : report.getVehicleUtilizations()) {
            System.out.printf("  %s (%s): %.2f/%.2f kg, %d/%d packages, %.1f%% utilized [%s]%n",
                util.getVehicleId(),
                util.getVehicleType(),
                util.getCurrentWeight(),
                util.getMaxWeight(),
                util.getCurrentPackages(),
                util.getMaxPackages(),
                util.getUtilizationPercentage(),
                util.isAvailable() ? "Available" : "Full");
        }
        
    }

    /**
     * Exports vehicle utilization report to PDF.
     */
    public void exportVehicleUtilizationReportToPDF(String filename) {
        VehicleUtilizationReport report = reportGenerator.generateVehicleUtilizationReport();
        pdfExporter.exportVehicleUtilizationReport(report, filename);
        System.out.println(name + " exported vehicle utilization report to: " + filename);
    }

    /**
     * Generates a comprehensive dashboard report showing all key metrics.
     */
    public void generateDashboardReport() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);

        System.out.println("\n========== MANAGEMENT DASHBOARD ==========");
        System.out.println("Generated by: " + name);
        System.out.println("Date: " + now);
        

        // Shipment summary
        ShipmentReport shipmentReport = reportGenerator.generateShipmentReport(
            startOfMonth, now, ReportGenerator.ReportPeriod.MONTHLY);
        System.out.println("SHIPMENTS THIS MONTH: " + shipmentReport.getTotalShipments());

        // Delivery performance
        DeliveryPerformanceReport perfReport = reportGenerator.generateDeliveryPerformanceReport(
            startOfMonth, now);
        System.out.println("ON-TIME DELIVERY RATE: " + 
            String.format("%.2f%%", perfReport.getOnTimePercentage()));

        // Revenue
        RevenueReport revenueReport = reportGenerator.generateRevenueReport(startOfMonth, now);
        System.out.println("REVENUE THIS MONTH: $" + 
            String.format("%.2f", revenueReport.getTotalRevenue()));

        // Vehicle utilization
        VehicleUtilizationReport vehicleReport = reportGenerator.generateVehicleUtilizationReport();
        System.out.println("AVERAGE VEHICLE UTILIZATION: " + 
            String.format("%.2f%%", vehicleReport.getAverageUtilization()));

           }

    // method for compatibility.
     
    public void manageUserAccounts() {
        System.out.println(name + " is managing all user accounts.");
    }

    public void generateReports() {
        System.out.println(name + " is generating shipment, revenue, and performance reports.");
        generateDashboardReport();
    }
}

