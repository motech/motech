-- MySQL dump 10.13  Distrib 5.5.43, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: motech_data_services
-- ------------------------------------------------------
-- Server version	5.5.43-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Entity`
--

DROP TABLE IF EXISTS `Entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Entity` (
  `id` bigint(20) NOT NULL,
  `className` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `entityVersion` bigint(20) DEFAULT NULL,
  `module` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `namespace` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `DISCRIMINATOR` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `changesMade` bit(1) DEFAULT NULL,
  `draftOwnerUsername` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lastModificationDate` datetime DEFAULT NULL,
  `parentEntity_id_OID` bigint(20) DEFAULT NULL,
  `parentVersion` bigint(20) DEFAULT NULL,
  `drafts_INTEGER_IDX` int(11) DEFAULT NULL,
  `securityMode` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `tableName` varchar(255) DEFAULT NULL,
  `maxFetchDepth` bigint(20) DEFAULT NULL,
  `securityOptionsModified` bit(1) NOT NULL DEFAULT b'0',
  `superClass` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `abstractClass` bit(1) NOT NULL,
  `fieldNameChanges` mediumblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `DRAFT_USER_IDX` (`parentEntity_id_OID`,`draftOwnerUsername`),
  KEY `Entity_N49` (`parentEntity_id_OID`),
  CONSTRAINT `Entity_FK1` FOREIGN KEY (`parentEntity_id_OID`) REFERENCES `Entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Entity`
--

LOCK TABLES `Entity` WRITE;
/*!40000 ALTER TABLE `Entity` DISABLE KEYS */;
INSERT INTO `Entity` VALUES (1,'org.motechproject.server.config.domain.SettingsRecord',1,'MOTECH Platform Server Config','SettingsRecord','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(2,'org.motechproject.config.domain.ModulePropertiesRecord',1,'MOTECH Platform Server Config','ModulePropertiesRecord','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(3,'org.motechproject.security.domain.MotechPermission',1,'MOTECH Web Security','MotechPermission','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(4,'org.motechproject.security.domain.MotechRole',23,'MOTECH Web Security','MotechRole','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(5,'org.motechproject.security.domain.MotechURLSecurityRule',1,'MOTECH Web Security','MotechURLSecurityRule','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(6,'org.motechproject.security.domain.MotechUser',2,'MOTECH Web Security','MotechUser','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(7,'org.motechproject.security.domain.PasswordRecovery',1,'MOTECH Web Security','PasswordRecovery','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(8,'org.motechproject.tasks.domain.TaskEventInformation',1,'MOTECH Tasks','TaskEventInformation','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','',NULL),(9,'org.motechproject.tasks.domain.ActionParameter',1,'MOTECH Tasks','ActionParameter','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.Parameter','\0',NULL),(10,'org.motechproject.tasks.domain.Lookup',1,'MOTECH Tasks','Lookup','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(11,'org.motechproject.tasks.domain.EventParameter',1,'MOTECH Tasks','EventParameter','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.Parameter','\0',NULL),(12,'org.motechproject.tasks.domain.FieldParameter',1,'MOTECH Tasks','FieldParameter','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.Parameter','\0',NULL),(13,'org.motechproject.tasks.domain.FilterSet',1,'MOTECH Tasks','FilterSet','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.TaskConfigStep','\0',NULL),(14,'org.motechproject.tasks.domain.Parameter',1,'MOTECH Tasks','Parameter','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','',NULL),(15,'org.motechproject.tasks.domain.TaskConfigStep',1,'MOTECH Tasks','TaskConfigStep','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','',NULL),(16,'org.motechproject.tasks.domain.ActionEvent',1,'MOTECH Tasks','ActionEvent','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.TaskEvent','\0',NULL),(17,'org.motechproject.tasks.domain.TaskConfig',1,'MOTECH Tasks','TaskConfig','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(18,'org.motechproject.tasks.domain.TaskActivity',1,'MOTECH Tasks','TaskActivity','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(19,'org.motechproject.tasks.domain.TaskEvent',1,'MOTECH Tasks','TaskEvent','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','',NULL),(20,'org.motechproject.tasks.domain.DataSource',1,'MOTECH Tasks','DataSource','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.TaskConfigStep','\0',NULL),(21,'org.motechproject.tasks.domain.LookupFieldsParameter',1,'MOTECH Tasks','LookupFieldsParameter','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(22,'org.motechproject.tasks.domain.TriggerEvent',1,'MOTECH Tasks','TriggerEvent','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.TaskEvent','\0',NULL),(23,'org.motechproject.tasks.domain.Channel',1,'MOTECH Tasks','Channel','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(24,'org.motechproject.tasks.domain.TaskError',1,'MOTECH Tasks','TaskError','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(25,'org.motechproject.tasks.domain.Task',1,'MOTECH Tasks','Task','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(26,'org.motechproject.tasks.domain.TaskDataProvider',1,'MOTECH Tasks','TaskDataProvider','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(27,'org.motechproject.tasks.domain.TaskTriggerInformation',1,'MOTECH Tasks','TaskTriggerInformation','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.TaskEventInformation','\0',NULL),(28,'org.motechproject.tasks.domain.Filter',1,'MOTECH Tasks','Filter','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(29,'org.motechproject.tasks.domain.TaskActionInformation',1,'MOTECH Tasks','TaskActionInformation','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','org.motechproject.tasks.domain.TaskEventInformation','\0',NULL),(30,'org.motechproject.tasks.domain.TaskDataProviderObject',1,'MOTECH Tasks','TaskDataProviderObject','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(31,'org.motechproject.email.domain.EmailRecord',1,'MOTECH Platform Email','EmailRecord','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(32,'org.motechproject.admin.domain.NotificationRule',1,'MOTECH Admin','NotificationRule','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL),(33,'org.motechproject.admin.domain.StatusMessage',1,'MOTECH Admin','StatusMessage','','org.motechproject.mds.domain.Entity',NULL,NULL,NULL,NULL,NULL,-1,'EVERYONE','',NULL,'\0','java.lang.Object','\0',NULL);
/*!40000 ALTER TABLE `Entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EntityAudit`
--

DROP TABLE IF EXISTS `EntityAudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EntityAudit` (
  `id` bigint(20) NOT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `ownerUsername` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `EntityAudit_FK1` FOREIGN KEY (`id`) REFERENCES `Entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EntityAudit`
--

LOCK TABLES `EntityAudit` WRITE;
/*!40000 ALTER TABLE `EntityAudit` DISABLE KEYS */;
/*!40000 ALTER TABLE `EntityAudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Entity_securityMembers`
--

DROP TABLE IF EXISTS `Entity_securityMembers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Entity_securityMembers` (
  `Entity_OID` bigint(20) NOT NULL,
  `SecurityMember` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`Entity_OID`,`SecurityMember`),
  KEY `Entity_securityMembers_N49` (`Entity_OID`),
  CONSTRAINT `Entity_securityMembers_FK1` FOREIGN KEY (`Entity_OID`) REFERENCES `Entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Entity_securityMembers`
--

LOCK TABLES `Entity_securityMembers` WRITE;
/*!40000 ALTER TABLE `Entity_securityMembers` DISABLE KEYS */;
/*!40000 ALTER TABLE `Entity_securityMembers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Field`
--

DROP TABLE IF EXISTS `Field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Field` (
  `id` bigint(20) NOT NULL,
  `defaultValue` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `entity_id_OID` bigint(20) DEFAULT NULL,
  `exposedViaRest` bit(1) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `required` bit(1) NOT NULL,
  `tooltip` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type_id_OID` bigint(20) DEFAULT NULL,
  `fields_INTEGER_IDX` int(11) DEFAULT NULL,
  `uiDisplayable` bit(1) NOT NULL,
  `uiFilterable` bit(1) NOT NULL,
  `uiDisplayPosition` bigint(20) DEFAULT NULL,
  `readOnly` bit(1) NOT NULL,
  `nonEditable` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ENTITY_FIELDNAME_IDX` (`entity_id_OID`,`name`),
  KEY `Field_N50` (`entity_id_OID`),
  KEY `Field_N49` (`type_id_OID`),
  CONSTRAINT `Field_FK1` FOREIGN KEY (`type_id_OID`) REFERENCES `Type` (`id`),
  CONSTRAINT `Field_FK2` FOREIGN KEY (`entity_id_OID`) REFERENCES `Entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Field`
--

LOCK TABLES `Field` WRITE;
/*!40000 ALTER TABLE `Field` DISABLE KEYS */;
INSERT INTO `Field` VALUES (1,NULL,'Id',1,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(2,NULL,'Created By',1,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(3,NULL,'Owner',1,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(4,NULL,'Modified By',1,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(5,NULL,'Creation Date',1,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(6,NULL,'Modification Date',1,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(7,'false','Platform Initialized',1,'','platformInitialized','','',3,6,'','\0',6,'','\0'),(8,'','Last Run',1,'','lastRun','\0','',6,7,'','\0',7,'','\0'),(9,'','Platform Settings',1,'','platformSettings','\0','',10,8,'','\0',8,'','\0'),(10,'','File Path',1,'','filePath','\0','',2,9,'','\0',9,'','\0'),(11,'','Config File Checksum',1,'','configFileChecksum','\0','',2,10,'','\0',10,'','\0'),(12,NULL,'Id',2,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(13,NULL,'Created By',2,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(14,NULL,'Owner',2,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(15,NULL,'Modified By',2,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(16,NULL,'Creation Date',2,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(17,NULL,'Modification Date',2,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(18,'','Properties',2,'','properties','\0','',10,6,'','\0',6,'','\0'),(19,'false','Raw',2,'','raw','','',3,7,'','\0',7,'','\0'),(20,'','Version',2,'','version','\0','',2,8,'','\0',8,'','\0'),(21,'','Filename',2,'','filename','\0','',2,9,'','\0',9,'','\0'),(22,'','Bundle',2,'','bundle','\0','',2,10,'','\0',10,'','\0'),(23,NULL,'Id',3,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(24,NULL,'Created By',3,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(25,NULL,'Owner',3,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(26,NULL,'Modified By',3,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(27,NULL,'Creation Date',3,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(28,NULL,'Modification Date',3,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(29,NULL,'Bundle Name',3,'','bundleName','\0','',2,6,'','\0',6,'','\0'),(30,NULL,'Permission Name',3,'','permissionName','\0','',2,7,'','\0',7,'','\0'),(31,NULL,'Id',4,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(32,NULL,'Created By',4,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(33,NULL,'Owner',4,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(34,NULL,'Modified By',4,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(35,NULL,'Creation Date',4,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(36,NULL,'Modification Date',4,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(37,NULL,'Permission Names',4,'','permissionNames','\0','',8,6,'','\0',6,'','\0'),(38,NULL,'Role Name',4,'','roleName','\0','',2,7,'','\0',7,'','\0'),(39,'false','Deletable',4,'','deletable','','',3,8,'','\0',8,'','\0'),(40,NULL,'Id',5,'','id','\0','',15,0,'\0','\0',NULL,'','\0'),(41,NULL,'Created By',5,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(42,NULL,'Owner',5,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(43,NULL,'Modified By',5,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(44,NULL,'Creation Date',5,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(45,NULL,'Modification Date',5,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(46,'0','Priority',5,'','priority','','',1,6,'','\0',6,'','\0'),(47,NULL,'Protocol',5,'','protocol','\0','',8,7,'','\0',7,'','\0'),(48,NULL,'User Access',5,'','userAccess','\0','',8,8,'','\0',8,'','\0'),(49,NULL,'Pattern',5,'','pattern','\0','',2,9,'','\0',9,'','\0'),(50,'false','Rest',5,'','rest','','',3,10,'','\0',10,'','\0'),(51,NULL,'Methods Required',5,'','methodsRequired','\0','',8,11,'','\0',11,'','\0'),(52,NULL,'Supported Schemes',5,'','supportedSchemes','\0','',8,12,'','\0',12,'','\0'),(53,'false','Active',5,'','active','','',3,13,'','\0',13,'','\0'),(54,NULL,'Origin',5,'','origin','\0','',2,14,'','\0',14,'','\0'),(55,NULL,'Version',5,'','version','\0','',2,15,'','\0',15,'','\0'),(56,'false','Deleted',5,'','deleted','','',3,16,'','\0',16,'','\0'),(57,NULL,'Permission Access',5,'','permissionAccess','\0','',8,17,'','\0',17,'','\0'),(58,NULL,'Id',6,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(59,NULL,'Created By',6,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(60,NULL,'Owner',6,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(61,NULL,'Modified By',6,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(62,NULL,'Creation Date',6,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(63,NULL,'Modification Date',6,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(64,NULL,'User Name',6,'','userName','\0','',2,6,'','\0',6,'','\0'),(65,NULL,'Roles',6,'','roles','\0','',8,7,'','\0',7,'','\0'),(66,NULL,'Locale',6,'','locale','\0','',12,8,'','\0',8,'','\0'),(67,NULL,'Password',6,'','password','\0','',2,9,'','\0',9,'','\0'),(68,NULL,'Open Id',6,'','openId','\0','',2,10,'','\0',10,'','\0'),(69,NULL,'External Id',6,'','externalId','\0','',2,11,'','\0',11,'','\0'),(70,NULL,'Email',6,'','email','\0','',2,12,'','\0',12,'','\0'),(71,'false','Active',6,'','active','','',3,13,'','\0',13,'','\0'),(72,NULL,'Id',7,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(73,NULL,'Created By',7,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(74,NULL,'Owner',7,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(75,NULL,'Modified By',7,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(76,NULL,'Creation Date',7,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(77,NULL,'Modification Date',7,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(78,NULL,'Username',7,'','username','\0','',2,6,'','\0',6,'','\0'),(79,NULL,'Locale',7,'','locale','\0','',12,7,'','\0',7,'','\0'),(80,NULL,'Email',7,'','email','\0','',2,8,'','\0',8,'','\0'),(81,NULL,'Expiration Date',7,'','expirationDate','\0','',6,9,'','\0',9,'','\0'),(82,NULL,'Token',7,'','token','\0','',2,10,'','\0',10,'','\0'),(83,NULL,'Id',8,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(84,NULL,'Created By',8,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(85,NULL,'Owner',8,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(86,NULL,'Modified By',8,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(87,NULL,'Creation Date',8,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(88,NULL,'Modification Date',8,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(89,NULL,'Subject',8,'','subject','\0','',2,6,'','\0',6,'','\0'),(90,NULL,'Channel Name',8,'','channelName','\0','',2,7,'','\0',7,'','\0'),(91,NULL,'Module Version',8,'','moduleVersion','\0','',2,8,'','\0',8,'','\0'),(92,NULL,'Module Name',8,'','moduleName','\0','',2,9,'','\0',9,'','\0'),(93,NULL,'Name',8,'','name','\0','',2,10,'','\0',10,'','\0'),(94,NULL,'Display Name',8,'','displayName','\0','',2,11,'','\0',11,'','\0'),(95,NULL,'Id',9,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(96,NULL,'Created By',9,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(97,NULL,'Owner',9,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(98,NULL,'Modified By',9,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(99,NULL,'Creation Date',9,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(100,NULL,'Modification Date',9,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(101,'','Order',9,'','order','\0','',1,6,'','\0',6,'','\0'),(102,'','Hidden',9,'','hidden','\0','',3,7,'','\0',7,'','\0'),(103,'','Required',9,'','required','\0','',3,8,'','\0',8,'','\0'),(104,NULL,'Type',9,'','type','\0','',8,9,'','\0',9,'','\0'),(105,'','Value',9,'','value','\0','',2,10,'','\0',10,'','\0'),(106,'','Key',9,'','key','\0','',2,11,'','\0',11,'','\0'),(107,NULL,'Display Name',9,'','displayName','\0','',2,12,'','\0',12,'','\0'),(108,NULL,'Id',10,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(109,NULL,'Created By',10,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(110,NULL,'Owner',10,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(111,NULL,'Modified By',10,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(112,NULL,'Creation Date',10,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(113,NULL,'Modification Date',10,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(114,NULL,'Value',10,'','value','\0','',2,6,'','\0',6,'','\0'),(115,NULL,'Field',10,'','field','\0','',2,7,'','\0',7,'','\0'),(116,NULL,'Id',11,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(117,NULL,'Created By',11,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(118,NULL,'Owner',11,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(119,NULL,'Modified By',11,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(120,NULL,'Creation Date',11,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(121,NULL,'Modification Date',11,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(122,'','Event Key',11,'','eventKey','\0','',2,6,'','\0',6,'','\0'),(123,NULL,'Type',11,'','type','\0','',8,7,'','\0',7,'','\0'),(124,NULL,'Display Name',11,'','displayName','\0','',2,8,'','\0',8,'','\0'),(125,NULL,'Id',12,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(126,NULL,'Created By',12,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(127,NULL,'Owner',12,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(128,NULL,'Modified By',12,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(129,NULL,'Creation Date',12,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(130,NULL,'Modification Date',12,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(131,NULL,'Field Key',12,'','fieldKey','\0','',2,6,'','\0',6,'','\0'),(132,NULL,'Type',12,'','type','\0','',8,7,'','\0',7,'','\0'),(133,NULL,'Display Name',12,'','displayName','\0','',2,8,'','\0',8,'','\0'),(134,NULL,'Id',13,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(135,NULL,'Created By',13,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(136,NULL,'Owner',13,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(137,NULL,'Modified By',13,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(138,NULL,'Creation Date',13,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(139,NULL,'Modification Date',13,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(140,NULL,'Order',13,'','order','\0','',1,6,'','\0',6,'','\0'),(141,'','Operator',13,'','operator','\0','',8,7,'','\0',7,'','\0'),(142,'','Filters',13,'','filters','\0','',17,8,'','\0',8,'','\0'),(143,NULL,'Id',14,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(144,NULL,'Created By',14,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(145,NULL,'Owner',14,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(146,NULL,'Modified By',14,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(147,NULL,'Creation Date',14,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(148,NULL,'Modification Date',14,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(149,NULL,'Type',14,'','type','\0','',8,6,'','\0',6,'','\0'),(150,NULL,'Display Name',14,'','displayName','\0','',2,7,'','\0',7,'','\0'),(151,NULL,'Id',15,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(152,NULL,'Created By',15,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(153,NULL,'Owner',15,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(154,NULL,'Modified By',15,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(155,NULL,'Creation Date',15,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(156,NULL,'Modification Date',15,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(157,NULL,'Order',15,'','order','\0','',1,6,'','\0',6,'','\0'),(158,NULL,'Id',16,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(159,NULL,'Created By',16,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(160,NULL,'Owner',16,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(161,NULL,'Modified By',16,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(162,NULL,'Creation Date',16,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(163,NULL,'Modification Date',16,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(164,NULL,'Subject',16,'','subject','\0','',2,6,'','\0',6,'','\0'),(165,'','Service Method',16,'','serviceMethod','\0','',2,7,'','\0',7,'','\0'),(166,NULL,'Description',16,'','description','\0','',2,8,'','\0',8,'','\0'),(167,'','Service Method Call Manner',16,'','serviceMethodCallManner','\0','',8,9,'','\0',9,'','\0'),(168,'','Service Interface',16,'','serviceInterface','\0','',2,10,'','\0',10,'','\0'),(169,'','Action Parameters',16,'','actionParameters','\0','',17,11,'','\0',11,'','\0'),(170,NULL,'Name',16,'','name','\0','',2,12,'','\0',12,'','\0'),(171,NULL,'Display Name',16,'','displayName','\0','',2,13,'','\0',13,'','\0'),(172,NULL,'Id',17,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(173,NULL,'Created By',17,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(174,NULL,'Owner',17,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(175,NULL,'Modified By',17,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(176,NULL,'Creation Date',17,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(177,NULL,'Modification Date',17,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(178,'','Filters',17,'','filters','\0','',17,6,'','\0',6,'','\0'),(179,'','Data Sources',17,'','dataSources','\0','',17,7,'','\0',7,'','\0'),(180,NULL,'Id',18,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(181,NULL,'Created By',18,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(182,NULL,'Owner',18,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(183,NULL,'Modified By',18,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(184,NULL,'Creation Date',18,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(185,NULL,'Modification Date',18,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(186,'','Fields',18,'','fields','\0','',8,6,'','\0',6,'','\0'),(187,'','Task',18,'','task','\0','',15,7,'','\0',7,'','\0'),(188,'','Activity Type',18,'','activityType','\0','',8,8,'','\0',8,'','\0'),(189,'','StackTrace element',18,'','stackTraceElement','\0','',2,9,'','\0',9,'','\0'),(190,'','Date',18,'','date','\0','',6,10,'','\0',10,'','\0'),(191,'','Message',18,'','message','\0','',2,11,'','\0',11,'','\0'),(192,NULL,'Id',19,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(193,NULL,'Created By',19,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(194,NULL,'Owner',19,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(195,NULL,'Modified By',19,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(196,NULL,'Creation Date',19,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(197,NULL,'Modification Date',19,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(198,NULL,'Subject',19,'','subject','\0','',2,6,'','\0',6,'','\0'),(199,NULL,'Description',19,'','description','\0','',2,7,'','\0',7,'','\0'),(200,NULL,'Name',19,'','name','\0','',2,8,'','\0',8,'','\0'),(201,NULL,'Display Name',19,'','displayName','\0','',2,9,'','\0',9,'','\0'),(202,NULL,'Id',20,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(203,NULL,'Created By',20,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(204,NULL,'Owner',20,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(205,NULL,'Modified By',20,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(206,NULL,'Creation Date',20,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(207,NULL,'Modification Date',20,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(208,'','Lookup',20,'','lookup','\0','',17,6,'','\0',6,'','\0'),(209,NULL,'Order',20,'','order','\0','',1,7,'','\0',7,'','\0'),(210,NULL,'Object Id',20,'','objectId','\0','',15,8,'','\0',8,'','\0'),(211,'false','Fail If Data Not Found',20,'','failIfDataNotFound','','',3,9,'','\0',9,'','\0'),(212,NULL,'Type',20,'','type','\0','',2,10,'','\0',10,'','\0'),(213,NULL,'Provider Id',20,'','providerId','\0','',15,11,'','\0',11,'','\0'),(214,NULL,'Provider Name',20,'','providerName','\0','',2,12,'','\0',12,'','\0'),(215,NULL,'Name',20,'','name','\0','',2,13,'','\0',13,'','\0'),(216,NULL,'Id',21,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(217,NULL,'Created By',21,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(218,NULL,'Owner',21,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(219,NULL,'Modified By',21,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(220,NULL,'Creation Date',21,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(221,NULL,'Modification Date',21,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(222,NULL,'Fields',21,'','fields','\0','',8,6,'','\0',6,'','\0'),(223,NULL,'Display Name',21,'','displayName','\0','',2,7,'','\0',7,'','\0'),(224,NULL,'Id',22,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(225,NULL,'Created By',22,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(226,NULL,'Owner',22,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(227,NULL,'Modified By',22,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(228,NULL,'Creation Date',22,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(229,NULL,'Modification Date',22,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(230,NULL,'Subject',22,'','subject','\0','',2,6,'','\0',6,'','\0'),(231,NULL,'Description',22,'','description','\0','',2,7,'','\0',7,'','\0'),(232,'','Event Parameters',22,'','eventParameters','\0','',17,8,'','\0',8,'','\0'),(233,'','Trigger Listener Subject',22,'','triggerListenerSubject','\0','',2,9,'','\0',9,'','\0'),(234,NULL,'Name',22,'','name','\0','',2,10,'','\0',10,'','\0'),(235,NULL,'Display Name',22,'','displayName','\0','',2,11,'','\0',11,'','\0'),(236,NULL,'Id',23,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(237,NULL,'Created By',23,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(238,NULL,'Owner',23,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(239,NULL,'Modified By',23,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(240,NULL,'Creation Date',23,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(241,NULL,'Modification Date',23,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(242,'','Description',23,'','description','\0','',2,6,'','\0',6,'','\0'),(243,'','Module Version',23,'','moduleVersion','\0','',2,7,'','\0',7,'','\0'),(244,'','Trigger Task Events',23,'','triggerTaskEvents','\0','',17,8,'','\0',8,'','\0'),(245,'','Module Name',23,'','moduleName','\0','',2,9,'','\0',9,'','\0'),(246,'','Display Name',23,'','displayName','\0','',2,10,'','\0',10,'','\0'),(247,'','Action Task Events',23,'','actionTaskEvents','\0','',17,11,'','\0',11,'','\0'),(248,NULL,'Id',24,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(249,NULL,'Created By',24,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(250,NULL,'Owner',24,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(251,NULL,'Modified By',24,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(252,NULL,'Creation Date',24,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(253,NULL,'Modification Date',24,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(254,NULL,'Args',24,'','args','\0','',8,6,'','\0',6,'','\0'),(255,NULL,'Message',24,'','message','\0','',2,7,'','\0',7,'','\0'),(256,NULL,'Id',25,'','id','\0','',15,0,'\0','\0',NULL,'','\0'),(257,NULL,'Created By',25,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(258,NULL,'Owner',25,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(259,NULL,'Modified By',25,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(260,NULL,'Creation Date',25,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(261,NULL,'Modification Date',25,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(262,NULL,'Description',25,'','description','\0','',2,6,'','\0',6,'','\0'),(263,'','Actions',25,'','actions','\0','',17,7,'','\0',7,'','\0'),(264,'','Task Config',25,'','taskConfig','\0','',18,8,'','\0',8,'','\0'),(265,'false','Enabled',25,'','enabled','','',3,9,'','\0',9,'','\0'),(266,'','Trigger',25,'','trigger','\0','',18,10,'','\0',10,'','\0'),(267,'false','Has Registered Channel',25,'','hasRegisteredChannel','','',3,11,'','\0',11,'','\0'),(268,'','Validation Errors',25,'','validationErrors','\0','',17,12,'','\0',12,'','\0'),(269,NULL,'Name',25,'','name','\0','',2,13,'','\0',13,'','\0'),(270,'','Id',26,'','id','\0','',15,0,'\0','\0',NULL,'','\0'),(271,NULL,'Created By',26,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(272,NULL,'Owner',26,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(273,NULL,'Modified By',26,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(274,NULL,'Creation Date',26,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(275,NULL,'Modification Date',26,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(276,'','Name',26,'','name','','',2,6,'','\0',6,'','\0'),(277,'','Objects',26,'','objects','\0','',17,7,'','\0',7,'','\0'),(278,NULL,'Id',27,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(279,NULL,'Created By',27,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(280,NULL,'Owner',27,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(281,NULL,'Modified By',27,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(282,NULL,'Creation Date',27,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(283,NULL,'Modification Date',27,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(284,NULL,'Subject',27,'','subject','\0','',2,6,'','\0',6,'','\0'),(285,NULL,'Trigger Listener Subject',27,'','triggerListenerSubject','\0','',2,7,'','\0',7,'','\0'),(286,NULL,'Module Name',27,'','moduleName','\0','',2,8,'','\0',8,'','\0'),(287,NULL,'Display Name',27,'','displayName','\0','',2,9,'','\0',9,'','\0'),(288,NULL,'Channel Name',27,'','channelName','\0','',2,10,'','\0',10,'','\0'),(289,NULL,'Module Version',27,'','moduleVersion','\0','',2,11,'','\0',11,'','\0'),(290,NULL,'Name',27,'','name','\0','',2,12,'','\0',12,'','\0'),(291,NULL,'Id',28,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(292,NULL,'Created By',28,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(293,NULL,'Owner',28,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(294,NULL,'Modified By',28,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(295,NULL,'Creation Date',28,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(296,NULL,'Modification Date',28,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(297,NULL,'Expression',28,'','expression','\0','',2,6,'','\0',6,'','\0'),(298,NULL,'Operator',28,'','operator','\0','',2,7,'','\0',7,'','\0'),(299,NULL,'Type',28,'','type','\0','',8,8,'','\0',8,'','\0'),(300,'false','Negation Operator',28,'','negationOperator','','',3,9,'','\0',9,'','\0'),(301,NULL,'Key',28,'','key','\0','',2,10,'','\0',10,'','\0'),(302,NULL,'Display Name',28,'','displayName','\0','',2,11,'','\0',11,'','\0'),(303,NULL,'Id',29,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(304,NULL,'Created By',29,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(305,NULL,'Owner',29,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(306,NULL,'Modified By',29,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(307,NULL,'Creation Date',29,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(308,NULL,'Modification Date',29,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(309,NULL,'Subject',29,'','subject','\0','',2,6,'','\0',6,'','\0'),(310,'','Service Method',29,'','serviceMethod','\0','',2,7,'','\0',7,'','\0'),(311,'','Service Interface',29,'','serviceInterface','\0','',2,8,'','\0',8,'','\0'),(312,NULL,'Module Name',29,'','moduleName','\0','',2,9,'','\0',9,'','\0'),(313,NULL,'Display Name',29,'','displayName','\0','',2,10,'','\0',10,'','\0'),(314,NULL,'Channel Name',29,'','channelName','\0','',2,11,'','\0',11,'','\0'),(315,NULL,'Module Version',29,'','moduleVersion','\0','',2,12,'','\0',12,'','\0'),(316,'','Values',29,'','values','\0','',10,13,'','\0',13,'','\0'),(317,NULL,'Name',29,'','name','\0','',2,14,'','\0',14,'','\0'),(318,NULL,'Id',30,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(319,NULL,'Created By',30,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(320,NULL,'Owner',30,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(321,NULL,'Modified By',30,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(322,NULL,'Creation Date',30,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(323,NULL,'Modification Date',30,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(324,'','Lookup Fields',30,'','lookupFields','\0','',17,6,'','\0',6,'','\0'),(325,'','Type',30,'','type','\0','',2,7,'','\0',7,'','\0'),(326,'','Fields',30,'','fields','\0','',17,8,'','\0',8,'','\0'),(327,'','Display Name',30,'','displayName','\0','',2,9,'','\0',9,'','\0'),(328,'','Id',31,'','id','\0','',15,0,'\0','\0',NULL,'','\0'),(329,NULL,'Created By',31,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(330,NULL,'Owner',31,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(331,NULL,'Modified By',31,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(332,NULL,'Creation Date',31,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(333,NULL,'Modification Date',31,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(334,'','Subject',31,'','subject','\0','',2,6,'','\0',6,'','\0'),(335,NULL,'From Address',31,'','fromAddress','\0','',2,7,'','\0',7,'','\0'),(336,'','Delivery Status',31,'','deliveryStatus','','',8,8,'','\0',8,'','\0'),(337,'','Message',31,'','message','\0','',2,9,'','\0',9,'','\0'),(338,'','To Address',31,'','toAddress','','',2,10,'','\0',10,'','\0'),(339,'','Delivery Time',31,'','deliveryTime','','',6,11,'','\0',11,'','\0'),(340,NULL,'Id',32,'','id','\0','',15,0,'\0','\0',NULL,'','\0'),(341,NULL,'Created By',32,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(342,NULL,'Owner',32,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(343,NULL,'Modified By',32,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(344,NULL,'Creation Date',32,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(345,NULL,'Modification Date',32,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(346,'EMAIL','Action Type',32,'','actionType','','',8,6,'','\0',0,'','\0'),(347,'CRITICAL','Level',32,'','level','','',8,7,'','\0',1,'','\0'),(348,'','Recipient',32,'','recipient','','',2,8,'','\0',3,'','\0'),(349,'','Module Name',32,'','moduleName','\0','',2,9,'','\0',2,'','\0'),(350,NULL,'Id',33,'','id','',NULL,15,0,'\0','\0',NULL,'','\0'),(351,NULL,'Created By',33,'','creator','',NULL,2,1,'\0','\0',NULL,'','\0'),(352,NULL,'Owner',33,'','owner','\0',NULL,2,2,'\0','\0',NULL,'','\0'),(353,NULL,'Modified By',33,'','modifiedBy','',NULL,2,3,'\0','\0',NULL,'','\0'),(354,NULL,'Creation Date',33,'','creationDate','',NULL,6,4,'\0','\0',NULL,'','\0'),(355,NULL,'Modification Date',33,'','modificationDate','',NULL,6,5,'\0','\0',NULL,'','\0'),(356,'','Module Name',33,'','moduleName','','',2,6,'','\0',6,'','\0'),(357,'','Text',33,'','text','','',2,7,'','\0',7,'','\0'),(358,'','Int Str Map',33,'','intStrMap','\0','',10,8,'','\0',8,'','\0'),(359,'','Date',33,'','date','\0','',6,9,'','\0',9,'','\0'),(360,'','Str Str Map',33,'','strStrMap','\0','',10,10,'','\0',10,'','\0'),(361,'','Str Long Map',33,'','strLongMap','\0','',10,11,'','\0',11,'','\0'),(362,'','Str Object Map',33,'','strObjectMap','\0','',10,12,'','\0',12,'','\0'),(363,'INFO','Level',33,'','level','','',8,13,'','\0',13,'','\0'),(364,'','Timeout',33,'','timeout','\0','',6,14,'','\0',14,'','\0');
/*!40000 ALTER TABLE `Field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FieldMetadata`
--

DROP TABLE IF EXISTS `FieldMetadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FieldMetadata` (
  `id` bigint(20) NOT NULL,
  `field_id_OID` bigint(20) DEFAULT NULL,
  `key` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `metadata_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FieldMetadata_N49` (`field_id_OID`),
  CONSTRAINT `FieldMetadata_FK1` FOREIGN KEY (`field_id_OID`) REFERENCES `Field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FieldMetadata`
--

LOCK TABLES `FieldMetadata` WRITE;
/*!40000 ALTER TABLE `FieldMetadata` DISABLE KEYS */;
INSERT INTO `FieldMetadata` VALUES (1,1,'autoGenerated','true',0),(2,2,'autoGenerated','true',0),(3,3,'autoGeneratedEditable','true',0),(4,4,'autoGenerated','true',0),(5,5,'autoGenerated','true',0),(6,6,'autoGenerated','true',0),(7,9,'map.key.class','java.lang.String',0),(8,9,'map.value.class','java.lang.String',1),(9,12,'autoGenerated','true',0),(10,13,'autoGenerated','true',0),(11,14,'autoGeneratedEditable','true',0),(12,15,'autoGenerated','true',0),(13,16,'autoGenerated','true',0),(14,17,'autoGenerated','true',0),(15,18,'map.key.class','java.lang.String',0),(16,18,'map.value.class','java.lang.Object',1),(17,23,'autoGenerated','true',0),(18,24,'autoGenerated','true',0),(19,25,'autoGeneratedEditable','true',0),(20,26,'autoGenerated','true',0),(21,27,'autoGenerated','true',0),(22,28,'autoGenerated','true',0),(23,31,'autoGenerated','true',0),(24,32,'autoGenerated','true',0),(25,33,'autoGeneratedEditable','true',0),(26,34,'autoGenerated','true',0),(27,35,'autoGenerated','true',0),(28,36,'autoGenerated','true',0),(29,40,'autoGenerated','true',0),(30,41,'autoGenerated','true',0),(31,42,'autoGeneratedEditable','true',0),(32,43,'autoGenerated','true',0),(33,44,'autoGenerated','true',0),(34,45,'autoGenerated','true',0),(35,47,'enum.className','org.motechproject.security.constants.Protocol',0),(36,51,'enum.className','org.motechproject.security.constants.HTTPMethod',0),(37,52,'enum.className','org.motechproject.security.constants.Scheme',0),(38,58,'autoGenerated','true',0),(39,59,'autoGenerated','true',0),(40,60,'autoGeneratedEditable','true',0),(41,61,'autoGenerated','true',0),(42,62,'autoGenerated','true',0),(43,63,'autoGenerated','true',0),(44,72,'autoGenerated','true',0),(45,73,'autoGenerated','true',0),(46,74,'autoGeneratedEditable','true',0),(47,75,'autoGenerated','true',0),(48,76,'autoGenerated','true',0),(49,77,'autoGenerated','true',0),(50,83,'autoGenerated','true',0),(51,84,'autoGenerated','true',0),(52,85,'autoGeneratedEditable','true',0),(53,86,'autoGenerated','true',0),(54,87,'autoGenerated','true',0),(55,88,'autoGenerated','true',0),(56,95,'autoGenerated','true',0),(57,96,'autoGenerated','true',0),(58,97,'autoGeneratedEditable','true',0),(59,98,'autoGenerated','true',0),(60,99,'autoGenerated','true',0),(61,100,'autoGenerated','true',0),(62,104,'enum.className','org.motechproject.tasks.domain.ParameterType',0),(63,108,'autoGenerated','true',0),(64,109,'autoGenerated','true',0),(65,110,'autoGeneratedEditable','true',0),(66,111,'autoGenerated','true',0),(67,112,'autoGenerated','true',0),(68,113,'autoGenerated','true',0),(69,116,'autoGenerated','true',0),(70,117,'autoGenerated','true',0),(71,118,'autoGeneratedEditable','true',0),(72,119,'autoGenerated','true',0),(73,120,'autoGenerated','true',0),(74,121,'autoGenerated','true',0),(75,123,'enum.className','org.motechproject.tasks.domain.ParameterType',0),(76,125,'autoGenerated','true',0),(77,126,'autoGenerated','true',0),(78,127,'autoGeneratedEditable','true',0),(79,128,'autoGenerated','true',0),(80,129,'autoGenerated','true',0),(81,130,'autoGenerated','true',0),(82,132,'enum.className','org.motechproject.tasks.domain.ParameterType',0),(83,134,'autoGenerated','true',0),(84,135,'autoGenerated','true',0),(85,136,'autoGeneratedEditable','true',0),(86,137,'autoGenerated','true',0),(87,138,'autoGenerated','true',0),(88,139,'autoGenerated','true',0),(89,141,'enum.className','org.motechproject.tasks.domain.LogicalOperator',0),(90,142,'related.class','org.motechproject.tasks.domain.Filter',0),(91,142,'related.collectionType','java.util.List',1),(92,143,'autoGenerated','true',0),(93,144,'autoGenerated','true',0),(94,145,'autoGeneratedEditable','true',0),(95,146,'autoGenerated','true',0),(96,147,'autoGenerated','true',0),(97,148,'autoGenerated','true',0),(98,149,'enum.className','org.motechproject.tasks.domain.ParameterType',0),(99,151,'autoGenerated','true',0),(100,152,'autoGenerated','true',0),(101,153,'autoGeneratedEditable','true',0),(102,154,'autoGenerated','true',0),(103,155,'autoGenerated','true',0),(104,156,'autoGenerated','true',0),(105,158,'autoGenerated','true',0),(106,159,'autoGenerated','true',0),(107,160,'autoGeneratedEditable','true',0),(108,161,'autoGenerated','true',0),(109,162,'autoGenerated','true',0),(110,163,'autoGenerated','true',0),(111,167,'enum.className','org.motechproject.tasks.domain.MethodCallManner',0),(112,169,'related.class','org.motechproject.tasks.domain.ActionParameter',0),(113,169,'related.collectionType','java.util.SortedSet',1),(114,172,'autoGenerated','true',0),(115,173,'autoGenerated','true',0),(116,174,'autoGeneratedEditable','true',0),(117,175,'autoGenerated','true',0),(118,176,'autoGenerated','true',0),(119,177,'autoGenerated','true',0),(120,178,'related.class','org.motechproject.tasks.domain.FilterSet',0),(121,178,'related.collectionType','java.util.List',1),(122,179,'related.class','org.motechproject.tasks.domain.DataSource',0),(123,179,'related.collectionType','java.util.List',1),(124,180,'autoGenerated','true',0),(125,181,'autoGenerated','true',0),(126,182,'autoGeneratedEditable','true',0),(127,183,'autoGenerated','true',0),(128,184,'autoGenerated','true',0),(129,185,'autoGenerated','true',0),(130,188,'enum.className','org.motechproject.tasks.domain.TaskActivityType',0),(131,192,'autoGenerated','true',0),(132,193,'autoGenerated','true',0),(133,194,'autoGeneratedEditable','true',0),(134,195,'autoGenerated','true',0),(135,196,'autoGenerated','true',0),(136,197,'autoGenerated','true',0),(137,202,'autoGenerated','true',0),(138,203,'autoGenerated','true',0),(139,204,'autoGeneratedEditable','true',0),(140,205,'autoGenerated','true',0),(141,206,'autoGenerated','true',0),(142,207,'autoGenerated','true',0),(143,208,'related.class','org.motechproject.tasks.domain.Lookup',0),(144,208,'related.collectionType','java.util.List',1),(145,216,'autoGenerated','true',0),(146,217,'autoGenerated','true',0),(147,218,'autoGeneratedEditable','true',0),(148,219,'autoGenerated','true',0),(149,220,'autoGenerated','true',0),(150,221,'autoGenerated','true',0),(151,224,'autoGenerated','true',0),(152,225,'autoGenerated','true',0),(153,226,'autoGeneratedEditable','true',0),(154,227,'autoGenerated','true',0),(155,228,'autoGenerated','true',0),(156,229,'autoGenerated','true',0),(157,232,'related.class','org.motechproject.tasks.domain.EventParameter',0),(158,232,'related.collectionType','java.util.List',1),(159,236,'autoGenerated','true',0),(160,237,'autoGenerated','true',0),(161,238,'autoGeneratedEditable','true',0),(162,239,'autoGenerated','true',0),(163,240,'autoGenerated','true',0),(164,241,'autoGenerated','true',0),(165,244,'related.class','org.motechproject.tasks.domain.TriggerEvent',0),(166,244,'related.collectionType','java.util.List',1),(167,247,'related.class','org.motechproject.tasks.domain.ActionEvent',0),(168,247,'related.collectionType','java.util.List',1),(169,248,'autoGenerated','true',0),(170,249,'autoGenerated','true',0),(171,250,'autoGeneratedEditable','true',0),(172,251,'autoGenerated','true',0),(173,252,'autoGenerated','true',0),(174,253,'autoGenerated','true',0),(175,256,'autoGenerated','true',0),(176,257,'autoGenerated','true',0),(177,258,'autoGeneratedEditable','true',0),(178,259,'autoGenerated','true',0),(179,260,'autoGenerated','true',0),(180,261,'autoGenerated','true',0),(181,263,'related.class','org.motechproject.tasks.domain.TaskActionInformation',0),(182,263,'related.collectionType','java.util.List',1),(183,264,'related.class','org.motechproject.tasks.domain.TaskConfig',0),(184,266,'related.class','org.motechproject.tasks.domain.TaskTriggerInformation',0),(185,268,'related.class','org.motechproject.tasks.domain.TaskError',0),(186,268,'related.collectionType','java.util.Set',1),(187,270,'autoGenerated','true',0),(188,271,'autoGenerated','true',0),(189,272,'autoGeneratedEditable','true',0),(190,273,'autoGenerated','true',0),(191,274,'autoGenerated','true',0),(192,275,'autoGenerated','true',0),(193,277,'related.class','org.motechproject.tasks.domain.TaskDataProviderObject',0),(194,277,'related.collectionType','java.util.List',1),(195,278,'autoGenerated','true',0),(196,279,'autoGenerated','true',0),(197,280,'autoGeneratedEditable','true',0),(198,281,'autoGenerated','true',0),(199,282,'autoGenerated','true',0),(200,283,'autoGenerated','true',0),(201,291,'autoGenerated','true',0),(202,292,'autoGenerated','true',0),(203,293,'autoGeneratedEditable','true',0),(204,294,'autoGenerated','true',0),(205,295,'autoGenerated','true',0),(206,296,'autoGenerated','true',0),(207,299,'enum.className','org.motechproject.tasks.domain.ParameterType',0),(208,303,'autoGenerated','true',0),(209,304,'autoGenerated','true',0),(210,305,'autoGeneratedEditable','true',0),(211,306,'autoGenerated','true',0),(212,307,'autoGenerated','true',0),(213,308,'autoGenerated','true',0),(214,316,'map.key.class','java.lang.String',0),(215,316,'map.value.class','java.lang.String',1),(216,318,'autoGenerated','true',0),(217,319,'autoGenerated','true',0),(218,320,'autoGeneratedEditable','true',0),(219,321,'autoGenerated','true',0),(220,322,'autoGenerated','true',0),(221,323,'autoGenerated','true',0),(222,324,'related.class','org.motechproject.tasks.domain.LookupFieldsParameter',0),(223,324,'related.collectionType','java.util.List',1),(224,326,'related.class','org.motechproject.tasks.domain.FieldParameter',0),(225,326,'related.collectionType','java.util.List',1),(226,328,'autoGenerated','true',0),(227,329,'autoGenerated','true',0),(228,330,'autoGeneratedEditable','true',0),(229,331,'autoGenerated','true',0),(230,332,'autoGenerated','true',0),(231,333,'autoGenerated','true',0),(232,336,'enum.className','org.motechproject.email.domain.DeliveryStatus',0),(233,340,'autoGenerated','true',0),(234,341,'autoGenerated','true',0),(235,342,'autoGeneratedEditable','true',0),(236,343,'autoGenerated','true',0),(237,344,'autoGenerated','true',0),(238,345,'autoGenerated','true',0),(239,346,'enum.className','org.motechproject.admin.messages.ActionType',0),(240,347,'enum.className','org.motechproject.admin.messages.Level',0),(241,350,'autoGenerated','true',0),(242,351,'autoGenerated','true',0),(243,352,'autoGeneratedEditable','true',0),(244,353,'autoGenerated','true',0),(245,354,'autoGenerated','true',0),(246,355,'autoGenerated','true',0),(247,358,'map.key.class','java.lang.Integer',0),(248,358,'map.value.class','java.lang.String',1),(249,360,'map.key.class','java.lang.String',0),(250,360,'map.value.class','java.lang.String',1),(251,361,'map.key.class','java.lang.String',0),(252,361,'map.value.class','java.lang.Long',1),(253,362,'map.key.class','java.lang.String',0),(254,362,'map.value.class','java.lang.Object',1),(255,363,'enum.className','org.motechproject.admin.messages.Level',0);
/*!40000 ALTER TABLE `FieldMetadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FieldSetting`
--

DROP TABLE IF EXISTS `FieldSetting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FieldSetting` (
  `id` bigint(20) NOT NULL,
  `DETAILS_ID` bigint(20) DEFAULT NULL,
  `field_id_OID` bigint(20) DEFAULT NULL,
  `settings_INTEGER_IDX` int(11) DEFAULT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `FieldSetting_N50` (`field_id_OID`),
  KEY `FieldSetting_N49` (`DETAILS_ID`),
  CONSTRAINT `FieldSetting_FK1` FOREIGN KEY (`field_id_OID`) REFERENCES `Field` (`id`),
  CONSTRAINT `FieldSetting_FK2` FOREIGN KEY (`DETAILS_ID`) REFERENCES `TypeSetting` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FieldSetting`
--

LOCK TABLES `FieldSetting` WRITE;
/*!40000 ALTER TABLE `FieldSetting` DISABLE KEYS */;
INSERT INTO `FieldSetting` VALUES (1,9,10,0,'false'),(2,10,10,1,'255'),(3,9,11,0,'false'),(4,10,11,1,'255'),(5,9,20,0,'false'),(6,10,20,1,'255'),(7,9,21,0,'false'),(8,10,21,1,'255'),(9,9,22,0,'false'),(10,10,22,1,'255'),(11,9,29,0,'false'),(12,10,29,1,'255'),(13,9,30,0,'false'),(14,10,30,1,'255'),(15,3,37,0,'addUser\neditUser\ndeleteUser\nmanageUser\nactivateUser\nmanageRole\nmanagePermission\nviewSecurity\nupdateSecurity\nmdsSchemaAccess\nmdsSettingsAccess\nmdsDataAccess\nviewBasicEmailLogs\nviewDetailedEmailLogs\nviewUser\nviewRole\nstartBundle\nstopBundle\nmanageBundles\ninstallBundle\nbundleDetails\nuninstallBundle'),(16,4,37,1,'true'),(17,5,37,2,'true'),(18,9,38,0,'false'),(19,10,38,1,'255'),(20,3,47,0,'[HTTP, HTTPS]'),(21,4,47,1,'false'),(22,5,47,2,'false'),(23,3,48,0,'[]'),(24,4,48,1,'true'),(25,5,48,2,'true'),(26,9,49,0,'false'),(27,10,49,1,'255'),(28,3,51,0,'[ANY, GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE]'),(29,4,51,1,'false'),(30,5,51,2,'true'),(31,3,52,0,'[NO_SECURITY, USERNAME_PASSWORD, BASIC, OPEN_ID, OATH]'),(32,4,52,1,'false'),(33,5,52,2,'true'),(34,9,54,0,'false'),(35,10,54,1,'255'),(36,9,55,0,'false'),(37,10,55,1,'255'),(38,3,57,0,'[]'),(39,4,57,1,'true'),(40,5,57,2,'true'),(41,9,64,0,'false'),(42,10,64,1,'255'),(43,3,65,0,'Motech%20Admin'),(44,4,65,1,'true'),(45,5,65,2,'true'),(46,9,67,0,'false'),(47,10,67,1,'255'),(48,9,68,0,'false'),(49,10,68,1,'255'),(50,9,69,0,'false'),(51,10,69,1,'255'),(52,9,70,0,'false'),(53,10,70,1,'255'),(54,9,78,0,'false'),(55,10,78,1,'255'),(56,9,80,0,'false'),(57,10,80,1,'255'),(58,9,82,0,'false'),(59,10,82,1,'255'),(60,9,89,0,'false'),(61,10,89,1,'255'),(62,9,90,0,'false'),(63,10,90,1,'255'),(64,9,91,0,'false'),(65,10,91,1,'255'),(66,9,92,0,'false'),(67,10,92,1,'255'),(68,9,93,0,'false'),(69,10,93,1,'255'),(70,9,94,0,'false'),(71,10,94,1,'255'),(72,3,104,0,'[UNICODE, TEXTAREA, INTEGER, LONG, DOUBLE, DATE, TIME, PERIOD, BOOLEAN, LIST, MAP, UNKNOWN]'),(73,4,104,1,'false'),(74,5,104,2,'false'),(75,9,105,0,'false'),(76,10,105,1,'255'),(77,9,106,0,'false'),(78,10,106,1,'255'),(79,9,107,0,'false'),(80,10,107,1,'255'),(81,9,114,0,'false'),(82,10,114,1,'255'),(83,9,115,0,'false'),(84,10,115,1,'255'),(85,9,122,0,'false'),(86,10,122,1,'255'),(87,3,123,0,'[UNICODE, TEXTAREA, INTEGER, LONG, DOUBLE, DATE, TIME, PERIOD, BOOLEAN, LIST, MAP, UNKNOWN]'),(88,4,123,1,'false'),(89,5,123,2,'false'),(90,9,124,0,'false'),(91,10,124,1,'255'),(92,9,131,0,'false'),(93,10,131,1,'255'),(94,3,132,0,'[UNICODE, TEXTAREA, INTEGER, LONG, DOUBLE, DATE, TIME, PERIOD, BOOLEAN, LIST, MAP, UNKNOWN]'),(95,4,132,1,'false'),(96,5,132,2,'false'),(97,9,133,0,'false'),(98,10,133,1,'255'),(99,3,141,0,'[AND, OR]'),(100,4,141,1,'false'),(101,5,141,2,'false'),(102,6,142,0,'true'),(103,7,142,1,'true'),(104,8,142,2,'true'),(105,3,149,0,'[UNICODE, TEXTAREA, INTEGER, LONG, DOUBLE, DATE, TIME, PERIOD, BOOLEAN, LIST, MAP, UNKNOWN]'),(106,4,149,1,'false'),(107,5,149,2,'false'),(108,9,150,0,'false'),(109,10,150,1,'255'),(110,9,164,0,'false'),(111,10,164,1,'255'),(112,9,165,0,'false'),(113,10,165,1,'255'),(114,9,166,0,'false'),(115,10,166,1,'255'),(116,3,167,0,'[NAMED_PARAMETERS, MAP]'),(117,4,167,1,'false'),(118,5,167,2,'false'),(119,9,168,0,'false'),(120,10,168,1,'255'),(121,6,169,0,'true'),(122,7,169,1,'true'),(123,8,169,2,'true'),(124,9,170,0,'false'),(125,10,170,1,'255'),(126,9,171,0,'false'),(127,10,171,1,'255'),(128,6,178,0,'true'),(129,7,178,1,'true'),(130,8,178,2,'true'),(131,6,179,0,'true'),(132,7,179,1,'true'),(133,8,179,2,'true'),(134,3,186,0,'[]'),(135,4,186,1,'true'),(136,5,186,2,'true'),(137,3,188,0,'[ERROR, WARNING, SUCCESS]'),(138,4,188,1,'false'),(139,5,188,2,'false'),(140,9,189,0,'false'),(141,10,189,1,'8096'),(142,9,191,0,'false'),(143,10,191,1,'255'),(144,9,198,0,'false'),(145,10,198,1,'255'),(146,9,199,0,'false'),(147,10,199,1,'255'),(148,9,200,0,'false'),(149,10,200,1,'255'),(150,9,201,0,'false'),(151,10,201,1,'255'),(152,6,208,0,'true'),(153,7,208,1,'true'),(154,8,208,2,'true'),(155,9,212,0,'false'),(156,10,212,1,'255'),(157,9,214,0,'false'),(158,10,214,1,'255'),(159,9,215,0,'false'),(160,10,215,1,'255'),(161,3,222,0,'[]'),(162,4,222,1,'true'),(163,5,222,2,'true'),(164,9,223,0,'false'),(165,10,223,1,'255'),(166,9,230,0,'false'),(167,10,230,1,'255'),(168,9,231,0,'false'),(169,10,231,1,'255'),(170,6,232,0,'true'),(171,7,232,1,'true'),(172,8,232,2,'true'),(173,9,233,0,'false'),(174,10,233,1,'255'),(175,9,234,0,'false'),(176,10,234,1,'255'),(177,9,235,0,'false'),(178,10,235,1,'255'),(179,9,242,0,'false'),(180,10,242,1,'255'),(181,9,243,0,'false'),(182,10,243,1,'255'),(183,6,244,0,'true'),(184,7,244,1,'true'),(185,8,244,2,'true'),(186,9,245,0,'false'),(187,10,245,1,'255'),(188,9,246,0,'false'),(189,10,246,1,'255'),(190,6,247,0,'true'),(191,7,247,1,'true'),(192,8,247,2,'true'),(193,3,254,0,'[]'),(194,4,254,1,'true'),(195,5,254,2,'true'),(196,9,255,0,'false'),(197,10,255,1,'255'),(198,9,262,0,'false'),(199,10,262,1,'255'),(200,6,263,0,'true'),(201,7,263,1,'true'),(202,8,263,2,'true'),(203,6,264,0,'true'),(204,7,264,1,'true'),(205,8,264,2,'true'),(206,6,266,0,'true'),(207,7,266,1,'true'),(208,8,266,2,'true'),(209,6,268,0,'true'),(210,7,268,1,'true'),(211,8,268,2,'true'),(212,9,269,0,'false'),(213,10,269,1,'255'),(214,9,276,0,'false'),(215,10,276,1,'255'),(216,6,277,0,'true'),(217,7,277,1,'true'),(218,8,277,2,'true'),(219,9,284,0,'false'),(220,10,284,1,'255'),(221,9,285,0,'false'),(222,10,285,1,'255'),(223,9,286,0,'false'),(224,10,286,1,'255'),(225,9,287,0,'false'),(226,10,287,1,'255'),(227,9,288,0,'false'),(228,10,288,1,'255'),(229,9,289,0,'false'),(230,10,289,1,'255'),(231,9,290,0,'false'),(232,10,290,1,'255'),(233,9,297,0,'false'),(234,10,297,1,'255'),(235,9,298,0,'false'),(236,10,298,1,'255'),(237,3,299,0,'[UNICODE, TEXTAREA, INTEGER, LONG, DOUBLE, DATE, TIME, PERIOD, BOOLEAN, LIST, MAP, UNKNOWN]'),(238,4,299,1,'false'),(239,5,299,2,'false'),(240,9,301,0,'false'),(241,10,301,1,'255'),(242,9,302,0,'false'),(243,10,302,1,'255'),(244,9,309,0,'false'),(245,10,309,1,'255'),(246,9,310,0,'false'),(247,10,310,1,'255'),(248,9,311,0,'false'),(249,10,311,1,'255'),(250,9,312,0,'false'),(251,10,312,1,'255'),(252,9,313,0,'false'),(253,10,313,1,'255'),(254,9,314,0,'false'),(255,10,314,1,'255'),(256,9,315,0,'false'),(257,10,315,1,'255'),(258,9,317,0,'false'),(259,10,317,1,'255'),(260,6,324,0,'true'),(261,7,324,1,'true'),(262,8,324,2,'true'),(263,9,325,0,'false'),(264,10,325,1,'255'),(265,6,326,0,'true'),(266,7,326,1,'true'),(267,8,326,2,'true'),(268,9,327,0,'false'),(269,10,327,1,'255'),(270,9,334,0,'false'),(271,10,334,1,'255'),(272,9,335,0,'false'),(273,10,335,1,'255'),(274,3,336,0,'[SENT, ERROR, RECEIVED]'),(275,4,336,1,'false'),(276,5,336,2,'false'),(277,9,337,0,'true'),(278,10,337,1,'255'),(279,9,338,0,'false'),(280,10,338,1,'255'),(281,3,346,0,'[SMS, EMAIL]'),(282,4,346,1,'false'),(283,5,346,2,'false'),(284,3,347,0,'[CRITICAL, ERROR, WARN, INFO, DEBUG]'),(285,4,347,1,'false'),(286,5,347,2,'false'),(287,9,348,0,'false'),(288,10,348,1,'255'),(289,9,349,0,'false'),(290,10,349,1,'255'),(291,9,356,0,'false'),(292,10,356,1,'255'),(293,9,357,0,'false'),(294,10,357,1,'255'),(295,3,363,0,'[CRITICAL, ERROR, WARN, INFO, DEBUG]'),(296,4,363,1,'false'),(297,5,363,2,'false');
/*!40000 ALTER TABLE `FieldSetting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FieldValidation`
--

DROP TABLE IF EXISTS `FieldValidation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FieldValidation` (
  `id` bigint(20) NOT NULL,
  `DETAILS_ID` bigint(20) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `field_id_OID` bigint(20) DEFAULT NULL,
  `value` varchar(1024) DEFAULT NULL,
  `validations_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FieldValidation_N49` (`field_id_OID`),
  KEY `FieldValidation_N50` (`DETAILS_ID`),
  CONSTRAINT `FieldValidation_FK1` FOREIGN KEY (`DETAILS_ID`) REFERENCES `TypeValidation` (`id`),
  CONSTRAINT `FieldValidation_FK2` FOREIGN KEY (`field_id_OID`) REFERENCES `Field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FieldValidation`
--

LOCK TABLES `FieldValidation` WRITE;
/*!40000 ALTER TABLE `FieldValidation` DISABLE KEYS */;
INSERT INTO `FieldValidation` VALUES (1,5,'\0',10,NULL,0),(2,6,'\0',10,NULL,1),(3,7,'\0',10,NULL,2),(4,5,'\0',11,NULL,0),(5,6,'\0',11,NULL,1),(6,7,'\0',11,NULL,2),(7,5,'\0',20,NULL,0),(8,6,'\0',20,NULL,1),(9,7,'\0',20,NULL,2),(10,5,'\0',21,NULL,0),(11,6,'\0',21,NULL,1),(12,7,'\0',21,NULL,2),(13,5,'\0',22,NULL,0),(14,6,'\0',22,NULL,1),(15,7,'\0',22,NULL,2),(16,5,'\0',29,NULL,0),(17,6,'\0',29,NULL,1),(18,7,'\0',29,NULL,2),(19,5,'\0',30,NULL,0),(20,6,'\0',30,NULL,1),(21,7,'\0',30,NULL,2),(22,5,'\0',38,NULL,0),(23,6,'\0',38,NULL,1),(24,7,'\0',38,NULL,2),(25,1,'\0',46,NULL,0),(26,2,'\0',46,NULL,1),(27,3,'\0',46,NULL,2),(28,4,'\0',46,NULL,3),(29,5,'\0',49,NULL,0),(30,6,'\0',49,NULL,1),(31,7,'\0',49,NULL,2),(32,5,'\0',54,NULL,0),(33,6,'\0',54,NULL,1),(34,7,'\0',54,NULL,2),(35,5,'\0',55,NULL,0),(36,6,'\0',55,NULL,1),(37,7,'\0',55,NULL,2),(38,5,'\0',64,NULL,0),(39,6,'\0',64,NULL,1),(40,7,'\0',64,NULL,2),(41,5,'\0',67,NULL,0),(42,6,'\0',67,NULL,1),(43,7,'\0',67,NULL,2),(44,5,'\0',68,NULL,0),(45,6,'\0',68,NULL,1),(46,7,'\0',68,NULL,2),(47,5,'\0',69,NULL,0),(48,6,'\0',69,NULL,1),(49,7,'\0',69,NULL,2),(50,5,'\0',70,NULL,0),(51,6,'\0',70,NULL,1),(52,7,'\0',70,NULL,2),(53,5,'\0',78,NULL,0),(54,6,'\0',78,NULL,1),(55,7,'\0',78,NULL,2),(56,5,'\0',80,NULL,0),(57,6,'\0',80,NULL,1),(58,7,'\0',80,NULL,2),(59,5,'\0',82,NULL,0),(60,6,'\0',82,NULL,1),(61,7,'\0',82,NULL,2),(62,5,'\0',89,NULL,0),(63,6,'\0',89,NULL,1),(64,7,'\0',89,NULL,2),(65,5,'\0',90,NULL,0),(66,6,'\0',90,NULL,1),(67,7,'\0',90,NULL,2),(68,5,'\0',91,NULL,0),(69,6,'\0',91,NULL,1),(70,7,'\0',91,NULL,2),(71,5,'\0',92,NULL,0),(72,6,'\0',92,NULL,1),(73,7,'\0',92,NULL,2),(74,5,'\0',93,NULL,0),(75,6,'\0',93,NULL,1),(76,7,'\0',93,NULL,2),(77,5,'\0',94,NULL,0),(78,6,'\0',94,NULL,1),(79,7,'\0',94,NULL,2),(80,1,'\0',101,NULL,0),(81,2,'\0',101,NULL,1),(82,3,'\0',101,NULL,2),(83,4,'\0',101,NULL,3),(84,5,'\0',105,NULL,0),(85,6,'\0',105,NULL,1),(86,7,'\0',105,NULL,2),(87,5,'\0',106,NULL,0),(88,6,'\0',106,NULL,1),(89,7,'\0',106,NULL,2),(90,5,'\0',107,NULL,0),(91,6,'\0',107,NULL,1),(92,7,'\0',107,NULL,2),(93,5,'\0',114,NULL,0),(94,6,'\0',114,NULL,1),(95,7,'\0',114,NULL,2),(96,5,'\0',115,NULL,0),(97,6,'\0',115,NULL,1),(98,7,'\0',115,NULL,2),(99,5,'\0',122,NULL,0),(100,6,'\0',122,NULL,1),(101,7,'\0',122,NULL,2),(102,5,'\0',124,NULL,0),(103,6,'\0',124,NULL,1),(104,7,'\0',124,NULL,2),(105,5,'\0',131,NULL,0),(106,6,'\0',131,NULL,1),(107,7,'\0',131,NULL,2),(108,5,'\0',133,NULL,0),(109,6,'\0',133,NULL,1),(110,7,'\0',133,NULL,2),(111,1,'\0',140,NULL,0),(112,2,'\0',140,NULL,1),(113,3,'\0',140,NULL,2),(114,4,'\0',140,NULL,3),(115,5,'\0',150,NULL,0),(116,6,'\0',150,NULL,1),(117,7,'\0',150,NULL,2),(118,1,'\0',157,NULL,0),(119,2,'\0',157,NULL,1),(120,3,'\0',157,NULL,2),(121,4,'\0',157,NULL,3),(122,5,'\0',164,NULL,0),(123,6,'\0',164,NULL,1),(124,7,'\0',164,NULL,2),(125,5,'\0',165,NULL,0),(126,6,'\0',165,NULL,1),(127,7,'\0',165,NULL,2),(128,5,'\0',166,NULL,0),(129,6,'\0',166,NULL,1),(130,7,'\0',166,NULL,2),(131,5,'\0',168,NULL,0),(132,6,'\0',168,NULL,1),(133,7,'\0',168,NULL,2),(134,5,'\0',170,NULL,0),(135,6,'\0',170,NULL,1),(136,7,'\0',170,NULL,2),(137,5,'\0',171,NULL,0),(138,6,'\0',171,NULL,1),(139,7,'\0',171,NULL,2),(140,5,'\0',189,NULL,0),(141,6,'\0',189,NULL,1),(142,7,'\0',189,NULL,2),(143,5,'\0',191,NULL,0),(144,6,'\0',191,NULL,1),(145,7,'\0',191,NULL,2),(146,5,'\0',198,NULL,0),(147,6,'\0',198,NULL,1),(148,7,'\0',198,NULL,2),(149,5,'\0',199,NULL,0),(150,6,'\0',199,NULL,1),(151,7,'\0',199,NULL,2),(152,5,'\0',200,NULL,0),(153,6,'\0',200,NULL,1),(154,7,'\0',200,NULL,2),(155,5,'\0',201,NULL,0),(156,6,'\0',201,NULL,1),(157,7,'\0',201,NULL,2),(158,1,'\0',209,NULL,0),(159,2,'\0',209,NULL,1),(160,3,'\0',209,NULL,2),(161,4,'\0',209,NULL,3),(162,5,'\0',212,NULL,0),(163,6,'\0',212,NULL,1),(164,7,'\0',212,NULL,2),(165,5,'\0',214,NULL,0),(166,6,'\0',214,NULL,1),(167,7,'\0',214,NULL,2),(168,5,'\0',215,NULL,0),(169,6,'\0',215,NULL,1),(170,7,'\0',215,NULL,2),(171,5,'\0',223,NULL,0),(172,6,'\0',223,NULL,1),(173,7,'\0',223,NULL,2),(174,5,'\0',230,NULL,0),(175,6,'\0',230,NULL,1),(176,7,'\0',230,NULL,2),(177,5,'\0',231,NULL,0),(178,6,'\0',231,NULL,1),(179,7,'\0',231,NULL,2),(180,5,'\0',233,NULL,0),(181,6,'\0',233,NULL,1),(182,7,'\0',233,NULL,2),(183,5,'\0',234,NULL,0),(184,6,'\0',234,NULL,1),(185,7,'\0',234,NULL,2),(186,5,'\0',235,NULL,0),(187,6,'\0',235,NULL,1),(188,7,'\0',235,NULL,2),(189,5,'\0',242,NULL,0),(190,6,'\0',242,NULL,1),(191,7,'\0',242,NULL,2),(192,5,'\0',243,NULL,0),(193,6,'\0',243,NULL,1),(194,7,'\0',243,NULL,2),(195,5,'\0',245,NULL,0),(196,6,'\0',245,NULL,1),(197,7,'\0',245,NULL,2),(198,5,'\0',246,NULL,0),(199,6,'\0',246,NULL,1),(200,7,'\0',246,NULL,2),(201,5,'\0',255,NULL,0),(202,6,'\0',255,NULL,1),(203,7,'\0',255,NULL,2),(204,5,'\0',262,NULL,0),(205,6,'\0',262,NULL,1),(206,7,'\0',262,NULL,2),(207,5,'\0',269,NULL,0),(208,6,'\0',269,NULL,1),(209,7,'\0',269,NULL,2),(210,5,'\0',276,NULL,0),(211,6,'\0',276,NULL,1),(212,7,'\0',276,NULL,2),(213,5,'\0',284,NULL,0),(214,6,'\0',284,NULL,1),(215,7,'\0',284,NULL,2),(216,5,'\0',285,NULL,0),(217,6,'\0',285,NULL,1),(218,7,'\0',285,NULL,2),(219,5,'\0',286,NULL,0),(220,6,'\0',286,NULL,1),(221,7,'\0',286,NULL,2),(222,5,'\0',287,NULL,0),(223,6,'\0',287,NULL,1),(224,7,'\0',287,NULL,2),(225,5,'\0',288,NULL,0),(226,6,'\0',288,NULL,1),(227,7,'\0',288,NULL,2),(228,5,'\0',289,NULL,0),(229,6,'\0',289,NULL,1),(230,7,'\0',289,NULL,2),(231,5,'\0',290,NULL,0),(232,6,'\0',290,NULL,1),(233,7,'\0',290,NULL,2),(234,5,'\0',297,NULL,0),(235,6,'\0',297,NULL,1),(236,7,'\0',297,NULL,2),(237,5,'\0',298,NULL,0),(238,6,'\0',298,NULL,1),(239,7,'\0',298,NULL,2),(240,5,'\0',301,NULL,0),(241,6,'\0',301,NULL,1),(242,7,'\0',301,NULL,2),(243,5,'\0',302,NULL,0),(244,6,'\0',302,NULL,1),(245,7,'\0',302,NULL,2),(246,5,'\0',309,NULL,0),(247,6,'\0',309,NULL,1),(248,7,'\0',309,NULL,2),(249,5,'\0',310,NULL,0),(250,6,'\0',310,NULL,1),(251,7,'\0',310,NULL,2),(252,5,'\0',311,NULL,0),(253,6,'\0',311,NULL,1),(254,7,'\0',311,NULL,2),(255,5,'\0',312,NULL,0),(256,6,'\0',312,NULL,1),(257,7,'\0',312,NULL,2),(258,5,'\0',313,NULL,0),(259,6,'\0',313,NULL,1),(260,7,'\0',313,NULL,2),(261,5,'\0',314,NULL,0),(262,6,'\0',314,NULL,1),(263,7,'\0',314,NULL,2),(264,5,'\0',315,NULL,0),(265,6,'\0',315,NULL,1),(266,7,'\0',315,NULL,2),(267,5,'\0',317,NULL,0),(268,6,'\0',317,NULL,1),(269,7,'\0',317,NULL,2),(270,5,'\0',325,NULL,0),(271,6,'\0',325,NULL,1),(272,7,'\0',325,NULL,2),(273,5,'\0',327,NULL,0),(274,6,'\0',327,NULL,1),(275,7,'\0',327,NULL,2),(276,5,'\0',334,NULL,0),(277,6,'\0',334,NULL,1),(278,7,'\0',334,NULL,2),(279,5,'\0',335,NULL,0),(280,6,'\0',335,NULL,1),(281,7,'\0',335,NULL,2),(282,5,'\0',337,NULL,0),(283,6,'\0',337,NULL,1),(284,7,'\0',337,NULL,2),(285,5,'\0',338,NULL,0),(286,6,'\0',338,NULL,1),(287,7,'\0',338,NULL,2),(288,5,'\0',348,NULL,0),(289,6,'\0',348,NULL,1),(290,7,'\0',348,NULL,2),(291,5,'\0',349,NULL,0),(292,6,'\0',349,NULL,1),(293,7,'\0',349,NULL,2),(294,5,'\0',356,NULL,0),(295,6,'\0',356,NULL,1),(296,7,'\0',356,NULL,2),(297,5,'\0',357,NULL,0),(298,6,'\0',357,NULL,1),(299,7,'\0',357,NULL,2);
/*!40000 ALTER TABLE `FieldValidation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Lookup`
--

DROP TABLE IF EXISTS `Lookup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Lookup` (
  `id` bigint(20) NOT NULL,
  `entity_id_OID` bigint(20) DEFAULT NULL,
  `exposedViaRest` bit(1) NOT NULL,
  `lookupName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `singleObjectReturn` bit(1) NOT NULL,
  `lookups_INTEGER_IDX` int(11) DEFAULT NULL,
  `readOnly` bit(1) NOT NULL,
  `setLookupFields` mediumblob,
  `useGenericParams` mediumblob,
  `rangeLookupFields` mediumblob,
  `customOperators` mediumblob,
  `methodName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Lookup_N49` (`entity_id_OID`),
  CONSTRAINT `Lookup_FK1` FOREIGN KEY (`entity_id_OID`) REFERENCES `Entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Lookup`
--

LOCK TABLES `Lookup` WRITE;
/*!40000 ALTER TABLE `Lookup` DISABLE KEYS */;
INSERT INTO `Lookup` VALUES (1,2,'\0','By bundle and file name','\0',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0filenamesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0t\0bundleq\0~\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByBundleAndFileName'),(2,2,'\0','By bundle','\0',1,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0bundlesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByBundle'),(3,4,'\0','Find By Role Name','',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0roleNamesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByRoleName'),(4,5,'\0','Find By Origin','\0',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0originsr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByOrigin'),(5,7,'\0','Find For User','',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0usernamesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findForUser'),(6,7,'\0','Find By Expiration Date','\0',1,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0expirationDatesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0w\0\0\0t\0expirationDatex','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByExpirationDate'),(7,7,'\0','Find For Token','',2,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0tokensr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findForToken'),(8,3,'\0','Find By Permission Name','',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0permissionNamesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByPermissionName'),(9,6,'\0','Find By Role','\0',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0rolessr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexpx','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByRole'),(10,6,'\0','Find By User Name','',1,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0userNamesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0userNamet\0equalsIgnoreCase()x','findByUserName'),(11,6,'\0','Find By Open Id','',2,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0openIdsr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByOpenId'),(12,6,'\0','Find By Email','',3,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0emailsr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByEmail'),(13,25,'\0','Find Tasks By Name','\0',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0namesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findTasksByName'),(14,26,'\0','By data provider name','',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0namesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByName'),(15,18,'\0','By Task','\0',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0tasksr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','byTask'),(16,23,'\0','Find By Module Name','',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\nmoduleNamesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByModuleName'),(17,31,'\0','By recipient address','\0',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0	toAddresssr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByRecipientAddress'),(18,31,'\0','Search','\0',1,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0w\0\0\0t\0deliveryStatusx','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0messagesr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0t\0deliveryTimeq\0~\0t\0subjectq\0~\0t\0fromAddressq\0~\0t\0deliveryStatusq\0~\0t\0	toAddressq\0~\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0w\0\0\0t\0deliveryTimex','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','find'),(19,33,'\0','Find By Timeout','\0',0,'','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0\0w\0\0\0\0x','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0timeoutsr\0java.lang.BooleanÕ rÄ’ú˙Ó\0Z\0valuexp\0x','¨Ì\0sr\0java.util.ArrayListxÅ“ô«aù\0I\0sizexp\0\0\0w\0\0\0t\0timeoutx','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','findByTimeout');
/*!40000 ALTER TABLE `Lookup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LookupFields`
--

DROP TABLE IF EXISTS `LookupFields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LookupFields` (
  `id_OID` bigint(20) NOT NULL,
  `id_EID` bigint(20) NOT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`id_OID`,`IDX`),
  KEY `LookupFields_N49` (`id_OID`),
  KEY `LookupFields_N50` (`id_EID`),
  CONSTRAINT `LookupFields_FK1` FOREIGN KEY (`id_OID`) REFERENCES `Lookup` (`id`),
  CONSTRAINT `LookupFields_FK2` FOREIGN KEY (`id_EID`) REFERENCES `Field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LookupFields`
--

LOCK TABLES `LookupFields` WRITE;
/*!40000 ALTER TABLE `LookupFields` DISABLE KEYS */;
INSERT INTO `LookupFields` VALUES (1,21,1),(1,22,0),(2,22,0),(8,30,0),(3,38,0),(4,54,0),(10,64,0),(9,65,0),(11,68,0),(12,70,0),(5,78,0),(6,81,0),(7,82,0),(15,187,0),(16,245,0),(13,269,0),(14,276,0),(18,334,2),(18,335,0),(18,336,5),(18,337,3),(17,338,0),(18,338,1),(18,339,4),(19,364,0);
/*!40000 ALTER TABLE `LookupFields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_ADMIN_NOTIFICATIONRULE`
--

DROP TABLE IF EXISTS `MOTECH_ADMIN_NOTIFICATIONRULE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_ADMIN_NOTIFICATIONRULE` (
  `id` bigint(20) NOT NULL,
  `actionType` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `level` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `recipient` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_ADMIN_NOTIFICATIONRULE_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_ADMIN_NOTIFICATIONRULE`
--

LOCK TABLES `MOTECH_ADMIN_NOTIFICATIONRULE` WRITE;
/*!40000 ALTER TABLE `MOTECH_ADMIN_NOTIFICATIONRULE` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_ADMIN_NOTIFICATIONRULE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY` (
  `id` bigint(20) NOT NULL,
  `actionType` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `level` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `notificationRule__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `notificationRule__HistoryFromTrash` bit(1) DEFAULT NULL,
  `notificationRule__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `recipient` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY`
--

LOCK TABLES `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_ADMIN_NOTIFICATIONRULE__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH` (
  `id` bigint(20) NOT NULL,
  `actionType` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `level` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `recipient` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH`
--

LOCK TABLES `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_ADMIN_NOTIFICATIONRULE__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_ADMIN_STATUSMESSAGE`
--

DROP TABLE IF EXISTS `MOTECH_ADMIN_STATUSMESSAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_ADMIN_STATUSMESSAGE` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `date` datetime DEFAULT NULL,
  `intStrMap` mediumblob,
  `level` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `strLongMap` mediumblob,
  `strObjectMap` mediumblob,
  `text` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `timeout` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_ADMIN_STATUSMESSAGE_N50` (`timeout`),
  KEY `MOTECH_ADMIN_STATUSMESSAGE_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_ADMIN_STATUSMESSAGE`
--

LOCK TABLES `MOTECH_ADMIN_STATUSMESSAGE` WRITE;
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE` DISABLE KEYS */;
INSERT INTO `MOTECH_ADMIN_STATUSMESSAGE` VALUES (1,'2015-04-23 15:12:01','motech',NULL,'¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\01t\0Val1t\02t\0Val2x\0','INFO','2015-04-23 15:12:01','motech','dfda','motech','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0abct\0123t\0	somethingt\0	388472385t\0hmmt\01x\0','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0amt\0it\0dumbt\0?x\0','sdfafdsa',NULL),(2,'2015-04-23 15:12:28','motech',NULL,'¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\011t\023234ffft\022t\0xvczcvzx\0','INFO','2015-04-23 15:12:28','motech','sdfasfd','motech','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0keyt\01t\0keylot\02342324x\0','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0aat\0bbx\0','dsafa',NULL);
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP`
--

DROP TABLE IF EXISTS `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP_N49` (`id_OID`),
  CONSTRAINT `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_ADMIN_STATUSMESSAGE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP`
--

LOCK TABLES `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP` WRITE;
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP` DISABLE KEYS */;
INSERT INTO `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP` VALUES (1,'k1','v1'),(1,'k2','value2'),(1,'k3','val3'),(2,'aaa','bbb'),(2,'cccc','ddd');
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE_STRSTRMAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_ADMIN_STATUSMESSAGE__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_ADMIN_STATUSMESSAGE__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_ADMIN_STATUSMESSAGE__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `date` datetime DEFAULT NULL,
  `intStrMap` mediumblob,
  `level` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `strLongMap` mediumblob,
  `strObjectMap` mediumblob,
  `text` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `timeout` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_ADMIN_STATUSMESSAGE__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_ADMIN_STATUSMESSAGE__TRASH`
--

LOCK TABLES `MOTECH_ADMIN_STATUSMESSAGE__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP`
--

DROP TABLE IF EXISTS `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP_N49` (`id_OID`),
  CONSTRAINT `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_ADMIN_STATUSMESSAGE__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP`
--

LOCK TABLES `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP` WRITE;
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_ADMIN_STATUSMESSAGE__TRASH_STRSTRMAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_EMAIL_EMAILRECORD`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_EMAIL_EMAILRECORD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_EMAIL_EMAILRECORD` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deliveryStatus` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deliveryTime` datetime NOT NULL,
  `fromAddress` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `message` mediumtext,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `toAddress` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_EMAIL_EMAILRECORD_N53` (`subject`),
  KEY `MOTECH_PLATFORM_EMAIL_EMAILRECORD_N54` (`deliveryTime`),
  KEY `MOTECH_PLATFORM_EMAIL_EMAILRECORD_N51` (`toAddress`),
  KEY `MOTECH_PLATFORM_EMAIL_EMAILRECORD_N52` (`fromAddress`),
  KEY `MOTECH_PLATFORM_EMAIL_EMAILRECORD_N50` (`deliveryStatus`),
  KEY `MOTECH_PLATFORM_EMAIL_EMAILRECORD_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_EMAIL_EMAILRECORD`
--

LOCK TABLES `MOTECH_PLATFORM_EMAIL_EMAILRECORD` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_EMAIL_EMAILRECORD` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_EMAIL_EMAILRECORD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deliveryStatus` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deliveryTime` datetime NOT NULL,
  `fromAddress` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `message` mediumtext,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `toAddress` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH`
--

LOCK TABLES `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_EMAIL_EMAILRECORD__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD` (
  `id` bigint(20) NOT NULL,
  `bundle` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `filename` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `properties` mediumblob,
  `raw` bit(1) NOT NULL,
  `version` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD_N49` (`id`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD_N50` (`bundle`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD_N51` (`filename`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD` DISABLE KEYS */;
INSERT INTO `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD` VALUES (1,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:24','','security.properties','2015-04-23 15:10:25','','','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x\0','\0','0.26.0.SNAPSHOT'),(2,'org.motechproject.motech-platform-email','2015-04-23 15:10:30','','motech-email.properties','2015-04-23 15:10:30','','','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x\0','\0','0.26.0.SNAPSHOT'),(3,'org.motechproject.motech-scheduler','2015-04-23 15:10:45','','quartz.properties','2015-04-23 15:10:45','','','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x\0','\0','0.26.0.SNAPSHOT'),(4,'org.motechproject.motech-tasks','2015-04-23 15:10:45','','handler-settings.properties','2015-04-23 15:10:45','','','¨Ì\0sr\0java.util.LinkedHashMap4¿N\\l¿˚\0Z\0accessOrderxr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x\0','\0','0.26.0.SNAPSHOT');
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY` (
  `id` bigint(20) NOT NULL,
  `bundle` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `filename` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modulePropertiesRecord__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `modulePropertiesRecord__HistoryFromTrash` bit(1) DEFAULT NULL,
  `modulePropertiesRecord__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `properties` mediumblob,
  `raw` bit(1) NOT NULL,
  `version` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HIqttq_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY` DISABLE KEYS */;
INSERT INTO `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY` VALUES (1,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:24','','security.properties','2015-04-23 15:10:25','',1,'\0',1,'','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','\0','0.26.0.SNAPSHOT'),(2,'org.motechproject.motech-platform-email','2015-04-23 15:10:30','','motech-email.properties','2015-04-23 15:10:30','',2,'\0',1,'','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','\0','0.26.0.SNAPSHOT'),(3,'org.motechproject.motech-scheduler','2015-04-23 15:10:45','','quartz.properties','2015-04-23 15:10:45','',3,'\0',1,'','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','\0','0.26.0.SNAPSHOT'),(4,'org.motechproject.motech-tasks','2015-04-23 15:10:45','','handler-settings.properties','2015-04-23 15:10:45','',4,'\0',1,'','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x','\0','0.26.0.SNAPSHOT');
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH` (
  `id` bigint(20) NOT NULL,
  `bundle` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `filename` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `properties` mediumblob,
  `raw` bit(1) NOT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `version` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_MODULEPROPERTIESRECORD__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD` (
  `id` bigint(20) NOT NULL,
  `configFileChecksum` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `filePath` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lastRun` datetime DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `platformInitialized` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD` DISABLE KEYS */;
INSERT INTO `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD` VALUES (1,'¬ù¬ùK¬ù+¬ùzP¬ù¬ù¬ù¬ù¬ù\n¬ù','2015-04-23 15:10:44','',NULL,'2015-04-23 15:10:44','2015-04-23 15:10:44','','','');
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSETTINGS`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSETTINGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSETTINGS` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSET5523_N49` (`id_OID`),
  CONSTRAINT `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSET5523_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSETTINGS`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSETTINGS` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSETTINGS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD_PLATFORMSETTINGS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY` (
  `id` bigint(20) NOT NULL,
  `configFileChecksum` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `filePath` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lastRun` datetime DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `platformInitialized` bit(1) NOT NULL,
  `settingsRecord__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `settingsRecord__HistoryFromTrash` bit(1) DEFAULT NULL,
  `settingsRecord__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY` DISABLE KEYS */;
INSERT INTO `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY` VALUES (1,'¬ù¬ùK¬ù+¬ùzP¬ù¬ù¬ù¬ù¬ù\n¬ù','2015-04-23 15:10:44','',NULL,'2015-04-23 15:10:44','2015-04-23 15:10:44','','','',1,'\0',1);
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLATFOnz5n`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLATFOnz5n`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLATFOnz5n` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLw536_N49` (`id_OID`),
  CONSTRAINT `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLw536_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLATFOnz5n`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLATFOnz5n` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLATFOnz5n` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__HISTORY_PLATFOnz5n` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH` (
  `id` bigint(20) NOT NULL,
  `configFileChecksum` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `filePath` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lastRun` datetime DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `platformInitialized` bit(1) NOT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLATFORM5gx5`
--

DROP TABLE IF EXISTS `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLATFORM5gx5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLATFORM5gx5` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLAT0jyb_N49` (`id_OID`),
  CONSTRAINT `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLAT0jyb_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLATFORM5gx5`
--

LOCK TABLES `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLATFORM5gx5` WRITE;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLATFORM5gx5` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_PLATFORM_SERVER_CONFIG_SETTINGSRECORD__TRASH_PLATFORM5gx5` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_ACTIONEVENT`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_ACTIONEVENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_ACTIONEVENT` (
  `id` bigint(20) NOT NULL,
  `serviceInterface` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceMethod` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceMethodCallManner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `actionTaskEvents_id_OWN` bigint(20) DEFAULT NULL,
  `actionTaskEvents_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_ACTIONEVENT_N49` (`id`),
  KEY `MOTECH_TASKS_ACTIONEVENT_FK1` (`actionTaskEvents_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_ACTIONEVENT_FK1` FOREIGN KEY (`actionTaskEvents_id_OWN`) REFERENCES `MOTECH_TASKS_CHANNEL` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_ACTIONEVENT`
--

LOCK TABLES `MOTECH_TASKS_ACTIONEVENT` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONEVENT` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_ACTIONEVENT` VALUES (1,'org.motechproject.scheduler.service.MotechSchedulerActionProxyService','scheduleCronJob','NAMED_PARAMETERS','2015-04-23 15:10:50','',NULL,'scheduler.scheduleCronJob','2015-04-23 15:10:51','',NULL,'',NULL,1,0),(2,'org.motechproject.scheduler.service.MotechSchedulerActionProxyService','scheduleRepeatingJob','NAMED_PARAMETERS','2015-04-23 15:10:51','',NULL,'scheduler.scheduleRepeatingJob','2015-04-23 15:10:51','',NULL,'',NULL,1,1),(3,'org.motechproject.scheduler.service.MotechSchedulerActionProxyService','scheduleRunOnceJob','NAMED_PARAMETERS','2015-04-23 15:10:51','',NULL,'scheduler.scheduleRunOnceJob','2015-04-23 15:10:51','',NULL,'',NULL,1,2),(4,'org.motechproject.scheduler.service.MotechSchedulerActionProxyService','scheduleDayOfWeekJob','NAMED_PARAMETERS','2015-04-23 15:10:51','',NULL,'scheduler.scheduleDayOfWeekJob','2015-04-23 15:10:51','',NULL,'',NULL,1,3),(5,'org.motechproject.scheduler.service.MotechSchedulerActionProxyService','schedulePeriodRepeatingJob','NAMED_PARAMETERS','2015-04-23 15:10:51','',NULL,'scheduler.schedulePeriodRepeatingJob','2015-04-23 15:10:51','',NULL,'',NULL,1,4),(6,'org.motechproject.scheduler.service.MotechSchedulerActionProxyService','unscheduleJobs','NAMED_PARAMETERS','2015-04-23 15:10:51','',NULL,'scheduler.unscheduleJobs','2015-04-23 15:10:51','',NULL,'',NULL,1,5),(7,NULL,NULL,'NAMED_PARAMETERS','2015-04-23 15:10:51','',NULL,'email.send','2015-04-23 15:10:51','',NULL,'','SendEMail',2,0),(50,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:53','',NULL,'CREATE SettingsRecord','2015-04-23 15:10:53','','org.motechproject.server.config.domain.SettingsRecord.create','',NULL,3,0),(51,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:53','',NULL,'UPDATE SettingsRecord','2015-04-23 15:10:53','','org.motechproject.server.config.domain.SettingsRecord.update','',NULL,3,1),(52,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:53','',NULL,'DELETE SettingsRecord','2015-04-23 15:10:53','','org.motechproject.server.config.domain.SettingsRecord.delete','',NULL,3,2),(53,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:53','',NULL,'CREATE ModulePropertiesRecord','2015-04-23 15:10:53','','org.motechproject.config.domain.ModulePropertiesRecord.create','',NULL,3,3),(54,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:53','',NULL,'UPDATE ModulePropertiesRecord','2015-04-23 15:10:53','','org.motechproject.config.domain.ModulePropertiesRecord.update','',NULL,3,4),(55,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:53','',NULL,'DELETE ModulePropertiesRecord','2015-04-23 15:10:53','','org.motechproject.config.domain.ModulePropertiesRecord.delete','',NULL,3,5),(56,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:53','',NULL,'CREATE MotechPermission','2015-04-23 15:10:53','','org.motechproject.security.domain.MotechPermission.create','',NULL,3,6),(57,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:53','',NULL,'UPDATE MotechPermission','2015-04-23 15:10:53','','org.motechproject.security.domain.MotechPermission.update','',NULL,3,7),(58,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:53','',NULL,'DELETE MotechPermission','2015-04-23 15:10:53','','org.motechproject.security.domain.MotechPermission.delete','',NULL,3,8),(59,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:53','',NULL,'CREATE MotechRole','2015-04-23 15:10:53','','org.motechproject.security.domain.MotechRole.create','',NULL,3,9),(60,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:53','',NULL,'UPDATE MotechRole','2015-04-23 15:10:53','','org.motechproject.security.domain.MotechRole.update','',NULL,3,10),(61,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:53','',NULL,'DELETE MotechRole','2015-04-23 15:10:53','','org.motechproject.security.domain.MotechRole.delete','',NULL,3,11),(62,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:53','',NULL,'CREATE MotechURLSecurityRule','2015-04-23 15:10:53','','org.motechproject.security.domain.MotechURLSecurityRule.create','',NULL,3,12),(63,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:54','',NULL,'UPDATE MotechURLSecurityRule','2015-04-23 15:10:54','','org.motechproject.security.domain.MotechURLSecurityRule.update','',NULL,3,13),(64,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:54','',NULL,'DELETE MotechURLSecurityRule','2015-04-23 15:10:54','','org.motechproject.security.domain.MotechURLSecurityRule.delete','',NULL,3,14),(65,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:54','',NULL,'CREATE MotechUser','2015-04-23 15:10:54','','org.motechproject.security.domain.MotechUser.create','',NULL,3,15),(66,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:54','',NULL,'UPDATE MotechUser','2015-04-23 15:10:54','','org.motechproject.security.domain.MotechUser.update','',NULL,3,16),(67,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:54','',NULL,'DELETE MotechUser','2015-04-23 15:10:54','','org.motechproject.security.domain.MotechUser.delete','',NULL,3,17),(68,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:54','',NULL,'CREATE PasswordRecovery','2015-04-23 15:10:54','','org.motechproject.security.domain.PasswordRecovery.create','',NULL,3,18),(69,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:54','',NULL,'UPDATE PasswordRecovery','2015-04-23 15:10:54','','org.motechproject.security.domain.PasswordRecovery.update','',NULL,3,19),(70,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:54','',NULL,'DELETE PasswordRecovery','2015-04-23 15:10:54','','org.motechproject.security.domain.PasswordRecovery.delete','',NULL,3,20),(71,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:54','',NULL,'CREATE TaskEventInformation','2015-04-23 15:10:54','','org.motechproject.tasks.domain.TaskEventInformation.create','',NULL,3,21),(72,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:54','',NULL,'UPDATE TaskEventInformation','2015-04-23 15:10:54','','org.motechproject.tasks.domain.TaskEventInformation.update','',NULL,3,22),(73,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:55','',NULL,'DELETE TaskEventInformation','2015-04-23 15:10:55','','org.motechproject.tasks.domain.TaskEventInformation.delete','',NULL,3,23),(74,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:55','',NULL,'CREATE Lookup','2015-04-23 15:10:55','','org.motechproject.tasks.domain.Lookup.create','',NULL,3,24),(75,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:55','',NULL,'UPDATE Lookup','2015-04-23 15:10:55','','org.motechproject.tasks.domain.Lookup.update','',NULL,3,25),(76,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:55','',NULL,'DELETE Lookup','2015-04-23 15:10:55','','org.motechproject.tasks.domain.Lookup.delete','',NULL,3,26),(77,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:55','',NULL,'CREATE Parameter','2015-04-23 15:10:55','','org.motechproject.tasks.domain.Parameter.create','',NULL,3,27),(78,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:55','',NULL,'UPDATE Parameter','2015-04-23 15:10:55','','org.motechproject.tasks.domain.Parameter.update','',NULL,3,28),(79,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:55','',NULL,'DELETE Parameter','2015-04-23 15:10:55','','org.motechproject.tasks.domain.Parameter.delete','',NULL,3,29),(80,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:55','',NULL,'CREATE TaskConfigStep','2015-04-23 15:10:55','','org.motechproject.tasks.domain.TaskConfigStep.create','',NULL,3,30),(81,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:55','',NULL,'UPDATE TaskConfigStep','2015-04-23 15:10:55','','org.motechproject.tasks.domain.TaskConfigStep.update','',NULL,3,31),(82,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:55','',NULL,'DELETE TaskConfigStep','2015-04-23 15:10:55','','org.motechproject.tasks.domain.TaskConfigStep.delete','',NULL,3,32),(83,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:55','',NULL,'CREATE TaskActivity','2015-04-23 15:10:55','','org.motechproject.tasks.domain.TaskActivity.create','',NULL,3,33),(84,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:56','',NULL,'UPDATE TaskActivity','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskActivity.update','',NULL,3,34),(85,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:56','',NULL,'DELETE TaskActivity','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskActivity.delete','',NULL,3,35),(86,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:56','',NULL,'CREATE TaskEvent','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskEvent.create','',NULL,3,36),(87,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:56','',NULL,'UPDATE TaskEvent','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskEvent.update','',NULL,3,37),(88,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:56','',NULL,'DELETE TaskEvent','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskEvent.delete','',NULL,3,38),(89,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:56','',NULL,'CREATE LookupFieldsParameter','2015-04-23 15:10:56','','org.motechproject.tasks.domain.LookupFieldsParameter.create','',NULL,3,39),(90,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:56','',NULL,'UPDATE LookupFieldsParameter','2015-04-23 15:10:56','','org.motechproject.tasks.domain.LookupFieldsParameter.update','',NULL,3,40),(91,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:56','',NULL,'DELETE LookupFieldsParameter','2015-04-23 15:10:56','','org.motechproject.tasks.domain.LookupFieldsParameter.delete','',NULL,3,41),(92,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:56','',NULL,'CREATE TaskError','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskError.create','',NULL,3,42),(93,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:56','',NULL,'UPDATE TaskError','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskError.update','',NULL,3,43),(94,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:56','',NULL,'DELETE TaskError','2015-04-23 15:10:56','','org.motechproject.tasks.domain.TaskError.delete','',NULL,3,44),(95,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:56','',NULL,'CREATE Filter','2015-04-23 15:10:56','','org.motechproject.tasks.domain.Filter.create','',NULL,3,45),(96,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:56','',NULL,'UPDATE Filter','2015-04-23 15:10:56','','org.motechproject.tasks.domain.Filter.update','',NULL,3,46),(97,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:56','',NULL,'DELETE Filter','2015-04-23 15:10:56','','org.motechproject.tasks.domain.Filter.delete','',NULL,3,47),(98,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:56','',NULL,'CREATE EmailRecord','2015-04-23 15:10:56','','org.motechproject.email.domain.EmailRecord.create','',NULL,3,48),(99,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:56','',NULL,'UPDATE EmailRecord','2015-04-23 15:10:56','','org.motechproject.email.domain.EmailRecord.update','',NULL,3,49),(100,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:56','',NULL,'DELETE EmailRecord','2015-04-23 15:10:56','','org.motechproject.email.domain.EmailRecord.delete','',NULL,3,50),(101,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:56','',NULL,'CREATE NotificationRule','2015-04-23 15:10:56','','org.motechproject.admin.domain.NotificationRule.create','',NULL,3,51),(102,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:56','',NULL,'UPDATE NotificationRule','2015-04-23 15:10:56','','org.motechproject.admin.domain.NotificationRule.update','',NULL,3,52),(103,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:56','',NULL,'DELETE NotificationRule','2015-04-23 15:10:56','','org.motechproject.admin.domain.NotificationRule.delete','',NULL,3,53),(104,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:56','',NULL,'CREATE StatusMessage','2015-04-23 15:10:56','','org.motechproject.admin.domain.StatusMessage.create','',NULL,3,54),(105,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE StatusMessage','2015-04-23 15:10:57','','org.motechproject.admin.domain.StatusMessage.update','',NULL,3,55),(106,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE StatusMessage','2015-04-23 15:10:57','','org.motechproject.admin.domain.StatusMessage.delete','',NULL,3,56),(107,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE ActionParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.ActionParameter.create','',NULL,3,57),(108,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE ActionParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.ActionParameter.update','',NULL,3,58),(109,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE ActionParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.ActionParameter.delete','',NULL,3,59),(110,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE EventParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.EventParameter.create','',NULL,3,60),(111,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE EventParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.EventParameter.update','',NULL,3,61),(112,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE EventParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.EventParameter.delete','',NULL,3,62),(113,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE FieldParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.FieldParameter.create','',NULL,3,63),(114,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE FieldParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.FieldParameter.update','',NULL,3,64),(115,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE FieldParameter','2015-04-23 15:10:57','','org.motechproject.tasks.domain.FieldParameter.delete','',NULL,3,65),(116,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE TaskDataProviderObject','2015-04-23 15:10:57','','org.motechproject.tasks.domain.TaskDataProviderObject.create','',NULL,3,66),(117,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE TaskDataProviderObject','2015-04-23 15:10:57','','org.motechproject.tasks.domain.TaskDataProviderObject.update','',NULL,3,67),(118,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE TaskDataProviderObject','2015-04-23 15:10:57','','org.motechproject.tasks.domain.TaskDataProviderObject.delete','',NULL,3,68),(119,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE TaskDataProvider','2015-04-23 15:10:57','','org.motechproject.tasks.domain.TaskDataProvider.create','',NULL,3,69),(120,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE TaskDataProvider','2015-04-23 15:10:57','','org.motechproject.tasks.domain.TaskDataProvider.update','',NULL,3,70),(121,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE TaskDataProvider','2015-04-23 15:10:57','','org.motechproject.tasks.domain.TaskDataProvider.delete','',NULL,3,71),(122,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE FilterSet','2015-04-23 15:10:57','','org.motechproject.tasks.domain.FilterSet.create','',NULL,3,72),(123,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE FilterSet','2015-04-23 15:10:57','','org.motechproject.tasks.domain.FilterSet.update','',NULL,3,73),(124,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE FilterSet','2015-04-23 15:10:57','','org.motechproject.tasks.domain.FilterSet.delete','',NULL,3,74),(125,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE ActionEvent','2015-04-23 15:10:57','','org.motechproject.tasks.domain.ActionEvent.create','',NULL,3,75),(126,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE ActionEvent','2015-04-23 15:10:57','','org.motechproject.tasks.domain.ActionEvent.update','',NULL,3,76),(127,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:57','',NULL,'DELETE ActionEvent','2015-04-23 15:10:57','','org.motechproject.tasks.domain.ActionEvent.delete','',NULL,3,77),(128,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:57','',NULL,'CREATE DataSource','2015-04-23 15:10:57','','org.motechproject.tasks.domain.DataSource.create','',NULL,3,78),(129,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:57','',NULL,'UPDATE DataSource','2015-04-23 15:10:57','','org.motechproject.tasks.domain.DataSource.update','',NULL,3,79),(130,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:58','',NULL,'DELETE DataSource','2015-04-23 15:10:58','','org.motechproject.tasks.domain.DataSource.delete','',NULL,3,80),(131,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:58','',NULL,'CREATE TaskConfig','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskConfig.create','',NULL,3,81),(132,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:58','',NULL,'UPDATE TaskConfig','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskConfig.update','',NULL,3,82),(133,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:58','',NULL,'DELETE TaskConfig','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskConfig.delete','',NULL,3,83),(134,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:58','',NULL,'CREATE TriggerEvent','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TriggerEvent.create','',NULL,3,84),(135,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:58','',NULL,'UPDATE TriggerEvent','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TriggerEvent.update','',NULL,3,85),(136,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:58','',NULL,'DELETE TriggerEvent','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TriggerEvent.delete','',NULL,3,86),(137,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:58','',NULL,'CREATE Channel','2015-04-23 15:10:58','','org.motechproject.tasks.domain.Channel.create','',NULL,3,87),(138,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:58','',NULL,'UPDATE Channel','2015-04-23 15:10:58','','org.motechproject.tasks.domain.Channel.update','',NULL,3,88),(139,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:58','',NULL,'DELETE Channel','2015-04-23 15:10:58','','org.motechproject.tasks.domain.Channel.delete','',NULL,3,89),(140,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:58','',NULL,'CREATE TaskTriggerInformation','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskTriggerInformation.create','',NULL,3,90),(141,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:58','',NULL,'UPDATE TaskTriggerInformation','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskTriggerInformation.update','',NULL,3,91),(142,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:58','',NULL,'DELETE TaskTriggerInformation','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskTriggerInformation.delete','',NULL,3,92),(143,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:58','',NULL,'CREATE TaskActionInformation','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskActionInformation.create','',NULL,3,93),(144,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:58','',NULL,'UPDATE TaskActionInformation','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskActionInformation.update','',NULL,3,94),(145,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:58','',NULL,'DELETE TaskActionInformation','2015-04-23 15:10:58','','org.motechproject.tasks.domain.TaskActionInformation.delete','',NULL,3,95),(146,'org.motechproject.mds.service.ActionHandlerService','create','MAP','2015-04-23 15:10:58','',NULL,'CREATE Task','2015-04-23 15:10:58','','org.motechproject.tasks.domain.Task.create','',NULL,3,96),(147,'org.motechproject.mds.service.ActionHandlerService','update','MAP','2015-04-23 15:10:58','',NULL,'UPDATE Task','2015-04-23 15:10:58','','org.motechproject.tasks.domain.Task.update','',NULL,3,97),(148,'org.motechproject.mds.service.ActionHandlerService','delete','MAP','2015-04-23 15:10:58','',NULL,'DELETE Task','2015-04-23 15:10:58','','org.motechproject.tasks.domain.Task.delete','',NULL,3,98);
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONEVENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_ACTIONEVENT__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_ACTIONEVENT__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_ACTIONEVENT__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `serviceInterface` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceMethod` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceMethodCallManner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `actionTaskEvents_id_OWN` bigint(20) DEFAULT NULL,
  `actionTaskEvents_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_ACTIONEVENT__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_ACTIONEVENT__TRASH_FK1` (`actionTaskEvents_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_ACTIONEVENT__TRASH_FK1` FOREIGN KEY (`actionTaskEvents_id_OWN`) REFERENCES `MOTECH_TASKS_CHANNEL__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_ACTIONEVENT__TRASH`
--

LOCK TABLES `MOTECH_TASKS_ACTIONEVENT__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONEVENT__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONEVENT__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_ACTIONPARAMETER`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_ACTIONPARAMETER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_ACTIONPARAMETER` (
  `id` bigint(20) NOT NULL,
  `hidden` bit(1) DEFAULT NULL,
  `key` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `required` bit(1) DEFAULT NULL,
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `actionParameters_id_OWN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_ACTIONPARAMETER_N49` (`id`),
  KEY `MOTECH_TASKS_ACTIONPARAMETER_FK1` (`actionParameters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_ACTIONPARAMETER_FK1` FOREIGN KEY (`actionParameters_id_OWN`) REFERENCES `MOTECH_TASKS_ACTIONEVENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_ACTIONPARAMETER`
--

LOCK TABLES `MOTECH_TASKS_ACTIONPARAMETER` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONPARAMETER` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_ACTIONPARAMETER` VALUES (365,'\0','motechEventSubject',0,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventSubject','2015-04-23 15:10:51','','','UNICODE',1),(366,'\0','motechEventParameters',1,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventParameters','2015-04-23 15:10:51','','','MAP',1),(367,'\0','cronExpression',2,'',NULL,'2015-04-23 15:10:51','','scheduler.cronExpression','2015-04-23 15:10:51','','','UNICODE',1),(368,'\0','startDate',3,'',NULL,'2015-04-23 15:10:51','','scheduler.startDate','2015-04-23 15:10:51','','','DATE',1),(369,'\0','endTime',4,'',NULL,'2015-04-23 15:10:51','','scheduler.endDate','2015-04-23 15:10:51','','','DATE',1),(370,'\0','ignorePastFiresAtStart',5,'',NULL,'2015-04-23 15:10:51','','scheduler.ignorePastFiresAtStart','2015-04-23 15:10:51','','','BOOLEAN',1),(371,'\0','motechEventSubject',0,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventSubject','2015-04-23 15:10:51','','','UNICODE',2),(372,'\0','motechEventParameters',1,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventParameters','2015-04-23 15:10:51','','','MAP',2),(373,'\0','startDate',2,'',NULL,'2015-04-23 15:10:51','','scheduler.startDate','2015-04-23 15:10:51','','','DATE',2),(374,'\0','endTime',3,'',NULL,'2015-04-23 15:10:51','','scheduler.endDate','2015-04-23 15:10:51','','','DATE',2),(375,'\0','repeatCount',4,'',NULL,'2015-04-23 15:10:51','','scheduler.repeatCount','2015-04-23 15:10:51','','','INTEGER',2),(376,'\0','repeatIntervalInMilliSeconds',5,'',NULL,'2015-04-23 15:10:51','','scheduler.repeatIntervalInMilliSeconds','2015-04-23 15:10:51','','','LONG',2),(377,'\0','ignorePastFiresAtStart',6,'',NULL,'2015-04-23 15:10:51','','scheduler.ignorePastFiresAtStart','2015-04-23 15:10:51','','','BOOLEAN',2),(378,'\0','useOriginalFireTimeAfterMisfire',7,'',NULL,'2015-04-23 15:10:51','','scheduler.useOriginalFireTimeAfterMisfire','2015-04-23 15:10:51','','','BOOLEAN',2),(379,'\0','motechEventSubject',0,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventSubject','2015-04-23 15:10:51','','','UNICODE',3),(380,'\0','motechEventParameters',1,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventParameters','2015-04-23 15:10:51','','','MAP',3),(381,'\0','startDate',2,'',NULL,'2015-04-23 15:10:51','','scheduler.startDate','2015-04-23 15:10:51','','','DATE',3),(382,'\0','motechEventSubject',0,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventSubject','2015-04-23 15:10:51','','','UNICODE',4),(383,'\0','motechEventParameters',1,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventParameters','2015-04-23 15:10:51','','','MAP',4),(384,'\0','startDate',2,'',NULL,'2015-04-23 15:10:51','','scheduler.startDate','2015-04-23 15:10:51','','','DATE',4),(385,'\0','endTime',3,'',NULL,'2015-04-23 15:10:51','','scheduler.endDate','2015-04-23 15:10:51','','','DATE',4),(386,'\0','days',4,'',NULL,'2015-04-23 15:10:51','','scheduler.days','2015-04-23 15:10:51','','','LIST',4),(387,'\0','time',5,'',NULL,'2015-04-23 15:10:51','','scheduler.time','2015-04-23 15:10:51','','','TIME',4),(388,'\0','ignorePastFiresAtStart',6,'',NULL,'2015-04-23 15:10:51','','scheduler.ignorePastFiresAtStart','2015-04-23 15:10:51','','','BOOLEAN',4),(389,'\0','motechEventSubject',0,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventSubject','2015-04-23 15:10:51','','','UNICODE',5),(390,'\0','motechEventParameters',1,'',NULL,'2015-04-23 15:10:51','','scheduler.motechEventParameters','2015-04-23 15:10:51','','','MAP',5),(391,'\0','startDate',2,'',NULL,'2015-04-23 15:10:51','','scheduler.startDate','2015-04-23 15:10:51','','','DATE',5),(392,'\0','endTime',3,'',NULL,'2015-04-23 15:10:51','','scheduler.endDate','2015-04-23 15:10:51','','','DATE',5),(393,'\0','repeatPeriod',4,'',NULL,'2015-04-23 15:10:51','','scheduler.repeatPeriod','2015-04-23 15:10:51','','','PERIOD',5),(394,'\0','ignorePastFiresAtStart',5,'',NULL,'2015-04-23 15:10:51','','scheduler.ignorePastFiresAtStart','2015-04-23 15:10:51','','','BOOLEAN',5),(395,'\0','useOriginalFireTimeAfterMisfire',6,'',NULL,'2015-04-23 15:10:51','','scheduler.useOriginalFireTimeAfterMisfire','2015-04-23 15:10:51','','','BOOLEAN',5),(396,'\0','subject',0,'',NULL,'2015-04-23 15:10:51','','scheduler.subject','2015-04-23 15:10:51','','','UNICODE',6),(397,'\0','fromAddress',0,'',NULL,'2015-04-23 15:10:51','','email.from.address','2015-04-23 15:10:51','','','UNICODE',7),(398,'\0','toAddress',1,'',NULL,'2015-04-23 15:10:51','','email.to.address','2015-04-23 15:10:51','','','UNICODE',7),(399,'\0','subject',2,'',NULL,'2015-04-23 15:10:51','','email.subject','2015-04-23 15:10:51','','','UNICODE',7),(400,'\0','message',3,'',NULL,'2015-04-23 15:10:51','','email.message','2015-04-23 15:10:51','','','UNICODE',7),(527,'','@ENTITY',0,'','org.motechproject.server.config.domain.SettingsRecord','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',50),(528,'\0','platformInitialized',1,'',NULL,'2015-04-23 15:10:53','','Platform Initialized','2015-04-23 15:10:53','','','BOOLEAN',50),(529,'\0','lastRun',2,'\0',NULL,'2015-04-23 15:10:53','','Last Run','2015-04-23 15:10:53','','','DATE',50),(530,'\0','platformSettings',3,'\0',NULL,'2015-04-23 15:10:53','','Platform Settings','2015-04-23 15:10:53','','','MAP',50),(531,'\0','filePath',4,'\0',NULL,'2015-04-23 15:10:53','','File Path','2015-04-23 15:10:53','','','UNICODE',50),(532,'\0','configFileChecksum',5,'\0',NULL,'2015-04-23 15:10:53','','Config File Checksum','2015-04-23 15:10:53','','','UNICODE',50),(533,'\0','owner',6,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',50),(534,'','@ENTITY',0,'','org.motechproject.server.config.domain.SettingsRecord','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',51),(535,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',51),(536,'\0','platformInitialized',2,'\0',NULL,'2015-04-23 15:10:53','','Platform Initialized','2015-04-23 15:10:53','','','BOOLEAN',51),(537,'\0','lastRun',3,'\0',NULL,'2015-04-23 15:10:53','','Last Run','2015-04-23 15:10:53','','','DATE',51),(538,'\0','platformSettings',4,'\0',NULL,'2015-04-23 15:10:53','','Platform Settings','2015-04-23 15:10:53','','','MAP',51),(539,'\0','filePath',5,'\0',NULL,'2015-04-23 15:10:53','','File Path','2015-04-23 15:10:53','','','UNICODE',51),(540,'\0','configFileChecksum',6,'\0',NULL,'2015-04-23 15:10:53','','Config File Checksum','2015-04-23 15:10:53','','','UNICODE',51),(541,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',51),(542,'','@ENTITY',0,'','org.motechproject.server.config.domain.SettingsRecord','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',52),(543,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',52),(544,'','@ENTITY',0,'','org.motechproject.config.domain.ModulePropertiesRecord','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',53),(545,'\0','properties',1,'\0',NULL,'2015-04-23 15:10:53','','Properties','2015-04-23 15:10:53','','','MAP',53),(546,'\0','raw',2,'',NULL,'2015-04-23 15:10:53','','Raw','2015-04-23 15:10:53','','','BOOLEAN',53),(547,'\0','version',3,'\0',NULL,'2015-04-23 15:10:53','','Version','2015-04-23 15:10:53','','','UNICODE',53),(548,'\0','filename',4,'\0',NULL,'2015-04-23 15:10:53','','Filename','2015-04-23 15:10:53','','','UNICODE',53),(549,'\0','bundle',5,'\0',NULL,'2015-04-23 15:10:53','','Bundle','2015-04-23 15:10:53','','','UNICODE',53),(550,'\0','owner',6,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',53),(551,'','@ENTITY',0,'','org.motechproject.config.domain.ModulePropertiesRecord','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',54),(552,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',54),(553,'\0','properties',2,'\0',NULL,'2015-04-23 15:10:53','','Properties','2015-04-23 15:10:53','','','MAP',54),(554,'\0','raw',3,'\0',NULL,'2015-04-23 15:10:53','','Raw','2015-04-23 15:10:53','','','BOOLEAN',54),(555,'\0','version',4,'\0',NULL,'2015-04-23 15:10:53','','Version','2015-04-23 15:10:53','','','UNICODE',54),(556,'\0','filename',5,'\0',NULL,'2015-04-23 15:10:53','','Filename','2015-04-23 15:10:53','','','UNICODE',54),(557,'\0','bundle',6,'\0',NULL,'2015-04-23 15:10:53','','Bundle','2015-04-23 15:10:53','','','UNICODE',54),(558,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',54),(559,'','@ENTITY',0,'','org.motechproject.config.domain.ModulePropertiesRecord','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',55),(560,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',55),(561,'','@ENTITY',0,'','org.motechproject.security.domain.MotechPermission','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',56),(562,'\0','bundleName',1,'\0',NULL,'2015-04-23 15:10:53','','Bundle Name','2015-04-23 15:10:53','','','UNICODE',56),(563,'\0','permissionName',2,'\0',NULL,'2015-04-23 15:10:53','','Permission Name','2015-04-23 15:10:53','','','UNICODE',56),(564,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',56),(565,'','@ENTITY',0,'','org.motechproject.security.domain.MotechPermission','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',57),(566,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',57),(567,'\0','bundleName',2,'\0',NULL,'2015-04-23 15:10:53','','Bundle Name','2015-04-23 15:10:53','','','UNICODE',57),(568,'\0','permissionName',3,'\0',NULL,'2015-04-23 15:10:53','','Permission Name','2015-04-23 15:10:53','','','UNICODE',57),(569,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',57),(570,'','@ENTITY',0,'','org.motechproject.security.domain.MotechPermission','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',58),(571,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',58),(572,'','@ENTITY',0,'','org.motechproject.security.domain.MotechRole','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',59),(573,'\0','permissionNames',1,'\0',NULL,'2015-04-23 15:10:53','','Permission Names','2015-04-23 15:10:53','','','LIST',59),(574,'\0','roleName',2,'\0',NULL,'2015-04-23 15:10:53','','Role Name','2015-04-23 15:10:53','','','UNICODE',59),(575,'\0','deletable',3,'',NULL,'2015-04-23 15:10:53','','Deletable','2015-04-23 15:10:53','','','BOOLEAN',59),(576,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',59),(577,'','@ENTITY',0,'','org.motechproject.security.domain.MotechRole','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',60),(578,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',60),(579,'\0','permissionNames',2,'\0',NULL,'2015-04-23 15:10:53','','Permission Names','2015-04-23 15:10:53','','','LIST',60),(580,'\0','roleName',3,'\0',NULL,'2015-04-23 15:10:53','','Role Name','2015-04-23 15:10:53','','','UNICODE',60),(581,'\0','deletable',4,'\0',NULL,'2015-04-23 15:10:53','','Deletable','2015-04-23 15:10:53','','','BOOLEAN',60),(582,'\0','owner',5,'\0',NULL,'2015-04-23 15:10:53','','Owner','2015-04-23 15:10:53','','','UNICODE',60),(583,'','@ENTITY',0,'','org.motechproject.security.domain.MotechRole','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',61),(584,'\0','id',1,'',NULL,'2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',61),(585,'','@ENTITY',0,'','org.motechproject.security.domain.MotechURLSecurityRule','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',62),(586,'\0','priority',1,'',NULL,'2015-04-23 15:10:53','','Priority','2015-04-23 15:10:53','','','INTEGER',62),(587,'\0','protocol',2,'\0',NULL,'2015-04-23 15:10:53','','Protocol','2015-04-23 15:10:53','','','UNICODE',62),(588,'\0','userAccess',3,'\0',NULL,'2015-04-23 15:10:53','','User Access','2015-04-23 15:10:53','','','LIST',62),(589,'\0','pattern',4,'\0',NULL,'2015-04-23 15:10:53','','Pattern','2015-04-23 15:10:53','','','UNICODE',62),(590,'\0','rest',5,'',NULL,'2015-04-23 15:10:53','','Rest','2015-04-23 15:10:53','','','BOOLEAN',62),(591,'\0','methodsRequired',6,'\0',NULL,'2015-04-23 15:10:53','','Methods Required','2015-04-23 15:10:54','','','LIST',62),(592,'\0','supportedSchemes',7,'\0',NULL,'2015-04-23 15:10:54','','Supported Schemes','2015-04-23 15:10:54','','','LIST',62),(593,'\0','active',8,'',NULL,'2015-04-23 15:10:54','','Active','2015-04-23 15:10:54','','','BOOLEAN',62),(594,'\0','origin',9,'\0',NULL,'2015-04-23 15:10:54','','Origin','2015-04-23 15:10:54','','','UNICODE',62),(595,'\0','version',10,'\0',NULL,'2015-04-23 15:10:54','','Version','2015-04-23 15:10:54','','','UNICODE',62),(596,'\0','deleted',11,'',NULL,'2015-04-23 15:10:54','','Deleted','2015-04-23 15:10:54','','','BOOLEAN',62),(597,'\0','permissionAccess',12,'\0',NULL,'2015-04-23 15:10:54','','Permission Access','2015-04-23 15:10:54','','','LIST',62),(598,'\0','owner',13,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',62),(599,'','@ENTITY',0,'','org.motechproject.security.domain.MotechURLSecurityRule','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',63),(600,'\0','id',1,'',NULL,'2015-04-23 15:10:54','','Id','2015-04-23 15:10:54','','','LONG',63),(601,'\0','priority',2,'\0',NULL,'2015-04-23 15:10:54','','Priority','2015-04-23 15:10:54','','','INTEGER',63),(602,'\0','protocol',3,'\0',NULL,'2015-04-23 15:10:54','','Protocol','2015-04-23 15:10:54','','','UNICODE',63),(603,'\0','userAccess',4,'\0',NULL,'2015-04-23 15:10:54','','User Access','2015-04-23 15:10:54','','','LIST',63),(604,'\0','pattern',5,'\0',NULL,'2015-04-23 15:10:54','','Pattern','2015-04-23 15:10:54','','','UNICODE',63),(605,'\0','rest',6,'\0',NULL,'2015-04-23 15:10:54','','Rest','2015-04-23 15:10:54','','','BOOLEAN',63),(606,'\0','methodsRequired',7,'\0',NULL,'2015-04-23 15:10:54','','Methods Required','2015-04-23 15:10:54','','','LIST',63),(607,'\0','supportedSchemes',8,'\0',NULL,'2015-04-23 15:10:54','','Supported Schemes','2015-04-23 15:10:54','','','LIST',63),(608,'\0','active',9,'\0',NULL,'2015-04-23 15:10:54','','Active','2015-04-23 15:10:54','','','BOOLEAN',63),(609,'\0','origin',10,'\0',NULL,'2015-04-23 15:10:54','','Origin','2015-04-23 15:10:54','','','UNICODE',63),(610,'\0','version',11,'\0',NULL,'2015-04-23 15:10:54','','Version','2015-04-23 15:10:54','','','UNICODE',63),(611,'\0','deleted',12,'\0',NULL,'2015-04-23 15:10:54','','Deleted','2015-04-23 15:10:54','','','BOOLEAN',63),(612,'\0','permissionAccess',13,'\0',NULL,'2015-04-23 15:10:54','','Permission Access','2015-04-23 15:10:54','','','LIST',63),(613,'\0','owner',14,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',63),(614,'','@ENTITY',0,'','org.motechproject.security.domain.MotechURLSecurityRule','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',64),(615,'\0','id',1,'',NULL,'2015-04-23 15:10:54','','Id','2015-04-23 15:10:54','','','LONG',64),(616,'','@ENTITY',0,'','org.motechproject.security.domain.MotechUser','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',65),(617,'\0','userName',1,'\0',NULL,'2015-04-23 15:10:54','','User Name','2015-04-23 15:10:54','','','UNICODE',65),(618,'\0','roles',2,'\0',NULL,'2015-04-23 15:10:54','','Roles','2015-04-23 15:10:54','','','LIST',65),(619,'\0','locale',3,'\0',NULL,'2015-04-23 15:10:54','','Locale','2015-04-23 15:10:54','','','UNICODE',65),(620,'\0','password',4,'\0',NULL,'2015-04-23 15:10:54','','Password','2015-04-23 15:10:54','','','UNICODE',65),(621,'\0','openId',5,'\0',NULL,'2015-04-23 15:10:54','','Open Id','2015-04-23 15:10:54','','','UNICODE',65),(622,'\0','externalId',6,'\0',NULL,'2015-04-23 15:10:54','','External Id','2015-04-23 15:10:54','','','UNICODE',65),(623,'\0','email',7,'\0',NULL,'2015-04-23 15:10:54','','Email','2015-04-23 15:10:54','','','UNICODE',65),(624,'\0','active',8,'',NULL,'2015-04-23 15:10:54','','Active','2015-04-23 15:10:54','','','BOOLEAN',65),(625,'\0','owner',9,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',65),(626,'','@ENTITY',0,'','org.motechproject.security.domain.MotechUser','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',66),(627,'\0','id',1,'',NULL,'2015-04-23 15:10:54','','Id','2015-04-23 15:10:54','','','LONG',66),(628,'\0','userName',2,'\0',NULL,'2015-04-23 15:10:54','','User Name','2015-04-23 15:10:54','','','UNICODE',66),(629,'\0','roles',3,'\0',NULL,'2015-04-23 15:10:54','','Roles','2015-04-23 15:10:54','','','LIST',66),(630,'\0','locale',4,'\0',NULL,'2015-04-23 15:10:54','','Locale','2015-04-23 15:10:54','','','UNICODE',66),(631,'\0','password',5,'\0',NULL,'2015-04-23 15:10:54','','Password','2015-04-23 15:10:54','','','UNICODE',66),(632,'\0','openId',6,'\0',NULL,'2015-04-23 15:10:54','','Open Id','2015-04-23 15:10:54','','','UNICODE',66),(633,'\0','externalId',7,'\0',NULL,'2015-04-23 15:10:54','','External Id','2015-04-23 15:10:54','','','UNICODE',66),(634,'\0','email',8,'\0',NULL,'2015-04-23 15:10:54','','Email','2015-04-23 15:10:54','','','UNICODE',66),(635,'\0','active',9,'\0',NULL,'2015-04-23 15:10:54','','Active','2015-04-23 15:10:54','','','BOOLEAN',66),(636,'\0','owner',10,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',66),(637,'','@ENTITY',0,'','org.motechproject.security.domain.MotechUser','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',67),(638,'\0','id',1,'',NULL,'2015-04-23 15:10:54','','Id','2015-04-23 15:10:54','','','LONG',67),(639,'','@ENTITY',0,'','org.motechproject.security.domain.PasswordRecovery','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',68),(640,'\0','username',1,'\0',NULL,'2015-04-23 15:10:54','','Username','2015-04-23 15:10:54','','','UNICODE',68),(641,'\0','locale',2,'\0',NULL,'2015-04-23 15:10:54','','Locale','2015-04-23 15:10:54','','','UNICODE',68),(642,'\0','email',3,'\0',NULL,'2015-04-23 15:10:54','','Email','2015-04-23 15:10:54','','','UNICODE',68),(643,'\0','expirationDate',4,'\0',NULL,'2015-04-23 15:10:54','','Expiration Date','2015-04-23 15:10:54','','','DATE',68),(644,'\0','token',5,'\0',NULL,'2015-04-23 15:10:54','','Token','2015-04-23 15:10:54','','','UNICODE',68),(645,'\0','owner',6,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',68),(646,'','@ENTITY',0,'','org.motechproject.security.domain.PasswordRecovery','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',69),(647,'\0','id',1,'',NULL,'2015-04-23 15:10:54','','Id','2015-04-23 15:10:54','','','LONG',69),(648,'\0','username',2,'\0',NULL,'2015-04-23 15:10:54','','Username','2015-04-23 15:10:54','','','UNICODE',69),(649,'\0','locale',3,'\0',NULL,'2015-04-23 15:10:54','','Locale','2015-04-23 15:10:54','','','UNICODE',69),(650,'\0','email',4,'\0',NULL,'2015-04-23 15:10:54','','Email','2015-04-23 15:10:54','','','UNICODE',69),(651,'\0','expirationDate',5,'\0',NULL,'2015-04-23 15:10:54','','Expiration Date','2015-04-23 15:10:54','','','DATE',69),(652,'\0','token',6,'\0',NULL,'2015-04-23 15:10:54','','Token','2015-04-23 15:10:54','','','UNICODE',69),(653,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',69),(654,'','@ENTITY',0,'','org.motechproject.security.domain.PasswordRecovery','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',70),(655,'\0','id',1,'',NULL,'2015-04-23 15:10:54','','Id','2015-04-23 15:10:54','','','LONG',70),(656,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskEventInformation','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',71),(657,'\0','subject',1,'\0',NULL,'2015-04-23 15:10:54','','Subject','2015-04-23 15:10:54','','','UNICODE',71),(658,'\0','channelName',2,'\0',NULL,'2015-04-23 15:10:54','','Channel Name','2015-04-23 15:10:54','','','UNICODE',71),(659,'\0','moduleVersion',3,'\0',NULL,'2015-04-23 15:10:54','','Module Version','2015-04-23 15:10:54','','','UNICODE',71),(660,'\0','moduleName',4,'\0',NULL,'2015-04-23 15:10:54','','Module Name','2015-04-23 15:10:54','','','UNICODE',71),(661,'\0','name',5,'\0',NULL,'2015-04-23 15:10:54','','Name','2015-04-23 15:10:54','','','UNICODE',71),(662,'\0','displayName',6,'\0',NULL,'2015-04-23 15:10:54','','Display Name','2015-04-23 15:10:54','','','UNICODE',71),(663,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',71),(664,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskEventInformation','2015-04-23 15:10:54','','Entity Class','2015-04-23 15:10:54','','','UNICODE',72),(665,'\0','id',1,'',NULL,'2015-04-23 15:10:54','','Id','2015-04-23 15:10:54','','','LONG',72),(666,'\0','subject',2,'\0',NULL,'2015-04-23 15:10:54','','Subject','2015-04-23 15:10:54','','','UNICODE',72),(667,'\0','channelName',3,'\0',NULL,'2015-04-23 15:10:54','','Channel Name','2015-04-23 15:10:54','','','UNICODE',72),(668,'\0','moduleVersion',4,'\0',NULL,'2015-04-23 15:10:54','','Module Version','2015-04-23 15:10:54','','','UNICODE',72),(669,'\0','moduleName',5,'\0',NULL,'2015-04-23 15:10:54','','Module Name','2015-04-23 15:10:54','','','UNICODE',72),(670,'\0','name',6,'\0',NULL,'2015-04-23 15:10:54','','Name','2015-04-23 15:10:54','','','UNICODE',72),(671,'\0','displayName',7,'\0',NULL,'2015-04-23 15:10:54','','Display Name','2015-04-23 15:10:54','','','UNICODE',72),(672,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:54','','Owner','2015-04-23 15:10:54','','','UNICODE',72),(673,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskEventInformation','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',73),(674,'\0','id',1,'',NULL,'2015-04-23 15:10:55','','Id','2015-04-23 15:10:55','','','LONG',73),(675,'','@ENTITY',0,'','org.motechproject.tasks.domain.Lookup','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',74),(676,'\0','value',1,'\0',NULL,'2015-04-23 15:10:55','','Value','2015-04-23 15:10:55','','','UNICODE',74),(677,'\0','field',2,'\0',NULL,'2015-04-23 15:10:55','','Field','2015-04-23 15:10:55','','','UNICODE',74),(678,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:55','','Owner','2015-04-23 15:10:55','','','UNICODE',74),(679,'','@ENTITY',0,'','org.motechproject.tasks.domain.Lookup','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',75),(680,'\0','id',1,'',NULL,'2015-04-23 15:10:55','','Id','2015-04-23 15:10:55','','','LONG',75),(681,'\0','value',2,'\0',NULL,'2015-04-23 15:10:55','','Value','2015-04-23 15:10:55','','','UNICODE',75),(682,'\0','field',3,'\0',NULL,'2015-04-23 15:10:55','','Field','2015-04-23 15:10:55','','','UNICODE',75),(683,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:55','','Owner','2015-04-23 15:10:55','','','UNICODE',75),(684,'','@ENTITY',0,'','org.motechproject.tasks.domain.Lookup','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',76),(685,'\0','id',1,'',NULL,'2015-04-23 15:10:55','','Id','2015-04-23 15:10:55','','','LONG',76),(686,'','@ENTITY',0,'','org.motechproject.tasks.domain.Parameter','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',77),(687,'\0','type',1,'\0',NULL,'2015-04-23 15:10:55','','Type','2015-04-23 15:10:55','','','UNICODE',77),(688,'\0','displayName',2,'\0',NULL,'2015-04-23 15:10:55','','Display Name','2015-04-23 15:10:55','','','UNICODE',77),(689,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:55','','Owner','2015-04-23 15:10:55','','','UNICODE',77),(690,'','@ENTITY',0,'','org.motechproject.tasks.domain.Parameter','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',78),(691,'\0','id',1,'',NULL,'2015-04-23 15:10:55','','Id','2015-04-23 15:10:55','','','LONG',78),(692,'\0','type',2,'\0',NULL,'2015-04-23 15:10:55','','Type','2015-04-23 15:10:55','','','UNICODE',78),(693,'\0','displayName',3,'\0',NULL,'2015-04-23 15:10:55','','Display Name','2015-04-23 15:10:55','','','UNICODE',78),(694,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:55','','Owner','2015-04-23 15:10:55','','','UNICODE',78),(695,'','@ENTITY',0,'','org.motechproject.tasks.domain.Parameter','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',79),(696,'\0','id',1,'',NULL,'2015-04-23 15:10:55','','Id','2015-04-23 15:10:55','','','LONG',79),(697,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskConfigStep','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',80),(698,'\0','order',1,'\0',NULL,'2015-04-23 15:10:55','','Order','2015-04-23 15:10:55','','','INTEGER',80),(699,'\0','owner',2,'\0',NULL,'2015-04-23 15:10:55','','Owner','2015-04-23 15:10:55','','','UNICODE',80),(700,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskConfigStep','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',81),(701,'\0','id',1,'',NULL,'2015-04-23 15:10:55','','Id','2015-04-23 15:10:55','','','LONG',81),(702,'\0','order',2,'\0',NULL,'2015-04-23 15:10:55','','Order','2015-04-23 15:10:55','','','INTEGER',81),(703,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:55','','Owner','2015-04-23 15:10:55','','','UNICODE',81),(704,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskConfigStep','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',82),(705,'\0','id',1,'',NULL,'2015-04-23 15:10:55','','Id','2015-04-23 15:10:55','','','LONG',82),(706,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskActivity','2015-04-23 15:10:55','','Entity Class','2015-04-23 15:10:55','','','UNICODE',83),(707,'\0','fields',1,'\0',NULL,'2015-04-23 15:10:55','','Fields','2015-04-23 15:10:55','','','LIST',83),(708,'\0','task',2,'\0',NULL,'2015-04-23 15:10:55','','Task','2015-04-23 15:10:55','','','LONG',83),(709,'\0','activityType',3,'\0',NULL,'2015-04-23 15:10:55','','Activity Type','2015-04-23 15:10:55','','','UNICODE',83),(710,'\0','stackTraceElement',4,'\0',NULL,'2015-04-23 15:10:55','','StackTrace element','2015-04-23 15:10:55','','','UNICODE',83),(711,'\0','date',5,'\0',NULL,'2015-04-23 15:10:55','','Date','2015-04-23 15:10:56','','','DATE',83),(712,'\0','message',6,'\0',NULL,'2015-04-23 15:10:56','','Message','2015-04-23 15:10:56','','','UNICODE',83),(713,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',83),(714,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskActivity','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',84),(715,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',84),(716,'\0','fields',2,'\0',NULL,'2015-04-23 15:10:56','','Fields','2015-04-23 15:10:56','','','LIST',84),(717,'\0','task',3,'\0',NULL,'2015-04-23 15:10:56','','Task','2015-04-23 15:10:56','','','LONG',84),(718,'\0','activityType',4,'\0',NULL,'2015-04-23 15:10:56','','Activity Type','2015-04-23 15:10:56','','','UNICODE',84),(719,'\0','stackTraceElement',5,'\0',NULL,'2015-04-23 15:10:56','','StackTrace element','2015-04-23 15:10:56','','','UNICODE',84),(720,'\0','date',6,'\0',NULL,'2015-04-23 15:10:56','','Date','2015-04-23 15:10:56','','','DATE',84),(721,'\0','message',7,'\0',NULL,'2015-04-23 15:10:56','','Message','2015-04-23 15:10:56','','','UNICODE',84),(722,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',84),(723,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskActivity','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',85),(724,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',85),(725,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskEvent','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',86),(726,'\0','subject',1,'\0',NULL,'2015-04-23 15:10:56','','Subject','2015-04-23 15:10:56','','','UNICODE',86),(727,'\0','description',2,'\0',NULL,'2015-04-23 15:10:56','','Description','2015-04-23 15:10:56','','','UNICODE',86),(728,'\0','name',3,'\0',NULL,'2015-04-23 15:10:56','','Name','2015-04-23 15:10:56','','','UNICODE',86),(729,'\0','displayName',4,'\0',NULL,'2015-04-23 15:10:56','','Display Name','2015-04-23 15:10:56','','','UNICODE',86),(730,'\0','owner',5,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',86),(731,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskEvent','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',87),(732,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',87),(733,'\0','subject',2,'\0',NULL,'2015-04-23 15:10:56','','Subject','2015-04-23 15:10:56','','','UNICODE',87),(734,'\0','description',3,'\0',NULL,'2015-04-23 15:10:56','','Description','2015-04-23 15:10:56','','','UNICODE',87),(735,'\0','name',4,'\0',NULL,'2015-04-23 15:10:56','','Name','2015-04-23 15:10:56','','','UNICODE',87),(736,'\0','displayName',5,'\0',NULL,'2015-04-23 15:10:56','','Display Name','2015-04-23 15:10:56','','','UNICODE',87),(737,'\0','owner',6,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',87),(738,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskEvent','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',88),(739,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',88),(740,'','@ENTITY',0,'','org.motechproject.tasks.domain.LookupFieldsParameter','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',89),(741,'\0','fields',1,'\0',NULL,'2015-04-23 15:10:56','','Fields','2015-04-23 15:10:56','','','LIST',89),(742,'\0','displayName',2,'\0',NULL,'2015-04-23 15:10:56','','Display Name','2015-04-23 15:10:56','','','UNICODE',89),(743,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',89),(744,'','@ENTITY',0,'','org.motechproject.tasks.domain.LookupFieldsParameter','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',90),(745,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',90),(746,'\0','fields',2,'\0',NULL,'2015-04-23 15:10:56','','Fields','2015-04-23 15:10:56','','','LIST',90),(747,'\0','displayName',3,'\0',NULL,'2015-04-23 15:10:56','','Display Name','2015-04-23 15:10:56','','','UNICODE',90),(748,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',90),(749,'','@ENTITY',0,'','org.motechproject.tasks.domain.LookupFieldsParameter','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',91),(750,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',91),(751,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskError','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',92),(752,'\0','args',1,'\0',NULL,'2015-04-23 15:10:56','','Args','2015-04-23 15:10:56','','','LIST',92),(753,'\0','message',2,'\0',NULL,'2015-04-23 15:10:56','','Message','2015-04-23 15:10:56','','','UNICODE',92),(754,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',92),(755,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskError','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',93),(756,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',93),(757,'\0','args',2,'\0',NULL,'2015-04-23 15:10:56','','Args','2015-04-23 15:10:56','','','LIST',93),(758,'\0','message',3,'\0',NULL,'2015-04-23 15:10:56','','Message','2015-04-23 15:10:56','','','UNICODE',93),(759,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',93),(760,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskError','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',94),(761,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',94),(762,'','@ENTITY',0,'','org.motechproject.tasks.domain.Filter','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',95),(763,'\0','expression',1,'\0',NULL,'2015-04-23 15:10:56','','Expression','2015-04-23 15:10:56','','','UNICODE',95),(764,'\0','operator',2,'\0',NULL,'2015-04-23 15:10:56','','Operator','2015-04-23 15:10:56','','','UNICODE',95),(765,'\0','type',3,'\0',NULL,'2015-04-23 15:10:56','','Type','2015-04-23 15:10:56','','','UNICODE',95),(766,'\0','negationOperator',4,'',NULL,'2015-04-23 15:10:56','','Negation Operator','2015-04-23 15:10:56','','','BOOLEAN',95),(767,'\0','key',5,'\0',NULL,'2015-04-23 15:10:56','','Key','2015-04-23 15:10:56','','','UNICODE',95),(768,'\0','displayName',6,'\0',NULL,'2015-04-23 15:10:56','','Display Name','2015-04-23 15:10:56','','','UNICODE',95),(769,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',95),(770,'','@ENTITY',0,'','org.motechproject.tasks.domain.Filter','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',96),(771,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',96),(772,'\0','expression',2,'\0',NULL,'2015-04-23 15:10:56','','Expression','2015-04-23 15:10:56','','','UNICODE',96),(773,'\0','operator',3,'\0',NULL,'2015-04-23 15:10:56','','Operator','2015-04-23 15:10:56','','','UNICODE',96),(774,'\0','type',4,'\0',NULL,'2015-04-23 15:10:56','','Type','2015-04-23 15:10:56','','','UNICODE',96),(775,'\0','negationOperator',5,'\0',NULL,'2015-04-23 15:10:56','','Negation Operator','2015-04-23 15:10:56','','','BOOLEAN',96),(776,'\0','key',6,'\0',NULL,'2015-04-23 15:10:56','','Key','2015-04-23 15:10:56','','','UNICODE',96),(777,'\0','displayName',7,'\0',NULL,'2015-04-23 15:10:56','','Display Name','2015-04-23 15:10:56','','','UNICODE',96),(778,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',96),(779,'','@ENTITY',0,'','org.motechproject.tasks.domain.Filter','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',97),(780,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',97),(781,'','@ENTITY',0,'','org.motechproject.email.domain.EmailRecord','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',98),(782,'\0','subject',1,'\0',NULL,'2015-04-23 15:10:56','','Subject','2015-04-23 15:10:56','','','UNICODE',98),(783,'\0','fromAddress',2,'\0',NULL,'2015-04-23 15:10:56','','From Address','2015-04-23 15:10:56','','','UNICODE',98),(784,'\0','deliveryStatus',3,'',NULL,'2015-04-23 15:10:56','','Delivery Status','2015-04-23 15:10:56','','','UNICODE',98),(785,'\0','message',4,'\0',NULL,'2015-04-23 15:10:56','','Message','2015-04-23 15:10:56','','','UNICODE',98),(786,'\0','toAddress',5,'',NULL,'2015-04-23 15:10:56','','To Address','2015-04-23 15:10:56','','','UNICODE',98),(787,'\0','deliveryTime',6,'',NULL,'2015-04-23 15:10:56','','Delivery Time','2015-04-23 15:10:56','','','DATE',98),(788,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',98),(789,'','@ENTITY',0,'','org.motechproject.email.domain.EmailRecord','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',99),(790,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',99),(791,'\0','subject',2,'\0',NULL,'2015-04-23 15:10:56','','Subject','2015-04-23 15:10:56','','','UNICODE',99),(792,'\0','fromAddress',3,'\0',NULL,'2015-04-23 15:10:56','','From Address','2015-04-23 15:10:56','','','UNICODE',99),(793,'\0','deliveryStatus',4,'\0',NULL,'2015-04-23 15:10:56','','Delivery Status','2015-04-23 15:10:56','','','UNICODE',99),(794,'\0','message',5,'\0',NULL,'2015-04-23 15:10:56','','Message','2015-04-23 15:10:56','','','UNICODE',99),(795,'\0','toAddress',6,'\0',NULL,'2015-04-23 15:10:56','','To Address','2015-04-23 15:10:56','','','UNICODE',99),(796,'\0','deliveryTime',7,'\0',NULL,'2015-04-23 15:10:56','','Delivery Time','2015-04-23 15:10:56','','','DATE',99),(797,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',99),(798,'','@ENTITY',0,'','org.motechproject.email.domain.EmailRecord','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',100),(799,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',100),(800,'','@ENTITY',0,'','org.motechproject.admin.domain.NotificationRule','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',101),(801,'\0','actionType',1,'',NULL,'2015-04-23 15:10:56','','Action Type','2015-04-23 15:10:56','','','UNICODE',101),(802,'\0','level',2,'',NULL,'2015-04-23 15:10:56','','Level','2015-04-23 15:10:56','','','UNICODE',101),(803,'\0','moduleName',3,'\0',NULL,'2015-04-23 15:10:56','','Module Name','2015-04-23 15:10:56','','','UNICODE',101),(804,'\0','recipient',4,'',NULL,'2015-04-23 15:10:56','','Recipient','2015-04-23 15:10:56','','','UNICODE',101),(805,'\0','owner',5,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',101),(806,'','@ENTITY',0,'','org.motechproject.admin.domain.NotificationRule','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',102),(807,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',102),(808,'\0','actionType',2,'\0',NULL,'2015-04-23 15:10:56','','Action Type','2015-04-23 15:10:56','','','UNICODE',102),(809,'\0','level',3,'\0',NULL,'2015-04-23 15:10:56','','Level','2015-04-23 15:10:56','','','UNICODE',102),(810,'\0','moduleName',4,'\0',NULL,'2015-04-23 15:10:56','','Module Name','2015-04-23 15:10:56','','','UNICODE',102),(811,'\0','recipient',5,'\0',NULL,'2015-04-23 15:10:56','','Recipient','2015-04-23 15:10:56','','','UNICODE',102),(812,'\0','owner',6,'\0',NULL,'2015-04-23 15:10:56','','Owner','2015-04-23 15:10:56','','','UNICODE',102),(813,'','@ENTITY',0,'','org.motechproject.admin.domain.NotificationRule','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',103),(814,'\0','id',1,'',NULL,'2015-04-23 15:10:56','','Id','2015-04-23 15:10:56','','','LONG',103),(815,'','@ENTITY',0,'','org.motechproject.admin.domain.StatusMessage','2015-04-23 15:10:56','','Entity Class','2015-04-23 15:10:56','','','UNICODE',104),(816,'\0','moduleName',1,'',NULL,'2015-04-23 15:10:56','','Module Name','2015-04-23 15:10:56','','','UNICODE',104),(817,'\0','text',2,'',NULL,'2015-04-23 15:10:56','','Text','2015-04-23 15:10:56','','','UNICODE',104),(818,'\0','intStrMap',3,'\0',NULL,'2015-04-23 15:10:56','','Int Str Map','2015-04-23 15:10:56','','','MAP',104),(819,'\0','date',4,'\0',NULL,'2015-04-23 15:10:56','','Date','2015-04-23 15:10:56','','','DATE',104),(820,'\0','strStrMap',5,'\0',NULL,'2015-04-23 15:10:56','','Str Str Map','2015-04-23 15:10:56','','','MAP',104),(821,'\0','strLongMap',6,'\0',NULL,'2015-04-23 15:10:56','','Str Long Map','2015-04-23 15:10:57','','','MAP',104),(822,'\0','strObjectMap',7,'\0',NULL,'2015-04-23 15:10:57','','Str Object Map','2015-04-23 15:10:57','','','MAP',104),(823,'\0','level',8,'',NULL,'2015-04-23 15:10:57','','Level','2015-04-23 15:10:57','','','UNICODE',104),(824,'\0','timeout',9,'\0',NULL,'2015-04-23 15:10:57','','Timeout','2015-04-23 15:10:57','','','DATE',104),(825,'\0','owner',10,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',104),(826,'','@ENTITY',0,'','org.motechproject.admin.domain.StatusMessage','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',105),(827,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',105),(828,'\0','moduleName',2,'\0',NULL,'2015-04-23 15:10:57','','Module Name','2015-04-23 15:10:57','','','UNICODE',105),(829,'\0','text',3,'\0',NULL,'2015-04-23 15:10:57','','Text','2015-04-23 15:10:57','','','UNICODE',105),(830,'\0','intStrMap',4,'\0',NULL,'2015-04-23 15:10:57','','Int Str Map','2015-04-23 15:10:57','','','MAP',105),(831,'\0','date',5,'\0',NULL,'2015-04-23 15:10:57','','Date','2015-04-23 15:10:57','','','DATE',105),(832,'\0','strStrMap',6,'\0',NULL,'2015-04-23 15:10:57','','Str Str Map','2015-04-23 15:10:57','','','MAP',105),(833,'\0','strLongMap',7,'\0',NULL,'2015-04-23 15:10:57','','Str Long Map','2015-04-23 15:10:57','','','MAP',105),(834,'\0','strObjectMap',8,'\0',NULL,'2015-04-23 15:10:57','','Str Object Map','2015-04-23 15:10:57','','','MAP',105),(835,'\0','level',9,'\0',NULL,'2015-04-23 15:10:57','','Level','2015-04-23 15:10:57','','','UNICODE',105),(836,'\0','timeout',10,'\0',NULL,'2015-04-23 15:10:57','','Timeout','2015-04-23 15:10:57','','','DATE',105),(837,'\0','owner',11,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',105),(838,'','@ENTITY',0,'','org.motechproject.admin.domain.StatusMessage','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',106),(839,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',106),(840,'','@ENTITY',0,'','org.motechproject.tasks.domain.ActionParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',107),(841,'\0','order',1,'\0',NULL,'2015-04-23 15:10:57','','Order','2015-04-23 15:10:57','','','INTEGER',107),(842,'\0','hidden',2,'\0',NULL,'2015-04-23 15:10:57','','Hidden','2015-04-23 15:10:57','','','BOOLEAN',107),(843,'\0','required',3,'\0',NULL,'2015-04-23 15:10:57','','Required','2015-04-23 15:10:57','','','BOOLEAN',107),(844,'\0','type',4,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',107),(845,'\0','value',5,'\0',NULL,'2015-04-23 15:10:57','','Value','2015-04-23 15:10:57','','','UNICODE',107),(846,'\0','key',6,'\0',NULL,'2015-04-23 15:10:57','','Key','2015-04-23 15:10:57','','','UNICODE',107),(847,'\0','displayName',7,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',107),(848,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',107),(849,'','@ENTITY',0,'','org.motechproject.tasks.domain.ActionParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',108),(850,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',108),(851,'\0','order',2,'\0',NULL,'2015-04-23 15:10:57','','Order','2015-04-23 15:10:57','','','INTEGER',108),(852,'\0','hidden',3,'\0',NULL,'2015-04-23 15:10:57','','Hidden','2015-04-23 15:10:57','','','BOOLEAN',108),(853,'\0','required',4,'\0',NULL,'2015-04-23 15:10:57','','Required','2015-04-23 15:10:57','','','BOOLEAN',108),(854,'\0','type',5,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',108),(855,'\0','value',6,'\0',NULL,'2015-04-23 15:10:57','','Value','2015-04-23 15:10:57','','','UNICODE',108),(856,'\0','key',7,'\0',NULL,'2015-04-23 15:10:57','','Key','2015-04-23 15:10:57','','','UNICODE',108),(857,'\0','displayName',8,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',108),(858,'\0','owner',9,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',108),(859,'','@ENTITY',0,'','org.motechproject.tasks.domain.ActionParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',109),(860,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',109),(861,'','@ENTITY',0,'','org.motechproject.tasks.domain.EventParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',110),(862,'\0','eventKey',1,'\0',NULL,'2015-04-23 15:10:57','','Event Key','2015-04-23 15:10:57','','','UNICODE',110),(863,'\0','type',2,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',110),(864,'\0','displayName',3,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',110),(865,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',110),(866,'','@ENTITY',0,'','org.motechproject.tasks.domain.EventParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',111),(867,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',111),(868,'\0','eventKey',2,'\0',NULL,'2015-04-23 15:10:57','','Event Key','2015-04-23 15:10:57','','','UNICODE',111),(869,'\0','type',3,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',111),(870,'\0','displayName',4,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',111),(871,'\0','owner',5,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',111),(872,'','@ENTITY',0,'','org.motechproject.tasks.domain.EventParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',112),(873,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',112),(874,'','@ENTITY',0,'','org.motechproject.tasks.domain.FieldParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',113),(875,'\0','fieldKey',1,'\0',NULL,'2015-04-23 15:10:57','','Field Key','2015-04-23 15:10:57','','','UNICODE',113),(876,'\0','type',2,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',113),(877,'\0','displayName',3,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',113),(878,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',113),(879,'','@ENTITY',0,'','org.motechproject.tasks.domain.FieldParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',114),(880,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',114),(881,'\0','fieldKey',2,'\0',NULL,'2015-04-23 15:10:57','','Field Key','2015-04-23 15:10:57','','','UNICODE',114),(882,'\0','type',3,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',114),(883,'\0','displayName',4,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',114),(884,'\0','owner',5,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',114),(885,'','@ENTITY',0,'','org.motechproject.tasks.domain.FieldParameter','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',115),(886,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',115),(887,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskDataProviderObject','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',116),(888,'\0','lookupFields',1,'\0',NULL,'2015-04-23 15:10:57','','Lookup Fields','2015-04-23 15:10:57','','','LIST',116),(889,'\0','type',2,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',116),(890,'\0','fields',3,'\0',NULL,'2015-04-23 15:10:57','','Fields','2015-04-23 15:10:57','','','LIST',116),(891,'\0','displayName',4,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',116),(892,'\0','owner',5,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',116),(893,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskDataProviderObject','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',117),(894,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',117),(895,'\0','lookupFields',2,'\0',NULL,'2015-04-23 15:10:57','','Lookup Fields','2015-04-23 15:10:57','','','LIST',117),(896,'\0','type',3,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',117),(897,'\0','fields',4,'\0',NULL,'2015-04-23 15:10:57','','Fields','2015-04-23 15:10:57','','','LIST',117),(898,'\0','displayName',5,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',117),(899,'\0','owner',6,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',117),(900,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskDataProviderObject','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',118),(901,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',118),(902,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskDataProvider','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',119),(903,'\0','name',1,'',NULL,'2015-04-23 15:10:57','','Name','2015-04-23 15:10:57','','','UNICODE',119),(904,'\0','objects',2,'\0',NULL,'2015-04-23 15:10:57','','Objects','2015-04-23 15:10:57','','','LIST',119),(905,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',119),(906,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskDataProvider','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',120),(907,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',120),(908,'\0','name',2,'\0',NULL,'2015-04-23 15:10:57','','Name','2015-04-23 15:10:57','','','UNICODE',120),(909,'\0','objects',3,'\0',NULL,'2015-04-23 15:10:57','','Objects','2015-04-23 15:10:57','','','LIST',120),(910,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',120),(911,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskDataProvider','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',121),(912,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',121),(913,'','@ENTITY',0,'','org.motechproject.tasks.domain.FilterSet','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',122),(914,'\0','order',1,'\0',NULL,'2015-04-23 15:10:57','','Order','2015-04-23 15:10:57','','','INTEGER',122),(915,'\0','operator',2,'\0',NULL,'2015-04-23 15:10:57','','Operator','2015-04-23 15:10:57','','','UNICODE',122),(916,'\0','filters',3,'\0',NULL,'2015-04-23 15:10:57','','Filters','2015-04-23 15:10:57','','','LIST',122),(917,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',122),(918,'','@ENTITY',0,'','org.motechproject.tasks.domain.FilterSet','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',123),(919,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',123),(920,'\0','order',2,'\0',NULL,'2015-04-23 15:10:57','','Order','2015-04-23 15:10:57','','','INTEGER',123),(921,'\0','operator',3,'\0',NULL,'2015-04-23 15:10:57','','Operator','2015-04-23 15:10:57','','','UNICODE',123),(922,'\0','filters',4,'\0',NULL,'2015-04-23 15:10:57','','Filters','2015-04-23 15:10:57','','','LIST',123),(923,'\0','owner',5,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',123),(924,'','@ENTITY',0,'','org.motechproject.tasks.domain.FilterSet','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',124),(925,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',124),(926,'','@ENTITY',0,'','org.motechproject.tasks.domain.ActionEvent','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',125),(927,'\0','subject',1,'\0',NULL,'2015-04-23 15:10:57','','Subject','2015-04-23 15:10:57','','','UNICODE',125),(928,'\0','serviceMethod',2,'\0',NULL,'2015-04-23 15:10:57','','Service Method','2015-04-23 15:10:57','','','UNICODE',125),(929,'\0','description',3,'\0',NULL,'2015-04-23 15:10:57','','Description','2015-04-23 15:10:57','','','UNICODE',125),(930,'\0','serviceMethodCallManner',4,'\0',NULL,'2015-04-23 15:10:57','','Service Method Call Manner','2015-04-23 15:10:57','','','UNICODE',125),(931,'\0','serviceInterface',5,'\0',NULL,'2015-04-23 15:10:57','','Service Interface','2015-04-23 15:10:57','','','UNICODE',125),(932,'\0','actionParameters',6,'\0',NULL,'2015-04-23 15:10:57','','Action Parameters','2015-04-23 15:10:57','','','LIST',125),(933,'\0','name',7,'\0',NULL,'2015-04-23 15:10:57','','Name','2015-04-23 15:10:57','','','UNICODE',125),(934,'\0','displayName',8,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',125),(935,'\0','owner',9,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',125),(936,'','@ENTITY',0,'','org.motechproject.tasks.domain.ActionEvent','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',126),(937,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',126),(938,'\0','subject',2,'\0',NULL,'2015-04-23 15:10:57','','Subject','2015-04-23 15:10:57','','','UNICODE',126),(939,'\0','serviceMethod',3,'\0',NULL,'2015-04-23 15:10:57','','Service Method','2015-04-23 15:10:57','','','UNICODE',126),(940,'\0','description',4,'\0',NULL,'2015-04-23 15:10:57','','Description','2015-04-23 15:10:57','','','UNICODE',126),(941,'\0','serviceMethodCallManner',5,'\0',NULL,'2015-04-23 15:10:57','','Service Method Call Manner','2015-04-23 15:10:57','','','UNICODE',126),(942,'\0','serviceInterface',6,'\0',NULL,'2015-04-23 15:10:57','','Service Interface','2015-04-23 15:10:57','','','UNICODE',126),(943,'\0','actionParameters',7,'\0',NULL,'2015-04-23 15:10:57','','Action Parameters','2015-04-23 15:10:57','','','LIST',126),(944,'\0','name',8,'\0',NULL,'2015-04-23 15:10:57','','Name','2015-04-23 15:10:57','','','UNICODE',126),(945,'\0','displayName',9,'\0',NULL,'2015-04-23 15:10:57','','Display Name','2015-04-23 15:10:57','','','UNICODE',126),(946,'\0','owner',10,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',126),(947,'','@ENTITY',0,'','org.motechproject.tasks.domain.ActionEvent','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',127),(948,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',127),(949,'','@ENTITY',0,'','org.motechproject.tasks.domain.DataSource','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',128),(950,'\0','lookup',1,'\0',NULL,'2015-04-23 15:10:57','','Lookup','2015-04-23 15:10:57','','','LIST',128),(951,'\0','order',2,'\0',NULL,'2015-04-23 15:10:57','','Order','2015-04-23 15:10:57','','','INTEGER',128),(952,'\0','objectId',3,'\0',NULL,'2015-04-23 15:10:57','','Object Id','2015-04-23 15:10:57','','','LONG',128),(953,'\0','failIfDataNotFound',4,'',NULL,'2015-04-23 15:10:57','','Fail If Data Not Found','2015-04-23 15:10:57','','','BOOLEAN',128),(954,'\0','type',5,'\0',NULL,'2015-04-23 15:10:57','','Type','2015-04-23 15:10:57','','','UNICODE',128),(955,'\0','providerId',6,'\0',NULL,'2015-04-23 15:10:57','','Provider Id','2015-04-23 15:10:57','','','LONG',128),(956,'\0','providerName',7,'\0',NULL,'2015-04-23 15:10:57','','Provider Name','2015-04-23 15:10:57','','','UNICODE',128),(957,'\0','name',8,'\0',NULL,'2015-04-23 15:10:57','','Name','2015-04-23 15:10:57','','','UNICODE',128),(958,'\0','owner',9,'\0',NULL,'2015-04-23 15:10:57','','Owner','2015-04-23 15:10:57','','','UNICODE',128),(959,'','@ENTITY',0,'','org.motechproject.tasks.domain.DataSource','2015-04-23 15:10:57','','Entity Class','2015-04-23 15:10:57','','','UNICODE',129),(960,'\0','id',1,'',NULL,'2015-04-23 15:10:57','','Id','2015-04-23 15:10:57','','','LONG',129),(961,'\0','lookup',2,'\0',NULL,'2015-04-23 15:10:57','','Lookup','2015-04-23 15:10:58','','','LIST',129),(962,'\0','order',3,'\0',NULL,'2015-04-23 15:10:58','','Order','2015-04-23 15:10:58','','','INTEGER',129),(963,'\0','objectId',4,'\0',NULL,'2015-04-23 15:10:58','','Object Id','2015-04-23 15:10:58','','','LONG',129),(964,'\0','failIfDataNotFound',5,'\0',NULL,'2015-04-23 15:10:58','','Fail If Data Not Found','2015-04-23 15:10:58','','','BOOLEAN',129),(965,'\0','type',6,'\0',NULL,'2015-04-23 15:10:58','','Type','2015-04-23 15:10:58','','','UNICODE',129),(966,'\0','providerId',7,'\0',NULL,'2015-04-23 15:10:58','','Provider Id','2015-04-23 15:10:58','','','LONG',129),(967,'\0','providerName',8,'\0',NULL,'2015-04-23 15:10:58','','Provider Name','2015-04-23 15:10:58','','','UNICODE',129),(968,'\0','name',9,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',129),(969,'\0','owner',10,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',129),(970,'','@ENTITY',0,'','org.motechproject.tasks.domain.DataSource','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',130),(971,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',130),(972,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskConfig','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',131),(973,'\0','filters',1,'\0',NULL,'2015-04-23 15:10:58','','Filters','2015-04-23 15:10:58','','','LIST',131),(974,'\0','dataSources',2,'\0',NULL,'2015-04-23 15:10:58','','Data Sources','2015-04-23 15:10:58','','','LIST',131),(975,'\0','owner',3,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',131),(976,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskConfig','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',132),(977,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',132),(978,'\0','filters',2,'\0',NULL,'2015-04-23 15:10:58','','Filters','2015-04-23 15:10:58','','','LIST',132),(979,'\0','dataSources',3,'\0',NULL,'2015-04-23 15:10:58','','Data Sources','2015-04-23 15:10:58','','','LIST',132),(980,'\0','owner',4,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',132),(981,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskConfig','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',133),(982,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',133),(983,'','@ENTITY',0,'','org.motechproject.tasks.domain.TriggerEvent','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',134),(984,'\0','subject',1,'\0',NULL,'2015-04-23 15:10:58','','Subject','2015-04-23 15:10:58','','','UNICODE',134),(985,'\0','description',2,'\0',NULL,'2015-04-23 15:10:58','','Description','2015-04-23 15:10:58','','','UNICODE',134),(986,'\0','eventParameters',3,'\0',NULL,'2015-04-23 15:10:58','','Event Parameters','2015-04-23 15:10:58','','','LIST',134),(987,'\0','triggerListenerSubject',4,'\0',NULL,'2015-04-23 15:10:58','','Trigger Listener Subject','2015-04-23 15:10:58','','','UNICODE',134),(988,'\0','name',5,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',134),(989,'\0','displayName',6,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',134),(990,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',134),(991,'','@ENTITY',0,'','org.motechproject.tasks.domain.TriggerEvent','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',135),(992,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',135),(993,'\0','subject',2,'\0',NULL,'2015-04-23 15:10:58','','Subject','2015-04-23 15:10:58','','','UNICODE',135),(994,'\0','description',3,'\0',NULL,'2015-04-23 15:10:58','','Description','2015-04-23 15:10:58','','','UNICODE',135),(995,'\0','eventParameters',4,'\0',NULL,'2015-04-23 15:10:58','','Event Parameters','2015-04-23 15:10:58','','','LIST',135),(996,'\0','triggerListenerSubject',5,'\0',NULL,'2015-04-23 15:10:58','','Trigger Listener Subject','2015-04-23 15:10:58','','','UNICODE',135),(997,'\0','name',6,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',135),(998,'\0','displayName',7,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',135),(999,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',135),(1000,'','@ENTITY',0,'','org.motechproject.tasks.domain.TriggerEvent','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',136),(1001,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',136),(1002,'','@ENTITY',0,'','org.motechproject.tasks.domain.Channel','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',137),(1003,'\0','description',1,'\0',NULL,'2015-04-23 15:10:58','','Description','2015-04-23 15:10:58','','','UNICODE',137),(1004,'\0','moduleVersion',2,'\0',NULL,'2015-04-23 15:10:58','','Module Version','2015-04-23 15:10:58','','','UNICODE',137),(1005,'\0','triggerTaskEvents',3,'\0',NULL,'2015-04-23 15:10:58','','Trigger Task Events','2015-04-23 15:10:58','','','LIST',137),(1006,'\0','moduleName',4,'\0',NULL,'2015-04-23 15:10:58','','Module Name','2015-04-23 15:10:58','','','UNICODE',137),(1007,'\0','displayName',5,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',137),(1008,'\0','actionTaskEvents',6,'\0',NULL,'2015-04-23 15:10:58','','Action Task Events','2015-04-23 15:10:58','','','LIST',137),(1009,'\0','owner',7,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',137),(1010,'','@ENTITY',0,'','org.motechproject.tasks.domain.Channel','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',138),(1011,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',138),(1012,'\0','description',2,'\0',NULL,'2015-04-23 15:10:58','','Description','2015-04-23 15:10:58','','','UNICODE',138),(1013,'\0','moduleVersion',3,'\0',NULL,'2015-04-23 15:10:58','','Module Version','2015-04-23 15:10:58','','','UNICODE',138),(1014,'\0','triggerTaskEvents',4,'\0',NULL,'2015-04-23 15:10:58','','Trigger Task Events','2015-04-23 15:10:58','','','LIST',138),(1015,'\0','moduleName',5,'\0',NULL,'2015-04-23 15:10:58','','Module Name','2015-04-23 15:10:58','','','UNICODE',138),(1016,'\0','displayName',6,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',138),(1017,'\0','actionTaskEvents',7,'\0',NULL,'2015-04-23 15:10:58','','Action Task Events','2015-04-23 15:10:58','','','LIST',138),(1018,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',138),(1019,'','@ENTITY',0,'','org.motechproject.tasks.domain.Channel','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',139),(1020,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',139),(1021,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskTriggerInformation','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',140),(1022,'\0','subject',1,'\0',NULL,'2015-04-23 15:10:58','','Subject','2015-04-23 15:10:58','','','UNICODE',140),(1023,'\0','triggerListenerSubject',2,'\0',NULL,'2015-04-23 15:10:58','','Trigger Listener Subject','2015-04-23 15:10:58','','','UNICODE',140),(1024,'\0','moduleName',3,'\0',NULL,'2015-04-23 15:10:58','','Module Name','2015-04-23 15:10:58','','','UNICODE',140),(1025,'\0','displayName',4,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',140),(1026,'\0','channelName',5,'\0',NULL,'2015-04-23 15:10:58','','Channel Name','2015-04-23 15:10:58','','','UNICODE',140),(1027,'\0','moduleVersion',6,'\0',NULL,'2015-04-23 15:10:58','','Module Version','2015-04-23 15:10:58','','','UNICODE',140),(1028,'\0','name',7,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',140),(1029,'\0','owner',8,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',140),(1030,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskTriggerInformation','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',141),(1031,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',141),(1032,'\0','subject',2,'\0',NULL,'2015-04-23 15:10:58','','Subject','2015-04-23 15:10:58','','','UNICODE',141),(1033,'\0','triggerListenerSubject',3,'\0',NULL,'2015-04-23 15:10:58','','Trigger Listener Subject','2015-04-23 15:10:58','','','UNICODE',141),(1034,'\0','moduleName',4,'\0',NULL,'2015-04-23 15:10:58','','Module Name','2015-04-23 15:10:58','','','UNICODE',141),(1035,'\0','displayName',5,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',141),(1036,'\0','channelName',6,'\0',NULL,'2015-04-23 15:10:58','','Channel Name','2015-04-23 15:10:58','','','UNICODE',141),(1037,'\0','moduleVersion',7,'\0',NULL,'2015-04-23 15:10:58','','Module Version','2015-04-23 15:10:58','','','UNICODE',141),(1038,'\0','name',8,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',141),(1039,'\0','owner',9,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',141),(1040,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskTriggerInformation','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',142),(1041,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',142),(1042,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskActionInformation','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',143),(1043,'\0','subject',1,'\0',NULL,'2015-04-23 15:10:58','','Subject','2015-04-23 15:10:58','','','UNICODE',143),(1044,'\0','serviceMethod',2,'\0',NULL,'2015-04-23 15:10:58','','Service Method','2015-04-23 15:10:58','','','UNICODE',143),(1045,'\0','serviceInterface',3,'\0',NULL,'2015-04-23 15:10:58','','Service Interface','2015-04-23 15:10:58','','','UNICODE',143),(1046,'\0','moduleName',4,'\0',NULL,'2015-04-23 15:10:58','','Module Name','2015-04-23 15:10:58','','','UNICODE',143),(1047,'\0','displayName',5,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',143),(1048,'\0','channelName',6,'\0',NULL,'2015-04-23 15:10:58','','Channel Name','2015-04-23 15:10:58','','','UNICODE',143),(1049,'\0','moduleVersion',7,'\0',NULL,'2015-04-23 15:10:58','','Module Version','2015-04-23 15:10:58','','','UNICODE',143),(1050,'\0','values',8,'\0',NULL,'2015-04-23 15:10:58','','Values','2015-04-23 15:10:58','','','MAP',143),(1051,'\0','name',9,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',143),(1052,'\0','owner',10,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',143),(1053,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskActionInformation','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',144),(1054,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',144),(1055,'\0','subject',2,'\0',NULL,'2015-04-23 15:10:58','','Subject','2015-04-23 15:10:58','','','UNICODE',144),(1056,'\0','serviceMethod',3,'\0',NULL,'2015-04-23 15:10:58','','Service Method','2015-04-23 15:10:58','','','UNICODE',144),(1057,'\0','serviceInterface',4,'\0',NULL,'2015-04-23 15:10:58','','Service Interface','2015-04-23 15:10:58','','','UNICODE',144),(1058,'\0','moduleName',5,'\0',NULL,'2015-04-23 15:10:58','','Module Name','2015-04-23 15:10:58','','','UNICODE',144),(1059,'\0','displayName',6,'\0',NULL,'2015-04-23 15:10:58','','Display Name','2015-04-23 15:10:58','','','UNICODE',144),(1060,'\0','channelName',7,'\0',NULL,'2015-04-23 15:10:58','','Channel Name','2015-04-23 15:10:58','','','UNICODE',144),(1061,'\0','moduleVersion',8,'\0',NULL,'2015-04-23 15:10:58','','Module Version','2015-04-23 15:10:58','','','UNICODE',144),(1062,'\0','values',9,'\0',NULL,'2015-04-23 15:10:58','','Values','2015-04-23 15:10:58','','','MAP',144),(1063,'\0','name',10,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',144),(1064,'\0','owner',11,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',144),(1065,'','@ENTITY',0,'','org.motechproject.tasks.domain.TaskActionInformation','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',145),(1066,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',145),(1067,'','@ENTITY',0,'','org.motechproject.tasks.domain.Task','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',146),(1068,'\0','description',1,'\0',NULL,'2015-04-23 15:10:58','','Description','2015-04-23 15:10:58','','','UNICODE',146),(1069,'\0','actions',2,'\0',NULL,'2015-04-23 15:10:58','','Actions','2015-04-23 15:10:58','','','LIST',146),(1070,'\0','taskConfig',3,'\0',NULL,'2015-04-23 15:10:58','','Task Config','2015-04-23 15:10:58','','','LONG',146),(1071,'\0','enabled',4,'',NULL,'2015-04-23 15:10:58','','Enabled','2015-04-23 15:10:58','','','BOOLEAN',146),(1072,'\0','trigger',5,'\0',NULL,'2015-04-23 15:10:58','','Trigger','2015-04-23 15:10:58','','','LONG',146),(1073,'\0','hasRegisteredChannel',6,'',NULL,'2015-04-23 15:10:58','','Has Registered Channel','2015-04-23 15:10:58','','','BOOLEAN',146),(1074,'\0','validationErrors',7,'\0',NULL,'2015-04-23 15:10:58','','Validation Errors','2015-04-23 15:10:58','','','LIST',146),(1075,'\0','name',8,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',146),(1076,'\0','owner',9,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',146),(1077,'','@ENTITY',0,'','org.motechproject.tasks.domain.Task','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',147),(1078,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',147),(1079,'\0','description',2,'\0',NULL,'2015-04-23 15:10:58','','Description','2015-04-23 15:10:58','','','UNICODE',147),(1080,'\0','actions',3,'\0',NULL,'2015-04-23 15:10:58','','Actions','2015-04-23 15:10:58','','','LIST',147),(1081,'\0','taskConfig',4,'\0',NULL,'2015-04-23 15:10:58','','Task Config','2015-04-23 15:10:58','','','LONG',147),(1082,'\0','enabled',5,'\0',NULL,'2015-04-23 15:10:58','','Enabled','2015-04-23 15:10:58','','','BOOLEAN',147),(1083,'\0','trigger',6,'\0',NULL,'2015-04-23 15:10:58','','Trigger','2015-04-23 15:10:58','','','LONG',147),(1084,'\0','hasRegisteredChannel',7,'\0',NULL,'2015-04-23 15:10:58','','Has Registered Channel','2015-04-23 15:10:58','','','BOOLEAN',147),(1085,'\0','validationErrors',8,'\0',NULL,'2015-04-23 15:10:58','','Validation Errors','2015-04-23 15:10:58','','','LIST',147),(1086,'\0','name',9,'\0',NULL,'2015-04-23 15:10:58','','Name','2015-04-23 15:10:58','','','UNICODE',147),(1087,'\0','owner',10,'\0',NULL,'2015-04-23 15:10:58','','Owner','2015-04-23 15:10:58','','','UNICODE',147),(1088,'','@ENTITY',0,'','org.motechproject.tasks.domain.Task','2015-04-23 15:10:58','','Entity Class','2015-04-23 15:10:58','','','UNICODE',148),(1089,'\0','id',1,'',NULL,'2015-04-23 15:10:58','','Id','2015-04-23 15:10:58','','','LONG',148);
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONPARAMETER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_ACTIONPARAMETER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_ACTIONPARAMETER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_ACTIONPARAMETER__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `hidden` bit(1) DEFAULT NULL,
  `key` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `required` bit(1) DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `actionParameters_id_OWN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_ACTIONPARAMETER__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_ACTIONPARAMETER__TRASH_FK1` (`actionParameters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_ACTIONPARAMETER__TRASH_FK1` FOREIGN KEY (`actionParameters_id_OWN`) REFERENCES `MOTECH_TASKS_ACTIONEVENT__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_ACTIONPARAMETER__TRASH`
--

LOCK TABLES `MOTECH_TASKS_ACTIONPARAMETER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONPARAMETER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_ACTIONPARAMETER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_CHANNEL`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_CHANNEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_CHANNEL` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_CHANNEL_N50` (`id`),
  KEY `MOTECH_TASKS_CHANNEL_N49` (`moduleName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_CHANNEL`
--

LOCK TABLES `MOTECH_TASKS_CHANNEL` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_CHANNEL` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_CHANNEL` VALUES (1,'2015-04-23 15:10:50','',NULL,'scheduler','2015-04-23 15:10:50','','org.motechproject.motech-scheduler','0.26.0.SNAPSHOT',''),(2,'2015-04-23 15:10:51','',NULL,'email','2015-04-23 15:10:51','','org.motechproject.motech-platform-email','0.26.0.SNAPSHOT',''),(3,'2015-04-23 15:10:51','',NULL,'data-services','2015-04-23 15:10:51','','org.motechproject.motech-platform-dataservices-entities','0.26.0.SNAPSHOT','');
/*!40000 ALTER TABLE `MOTECH_TASKS_CHANNEL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_CHANNEL__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_CHANNEL__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_CHANNEL__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_CHANNEL__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_CHANNEL__TRASH`
--

LOCK TABLES `MOTECH_TASKS_CHANNEL__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_CHANNEL__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_CHANNEL__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_DATASOURCE`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_DATASOURCE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_DATASOURCE` (
  `id` bigint(20) NOT NULL,
  `failIfDataNotFound` bit(1) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `objectId` bigint(20) DEFAULT NULL,
  `providerId` bigint(20) DEFAULT NULL,
  `providerName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `dataSources_id_OWN` bigint(20) DEFAULT NULL,
  `dataSources_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_DATASOURCE_N49` (`id`),
  KEY `MOTECH_TASKS_DATASOURCE_FK1` (`dataSources_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_DATASOURCE_FK1` FOREIGN KEY (`dataSources_id_OWN`) REFERENCES `MOTECH_TASKS_TASKCONFIG` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_DATASOURCE`
--

LOCK TABLES `MOTECH_TASKS_DATASOURCE` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_DATASOURCE` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_DATASOURCE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_DATASOURCE__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_DATASOURCE__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_DATASOURCE__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `dataSource__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `dataSource__HistoryFromTrash` bit(1) DEFAULT NULL,
  `dataSource__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `failIfDataNotFound` bit(1) NOT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `objectId` bigint(20) DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `providerId` bigint(20) DEFAULT NULL,
  `providerName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `dataSources_id_OWN` bigint(20) DEFAULT NULL,
  `dataSources_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_DATASOURCE__HISTORY_N49` (`id`),
  KEY `MOTECH_TASKS_DATASOURCE__HISTORY_FK1` (`dataSources_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_DATASOURCE__HISTORY_FK1` FOREIGN KEY (`dataSources_id_OWN`) REFERENCES `MOTECH_TASKS_TASKCONFIG__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_DATASOURCE__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_DATASOURCE__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_DATASOURCE__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_DATASOURCE__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_DATASOURCE__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_DATASOURCE__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_DATASOURCE__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `failIfDataNotFound` bit(1) NOT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `objectId` bigint(20) DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `providerId` bigint(20) DEFAULT NULL,
  `providerName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `dataSources_id_OWN` bigint(20) DEFAULT NULL,
  `dataSources_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_DATASOURCE__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_DATASOURCE__TRASH_FK1` (`dataSources_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_DATASOURCE__TRASH_FK1` FOREIGN KEY (`dataSources_id_OWN`) REFERENCES `MOTECH_TASKS_TASKCONFIG__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_DATASOURCE__TRASH`
--

LOCK TABLES `MOTECH_TASKS_DATASOURCE__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_DATASOURCE__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_DATASOURCE__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_EVENTPARAMETER`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_EVENTPARAMETER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_EVENTPARAMETER` (
  `id` bigint(20) NOT NULL,
  `eventKey` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `eventParameters_id_OWN` bigint(20) DEFAULT NULL,
  `eventParameters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_EVENTPARAMETER_N49` (`id`),
  KEY `MOTECH_TASKS_EVENTPARAMETER_FK1` (`eventParameters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_EVENTPARAMETER_FK1` FOREIGN KEY (`eventParameters_id_OWN`) REFERENCES `MOTECH_TASKS_TRIGGEREVENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_EVENTPARAMETER`
--

LOCK TABLES `MOTECH_TASKS_EVENTPARAMETER` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_EVENTPARAMETER` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_EVENTPARAMETER` VALUES (401,'entity_name','2015-04-23 15:10:51','','Entity Name','2015-04-23 15:10:52','','','UNICODE',8,0),(402,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',8,1),(403,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',8,2),(404,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',9,0),(405,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',9,1),(406,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',9,2),(407,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',10,0),(408,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',10,1),(409,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',10,2),(410,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',11,0),(411,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',11,1),(412,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',11,2),(413,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',12,0),(414,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',12,1),(415,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',12,2),(416,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',13,0),(417,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',13,1),(418,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',13,2),(419,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',14,0),(420,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',14,1),(421,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',14,2),(422,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',15,0),(423,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',15,1),(424,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',15,2),(425,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',16,0),(426,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',16,1),(427,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',16,2),(428,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',17,0),(429,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',17,1),(430,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',17,2),(431,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',18,0),(432,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',18,1),(433,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',18,2),(434,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',19,0),(435,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',19,1),(436,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',19,2),(437,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',20,0),(438,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',20,1),(439,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',20,2),(440,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',21,0),(441,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',21,1),(442,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',21,2),(443,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',22,0),(444,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',22,1),(445,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',22,2),(446,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',23,0),(447,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',23,1),(448,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',23,2),(449,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',24,0),(450,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',24,1),(451,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',24,2),(452,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',25,0),(453,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',25,1),(454,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',25,2),(455,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',26,0),(456,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',26,1),(457,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',26,2),(458,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',27,0),(459,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',27,1),(460,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',27,2),(461,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',28,0),(462,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',28,1),(463,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',28,2),(464,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',29,0),(465,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',29,1),(466,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',29,2),(467,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',30,0),(468,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',30,1),(469,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',30,2),(470,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',31,0),(471,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',31,1),(472,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',31,2),(473,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',32,0),(474,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',32,1),(475,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',32,2),(476,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',33,0),(477,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',33,1),(478,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',33,2),(479,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',34,0),(480,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',34,1),(481,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',34,2),(482,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',35,0),(483,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',35,1),(484,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',35,2),(485,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',36,0),(486,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',36,1),(487,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',36,2),(488,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:52','','','UNICODE',37,0),(489,'entity_class','2015-04-23 15:10:52','','Entity Class','2015-04-23 15:10:52','','','UNICODE',37,1),(490,'object_id','2015-04-23 15:10:52','','Id','2015-04-23 15:10:52','','','LONG',37,2),(491,'entity_name','2015-04-23 15:10:52','','Entity Name','2015-04-23 15:10:53','','','UNICODE',38,0),(492,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',38,1),(493,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',38,2),(494,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',39,0),(495,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',39,1),(496,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',39,2),(497,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',40,0),(498,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',40,1),(499,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',40,2),(500,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',41,0),(501,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',41,1),(502,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',41,2),(503,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',42,0),(504,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',42,1),(505,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',42,2),(506,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',43,0),(507,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',43,1),(508,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',43,2),(509,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',44,0),(510,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',44,1),(511,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',44,2),(512,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',45,0),(513,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',45,1),(514,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',45,2),(515,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',46,0),(516,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',46,1),(517,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',46,2),(518,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',47,0),(519,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',47,1),(520,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',47,2),(521,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',48,0),(522,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',48,1),(523,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',48,2),(524,'entity_name','2015-04-23 15:10:53','','Entity Name','2015-04-23 15:10:53','','','UNICODE',49,0),(525,'entity_class','2015-04-23 15:10:53','','Entity Class','2015-04-23 15:10:53','','','UNICODE',49,1),(526,'object_id','2015-04-23 15:10:53','','Id','2015-04-23 15:10:53','','','LONG',49,2);
/*!40000 ALTER TABLE `MOTECH_TASKS_EVENTPARAMETER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_EVENTPARAMETER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_EVENTPARAMETER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_EVENTPARAMETER__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `eventKey` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `eventParameters_id_OWN` bigint(20) DEFAULT NULL,
  `eventParameters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_EVENTPARAMETER__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_EVENTPARAMETER__TRASH_FK1` (`eventParameters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_EVENTPARAMETER__TRASH_FK1` FOREIGN KEY (`eventParameters_id_OWN`) REFERENCES `MOTECH_TASKS_TRIGGEREVENT__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_EVENTPARAMETER__TRASH`
--

LOCK TABLES `MOTECH_TASKS_EVENTPARAMETER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_EVENTPARAMETER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_EVENTPARAMETER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FIELDPARAMETER`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FIELDPARAMETER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FIELDPARAMETER` (
  `id` bigint(20) NOT NULL,
  `fieldKey` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `fields_id_OWN` bigint(20) DEFAULT NULL,
  `fields_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FIELDPARAMETER_N49` (`id`),
  KEY `MOTECH_TASKS_FIELDPARAMETER_FK1` (`fields_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FIELDPARAMETER_FK1` FOREIGN KEY (`fields_id_OWN`) REFERENCES `MOTECH_TASKS_TASKDATAPROVIDEROBJECT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FIELDPARAMETER`
--

LOCK TABLES `MOTECH_TASKS_FIELDPARAMETER` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FIELDPARAMETER` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_FIELDPARAMETER` VALUES (1,'platformInitialized','2015-04-23 15:10:46','','Platform Initialized','2015-04-23 15:10:46','','','UNICODE',1,0),(2,'lastRun','2015-04-23 15:10:46','','Last Run','2015-04-23 15:10:46','','','UNICODE',1,1),(3,'platformSettings','2015-04-23 15:10:46','','Platform Settings','2015-04-23 15:10:46','','','UNICODE',1,2),(4,'filePath','2015-04-23 15:10:46','','File Path','2015-04-23 15:10:46','','','UNICODE',1,3),(5,'configFileChecksum','2015-04-23 15:10:46','','Config File Checksum','2015-04-23 15:10:46','','','UNICODE',1,4),(6,'modificationDate','2015-04-23 15:10:46','','Modification Date','2015-04-23 15:10:46','','','UNICODE',1,5),(7,'creationDate','2015-04-23 15:10:46','','Creation Date','2015-04-23 15:10:46','','','UNICODE',1,6),(8,'modifiedBy','2015-04-23 15:10:46','','Modified By','2015-04-23 15:10:46','','','UNICODE',1,7),(9,'owner','2015-04-23 15:10:46','','Owner','2015-04-23 15:10:46','','','UNICODE',1,8),(10,'creator','2015-04-23 15:10:46','','Created By','2015-04-23 15:10:46','','','UNICODE',1,9),(11,'id','2015-04-23 15:10:46','','Id','2015-04-23 15:10:46','','','UNICODE',1,10),(12,'properties','2015-04-23 15:10:47','','Properties','2015-04-23 15:10:47','','','UNICODE',2,0),(13,'raw','2015-04-23 15:10:47','','Raw','2015-04-23 15:10:47','','','UNICODE',2,1),(14,'version','2015-04-23 15:10:47','','Version','2015-04-23 15:10:47','','','UNICODE',2,2),(15,'filename','2015-04-23 15:10:47','','Filename','2015-04-23 15:10:47','','','UNICODE',2,3),(16,'bundle','2015-04-23 15:10:47','','Bundle','2015-04-23 15:10:47','','','UNICODE',2,4),(17,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',2,5),(18,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',2,6),(19,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',2,7),(20,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',2,8),(21,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',2,9),(22,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',2,10),(23,'bundleName','2015-04-23 15:10:47','','Bundle Name','2015-04-23 15:10:47','','','UNICODE',3,0),(24,'permissionName','2015-04-23 15:10:47','','Permission Name','2015-04-23 15:10:47','','','UNICODE',3,1),(25,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',3,2),(26,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',3,3),(27,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',3,4),(28,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',3,5),(29,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',3,6),(30,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',3,7),(31,'permissionNames','2015-04-23 15:10:47','','Permission Names','2015-04-23 15:10:47','','','UNICODE',4,0),(32,'roleName','2015-04-23 15:10:47','','Role Name','2015-04-23 15:10:47','','','UNICODE',4,1),(33,'deletable','2015-04-23 15:10:47','','Deletable','2015-04-23 15:10:47','','','UNICODE',4,2),(34,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',4,3),(35,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',4,4),(36,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',4,5),(37,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',4,6),(38,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',4,7),(39,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',4,8),(40,'priority','2015-04-23 15:10:47','','Priority','2015-04-23 15:10:47','','','UNICODE',5,0),(41,'protocol','2015-04-23 15:10:47','','Protocol','2015-04-23 15:10:47','','','UNICODE',5,1),(42,'userAccess','2015-04-23 15:10:47','','User Access','2015-04-23 15:10:47','','','UNICODE',5,2),(43,'pattern','2015-04-23 15:10:47','','Pattern','2015-04-23 15:10:47','','','UNICODE',5,3),(44,'rest','2015-04-23 15:10:47','','Rest','2015-04-23 15:10:47','','','UNICODE',5,4),(45,'methodsRequired','2015-04-23 15:10:47','','Methods Required','2015-04-23 15:10:47','','','UNICODE',5,5),(46,'supportedSchemes','2015-04-23 15:10:47','','Supported Schemes','2015-04-23 15:10:47','','','UNICODE',5,6),(47,'active','2015-04-23 15:10:47','','Active','2015-04-23 15:10:47','','','UNICODE',5,7),(48,'origin','2015-04-23 15:10:47','','Origin','2015-04-23 15:10:47','','','UNICODE',5,8),(49,'version','2015-04-23 15:10:47','','Version','2015-04-23 15:10:47','','','UNICODE',5,9),(50,'deleted','2015-04-23 15:10:47','','Deleted','2015-04-23 15:10:47','','','UNICODE',5,10),(51,'permissionAccess','2015-04-23 15:10:47','','Permission Access','2015-04-23 15:10:47','','','UNICODE',5,11),(52,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',5,12),(53,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',5,13),(54,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',5,14),(55,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',5,15),(56,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',5,16),(57,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',5,17),(58,'userName','2015-04-23 15:10:47','','User Name','2015-04-23 15:10:47','','','UNICODE',6,0),(59,'roles','2015-04-23 15:10:47','','Roles','2015-04-23 15:10:47','','','UNICODE',6,1),(60,'locale','2015-04-23 15:10:47','','Locale','2015-04-23 15:10:47','','','UNICODE',6,2),(61,'password','2015-04-23 15:10:47','','Password','2015-04-23 15:10:47','','','UNICODE',6,3),(62,'openId','2015-04-23 15:10:47','','Open Id','2015-04-23 15:10:47','','','UNICODE',6,4),(63,'externalId','2015-04-23 15:10:47','','External Id','2015-04-23 15:10:47','','','UNICODE',6,5),(64,'email','2015-04-23 15:10:47','','Email','2015-04-23 15:10:47','','','UNICODE',6,6),(65,'active','2015-04-23 15:10:47','','Active','2015-04-23 15:10:47','','','UNICODE',6,7),(66,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',6,8),(67,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',6,9),(68,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',6,10),(69,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',6,11),(70,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',6,12),(71,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',6,13),(72,'username','2015-04-23 15:10:47','','Username','2015-04-23 15:10:47','','','UNICODE',7,0),(73,'locale','2015-04-23 15:10:47','','Locale','2015-04-23 15:10:47','','','UNICODE',7,1),(74,'email','2015-04-23 15:10:47','','Email','2015-04-23 15:10:47','','','UNICODE',7,2),(75,'expirationDate','2015-04-23 15:10:47','','Expiration Date','2015-04-23 15:10:47','','','UNICODE',7,3),(76,'token','2015-04-23 15:10:47','','Token','2015-04-23 15:10:47','','','UNICODE',7,4),(77,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',7,5),(78,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',7,6),(79,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',7,7),(80,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',7,8),(81,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',7,9),(82,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',7,10),(83,'subject','2015-04-23 15:10:47','','Subject','2015-04-23 15:10:47','','','UNICODE',8,0),(84,'channelName','2015-04-23 15:10:47','','Channel Name','2015-04-23 15:10:47','','','UNICODE',8,1),(85,'moduleVersion','2015-04-23 15:10:47','','Module Version','2015-04-23 15:10:47','','','UNICODE',8,2),(86,'moduleName','2015-04-23 15:10:47','','Module Name','2015-04-23 15:10:47','','','UNICODE',8,3),(87,'name','2015-04-23 15:10:47','','Name','2015-04-23 15:10:47','','','UNICODE',8,4),(88,'displayName','2015-04-23 15:10:47','','Display Name','2015-04-23 15:10:47','','','UNICODE',8,5),(89,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',8,6),(90,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',8,7),(91,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',8,8),(92,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',8,9),(93,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',8,10),(94,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',8,11),(95,'order','2015-04-23 15:10:47','','Order','2015-04-23 15:10:47','','','UNICODE',9,0),(96,'hidden','2015-04-23 15:10:47','','Hidden','2015-04-23 15:10:47','','','UNICODE',9,1),(97,'required','2015-04-23 15:10:47','','Required','2015-04-23 15:10:47','','','UNICODE',9,2),(98,'type','2015-04-23 15:10:47','','Type','2015-04-23 15:10:47','','','UNICODE',9,3),(99,'value','2015-04-23 15:10:47','','Value','2015-04-23 15:10:47','','','UNICODE',9,4),(100,'key','2015-04-23 15:10:47','','Key','2015-04-23 15:10:47','','','UNICODE',9,5),(101,'displayName','2015-04-23 15:10:47','','Display Name','2015-04-23 15:10:47','','','UNICODE',9,6),(102,'modificationDate','2015-04-23 15:10:47','','Modification Date','2015-04-23 15:10:47','','','UNICODE',9,7),(103,'creationDate','2015-04-23 15:10:47','','Creation Date','2015-04-23 15:10:47','','','UNICODE',9,8),(104,'modifiedBy','2015-04-23 15:10:47','','Modified By','2015-04-23 15:10:47','','','UNICODE',9,9),(105,'owner','2015-04-23 15:10:47','','Owner','2015-04-23 15:10:47','','','UNICODE',9,10),(106,'creator','2015-04-23 15:10:47','','Created By','2015-04-23 15:10:47','','','UNICODE',9,11),(107,'id','2015-04-23 15:10:47','','Id','2015-04-23 15:10:47','','','UNICODE',9,12),(108,'value','2015-04-23 15:10:48','','Value','2015-04-23 15:10:48','','','UNICODE',10,0),(109,'field','2015-04-23 15:10:48','','Field','2015-04-23 15:10:48','','','UNICODE',10,1),(110,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',10,2),(111,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',10,3),(112,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',10,4),(113,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',10,5),(114,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',10,6),(115,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',10,7),(116,'eventKey','2015-04-23 15:10:48','','Event Key','2015-04-23 15:10:48','','','UNICODE',11,0),(117,'type','2015-04-23 15:10:48','','Type','2015-04-23 15:10:48','','','UNICODE',11,1),(118,'displayName','2015-04-23 15:10:48','','Display Name','2015-04-23 15:10:48','','','UNICODE',11,2),(119,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',11,3),(120,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',11,4),(121,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',11,5),(122,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',11,6),(123,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',11,7),(124,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',11,8),(125,'fieldKey','2015-04-23 15:10:48','','Field Key','2015-04-23 15:10:48','','','UNICODE',12,0),(126,'type','2015-04-23 15:10:48','','Type','2015-04-23 15:10:48','','','UNICODE',12,1),(127,'displayName','2015-04-23 15:10:48','','Display Name','2015-04-23 15:10:48','','','UNICODE',12,2),(128,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',12,3),(129,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',12,4),(130,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',12,5),(131,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',12,6),(132,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',12,7),(133,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',12,8),(134,'order','2015-04-23 15:10:48','','Order','2015-04-23 15:10:48','','','UNICODE',13,0),(135,'operator','2015-04-23 15:10:48','','Operator','2015-04-23 15:10:48','','','UNICODE',13,1),(136,'filters','2015-04-23 15:10:48','','Filters','2015-04-23 15:10:48','','','UNICODE',13,2),(137,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',13,3),(138,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',13,4),(139,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',13,5),(140,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',13,6),(141,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',13,7),(142,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',13,8),(143,'type','2015-04-23 15:10:48','','Type','2015-04-23 15:10:48','','','UNICODE',14,0),(144,'displayName','2015-04-23 15:10:48','','Display Name','2015-04-23 15:10:48','','','UNICODE',14,1),(145,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',14,2),(146,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',14,3),(147,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',14,4),(148,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',14,5),(149,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',14,6),(150,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',14,7),(151,'order','2015-04-23 15:10:48','','Order','2015-04-23 15:10:48','','','UNICODE',15,0),(152,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',15,1),(153,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',15,2),(154,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',15,3),(155,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',15,4),(156,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',15,5),(157,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',15,6),(158,'subject','2015-04-23 15:10:48','','Subject','2015-04-23 15:10:48','','','UNICODE',16,0),(159,'serviceMethod','2015-04-23 15:10:48','','Service Method','2015-04-23 15:10:48','','','UNICODE',16,1),(160,'description','2015-04-23 15:10:48','','Description','2015-04-23 15:10:48','','','UNICODE',16,2),(161,'serviceMethodCallManner','2015-04-23 15:10:48','','Service Method Call Manner','2015-04-23 15:10:48','','','UNICODE',16,3),(162,'serviceInterface','2015-04-23 15:10:48','','Service Interface','2015-04-23 15:10:48','','','UNICODE',16,4),(163,'actionParameters','2015-04-23 15:10:48','','Action Parameters','2015-04-23 15:10:48','','','UNICODE',16,5),(164,'name','2015-04-23 15:10:48','','Name','2015-04-23 15:10:48','','','UNICODE',16,6),(165,'displayName','2015-04-23 15:10:48','','Display Name','2015-04-23 15:10:48','','','UNICODE',16,7),(166,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',16,8),(167,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',16,9),(168,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',16,10),(169,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',16,11),(170,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',16,12),(171,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',16,13),(172,'filters','2015-04-23 15:10:48','','Filters','2015-04-23 15:10:48','','','UNICODE',17,0),(173,'dataSources','2015-04-23 15:10:48','','Data Sources','2015-04-23 15:10:48','','','UNICODE',17,1),(174,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',17,2),(175,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',17,3),(176,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',17,4),(177,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',17,5),(178,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',17,6),(179,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',17,7),(180,'fields','2015-04-23 15:10:48','','Fields','2015-04-23 15:10:48','','','UNICODE',18,0),(181,'task','2015-04-23 15:10:48','','Task','2015-04-23 15:10:48','','','UNICODE',18,1),(182,'activityType','2015-04-23 15:10:48','','Activity Type','2015-04-23 15:10:48','','','UNICODE',18,2),(183,'stackTraceElement','2015-04-23 15:10:48','','StackTrace element','2015-04-23 15:10:48','','','UNICODE',18,3),(184,'date','2015-04-23 15:10:48','','Date','2015-04-23 15:10:48','','','UNICODE',18,4),(185,'message','2015-04-23 15:10:48','','Message','2015-04-23 15:10:48','','','UNICODE',18,5),(186,'modificationDate','2015-04-23 15:10:48','','Modification Date','2015-04-23 15:10:48','','','UNICODE',18,6),(187,'creationDate','2015-04-23 15:10:48','','Creation Date','2015-04-23 15:10:48','','','UNICODE',18,7),(188,'modifiedBy','2015-04-23 15:10:48','','Modified By','2015-04-23 15:10:48','','','UNICODE',18,8),(189,'owner','2015-04-23 15:10:48','','Owner','2015-04-23 15:10:48','','','UNICODE',18,9),(190,'creator','2015-04-23 15:10:48','','Created By','2015-04-23 15:10:48','','','UNICODE',18,10),(191,'id','2015-04-23 15:10:48','','Id','2015-04-23 15:10:48','','','UNICODE',18,11),(192,'subject','2015-04-23 15:10:49','','Subject','2015-04-23 15:10:49','','','UNICODE',19,0),(193,'description','2015-04-23 15:10:49','','Description','2015-04-23 15:10:49','','','UNICODE',19,1),(194,'name','2015-04-23 15:10:49','','Name','2015-04-23 15:10:49','','','UNICODE',19,2),(195,'displayName','2015-04-23 15:10:49','','Display Name','2015-04-23 15:10:49','','','UNICODE',19,3),(196,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',19,4),(197,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',19,5),(198,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',19,6),(199,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',19,7),(200,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',19,8),(201,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',19,9),(202,'lookup','2015-04-23 15:10:49','','Lookup','2015-04-23 15:10:49','','','UNICODE',20,0),(203,'order','2015-04-23 15:10:49','','Order','2015-04-23 15:10:49','','','UNICODE',20,1),(204,'objectId','2015-04-23 15:10:49','','Object Id','2015-04-23 15:10:49','','','UNICODE',20,2),(205,'failIfDataNotFound','2015-04-23 15:10:49','','Fail If Data Not Found','2015-04-23 15:10:49','','','UNICODE',20,3),(206,'type','2015-04-23 15:10:49','','Type','2015-04-23 15:10:49','','','UNICODE',20,4),(207,'providerId','2015-04-23 15:10:49','','Provider Id','2015-04-23 15:10:49','','','UNICODE',20,5),(208,'providerName','2015-04-23 15:10:49','','Provider Name','2015-04-23 15:10:49','','','UNICODE',20,6),(209,'name','2015-04-23 15:10:49','','Name','2015-04-23 15:10:49','','','UNICODE',20,7),(210,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',20,8),(211,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',20,9),(212,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',20,10),(213,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',20,11),(214,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',20,12),(215,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',20,13),(216,'fields','2015-04-23 15:10:49','','Fields','2015-04-23 15:10:49','','','UNICODE',21,0),(217,'displayName','2015-04-23 15:10:49','','Display Name','2015-04-23 15:10:49','','','UNICODE',21,1),(218,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',21,2),(219,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',21,3),(220,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',21,4),(221,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',21,5),(222,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',21,6),(223,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',21,7),(224,'subject','2015-04-23 15:10:49','','Subject','2015-04-23 15:10:49','','','UNICODE',22,0),(225,'description','2015-04-23 15:10:49','','Description','2015-04-23 15:10:49','','','UNICODE',22,1),(226,'eventParameters','2015-04-23 15:10:49','','Event Parameters','2015-04-23 15:10:49','','','UNICODE',22,2),(227,'triggerListenerSubject','2015-04-23 15:10:49','','Trigger Listener Subject','2015-04-23 15:10:49','','','UNICODE',22,3),(228,'name','2015-04-23 15:10:49','','Name','2015-04-23 15:10:49','','','UNICODE',22,4),(229,'displayName','2015-04-23 15:10:49','','Display Name','2015-04-23 15:10:49','','','UNICODE',22,5),(230,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',22,6),(231,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',22,7),(232,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',22,8),(233,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',22,9),(234,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',22,10),(235,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',22,11),(236,'description','2015-04-23 15:10:49','','Description','2015-04-23 15:10:49','','','UNICODE',23,0),(237,'moduleVersion','2015-04-23 15:10:49','','Module Version','2015-04-23 15:10:49','','','UNICODE',23,1),(238,'triggerTaskEvents','2015-04-23 15:10:49','','Trigger Task Events','2015-04-23 15:10:49','','','UNICODE',23,2),(239,'moduleName','2015-04-23 15:10:49','','Module Name','2015-04-23 15:10:49','','','UNICODE',23,3),(240,'displayName','2015-04-23 15:10:49','','Display Name','2015-04-23 15:10:49','','','UNICODE',23,4),(241,'actionTaskEvents','2015-04-23 15:10:49','','Action Task Events','2015-04-23 15:10:49','','','UNICODE',23,5),(242,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',23,6),(243,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',23,7),(244,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',23,8),(245,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',23,9),(246,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',23,10),(247,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',23,11),(248,'args','2015-04-23 15:10:49','','Args','2015-04-23 15:10:49','','','UNICODE',24,0),(249,'message','2015-04-23 15:10:49','','Message','2015-04-23 15:10:49','','','UNICODE',24,1),(250,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',24,2),(251,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',24,3),(252,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',24,4),(253,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',24,5),(254,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',24,6),(255,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',24,7),(256,'description','2015-04-23 15:10:49','','Description','2015-04-23 15:10:49','','','UNICODE',25,0),(257,'actions','2015-04-23 15:10:49','','Actions','2015-04-23 15:10:49','','','UNICODE',25,1),(258,'taskConfig','2015-04-23 15:10:49','','Task Config','2015-04-23 15:10:49','','','UNICODE',25,2),(259,'enabled','2015-04-23 15:10:49','','Enabled','2015-04-23 15:10:49','','','UNICODE',25,3),(260,'trigger','2015-04-23 15:10:49','','Trigger','2015-04-23 15:10:49','','','UNICODE',25,4),(261,'hasRegisteredChannel','2015-04-23 15:10:49','','Has Registered Channel','2015-04-23 15:10:49','','','UNICODE',25,5),(262,'validationErrors','2015-04-23 15:10:49','','Validation Errors','2015-04-23 15:10:49','','','UNICODE',25,6),(263,'name','2015-04-23 15:10:49','','Name','2015-04-23 15:10:49','','','UNICODE',25,7),(264,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',25,8),(265,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',25,9),(266,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',25,10),(267,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',25,11),(268,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',25,12),(269,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',25,13),(270,'name','2015-04-23 15:10:49','','Name','2015-04-23 15:10:49','','','UNICODE',26,0),(271,'objects','2015-04-23 15:10:49','','Objects','2015-04-23 15:10:49','','','UNICODE',26,1),(272,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',26,2),(273,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',26,3),(274,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',26,4),(275,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',26,5),(276,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',26,6),(277,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',26,7),(278,'subject','2015-04-23 15:10:49','','Subject','2015-04-23 15:10:49','','','UNICODE',27,0),(279,'triggerListenerSubject','2015-04-23 15:10:49','','Trigger Listener Subject','2015-04-23 15:10:49','','','UNICODE',27,1),(280,'moduleName','2015-04-23 15:10:49','','Module Name','2015-04-23 15:10:49','','','UNICODE',27,2),(281,'displayName','2015-04-23 15:10:49','','Display Name','2015-04-23 15:10:49','','','UNICODE',27,3),(282,'channelName','2015-04-23 15:10:49','','Channel Name','2015-04-23 15:10:49','','','UNICODE',27,4),(283,'moduleVersion','2015-04-23 15:10:49','','Module Version','2015-04-23 15:10:49','','','UNICODE',27,5),(284,'name','2015-04-23 15:10:49','','Name','2015-04-23 15:10:49','','','UNICODE',27,6),(285,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',27,7),(286,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',27,8),(287,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',27,9),(288,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',27,10),(289,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',27,11),(290,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',27,12),(291,'expression','2015-04-23 15:10:49','','Expression','2015-04-23 15:10:49','','','UNICODE',28,0),(292,'operator','2015-04-23 15:10:49','','Operator','2015-04-23 15:10:49','','','UNICODE',28,1),(293,'type','2015-04-23 15:10:49','','Type','2015-04-23 15:10:49','','','UNICODE',28,2),(294,'negationOperator','2015-04-23 15:10:49','','Negation Operator','2015-04-23 15:10:49','','','UNICODE',28,3),(295,'key','2015-04-23 15:10:49','','Key','2015-04-23 15:10:49','','','UNICODE',28,4),(296,'displayName','2015-04-23 15:10:49','','Display Name','2015-04-23 15:10:49','','','UNICODE',28,5),(297,'modificationDate','2015-04-23 15:10:49','','Modification Date','2015-04-23 15:10:49','','','UNICODE',28,6),(298,'creationDate','2015-04-23 15:10:49','','Creation Date','2015-04-23 15:10:49','','','UNICODE',28,7),(299,'modifiedBy','2015-04-23 15:10:49','','Modified By','2015-04-23 15:10:49','','','UNICODE',28,8),(300,'owner','2015-04-23 15:10:49','','Owner','2015-04-23 15:10:49','','','UNICODE',28,9),(301,'creator','2015-04-23 15:10:49','','Created By','2015-04-23 15:10:49','','','UNICODE',28,10),(302,'id','2015-04-23 15:10:49','','Id','2015-04-23 15:10:49','','','UNICODE',28,11),(303,'subject','2015-04-23 15:10:49','','Subject','2015-04-23 15:10:49','','','UNICODE',29,0),(304,'serviceMethod','2015-04-23 15:10:49','','Service Method','2015-04-23 15:10:49','','','UNICODE',29,1),(305,'serviceInterface','2015-04-23 15:10:49','','Service Interface','2015-04-23 15:10:49','','','UNICODE',29,2),(306,'moduleName','2015-04-23 15:10:49','','Module Name','2015-04-23 15:10:49','','','UNICODE',29,3),(307,'displayName','2015-04-23 15:10:49','','Display Name','2015-04-23 15:10:49','','','UNICODE',29,4),(308,'channelName','2015-04-23 15:10:49','','Channel Name','2015-04-23 15:10:49','','','UNICODE',29,5),(309,'moduleVersion','2015-04-23 15:10:49','','Module Version','2015-04-23 15:10:49','','','UNICODE',29,6),(310,'values','2015-04-23 15:10:49','','Values','2015-04-23 15:10:49','','','UNICODE',29,7),(311,'name','2015-04-23 15:10:49','','Name','2015-04-23 15:10:50','','','UNICODE',29,8),(312,'modificationDate','2015-04-23 15:10:50','','Modification Date','2015-04-23 15:10:50','','','UNICODE',29,9),(313,'creationDate','2015-04-23 15:10:50','','Creation Date','2015-04-23 15:10:50','','','UNICODE',29,10),(314,'modifiedBy','2015-04-23 15:10:50','','Modified By','2015-04-23 15:10:50','','','UNICODE',29,11),(315,'owner','2015-04-23 15:10:50','','Owner','2015-04-23 15:10:50','','','UNICODE',29,12),(316,'creator','2015-04-23 15:10:50','','Created By','2015-04-23 15:10:50','','','UNICODE',29,13),(317,'id','2015-04-23 15:10:50','','Id','2015-04-23 15:10:50','','','UNICODE',29,14),(318,'lookupFields','2015-04-23 15:10:50','','Lookup Fields','2015-04-23 15:10:50','','','UNICODE',30,0),(319,'type','2015-04-23 15:10:50','','Type','2015-04-23 15:10:50','','','UNICODE',30,1),(320,'fields','2015-04-23 15:10:50','','Fields','2015-04-23 15:10:50','','','UNICODE',30,2),(321,'displayName','2015-04-23 15:10:50','','Display Name','2015-04-23 15:10:50','','','UNICODE',30,3),(322,'modificationDate','2015-04-23 15:10:50','','Modification Date','2015-04-23 15:10:50','','','UNICODE',30,4),(323,'creationDate','2015-04-23 15:10:50','','Creation Date','2015-04-23 15:10:50','','','UNICODE',30,5),(324,'modifiedBy','2015-04-23 15:10:50','','Modified By','2015-04-23 15:10:50','','','UNICODE',30,6),(325,'owner','2015-04-23 15:10:50','','Owner','2015-04-23 15:10:50','','','UNICODE',30,7),(326,'creator','2015-04-23 15:10:50','','Created By','2015-04-23 15:10:50','','','UNICODE',30,8),(327,'id','2015-04-23 15:10:50','','Id','2015-04-23 15:10:50','','','UNICODE',30,9),(328,'subject','2015-04-23 15:10:50','','Subject','2015-04-23 15:10:50','','','UNICODE',31,0),(329,'fromAddress','2015-04-23 15:10:50','','From Address','2015-04-23 15:10:50','','','UNICODE',31,1),(330,'deliveryStatus','2015-04-23 15:10:50','','Delivery Status','2015-04-23 15:10:50','','','UNICODE',31,2),(331,'message','2015-04-23 15:10:50','','Message','2015-04-23 15:10:50','','','UNICODE',31,3),(332,'toAddress','2015-04-23 15:10:50','','To Address','2015-04-23 15:10:50','','','UNICODE',31,4),(333,'deliveryTime','2015-04-23 15:10:50','','Delivery Time','2015-04-23 15:10:50','','','UNICODE',31,5),(334,'modificationDate','2015-04-23 15:10:50','','Modification Date','2015-04-23 15:10:50','','','UNICODE',31,6),(335,'creationDate','2015-04-23 15:10:50','','Creation Date','2015-04-23 15:10:50','','','UNICODE',31,7),(336,'modifiedBy','2015-04-23 15:10:50','','Modified By','2015-04-23 15:10:50','','','UNICODE',31,8),(337,'owner','2015-04-23 15:10:50','','Owner','2015-04-23 15:10:50','','','UNICODE',31,9),(338,'creator','2015-04-23 15:10:50','','Created By','2015-04-23 15:10:50','','','UNICODE',31,10),(339,'id','2015-04-23 15:10:50','','Id','2015-04-23 15:10:50','','','UNICODE',31,11),(340,'actionType','2015-04-23 15:10:50','','Action Type','2015-04-23 15:10:50','','','UNICODE',32,0),(341,'level','2015-04-23 15:10:50','','Level','2015-04-23 15:10:50','','','UNICODE',32,1),(342,'moduleName','2015-04-23 15:10:50','','Module Name','2015-04-23 15:10:50','','','UNICODE',32,2),(343,'recipient','2015-04-23 15:10:50','','Recipient','2015-04-23 15:10:50','','','UNICODE',32,3),(344,'modificationDate','2015-04-23 15:10:50','','Modification Date','2015-04-23 15:10:50','','','UNICODE',32,4),(345,'creationDate','2015-04-23 15:10:50','','Creation Date','2015-04-23 15:10:50','','','UNICODE',32,5),(346,'modifiedBy','2015-04-23 15:10:50','','Modified By','2015-04-23 15:10:50','','','UNICODE',32,6),(347,'owner','2015-04-23 15:10:50','','Owner','2015-04-23 15:10:50','','','UNICODE',32,7),(348,'creator','2015-04-23 15:10:50','','Created By','2015-04-23 15:10:50','','','UNICODE',32,8),(349,'id','2015-04-23 15:10:50','','Id','2015-04-23 15:10:50','','','UNICODE',32,9),(350,'moduleName','2015-04-23 15:10:50','','Module Name','2015-04-23 15:10:50','','','UNICODE',33,0),(351,'text','2015-04-23 15:10:50','','Text','2015-04-23 15:10:50','','','UNICODE',33,1),(352,'intStrMap','2015-04-23 15:10:50','','Int Str Map','2015-04-23 15:10:50','','','UNICODE',33,2),(353,'date','2015-04-23 15:10:50','','Date','2015-04-23 15:10:50','','','UNICODE',33,3),(354,'strStrMap','2015-04-23 15:10:50','','Str Str Map','2015-04-23 15:10:50','','','UNICODE',33,4),(355,'strLongMap','2015-04-23 15:10:50','','Str Long Map','2015-04-23 15:10:50','','','UNICODE',33,5),(356,'strObjectMap','2015-04-23 15:10:50','','Str Object Map','2015-04-23 15:10:50','','','UNICODE',33,6),(357,'level','2015-04-23 15:10:50','','Level','2015-04-23 15:10:50','','','UNICODE',33,7),(358,'timeout','2015-04-23 15:10:50','','Timeout','2015-04-23 15:10:50','','','UNICODE',33,8),(359,'modificationDate','2015-04-23 15:10:50','','Modification Date','2015-04-23 15:10:50','','','UNICODE',33,9),(360,'creationDate','2015-04-23 15:10:50','','Creation Date','2015-04-23 15:10:50','','','UNICODE',33,10),(361,'modifiedBy','2015-04-23 15:10:50','','Modified By','2015-04-23 15:10:50','','','UNICODE',33,11),(362,'owner','2015-04-23 15:10:50','','Owner','2015-04-23 15:10:50','','','UNICODE',33,12),(363,'creator','2015-04-23 15:10:50','','Created By','2015-04-23 15:10:50','','','UNICODE',33,13),(364,'id','2015-04-23 15:10:50','','Id','2015-04-23 15:10:50','','','UNICODE',33,14);
/*!40000 ALTER TABLE `MOTECH_TASKS_FIELDPARAMETER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FIELDPARAMETER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FIELDPARAMETER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FIELDPARAMETER__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `fieldKey` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `fields_id_OWN` bigint(20) DEFAULT NULL,
  `fields_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FIELDPARAMETER__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_FIELDPARAMETER__TRASH_FK1` (`fields_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FIELDPARAMETER__TRASH_FK1` FOREIGN KEY (`fields_id_OWN`) REFERENCES `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FIELDPARAMETER__TRASH`
--

LOCK TABLES `MOTECH_TASKS_FIELDPARAMETER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FIELDPARAMETER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_FIELDPARAMETER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FILTER`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FILTER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FILTER` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `expression` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `key` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `negationOperator` bit(1) NOT NULL,
  `operator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `filters_id_OWN` bigint(20) DEFAULT NULL,
  `filters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FILTER_N49` (`id`),
  KEY `MOTECH_TASKS_FILTER_FK1` (`filters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FILTER_FK1` FOREIGN KEY (`filters_id_OWN`) REFERENCES `MOTECH_TASKS_FILTERSET` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FILTER`
--

LOCK TABLES `MOTECH_TASKS_FILTER` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTER` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FILTERSET`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FILTERSET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FILTERSET` (
  `id` bigint(20) NOT NULL,
  `operator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `filters_id_OWN` bigint(20) DEFAULT NULL,
  `filters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FILTERSET_N49` (`id`),
  KEY `MOTECH_TASKS_FILTERSET_FK1` (`filters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FILTERSET_FK1` FOREIGN KEY (`filters_id_OWN`) REFERENCES `MOTECH_TASKS_TASKCONFIG` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FILTERSET`
--

LOCK TABLES `MOTECH_TASKS_FILTERSET` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTERSET` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTERSET` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FILTERSET__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FILTERSET__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FILTERSET__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `filterSet__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `filterSet__HistoryFromTrash` bit(1) DEFAULT NULL,
  `filterSet__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `operator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `filters_id_OWN` bigint(20) DEFAULT NULL,
  `filters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FILTERSET__HISTORY_N49` (`id`),
  KEY `MOTECH_TASKS_FILTERSET__HISTORY_FK1` (`filters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FILTERSET__HISTORY_FK1` FOREIGN KEY (`filters_id_OWN`) REFERENCES `MOTECH_TASKS_TASKCONFIG__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FILTERSET__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_FILTERSET__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTERSET__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTERSET__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FILTERSET__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FILTERSET__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FILTERSET__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `operator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `filters_id_OWN` bigint(20) DEFAULT NULL,
  `filters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FILTERSET__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_FILTERSET__TRASH_FK1` (`filters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FILTERSET__TRASH_FK1` FOREIGN KEY (`filters_id_OWN`) REFERENCES `MOTECH_TASKS_TASKCONFIG__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FILTERSET__TRASH`
--

LOCK TABLES `MOTECH_TASKS_FILTERSET__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTERSET__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTERSET__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FILTER__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FILTER__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FILTER__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `expression` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `filter__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `filter__HistoryFromTrash` bit(1) DEFAULT NULL,
  `filter__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `key` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `negationOperator` bit(1) NOT NULL,
  `operator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `filters_id_OWN` bigint(20) DEFAULT NULL,
  `filters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FILTER__HISTORY_N49` (`id`),
  KEY `MOTECH_TASKS_FILTER__HISTORY_FK1` (`filters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FILTER__HISTORY_FK1` FOREIGN KEY (`filters_id_OWN`) REFERENCES `MOTECH_TASKS_FILTERSET__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FILTER__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_FILTER__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTER__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTER__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_FILTER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_FILTER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_FILTER__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `expression` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `key` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `negationOperator` bit(1) NOT NULL,
  `operator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `filters_id_OWN` bigint(20) DEFAULT NULL,
  `filters_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_FILTER__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_FILTER__TRASH_FK1` (`filters_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_FILTER__TRASH_FK1` FOREIGN KEY (`filters_id_OWN`) REFERENCES `MOTECH_TASKS_FILTERSET__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_FILTER__TRASH`
--

LOCK TABLES `MOTECH_TASKS_FILTER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_FILTER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUP`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUP` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `field` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lookup_id_OWN` bigint(20) DEFAULT NULL,
  `lookup_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_LOOKUP_N49` (`id`),
  KEY `MOTECH_TASKS_LOOKUP_FK1` (`lookup_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_LOOKUP_FK1` FOREIGN KEY (`lookup_id_OWN`) REFERENCES `MOTECH_TASKS_DATASOURCE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUP`
--

LOCK TABLES `MOTECH_TASKS_LOOKUP` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUP` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUPFIELDSPARAMETER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lookupFields_id_OWN` bigint(20) DEFAULT NULL,
  `lookupFields_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_N49` (`id`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FK1` (`lookupFields_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FK1` FOREIGN KEY (`lookupFields_id_OWN`) REFERENCES `MOTECH_TASKS_TASKDATAPROVIDEROBJECT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER`
--

LOCK TABLES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_LOOKUPFIELDSPARAMETER` VALUES (1,'2015-04-23 15:10:46','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',1,0),(2,'2015-04-23 15:10:47','','By bundle and file name','2015-04-23 15:10:47','','',2,0),(3,'2015-04-23 15:10:47','','By bundle','2015-04-23 15:10:47','','',2,1),(4,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',2,2),(5,'2015-04-23 15:10:47','','Find By Permission Name','2015-04-23 15:10:47','','',3,0),(6,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',3,1),(7,'2015-04-23 15:10:47','','Find By Role Name','2015-04-23 15:10:47','','',4,0),(8,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',4,1),(9,'2015-04-23 15:10:47','','Find By Origin','2015-04-23 15:10:47','','',5,0),(10,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',5,1),(11,'2015-04-23 15:10:47','','Find By Role','2015-04-23 15:10:47','','',6,0),(12,'2015-04-23 15:10:47','','Find By User Name','2015-04-23 15:10:47','','',6,1),(13,'2015-04-23 15:10:47','','Find By Open Id','2015-04-23 15:10:47','','',6,2),(14,'2015-04-23 15:10:47','','Find By Email','2015-04-23 15:10:47','','',6,3),(15,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',6,4),(16,'2015-04-23 15:10:47','','Find For User','2015-04-23 15:10:47','','',7,0),(17,'2015-04-23 15:10:47','','Find By Expiration Date','2015-04-23 15:10:47','','',7,1),(18,'2015-04-23 15:10:47','','Find For Token','2015-04-23 15:10:47','','',7,2),(19,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',7,3),(20,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:47','','',8,0),(21,'2015-04-23 15:10:47','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',9,0),(22,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',10,0),(23,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',11,0),(24,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',12,0),(25,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',13,0),(26,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',14,0),(27,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',15,0),(28,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',16,0),(29,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:48','','',17,0),(30,'2015-04-23 15:10:48','','By Task','2015-04-23 15:10:48','','',18,0),(31,'2015-04-23 15:10:48','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',18,1),(32,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',19,0),(33,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',20,0),(34,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',21,0),(35,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',22,0),(36,'2015-04-23 15:10:49','','Find By Module Name','2015-04-23 15:10:49','','',23,0),(37,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',23,1),(38,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',24,0),(39,'2015-04-23 15:10:49','','Find Tasks By Name','2015-04-23 15:10:49','','',25,0),(40,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',25,1),(41,'2015-04-23 15:10:49','','By data provider name','2015-04-23 15:10:49','','',26,0),(42,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',26,1),(43,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',27,0),(44,'2015-04-23 15:10:49','','mds.dataprovider.byinstanceid','2015-04-23 15:10:49','','',28,0),(45,'2015-04-23 15:10:50','','mds.dataprovider.byinstanceid','2015-04-23 15:10:50','','',29,0),(46,'2015-04-23 15:10:50','','mds.dataprovider.byinstanceid','2015-04-23 15:10:50','','',30,0),(47,'2015-04-23 15:10:50','','By recipient address','2015-04-23 15:10:50','','',31,0),(48,'2015-04-23 15:10:50','','Search','2015-04-23 15:10:50','','',31,1),(49,'2015-04-23 15:10:50','','mds.dataprovider.byinstanceid','2015-04-23 15:10:50','','',31,2),(50,'2015-04-23 15:10:50','','mds.dataprovider.byinstanceid','2015-04-23 15:10:50','','',32,0),(51,'2015-04-23 15:10:50','','Find By Timeout','2015-04-23 15:10:50','','',33,0),(52,'2015-04-23 15:10:50','','mds.dataprovider.byinstanceid','2015-04-23 15:10:50','','',33,1);
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS` (
  `fields_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`fields_OID`,`IDX`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS_N49` (`fields_OID`),
  CONSTRAINT `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS_FK1` FOREIGN KEY (`fields_OID`) REFERENCES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS`
--

LOCK TABLES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS` VALUES (1,'mds.dataprovider.instanceid',0),(2,'bundle',0),(2,'filename',1),(3,'bundle',0),(4,'mds.dataprovider.instanceid',0),(5,'permissionName',0),(6,'mds.dataprovider.instanceid',0),(7,'roleName',0),(8,'mds.dataprovider.instanceid',0),(9,'origin',0),(10,'mds.dataprovider.instanceid',0),(11,'roles',0),(12,'userName',0),(13,'openId',0),(14,'email',0),(15,'mds.dataprovider.instanceid',0),(16,'username',0),(17,'expirationDate',0),(18,'token',0),(19,'mds.dataprovider.instanceid',0),(20,'mds.dataprovider.instanceid',0),(21,'mds.dataprovider.instanceid',0),(22,'mds.dataprovider.instanceid',0),(23,'mds.dataprovider.instanceid',0),(24,'mds.dataprovider.instanceid',0),(25,'mds.dataprovider.instanceid',0),(26,'mds.dataprovider.instanceid',0),(27,'mds.dataprovider.instanceid',0),(28,'mds.dataprovider.instanceid',0),(29,'mds.dataprovider.instanceid',0),(30,'task',0),(31,'mds.dataprovider.instanceid',0),(32,'mds.dataprovider.instanceid',0),(33,'mds.dataprovider.instanceid',0),(34,'mds.dataprovider.instanceid',0),(35,'mds.dataprovider.instanceid',0),(36,'moduleName',0),(37,'mds.dataprovider.instanceid',0),(38,'mds.dataprovider.instanceid',0),(39,'name',0),(40,'mds.dataprovider.instanceid',0),(41,'name',0),(42,'mds.dataprovider.instanceid',0),(43,'mds.dataprovider.instanceid',0),(44,'mds.dataprovider.instanceid',0),(45,'mds.dataprovider.instanceid',0),(46,'mds.dataprovider.instanceid',0),(47,'toAddress',0),(48,'fromAddress',0),(48,'toAddress',1),(48,'subject',2),(48,'message',3),(48,'deliveryTime',4),(48,'deliveryStatus',5),(49,'mds.dataprovider.instanceid',0),(50,'mds.dataprovider.instanceid',0),(51,'timeout',0),(52,'mds.dataprovider.instanceid',0);
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER_FIELDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lookupFieldsParameter__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `lookupFieldsParameter__HistoryFromTrash` bit(1) DEFAULT NULL,
  `lookupFieldsParameter__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS` (
  `fields_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`fields_OID`,`IDX`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS_N49` (`fields_OID`),
  CONSTRAINT `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS_FK1` FOREIGN KEY (`fields_OID`) REFERENCES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS`
--

LOCK TABLES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__HISTORY_FIELDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `lookupFields_id_OWN` bigint(20) DEFAULT NULL,
  `lookupFields_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FK1` (`lookupFields_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FK1` FOREIGN KEY (`lookupFields_id_OWN`) REFERENCES `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH`
--

LOCK TABLES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS` (
  `fields_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`fields_OID`,`IDX`),
  KEY `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS_N49` (`fields_OID`),
  CONSTRAINT `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS_FK1` FOREIGN KEY (`fields_OID`) REFERENCES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS`
--

LOCK TABLES `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUPFIELDSPARAMETER__TRASH_FIELDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUP__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUP__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUP__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `field` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lookup__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `lookup__HistoryFromTrash` bit(1) DEFAULT NULL,
  `lookup__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lookup_id_OWN` bigint(20) DEFAULT NULL,
  `lookup_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_LOOKUP__HISTORY_N49` (`id`),
  KEY `MOTECH_TASKS_LOOKUP__HISTORY_FK1` (`lookup_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_LOOKUP__HISTORY_FK1` FOREIGN KEY (`lookup_id_OWN`) REFERENCES `MOTECH_TASKS_DATASOURCE__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUP__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_LOOKUP__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUP__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUP__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_LOOKUP__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_LOOKUP__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_LOOKUP__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `field` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `lookup_id_OWN` bigint(20) DEFAULT NULL,
  `lookup_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_LOOKUP__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_LOOKUP__TRASH_FK1` (`lookup_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_LOOKUP__TRASH_FK1` FOREIGN KEY (`lookup_id_OWN`) REFERENCES `MOTECH_TASKS_DATASOURCE__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_LOOKUP__TRASH`
--

LOCK TABLES `MOTECH_TASKS_LOOKUP__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUP__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_LOOKUP__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_PARAMETER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_PARAMETER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_PARAMETER__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_PARAMETER__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_PARAMETER__TRASH`
--

LOCK TABLES `MOTECH_TASKS_PARAMETER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_PARAMETER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_PARAMETER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASK`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASK` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `hasRegisteredChannel` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `taskConfig_id_OID` bigint(20) DEFAULT NULL,
  `trigger_id_OID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASK_N49` (`trigger_id_OID`),
  KEY `MOTECH_TASKS_TASK_N50` (`id`),
  KEY `MOTECH_TASKS_TASK_N51` (`taskConfig_id_OID`),
  KEY `MOTECH_TASKS_TASK_N52` (`name`),
  CONSTRAINT `MOTECH_TASKS_TASK_FK1` FOREIGN KEY (`taskConfig_id_OID`) REFERENCES `MOTECH_TASKS_TASKCONFIG` (`id`),
  CONSTRAINT `MOTECH_TASKS_TASK_FK2` FOREIGN KEY (`trigger_id_OID`) REFERENCES `MOTECH_TASKS_TASKTRIGGERINFORMATION` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASK`
--

LOCK TABLES `MOTECH_TASKS_TASK` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASK` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIONINFORMATION`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIONINFORMATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIONINFORMATION` (
  `id` bigint(20) NOT NULL,
  `serviceInterface` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceMethod` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `channelName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `actions_id_OWN` bigint(20) DEFAULT NULL,
  `actions_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION_N49` (`id`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION_FK1` (`actions_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIONINFORMATION_FK1` FOREIGN KEY (`actions_id_OWN`) REFERENCES `MOTECH_TASKS_TASK` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIONINFORMATION`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIONINFORMATION` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES_N49` (`id_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_TASKS_TASKACTIONINFORMATION` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION_VALUES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY` (
  `id` bigint(20) NOT NULL,
  `channelName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceInterface` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceMethod` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `taskActionInformation__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `taskActionInformation__HistoryFromTrash` bit(1) DEFAULT NULL,
  `taskActionInformation__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `actions_id_OWN` bigint(20) DEFAULT NULL,
  `actions_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_N49` (`id`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_FK1` (`actions_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_FK1` FOREIGN KEY (`actions_id_OWN`) REFERENCES `MOTECH_TASKS_TASK__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES_N49` (`id_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__HISTORY_VALUES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH` (
  `id` bigint(20) NOT NULL,
  `channelName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `serviceInterface` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `serviceMethod` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `actions_id_OWN` bigint(20) DEFAULT NULL,
  `actions_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_FK1` (`actions_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_FK1` FOREIGN KEY (`actions_id_OWN`) REFERENCES `MOTECH_TASKS_TASK__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES` (
  `id_OID` bigint(20) NOT NULL,
  `KEY` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `VALUE` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_OID`,`KEY`),
  KEY `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES_N49` (`id_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES_FK1` FOREIGN KEY (`id_OID`) REFERENCES `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIONINFORMATION__TRASH_VALUES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIVITY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIVITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIVITY` (
  `id` bigint(20) NOT NULL,
  `activityType` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `date` datetime DEFAULT NULL,
  `message` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `stackTraceElement` varchar(8096) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `task` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKACTIVITY_N49` (`task`),
  KEY `MOTECH_TASKS_TASKACTIVITY_N50` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIVITY`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIVITY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIVITY_FIELDS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIVITY_FIELDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIVITY_FIELDS` (
  `fields_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`fields_OID`,`IDX`),
  KEY `MOTECH_TASKS_TASKACTIVITY_FIELDS_N49` (`fields_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIVITY_FIELDS_FK1` FOREIGN KEY (`fields_OID`) REFERENCES `MOTECH_TASKS_TASKACTIVITY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIVITY_FIELDS`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIVITY_FIELDS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY_FIELDS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY_FIELDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIVITY__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIVITY__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIVITY__TRASH` (
  `id` bigint(20) NOT NULL,
  `activityType` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `date` datetime DEFAULT NULL,
  `message` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `stackTraceElement` varchar(8096) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `task` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKACTIVITY__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIVITY__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIVITY__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS` (
  `fields_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`fields_OID`,`IDX`),
  KEY `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS_N49` (`fields_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS_FK1` FOREIGN KEY (`fields_OID`) REFERENCES `MOTECH_TASKS_TASKACTIVITY__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS`
--

LOCK TABLES `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKACTIVITY__TRASH_FIELDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKCONFIG`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKCONFIG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKCONFIG` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKCONFIG_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKCONFIG`
--

LOCK TABLES `MOTECH_TASKS_TASKCONFIG` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIG` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKCONFIGSTEP__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKCONFIGSTEP__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKCONFIGSTEP__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `order` int(11) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKCONFIGSTEP__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKCONFIGSTEP__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKCONFIGSTEP__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIGSTEP__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIGSTEP__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKCONFIG__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKCONFIG__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKCONFIG__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `taskConfig__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `taskConfig__HistoryFromTrash` bit(1) DEFAULT NULL,
  `taskConfig__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKCONFIG__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKCONFIG__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_TASKCONFIG__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIG__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIG__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKCONFIG__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKCONFIG__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKCONFIG__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKCONFIG__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKCONFIG__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKCONFIG__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIG__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKCONFIG__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKDATAPROVIDER`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKDATAPROVIDER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKDATAPROVIDER` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `MOTECH_TASKS_TASKDATAPROVIDER_U1` (`name`),
  KEY `MOTECH_TASKS_TASKDATAPROVIDER_N50` (`id`),
  KEY `MOTECH_TASKS_TASKDATAPROVIDER_N49` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKDATAPROVIDER`
--

LOCK TABLES `MOTECH_TASKS_TASKDATAPROVIDER` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDER` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_TASKDATAPROVIDER` VALUES (1,'2015-04-23 15:10:46','','2015-04-23 15:10:46','','data-services','');
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKDATAPROVIDEROBJECT`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKDATAPROVIDEROBJECT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKDATAPROVIDEROBJECT` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `objects_id_OWN` bigint(20) DEFAULT NULL,
  `objects_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKDATAPROVIDEROBJECT_N49` (`id`),
  KEY `MOTECH_TASKS_TASKDATAPROVIDEROBJECT_FK1` (`objects_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKDATAPROVIDEROBJECT_FK1` FOREIGN KEY (`objects_id_OWN`) REFERENCES `MOTECH_TASKS_TASKDATAPROVIDER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKDATAPROVIDEROBJECT`
--

LOCK TABLES `MOTECH_TASKS_TASKDATAPROVIDEROBJECT` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDEROBJECT` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_TASKDATAPROVIDEROBJECT` VALUES (1,'2015-04-23 15:10:46','','SettingsRecord','2015-04-23 15:10:46','','','org.motechproject.server.config.domain.SettingsRecord',1,0),(2,'2015-04-23 15:10:47','','ModulePropertiesRecord','2015-04-23 15:10:47','','','org.motechproject.config.domain.ModulePropertiesRecord',1,1),(3,'2015-04-23 15:10:47','','MotechPermission','2015-04-23 15:10:47','','','org.motechproject.security.domain.MotechPermission',1,2),(4,'2015-04-23 15:10:47','','MotechRole','2015-04-23 15:10:47','','','org.motechproject.security.domain.MotechRole',1,3),(5,'2015-04-23 15:10:47','','MotechURLSecurityRule','2015-04-23 15:10:47','','','org.motechproject.security.domain.MotechURLSecurityRule',1,4),(6,'2015-04-23 15:10:47','','MotechUser','2015-04-23 15:10:47','','','org.motechproject.security.domain.MotechUser',1,5),(7,'2015-04-23 15:10:47','','PasswordRecovery','2015-04-23 15:10:47','','','org.motechproject.security.domain.PasswordRecovery',1,6),(8,'2015-04-23 15:10:47','','TaskEventInformation','2015-04-23 15:10:47','','','org.motechproject.tasks.domain.TaskEventInformation',1,7),(9,'2015-04-23 15:10:47','','ActionParameter','2015-04-23 15:10:47','','','org.motechproject.tasks.domain.ActionParameter',1,8),(10,'2015-04-23 15:10:48','','Lookup','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.Lookup',1,9),(11,'2015-04-23 15:10:48','','EventParameter','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.EventParameter',1,10),(12,'2015-04-23 15:10:48','','FieldParameter','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.FieldParameter',1,11),(13,'2015-04-23 15:10:48','','FilterSet','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.FilterSet',1,12),(14,'2015-04-23 15:10:48','','Parameter','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.Parameter',1,13),(15,'2015-04-23 15:10:48','','TaskConfigStep','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.TaskConfigStep',1,14),(16,'2015-04-23 15:10:48','','ActionEvent','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.ActionEvent',1,15),(17,'2015-04-23 15:10:48','','TaskConfig','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.TaskConfig',1,16),(18,'2015-04-23 15:10:48','','TaskActivity','2015-04-23 15:10:48','','','org.motechproject.tasks.domain.TaskActivity',1,17),(19,'2015-04-23 15:10:49','','TaskEvent','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.TaskEvent',1,18),(20,'2015-04-23 15:10:49','','DataSource','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.DataSource',1,19),(21,'2015-04-23 15:10:49','','LookupFieldsParameter','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.LookupFieldsParameter',1,20),(22,'2015-04-23 15:10:49','','TriggerEvent','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.TriggerEvent',1,21),(23,'2015-04-23 15:10:49','','Channel','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.Channel',1,22),(24,'2015-04-23 15:10:49','','TaskError','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.TaskError',1,23),(25,'2015-04-23 15:10:49','','Task','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.Task',1,24),(26,'2015-04-23 15:10:49','','TaskDataProvider','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.TaskDataProvider',1,25),(27,'2015-04-23 15:10:49','','TaskTriggerInformation','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.TaskTriggerInformation',1,26),(28,'2015-04-23 15:10:49','','Filter','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.Filter',1,27),(29,'2015-04-23 15:10:49','','TaskActionInformation','2015-04-23 15:10:49','','','org.motechproject.tasks.domain.TaskActionInformation',1,28),(30,'2015-04-23 15:10:50','','TaskDataProviderObject','2015-04-23 15:10:50','','','org.motechproject.tasks.domain.TaskDataProviderObject',1,29),(31,'2015-04-23 15:10:50','','EmailRecord','2015-04-23 15:10:50','','','org.motechproject.email.domain.EmailRecord',1,30),(32,'2015-04-23 15:10:50','','NotificationRule','2015-04-23 15:10:50','','','org.motechproject.admin.domain.NotificationRule',1,31),(33,'2015-04-23 15:10:50','','StatusMessage','2015-04-23 15:10:50','','','org.motechproject.admin.domain.StatusMessage',1,32);
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDEROBJECT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `objects_id_OWN` bigint(20) DEFAULT NULL,
  `objects_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH_FK1` (`objects_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH_FK1` FOREIGN KEY (`objects_id_OWN`) REFERENCES `MOTECH_TASKS_TASKDATAPROVIDER__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDEROBJECT__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKDATAPROVIDER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKDATAPROVIDER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKDATAPROVIDER__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKDATAPROVIDER__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKDATAPROVIDER__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKDATAPROVIDER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKDATAPROVIDER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKERROR`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKERROR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKERROR` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `message` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `validationErrors_id_OWN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKERROR_N49` (`id`),
  KEY `MOTECH_TASKS_TASKERROR_FK1` (`validationErrors_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKERROR_FK1` FOREIGN KEY (`validationErrors_id_OWN`) REFERENCES `MOTECH_TASKS_TASK` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKERROR`
--

LOCK TABLES `MOTECH_TASKS_TASKERROR` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKERROR_ARGS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKERROR_ARGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKERROR_ARGS` (
  `args_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`args_OID`,`IDX`),
  KEY `MOTECH_TASKS_TASKERROR_ARGS_N49` (`args_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKERROR_ARGS_FK1` FOREIGN KEY (`args_OID`) REFERENCES `MOTECH_TASKS_TASKERROR` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKERROR_ARGS`
--

LOCK TABLES `MOTECH_TASKS_TASKERROR_ARGS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR_ARGS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR_ARGS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKERROR__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKERROR__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKERROR__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `message` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `taskError__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `taskError__HistoryFromTrash` bit(1) DEFAULT NULL,
  `taskError__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `validationErrors_id_OWN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKERROR__HISTORY_N49` (`id`),
  KEY `MOTECH_TASKS_TASKERROR__HISTORY_FK1` (`validationErrors_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKERROR__HISTORY_FK1` FOREIGN KEY (`validationErrors_id_OWN`) REFERENCES `MOTECH_TASKS_TASK__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKERROR__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_TASKERROR__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKERROR__HISTORY_ARGS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKERROR__HISTORY_ARGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKERROR__HISTORY_ARGS` (
  `args_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`args_OID`,`IDX`),
  KEY `MOTECH_TASKS_TASKERROR__HISTORY_ARGS_N49` (`args_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKERROR__HISTORY_ARGS_FK1` FOREIGN KEY (`args_OID`) REFERENCES `MOTECH_TASKS_TASKERROR__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKERROR__HISTORY_ARGS`
--

LOCK TABLES `MOTECH_TASKS_TASKERROR__HISTORY_ARGS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__HISTORY_ARGS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__HISTORY_ARGS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKERROR__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKERROR__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKERROR__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `message` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `validationErrors_id_OWN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKERROR__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_TASKERROR__TRASH_FK1` (`validationErrors_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TASKERROR__TRASH_FK1` FOREIGN KEY (`validationErrors_id_OWN`) REFERENCES `MOTECH_TASKS_TASK__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKERROR__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKERROR__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKERROR__TRASH_ARGS`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKERROR__TRASH_ARGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKERROR__TRASH_ARGS` (
  `args_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`args_OID`,`IDX`),
  KEY `MOTECH_TASKS_TASKERROR__TRASH_ARGS_N49` (`args_OID`),
  CONSTRAINT `MOTECH_TASKS_TASKERROR__TRASH_ARGS_FK1` FOREIGN KEY (`args_OID`) REFERENCES `MOTECH_TASKS_TASKERROR__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKERROR__TRASH_ARGS`
--

LOCK TABLES `MOTECH_TASKS_TASKERROR__TRASH_ARGS` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__TRASH_ARGS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKERROR__TRASH_ARGS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH` (
  `id` bigint(20) NOT NULL,
  `channelName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKEVENTINFORMATION__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKEVENT__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKEVENT__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKEVENT__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKEVENT__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKEVENT__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKEVENT__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKEVENT__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKEVENT__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKTRIGGERINFORMATION`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKTRIGGERINFORMATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION` (
  `id` bigint(20) NOT NULL,
  `triggerListenerSubject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `channelName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKTRIGGERINFORMATION_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKTRIGGERINFORMATION`
--

LOCK TABLES `MOTECH_TASKS_TASKTRIGGERINFORMATION` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY` (
  `id` bigint(20) NOT NULL,
  `channelName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `taskTriggerInformation__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `taskTriggerInformation__HistoryFromTrash` bit(1) DEFAULT NULL,
  `taskTriggerInformation__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `triggerListenerSubject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH` (
  `id` bigint(20) NOT NULL,
  `channelName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `moduleVersion` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `triggerListenerSubject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASK__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASK__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASK__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `hasRegisteredChannel` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `taskConfig_id_OID` bigint(20) DEFAULT NULL,
  `task__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `task__HistoryFromTrash` bit(1) DEFAULT NULL,
  `task__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `trigger_id_OID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASK__HISTORY_N50` (`trigger_id_OID`),
  KEY `MOTECH_TASKS_TASK__HISTORY_N51` (`id`),
  KEY `MOTECH_TASKS_TASK__HISTORY_N49` (`taskConfig_id_OID`),
  CONSTRAINT `MOTECH_TASKS_TASK__HISTORY_FK2` FOREIGN KEY (`taskConfig_id_OID`) REFERENCES `MOTECH_TASKS_TASKCONFIG__HISTORY` (`id`),
  CONSTRAINT `MOTECH_TASKS_TASK__HISTORY_FK1` FOREIGN KEY (`trigger_id_OID`) REFERENCES `MOTECH_TASKS_TASKTRIGGERINFORMATION__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASK__HISTORY`
--

LOCK TABLES `MOTECH_TASKS_TASK__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASK__HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASK__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TASK__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TASK__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TASK__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `hasRegisteredChannel` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `taskConfig_id_OID` bigint(20) DEFAULT NULL,
  `trigger_id_OID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TASK__TRASH_N51` (`id`),
  KEY `MOTECH_TASKS_TASK__TRASH_N50` (`taskConfig_id_OID`),
  KEY `MOTECH_TASKS_TASK__TRASH_N49` (`trigger_id_OID`),
  CONSTRAINT `MOTECH_TASKS_TASK__TRASH_FK2` FOREIGN KEY (`trigger_id_OID`) REFERENCES `MOTECH_TASKS_TASKTRIGGERINFORMATION__TRASH` (`id`),
  CONSTRAINT `MOTECH_TASKS_TASK__TRASH_FK1` FOREIGN KEY (`taskConfig_id_OID`) REFERENCES `MOTECH_TASKS_TASKCONFIG__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TASK__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TASK__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASK__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TASK__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TRIGGEREVENT`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TRIGGEREVENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TRIGGEREVENT` (
  `id` bigint(20) NOT NULL,
  `triggerListenerSubject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `triggerTaskEvents_id_OWN` bigint(20) DEFAULT NULL,
  `triggerTaskEvents_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TRIGGEREVENT_N49` (`id`),
  KEY `MOTECH_TASKS_TRIGGEREVENT_FK1` (`triggerTaskEvents_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TRIGGEREVENT_FK1` FOREIGN KEY (`triggerTaskEvents_id_OWN`) REFERENCES `MOTECH_TASKS_CHANNEL` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TRIGGEREVENT`
--

LOCK TABLES `MOTECH_TASKS_TRIGGEREVENT` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TRIGGEREVENT` DISABLE KEYS */;
INSERT INTO `MOTECH_TASKS_TRIGGEREVENT` VALUES (8,'mds.crud.serverconfig.SettingsRecord.CREATE','2015-04-23 15:10:51','',NULL,'CREATE SettingsRecord','2015-04-23 15:10:51','',NULL,'','mds.crud.serverconfig.SettingsRecord.CREATE',3,0),(9,'mds.crud.serverconfig.SettingsRecord.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE SettingsRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.serverconfig.SettingsRecord.UPDATE',3,1),(10,'mds.crud.serverconfig.SettingsRecord.DELETE','2015-04-23 15:10:52','',NULL,'DELETE SettingsRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.serverconfig.SettingsRecord.DELETE',3,2),(11,'mds.crud.serverconfig.ModulePropertiesRecord.CREATE','2015-04-23 15:10:52','',NULL,'CREATE ModulePropertiesRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.serverconfig.ModulePropertiesRecord.CREATE',3,3),(12,'mds.crud.serverconfig.ModulePropertiesRecord.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE ModulePropertiesRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.serverconfig.ModulePropertiesRecord.UPDATE',3,4),(13,'mds.crud.serverconfig.ModulePropertiesRecord.DELETE','2015-04-23 15:10:52','',NULL,'DELETE ModulePropertiesRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.serverconfig.ModulePropertiesRecord.DELETE',3,5),(14,'mds.crud.websecurity.MotechPermission.CREATE','2015-04-23 15:10:52','',NULL,'CREATE MotechPermission','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechPermission.CREATE',3,6),(15,'mds.crud.websecurity.MotechPermission.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE MotechPermission','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechPermission.UPDATE',3,7),(16,'mds.crud.websecurity.MotechPermission.DELETE','2015-04-23 15:10:52','',NULL,'DELETE MotechPermission','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechPermission.DELETE',3,8),(17,'mds.crud.websecurity.MotechRole.CREATE','2015-04-23 15:10:52','',NULL,'CREATE MotechRole','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechRole.CREATE',3,9),(18,'mds.crud.websecurity.MotechRole.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE MotechRole','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechRole.UPDATE',3,10),(19,'mds.crud.websecurity.MotechRole.DELETE','2015-04-23 15:10:52','',NULL,'DELETE MotechRole','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechRole.DELETE',3,11),(20,'mds.crud.websecurity.MotechURLSecurityRule.CREATE','2015-04-23 15:10:52','',NULL,'CREATE MotechURLSecurityRule','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechURLSecurityRule.CREATE',3,12),(21,'mds.crud.websecurity.MotechURLSecurityRule.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE MotechURLSecurityRule','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechURLSecurityRule.UPDATE',3,13),(22,'mds.crud.websecurity.MotechURLSecurityRule.DELETE','2015-04-23 15:10:52','',NULL,'DELETE MotechURLSecurityRule','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechURLSecurityRule.DELETE',3,14),(23,'mds.crud.websecurity.MotechUser.CREATE','2015-04-23 15:10:52','',NULL,'CREATE MotechUser','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechUser.CREATE',3,15),(24,'mds.crud.websecurity.MotechUser.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE MotechUser','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechUser.UPDATE',3,16),(25,'mds.crud.websecurity.MotechUser.DELETE','2015-04-23 15:10:52','',NULL,'DELETE MotechUser','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.MotechUser.DELETE',3,17),(26,'mds.crud.websecurity.PasswordRecovery.CREATE','2015-04-23 15:10:52','',NULL,'CREATE PasswordRecovery','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.PasswordRecovery.CREATE',3,18),(27,'mds.crud.websecurity.PasswordRecovery.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE PasswordRecovery','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.PasswordRecovery.UPDATE',3,19),(28,'mds.crud.websecurity.PasswordRecovery.DELETE','2015-04-23 15:10:52','',NULL,'DELETE PasswordRecovery','2015-04-23 15:10:52','',NULL,'','mds.crud.websecurity.PasswordRecovery.DELETE',3,20),(29,'mds.crud.email.EmailRecord.CREATE','2015-04-23 15:10:52','',NULL,'CREATE EmailRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.email.EmailRecord.CREATE',3,21),(30,'mds.crud.email.EmailRecord.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE EmailRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.email.EmailRecord.UPDATE',3,22),(31,'mds.crud.email.EmailRecord.DELETE','2015-04-23 15:10:52','',NULL,'DELETE EmailRecord','2015-04-23 15:10:52','',NULL,'','mds.crud.email.EmailRecord.DELETE',3,23),(32,'mds.crud.admin.NotificationRule.CREATE','2015-04-23 15:10:52','',NULL,'CREATE NotificationRule','2015-04-23 15:10:52','',NULL,'','mds.crud.admin.NotificationRule.CREATE',3,24),(33,'mds.crud.admin.NotificationRule.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE NotificationRule','2015-04-23 15:10:52','',NULL,'','mds.crud.admin.NotificationRule.UPDATE',3,25),(34,'mds.crud.admin.NotificationRule.DELETE','2015-04-23 15:10:52','',NULL,'DELETE NotificationRule','2015-04-23 15:10:52','',NULL,'','mds.crud.admin.NotificationRule.DELETE',3,26),(35,'mds.crud.admin.StatusMessage.CREATE','2015-04-23 15:10:52','',NULL,'CREATE StatusMessage','2015-04-23 15:10:52','',NULL,'','mds.crud.admin.StatusMessage.CREATE',3,27),(36,'mds.crud.admin.StatusMessage.UPDATE','2015-04-23 15:10:52','',NULL,'UPDATE StatusMessage','2015-04-23 15:10:52','',NULL,'','mds.crud.admin.StatusMessage.UPDATE',3,28),(37,'mds.crud.admin.StatusMessage.DELETE','2015-04-23 15:10:52','',NULL,'DELETE StatusMessage','2015-04-23 15:10:52','',NULL,'','mds.crud.admin.StatusMessage.DELETE',3,29),(38,'mds.crud.tasks.TaskDataProviderObject.CREATE','2015-04-23 15:10:52','',NULL,'CREATE TaskDataProviderObject','2015-04-23 15:10:52','',NULL,'','mds.crud.tasks.TaskDataProviderObject.CREATE',3,30),(39,'mds.crud.tasks.TaskDataProviderObject.UPDATE','2015-04-23 15:10:53','',NULL,'UPDATE TaskDataProviderObject','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.TaskDataProviderObject.UPDATE',3,31),(40,'mds.crud.tasks.TaskDataProviderObject.DELETE','2015-04-23 15:10:53','',NULL,'DELETE TaskDataProviderObject','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.TaskDataProviderObject.DELETE',3,32),(41,'mds.crud.tasks.TaskDataProvider.CREATE','2015-04-23 15:10:53','',NULL,'CREATE TaskDataProvider','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.TaskDataProvider.CREATE',3,33),(42,'mds.crud.tasks.TaskDataProvider.UPDATE','2015-04-23 15:10:53','',NULL,'UPDATE TaskDataProvider','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.TaskDataProvider.UPDATE',3,34),(43,'mds.crud.tasks.TaskDataProvider.DELETE','2015-04-23 15:10:53','',NULL,'DELETE TaskDataProvider','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.TaskDataProvider.DELETE',3,35),(44,'mds.crud.tasks.Channel.CREATE','2015-04-23 15:10:53','',NULL,'CREATE Channel','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.Channel.CREATE',3,36),(45,'mds.crud.tasks.Channel.UPDATE','2015-04-23 15:10:53','',NULL,'UPDATE Channel','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.Channel.UPDATE',3,37),(46,'mds.crud.tasks.Channel.DELETE','2015-04-23 15:10:53','',NULL,'DELETE Channel','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.Channel.DELETE',3,38),(47,'mds.crud.tasks.Task.CREATE','2015-04-23 15:10:53','',NULL,'CREATE Task','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.Task.CREATE',3,39),(48,'mds.crud.tasks.Task.UPDATE','2015-04-23 15:10:53','',NULL,'UPDATE Task','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.Task.UPDATE',3,40),(49,'mds.crud.tasks.Task.DELETE','2015-04-23 15:10:53','',NULL,'DELETE Task','2015-04-23 15:10:53','',NULL,'','mds.crud.tasks.Task.DELETE',3,41);
/*!40000 ALTER TABLE `MOTECH_TASKS_TRIGGEREVENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_TASKS_TRIGGEREVENT__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_TASKS_TRIGGEREVENT__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_TASKS_TRIGGEREVENT__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `triggerListenerSubject` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `triggerTaskEvents_id_OWN` bigint(20) DEFAULT NULL,
  `triggerTaskEvents_INTEGER_IDX` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_TASKS_TRIGGEREVENT__TRASH_N49` (`id`),
  KEY `MOTECH_TASKS_TRIGGEREVENT__TRASH_FK1` (`triggerTaskEvents_id_OWN`),
  CONSTRAINT `MOTECH_TASKS_TRIGGEREVENT__TRASH_FK1` FOREIGN KEY (`triggerTaskEvents_id_OWN`) REFERENCES `MOTECH_TASKS_CHANNEL__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_TASKS_TRIGGEREVENT__TRASH`
--

LOCK TABLES `MOTECH_TASKS_TRIGGEREVENT__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_TASKS_TRIGGEREVENT__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_TASKS_TRIGGEREVENT__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHPERMISSION`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHPERMISSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHPERMISSION` (
  `id` bigint(20) NOT NULL,
  `bundleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `permissionName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHPERMISSION_N49` (`permissionName`),
  KEY `MOTECH_WEB_SECURITY_MOTECHPERMISSION_N50` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHPERMISSION`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHPERMISSION` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHPERMISSION` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHPERMISSION` VALUES (1,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:20','','2015-04-23 15:10:20','','','addUser'),(2,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:21','','2015-04-23 15:10:21','','','editUser'),(3,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:21','','2015-04-23 15:10:21','','','deleteUser'),(4,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:21','','2015-04-23 15:10:21','','','manageUser'),(5,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:21','','2015-04-23 15:10:21','','','activateUser'),(6,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:22','','2015-04-23 15:10:22','','','manageRole'),(7,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:22','','2015-04-23 15:10:22','','','managePermission'),(8,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:22','','2015-04-23 15:10:22','','','viewSecurity'),(9,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:23','','2015-04-23 15:10:23','','','updateSecurity'),(10,'org.motechproject.motech-platform-dataservices','2015-04-23 15:10:23','','2015-04-23 15:10:23','','','mdsSchemaAccess'),(11,'org.motechproject.motech-platform-dataservices','2015-04-23 15:10:23','','2015-04-23 15:10:23','','','mdsSettingsAccess'),(12,'org.motechproject.motech-platform-dataservices','2015-04-23 15:10:23','','2015-04-23 15:10:23','','','mdsDataAccess'),(13,'org.motechproject.motech-platform-email','2015-04-23 15:10:24','','2015-04-23 15:10:24','','','viewBasicEmailLogs'),(14,'org.motechproject.motech-platform-email','2015-04-23 15:10:24','','2015-04-23 15:10:24','','','viewDetailedEmailLogs'),(15,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:26','','2015-04-23 15:10:26','','','viewUser'),(16,'org.motechproject.motech-platform-web-security','2015-04-23 15:10:27','','2015-04-23 15:10:27','','','viewRole'),(17,'org.motechproject.motech-admin','2015-04-23 15:10:46','','2015-04-23 15:10:46','','','startBundle'),(18,'org.motechproject.motech-admin','2015-04-23 15:10:46','','2015-04-23 15:10:46','','','stopBundle'),(19,'org.motechproject.motech-admin','2015-04-23 15:10:47','','2015-04-23 15:10:47','','','manageBundles'),(20,'org.motechproject.motech-admin','2015-04-23 15:10:47','','2015-04-23 15:10:47','','','installBundle'),(21,'org.motechproject.motech-admin','2015-04-23 15:10:47','','2015-04-23 15:10:47','','','bundleDetails'),(22,'org.motechproject.motech-admin','2015-04-23 15:10:48','','2015-04-23 15:10:48','','','uninstallBundle');
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHPERMISSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH` (
  `id` bigint(20) NOT NULL,
  `bundleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `permissionName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHPERMISSION__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHROLE`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHROLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHROLE` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deletable` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `roleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHROLE_N50` (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHROLE_N49` (`roleName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHROLE`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHROLE` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHROLE` VALUES (1,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:48','','','Motech Admin'),(2,'2015-04-23 15:10:30','','\0','2015-04-23 15:10:31','','','Email Admin'),(3,'2015-04-23 15:10:30','','\0','2015-04-23 15:10:31','','','Email Junior Admin'),(4,'2015-04-23 15:10:48','','\0','2015-04-23 15:10:48','','','Bundle Admin');
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES` (
  `permissionNames_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`permissionNames_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES_N49` (`permissionNames_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES_FK1` FOREIGN KEY (`permissionNames_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHROLE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES` VALUES (1,'addUser',0),(1,'editUser',1),(1,'deleteUser',2),(1,'manageUser',3),(1,'activateUser',4),(1,'manageRole',5),(1,'managePermission',6),(1,'viewSecurity',7),(1,'updateSecurity',8),(1,'mdsSchemaAccess',9),(1,'mdsSettingsAccess',10),(1,'mdsDataAccess',11),(1,'viewBasicEmailLogs',12),(1,'viewDetailedEmailLogs',13),(1,'viewUser',14),(1,'viewRole',15),(1,'startBundle',16),(1,'stopBundle',17),(1,'manageBundles',18),(1,'installBundle',19),(1,'bundleDetails',20),(1,'uninstallBundle',21),(2,'viewDetailedEmailLogs',0),(2,'viewBasicEmailLogs',1),(3,'viewBasicEmailLogs',0),(4,'manageBundles',0),(4,'stopBundle',1),(4,'startBundle',2),(4,'uninstallBundle',3),(4,'installBundle',4),(4,'bundleDetails',5);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE_PERMISSIONNAMES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deletable` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `motechRole__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `motechRole__HistoryFromTrash` bit(1) DEFAULT NULL,
  `motechRole__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `roleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY` VALUES (1,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:20','',1,'\0',1,'','Motech Admin'),(2,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:20','',1,'\0',1,'','Motech Admin'),(3,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:21','',1,'\0',1,'','Motech Admin'),(4,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:21','',1,'\0',1,'','Motech Admin'),(5,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:21','',1,'\0',1,'','Motech Admin'),(6,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:21','',1,'\0',1,'','Motech Admin'),(7,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:22','',1,'\0',1,'','Motech Admin'),(8,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:22','',1,'\0',1,'','Motech Admin'),(9,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:22','',1,'\0',1,'','Motech Admin'),(10,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:23','',1,'\0',1,'','Motech Admin'),(11,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:23','',1,'\0',1,'','Motech Admin'),(12,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:23','',1,'\0',1,'','Motech Admin'),(13,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:23','',1,'\0',1,'','Motech Admin'),(14,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:24','',1,'\0',1,'','Motech Admin'),(15,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:24','',1,'\0',1,'','Motech Admin'),(16,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:26','',1,'\0',1,'','Motech Admin'),(17,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:27','',1,'\0',1,'','Motech Admin'),(18,'2015-04-23 15:10:30','','\0','2015-04-23 15:10:30','',2,'\0',1,'','Email Admin'),(19,'2015-04-23 15:10:30','','\0','2015-04-23 15:10:30','',3,'\0',1,'','Email Junior Admin'),(20,'2015-04-23 15:10:30','','\0','2015-04-23 15:10:31','',2,'\0',1,'','Email Admin'),(21,'2015-04-23 15:10:30','','\0','2015-04-23 15:10:31','',3,'\0',1,'','Email Junior Admin'),(22,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:46','',1,'\0',1,'','Motech Admin'),(23,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:46','',1,'\0',1,'','Motech Admin'),(24,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:47','',1,'\0',1,'','Motech Admin'),(25,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:47','',1,'\0',1,'','Motech Admin'),(26,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:47','',1,'\0',1,'','Motech Admin'),(27,'2015-04-23 15:10:20','','\0','2015-04-23 15:10:48','',1,'\0',1,'','Motech Admin'),(28,'2015-04-23 15:10:48','','\0','2015-04-23 15:10:48','',4,'\0',1,'','Bundle Admin'),(29,'2015-04-23 15:10:48','','\0','2015-04-23 15:10:48','',4,'\0',1,'','Bundle Admin');
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES` (
  `permissionNames_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`permissionNames_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES_N49` (`permissionNames_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES_FK1` FOREIGN KEY (`permissionNames_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES` VALUES (2,'addUser',0),(3,'addUser',0),(3,'editUser',1),(4,'addUser',0),(4,'editUser',1),(4,'deleteUser',2),(5,'addUser',0),(5,'editUser',1),(5,'deleteUser',2),(5,'manageUser',3),(6,'addUser',0),(6,'editUser',1),(6,'deleteUser',2),(6,'manageUser',3),(6,'activateUser',4),(7,'addUser',0),(7,'editUser',1),(7,'deleteUser',2),(7,'manageUser',3),(7,'activateUser',4),(7,'manageRole',5),(8,'addUser',0),(8,'editUser',1),(8,'deleteUser',2),(8,'manageUser',3),(8,'activateUser',4),(8,'manageRole',5),(8,'managePermission',6),(9,'addUser',0),(9,'editUser',1),(9,'deleteUser',2),(9,'manageUser',3),(9,'activateUser',4),(9,'manageRole',5),(9,'managePermission',6),(9,'viewSecurity',7),(10,'addUser',0),(10,'editUser',1),(10,'deleteUser',2),(10,'manageUser',3),(10,'activateUser',4),(10,'manageRole',5),(10,'managePermission',6),(10,'viewSecurity',7),(10,'updateSecurity',8),(11,'addUser',0),(11,'editUser',1),(11,'deleteUser',2),(11,'manageUser',3),(11,'activateUser',4),(11,'manageRole',5),(11,'managePermission',6),(11,'viewSecurity',7),(11,'updateSecurity',8),(11,'mdsSchemaAccess',9),(12,'addUser',0),(12,'editUser',1),(12,'deleteUser',2),(12,'manageUser',3),(12,'activateUser',4),(12,'manageRole',5),(12,'managePermission',6),(12,'viewSecurity',7),(12,'updateSecurity',8),(12,'mdsSchemaAccess',9),(12,'mdsSettingsAccess',10),(13,'addUser',0),(13,'editUser',1),(13,'deleteUser',2),(13,'manageUser',3),(13,'activateUser',4),(13,'manageRole',5),(13,'managePermission',6),(13,'viewSecurity',7),(13,'updateSecurity',8),(13,'mdsSchemaAccess',9),(13,'mdsSettingsAccess',10),(13,'mdsDataAccess',11),(14,'addUser',0),(14,'editUser',1),(14,'deleteUser',2),(14,'manageUser',3),(14,'activateUser',4),(14,'manageRole',5),(14,'managePermission',6),(14,'viewSecurity',7),(14,'updateSecurity',8),(14,'mdsSchemaAccess',9),(14,'mdsSettingsAccess',10),(14,'mdsDataAccess',11),(14,'viewBasicEmailLogs',12),(15,'addUser',0),(15,'editUser',1),(15,'deleteUser',2),(15,'manageUser',3),(15,'activateUser',4),(15,'manageRole',5),(15,'managePermission',6),(15,'viewSecurity',7),(15,'updateSecurity',8),(15,'mdsSchemaAccess',9),(15,'mdsSettingsAccess',10),(15,'mdsDataAccess',11),(15,'viewBasicEmailLogs',12),(15,'viewDetailedEmailLogs',13),(16,'addUser',0),(16,'editUser',1),(16,'deleteUser',2),(16,'manageUser',3),(16,'activateUser',4),(16,'manageRole',5),(16,'managePermission',6),(16,'viewSecurity',7),(16,'updateSecurity',8),(16,'mdsSchemaAccess',9),(16,'mdsSettingsAccess',10),(16,'mdsDataAccess',11),(16,'viewBasicEmailLogs',12),(16,'viewDetailedEmailLogs',13),(16,'viewUser',14),(17,'addUser',0),(17,'editUser',1),(17,'deleteUser',2),(17,'manageUser',3),(17,'activateUser',4),(17,'manageRole',5),(17,'managePermission',6),(17,'viewSecurity',7),(17,'updateSecurity',8),(17,'mdsSchemaAccess',9),(17,'mdsSettingsAccess',10),(17,'mdsDataAccess',11),(17,'viewBasicEmailLogs',12),(17,'viewDetailedEmailLogs',13),(17,'viewUser',14),(17,'viewRole',15),(18,'viewDetailedEmailLogs',0),(18,'viewBasicEmailLogs',1),(19,'viewBasicEmailLogs',0),(20,'viewDetailedEmailLogs',0),(20,'viewBasicEmailLogs',1),(21,'viewBasicEmailLogs',0),(22,'addUser',0),(22,'editUser',1),(22,'deleteUser',2),(22,'manageUser',3),(22,'activateUser',4),(22,'manageRole',5),(22,'managePermission',6),(22,'viewSecurity',7),(22,'updateSecurity',8),(22,'mdsSchemaAccess',9),(22,'mdsSettingsAccess',10),(22,'mdsDataAccess',11),(22,'viewBasicEmailLogs',12),(22,'viewDetailedEmailLogs',13),(22,'viewUser',14),(22,'viewRole',15),(22,'startBundle',16),(23,'addUser',0),(23,'editUser',1),(23,'deleteUser',2),(23,'manageUser',3),(23,'activateUser',4),(23,'manageRole',5),(23,'managePermission',6),(23,'viewSecurity',7),(23,'updateSecurity',8),(23,'mdsSchemaAccess',9),(23,'mdsSettingsAccess',10),(23,'mdsDataAccess',11),(23,'viewBasicEmailLogs',12),(23,'viewDetailedEmailLogs',13),(23,'viewUser',14),(23,'viewRole',15),(23,'startBundle',16),(23,'stopBundle',17),(24,'addUser',0),(24,'editUser',1),(24,'deleteUser',2),(24,'manageUser',3),(24,'activateUser',4),(24,'manageRole',5),(24,'managePermission',6),(24,'viewSecurity',7),(24,'updateSecurity',8),(24,'mdsSchemaAccess',9),(24,'mdsSettingsAccess',10),(24,'mdsDataAccess',11),(24,'viewBasicEmailLogs',12),(24,'viewDetailedEmailLogs',13),(24,'viewUser',14),(24,'viewRole',15),(24,'startBundle',16),(24,'stopBundle',17),(24,'manageBundles',18),(25,'addUser',0),(25,'editUser',1),(25,'deleteUser',2),(25,'manageUser',3),(25,'activateUser',4),(25,'manageRole',5),(25,'managePermission',6),(25,'viewSecurity',7),(25,'updateSecurity',8),(25,'mdsSchemaAccess',9),(25,'mdsSettingsAccess',10),(25,'mdsDataAccess',11),(25,'viewBasicEmailLogs',12),(25,'viewDetailedEmailLogs',13),(25,'viewUser',14),(25,'viewRole',15),(25,'startBundle',16),(25,'stopBundle',17),(25,'manageBundles',18),(25,'installBundle',19),(26,'addUser',0),(26,'editUser',1),(26,'deleteUser',2),(26,'manageUser',3),(26,'activateUser',4),(26,'manageRole',5),(26,'managePermission',6),(26,'viewSecurity',7),(26,'updateSecurity',8),(26,'mdsSchemaAccess',9),(26,'mdsSettingsAccess',10),(26,'mdsDataAccess',11),(26,'viewBasicEmailLogs',12),(26,'viewDetailedEmailLogs',13),(26,'viewUser',14),(26,'viewRole',15),(26,'startBundle',16),(26,'stopBundle',17),(26,'manageBundles',18),(26,'installBundle',19),(26,'bundleDetails',20),(27,'addUser',0),(27,'editUser',1),(27,'deleteUser',2),(27,'manageUser',3),(27,'activateUser',4),(27,'manageRole',5),(27,'managePermission',6),(27,'viewSecurity',7),(27,'updateSecurity',8),(27,'mdsSchemaAccess',9),(27,'mdsSettingsAccess',10),(27,'mdsDataAccess',11),(27,'viewBasicEmailLogs',12),(27,'viewDetailedEmailLogs',13),(27,'viewUser',14),(27,'viewRole',15),(27,'startBundle',16),(27,'stopBundle',17),(27,'manageBundles',18),(27,'installBundle',19),(27,'bundleDetails',20),(27,'uninstallBundle',21),(28,'manageBundles',0),(28,'stopBundle',1),(28,'startBundle',2),(28,'uninstallBundle',3),(28,'installBundle',4),(28,'bundleDetails',5),(29,'manageBundles',0),(29,'stopBundle',1),(29,'startBundle',2),(29,'uninstallBundle',3),(29,'installBundle',4),(29,'bundleDetails',5);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__HISTORY_PERMISSIONNAMES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deletable` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `roleName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES` (
  `permissionNames_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`permissionNames_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES_N49` (`permissionNames_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES_FK1` FOREIGN KEY (`permissionNames_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHROLE__TRASH_PERMISSIONNAMES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `origin` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `pattern` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `priority` int(11) NOT NULL,
  `protocol` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `rest` bit(1) NOT NULL,
  `version` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_N49` (`origin`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_N50` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` VALUES (1,'','2015-04-23 15:10:27','','\0','2015-04-23 15:10:27','','SYSTEM_PLATFORM','','/**/resources/**',1,'HTTP','\0','0.22'),(2,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','','SYSTEM_PLATFORM','','/**/server/login*',1,'HTTP','\0','0.22'),(3,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','','SYSTEM_PLATFORM','','/**/forgot*',1,'HTTP','\0','0.22'),(4,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','','SYSTEM_PLATFORM','','/**/server/lang/**',1,'HTTP','\0','0.22'),(5,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','','SYSTEM_PLATFORM','','/**/server/startup*',1,'HTTP','\0','0.22'),(6,'','2015-04-23 15:10:29','','\0','2015-04-23 15:10:29','','SYSTEM_PLATFORM','','/**/web-api/**',1,'HTTP','','0.22'),(7,'','2015-04-23 15:10:29','','\0','2015-04-23 15:10:29','','SYSTEM_PLATFORM','','/**',0,'HTTP','\0','0.22'),(8,'','2015-04-23 15:10:29','','\0','2015-04-23 15:10:29','','SYSTEM_PLATFORM','','/**/mds/rest/**',2,'HTTP','','0.25');
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED` (
  `methodsRequired_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`methodsRequired_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED_N49` (`methodsRequired_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED_FK1` FOREIGN KEY (`methodsRequired_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED` VALUES (1,'ANY',0),(2,'ANY',0),(3,'ANY',0),(4,'ANY',0),(5,'ANY',0),(6,'ANY',0),(7,'ANY',0),(8,'ANY',0);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_METHODSREQUIRED` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS` (
  `permissionAccess_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`permissionAccess_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS_N49` (`permissionAccess_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS_FK1` FOREIGN KEY (`permissionAccess_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_PERMISSIONACCESS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES` (
  `supportedSchemes_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`supportedSchemes_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES_N49` (`supportedSchemes_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES_FK1` FOREIGN KEY (`supportedSchemes_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES` VALUES (1,'NO_SECURITY',0),(2,'NO_SECURITY',0),(3,'NO_SECURITY',0),(4,'NO_SECURITY',0),(5,'NO_SECURITY',0),(6,'BASIC',0),(6,'USERNAME_PASSWORD',1),(7,'USERNAME_PASSWORD',0),(7,'OPEN_ID',1),(8,'BASIC',0);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_SUPPORTEDSCHEMES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS` (
  `userAccess_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`userAccess_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS_N49` (`userAccess_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS_FK1` FOREIGN KEY (`userAccess_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE_USERACCESS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `motechURLSecurityRule__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `motechURLSecurityRule__HistoryFromTrash` bit(1) DEFAULT NULL,
  `motechURLSecurityRule__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `origin` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `pattern` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `priority` int(11) NOT NULL,
  `protocol` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `rest` bit(1) NOT NULL,
  `version` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` VALUES (1,'','2015-04-23 15:10:27','','\0','2015-04-23 15:10:27','',1,'\0',1,'SYSTEM_PLATFORM','','/**/resources/**',1,'HTTP','\0','0.22'),(2,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','',2,'\0',1,'SYSTEM_PLATFORM','','/**/server/login*',1,'HTTP','\0','0.22'),(3,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','',3,'\0',1,'SYSTEM_PLATFORM','','/**/forgot*',1,'HTTP','\0','0.22'),(4,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','',4,'\0',1,'SYSTEM_PLATFORM','','/**/server/lang/**',1,'HTTP','\0','0.22'),(5,'','2015-04-23 15:10:28','','\0','2015-04-23 15:10:28','',5,'\0',1,'SYSTEM_PLATFORM','','/**/server/startup*',1,'HTTP','\0','0.22'),(6,'','2015-04-23 15:10:29','','\0','2015-04-23 15:10:29','',6,'\0',1,'SYSTEM_PLATFORM','','/**/web-api/**',1,'HTTP','','0.22'),(7,'','2015-04-23 15:10:29','','\0','2015-04-23 15:10:29','',7,'\0',1,'SYSTEM_PLATFORM','','/**',0,'HTTP','\0','0.22'),(8,'','2015-04-23 15:10:29','','\0','2015-04-23 15:10:29','',8,'\0',1,'SYSTEM_PLATFORM','','/**/mds/rest/**',2,'HTTP','','0.25');
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww` (
  `methodsRequired_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`methodsRequired_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHOu2ku_N49` (`methodsRequired_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHOu2ku_FK1` FOREIGN KEY (`methodsRequired_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww` VALUES (1,'ANY',0),(2,'ANY',0),(3,'ANY',0),(4,'ANY',0),(5,'ANY',0),(6,'ANY',0),(7,'ANY',0),(8,'ANY',0);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_METHODSREv0ww` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMISSIO7zc4`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMISSIO7zc4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMISSIO7zc4` (
  `permissionAccess_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`permissionAccess_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMIjhia_N49` (`permissionAccess_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMIjhia_FK1` FOREIGN KEY (`permissionAccess_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMISSIO7zc4`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMISSIO7zc4` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMISSIO7zc4` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_PERMISSIO7zc4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt` (
  `supportedSchemes_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`supportedSchemes_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPOvzuz_N49` (`supportedSchemes_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPOvzuz_FK1` FOREIGN KEY (`supportedSchemes_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt` VALUES (1,'NO_SECURITY',0),(2,'NO_SECURITY',0),(3,'NO_SECURITY',0),(4,'NO_SECURITY',0),(5,'NO_SECURITY',0),(6,'BASIC',0),(6,'USERNAME_PASSWORD',1),(7,'USERNAME_PASSWORD',0),(7,'OPEN_ID',1),(8,'BASIC',0);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_SUPPORTED64dt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERACCESS`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERACCESS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERACCESS` (
  `userAccess_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`userAccess_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERAwmzk_N49` (`userAccess_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERAwmzk_FK1` FOREIGN KEY (`userAccess_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERACCESS`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERACCESS` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERACCESS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__HISTORY_USERACCESS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `origin` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `pattern` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `priority` int(11) NOT NULL,
  `protocol` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `rest` bit(1) NOT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `version` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSREQUIRED`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSREQUIRED`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSREQUIRED` (
  `methodsRequired_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`methodsRequired_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSyzr0_N49` (`methodsRequired_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSyzr0_FK1` FOREIGN KEY (`methodsRequired_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSREQUIRED`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSREQUIRED` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSREQUIRED` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_METHODSREQUIRED` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSIONAy6f4`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSIONAy6f4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSIONAy6f4` (
  `permissionAccess_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`permissionAccess_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSfqog_N49` (`permissionAccess_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSfqog_FK1` FOREIGN KEY (`permissionAccess_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSIONAy6f4`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSIONAy6f4` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSIONAy6f4` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_PERMISSIONAy6f4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORTEDSC7qib`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORTEDSC7qib`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORTEDSC7qib` (
  `supportedSchemes_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`supportedSchemes_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORT21ss_N49` (`supportedSchemes_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORT21ss_FK1` FOREIGN KEY (`supportedSchemes_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORTEDSC7qib`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORTEDSC7qib` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORTEDSC7qib` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_SUPPORTEDSC7qib` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS` (
  `userAccess_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`userAccess_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS_N49` (`userAccess_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS_FK1` FOREIGN KEY (`userAccess_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHURLSECURITYRULE__TRASH_USERACCESS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHUSER`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHUSER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHUSER` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `externalId` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `locale` varchar(20) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `openId` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `password` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `userName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER_N49` (`email`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER_N52` (`openId`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER_N50` (`userName`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER_N51` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHUSER`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHUSER` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHUSER` VALUES (1,'','2015-04-23 15:10:45','','motech@motech.pl',NULL,'en','2015-04-23 15:10:45','','','','$2a$10$diF.XuzJBEPqnMlEastO/uon6F1OY2.yJnZx2yH4PTRBlId/2Hu2y','motech');
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES` (
  `roles_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`roles_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES_N49` (`roles_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES_FK1` FOREIGN KEY (`roles_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHUSER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES` VALUES (1,'Motech Admin',0);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER_ROLES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `externalId` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `locale` varchar(20) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `motechUser__HistoryCurrentVersion` bigint(20) DEFAULT NULL,
  `motechUser__HistoryFromTrash` bit(1) DEFAULT NULL,
  `motechUser__HistorySchemaVersion` bigint(20) DEFAULT NULL,
  `openId` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `password` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `userName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY` VALUES (1,'','2015-04-23 15:10:45','','motech@motech.pl',NULL,'en','2015-04-23 15:10:45','',1,'\0',1,'','','$2a$10$diF.XuzJBEPqnMlEastO/uon6F1OY2.yJnZx2yH4PTRBlId/2Hu2y','motech');
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES` (
  `roles_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`roles_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES_N49` (`roles_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES_FK1` FOREIGN KEY (`roles_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES` DISABLE KEYS */;
INSERT INTO `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES` VALUES (1,'Motech Admin',0);
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY_ROLES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `externalId` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `locale` varchar(20) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `openId` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `password` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `userName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES` (
  `roles_OID` bigint(20) NOT NULL,
  `ELEMENT` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`roles_OID`,`IDX`),
  KEY `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES_N49` (`roles_OID`),
  CONSTRAINT `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES_FK1` FOREIGN KEY (`roles_OID`) REFERENCES `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES`
--

LOCK TABLES `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_MOTECHUSER__TRASH_ROLES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_PASSWORDRECOVERY`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_PASSWORDRECOVERY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_PASSWORDRECOVERY` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `expirationDate` datetime DEFAULT NULL,
  `locale` varchar(20) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `token` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `username` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_PASSWORDRECOVERY_N52` (`id`),
  KEY `MOTECH_WEB_SECURITY_PASSWORDRECOVERY_N51` (`username`),
  KEY `MOTECH_WEB_SECURITY_PASSWORDRECOVERY_N50` (`token`),
  KEY `MOTECH_WEB_SECURITY_PASSWORDRECOVERY_N49` (`expirationDate`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_PASSWORDRECOVERY`
--

LOCK TABLES `MOTECH_WEB_SECURITY_PASSWORDRECOVERY` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_PASSWORDRECOVERY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_PASSWORDRECOVERY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH`
--

DROP TABLE IF EXISTS `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `creator` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `expirationDate` datetime DEFAULT NULL,
  `locale` varchar(20) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `modificationDate` datetime NOT NULL,
  `modifiedBy` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `owner` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `schemaVersion` bigint(20) DEFAULT NULL,
  `token` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `username` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH_N49` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH`
--

LOCK TABLES `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH` WRITE;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH` DISABLE KEYS */;
/*!40000 ALTER TABLE `MOTECH_WEB_SECURITY_PASSWORDRECOVERY__TRASH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RestOptions`
--

DROP TABLE IF EXISTS `RestOptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RestOptions` (
  `id` bigint(20) NOT NULL,
  `allowCreate` bit(1) NOT NULL,
  `allowDelete` bit(1) NOT NULL,
  `allowRead` bit(1) NOT NULL,
  `allowUpdate` bit(1) NOT NULL,
  `entity_id_OID` bigint(20) DEFAULT NULL,
  `modifiedByUser` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `RestOptions_N49` (`entity_id_OID`),
  CONSTRAINT `RestOptions_FK1` FOREIGN KEY (`entity_id_OID`) REFERENCES `Entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RestOptions`
--

LOCK TABLES `RestOptions` WRITE;
/*!40000 ALTER TABLE `RestOptions` DISABLE KEYS */;
INSERT INTO `RestOptions` VALUES (1,'\0','\0','\0','\0',1,'\0'),(2,'\0','\0','\0','\0',2,'\0'),(3,'\0','\0','\0','\0',3,'\0'),(4,'\0','\0','\0','\0',4,'\0'),(5,'\0','\0','\0','\0',5,'\0'),(6,'\0','\0','\0','\0',6,'\0'),(7,'\0','\0','\0','\0',7,'\0'),(8,'\0','\0','\0','\0',8,'\0'),(9,'\0','\0','\0','\0',9,'\0'),(10,'\0','\0','\0','\0',10,'\0'),(11,'\0','\0','\0','\0',11,'\0'),(12,'\0','\0','\0','\0',12,'\0'),(13,'\0','\0','\0','\0',13,'\0'),(14,'\0','\0','\0','\0',14,'\0'),(15,'\0','\0','\0','\0',15,'\0'),(16,'\0','\0','\0','\0',16,'\0'),(17,'\0','\0','\0','\0',17,'\0'),(18,'\0','\0','\0','\0',18,'\0'),(19,'\0','\0','\0','\0',19,'\0'),(20,'\0','\0','\0','\0',20,'\0'),(21,'\0','\0','\0','\0',21,'\0'),(22,'\0','\0','\0','\0',22,'\0'),(23,'\0','\0','\0','\0',23,'\0'),(24,'\0','\0','\0','\0',24,'\0'),(25,'\0','\0','\0','\0',25,'\0'),(26,'\0','\0','\0','\0',26,'\0'),(27,'\0','\0','\0','\0',27,'\0'),(28,'\0','\0','\0','\0',28,'\0'),(29,'\0','\0','\0','\0',29,'\0'),(30,'\0','\0','\0','\0',30,'\0'),(31,'\0','\0','\0','\0',31,'\0'),(32,'\0','\0','\0','\0',32,'\0'),(33,'\0','\0','\0','\0',33,'\0');
/*!40000 ALTER TABLE `RestOptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQUENCE_TABLE`
--

DROP TABLE IF EXISTS `SEQUENCE_TABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQUENCE_TABLE` (
  `SEQUENCE_NAME` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `NEXT_VAL` bigint(20) NOT NULL,
  PRIMARY KEY (`SEQUENCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQUENCE_TABLE`
--

LOCK TABLES `SEQUENCE_TABLE` WRITE;
/*!40000 ALTER TABLE `SEQUENCE_TABLE` DISABLE KEYS */;
INSERT INTO `SEQUENCE_TABLE` VALUES ('org.motechproject.admin.domain.StatusMessage',11),('org.motechproject.config.domain.ModulePropertiesRecord',11),('org.motechproject.config.domain.history.ModulePropertiesRecord__History',11),('org.motechproject.mds.domain.Entity',41),('org.motechproject.mds.domain.Field',371),('org.motechproject.mds.domain.FieldMetadata',261),('org.motechproject.mds.domain.FieldSetting',301),('org.motechproject.mds.domain.FieldValidation',301),('org.motechproject.mds.domain.Lookup',21),('org.motechproject.mds.domain.RestOptions',41),('org.motechproject.mds.domain.Tracking',41),('org.motechproject.mds.domain.Type',21),('org.motechproject.mds.domain.TypeSetting',11),('org.motechproject.mds.domain.TypeSettingOption',11),('org.motechproject.mds.domain.TypeValidation',21),('org.motechproject.security.domain.MotechPermission',31),('org.motechproject.security.domain.MotechRole',11),('org.motechproject.security.domain.MotechURLSecurityRule',11),('org.motechproject.security.domain.MotechUser',11),('org.motechproject.security.domain.history.MotechRole__History',31),('org.motechproject.security.domain.history.MotechURLSecurityRule__History',11),('org.motechproject.security.domain.history.MotechUser__History',11),('org.motechproject.server.config.domain.SettingsRecord',11),('org.motechproject.server.config.domain.history.SettingsRecord__History',11),('org.motechproject.tasks.domain.Channel',11),('org.motechproject.tasks.domain.LookupFieldsParameter',61),('org.motechproject.tasks.domain.Parameter',1091),('org.motechproject.tasks.domain.TaskDataProvider',11),('org.motechproject.tasks.domain.TaskDataProviderObject',41),('org.motechproject.tasks.domain.TaskEvent',151);
/*!40000 ALTER TABLE `SEQUENCE_TABLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SchemaChangeLock`
--

DROP TABLE IF EXISTS `SchemaChangeLock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SchemaChangeLock` (
  `id` bigint(20) NOT NULL,
  `lockId` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `lockId` (`lockId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SchemaChangeLock`
--

LOCK TABLES `SchemaChangeLock` WRITE;
/*!40000 ALTER TABLE `SchemaChangeLock` DISABLE KEYS */;
INSERT INTO `SchemaChangeLock` VALUES (1,1);
/*!40000 ALTER TABLE `SchemaChangeLock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TYPE_SETTING_SETTING_OPTION`
--

DROP TABLE IF EXISTS `TYPE_SETTING_SETTING_OPTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TYPE_SETTING_SETTING_OPTION` (
  `TYPE_SETTING_ID_OID` bigint(20) NOT NULL,
  `SETTING_OPTION_ID_EID` bigint(20) DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`TYPE_SETTING_ID_OID`,`IDX`),
  KEY `TYPE_SETTING_SETTING_OPTION_N49` (`TYPE_SETTING_ID_OID`),
  KEY `TYPE_SETTING_SETTING_OPTION_N50` (`SETTING_OPTION_ID_EID`),
  CONSTRAINT `TYPE_SETTING_SETTING_OPTION_FK2` FOREIGN KEY (`SETTING_OPTION_ID_EID`) REFERENCES `TypeSettingOption` (`id`),
  CONSTRAINT `TYPE_SETTING_SETTING_OPTION_FK1` FOREIGN KEY (`TYPE_SETTING_ID_OID`) REFERENCES `TypeSetting` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TYPE_SETTING_SETTING_OPTION`
--

LOCK TABLES `TYPE_SETTING_SETTING_OPTION` WRITE;
/*!40000 ALTER TABLE `TYPE_SETTING_SETTING_OPTION` DISABLE KEYS */;
INSERT INTO `TYPE_SETTING_SETTING_OPTION` VALUES (1,1,0),(2,1,0),(3,1,0),(10,1,0),(1,2,1),(2,2,1),(10,2,1);
/*!40000 ALTER TABLE `TYPE_SETTING_SETTING_OPTION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TYPE_TYPE_SETTING`
--

DROP TABLE IF EXISTS `TYPE_TYPE_SETTING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TYPE_TYPE_SETTING` (
  `TYPE_ID_OID` bigint(20) NOT NULL,
  `TYPE_SETTING_ID_EID` bigint(20) DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`TYPE_ID_OID`,`IDX`),
  KEY `TYPE_TYPE_SETTING_N49` (`TYPE_SETTING_ID_EID`),
  KEY `TYPE_TYPE_SETTING_N50` (`TYPE_ID_OID`),
  CONSTRAINT `TYPE_TYPE_SETTING_FK1` FOREIGN KEY (`TYPE_ID_OID`) REFERENCES `Type` (`id`),
  CONSTRAINT `TYPE_TYPE_SETTING_FK2` FOREIGN KEY (`TYPE_SETTING_ID_EID`) REFERENCES `TypeSetting` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TYPE_TYPE_SETTING`
--

LOCK TABLES `TYPE_TYPE_SETTING` WRITE;
/*!40000 ALTER TABLE `TYPE_TYPE_SETTING` DISABLE KEYS */;
INSERT INTO `TYPE_TYPE_SETTING` VALUES (7,1,0),(7,2,1),(8,3,0),(8,4,1),(8,5,2),(16,6,0),(17,6,0),(18,6,0),(19,6,0),(20,6,0),(16,7,1),(17,7,1),(18,7,1),(19,7,1),(20,7,1),(16,8,2),(17,8,2),(18,8,2),(19,8,2),(20,8,2),(2,9,0),(2,10,1);
/*!40000 ALTER TABLE `TYPE_TYPE_SETTING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TYPE_TYPE_VALIDATION`
--

DROP TABLE IF EXISTS `TYPE_TYPE_VALIDATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TYPE_TYPE_VALIDATION` (
  `TYPE_ID_OID` bigint(20) NOT NULL,
  `TYPE_VALIDATION_ID_EID` bigint(20) DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`TYPE_ID_OID`,`IDX`),
  KEY `TYPE_TYPE_VALIDATION_N50` (`TYPE_VALIDATION_ID_EID`),
  KEY `TYPE_TYPE_VALIDATION_N49` (`TYPE_ID_OID`),
  CONSTRAINT `TYPE_TYPE_VALIDATION_FK2` FOREIGN KEY (`TYPE_VALIDATION_ID_EID`) REFERENCES `TypeValidation` (`id`),
  CONSTRAINT `TYPE_TYPE_VALIDATION_FK1` FOREIGN KEY (`TYPE_ID_OID`) REFERENCES `Type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TYPE_TYPE_VALIDATION`
--

LOCK TABLES `TYPE_TYPE_VALIDATION` WRITE;
/*!40000 ALTER TABLE `TYPE_TYPE_VALIDATION` DISABLE KEYS */;
INSERT INTO `TYPE_TYPE_VALIDATION` VALUES (1,1,0),(1,2,1),(1,3,2),(1,4,3),(2,5,0),(2,6,1),(2,7,2),(7,8,0),(7,9,1),(7,10,2),(7,11,3);
/*!40000 ALTER TABLE `TYPE_TYPE_VALIDATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tracking`
--

DROP TABLE IF EXISTS `Tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tracking` (
  `id` bigint(20) NOT NULL,
  `entity_id_OID` bigint(20) DEFAULT NULL,
  `recordHistory` bit(1) NOT NULL,
  `allowCreateEvent` bit(1) NOT NULL DEFAULT b'1',
  `allowDeleteEvent` bit(1) NOT NULL DEFAULT b'1',
  `allowUpdateEvent` bit(1) NOT NULL DEFAULT b'1',
  `modifiedByUser` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `Tracking_N49` (`entity_id_OID`),
  CONSTRAINT `Tracking_FK1` FOREIGN KEY (`entity_id_OID`) REFERENCES `Entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tracking`
--

LOCK TABLES `Tracking` WRITE;
/*!40000 ALTER TABLE `Tracking` DISABLE KEYS */;
INSERT INTO `Tracking` VALUES (1,1,'','','','','\0'),(2,2,'','','','','\0'),(3,3,'\0','','','','\0'),(4,4,'','','','','\0'),(5,5,'','','','','\0'),(6,6,'','','','','\0'),(7,7,'\0','','','','\0'),(8,8,'\0','\0','\0','\0','\0'),(9,9,'\0','\0','\0','\0','\0'),(10,10,'','\0','\0','\0','\0'),(11,11,'\0','\0','\0','\0','\0'),(12,12,'\0','\0','\0','\0','\0'),(13,13,'','\0','\0','\0','\0'),(14,14,'\0','\0','\0','\0','\0'),(15,15,'\0','\0','\0','\0','\0'),(16,16,'\0','\0','\0','\0','\0'),(17,17,'','\0','\0','\0','\0'),(18,18,'\0','\0','\0','\0','\0'),(19,19,'\0','\0','\0','\0','\0'),(20,20,'','\0','\0','\0','\0'),(21,21,'','\0','\0','\0','\0'),(22,22,'\0','\0','\0','\0','\0'),(23,23,'\0','','','','\0'),(24,24,'','\0','\0','\0','\0'),(25,25,'','','','','\0'),(26,26,'\0','','','','\0'),(27,27,'','\0','\0','\0','\0'),(28,28,'','\0','\0','\0','\0'),(29,29,'','\0','\0','\0','\0'),(30,30,'\0','','','','\0'),(31,31,'\0','','','','\0'),(32,32,'','','','','\0'),(33,33,'\0','','','','\0');
/*!40000 ALTER TABLE `Tracking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Type`
--

DROP TABLE IF EXISTS `Type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Type` (
  `id` bigint(20) NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `defaultName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `typeClass` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Type`
--

LOCK TABLES `Type` WRITE;
/*!40000 ALTER TABLE `Type` DISABLE KEYS */;
INSERT INTO `Type` VALUES (1,'mds.field.description.integer','mds.field.integer','integer','java.lang.Integer'),(2,'mds.field.description.string','mds.field.string','str','java.lang.String'),(3,'mds.field.description.boolean','mds.field.boolean','bool','java.lang.Boolean'),(4,'mds.field.description.date','mds.field.javaUtilDate','date','java.util.Date'),(5,'mds.field.description.time','mds.field.time','time','org.motechproject.commons.date.model.Time'),(6,'mds.field.description.datetime','mds.field.datetime','datetime','org.joda.time.DateTime'),(7,'mds.field.description.decimal','mds.field.decimal','dec','java.lang.Double'),(8,'mds.field.description.combobox','mds.field.combobox','list','java.util.List'),(10,'mds.field.description.map','mds.field.map','map','java.util.Map'),(11,'mds.field.description.period','mds.field.period','period','org.joda.time.Period'),(12,'mds.field.description.locale','mds.field.locale','locale','java.util.Locale'),(13,'mds.field.description.blob','mds.field.blob','blob','[Ljava.lang.Byte;'),(14,'mds.field.description.localDate','mds.field.date','date','org.joda.time.LocalDate'),(15,'mds.field.description.long','mds.field.long','long','java.lang.Long'),(16,'mds.field.description.relationship','mds.field.relationship','relationship','org.motechproject.mds.domain.Relationship'),(17,'mds.field.description.relationship.oneToMany','mds.field.relationship.oneToMany','oneToManyRelationship','org.motechproject.mds.domain.OneToManyRelationship'),(18,'mds.field.description.relationship.oneToOne','mds.field.relationship.oneToOne','oneToOneRelationship','org.motechproject.mds.domain.OneToOneRelationship'),(19,'mds.field.description.relationship.manyToOne','mds.field.relationship.manyToOne','manyToOneRelationship','org.motechproject.mds.domain.ManyToOneRelationship'),(20,'mds.field.description.relationship.manyToMany','mds.field.relationship.manyToMany','manyToManyRelationship','org.motechproject.mds.domain.ManyToManyRelationship');
/*!40000 ALTER TABLE `Type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TypeSetting`
--

DROP TABLE IF EXISTS `TypeSetting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TypeSetting` (
  `id` bigint(20) NOT NULL,
  `defaultValue` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `TYPE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `TypeSetting_N49` (`TYPE_ID`),
  CONSTRAINT `TypeSetting_FK1` FOREIGN KEY (`TYPE_ID`) REFERENCES `Type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TypeSetting`
--

LOCK TABLES `TypeSetting` WRITE;
/*!40000 ALTER TABLE `TypeSetting` DISABLE KEYS */;
INSERT INTO `TypeSetting` VALUES (1,'9','mds.form.label.precision',1),(2,'2','mds.form.label.scale',1),(3,'[]','mds.form.label.values',8),(4,'false','mds.form.label.allowUserSupplied',3),(5,'false','mds.form.label.allowMultipleSelections',3),(6,'true','mds.form.label.cascadePersist',3),(7,'true','mds.form.label.cascadeUpdate',3),(8,'false','mds.form.label.cascadeDelete',3),(9,'false','mds.form.label.textarea',3),(10,'255','mds.form.label.maxTextLength',1);
/*!40000 ALTER TABLE `TypeSetting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TypeSettingOption`
--

DROP TABLE IF EXISTS `TypeSettingOption`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TypeSettingOption` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TypeSettingOption`
--

LOCK TABLES `TypeSettingOption` WRITE;
/*!40000 ALTER TABLE `TypeSettingOption` DISABLE KEYS */;
INSERT INTO `TypeSettingOption` VALUES (1,'REQUIRE'),(2,'POSITIVE');
/*!40000 ALTER TABLE `TypeSettingOption` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TypeValidation`
--

DROP TABLE IF EXISTS `TypeValidation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TypeValidation` (
  `id` bigint(20) NOT NULL,
  `displayName` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `TYPE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `TypeValidation_N49` (`TYPE_ID`),
  CONSTRAINT `TypeValidation_FK1` FOREIGN KEY (`TYPE_ID`) REFERENCES `Type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TypeValidation`
--

LOCK TABLES `TypeValidation` WRITE;
/*!40000 ALTER TABLE `TypeValidation` DISABLE KEYS */;
INSERT INTO `TypeValidation` VALUES (1,'mds.field.validation.minValue',1),(2,'mds.field.validation.maxValue',1),(3,'mds.field.validation.mustBeInSet',2),(4,'mds.field.validation.cannotBeInSet',2),(5,'mds.field.validation.regex',2),(6,'mds.field.validation.minLength',1),(7,'mds.field.validation.maxLength',1),(8,'mds.field.validation.minValue',7),(9,'mds.field.validation.maxValue',7),(10,'mds.field.validation.mustBeInSet',2),(11,'mds.field.validation.cannotBeInSet',2);
/*!40000 ALTER TABLE `TypeValidation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TypeValidation_annotations`
--

DROP TABLE IF EXISTS `TypeValidation_annotations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TypeValidation_annotations` (
  `id_OID` bigint(20) NOT NULL,
  `ANNOTATION` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `IDX` int(11) NOT NULL,
  PRIMARY KEY (`id_OID`,`IDX`),
  KEY `TypeValidation_annotations_N49` (`id_OID`),
  CONSTRAINT `TypeValidation_annotations_FK1` FOREIGN KEY (`id_OID`) REFERENCES `TypeValidation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TypeValidation_annotations`
--

LOCK TABLES `TypeValidation_annotations` WRITE;
/*!40000 ALTER TABLE `TypeValidation_annotations` DISABLE KEYS */;
INSERT INTO `TypeValidation_annotations` VALUES (1,'javax.validation.constraints.DecimalMin',0),(1,'javax.validation.constraints.Min',1),(2,'javax.validation.constraints.DecimalMax',0),(2,'javax.validation.constraints.Max',1),(3,'org.motechproject.mds.annotations.InSet',0),(4,'org.motechproject.mds.annotations.NotInSet',0),(5,'javax.validation.constraints.Pattern',0),(6,'javax.validation.constraints.DecimalMin',0),(6,'javax.validation.constraints.Size',1),(7,'javax.validation.constraints.DecimalMax',0),(7,'javax.validation.constraints.Size',1),(8,'javax.validation.constraints.DecimalMin',0),(8,'javax.validation.constraints.Min',1),(9,'javax.validation.constraints.DecimalMax',0),(9,'javax.validation.constraints.Max',1),(10,'org.motechproject.mds.annotations.InSet',0),(11,'org.motechproject.mds.annotations.NotInSet',0);
/*!40000 ALTER TABLE `TypeValidation_annotations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `version_rank` int(11) NOT NULL,
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`version`),
  KEY `schema_version_vr_idx` (`version_rank`),
  KEY `schema_version_ir_idx` (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` VALUES (1,1,'1','Base version','SQL','V1__Base_version.sql',184686645,'root','2015-04-23 13:07:59',2759,1),(10,10,'10','MOTECH-1043','SQL','V10__MOTECH-1043.sql',-153979283,'root','2015-04-23 13:08:00',56,1),(11,11,'11','MOTECH-1046','SQL','V11__MOTECH-1046.sql',261886118,'root','2015-04-23 13:08:00',57,1),(12,12,'12','MOTECH-1109','SQL','V12__MOTECH-1109.sql',-1592196201,'root','2015-04-23 13:08:00',48,1),(13,13,'13','MOTECH-1044','SQL','V13__MOTECH-1044.sql',1490520512,'root','2015-04-23 13:08:00',48,1),(14,14,'14','MOTECH-993','SQL','V14__MOTECH-993.sql',861805926,'root','2015-04-23 13:08:01',512,1),(15,15,'15','MOTECH-1134','SQL','V15__MOTECH-1134.sql',46949703,'root','2015-04-23 13:08:01',50,1),(16,16,'16','MOTECH-1178','SQL','V16__MOTECH-1178.sql',1477109671,'root','2015-04-23 13:08:01',52,1),(17,17,'17','MOTECH-1108','SQL','V17__MOTECH-1108.sql',1187387715,'root','2015-04-23 13:08:01',56,1),(18,18,'18','MOTECH-1047','SQL','V18__MOTECH-1047.sql',-54491000,'root','2015-04-23 13:08:01',54,1),(19,19,'19','MOTECH-1356','SQL','V19__MOTECH-1356.sql',-1598048323,'root','2015-04-23 13:08:02',226,1),(2,2,'2','MOTECH-794','SQL','V2__MOTECH-794.sql',-1085096961,'root','2015-04-23 13:07:59',52,1),(20,20,'20','MOTECH-1347','SQL','V20__MOTECH-1347.sql',1161459552,'root','2015-04-23 13:08:02',227,1),(21,21,'21','MOTECH-1350','SQL','V21__MOTECH-1350.sql',733643515,'root','2015-04-23 13:08:03',1189,1),(22,22,'22','MOTECH-1399','SQL','V22__MOTECH-1399.sql',-2086471116,'root','2015-04-23 13:08:04',681,1),(23,23,'23','MOTECH-1447','SQL','V23__MOTECH-1447.sql',299566748,'root','2015-04-23 13:08:04',56,1),(24,24,'24','MOTECH-1464','SQL','V24__MOTECH-1464.sql',1619288797,'root','2015-04-23 13:08:04',284,1),(25,25,'25','MOTECH-1460','SQL','V25__MOTECH-1460.sql',-1581612414,'root','2015-04-23 13:08:05',359,1),(26,26,'26','MOTECH-1502','SQL','V26__MOTECH-1502.sql',1444802129,'root','2015-04-23 13:08:05',330,1),(27,27,'27','MOTECH-1516','SQL','V27__MOTECH-1516.sql',1145323936,'root','2015-04-23 13:08:06',290,1),(28,28,'28','MOTECH-1523','SQL','V28__MOTECH-1523.sql',-1565831115,'root','2015-04-23 13:08:06',278,1),(29,29,'29','MOTECH-1546','SQL','V29__MOTECH-1546.sql',-1115361870,'root','2015-04-23 13:08:06',50,1),(3,3,'3','MOTECH-873','SQL','V3__MOTECH-873.sql',75220187,'root','2015-04-23 13:07:59',46,1),(30,30,'30','MOTECH-1618','SQL','V30__MOTECH-1618.sql',2068487648,'root','2015-04-23 13:08:07',267,1),(31,31,'31','MOTECH-1619','SQL','V31__MOTECH-1619.sql',1680682625,'root','2015-04-23 13:08:07',218,1),(32,32,'32','MOTECH-1620','SQL','V32__MOTECH-1620.sql',-1115227186,'root','2015-04-23 13:08:07',322,1),(4,4,'4','MOTECH-811','SQL','V4__MOTECH-811.sql',-292569277,'root','2015-04-23 13:07:59',53,1),(5,5,'5','MOTECH-620','SQL','V5__MOTECH-620.sql',1295012535,'root','2015-04-23 13:07:59',54,1),(6,6,'6','MOTECH-993','SQL','V6__MOTECH-993.sql',1602345073,'root','2015-04-23 13:07:59',51,1),(7,7,'7','MOTECH-860','SQL','V7__MOTECH-860.sql',11405755,'root','2015-04-23 13:08:00',63,1),(8,8,'8','MOTECH-1086','SQL','V8__MOTECH-1086.sql',129137538,'root','2015-04-23 13:08:00',53,1),(9,9,'9','MOTECH-1076','SQL','V9__MOTECH-1076.sql',1939771693,'root','2015-04-23 13:08:00',83,1);
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-23 15:12:49
