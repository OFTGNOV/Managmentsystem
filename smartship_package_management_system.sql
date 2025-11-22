-- phpMyAdmin SQL Dump
-- version 4.0.4.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 22, 2025 at 05:33 AM
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
-- Table structure for table `clerk`
--

CREATE TABLE IF NOT EXISTS `clerk` (
  `clerkID` varchar(20) NOT NULL,
  `UserID` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`clerkID`),
  UNIQUE KEY `fk_user_id_clk` (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Child Class of User';

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE IF NOT EXISTS `customer` (
  `custID` varchar(300) NOT NULL,
  `address` varchar(250) NOT NULL,
  `zone` int(10) NOT NULL,
  `UserID` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`custID`),
  UNIQUE KEY `UserID` (`UserID`),
  KEY `fk_user_id_cust` (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Child Class of User';

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`custID`, `address`, `zone`, `UserID`) VALUES
('CUST-1763782047588-4560', '9 Blue Way', 3, 7),
('CUST-1763782838167-7528', '102 Trunck Drive', 1, 9),
('CUST-1763786827716-6416', '7 Baltimore Ave', 2, 10);

-- --------------------------------------------------------

--
-- Table structure for table `driver`
--

CREATE TABLE IF NOT EXISTS `driver` (
  `DLN` varchar(20) NOT NULL COMMENT 'Drivers License Numer',
  `UserID` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`DLN`),
  UNIQUE KEY `UserID` (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Child Class of User';

-- --------------------------------------------------------

--
-- Table structure for table `invoice`
--

CREATE TABLE IF NOT EXISTS `invoice` (
  `invoiceNum` varchar(50) NOT NULL,
  `shipment_trackingNumber` varchar(50) NOT NULL,
  `senderId` varchar(50) NOT NULL,
  `recipentId` varchar(50) NOT NULL,
  `totalAmount` double(10,2) NOT NULL,
  `issueDate` datetime NOT NULL,
  `dueDate` datetime NOT NULL,
  `status` enum('PENDING','PAID','PARTIAL','OVERDUE','CANCELLED') NOT NULL,
  `notes` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`invoiceNum`),
  UNIQUE KEY `shipmentTackingNumber` (`shipment_trackingNumber`),
  UNIQUE KEY `senderId` (`senderId`),
  UNIQUE KEY `recipentId` (`recipentId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `invoice`
--

INSERT INTO `invoice` (`invoiceNum`, `shipment_trackingNumber`, `senderId`, `recipentId`, `totalAmount`, `issueDate`, `dueDate`, `status`, `notes`, `created_at`, `updated_at`) VALUES
('INV-1763787592473-8557', 'TRK4984ST', 'CUST-1763786827716-6416', 'CUST-1763782047588-4560', 28.84, '2025-11-21 23:59:52', '2025-12-21 23:59:52', 'PENDING', 'Invoice for shipment: TRK4984ST', '2025-11-22 04:59:52', '2025-11-22 04:59:52');

-- --------------------------------------------------------

--
-- Table structure for table `manager`
--

CREATE TABLE IF NOT EXISTS `manager` (
  `mngID` varchar(20) NOT NULL,
  `UserID` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`mngID`),
  UNIQUE KEY `UserID` (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Child Class of User';

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
  `invoiceNum` varchar(50) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`paymentId`),
  UNIQUE KEY `invoiceNum` (`invoiceNum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `route`
--

CREATE TABLE IF NOT EXISTS `route` (
  `routeNum` varchar(20) NOT NULL,
  `VehiclePlateNum` varchar(20) NOT NULL,
  `zone` int(2) NOT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  PRIMARY KEY (`routeNum`),
  UNIQUE KEY `vehicleId` (`VehiclePlateNum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `route_shipments`
--

CREATE TABLE IF NOT EXISTS `route_shipments` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `routeNum` varchar(20) NOT NULL,
  `shipmentTrackingNumber` varchar(20) NOT NULL,
  `assignedDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `routeNum` (`routeNum`),
  UNIQUE KEY `shipmentTackingNumber` (`shipmentTrackingNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT=' -- LINKING TABLE: Route-Shipments (for shipments planned for routes)' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `shipment`
--

CREATE TABLE IF NOT EXISTS `shipment` (
  `trackingNumber` varchar(30) NOT NULL,
  `senderId` varchar(30) NOT NULL,
  `recipentId` varchar(30) NOT NULL,
  `weight` double(3,2) NOT NULL COMMENT 'kg',
  `length` double(10,2) NOT NULL COMMENT 'cm',
  `width` double(10,2) NOT NULL COMMENT 'cm',
  `height` double(4,2) NOT NULL COMMENT 'cm',
  `PackageType` enum('STANDARD','EXPRESS','FRAGILE','') NOT NULL,
  `ShipmentType` enum('PENDING','ASSIGNED','IN_TRANSIT','DELIVERED','CANCELLED') NOT NULL,
  `shippingCost` double(10,2) NOT NULL,
  `createdDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deliveredDate` datetime DEFAULT NULL,
  PRIMARY KEY (`trackingNumber`),
  UNIQUE KEY `senderId` (`senderId`),
  UNIQUE KEY `recipentId` (`recipentId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Links back to Shipment.java';

--
-- Dumping data for table `shipment`
--

INSERT INTO `shipment` (`trackingNumber`, `senderId`, `recipentId`, `weight`, `length`, `width`, `height`, `PackageType`, `ShipmentType`, `shippingCost`, `createdDate`, `deliveredDate`) VALUES
('TRK4984ST', 'CUST-1763786827716-6416', 'CUST-1763782047588-4560', 2.00, 12.00, 12.00, 12.00, 'EXPRESS', 'PENDING', 28.84, '2025-11-21 23:59:49', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `ID` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `Fname` varchar(20) NOT NULL,
  `Lname` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(250) NOT NULL,
  `salt` varchar(250) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COMMENT='Main Parent Class for users. To be used with user.java' AUTO_INCREMENT=11 ;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`ID`, `Fname`, `Lname`, `email`, `password`, `salt`) VALUES
(4, 'Tamai', 'Richards', 'tamai100009@gmail.com', 'chARBk+Yk85U3Cjv25IJ/3d2bV3Q9beufQcoojRynQA=', 'rikkofPSBHnKXQ50VMyzjw=='),
(5, 'John', 'Staymos', 'johnstay@gmail.com', 'mzK/Bq3LyzedWEjcBa7lPPypl60cnADyZDpOVlkXZMg=', 'tlNShjBu3TmJXxUTLTLRsQ=='),
(7, 'Yellow', 'Man', 'yellowman@gmail.com', 'L2gcLY88UmcbSyuiFBBhfphS06+6l0C8+ATDXOoEm0c=', 'eqEt69YNQ0lsUTvvw2GFtg=='),
(8, 'Blue', 'Johnson', 'bjohson@gmail.com', 'oowyk9PX9/tpo3ic20YPlPk/+oUGjr6ZmnVDIsSuzPk=', 'wRy4LQLwVs5k8WKq6fpmlQ=='),
(9, 'Steven', 'Young', 'styoung@gmail.com', 'lJhZuRi/oIaDuiFlG3AnUgZBLWN3RKe1pzA5jcfjSy0=', 'UKe6MsQced4VRabLpsqoAw=='),
(10, 'Green', 'Guy', 'greenguy@gmail.com', 'A4z6bcibGOvfj/zjjf8KKkg5OC7r+iTKiz8ZR89w7tA=', 'sGQ2DMSZETK943EAk6Snqg==');

-- --------------------------------------------------------

--
-- Table structure for table `vehicle`
--

CREATE TABLE IF NOT EXISTS `vehicle` (
  `licensePlate` varchar(20) NOT NULL,
  `vehicleType` varchar(20) NOT NULL,
  `maxWeightCapacity` decimal(10,2) NOT NULL,
  `maxPackageCapacity` int(11) NOT NULL,
  `currentWeight` double(10,2) NOT NULL DEFAULT '0.00',
  `currentPackageCount` int(11) NOT NULL DEFAULT '0',
  `isAvailable` tinyint(1) NOT NULL DEFAULT '1',
  `driversLicense` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`licensePlate`),
  UNIQUE KEY `driversLicense` (`driversLicense`)
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
-- Constraints for table `clerk`
--
ALTER TABLE `clerk`
  ADD CONSTRAINT `fk_user_id_clk` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `customer`
--
ALTER TABLE `customer`
  ADD CONSTRAINT `fk_user_id_cust` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `driver`
--
ALTER TABLE `driver`
  ADD CONSTRAINT `fk_user_id_drv` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `invoice`
--
ALTER TABLE `invoice`
  ADD CONSTRAINT `fk_invoice_recipentId` FOREIGN KEY (`recipentId`) REFERENCES `customer` (`custID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_invoice_senderId` FOREIGN KEY (`senderID`) REFERENCES `customer` (`custID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_invoice_shipmentTrackingNum` FOREIGN KEY (`shipment_trackingNumber`) REFERENCES `shipment` (`trackingNumber`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `manager`
--
ALTER TABLE `manager`
  ADD CONSTRAINT `fk_user_id_mng` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `fk_payment_invoiceNum` FOREIGN KEY (`invoiceNum`) REFERENCES `invoice` (`invoiceNum`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `route`
--
ALTER TABLE `route`
  ADD CONSTRAINT `fk_vehiclePlateNum` FOREIGN KEY (`VehiclePlateNum`) REFERENCES `vehicle` (`licensePlate`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `route_shipments`
--
ALTER TABLE `route_shipments`
  ADD CONSTRAINT `fk_routeShipment_trackingNum` FOREIGN KEY (`shipmentTrackingNumber`) REFERENCES `shipment` (`trackingNumber`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_routeShipments_routeNum` FOREIGN KEY (`routeNum`) REFERENCES `route` (`routeNum`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `shipment`
--
ALTER TABLE `shipment`
  ADD CONSTRAINT `fk_cust_id_recipentId` FOREIGN KEY (`recipentId`) REFERENCES `customer` (`custID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cust_id_senderId` FOREIGN KEY (`senderId`) REFERENCES `customer` (`custID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `vehicle`
--
ALTER TABLE `vehicle`
  ADD CONSTRAINT `fk_driversLicense` FOREIGN KEY (`driversLicense`) REFERENCES `driver` (`DLN`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `vehicle_shipments`
--
ALTER TABLE `vehicle_shipments`
  ADD CONSTRAINT `fk_vehicleShipment_trackingNum` FOREIGN KEY (`shipmentTrackingNumber`) REFERENCES `shipment` (`trackingNumber`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_vehicleShipment_plateNum` FOREIGN KEY (`licensePlate`) REFERENCES `vehicle` (`licensePlate`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
