# SmartShip Package Management System - Project Structure

## Project Overview
This is a full-stack Java application designed to manage shipments, fleets, billing, and user operations for a courier company. It's built as a group project for Advanced Programming (CIT3009) course at the University of Technology, Jamaica.

## Directory Structure
```
D:\Dev\Projects\eclipse-workspace\Managmentsystem\
├───.classpath
├───.gitattributes
├───.gitignore
├───.project
├───AP-Project Sem 1 AY2025-26.pdf
├───README.md
├───sources.txt
├───.git\...
├───.settings\
│   ├───org.eclipse.core.resources.prefs
│   └───org.eclipse.jdt.core.prefs
├───bin\
│   ├───.gitignore
│   ├───billingAndPaymentModule\
│   ├───databaseModule\
│   ├───driver\
│   └───...
├───lib\
└───src\
```

## Source Code Modules

### 1. billingAndPaymentModule
- **Invoice.java** - Handles invoice creation, tracking payments, and status management
- **Payment.java** - Manages payment processing, methods (CASH/CARD), and payment status
- **PaymentStatus.java** - Enum for payment status values

### 2. databaseModule
- **DBHelper.java** - Database connection helper with MySQL connectivity
- **sDAO/** - Shipment Data Access Objects
  - **ShipmentDAO.java** - CRUD operations for shipments
- **uDAO/** - User Data Access Objects
  - **ClerkDAO.java** - CRUD operations for clerks
  - **CustomerDAO.java** - CRUD operations for customers
  - **DriverDAO.java** - CRUD operations for drivers
  - **ManagerDAO.java** - CRUD operations for managers
  - **UserDAO.java** - Base user CRUD operations
- **varDAO/** - Vehicle and Route Data Access Objects
  - **RouteDAO.java** - CRUD operations for routes
  - **VehicleDAO.java** - CRUD operations for vehicles
- **bapDAO/** - Billing and Payment Data Access Objects
  - **InvoiceDAO.java** - CRUD operations for invoices
  - **PaymentDAO.java** - CRUD operations for payments

### 3. gui
- **ClerkPortal.java** - GUI for clerks
- **CustomerPortal.java** - GUI for customers
- **DriverPortal.java** - GUI for drivers
- **LoginWindow.java** - Login interface
- **MainWindow.java** - Main application window
- **ManagerPortal.java** - GUI for managers

### 4. reportingModule
- **Pdfreportexporter.java** - PDF report generation functionality

### 5. shipmentModule
- **PackageType.java** - Enum for package types (STANDARD, EXPRESS, FRAGILE)
- **Shipment.java** - Core shipment entity
- **ShipmentStatus.java** - Enum for shipment statuses
- **ShipmentStatusListener.java** - Interface for shipment status change listeners

### 6. testing
- **Testdriver.java** - Test driver class with example operations
- **TestRoute.java** - Route testing functionality

### 7. userModule
- **Clerk.java** - Clerk user type extending User
- **Customer.java** - Customer user type extending User
- **Driver.java** - Driver user type extending User
- **Manager.java** - Manager user type extending User
- **PasswordHasher.java** - Password hashing utilities using PBKDF2
- **User.java** - Base user class with authentication functionality

### 8. vehicleAndRoutingModule
- **Route.java** - Route entity with vehicle assignment and shipment planning
- **Vehicle.java** - Vehicle entity with capacity management and shipment assignment

## Key Features Implemented
1. **User Management**: Support for different user roles (Customer, Clerk, Driver, Manager)
2. **Shipment Management**: Complete shipment lifecycle with tracking and status updates
3. **Fleet Management**: Vehicle capacity and assignment management
4. **Billing System**: Invoice generation and payment processing (CASH/CARD)
5. **Database Integration**: MySQL with comprehensive DAO patterns
6. **Reporting**: PDF report generation for business analytics

## Database Schema
The system uses a normalized MySQL database with tables for:
- Users and their roles (user, customer, driver, clerk, manager)
- Shipments with tracking and status
- Vehicles and routes with assignment capabilities
- Invoice and payment management
- Junction tables for relationships (vehicle_shipments, route_shipments)

## Technology Stack
- Java SE 8+
- MySQL Database
- JDBC for database connectivity
- Swing for GUI components
- iText PDF library for report generation
- PBKDF2 for secure password hashing