-- phpMyAdmin SQL Dump
-- version 4.0.4.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 27, 2025 at 09:57 PM
-- Server version: 5.6.13
-- PHP Version: 5.4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `smartship_package_management_system`
--
CREATE DATABASE IF NOT EXISTS `smartship_package_management_system` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `smartship_package_management_system`;

-- --------------------------------------------------------

--
-- Table structure for table `invoice`
--

CREATE TABLE IF NOT EXISTS `invoice` (
  `invoiceID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `shipment_trackingNumber` varchar(50) NOT NULL,
  `senderId` int(10) unsigned NOT NULL,
  `recipentId` int(10) unsigned NOT NULL,
  `totalAmount` double(10,2) NOT NULL,
  `issueDate` datetime NOT NULL,
  `dueDate` datetime NOT NULL,
  `status` enum('PENDING','PAID','PARTIAL','OVERDUE','CANCELLED') NOT NULL,
  `notes` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`invoiceID`),
  UNIQUE KEY `shipmentTackingNumber` (`shipment_trackingNumber`),
  KEY `senderId` (`senderId`),
  KEY `recipentId` (`recipentId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE IF NOT EXISTS `payment` (
  `paymentId` int(11) NOT NULL AUTO_INCREMENT,
  `amount` double(10,2) NOT NULL,
  `paymentDate` datetime NOT NULL,
  `paymentMethod` enum('CASH','CARD') NOT NULL,
  `status` enum('SUCCESS','FAILED','PENDING','CANCELLED','REFUNDED') NOT NULL DEFAULT 'PENDING',
  `referenceNumber` varchar(100) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invoiceID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`paymentId`),
  UNIQUE KEY `invoiceNum` (`invoiceID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `route`
--

CREATE TABLE IF NOT EXISTS `route` (
  `routeID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `VehiclePlateNum` varchar(20) NOT NULL,
  `zone` int(2) NOT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  PRIMARY KEY (`routeID`),
  UNIQUE KEY `vehicleId` (`VehiclePlateNum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `route_shipments`
--

CREATE TABLE IF NOT EXISTS `route_shipments` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `routeID` int(10) unsigned NOT NULL,
  `shipmentTrackingNumber` varchar(20) NOT NULL,
  `assignedDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `routeNum` (`routeID`),
  UNIQUE KEY `shipmentTackingNumber` (`shipmentTrackingNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT=' -- LINKING TABLE: Route-Shipments (for shipments planned for routes)' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `shipment`
--

CREATE TABLE IF NOT EXISTS `shipment` (
  `trackingNumber` varchar(30) NOT NULL,
  `senderId` int(10) unsigned NOT NULL,
  `recipentId` int(10) unsigned NOT NULL,
  `weight` double(10,2) NOT NULL COMMENT 'kg',
  `length` double(10,2) NOT NULL COMMENT 'cm',
  `width` double(10,2) NOT NULL COMMENT 'cm',
  `height` double(10,2) NOT NULL COMMENT 'cm',
  `PackageType` enum('STANDARD','EXPRESS','FRAGILE','') NOT NULL,
  `ShipmentType` enum('PENDING','ASSIGNED','IN_TRANSIT','DELIVERED','CANCELLED') NOT NULL,
  `shippingCost` double(10,2) NOT NULL,
  `createdDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deliveredDate` datetime DEFAULT NULL,
  PRIMARY KEY (`trackingNumber`),
  KEY `senderId` (`senderId`),
  KEY `recipentId` (`recipentId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Links back to Shipment.java';

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Fname` varchar(20) NOT NULL,
  `Lname` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `userType` enum('MANAGE','CLERK','CUSTOMER','DRIVER') NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `zone` int(5) DEFAULT NULL,
  `password` varchar(250) NOT NULL,
  `salt` varchar(250) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COMMENT='Main Parent Class for users. To be used with user.java' AUTO_INCREMENT=11 ;

-- --------------------------------------------------------

--
-- Table structure for table `vehicle`
--

CREATE TABLE IF NOT EXISTS `vehicle` (
  `licensePlate` varchar(20) NOT NULL,
  `driverID` int(10) unsigned DEFAULT NULL,
  `vehicleType` varchar(20) NOT NULL,
  `maxWeightCapacity` decimal(10,2) NOT NULL,
  `maxPackageCapacity` int(11) NOT NULL,
  `currentWeight` double(10,2) NOT NULL DEFAULT '0.00',
  `currentPackageCount` int(11) NOT NULL DEFAULT '0',
  `isAvailable` tinyint(1) NOT NULL,
  PRIMARY KEY (`licensePlate`),
  UNIQUE KEY `driverID` (`driverID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vehicle_shipments`
--

CREATE TABLE IF NOT EXISTS `vehicle_shipments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `licensePlate` varchar(20) NOT NULL,
  `shipmentTrackingNumber` varchar(50) NOT NULL,
  `assignedDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `licensePlate` (`licensePlate`),
  UNIQUE KEY `shipmentTackingNumber` (`shipmentTrackingNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `invoice`
--
ALTER TABLE `invoice`
  ADD CONSTRAINT `fk_rID_invoice` FOREIGN KEY (`recipentId`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_sID_invoice` FOREIGN KEY (`senderId`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_trackingNum_invoice` FOREIGN KEY (`shipment_trackingNumber`) REFERENCES `shipment` (`trackingNumber`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `fk_iNum_payment` FOREIGN KEY (`invoiceID`) REFERENCES `invoice` (`invoiceID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `route`
--
ALTER TABLE `route`
  ADD CONSTRAINT `fk_lPlate_route` FOREIGN KEY (`VehiclePlateNum`) REFERENCES `vehicle_shipments` (`licensePlate`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `route_shipments`
--
ALTER TABLE `route_shipments`
  ADD CONSTRAINT `fk_trackingNum_routeShipments` FOREIGN KEY (`shipmentTrackingNumber`) REFERENCES `shipment` (`trackingNumber`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rID_routeShipments` FOREIGN KEY (`routeID`) REFERENCES `route` (`routeID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `shipment`
--
ALTER TABLE `shipment`
  ADD CONSTRAINT `fk_rID_shipment` FOREIGN KEY (`recipentId`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_sID_shipment` FOREIGN KEY (`senderId`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `vehicle`
--
ALTER TABLE `vehicle`
  ADD CONSTRAINT `fk_dID_vehicle` FOREIGN KEY (`driverID`) REFERENCES `user` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `vehicle_shipments`
--
ALTER TABLE `vehicle_shipments`
  ADD CONSTRAINT `fk_trackingNum_vehicleShipment` FOREIGN KEY (`shipmentTrackingNumber`) REFERENCES `shipment` (`trackingNumber`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_plateNum_vehicleShipment` FOREIGN KEY (`licensePlate`) REFERENCES `vehicle` (`licensePlate`) ON DELETE NO ACTION ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
