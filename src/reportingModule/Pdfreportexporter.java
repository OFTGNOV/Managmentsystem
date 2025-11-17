package reportingModule;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * PDFReportExporter exports reports to PDF format using iText library.
 * Provides formatted, professional PDF reports for managers.
 */
public class PDFReportExporter {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Exports a shipment report to PDF.
     */
    public void exportShipmentReport(ShipmentReport report, String filename) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add title
            Paragraph title = new Paragraph("SHIPMENT REPORT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add report period
            Paragraph period = new Paragraph("Period: " + report.getPeriod().toString(), HEADER_FONT);
            period.setSpacingAfter(5);
            document.add(period);

            Paragraph dates = new Paragraph(
                "From: " + report.getStartDate().format(DATE_FORMATTER) + 
                " To: " + report.getEndDate().format(DATE_FORMATTER), NORMAL_FONT);
            dates.setSpacingAfter(20);
            document.add(dates);

            // Add summary table
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20);

            addTableHeader(summaryTable, "Metric", "Value");
            addTableRow(summaryTable, "Total Shipments", String.valueOf(report.getTotalShipments()));
            addTableRow(summaryTable, "Pending", String.valueOf(report.getPendingCount()));
            addTableRow(summaryTable, "Assigned", String.valueOf(report.getAssignedCount()));
            addTableRow(summaryTable, "In Transit", String.valueOf(report.getInTransitCount()));
            addTableRow(summaryTable, "Delivered", String.valueOf(report.getDeliveredCount()));
            addTableRow(summaryTable, "Cancelled", String.valueOf(report.getCancelledCount()));
            addTableRow(summaryTable, "Average Weight", String.format("%.2f kg", report.getAverageWeight()));

            document.add(summaryTable);

            // Add package type breakdown
            if (report.getPackageTypeCounts() != null && !report.getPackageTypeCounts().isEmpty()) {
                Paragraph typeHeader = new Paragraph("Package Types Breakdown", HEADER_FONT);
                typeHeader.setSpacingBefore(10);
                typeHeader.setSpacingAfter(10);
                document.add(typeHeader);

                PdfPTable typeTable = new PdfPTable(2);
                typeTable.setWidthPercentage(60);
                addTableHeader(typeTable, "Type", "Count");

                for (Map.Entry<String, Long> entry : report.getPackageTypeCounts().entrySet()) {
                    addTableRow(typeTable, entry.getKey(), entry.getValue().toString());
                }

                document.add(typeTable);
            }

            // Add footer
            addFooter(document);

            document.close();
            System.out.println("Shipment report exported to: " + filename);
        } catch (Exception e) {
            System.err.println("Error exporting shipment report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exports a delivery performance report to PDF.
     */
    public void exportDeliveryPerformanceReport(DeliveryPerformanceReport report, String filename) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add title
            Paragraph title = new Paragraph("DELIVERY PERFORMANCE REPORT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add date range
            Paragraph dates = new Paragraph(
                "Period: " + report.getStartDate().format(DATE_FORMATTER) + 
                " to " + report.getEndDate().format(DATE_FORMATTER), NORMAL_FONT);
            dates.setSpacingAfter(20);
            document.add(dates);

            // Add performance metrics
            PdfPTable metricsTable = new PdfPTable(2);
            metricsTable.setWidthPercentage(100);
            metricsTable.setSpacingAfter(20);

            addTableHeader(metricsTable, "Metric", "Value");
            addTableRow(metricsTable, "Total Deliveries", String.valueOf(report.getTotalDeliveries()));
            addTableRow(metricsTable, "On-Time Deliveries", String.valueOf(report.getOnTimeDeliveries()));
            addTableRow(metricsTable, "Delayed Deliveries", String.valueOf(report.getDelayedDeliveries()));
            addTableRow(metricsTable, "On-Time Percentage", String.format("%.2f%%", report.getOnTimePercentage()));
            addTableRow(metricsTable, "Average Delivery Time", String.format("%.2f hours", report.getAverageDeliveryTime()));

            document.add(metricsTable);

            // Add performance visualization (text-based)
            Paragraph perfSection = new Paragraph("Performance Analysis", HEADER_FONT);
            perfSection.setSpacingBefore(20);
            perfSection.setSpacingAfter(10);
            document.add(perfSection);

            String performanceText;
            if (report.getOnTimePercentage() >= 90) {
                performanceText = "Excellent performance! Over 90% of deliveries are on time.";
            } else if (report.getOnTimePercentage() >= 75) {
                performanceText = "Good performance. Consider optimizing routes to improve on-time delivery rate.";
            } else {
                performanceText = "Performance needs improvement. Review delivery processes and resource allocation.";
            }

            Paragraph analysis = new Paragraph(performanceText, NORMAL_FONT);
            document.add(analysis);

            addFooter(document);
            document.close();
            System.out.println("Delivery performance report exported to: " + filename);
        } catch (Exception e) {
            System.err.println("Error exporting delivery performance report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exports a revenue report to PDF.
     */
    public void exportRevenueReport(RevenueReport report, String filename) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add title
            Paragraph title = new Paragraph("REVENUE REPORT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add date range
            Paragraph dates = new Paragraph(
                "Period: " + report.getStartDate().format(DATE_FORMATTER) + 
                " to " + report.getEndDate().format(DATE_FORMATTER), NORMAL_FONT);
            dates.setSpacingAfter(20);
            document.add(dates);

            // Add revenue summary
            PdfPTable revenueTable = new PdfPTable(2);
            revenueTable.setWidthPercentage(100);
            revenueTable.setSpacingAfter(20);

            addTableHeader(revenueTable, "Metric", "Amount");
            addTableRow(revenueTable, "Total Invoices", String.valueOf(report.getTotalInvoices()));
            addTableRow(revenueTable, "Total Revenue", String.format("$%.2f", report.getTotalRevenue()));
            addTableRow(revenueTable, "Paid Revenue", String.format("$%.2f", report.getPaidRevenue()));
            addTableRow(revenueTable, "Unpaid Revenue", String.format("$%.2f", report.getUnpaidRevenue()));
            addTableRow(revenueTable, "Paid Invoices", String.valueOf(report.getPaidInvoices()));
            addTableRow(revenueTable, "Unpaid Invoices", String.valueOf(report.getUnpaidInvoices()));
            addTableRow(revenueTable, "Average Invoice", String.format("$%.2f", report.getAverageInvoiceAmount()));

            document.add(revenueTable);

            // Add revenue analysis
            Paragraph analysisHeader = new Paragraph("Financial Analysis", HEADER_FONT);
            analysisHeader.setSpacingBefore(20);
            analysisHeader.setSpacingAfter(10);
            document.add(analysisHeader);

            double collectionRate = (report.getTotalRevenue() > 0) ? 
                (report.getPaidRevenue() / report.getTotalRevenue() * 100) : 0;

            Paragraph analysis = new Paragraph(
                String.format("Collection Rate: %.2f%% of total revenue has been collected. ", collectionRate) +
                String.format("Outstanding amount: $%.2f from %d unpaid invoices.", 
                    report.getUnpaidRevenue(), report.getUnpaidInvoices()), 
                NORMAL_FONT);
            document.add(analysis);

            addFooter(document);
            document.close();
            System.out.println("Revenue report exported to: " + filename);
        } catch (Exception e) {
            System.err.println("Error exporting revenue report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exports a vehicle utilization report to PDF.
     */
    public void exportVehicleUtilizationReport(VehicleUtilizationReport report, String filename) {
        try {
            Document document = new Document(PageSize.A4, 36, 36, 54, 54);
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add title
            Paragraph title = new Paragraph("VEHICLE UTILIZATION REPORT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add report date
            Paragraph date = new Paragraph(
                "Report Date: " + report.getReportDate().format(DATE_FORMATTER), NORMAL_FONT);
            date.setSpacingAfter(20);
            document.add(date);

            // Add summary
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(70);
            summaryTable.setSpacingAfter(20);

            addTableHeader(summaryTable, "Metric", "Value");
            addTableRow(summaryTable, "Total Vehicles", String.valueOf(report.getTotalVehicles()));
            addTableRow(summaryTable, "Active Vehicles", String.valueOf(report.getActiveVehicles()));
            addTableRow(summaryTable, "Average Utilization", String.format("%.2f%%", report.getAverageUtilization()));

            document.add(summaryTable);

            // Add detailed vehicle table
            Paragraph detailHeader = new Paragraph("Vehicle Details", HEADER_FONT);
            detailHeader.setSpacingBefore(10);
            detailHeader.setSpacingAfter(10);
            document.add(detailHeader);

            PdfPTable vehicleTable = new PdfPTable(6);
            vehicleTable.setWidthPercentage(100);
            vehicleTable.setWidths(new float[]{2, 1.5f, 1.5f, 1.5f, 1.5f, 1});

            addTableHeader(vehicleTable, "Vehicle ID", "Type", "Weight", "Packages", "Utilization", "Status");

            for (VehicleUtilization util : report.getVehicleUtilizations()) {
                PdfPCell cell1 = new PdfPCell(new Phrase(util.getVehicleId(), NORMAL_FONT));
                PdfPCell cell2 = new PdfPCell(new Phrase(util.getVehicleType(), NORMAL_FONT));
                PdfPCell cell3 = new PdfPCell(new Phrase(
                    String.format("%.1f/%.1f", util.getCurrentWeight(), util.getMaxWeight()), NORMAL_FONT));
                PdfPCell cell4 = new PdfPCell(new Phrase(
                    String.format("%d/%d", util.getCurrentPackages(), util.getMaxPackages()), NORMAL_FONT));
                PdfPCell cell5 = new PdfPCell(new Phrase(
                    String.format("%.1f%%", util.getUtilizationPercentage()), NORMAL_FONT));
                PdfPCell cell6 = new PdfPCell(new Phrase(
                    util.isAvailable() ? "Available" : "Full", NORMAL_FONT));

                vehicleTable.addCell(cell1);
                vehicleTable.addCell(cell2);
                vehicleTable.addCell(cell3);
                vehicleTable.addCell(cell4);
                vehicleTable.addCell(cell5);
                vehicleTable.addCell(cell6);
            }

            document.add(vehicleTable);

            addFooter(document);
            document.close();
            System.out.println("Vehicle utilization report exported to: " + filename);
        } catch (Exception e) {
            System.err.println("Error exporting vehicle utilization report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to add table header cells.
     */
    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, BOLD_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    /**
     * Helper method to add table row.
     */
    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setPadding(6);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(6);
        table.addCell(valueCell);
    }

    /**
     * Adds a footer to the document.
     */
    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph(
            "\nGenerated by SmartShip Management System - " + 
            java.time.LocalDateTime.now().format(DATE_FORMATTER), 
            new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }
}