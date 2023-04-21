CREATE DATABASE  IF NOT EXISTS `shop` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `shop`;
-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: shop
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `pricerange`
--

DROP TABLE IF EXISTS `pricerange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pricerange` (
  `SupplierId` int NOT NULL,
  `MinQuantity` int NOT NULL,
  `ShippingPrice` double NOT NULL,
  PRIMARY KEY (`SupplierId`,`MinQuantity`),
  CONSTRAINT `pricerange_chk_1` CHECK ((`MinQuantity` >= 0)),
  CONSTRAINT `pricerange_chk_2` CHECK ((`ShippingPrice` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pricerange`
--

LOCK TABLES `pricerange` WRITE;
/*!40000 ALTER TABLE `pricerange` DISABLE KEYS */;
/*!40000 ALTER TABLE `pricerange` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Description` varchar(1023) DEFAULT NULL,
  `Category` varchar(255) NOT NULL,
  `PhotoPath` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'MAC Eyeshadows','A palette of neutral tones eyeshadows branded MAC','Makeup','eyeshadow1.jpg'),(2,'Essence Sparkling Pink Eyeshadow','A sparkling pink eyeshadow branded Essence','Makeup','eyeshadow2.jpg'),(3,'MAC Red Lipstick','A red lipstick branded MAC','Makeup','lipstick1.jpg'),(4,'MAC Pink Lipstick','A pink lipstick branded MAC','Makeup','lipstick2.jpg'),(5,'Nike Sports Shoes','A pair of black, white and grey unisex sports shoes branded Nike','Clothing','shoes1.jpg'),(6,'Armani Exchange Sports Shoes','A pair of white, grey and pink sports shoes for women branded Armani Exchange','Clothing','shoes2.jpg'),(7,'Black Coat','A black coat for men','Clothing','coat1.jpg'),(8,'Black Heels','A pair of black elegant heels','Clothing','shoes3.jpg'),(9,'Brown Coat','An elegant brown coat for women','Clothing','coat2.jpg'),(10,'Brown Lamp','A brown lamp','House','lamp1.jpg'),(11,'Desk Lamp','A white desk lamp','House','lamp2.jpg'),(12,'Design Lamp','A white decorated lamp','House','lamp3.jpg'),(13,'Crystal Vase','A colorful hand decorated vase from Murano','House','vase1.jpg'),(14,'Design Blue Vase','A design blue glass vase','House','vase2.jpg');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_supplier`
--

DROP TABLE IF EXISTS `product_supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_supplier` (
  `ProductId` int NOT NULL,
  `SupplierId` int NOT NULL,
  `Price` double NOT NULL,
  PRIMARY KEY (`ProductId`,`SupplierId`),
  CONSTRAINT `product_supplier_chk_1` CHECK ((`Price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_supplier`
--

LOCK TABLES `product_supplier` WRITE;
/*!40000 ALTER TABLE `product_supplier` DISABLE KEYS */;
INSERT INTO `product_supplier` VALUES (1,1,15),(1,2,20),(2,1,3),(2,2,5),(3,1,10),(3,2,12),(4,1,10),(4,2,12),(5,3,110),(5,4,100),(6,3,130),(6,4,140),(6,5,110),(7,6,130),(7,7,90),(8,5,50),(8,6,45),(9,6,130),(9,7,90),(10,8,70),(10,9,50),(11,9,30),(11,10,40),(12,8,100),(12,10,110),(13,8,180),(13,9,120),(14,8,140),(14,9,110),(14,10,150);
/*!40000 ALTER TABLE `product_supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase`
--

DROP TABLE IF EXISTS `purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Total` double NOT NULL,
  `Date` date NOT NULL,
  `SupplierId` int DEFAULT NULL,
  `UserEmail` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  CONSTRAINT `purchase_chk_1` CHECK ((`Total` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase`
--

LOCK TABLES `purchase` WRITE;
/*!40000 ALTER TABLE `purchase` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_product`
--

DROP TABLE IF EXISTS `purchase_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_product` (
  `ProductId` int NOT NULL,
  `PurchaseId` int NOT NULL,
  `Quantity` int DEFAULT '1',
  PRIMARY KEY (`ProductId`,`PurchaseId`),
  CONSTRAINT `purchase_product_chk_1` CHECK ((`Quantity` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_product`
--

LOCK TABLES `purchase_product` WRITE;
/*!40000 ALTER TABLE `purchase_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchase_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `state`
--

DROP TABLE IF EXISTS `state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `state` (
  `Iso2` char(2) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Iso3` char(3) NOT NULL,
  PRIMARY KEY (`Iso3`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
INSERT INTO `state` VALUES ('AW','Aruba','ABW'),('AF','Afghanistan','AFG'),('AO','Angola','AGO'),('AI','Anguilla','AIA'),('AX','Aland Islands','ALA'),('AL','Albania','ALB'),('AD','Andorra','AND'),('AN','Netherlands Antilles','ANT'),('AE','United Arab Emirates','ARE'),('AR','Argentina','ARG'),('AM','Armenia','ARM'),('AS','American Samoa','ASM'),('AQ','Antarctica','ATA'),('TF','French Southern Territories','ATF'),('AG','Antigua and Barbuda','ATG'),('AU','Australia','AUS'),('AT','Austria','AUT'),('AZ','Azerbaijan','AZE'),('BI','Burundi','BDI'),('BE','Belgium','BEL'),('BJ','Benin','BEN'),('BQ','Bonaire, Sint Eustatius and Saba','BES'),('BF','Burkina Faso','BFA'),('BD','Bangladesh','BGD'),('BG','Bulgaria','BGR'),('BH','Bahrain','BHR'),('BS','Bahamas','BHS'),('BA','Bosnia and Herzegovina','BIH'),('BL','Saint Barthelemy','BLM'),('BY','Belarus','BLR'),('BZ','Belize','BLZ'),('BM','Bermuda','BMU'),('BO','Bolivia','BOL'),('BR','Brazil','BRA'),('BB','Barbados','BRB'),('BN','Brunei Darussalam','BRN'),('BT','Bhutan','BTN'),('BV','Bouvet Island','BVT'),('BW','Botswana','BWA'),('CF','Central African Republic','CAF'),('CA','Canada','CAN'),('CC','Cocos Islands','CCK'),('CH','Switzerland','CHE'),('CL','Chile','CHL'),('CN','China','CHN'),('CI','Cote D\'Ivoire','CIV'),('CM','Cameroon','CMR'),('CD','Democratic Republic of the Congo','COD'),('CG','Congo','COG'),('CK','Cook Islands','COK'),('CO','Colombia','COL'),('KM','Comoros','COM'),('CV','Cape Verde','CPV'),('CR','Costa Rica','CRI'),('CU','Cuba','CUB'),('CW','Curacao','CUW'),('CX','Christmas Island','CXR'),('KY','Cayman Islands','CYM'),('CY','Cyprus','CYP'),('CZ','Czech Republic','CZE'),('DE','Germany','DEU'),('DJ','Djibouti','DJI'),('DM','Dominica','DMA'),('DK','Denmark','DNK'),('DO','Dominican Republic','DOM'),('DZ','Algeria','DZA'),('EC','Ecuador','ECU'),('EG','Egypt','EGY'),('ER','Eritrea','ERI'),('EH','Western Sahara','ESH'),('ES','Spain','ESP'),('EE','Estonia','EST'),('ET','Ethiopia','ETH'),('FI','Finland','FIN'),('FJ','Fiji','FJI'),('FK','Falkland Islands','FLK'),('FR','France','FRA'),('FO','Faroe Islands','FRO'),('FM','Micronesia','FSM'),('GA','Gabon','GAB'),('GB','United Kingdom','GBR'),('GE','Georgia','GEO'),('GG','Guernsey','GGY'),('GH','Ghana','GHA'),('GI','Gibraltar','GIB'),('GN','Guinea','GIN'),('GP','Guadeloupe','GLP'),('GM','Gambia','GMB'),('GW','Guinea-Bissau','GNB'),('GQ','Equatorial Guinea','GNQ'),('GR','Greece','GRC'),('GD','Grenada','GRD'),('GL','Greenland','GRL'),('GT','Guatemala','GTM'),('GF','French Guiana','GUF'),('GU','Guam','GUM'),('GY','Guyana','GUY'),('HK','Hong Kong','HKG'),('HM','Heard Island and Mcdonald Islands','HMD'),('HN','Honduras','HND'),('HR','Croatia','HRV'),('HT','Haiti','HTI'),('HU','Hungary','HUN'),('ID','Indonesia','IDN'),('IM','Isle of Man','IMN'),('IN','India','IND'),('IO','British Indian Ocean Territory','IOT'),('IE','Ireland','IRL'),('IR','Iran','IRN'),('IQ','Iraq','IRQ'),('IS','Iceland','ISL'),('IL','Israel','ISR'),('IT','Italy','ITA'),('JM','Jamaica','JAM'),('JE','Jersey','JEY'),('JO','Jordan','JOR'),('JP','Japan','JPN'),('KZ','Kazakhstan','KAZ'),('KE','Kenya','KEN'),('KG','Kyrgyzstan','KGZ'),('KH','Cambodia','KHM'),('KI','Kiribati','KIR'),('KN','Saint Kitts and Nevis','KNA'),('KR','South Korea','KOR'),('KW','Kuwait','KWT'),('LA','Lao People\'s Democratic Republic','LAO'),('LB','Lebanon','LBN'),('LR','Liberia','LBR'),('LY','Libyan Arab Jamahiriya','LBY'),('LC','Saint Lucia','LCA'),('LI','Liechtenstein','LIE'),('LK','Sri Lanka','LKA'),('LS','Lesotho','LSO'),('LT','Lithuania','LTU'),('LU','Luxembourg','LUX'),('LV','Latvia','LVA'),('MO','Macao','MAC'),('MF','Saint Martin','MAF'),('MA','Morocco','MAR'),('MC','Monaco','MCO'),('MD','Moldova, Republic of','MDA'),('MG','Madagascar','MDG'),('MV','Maldives','MDV'),('MX','Mexico','MEX'),('MH','Marshall Islands','MHL'),('MK','Macedonia','MKD'),('ML','Mali','MLI'),('MT','Malta','MLT'),('MM','Myanmar','MMR'),('ME','Montenegro','MNE'),('MN','Mongolia','MNG'),('MP','Northern Mariana Islands','MNP'),('MZ','Mozambique','MOZ'),('MR','Mauritania','MRT'),('MS','Montserrat','MSR'),('MQ','Martinique','MTQ'),('MU','Mauritius','MUS'),('MW','Malawi','MWI'),('MY','Malaysia','MYS'),('YT','Mayotte','MYT'),('NA','Namibia','NAM'),('NC','New Caledonia','NCL'),('NE','Niger','NER'),('NF','Norfolk Island','NFK'),('NG','Nigeria','NGA'),('NI','Nicaragua','NIC'),('NU','Niue','NIU'),('NL','Netherlands','NLD'),('NO','Norway','NOR'),('NP','Nepal','NPL'),('NR','Nauru','NRU'),('NZ','New Zealand','NZL'),('OM','Oman','OMN'),('PK','Pakistan','PAK'),('PA','Panama','PAN'),('PN','Pitcairn','PCN'),('PE','Peru','PER'),('PH','Philippines','PHL'),('PW','Palau','PLW'),('PG','Papua New Guinea','PNG'),('PL','Poland','POL'),('PR','Puerto Rico','PRI'),('KP','Norht Korea','PRK'),('PT','Portugal','PRT'),('PY','Paraguay','PRY'),('PS','Palestina','PSE'),('PF','French Polynesia','PYF'),('QA','Qatar','QAT'),('RE','Reunion','REU'),('RO','Romania','ROM'),('RU','Russia','RUS'),('RW','Rwanda','RWA'),('SA','Saudi Arabia','SAU'),('CS','Serbia and Montenegro','SCG'),('SD','Sudan','SDN'),('SN','Senegal','SEN'),('SG','Singapore','SGP'),('GS','South Georgia and the South Sandwich Islands','SGS'),('SH','Saint Helena','SHN'),('SJ','Svalbard and Jan Mayen','SJM'),('SB','Solomon Islands','SLB'),('SL','Sierra Leone','SLE'),('SV','El Salvador','SLV'),('SM','San Marino','SMR'),('SO','Somalia','SOM'),('PM','Saint Pierre and Miquelon','SPM'),('RS','Serbia','SRB'),('SS','South Sudan','SSD'),('ST','Sao Tome and Principe','STP'),('SR','Suriname','SUR'),('SK','Slovakia','SVK'),('SI','Slovenia','SVN'),('SE','Sweden','SWE'),('SZ','Swaziland','SWZ'),('SX','Sint Maarten','SXM'),('SC','Seychelles','SYC'),('SY','Syrian Arab Republic','SYR'),('TC','Turks and Caicos Islands','TCA'),('TD','Chad','TCD'),('TG','Togo','TGO'),('TH','Thailand','THA'),('TJ','Tajikistan','TJK'),('TK','Tokelau','TKL'),('TM','Turkmenistan','TKM'),('TL','Timor-Leste','TLS'),('TO','Tonga','TON'),('TT','Trinidad and Tobago','TTO'),('TN','Tunisia','TUN'),('TR','Turkey','TUR'),('TV','Tuvalu','TUV'),('TW','Taiwan','TWN'),('TZ','Tanzania','TZA'),('UG','Uganda','UGA'),('UA','Ukraine','UKR'),('UM','United States Minor Outlying Islands','UMI'),('UY','Uruguay','URY'),('US','United States','USA'),('UZ','Uzbekistan','UZB'),('VA','Vatican City','VAT'),('VC','Saint Vincent and the Grenadines','VCT'),('VE','Venezuela','VEN'),('VG','Virgin Islands, British','VGB'),('VI','Virgin Islands, U.S.','VIR'),('VN','Viet Nam','VNM'),('VU','Vanuatu','VUT'),('WF','Wallis and Futuna','WLF'),('WS','Samoa','WSM'),('XK','Kosovo','XKX'),('YE','Yemen','YEM'),('ZA','South Africa','ZAF'),('ZM','Zambia','ZMB'),('ZW','Zimbabwe','ZWE');
/*!40000 ALTER TABLE `state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Rating` int DEFAULT NULL,
  `FreeShippingThreshold` double DEFAULT NULL,
  PRIMARY KEY (`Id`),
  CONSTRAINT `supplier_chk_1` CHECK ((((`Rating` >= 1) and (`Rating` <= 5)) or (`Rating` is null))),
  CONSTRAINT `supplier_chk_2` CHECK (((`FreeShippingThreshold` >= 0) or (`FreeShippingThreshold` is null)))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` VALUES (1,'Makeup Forever',4,30),(2,'Sephora',5,50),(3,'AW Lab',3,150),(4,'FootLocker',4,170),(5,'PittaRed',4,120),(6,'Zalando',5,200),(7,'Clothes For Everyone',2,100),(8,'IKEA',5,300),(9,'FeelAtHome',3,150),(10,'Maisons Du Monde',4,200);
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `Email` varchar(255) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Surname` varchar(255) NOT NULL,
  `StateIso3` char(3) DEFAULT NULL,
  `City` varchar(255) NOT NULL,
  `Street` varchar(255) NOT NULL,
  `CivicNumber` varchar(7) NOT NULL,
  `PasswordHash` binary(32) NOT NULL,
  `Salt` int NOT NULL,
  PRIMARY KEY (`Email`),
  CONSTRAINT `user_chk_1` CHECK ((`Email` like _utf8mb4'%@%.%'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('alessandro.annechini@mail.polimi.it','Alessandro','Annechini','ITA','Milano','Via Mario Rossi','1C',_binary 'À¼’@nÀ\n$O8ð^P#ë¹œ°(\ä6E\çWü\ë[U',649),('nicole.filippi@mail.polimi.it','Nicole','Filippi','ITA','Milano','Via Luigi Verdi','23',_binary '\éÁ9§L„=\nŠ>^)$]\èx´D·c\ç«Jh0œY\Ü~',137);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_product`
--

DROP TABLE IF EXISTS `user_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_product` (
  `UserEmail` varchar(255) NOT NULL,
  `ProductId` int NOT NULL,
  `TimeStamp` timestamp NOT NULL,
  PRIMARY KEY (`UserEmail`,`ProductId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_product`
--

LOCK TABLES `user_product` WRITE;
/*!40000 ALTER TABLE `user_product` DISABLE KEYS */;
INSERT INTO `user_product` VALUES ('alessandro.annechini@mail.polimi.it',1,'2023-04-21 15:49:43'),('alessandro.annechini@mail.polimi.it',2,'2023-04-21 15:49:44');
/*!40000 ALTER TABLE `user_product` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-04-21 17:50:59
