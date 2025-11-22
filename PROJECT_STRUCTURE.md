```markdown
# SmartShip Package Management System - Project Structure

## Project Overview
This is a full-stack Java application designed to manage shipments, fleets, billing, and user operations for a courier company. It's built as a group project for Advanced Programming (CIT3009) course.

## Directory Structure (relative to repository root)
```
Managmentsystem/
├── .classpath
├── .gitattributes
├── .gitignore
├── .project
├── README.md
├── PROJECT_STRUCTURE.md
├── sources.txt
├── .settings/
├── bin/
│   └── ... (compiled classes, ignored in VCS)
├── lib/
│   └── ... (third-party jars referenced by the project)
└── src/
    ├── module-info.java
    ├── billingAndPaymentModule/
    │   ├── Invoice.java
    │   ├── InvoiceStatus.java
    │   ├── Payment.java
    │   ├── PaymentMethod.java
    │   └── PaymentStatus.java
    ├── databaseModule/
    │   ├── DBHelper.java
    │   ├── bapDAO/
    │   │   ├── InvoiceDAO.java
    │   │   └── PaymentDAO.java
    │   ├── sDAO/
    │   │   └── ShipmentDAO.java
    │   ├── uDAO/
    │   │   ├── ClerkDAO.java
    │   │   ├── CustomerDAO.java
    │   │   ├── DriverDAO.java
    │   │   ├── ManagerDAO.java
    │   │   └── UserDAO.java
    │   └── varDAO/
    │       ├── RouteDAO.java
    │       └── VehicleDAO.java
    ├── gui/
    │   ├── ClerkPortal.java
    │   ├── CustomerPortal.java
    │   ├── DriverPortal.java
    │   ├── LoginWindow.java
    │   ├── MainWindow.java
    │   └── ManagerPortal.java
    ├── reportingModule/
    │   └── Pdfreportexporter.java
    ├── shipmentModule/
    │   ├── PackageType.java
    │   ├── Shipment.java
    │   ├── ShipmentStatus.java
    │   └── ShipmentStatusListener.java
    ├── testing/
    │   ├── Testdriver.java
    │   └── TestRoute.java
    ├── userModule/
    │   ├── Clerk.java
    │   ├── Customer.java
    │   ├── Driver.java
    │   ├── Manager.java
    │   ├── PasswordHasher.java
    │   └── User.java
    └── vehicleAndRoutingModule/
        ├── Route.java
        └── Vehicle.java
```

## Source Code Modules (brief)

- billingAndPaymentModule
  - Invoice, InvoiceStatus, Payment, PaymentMethod, PaymentStatus: invoice and payment domain models and enums

- databaseModule
  - DBHelper: DB connectivity helper
  - DAO subpackages (bapDAO, sDAO, uDAO, varDAO): data access objects for invoices, payments, shipments, users, routes, and vehicles

- gui
  - Swing/JavaFX GUIs for each user role and common windows (LoginWindow, MainWindow)

- reportingModule
  - Pdfreportexporter: PDF report generation

- shipmentModule
  - Shipment domain model and supporting enums/interfaces

- userModule
  - Domain classes for Clerk, Customer, Driver, Manager and authentication utilities (PasswordHasher, User base class)

- vehicleAndRoutingModule
  - Route and Vehicle domain models and assignment logic

- testing
  - Testdriver, TestRoute: lightweight test/demo classes

## Notes
* The project uses a modular layout (module-info.java) and standard package organization.
* Keep lib/ synchronized with project classpath; if you add third-party jars, add them to lib/ and to the Eclipse build path.
* sources.txt (in repo root) lists the source files used by the Eclipse project — updated to reflect current file layout.
```
