# SmartShip Package Management System - User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [System Requirements](#system-requirements)
3. [Database Setup](#database-setup)
4. [Getting Started](#getting-started)
5. [User Roles](#user-roles)
    - [Customer](#customer-role)
    - [Clerk](#clerk-role)
    - [Manager](#manager-role)
    - [Driver](#driver-role)
6. [Features](#features)
7. [Troubleshooting](#troubleshooting)

## Introduction

SmartShip is a full-stack Java application designed to manage shipments, fleets, billing, and user operations for a courier company. It provides a complete client/server Java application with persistent storage, GUI interface for different user roles, and concurrency control to prevent scheduling conflicts.

## System Requirements

- Java SE Development Kit (JDK 8 or higher)
- MySQL Server (version 5.6 or higher)
- Apache PDFBox library (for PDF report generation)
- A compatible IDE like Eclipse

## Database Setup

1. **Database Configuration**:
   - The application expects a MySQL database named `smartship_package_management_system`
   - Default connection settings: 
     - Host: `localhost:3307`
     - Username: `root`
     - Password: `usbw`

2. **Creating the Database**:
   - Use the provided SQL file `smartship_package_management_system.sql` to set up your database
   - Import this file into your MySQL server to create all necessary tables and relationships
   - You can import using MySQL Workbench, command line, or any other MySQL client

3. **Modifying Connection Settings** (if needed):
   - Open `/src/databaseModule/DBHelper.java`
   - Update the `DB_URL`, `USER`, and `PASS` constants with your database credentials

## Getting Started

1. **Importing the Project**:
   - If using Eclipse:
     - File > Import > Git > Projects from Git > Next
     - Select Clone URI and enter the repository URL
     - Follow the wizard to complete the import
     - File > Import > General > Existing Projects into Workspace
     - Browse to the cloned repository folder
     - Ensure the project is checked and click Finish
   - Add external libraries from the `/lib` folder to your build path

2. **Running the Application**:
   - Locate `SmartShipGUI.java` in the `/src/gui` directory
   - Right-click > Run As > Java Application
   - The application will connect to the database and initialize the GUI
   - If the connection is successful, you'll see a message: "Connection Established Successfully!"

3. **Initial Login**:
   - The first time you run the application, you'll need to create an account
   - Click the "Sign Up" button to create a new user account

## User Roles

The system supports four distinct user roles, each with specific permissions and features:

### Customer Role

Customers can create shipments, track packages, and view invoices.

#### Creating a New Account:
1. Click "Sign Up" on the login screen
2. Select "Customer" as the account type
3. Fill in your first name, last name, email, and password
4. Provide your address and select your zone (1-4)
5. Click "Sign Up"

#### Creating a Shipment:
1. Log in with your customer account
2. Navigate to the "Create Shipment" tab in the Customer panel
3. Enter the recipient's email (they must be registered in the system)
4. Fill in the package weight and dimensions (Length x Width x Height in cm)
5. Select the package type: Standard, Express, or Fragile
6. Click "Create Shipment"
7. You'll see the total cost and be prompted to pay if desired

#### Tracking a Shipment:
1. Navigate to the "Track Shipment" tab in the Customer panel
2. Enter the tracking number in the search field
3. Click "Track" to see detailed shipment information

### Clerk Role

Clerks process shipment orders, assign shipments to routes and vehicles, update shipment statuses, and handle payments.

#### Creating a New Account:
1. Click "Sign Up" on the login screen
2. Select "Clerk" as the account type
3. Fill in your first name, last name, email, and password
4. Click "Sign Up"

#### Managing Shipments:
1. Log in with your clerk account
2. The main panel displays all shipments in a table
3. You can refresh the table with the "Refresh" button
4. Select a shipment and use:
   - **Set In Transit**: Updates selected shipment status to "In Transit"
   - **Set Delivered**: Updates selected shipment status to "Delivered"
   - **Assign Vehicle**: Assigns the selected shipment to a vehicle

#### Assigning Vehicles:
1. Select a shipment in the table
2. Click "Assign Vehicle"
3. Choose a vehicle from the available list
4. Enter route time (e.g., 09:00-12:00)
5. The system will check vehicle capacity before assignment

### Manager Role

Managers manage user accounts, oversee operations, and generate reports.

#### Creating a New Account:
1. Click "Sign Up" on the login screen
2. Select "Manager" as the account type
3. Fill in your first name, last name, email, and password
4. Click "Sign Up"

#### Managing Operations:
1. Log in with your manager account
2. View shipment statistics in the right panel
3. Manage shipments in the main table

#### Adding Vehicles:
1. Click "Add Vehicle" button
2. Enter license plate number
3. Specify max weight capacity (in kg)
4. Specify max package capacity (count)
5. Optionally assign a driver from the available list

#### Adding Drivers:
1. Click "Add Driver" button
2. Enter driver details:
   - First name, Last name
   - Email address
   - Password
   - Driver's license number

#### Generating Reports:
The system provides four types of reports:
1. **Revenue Report**: Shows revenue summary between selected dates
2. **Shipment Report**: Displays shipment volume data
3. **Delivery Report**: Shows delivery performance metrics
4. **Vehicle Report**: Details vehicle utilization

To generate a report:
1. Click the appropriate "Generate [Report Type] Report" button
2. Reports are saved as PDF files in the project directory

### Driver Role

Drivers view assigned deliveries and update package status.

#### Creating a New Account:
1. Click "Sign Up" on the login screen
2. Select "Driver" as the account type
3. Fill in your first name, last name, email, and password
4. Provide your driver's license number
5. Click "Sign Up"

#### Managing Deliveries:
1. Log in with your driver account
2. The main table displays all deliveries assigned to you
3. Information includes tracking number, sender, addresses, and current status

#### Updating Shipment Status:
1. Select a shipment in the table
2. Click "Update Status"
3. Choose either "In Transit" or "Delivered" from the options
4. The system updates the status in the database

## Features

### Shipment Management
- **Tracking Numbers**: Automatic generation of unique tracking numbers
- **Package Types**: Support for Standard, Express, and Fragile packages
- **Zone-based Handling**: Shipments are managed by zone for efficient routing
- **Status Lifecycle**: Pending → Assigned → In Transit → Delivered
- **Cost Calculation**: Automatic calculation based on weight, distance, and package type

### Fleet & Scheduling
- **Vehicle Capacity Management**: Enforces weight and quantity limits
- **Conflict Prevention**: Prevents double-booking vehicles or overlapping routes
- **Assignment Logic**: Safely assigns shipments to drivers and vehicles

### Billing & Payment
- **Invoice Generation**: Auto-generated invoices for all shipments
- **Payment Tracking**: Supports cash and card payments with status tracking
- **PDF Receipts**: Export invoices as PDF documents
- **Payment Status**: Tracks Paid, Partial, Unpaid, and Overdue statuses

### Reporting
- **Revenue Reports**: Summarizes income for specified periods
- **Shipment Volume**: Tracks shipment counts and weights by time period
- **Delivery Performance**: Monitors delivery rates and success metrics
- **Vehicle Utilization**: Shows how efficiently vehicles are being used
- **Exportable PDFs**: All reports can be saved as PDF files

## Troubleshooting

### Database Connection Issues
- **Problem**: "Connection Failed: Access denied" or similar error
- **Solution**: 
  - Verify MySQL server is running
  - Check database credentials in `/src/databaseModule/DBHelper.java`
  - Ensure the database name `smartship_package_management_system` exists
  - Confirm the database tables have been created using the SQL script

### Application Crashes on Startup
- **Problem**: Application closes immediately after launching
- **Solution**:
  - Ensure all required libraries in the `lib` folder are on the build path
  - Check Java version compatibility (JDK 8+ required)
  - Verify database connection settings

### "Recipient not found in the system"
- **Problem**: When creating a shipment, the system reports the recipient email is not registered
- **Solution**: 
  - Have the recipient create an account in the system first
  - Verify the email was entered correctly

### Vehicle Assignment Failures
- **Problem**: "Cannot assign shipment. Check vehicle weight/quantity limits"
- **Solution**:
  - The selected vehicle has exceeded its weight or package capacity
  - Choose a different vehicle with sufficient capacity
  - The system shows current vs maximum capacity when this occurs

### Missing PDF Export Functionality
- **Problem**: "Error generating report" or similar PDF-related errors
- **Solution**:
  - Ensure Apache PDFBox libraries are in the classpath
  - Check that the output directory has write permissions

### Login Issues
- **Problem**: "Invalid credentials" message appears
- **Solution**:
  - Double-check email and password
  - Verify the correct user category is selected
  - Confirm the account exists in the database

### Common Error Messages
- **"Weight must be between 0.01 and 999.99 kg"**: Enter valid weight within range
- **"Card number must be 16 digits"**: Enter exactly 16 digits for card number
- **"CVV must be 3 digits"**: Enter exactly 3 digits for CVV
- **"Expiration date must be in MM/YYYY format"**: Enter date in correct format (e.g., 03/2026)

## Support

If you continue to experience issues after following the troubleshooting steps:

1. Check that all required dependencies are properly installed and configured
2. Verify your database connection and ensure all tables exist
3. Review the console output for detailed error messages
4. Contact your system administrator if you're experiencing network or server issues

---

This manual provides the essential information needed to use the SmartShip Package Management System. For additional support, consult your system administrator or the development team.