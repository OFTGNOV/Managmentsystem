package reportingModule;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import billingAndPaymentModule.Invoice;
import shipmentModule.Shipment;
import vehicleAndRoutingModule.Vehicle;
import databaseModule.bapDAO.InvoiceDAO;
import databaseModule.sDAO.ShipmentDAO;
import databaseModule.varDAO.VehicleDAO;
import userModule.User;
import userModule.UserType;

public class Pdfreportexporter {
    
    public static void exportRevenueSummaryReport(String filePath, LocalDate startDate, LocalDate endDate) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDFont normalFont = PDType1Font.HELVETICA;
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Title
            contentStream.beginText();
            contentStream.setFont(font, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Revenue Summary Report");
            contentStream.endText();
            
            // Date range
            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Date Range: " + startDate + " to " + endDate);
            contentStream.endText();
            
            // Revenue calculation
            double totalRevenue = calculateRevenueBetweenDates(startDate, endDate);
            double totalExpenses = calculateExpensesBetweenDates(startDate, endDate);
            double netRevenue = totalRevenue - totalExpenses;
            
            // Revenue details
            float yPosition = 680;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Revenue Details:");
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total Revenue: $" + String.format("%.2f", totalRevenue));
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total Expenses: $" + String.format("%.2f", totalExpenses));
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.setFont(font, 14);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Net Revenue: $" + String.format("%.2f", netRevenue));
            contentStream.endText();
            
            // Revenue by zone
            yPosition -= 40;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Revenue by Zone:");
            contentStream.endText();
            
            Map<Integer, Double> revenueByZone = calculateRevenueByZone(startDate, endDate);
            for (Map.Entry<Integer, Double> entry : revenueByZone.entrySet()) {
                yPosition -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Zone " + entry.getKey() + ": $" + String.format("%.2f", entry.getValue()));
                contentStream.endText();
            }
            
            contentStream.close();
            
            document.save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void exportShipmentVolumeReport(String filePath, LocalDate startDate, LocalDate endDate, String period) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDFont normalFont = PDType1Font.HELVETICA;
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Title
            contentStream.beginText();
            contentStream.setFont(font, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Shipment Volume Report");
            contentStream.endText();
            
            // Date range
            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Period: " + period + " | Date Range: " + startDate + " to " + endDate);
            contentStream.endText();
            
            // Volume details
            int totalShipments = getShipmentCountBetweenDates(startDate, endDate);
            double totalWeight = getTotalShipmentWeightBetweenDates(startDate, endDate);
            
            float yPosition = 680;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Shipment Volume Summary:");
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total Shipments: " + totalShipments);
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total Weight: " + String.format("%.2f", totalWeight) + " kg");
            contentStream.endText();
            
            // Volume by day/week/month
            yPosition -= 40;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Volume by " + period + ":");
            contentStream.endText();
            
            Map<String, Integer> shipmentsByPeriod = getShipmentsByPeriod(startDate, endDate, period);
            for (Map.Entry<String, Integer> entry : shipmentsByPeriod.entrySet()) {
                yPosition -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(entry.getKey() + ": " + entry.getValue() + " shipments");
                contentStream.endText();
            }
            
            contentStream.close();
            
            document.save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void exportDeliveryPerformanceReport(String filePath, LocalDate startDate, LocalDate endDate) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDFont normalFont = PDType1Font.HELVETICA;
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Title
            contentStream.beginText();
            contentStream.setFont(font, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Delivery Performance Report");
            contentStream.endText();
            
            // Date range
            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Date Range: " + startDate + " to " + endDate);
            contentStream.endText();
            
            // Performance details
            int totalShipments = getShipmentCountBetweenDates(startDate, endDate);
            int deliveredShipments = getDeliveredShipmentCountBetweenDates(startDate, endDate);
            int overdueShipments = getOverdueShipmentCountBetweenDates(startDate, endDate);
            
            float yPosition = 680;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Performance Metrics:");
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total Shipments: " + totalShipments);
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Delivered Shipments: " + deliveredShipments);
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Overdue Shipments: " + overdueShipments);
            contentStream.endText();
            
            if (totalShipments > 0) {
                double deliveryRate = (double) deliveredShipments / totalShipments * 100;
                yPosition -= 20;
                contentStream.beginText();
                contentStream.setFont(font, 14);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Delivery Rate: " + String.format("%.2f", deliveryRate) + "%");
                contentStream.endText();
            }
            
            // Performance by zone
            yPosition -= 40;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Performance by Zone:");
            contentStream.endText();
            
            Map<Integer, Map<String, Integer>> performanceByZone = getPerformanceByZone(startDate, endDate);
            for (Map.Entry<Integer, Map<String, Integer>> zoneEntry : performanceByZone.entrySet()) {
                int zone = zoneEntry.getKey();
                Map<String, Integer> details = zoneEntry.getValue();
                int zoneTotal = details.getOrDefault("total", 0);
                int zoneDelivered = details.getOrDefault("delivered", 0);
                
                yPosition -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                String zoneDeliveryRate = zoneTotal > 0 ? String.format("%.2f", (double) zoneDelivered / zoneTotal * 100) + "%" : "0%";
                contentStream.showText("Zone " + zone + ": " + zoneDelivered + "/" + zoneTotal + " delivered (" + zoneDeliveryRate + ")");
                contentStream.endText();
            }
            
            contentStream.close();
            
            document.save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void exportVehicleUtilizationReport(String filePath, LocalDate startDate, LocalDate endDate) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDFont normalFont = PDType1Font.HELVETICA;
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Title
            contentStream.beginText();
            contentStream.setFont(font, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Vehicle Utilization Report");
            contentStream.endText();
            
            // Date range
            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Date Range: " + startDate + " to " + endDate);
            contentStream.endText();
            
            // All vehicles
            List<Vehicle> allVehicles = VehicleDAO.readAllVehicles();
            float yPosition = 680;
            
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Vehicle Utilization Summary:");
            contentStream.endText();
            
            yPosition -= 30;
            contentStream.beginText();
            contentStream.setFont(normalFont, 10);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("License Plate\t\tMax Capacity (kg)\t\tCurrent Load (kg)\t\tUtilization %");
            contentStream.endText();
            
            for (Vehicle vehicle : allVehicles) {
                yPosition -= 15;
                contentStream.beginText();
                contentStream.setFont(normalFont, 10);
                contentStream.newLineAtOffset(50, yPosition);
                
                double utilization = vehicle.getMaxWeightCapacity() > 0 ? 
                    (vehicle.getCurrentWeight() / vehicle.getMaxWeightCapacity()) * 100 : 0;
                
                String utilizationStr = String.format("%.2f", utilization);
                contentStream.showText(vehicle.getLicensePlate() + "\t\t" + 
                    vehicle.getMaxWeightCapacity() + "\t\t\t" + 
                    vehicle.getCurrentWeight() + "\t\t\t" + 
                    utilizationStr + "%");
                contentStream.endText();
            }
            
            contentStream.close();
            
            document.save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    // Helper method to export a single invoice as PDF
    public static void exportInvoiceAsPDF(Invoice invoice, String filePath) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDFont normalFont = PDType1Font.HELVETICA;
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Header
            contentStream.beginText();
            contentStream.setFont(font, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("INVOICE");
            contentStream.endText();
            
            // Invoice details
            float yPosition = 720;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Invoice Number: " + invoice.getInvoiceID());
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Issue Date: " + invoice.getIssueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Due Date: " + invoice.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Status: " + invoice.getStatus());
            contentStream.endText();
            
            // Sender information
            yPosition -= 40;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("From:");
            contentStream.endText();
            
            if (invoice.getSenderId() > 0) {
                userModule.User sender = databaseModule.uDAO.UserDAO.retrieveUserRecordById(invoice.getSenderId());
                if (sender != null) {
                    yPosition -= 20;
                    contentStream.beginText();
                    contentStream.setFont(normalFont, 12);
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText(sender.getFirstName() + " " + sender.getLastName());
                    contentStream.endText();

                    yPosition -= 15;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText(sender.getAddress());
                    contentStream.endText();

                    yPosition -= 15;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Zone: " + sender.getZone());
                    contentStream.endText();
                } else {
                    yPosition -= 20;
                    contentStream.beginText();
                    contentStream.setFont(normalFont, 12);
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("User ID: " + invoice.getSenderId());
                    contentStream.endText();
                }
            }
            
            // Recipient information
            yPosition -= 25;
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("To:");
            contentStream.endText();
            
            if (invoice.getRecipentId() > 0) {
                userModule.User recipient = databaseModule.uDAO.UserDAO.retrieveUserRecordById(invoice.getRecipentId());
                if (recipient != null) {
                    yPosition -= 20;
                    contentStream.beginText();
                    contentStream.setFont(normalFont, 12);
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText(recipient.getFirstName() + " " + recipient.getLastName());
                    contentStream.endText();

                    yPosition -= 15;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText(recipient.getAddress());
                    contentStream.endText();

                    yPosition -= 15;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Zone: " + recipient.getZone());
                    contentStream.endText();
                } else {
                    yPosition -= 20;
                    contentStream.beginText();
                    contentStream.setFont(normalFont, 12);
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("User ID: " + invoice.getRecipentId());
                    contentStream.endText();
                }
            }
            
            // Shipment details
            if (invoice.getShipment() != null) {
                yPosition -= 25;
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Shipment Details:");
                contentStream.endText();
                
                yPosition -= 20;
                contentStream.beginText();
                contentStream.setFont(normalFont, 12);
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText("Tracking Number: " + invoice.getShipment().getTrackingNumber());
                contentStream.endText();
                
                yPosition -= 15;
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText("Package Type: " + invoice.getShipment().getpType().name());
                contentStream.endText();
                
                yPosition -= 15;
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText("Weight: " + invoice.getShipment().getWeight() + " kg");
                contentStream.endText();
                
                yPosition -= 15;
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText("Dimensions: " + invoice.getShipment().getLength() + "x" + 
                    invoice.getShipment().getWidth() + "x" + invoice.getShipment().getHeight() + " cm");
                contentStream.endText();
            }
            
            // Total amount
            yPosition -= 35;
            contentStream.beginText();
            contentStream.setFont(font, 14);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total Amount: $" + String.format("%.2f", invoice.getTotalAmount()));
            contentStream.endText();
            
            // Payments
            if (invoice.getPayments() != null && !invoice.getPayments().isEmpty()) {
                yPosition -= 30;
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Payment History:");
                contentStream.endText();
                
                for (int i = 0; i < invoice.getPayments().size(); i++) {
                    yPosition -= 15;
                    contentStream.beginText();
                    contentStream.setFont(normalFont, 10);
                    contentStream.newLineAtOffset(70, yPosition);
                    
                    var payment = invoice.getPayments().get(i);
                    contentStream.showText("Payment " + (i+1) + ": $" + String.format("%.2f", payment.getAmount()) + 
                        " | " + payment.getPaymentMethod() + " | " + payment.getStatus() + 
                        " | " + (payment.getPaymentDate() != null ? 
                            payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"));
                    contentStream.endText();
                }
                
                yPosition -= 20;
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText("Remaining Balance: $" + String.format("%.2f", invoice.getRemainingBalance()));
                contentStream.endText();
            }
            
            // Notes
            if (invoice.getNotes() != null) {
                yPosition -= 35;
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Notes:");
                contentStream.endText();
                
                yPosition -= 20;
                contentStream.beginText();
                contentStream.setFont(normalFont, 10);
                contentStream.newLineAtOffset(70, yPosition);
                // Simple text wrapping for notes
                String notes = invoice.getNotes();
                if (notes.length() > 80) {
                    for (int i = 0; i < notes.length(); i += 80) {
                        int end = Math.min(i + 80, notes.length());
                        String line = notes.substring(i, end);
                        contentStream.showText(line);
                        contentStream.endText();
                        yPosition -= 15;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(70, yPosition);
                    }
                } else {
                    contentStream.showText(notes);
                }
                contentStream.endText();
            }
            
            contentStream.close();
            
            try (FileOutputStream output = new FileOutputStream(filePath)) {
                document.save(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Helper methods for revenue calculations
    public static double calculateRevenueBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Invoice> allInvoices = InvoiceDAO.readAllInvoices();
        double totalRevenue = 0.0;
        
        for (Invoice invoice : allInvoices) {
            if (invoice.getIssueDate() != null) {
                LocalDate issueDate = invoice.getIssueDate().toLocalDate();
                if (!issueDate.isBefore(startDate) && !issueDate.isAfter(endDate)) {
                    totalRevenue += invoice.getTotalAmount();
                }
            }
        }
        
        return totalRevenue;
    }
    
    private static double calculateExpensesBetweenDates(LocalDate startDate, LocalDate endDate) {
        // Placeholder for expenses calculation
        // In a real implementation, this would include vehicle maintenance, fuel costs, etc.
        return 0.0; // Placeholder
    }
    
    private static Map<Integer, Double> calculateRevenueByZone(LocalDate startDate, LocalDate endDate) {
        // This would typically group invoices by customer zone and sum revenue
        // For now, returning a placeholder implementation
        java.util.HashMap<Integer, Double> revenueByZone = new java.util.HashMap<>();
        
        List<Invoice> allInvoices = InvoiceDAO.readAllInvoices();
        for (Invoice invoice : allInvoices) {
            if (invoice.getIssueDate() != null && invoice.getRecipentId() > 0) {
                LocalDate issueDate = invoice.getIssueDate().toLocalDate();
                if (!issueDate.isBefore(startDate) && !issueDate.isAfter(endDate)) {
                    // Need to get user by ID to get the zone
                    userModule.User recipient = databaseModule.uDAO.UserDAO.retrieveUserRecordById(invoice.getRecipentId());
                    if (recipient != null) {
                        int zone = recipient.getZone();
                        revenueByZone.merge(zone, invoice.getTotalAmount(), Double::sum);
                    }
                }
            }
        }
        
        return revenueByZone;
    }
    
    // Helper methods for shipment volume
    public static int getShipmentCountBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Shipment> allShipments = ShipmentDAO.readAllShipments();
        int count = 0;
        
        for (Shipment shipment : allShipments) {
            if (shipment.getCreatedDate() != null) {
                LocalDate creationDate = shipment.getCreatedDate().toLocalDate();
                if (!creationDate.isBefore(startDate) && !creationDate.isAfter(endDate)) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    //Gets total shipment weight between dates
    private static double getTotalShipmentWeightBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Shipment> allShipments = ShipmentDAO.readAllShipments();
        double totalWeight = 0.0;
        
        for (Shipment shipment : allShipments) {
            if (shipment.getCreatedDate() != null) {
                LocalDate creationDate = shipment.getCreatedDate().toLocalDate();
                if (!creationDate.isBefore(startDate) && !creationDate.isAfter(endDate)) {
                    totalWeight += shipment.getWeight();
                }
            }
        }
        return totalWeight;
    }
    
    //gets Shipments by period (daily, weekly, monthly)
    private static Map<String, Integer> getShipmentsByPeriod(LocalDate startDate, LocalDate endDate, String period) {
        Map<String, Integer> shipmentsByPeriod = new java.util.HashMap<>();
        
        List<Shipment> allShipments = ShipmentDAO.readAllShipments();
        for (Shipment shipment : allShipments) {
            if (shipment.getCreatedDate() != null) {
                LocalDate creationDate = shipment.getCreatedDate().toLocalDate();
                if (!creationDate.isBefore(startDate) && !creationDate.isAfter(endDate)) {
                    String periodKey = getPeriodKey(creationDate, period);
                    shipmentsByPeriod.merge(periodKey, 1, Integer::sum);
                }
            }
        }
        
        return shipmentsByPeriod;
    }

    // Gets the period key for a given date and period type
    private static String getPeriodKey(LocalDate date, String period) {
        switch (period.toLowerCase()) {
            case "daily":
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case "weekly":
                return "Week " + date.getYear() + "-W" + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            case "monthly":
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            default:
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
    
    // Gets delivered and overdue shipment counts
    // Helper methods for delivery performance
    private static int getDeliveredShipmentCountBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Shipment> allShipments = ShipmentDAO.readAllShipments();
        int count = 0;
        
        for (Shipment shipment : allShipments) {
            if (shipment.getCreatedDate() != null) {
                LocalDate creationDate = shipment.getCreatedDate().toLocalDate();
                if (!creationDate.isBefore(startDate) && !creationDate.isAfter(endDate) 
                    && shipment.getStatus() == shipmentModule.ShipmentStatus.DELIVERED) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private static int getOverdueShipmentCountBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Shipment> allShipments = ShipmentDAO.readAllShipments();
        int count = 0;
        
        for (Shipment shipment : allShipments) {
            if (shipment.getCreatedDate() != null) {
                LocalDate creationDate = shipment.getCreatedDate().toLocalDate();
                if (!creationDate.isBefore(startDate) && !creationDate.isAfter(endDate) 
                    && shipment.getStatus() == shipmentModule.ShipmentStatus.PENDING 
                    && shipment.getCreatedDate().isBefore(LocalDateTime.now().minusDays(7))) {
                    // Consider shipments pending for more than 7 days as overdue
                    count++;
                }
            }
        }
        return count;
    }
    
    private static Map<Integer, Map<String, Integer>> getPerformanceByZone(LocalDate startDate, LocalDate endDate) {
        Map<Integer, Map<String, Integer>> performanceByZone = new java.util.HashMap<>();

        List<Shipment> allShipments = ShipmentDAO.readAllShipments();
        for (Shipment shipment : allShipments) {
            if (shipment.getCreatedDate() != null) {
                LocalDate creationDate = shipment.getCreatedDate().toLocalDate();
                if (!creationDate.isBefore(startDate) && !creationDate.isAfter(endDate)) {
                    int zone = shipment.getRecipent().getZone();

                    performanceByZone.computeIfAbsent(zone, k -> new java.util.HashMap<>())
                        .merge("total", 1, Integer::sum);

                    if (shipment.getStatus() == shipmentModule.ShipmentStatus.DELIVERED) {
                        performanceByZone.get(zone).merge("delivered", 1, Integer::sum);
                    }
                }
            }
        }

        return performanceByZone;
    }

    // Public methods for use by ReportManager
    public static Map<Integer, Double> getRevenueByZone(LocalDate startDate, LocalDate endDate) {
        return calculateRevenueByZone(startDate, endDate);
    }

 }
 
 