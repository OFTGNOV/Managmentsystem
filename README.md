# **SmartShip Package Management System**

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
