# **SmartShip Package Management System.**

A full-stack Java application designed to manage shipments, fleets, billing, and user operations for a courier company.
Built as a group project for the **Advanced Programming (CIT3009)** course at the University of Technology, Jamaica.

---

## **📌 Overview**

SmartShip is a courier management system built to simulate how real logistics companies handle day-to-day operations. The goal was to replace manual processes with a digital platform that supports:

* Customer shipment requests
* Package tracking
* Vehicle scheduling & capacity management
* Driver assignment workflows
* Invoice generation & payments
* Business reporting

This application showcases OOP design, GUI development, database integration, networking, and concurrency within a real business scenario.

---

## **🎯 Core Objectives**

The project was designed to:

* Build a complete client/server Java application
* Apply solid object-oriented design (inheritance, interfaces, polymorphism)
* Work with a relational database for persistence
* Implement GUI interfaces for different user roles
* Manage concurrency so vehicles and shipments are never double-assigned
* Generate PDF-ready business reports
* Deliver a realistic courier management experience end-to-end

---

## **👤 User Roles & Capabilities**

### **Customer**

* Create accounts & log in
* Submit shipment requests
* Track packages online
* View and pay invoices

### **Clerk**

* Process shipment orders
* Assign shipments to routes and vehicles
* Update shipment statuses
* Handle cash/card payments

### **Driver**

* View assigned deliveries
* Update package status (In Transit / Delivered)

### **Manager**

* Manage user accounts
* Oversee all shipments, vehicles, and drivers
* Generate reports (revenue, performance, utilization)
* Export reports to PDF

---

## **📦 Key Features**

### **Shipment Management**

* Create detailed shipment orders
* Automatic tracking number generation
* Shipment types: Standard, Express, Fragile
* Zone-based distance handling
* Status lifecycle: Pending → Assigned → In Transit → Delivered
* Cost calculation based on weight, distance, and package type

### **Fleet & Scheduling**

* Vehicle capacity enforcement (weight & quantity)
* Prevent double-booking vehicles or overlapping routes
* Assign shipments safely to drivers and vehicles

### **Billing**

* Auto-generated invoices
* Payment statuses (Paid, Partial, Unpaid)
* Support for cash and card
* PDF receipts

### **Reporting**

* Revenue summaries
* Shipment volume (daily/weekly/monthly)
* Delivery performance
* Vehicle utilization
* Exportable PDF reports

---

## **🧱 Architecture**

* **Java Client/Server Application** – multiple clients connect simultaneously
* **Database-Driven Backend** – persistent storage for users, vehicles, shipments, invoices
* **GUI Application** – separate interfaces for each user role
* **Concurrency Control** – prevents scheduling and assignment conflicts

---

## **📊 System Design**

Major entities include:

* User (Customer, Clerk, Driver, Manager)
* Shipment
* Vehicle
* Assignment (link between shipments & vehicles)
* Invoice
* Payment

Design deliverables included:

* Class Diagram
* ER Diagram (3NF normalized)

---

## **🛠️ Technologies**

* **Java SE**
* **Java Swing / JavaFX** (GUI)
* **Socket Programming** (Client/Server)
* **SQL Database**
* **PDF Generation Libraries**

---

## Eclipse: Cloning & Importing into your Eclipse workspace

If you're a classmate and want to clone this repository and work on it in Eclipse, follow these steps.

Prerequisites:
* Install JDK (Java 8+ recommended).
* Install Git integration (Eclipse has EGit by default in most packages).
* (Optional) Create a local MySQL database and update DB connectivity in databaseModule/DBHelper.java before running.

1. Clone the repository
   - Open Eclipse.
   - File > Import...
   - Select Git > Projects from Git > Next.
   - Select Clone URI > Next.
   - For Repository URI enter: https://github.com/OFTGNOV/Managmentsystem.git
   - Enter credentials if required (you can clone anonymously for public repos).
   - Select the branch (usually main) and click Next.
   - Choose a local directory for the clone and click Finish.

2. Import the project into the workspace
   - After cloning, File > Import...
   - Select General > Existing Projects into Workspace > Next.
   - Click Browse and select the cloned repository folder.
   - The project should appear in the Projects list. Ensure it is checked and click Finish.

3. Configure project settings
   - Ensure the Java Build Path points to a valid JRE/JDK:
     - Right-click project > Properties > Java Build Path > Libraries.
     - Add/confirm the JRE System Library matches your installed JDK.
   - If your Eclipse workspace uses modules (module-info.java):
     - Ensure the project's Java compliance level matches the module settings (Project > Properties > Java Compiler).

4. Add external libraries
   - If the project depends on external jars in /lib, add them to the build path:
     - Right-click project > Properties > Java Build Path > Libraries > Add JARs... (select from project) or Add External JARs...
   - Alternatively, use a build tool (Maven/Gradle) if available; this repo uses manual lib/ inclusion.

5. Run the application
   - Locate the main class or GUI entry point (for example Testdriver in /src/testing or specific MainWindow classes).
   - Right-click the class > Run As > Java Application.
   - If the program needs DB access, ensure DBHelper settings (host, user, password) point to your local DB and that required tables are present.

Troubleshooting tips:
* If Eclipse doesn't detect an existing project, try: File > Import > Git > Projects from Git > Existing local repository, then Import as Existing Java Project.
* If classpath/module issues appear, check Project > Clean... and then rebuild.
* If you receive missing class errors, confirm all jars in lib are on the project's build path.
