package testing;

import java.time.LocalDate;
import reportingModule.ReportManager;
import billingAndPaymentModule.Invoice;
import shipmentModule.Shipment;
import vehicleAndRoutingModule.Vehicle;
import databaseModule.bapDAO.InvoiceDAO;
import databaseModule.sDAO.ShipmentDAO;
import databaseModule.varDAO.VehicleDAO;

public class ReportTest {
    public static void main(String[] args) {
        System.out.println("Testing Reporting Module...");

        // Test revenue summary report
        System.out.println("Generating Revenue Summary Report...");
        ReportManager.generateRevenueSummaryReport(
            "D:\\Dev\\Projects\\eclipse-workspace\\Managmentsystem\\RevenueSummary.pdf",
            LocalDate.now().minusDays(30),
            LocalDate.now()
        );

        // Test shipment volume report
        System.out.println("Generating Shipment Volume Report...");
        ReportManager.generateShipmentVolumeReport(
            "D:\\Dev\\Projects\\eclipse-workspace\\Managmentsystem\\ShipmentVolume.pdf",
            LocalDate.now().minusDays(30),
            LocalDate.now(),
            "daily"
        );

        // Test delivery performance report
        System.out.println("Generating Delivery Performance Report...");
        ReportManager.generateDeliveryPerformanceReport(
            "D:\\Dev\\Projects\\eclipse-workspace\\Managmentsystem\\DeliveryPerformance.pdf",
            LocalDate.now().minusDays(30),
            LocalDate.now()
        );

        // Test vehicle utilization report
        System.out.println("Generating Vehicle Utilization Report...");
        ReportManager.generateVehicleUtilizationReport(
            "D:\\Dev\\Projects\\eclipse-workspace\\Managmentsystem\\VehicleUtilization.pdf",
            LocalDate.now().minusDays(30),
            LocalDate.now()
        );

        // Test invoice export
        System.out.println("Exporting sample invoices as PDF...");
        // Get all invoices and export the first one as an example
        for (Invoice invoice : ReportManager.getAllInvoices()) {
            if (invoice != null) {
                ReportManager.exportInvoiceAsPDF(invoice,
                    "D:\\Dev\\Projects\\eclipse-workspace\\Managmentsystem\\Invoice_" +
                    invoice.getInvoiceNum() + ".pdf");
                System.out.println("Exported invoice: " + invoice.getInvoiceNum());
                break; // Just export the first one for testing
            }
        }

        System.out.println("All reports generated successfully!");
    }
}