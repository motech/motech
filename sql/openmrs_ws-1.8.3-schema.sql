-- MySQL dump 10.13  Distrib 5.5.12, for Win32 (x86)
--
-- Host: localhost    Database: openmrs_api_test
-- ------------------------------------------------------
-- Server version	5.5.12

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
-- Table structure for table `active_list`
--

DROP TABLE IF EXISTS `active_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `active_list` (
  `active_list_id` int(11) NOT NULL AUTO_INCREMENT,
  `active_list_type_id` int(11) NOT NULL,
  `person_id` int(11) NOT NULL,
  `concept_id` int(11) NOT NULL,
  `start_obs_id` int(11) DEFAULT NULL,
  `stop_obs_id` int(11) DEFAULT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`active_list_id`),
  KEY `user_who_voided_active_list` (`voided_by`),
  KEY `user_who_created_active_list` (`creator`),
  KEY `active_list_type_of_active_list` (`active_list_type_id`),
  KEY `person_of_active_list` (`person_id`),
  KEY `concept_active_list` (`concept_id`),
  KEY `start_obs_active_list` (`start_obs_id`),
  KEY `stop_obs_active_list` (`stop_obs_id`),
  CONSTRAINT `stop_obs_active_list` FOREIGN KEY (`stop_obs_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `active_list_type_of_active_list` FOREIGN KEY (`active_list_type_id`) REFERENCES `active_list_type` (`active_list_type_id`),
  CONSTRAINT `concept_active_list` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `person_of_active_list` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `start_obs_active_list` FOREIGN KEY (`start_obs_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `user_who_created_active_list` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_active_list` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `active_list`
--

LOCK TABLES `active_list` WRITE;
/*!40000 ALTER TABLE `active_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `active_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `active_list_allergy`
--

DROP TABLE IF EXISTS `active_list_allergy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `active_list_allergy` (
  `active_list_id` int(11) NOT NULL AUTO_INCREMENT,
  `allergy_type` varchar(50) DEFAULT NULL,
  `reaction_concept_id` int(11) DEFAULT NULL,
  `severity` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`active_list_id`),
  KEY `reaction_allergy` (`reaction_concept_id`),
  CONSTRAINT `reaction_allergy` FOREIGN KEY (`reaction_concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `active_list_allergy`
--

LOCK TABLES `active_list_allergy` WRITE;
/*!40000 ALTER TABLE `active_list_allergy` DISABLE KEYS */;
/*!40000 ALTER TABLE `active_list_allergy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `active_list_problem`
--

DROP TABLE IF EXISTS `active_list_problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `active_list_problem` (
  `active_list_id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(50) DEFAULT NULL,
  `sort_weight` double DEFAULT NULL,
  PRIMARY KEY (`active_list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `active_list_problem`
--

LOCK TABLES `active_list_problem` WRITE;
/*!40000 ALTER TABLE `active_list_problem` DISABLE KEYS */;
/*!40000 ALTER TABLE `active_list_problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `active_list_type`
--

DROP TABLE IF EXISTS `active_list_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `active_list_type` (
  `active_list_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`active_list_type_id`),
  KEY `user_who_retired_active_list_type` (`retired_by`),
  KEY `user_who_created_active_list_type` (`creator`),
  CONSTRAINT `user_who_created_active_list_type` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_active_list_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `active_list_type`
--

LOCK TABLES `active_list_type` WRITE;
/*!40000 ALTER TABLE `active_list_type` DISABLE KEYS */;
INSERT INTO `active_list_type` VALUES (1,'Allergy','An Allergy the Patient may have',1,'2010-05-28 00:00:00',0,NULL,NULL,NULL,'96f4f603-6a99-11df-a648-37a07f9c90fb'),(2,'Problem','A Problem the Patient may have',1,'2010-05-28 00:00:00',0,NULL,NULL,NULL,'a0c7422b-6a99-11df-a648-37a07f9c90fb');
/*!40000 ALTER TABLE `active_list_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cohort`
--

DROP TABLE IF EXISTS `cohort`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cohort` (
  `cohort_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`cohort_id`),
  UNIQUE KEY `cohort_uuid_index` (`uuid`),
  KEY `user_who_changed_cohort` (`changed_by`),
  KEY `cohort_creator` (`creator`),
  KEY `user_who_voided_cohort` (`voided_by`),
  CONSTRAINT `cohort_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_cohort` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_cohort` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cohort`
--

LOCK TABLES `cohort` WRITE;
/*!40000 ALTER TABLE `cohort` DISABLE KEYS */;
/*!40000 ALTER TABLE `cohort` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cohort_member`
--

DROP TABLE IF EXISTS `cohort_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cohort_member` (
  `cohort_id` int(11) NOT NULL DEFAULT '0',
  `patient_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cohort_id`,`patient_id`),
  KEY `member_patient` (`patient_id`),
  CONSTRAINT `member_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `parent_cohort` FOREIGN KEY (`cohort_id`) REFERENCES `cohort` (`cohort_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cohort_member`
--

LOCK TABLES `cohort_member` WRITE;
/*!40000 ALTER TABLE `cohort_member` DISABLE KEYS */;
/*!40000 ALTER TABLE `cohort_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept`
--

DROP TABLE IF EXISTS `concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept` (
  `concept_id` int(11) NOT NULL AUTO_INCREMENT,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `short_name` varchar(255) DEFAULT NULL,
  `description` text,
  `form_text` text,
  `datatype_id` int(11) NOT NULL DEFAULT '0',
  `class_id` int(11) NOT NULL DEFAULT '0',
  `is_set` smallint(6) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `version` varchar(50) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_id`),
  UNIQUE KEY `concept_uuid_index` (`uuid`),
  KEY `user_who_changed_concept` (`changed_by`),
  KEY `concept_classes` (`class_id`),
  KEY `concept_creator` (`creator`),
  KEY `concept_datatypes` (`datatype_id`),
  KEY `user_who_retired_concept` (`retired_by`),
  CONSTRAINT `concept_classes` FOREIGN KEY (`class_id`) REFERENCES `concept_class` (`concept_class_id`),
  CONSTRAINT `concept_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_datatypes` FOREIGN KEY (`datatype_id`) REFERENCES `concept_datatype` (`concept_datatype_id`),
  CONSTRAINT `user_who_changed_concept` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept`
--

LOCK TABLES `concept` WRITE;
/*!40000 ALTER TABLE `concept` DISABLE KEYS */;
INSERT INTO `concept` VALUES (1,0,'','',NULL,4,11,0,1,'2012-09-17 11:29:16',NULL,NULL,NULL,NULL,NULL,NULL,'574ae4df-4fc6-4b51-9e5e-556f64c29ed8'),(2,0,'','',NULL,4,11,0,1,'2012-09-17 11:29:16',NULL,NULL,NULL,NULL,NULL,NULL,'357238e8-deed-4f80-9dda-9a88bf45518e');
/*!40000 ALTER TABLE `concept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_answer`
--

DROP TABLE IF EXISTS `concept_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_answer` (
  `concept_answer_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `answer_concept` int(11) DEFAULT NULL,
  `answer_drug` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `uuid` char(38) NOT NULL,
  `sort_weight` double DEFAULT NULL,
  PRIMARY KEY (`concept_answer_id`),
  UNIQUE KEY `concept_answer_uuid_index` (`uuid`),
  KEY `answer` (`answer_concept`),
  KEY `answers_for_concept` (`concept_id`),
  KEY `answer_creator` (`creator`),
  CONSTRAINT `answer` FOREIGN KEY (`answer_concept`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `answers_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `answer_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_answer`
--

LOCK TABLES `concept_answer` WRITE;
/*!40000 ALTER TABLE `concept_answer` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_class`
--

DROP TABLE IF EXISTS `concept_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_class` (
  `concept_class_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_class_id`),
  UNIQUE KEY `concept_class_uuid_index` (`uuid`),
  KEY `concept_class_retired_status` (`retired`),
  KEY `concept_class_creator` (`creator`),
  KEY `user_who_retired_concept_class` (`retired_by`),
  CONSTRAINT `concept_class_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept_class` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_class`
--

LOCK TABLES `concept_class` WRITE;
/*!40000 ALTER TABLE `concept_class` DISABLE KEYS */;
INSERT INTO `concept_class` VALUES (1,'Test','Acq. during patient encounter (vitals, labs, etc.)',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d4907b2-c2cc-11de-8d13-0010c6dffd0f'),(2,'Procedure','Describes a clinical procedure',1,'2004-03-02 00:00:00',0,NULL,NULL,NULL,'8d490bf4-c2cc-11de-8d13-0010c6dffd0f'),(3,'Drug','Drug',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d490dfc-c2cc-11de-8d13-0010c6dffd0f'),(4,'Diagnosis','Conclusion drawn through findings',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d4918b0-c2cc-11de-8d13-0010c6dffd0f'),(5,'Finding','Practitioner observation/finding',1,'2004-03-02 00:00:00',0,NULL,NULL,NULL,'8d491a9a-c2cc-11de-8d13-0010c6dffd0f'),(6,'Anatomy','Anatomic sites / descriptors',1,'2004-03-02 00:00:00',0,NULL,NULL,NULL,'8d491c7a-c2cc-11de-8d13-0010c6dffd0f'),(7,'Question','Question (eg, patient history, SF36 items)',1,'2004-03-02 00:00:00',0,NULL,NULL,NULL,'8d491e50-c2cc-11de-8d13-0010c6dffd0f'),(8,'LabSet','Term to describe laboratory sets',1,'2004-03-02 00:00:00',0,NULL,NULL,NULL,'8d492026-c2cc-11de-8d13-0010c6dffd0f'),(9,'MedSet','Term to describe medication sets',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d4923b4-c2cc-11de-8d13-0010c6dffd0f'),(10,'ConvSet','Term to describe convenience sets',1,'2004-03-02 00:00:00',0,NULL,NULL,NULL,'8d492594-c2cc-11de-8d13-0010c6dffd0f'),(11,'Misc','Terms which don\'t fit other categories',1,'2004-03-02 00:00:00',0,NULL,NULL,NULL,'8d492774-c2cc-11de-8d13-0010c6dffd0f'),(12,'Symptom','Patient-reported observation',1,'2004-10-04 00:00:00',0,NULL,NULL,NULL,'8d492954-c2cc-11de-8d13-0010c6dffd0f'),(13,'Symptom/Finding','Observation that can be reported from patient or found on exam',1,'2004-10-04 00:00:00',0,NULL,NULL,NULL,'8d492b2a-c2cc-11de-8d13-0010c6dffd0f'),(14,'Specimen','Body or fluid specimen',1,'2004-12-02 00:00:00',0,NULL,NULL,NULL,'8d492d0a-c2cc-11de-8d13-0010c6dffd0f'),(15,'Misc Order','Orderable items which aren\'t tests or drugs',1,'2005-02-17 00:00:00',0,NULL,NULL,NULL,'8d492ee0-c2cc-11de-8d13-0010c6dffd0f');
/*!40000 ALTER TABLE `concept_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_complex`
--

DROP TABLE IF EXISTS `concept_complex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_complex` (
  `concept_id` int(11) NOT NULL,
  `handler` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`concept_id`),
  CONSTRAINT `concept_attributes` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_complex`
--

LOCK TABLES `concept_complex` WRITE;
/*!40000 ALTER TABLE `concept_complex` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_complex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_datatype`
--

DROP TABLE IF EXISTS `concept_datatype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_datatype` (
  `concept_datatype_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `hl7_abbreviation` varchar(3) DEFAULT NULL,
  `description` varchar(255) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_datatype_id`),
  UNIQUE KEY `concept_datatype_uuid_index` (`uuid`),
  KEY `concept_datatype_retired_status` (`retired`),
  KEY `concept_datatype_creator` (`creator`),
  KEY `user_who_retired_concept_datatype` (`retired_by`),
  CONSTRAINT `concept_datatype_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept_datatype` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_datatype`
--

LOCK TABLES `concept_datatype` WRITE;
/*!40000 ALTER TABLE `concept_datatype` DISABLE KEYS */;
INSERT INTO `concept_datatype` VALUES (1,'Numeric','NM','Numeric value, including integer or float (e.g., creatinine, weight)',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d4a4488-c2cc-11de-8d13-0010c6dffd0f'),(2,'Coded','CWE','Value determined by term dictionary lookup (i.e., term identifier)',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d4a48b6-c2cc-11de-8d13-0010c6dffd0f'),(3,'Text','ST','Free text',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f'),(4,'N/A','ZZ','Not associated with a datatype (e.g., term answers, sets)',1,'2004-02-02 00:00:00',0,NULL,NULL,NULL,'8d4a4c94-c2cc-11de-8d13-0010c6dffd0f'),(5,'Document','RP','Pointer to a binary or text-based document (e.g., clinical document, RTF, XML, EKG, image, etc.) stored in complex_obs table',1,'2004-04-15 00:00:00',0,NULL,NULL,NULL,'8d4a4e74-c2cc-11de-8d13-0010c6dffd0f'),(6,'Date','DT','Absolute date',1,'2004-07-22 00:00:00',0,NULL,NULL,NULL,'8d4a505e-c2cc-11de-8d13-0010c6dffd0f'),(7,'Time','TM','Absolute time of day',1,'2004-07-22 00:00:00',0,NULL,NULL,NULL,'8d4a591e-c2cc-11de-8d13-0010c6dffd0f'),(8,'Datetime','TS','Absolute date and time',1,'2004-07-22 00:00:00',0,NULL,NULL,NULL,'8d4a5af4-c2cc-11de-8d13-0010c6dffd0f'),(10,'Boolean','BIT','Boolean value (yes/no, true/false)',1,'2004-08-26 00:00:00',0,NULL,NULL,NULL,'8d4a5cca-c2cc-11de-8d13-0010c6dffd0f'),(11,'Rule','ZZ','Value derived from other data',1,'2006-09-11 00:00:00',0,NULL,NULL,NULL,'8d4a5e96-c2cc-11de-8d13-0010c6dffd0f'),(12,'Structured Numeric','SN','Complex numeric values possible (ie, <5, 1-10, etc.)',1,'2005-08-06 00:00:00',0,NULL,NULL,NULL,'8d4a606c-c2cc-11de-8d13-0010c6dffd0f'),(13,'Complex','ED','Complex value.  Analogous to HL7 Embedded Datatype',1,'2008-05-28 12:25:34',0,NULL,NULL,NULL,'8d4a6242-c2cc-11de-8d13-0010c6dffd0f');
/*!40000 ALTER TABLE `concept_datatype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_description`
--

DROP TABLE IF EXISTS `concept_description`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_description` (
  `concept_description_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `description` text NOT NULL,
  `locale` varchar(50) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_description_id`),
  UNIQUE KEY `concept_description_uuid_index` (`uuid`),
  KEY `user_who_changed_description` (`changed_by`),
  KEY `description_for_concept` (`concept_id`),
  KEY `user_who_created_description` (`creator`),
  CONSTRAINT `description_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_changed_description` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_description` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_description`
--

LOCK TABLES `concept_description` WRITE;
/*!40000 ALTER TABLE `concept_description` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_description` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_map`
--

DROP TABLE IF EXISTS `concept_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_map` (
  `concept_map_id` int(11) NOT NULL AUTO_INCREMENT,
  `source` int(11) DEFAULT NULL,
  `source_code` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_map_id`),
  UNIQUE KEY `concept_map_uuid_index` (`uuid`),
  KEY `map_for_concept` (`concept_id`),
  KEY `map_creator` (`creator`),
  KEY `map_source` (`source`),
  CONSTRAINT `map_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `map_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `map_source` FOREIGN KEY (`source`) REFERENCES `concept_source` (`concept_source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_map`
--

LOCK TABLES `concept_map` WRITE;
/*!40000 ALTER TABLE `concept_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_name`
--

DROP TABLE IF EXISTS `concept_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_name` (
  `concept_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `locale` varchar(50) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `concept_name_id` int(11) NOT NULL AUTO_INCREMENT,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `concept_name_type` varchar(50) DEFAULT NULL,
  `locale_preferred` smallint(6) DEFAULT '0',
  PRIMARY KEY (`concept_name_id`),
  UNIQUE KEY `concept_name_uuid_index` (`uuid`),
  KEY `name_of_concept` (`name`),
  KEY `name_for_concept` (`concept_id`),
  KEY `user_who_created_name` (`creator`),
  KEY `user_who_voided_this_name` (`voided_by`),
  CONSTRAINT `name_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_created_name` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_this_name` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_name`
--

LOCK TABLES `concept_name` WRITE;
/*!40000 ALTER TABLE `concept_name` DISABLE KEYS */;
INSERT INTO `concept_name` VALUES (1,'Vero','it',1,'2012-09-17 11:29:16',1,0,NULL,NULL,NULL,'22b173bc-8a73-4d3c-ac6a-67810826267d',NULL,0),(1,'Sì','it',1,'2012-09-17 11:29:16',2,0,NULL,NULL,NULL,'b7b7b837-b404-4a90-b43f-3076af900499',NULL,0),(1,'Verdadeiro','pt',1,'2012-09-17 11:29:16',3,0,NULL,NULL,NULL,'8da07da1-e3a6-4ea2-8cf6-e1e03586cccb',NULL,0),(1,'Sim','pt',1,'2012-09-17 11:29:16',4,0,NULL,NULL,NULL,'5a82b007-06a0-43b1-be16-a659556efd9c',NULL,0),(1,'Vrai','fr',1,'2012-09-17 11:29:16',5,0,NULL,NULL,NULL,'564308be-155e-474d-ae73-9d8973c938ce',NULL,0),(1,'Oui','fr',1,'2012-09-17 11:29:16',6,0,NULL,NULL,NULL,'22d6d144-1453-4341-8738-bbc0e1ddd369',NULL,0),(1,'True','en',1,'2012-09-17 11:29:16',7,0,NULL,NULL,NULL,'aa80f682-fcf1-49c9-a6c9-28e9cc6b27fe','FULLY_SPECIFIED',0),(1,'Yes','en',1,'2012-09-17 11:29:16',8,0,NULL,NULL,NULL,'fb63d774-9004-463b-a117-538fa56c848a',NULL,0),(1,'Verdadero','es',1,'2012-09-17 11:29:16',9,0,NULL,NULL,NULL,'d5c6ed9e-c57a-4565-9d8b-79bf8d0f2a3d',NULL,0),(1,'Sí','es',1,'2012-09-17 11:29:16',10,0,NULL,NULL,NULL,'7e0a1183-ff49-4c3a-8222-96c9805f8928',NULL,0),(2,'Falso','it',1,'2012-09-17 11:29:16',11,0,NULL,NULL,NULL,'0a233374-3a0a-4a91-9640-c3701e03ba0c',NULL,0),(2,'No','it',1,'2012-09-17 11:29:16',12,0,NULL,NULL,NULL,'cdf457b3-b15f-46c0-8ebb-856b9ca898fa',NULL,0),(2,'Falso','pt',1,'2012-09-17 11:29:16',13,0,NULL,NULL,NULL,'bebbf492-153f-4087-a83f-0988b2dccab3',NULL,0),(2,'Não','pt',1,'2012-09-17 11:29:16',14,0,NULL,NULL,NULL,'652cd48a-3228-4aaa-a926-c147125bdc1a',NULL,0),(2,'Faux','fr',1,'2012-09-17 11:29:16',15,0,NULL,NULL,NULL,'930f1423-5b42-4e55-9b06-44e6288470a8',NULL,0),(2,'Non','fr',1,'2012-09-17 11:29:16',16,0,NULL,NULL,NULL,'a8e2ea12-1c6f-48da-90eb-7d4b9266e296',NULL,0),(2,'False','en',1,'2012-09-17 11:29:16',17,0,NULL,NULL,NULL,'c0bb7029-cd67-48f7-89f9-b5b0629eaa64','FULLY_SPECIFIED',0),(2,'No','en',1,'2012-09-17 11:29:16',18,0,NULL,NULL,NULL,'58c81f7b-ba07-44de-8dc3-eca1c87d5f2d',NULL,0),(2,'Falso','es',1,'2012-09-17 11:29:16',19,0,NULL,NULL,NULL,'17a03696-4116-4614-8712-ad53c6b5867e',NULL,0),(2,'No','es',1,'2012-09-17 11:29:16',20,0,NULL,NULL,NULL,'3df75586-28cc-441b-95fe-e73561f7952f',NULL,0);
/*!40000 ALTER TABLE `concept_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_name_tag`
--

DROP TABLE IF EXISTS `concept_name_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_name_tag` (
  `concept_name_tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_name_tag_id`),
  UNIQUE KEY `concept_name_tag_unique_tags` (`tag`),
  UNIQUE KEY `concept_name_tag_uuid_index` (`uuid`),
  KEY `user_who_created_name_tag` (`creator`),
  KEY `user_who_voided_name_tag` (`voided_by`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_name_tag`
--

LOCK TABLES `concept_name_tag` WRITE;
/*!40000 ALTER TABLE `concept_name_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_name_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_name_tag_map`
--

DROP TABLE IF EXISTS `concept_name_tag_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_name_tag_map` (
  `concept_name_id` int(11) NOT NULL,
  `concept_name_tag_id` int(11) NOT NULL,
  KEY `mapped_concept_name` (`concept_name_id`),
  KEY `mapped_concept_name_tag` (`concept_name_tag_id`),
  CONSTRAINT `mapped_concept_name_tag` FOREIGN KEY (`concept_name_tag_id`) REFERENCES `concept_name_tag` (`concept_name_tag_id`),
  CONSTRAINT `mapped_concept_name` FOREIGN KEY (`concept_name_id`) REFERENCES `concept_name` (`concept_name_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_name_tag_map`
--

LOCK TABLES `concept_name_tag_map` WRITE;
/*!40000 ALTER TABLE `concept_name_tag_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_name_tag_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_numeric`
--

DROP TABLE IF EXISTS `concept_numeric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_numeric` (
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `hi_absolute` double DEFAULT NULL,
  `hi_critical` double DEFAULT NULL,
  `hi_normal` double DEFAULT NULL,
  `low_absolute` double DEFAULT NULL,
  `low_critical` double DEFAULT NULL,
  `low_normal` double DEFAULT NULL,
  `units` varchar(50) DEFAULT NULL,
  `precise` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`concept_id`),
  CONSTRAINT `numeric_attributes` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_numeric`
--

LOCK TABLES `concept_numeric` WRITE;
/*!40000 ALTER TABLE `concept_numeric` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_numeric` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_proposal`
--

DROP TABLE IF EXISTS `concept_proposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_proposal` (
  `concept_proposal_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) DEFAULT NULL,
  `encounter_id` int(11) DEFAULT NULL,
  `original_text` varchar(255) NOT NULL DEFAULT '',
  `final_text` varchar(255) DEFAULT NULL,
  `obs_id` int(11) DEFAULT NULL,
  `obs_concept_id` int(11) DEFAULT NULL,
  `state` varchar(32) NOT NULL DEFAULT 'UNMAPPED',
  `comments` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `locale` varchar(50) NOT NULL DEFAULT '',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_proposal_id`),
  UNIQUE KEY `concept_proposal_uuid_index` (`uuid`),
  KEY `user_who_changed_proposal` (`changed_by`),
  KEY `concept_for_proposal` (`concept_id`),
  KEY `user_who_created_proposal` (`creator`),
  KEY `encounter_for_proposal` (`encounter_id`),
  KEY `proposal_obs_concept_id` (`obs_concept_id`),
  KEY `proposal_obs_id` (`obs_id`),
  CONSTRAINT `concept_for_proposal` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `encounter_for_proposal` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `proposal_obs_concept_id` FOREIGN KEY (`obs_concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `proposal_obs_id` FOREIGN KEY (`obs_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `user_who_changed_proposal` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_proposal` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_proposal`
--

LOCK TABLES `concept_proposal` WRITE;
/*!40000 ALTER TABLE `concept_proposal` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_proposal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_proposal_tag_map`
--

DROP TABLE IF EXISTS `concept_proposal_tag_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_proposal_tag_map` (
  `concept_proposal_id` int(11) NOT NULL,
  `concept_name_tag_id` int(11) NOT NULL,
  KEY `mapped_concept_proposal_tag` (`concept_name_tag_id`),
  KEY `mapped_concept_proposal` (`concept_proposal_id`),
  CONSTRAINT `mapped_concept_proposal` FOREIGN KEY (`concept_proposal_id`) REFERENCES `concept_proposal` (`concept_proposal_id`),
  CONSTRAINT `mapped_concept_proposal_tag` FOREIGN KEY (`concept_name_tag_id`) REFERENCES `concept_name_tag` (`concept_name_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_proposal_tag_map`
--

LOCK TABLES `concept_proposal_tag_map` WRITE;
/*!40000 ALTER TABLE `concept_proposal_tag_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_proposal_tag_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_set`
--

DROP TABLE IF EXISTS `concept_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_set` (
  `concept_set_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `concept_set` int(11) NOT NULL DEFAULT '0',
  `sort_weight` double DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_set_id`),
  UNIQUE KEY `concept_set_uuid_index` (`uuid`),
  KEY `has_a` (`concept_set`),
  KEY `user_who_created` (`creator`),
  KEY `idx_concept_set_concept` (`concept_id`),
  CONSTRAINT `is_a` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `has_a` FOREIGN KEY (`concept_set`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_created` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_set`
--

LOCK TABLES `concept_set` WRITE;
/*!40000 ALTER TABLE `concept_set` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_set` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_set_derived`
--

DROP TABLE IF EXISTS `concept_set_derived`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_set_derived` (
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `concept_set` int(11) NOT NULL DEFAULT '0',
  `sort_weight` double DEFAULT NULL,
  PRIMARY KEY (`concept_id`,`concept_set`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_set_derived`
--

LOCK TABLES `concept_set_derived` WRITE;
/*!40000 ALTER TABLE `concept_set_derived` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_set_derived` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_source`
--

DROP TABLE IF EXISTS `concept_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_source` (
  `concept_source_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `hl7_code` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` tinyint(1) NOT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_source_id`),
  UNIQUE KEY `concept_source_uuid_index` (`uuid`),
  KEY `unique_hl7_code` (`hl7_code`,`retired`),
  KEY `concept_source_creator` (`creator`),
  KEY `user_who_retired_concept_source` (`retired_by`),
  CONSTRAINT `concept_source_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept_source` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_source`
--

LOCK TABLES `concept_source` WRITE;
/*!40000 ALTER TABLE `concept_source` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_source` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_state_conversion`
--

DROP TABLE IF EXISTS `concept_state_conversion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_state_conversion` (
  `concept_state_conversion_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) DEFAULT '0',
  `program_workflow_id` int(11) DEFAULT '0',
  `program_workflow_state_id` int(11) DEFAULT '0',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_state_conversion_id`),
  UNIQUE KEY `concept_state_conversion_uuid_index` (`uuid`),
  UNIQUE KEY `unique_workflow_concept_in_conversion` (`program_workflow_id`,`concept_id`),
  KEY `concept_triggers_conversion` (`concept_id`),
  KEY `conversion_to_state` (`program_workflow_state_id`),
  CONSTRAINT `concept_triggers_conversion` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `conversion_involves_workflow` FOREIGN KEY (`program_workflow_id`) REFERENCES `program_workflow` (`program_workflow_id`),
  CONSTRAINT `conversion_to_state` FOREIGN KEY (`program_workflow_state_id`) REFERENCES `program_workflow_state` (`program_workflow_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_state_conversion`
--

LOCK TABLES `concept_state_conversion` WRITE;
/*!40000 ALTER TABLE `concept_state_conversion` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_state_conversion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `concept_word`
--

DROP TABLE IF EXISTS `concept_word`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_word` (
  `concept_word_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `word` varchar(50) NOT NULL DEFAULT '',
  `locale` varchar(20) NOT NULL DEFAULT '',
  `concept_name_id` int(11) NOT NULL,
  `weight` double DEFAULT '1',
  PRIMARY KEY (`concept_word_id`),
  KEY `word_in_concept_name` (`word`),
  KEY `concept_word_concept_idx` (`concept_id`),
  KEY `word_for_name` (`concept_name_id`),
  KEY `concept_word_weight_index` (`weight`),
  CONSTRAINT `word_for` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `word_for_name` FOREIGN KEY (`concept_name_id`) REFERENCES `concept_name` (`concept_name_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concept_word`
--

LOCK TABLES `concept_word` WRITE;
/*!40000 ALTER TABLE `concept_word` DISABLE KEYS */;
INSERT INTO `concept_word` VALUES (21,1,'VERDADERO','es',9,8.45679012345679),(22,1,'VRAI','fr',5,10.5),(23,1,'VERDADEIRO','pt',3,8.31),(24,1,'SÌ','it',2,15.15),(25,1,'OUI','fr',6,11.911111111111111),(26,1,'SÍ','es',10,15.15),(27,1,'SIM','pt',4,11.911111111111111),(28,1,'YES','en',8,11.911111111111111),(29,1,'VERO','it',1,10.5),(30,1,'TRUE','en',7,10.5625),(31,2,'FAUX','fr',15,10.5),(32,2,'FALSE','en',17,9.780000000000001),(33,2,'FALSO','it',11,9.72),(34,2,'NO','it',12,15.15),(35,2,'NON','fr',16,11.911111111111111),(36,2,'NO','es',20,15.15),(37,2,'NÃO','pt',14,11.911111111111111),(38,2,'FALSO','pt',13,9.72),(39,2,'NO','en',18,15.15),(40,2,'FALSO','es',19,9.72);
/*!40000 ALTER TABLE `concept_word` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug`
--

DROP TABLE IF EXISTS `drug`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drug` (
  `drug_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL,
  `combination` smallint(6) NOT NULL DEFAULT '0',
  `dosage_form` int(11) DEFAULT NULL,
  `dose_strength` double DEFAULT NULL,
  `maximum_daily_dose` double DEFAULT NULL,
  `minimum_daily_dose` double DEFAULT NULL,
  `route` int(11) DEFAULT NULL,
  `units` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`drug_id`),
  UNIQUE KEY `drug_uuid_index` (`uuid`),
  KEY `primary_drug_concept` (`concept_id`),
  KEY `drug_creator` (`creator`),
  KEY `dosage_form_concept` (`dosage_form`),
  KEY `drug_retired_by` (`retired_by`),
  KEY `route_concept` (`route`),
  CONSTRAINT `dosage_form_concept` FOREIGN KEY (`dosage_form`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `drug_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `drug_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `primary_drug_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `route_concept` FOREIGN KEY (`route`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug`
--

LOCK TABLES `drug` WRITE;
/*!40000 ALTER TABLE `drug` DISABLE KEYS */;
/*!40000 ALTER TABLE `drug` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug_ingredient`
--

DROP TABLE IF EXISTS `drug_ingredient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drug_ingredient` (
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `ingredient_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ingredient_id`,`concept_id`),
  KEY `combination_drug` (`concept_id`),
  CONSTRAINT `ingredient` FOREIGN KEY (`ingredient_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `combination_drug` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug_ingredient`
--

LOCK TABLES `drug_ingredient` WRITE;
/*!40000 ALTER TABLE `drug_ingredient` DISABLE KEYS */;
/*!40000 ALTER TABLE `drug_ingredient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug_order`
--

DROP TABLE IF EXISTS `drug_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drug_order` (
  `order_id` int(11) NOT NULL DEFAULT '0',
  `drug_inventory_id` int(11) DEFAULT '0',
  `dose` double DEFAULT NULL,
  `equivalent_daily_dose` double DEFAULT NULL,
  `units` varchar(255) DEFAULT NULL,
  `frequency` varchar(255) DEFAULT NULL,
  `prn` smallint(6) NOT NULL DEFAULT '0',
  `complex` smallint(6) NOT NULL DEFAULT '0',
  `quantity` int(11) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `inventory_item` (`drug_inventory_id`),
  CONSTRAINT `extends_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `inventory_item` FOREIGN KEY (`drug_inventory_id`) REFERENCES `drug` (`drug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug_order`
--

LOCK TABLES `drug_order` WRITE;
/*!40000 ALTER TABLE `drug_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `drug_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `encounter`
--

DROP TABLE IF EXISTS `encounter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter` (
  `encounter_id` int(11) NOT NULL AUTO_INCREMENT,
  `encounter_type` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL DEFAULT '0',
  `provider_id` int(11) NOT NULL DEFAULT '0',
  `location_id` int(11) DEFAULT NULL,
  `form_id` int(11) DEFAULT NULL,
  `encounter_datetime` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`encounter_id`),
  UNIQUE KEY `encounter_uuid_index` (`uuid`),
  KEY `encounter_ibfk_1` (`creator`),
  KEY `encounter_type_id` (`encounter_type`),
  KEY `encounter_form` (`form_id`),
  KEY `encounter_location` (`location_id`),
  KEY `encounter_patient` (`patient_id`),
  KEY `user_who_voided_encounter` (`voided_by`),
  KEY `encounter_changed_by` (`changed_by`),
  KEY `encounter_provider` (`provider_id`),
  KEY `encounter_datetime_idx` (`encounter_datetime`),
  CONSTRAINT `encounter_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_form` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`),
  CONSTRAINT `encounter_ibfk_1` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `encounter_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `encounter_provider` FOREIGN KEY (`provider_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `encounter_type_id` FOREIGN KEY (`encounter_type`) REFERENCES `encounter_type` (`encounter_type_id`),
  CONSTRAINT `user_who_voided_encounter` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `encounter`
--

LOCK TABLES `encounter` WRITE;
/*!40000 ALTER TABLE `encounter` DISABLE KEYS */;
/*!40000 ALTER TABLE `encounter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `encounter_type`
--

DROP TABLE IF EXISTS `encounter_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter_type` (
  `encounter_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`encounter_type_id`),
  UNIQUE KEY `encounter_type_uuid_index` (`uuid`),
  KEY `retired_status` (`retired`),
  KEY `user_who_created_type` (`creator`),
  KEY `user_who_retired_encounter_type` (`retired_by`),
  CONSTRAINT `user_who_created_type` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_encounter_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `encounter_type`
--

LOCK TABLES `encounter_type` WRITE;
/*!40000 ALTER TABLE `encounter_type` DISABLE KEYS */;
INSERT INTO `encounter_type` VALUES (1,'ADULTINITIAL','Outpatient Adult Initial Visit',1,'2005-02-24 00:00:00',0,NULL,NULL,NULL,'8d5b27bc-c2cc-11de-8d13-0010c6dffd0f'),(2,'ADULTRETURN','Outpatient Adult Return Visit',1,'2005-02-24 00:00:00',0,NULL,NULL,NULL,'8d5b2be0-c2cc-11de-8d13-0010c6dffd0f'),(3,'PEDSINITIAL','Outpatient Pediatric Initial Visit',1,'2005-02-24 00:00:00',0,NULL,NULL,NULL,'8d5b2dde-c2cc-11de-8d13-0010c6dffd0f'),(4,'PEDSRETURN','Outpatient Pediatric Return Visit',1,'2005-02-24 00:00:00',0,NULL,NULL,NULL,'8d5b3108-c2cc-11de-8d13-0010c6dffd0f');
/*!40000 ALTER TABLE `encounter_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `field`
--

DROP TABLE IF EXISTS `field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `field` (
  `field_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  `field_type` int(11) DEFAULT NULL,
  `concept_id` int(11) DEFAULT NULL,
  `table_name` varchar(50) DEFAULT NULL,
  `attribute_name` varchar(50) DEFAULT NULL,
  `default_value` text,
  `select_multiple` smallint(6) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`field_id`),
  UNIQUE KEY `field_uuid_index` (`uuid`),
  KEY `field_retired_status` (`retired`),
  KEY `user_who_changed_field` (`changed_by`),
  KEY `concept_for_field` (`concept_id`),
  KEY `user_who_created_field` (`creator`),
  KEY `type_of_field` (`field_type`),
  KEY `user_who_retired_field` (`retired_by`),
  CONSTRAINT `concept_for_field` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `type_of_field` FOREIGN KEY (`field_type`) REFERENCES `field_type` (`field_type_id`),
  CONSTRAINT `user_who_changed_field` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_field` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_field` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `field`
--

LOCK TABLES `field` WRITE;
/*!40000 ALTER TABLE `field` DISABLE KEYS */;
/*!40000 ALTER TABLE `field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `field_answer`
--

DROP TABLE IF EXISTS `field_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `field_answer` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `answer_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`field_id`,`answer_id`),
  UNIQUE KEY `field_answer_uuid_index` (`uuid`),
  KEY `field_answer_concept` (`answer_id`),
  KEY `user_who_created_field_answer` (`creator`),
  CONSTRAINT `answers_for_field` FOREIGN KEY (`field_id`) REFERENCES `field` (`field_id`),
  CONSTRAINT `field_answer_concept` FOREIGN KEY (`answer_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_created_field_answer` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `field_answer`
--

LOCK TABLES `field_answer` WRITE;
/*!40000 ALTER TABLE `field_answer` DISABLE KEYS */;
/*!40000 ALTER TABLE `field_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `field_type`
--

DROP TABLE IF EXISTS `field_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `field_type` (
  `field_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` longtext,
  `is_set` smallint(6) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`field_type_id`),
  UNIQUE KEY `field_type_uuid_index` (`uuid`),
  KEY `user_who_created_field_type` (`creator`),
  CONSTRAINT `user_who_created_field_type` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `field_type`
--

LOCK TABLES `field_type` WRITE;
/*!40000 ALTER TABLE `field_type` DISABLE KEYS */;
INSERT INTO `field_type` VALUES (1,'Concept','',0,1,'2005-02-22 00:00:00','8d5e7d7c-c2cc-11de-8d13-0010c6dffd0f'),(2,'Database element','',0,1,'2005-02-22 00:00:00','8d5e8196-c2cc-11de-8d13-0010c6dffd0f'),(3,'Set of Concepts','',1,1,'2005-02-22 00:00:00','8d5e836c-c2cc-11de-8d13-0010c6dffd0f'),(4,'Miscellaneous Set','',1,1,'2005-02-22 00:00:00','8d5e852e-c2cc-11de-8d13-0010c6dffd0f'),(5,'Section','',1,1,'2005-02-22 00:00:00','8d5e86fa-c2cc-11de-8d13-0010c6dffd0f');
/*!40000 ALTER TABLE `field_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `form`
--

DROP TABLE IF EXISTS `form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form` (
  `form_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `version` varchar(50) NOT NULL DEFAULT '',
  `build` int(11) DEFAULT NULL,
  `published` smallint(6) NOT NULL DEFAULT '0',
  `description` text,
  `encounter_type` int(11) DEFAULT NULL,
  `template` mediumtext,
  `xslt` mediumtext,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retired_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`form_id`),
  UNIQUE KEY `form_uuid_index` (`uuid`),
  KEY `user_who_last_changed_form` (`changed_by`),
  KEY `user_who_created_form` (`creator`),
  KEY `form_encounter_type` (`encounter_type`),
  KEY `user_who_retired_form` (`retired_by`),
  KEY `form_published_index` (`published`),
  KEY `form_retired_index` (`retired`),
  KEY `form_published_and_retired_index` (`published`,`retired`),
  CONSTRAINT `form_encounter_type` FOREIGN KEY (`encounter_type`) REFERENCES `encounter_type` (`encounter_type_id`),
  CONSTRAINT `user_who_created_form` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_last_changed_form` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_form` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form`
--

LOCK TABLES `form` WRITE;
/*!40000 ALTER TABLE `form` DISABLE KEYS */;
/*!40000 ALTER TABLE `form` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `form_field`
--

DROP TABLE IF EXISTS `form_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_field` (
  `form_field_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) NOT NULL DEFAULT '0',
  `field_id` int(11) NOT NULL DEFAULT '0',
  `field_number` int(11) DEFAULT NULL,
  `field_part` varchar(5) DEFAULT NULL,
  `page_number` int(11) DEFAULT NULL,
  `parent_form_field` int(11) DEFAULT NULL,
  `min_occurs` int(11) DEFAULT NULL,
  `max_occurs` int(11) DEFAULT NULL,
  `required` smallint(6) NOT NULL DEFAULT '0',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `sort_weight` float(11,5) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`form_field_id`),
  UNIQUE KEY `form_field_uuid_index` (`uuid`),
  KEY `user_who_last_changed_form_field` (`changed_by`),
  KEY `user_who_created_form_field` (`creator`),
  KEY `field_within_form` (`field_id`),
  KEY `form_containing_field` (`form_id`),
  KEY `form_field_hierarchy` (`parent_form_field`),
  CONSTRAINT `field_within_form` FOREIGN KEY (`field_id`) REFERENCES `field` (`field_id`),
  CONSTRAINT `form_containing_field` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`),
  CONSTRAINT `form_field_hierarchy` FOREIGN KEY (`parent_form_field`) REFERENCES `form_field` (`form_field_id`),
  CONSTRAINT `user_who_created_form_field` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_last_changed_form_field` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_field`
--

LOCK TABLES `form_field` WRITE;
/*!40000 ALTER TABLE `form_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formentry_archive`
--

DROP TABLE IF EXISTS `formentry_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formentry_archive` (
  `formentry_archive_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_data` mediumtext NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `creator` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`formentry_archive_id`),
  KEY `User who created formentry_archive` (`creator`),
  CONSTRAINT `User who created formentry_archive` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formentry_archive`
--

LOCK TABLES `formentry_archive` WRITE;
/*!40000 ALTER TABLE `formentry_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `formentry_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formentry_error`
--

DROP TABLE IF EXISTS `formentry_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formentry_error` (
  `formentry_error_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_data` mediumtext NOT NULL,
  `error` varchar(255) NOT NULL DEFAULT '',
  `error_details` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`formentry_error_id`),
  KEY `User who created formentry_error` (`creator`),
  CONSTRAINT `User who created formentry_error` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formentry_error`
--

LOCK TABLES `formentry_error` WRITE;
/*!40000 ALTER TABLE `formentry_error` DISABLE KEYS */;
/*!40000 ALTER TABLE `formentry_error` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formentry_queue`
--

DROP TABLE IF EXISTS `formentry_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formentry_queue` (
  `formentry_queue_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_data` mediumtext NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0=pending, 1=processing, 2=processed, 3=error',
  `date_processed` datetime DEFAULT NULL,
  `error_msg` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`formentry_queue_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formentry_queue`
--

LOCK TABLES `formentry_queue` WRITE;
/*!40000 ALTER TABLE `formentry_queue` DISABLE KEYS */;
/*!40000 ALTER TABLE `formentry_queue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formentry_xsn`
--

DROP TABLE IF EXISTS `formentry_xsn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formentry_xsn` (
  `formentry_xsn_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) NOT NULL,
  `xsn_data` longblob NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `archived` int(1) NOT NULL DEFAULT '0',
  `archived_by` int(11) DEFAULT NULL,
  `date_archived` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`formentry_xsn_id`),
  UNIQUE KEY `formentry_xsn_uuid_index` (`uuid`),
  KEY `User who created formentry_xsn` (`creator`),
  KEY `Form with which this xsn is related` (`form_id`),
  KEY `User who archived formentry_xsn` (`archived_by`),
  KEY `formentry_xsn_archived_index` (`archived`),
  CONSTRAINT `Form with which this xsn is related` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`),
  CONSTRAINT `User who archived formentry_xsn` FOREIGN KEY (`archived_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `User who created formentry_xsn` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formentry_xsn`
--

LOCK TABLES `formentry_xsn` WRITE;
/*!40000 ALTER TABLE `formentry_xsn` DISABLE KEYS */;
/*!40000 ALTER TABLE `formentry_xsn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `global_property`
--

DROP TABLE IF EXISTS `global_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `global_property` (
  `property` varbinary(255) NOT NULL DEFAULT '',
  `property_value` mediumtext,
  `description` text,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`property`),
  UNIQUE KEY `global_property_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `global_property`
--

LOCK TABLES `global_property` WRITE;
/*!40000 ALTER TABLE `global_property` DISABLE KEYS */;
INSERT INTO `global_property` VALUES ('FormEntry.enableDashboardTab','true','true/false whether or not to show a Form Entry tab on the patient dashboard','2ae6a12e-0c30-4570-9a31-7c2e37d80f78'),('FormEntry.enableOnEncounterTab','false','true/false whether or not to show a Enter Form button on the encounters tab of the patient dashboard','35c086f5-4c2f-47da-8a1c-0a5312ac01a5'),('concept.causeOfDeath','5002','Concept id of the concept defining the CAUSE OF DEATH concept','d1c63ac3-9e59-43e5-a2f7-5ba7861ff460'),('concept.cd4_count','5497','Concept id of the concept defining the CD4 count concept','70b536f1-89a4-4116-86ca-4653ab46bf9a'),('concept.false','2','Concept id of the concept defining the FALSE boolean concept','56c073ad-cd5b-4ac7-95ca-01c513b3b7fe'),('concept.height','5090','Concept id of the concept defining the HEIGHT concept','9a9c19ca-a8ab-469f-a589-54d87762666e'),('concept.medicalRecordObservations','1238','The concept id of the MEDICAL_RECORD_OBSERVATIONS concept.  This concept_id is presumed to be the generic grouping (obr) concept in hl7 messages.  An obs_group row is not created for this concept.','aa1f5cf6-2365-4f66-832d-00561d6ae6f7'),('concept.none','1107','Concept id of the concept defining the NONE concept','eab5a0d2-344d-40fb-8ad7-888d28c85f20'),('concept.otherNonCoded','5622','Concept id of the concept defining the OTHER NON-CODED concept','deabb449-bcbd-4732-90d0-8d19fa159886'),('concept.patientDied','1742','Concept id of the concept defining the PATIENT DIED concept','9b943c2f-60f0-4628-a862-1d565886e8dd'),('concept.problemList','1284','The concept id of the PROBLEM LIST concept.  This concept_id is presumed to be the generic grouping (obr) concept in hl7 messages.  An obs_group row is not created for this concept.','79137129-fd52-4712-a7e1-bf682c6bb0c7'),('concept.reasonExitedCare','','Concept id of the concept defining the REASON EXITED CARE concept','70aa1844-1a65-4b93-8990-7eaa17b501f7'),('concept.reasonOrderStopped','1812','Concept id of the concept defining the REASON ORDER STOPPED concept','0ca1c44c-200e-4ede-a6da-e46251b984bd'),('concept.true','1','Concept id of the concept defining the TRUE boolean concept','fea12778-822a-4783-b556-30d5b4881dc7'),('concept.weight','5089','Concept id of the concept defining the WEIGHT concept','e8550336-4399-4122-811d-23f30eff0641'),('concepts.locked','false','true/false whether or not concepts can be edited in this database.','04913427-fa23-4c03-b3e6-71ac5f3d530a'),('dashboard.encounters.showEditLink','true','true/false whether or not to show the \'Edit Encounter\' link on the patient dashboard','e9628d67-2d13-41a2-8232-8b6667761930'),('dashboard.encounters.showEmptyFields','true','true/false whether or not to show empty fields on the \'View Encounter\' window','79940be6-5c51-4149-8228-0a8d3275d227'),('dashboard.encounters.showViewLink','true','true/false whether or not to show the \'View Encounter\' link on the patient dashboard','1df82555-d11c-4280-b409-967445d20958'),('dashboard.encounters.usePages','smart','true/false/smart on how to show the pages on the \'View Encounter\' window.  \'smart\' means that if > 50% of the fields have page numbers defined, show data in pages','3f621456-8f96-451a-b397-e840f2f597df'),('dashboard.header.programs_to_show','','List of programs to show Enrollment details of in the patient header. (Should be an ordered comma-separated list of program_ids or names.)','ef8e8a1b-5c7b-483a-947d-288e53d04be1'),('dashboard.header.workflows_to_show','','List of programs to show Enrollment details of in the patient header. List of workflows to show current status of in the patient header. These will only be displayed if they belong to a program listed above. (Should be a comma-separated list of program_workflow_ids.)','7ccf4824-2bee-453f-9096-f4b395f8be3a'),('dashboard.overview.showConcepts','','Comma delimited list of concepts ids to show on the patient dashboard overview tab','cb2b3827-955e-437f-b89b-2303bd5b6a98'),('dashboard.regimen.displayDrugSetIds','ANTIRETROVIRAL DRUGS,TUBERCULOSIS TREATMENT DRUGS','Drug sets that appear on the Patient Dashboard Regimen tab. Comma separated list of name of concepts that are defined as drug sets.','8b6d2ab9-b315-43c3-834f-f8e28f5af37e'),('dashboard.regimen.displayFrequencies','7 days/week,6 days/week,5 days/week,4 days/week,3 days/week,2 days/week,1 days/week','Frequency of a drug order that appear on the Patient Dashboard. Comma separated list of name of concepts that are defined as drug frequencies.','0c7846b3-83be-4c5d-bf20-f25f952b586b'),('dashboard.regimen.standardRegimens','<list>  <regimenSuggestion>    <drugComponents>      <drugSuggestion>        <drugId>2</drugId>        <dose>1</dose>        <units>tab(s)</units>        <frequency>2/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>    </drugComponents>    <displayName>3TC + d4T(30) + NVP (Triomune-30)</displayName>    <codeName>standardTri30</codeName>    <canReplace>ANTIRETROVIRAL DRUGS</canReplace>  </regimenSuggestion>  <regimenSuggestion>    <drugComponents>      <drugSuggestion>        <drugId>3</drugId>        <dose>1</dose>        <units>tab(s)</units>        <frequency>2/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>    </drugComponents>    <displayName>3TC + d4T(40) + NVP (Triomune-40)</displayName>    <codeName>standardTri40</codeName>    <canReplace>ANTIRETROVIRAL DRUGS</canReplace>  </regimenSuggestion>  <regimenSuggestion>    <drugComponents>      <drugSuggestion>        <drugId>39</drugId>        <dose>1</dose>        <units>tab(s)</units>        <frequency>2/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>      <drugSuggestion>        <drugId>22</drugId>        <dose>200</dose>        <units>mg</units>        <frequency>2/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>    </drugComponents>    <displayName>AZT + 3TC + NVP</displayName>    <codeName>standardAztNvp</codeName>    <canReplace>ANTIRETROVIRAL DRUGS</canReplace>  </regimenSuggestion>  <regimenSuggestion>    <drugComponents>      <drugSuggestion reference=\"../../../regimenSuggestion[3]/drugComponents/drugSuggestion\"/>      <drugSuggestion>        <drugId>11</drugId>        <dose>600</dose>        <units>mg</units>        <frequency>1/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>    </drugComponents>    <displayName>AZT + 3TC + EFV(600)</displayName>    <codeName>standardAztEfv</codeName>    <canReplace>ANTIRETROVIRAL DRUGS</canReplace>  </regimenSuggestion>  <regimenSuggestion>    <drugComponents>      <drugSuggestion>        <drugId>5</drugId>        <dose>30</dose>        <units>mg</units>        <frequency>2/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>      <drugSuggestion>        <drugId>42</drugId>        <dose>150</dose>        		<units>mg</units>        <frequency>2/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>      <drugSuggestion reference=\"../../../regimenSuggestion[4]/drugComponents/drugSuggestion[2]\"/>    </drugComponents>    <displayName>d4T(30) + 3TC + EFV(600)</displayName>    <codeName>standardD4t30Efv</codeName>    <canReplace>ANTIRETROVIRAL DRUGS</canReplace>  </regimenSuggestion>  <regimenSuggestion>    <drugComponents>      <drugSuggestion>        <drugId>6</drugId>        <dose>40</dose>        <units>mg</units>        <frequency>2/day x 7 days/week</frequency>        <instructions></instructions>      </drugSuggestion>      <drugSuggestion reference=\"../../../regimenSuggestion[5]/drugComponents/drugSuggestion[2]\"/>      <drugSuggestion reference=\"../../../regimenSuggestion[4]/drugComponents/drugSuggestion[2]\"/>    </drugComponents>    <displayName>d4T(40) + 3TC + EFV(600)</displayName>    <codeName>standardD4t40Efv</codeName>    <canReplace>ANTIRETROVIRAL DRUGS</canReplace>  </regimenSuggestion></list>','XML description of standard drug regimens, to be shown as shortcuts on the dashboard regimen entry tab','4c780218-3c88-4520-9075-34ccf5a9b58e'),('dashboard.relationships.show_types','','Types of relationships separated by commas.  Doctor/Patient,Parent/Child','b27d1257-0819-4f40-9e0b-bc31c2df9446'),('dashboard.showPatientName','false','Whether or not to display the patient name in the patient dashboard title. Note that enabling this could be security risk if multiple users operate on the same computer.','7bc12537-fcad-481f-bd67-15bc633663fb'),('default_locale','en_GB','Specifies the default locale. You can specify both the language code(ISO-639) and the country code(ISO-3166), e.g. \'en_GB\' or just country: e.g. \'en\'','127fc610-ae04-4c9a-a3c9-92ab28845652'),('default_location','Unknown Location','The name of the location to use as a system default','bbe91b8c-ff6c-4c58-a211-dbe8354d5d5d'),('default_theme','','Default theme for users.  OpenMRS ships with themes of \'green\', \'orange\', \'purple\', and \'legacy\'','ce848b47-5c0b-42dd-a3e7-4f19b0c6cd39'),('encounterForm.obsSortOrder','number','The sort order for the obs listed on the encounter edit form.  \'number\' sorts on the associated numbering from the form schema.  \'weight\' sorts on the order displayed in the form schema.','8636c5e7-85f0-4664-ba92-3da4b38b0856'),('formentry.database_version','2.8','DO NOT MODIFY.  Current database version number for the formentry module.','d03eab35-98fe-4738-b103-299ecfe75e74'),('formentry.infopath_server_url','','When uploading an XSN, this url is used as the \"base path\".  (Should be something like http://localhost:8080/openmrs)','679c07db-c40f-4f1d-aeae-7f61e709ce3d'),('formentry.infopath_taskpane.relationship_sort_order','','A comma-separated sorted list of relationship sides that should appear\nfirst in the InfoPath taskpane widget. Relationship sides should be\nspelled exactly as shown in the system; i.e. Parent,\nDoctor, Child','a1e018ad-847d-45a0-ab40-ab3ab971b86d'),('formentry.infopath_taskpane.showAllUsersOnLoad','true','When you view the \'users.htm\' page in the taskpane, i.e. by clicking on the Choose a Provider\nbutton, should the system automatically preload a list of all users?','156463e6-fca0-4e9e-b303-eaea3a604a9e'),('formentry.infopath_taskpane_caption','Welcome!','The text seen in the infopath taskpane upon first logging in','6944fbb8-b51e-4f59-99ac-87c5a881162a'),('formentry.infopath_taskpane_keepalive_min','','The number of minutes to keep refreshing the taskpane before allowing \nthe login to lapse','edeb8a6f-db6b-402d-8a62-ca55440c4aa9'),('formentry.infopath_taskpane_refresh_sec','','The number of seconds between taskpane refreshes.  This keeps the taskpane from\nlogging people out on longer forms','fc7222b5-bc66-41cd-9bef-fb39b48f5c4e'),('formentry.mandatory','false','true/false whether or not the formentry module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','3d1967d3-8f5a-4ab8-b1c6-04b38d0268ae'),('formentry.patientForms.goBackOnEntry','false','\'true\' means have the browser go back to the find patient page after picking a form\nfrom the patientForms tab on the patient dashboard page','1ab7ddb1-d910-4b9c-9a88-1485492277e9'),('formentry.queue_archive_dir','formentry/archive/%Y/%M','Directory containing the formentry archive items.  This will contain xml files that have\nbeen submitted by infopath and then processed sucessfully into hl7.\nCertain date parameters will be replaced with the current date:\n%Y = four digit year\n%M = two digit month\n%D = two digit date of the month\n%w = week of the year\n     %W = week of the month','166d7c7d-b655-486e-a614-ac284a66064a'),('formentry.queue_dir','formentry/queue','Directory containing the formentry queue items. This will contain xml files submitted by\ninfopath.  These items are awaiting processing to be turned into hl7 queue items','fbc5d75e-da9c-46c1-8449-a5e969aaba28'),('formentry.started','true','DO NOT MODIFY. true/false whether or not the formentry module has been started.  This is used to make sure modules that were running  prior to a restart are started again','5a21d652-8e00-466c-b19c-697ab3593db8'),('graph.color.absolute','rgb(20,20,20)','Color of the \'invalid\' section of numeric graphs on the patient dashboard.','c78de5eb-5ade-4147-b333-21207ffb679b'),('graph.color.critical','rgb(200,0,0)','Color of the \'critical\' section of numeric graphs on the patient dashboard.','66e33481-cf33-4643-b178-7aa9b9b4f753'),('graph.color.normal','rgb(255,126,0)','Color of the \'normal\' section of numeric graphs on the patient dashboard.','0df9f8d2-aba5-456f-9c1c-fa4b94f1d06d'),('gzip.enabled','false','Set to \'true\' to turn on OpenMRS\'s gzip filter, and have the webapp compress data before sending it to any client that supports it. Generally use this if you are running Tomcat standalone. If you are running Tomcat behind Apache, then you\'d want to use Apache to do gzip compression.','e85cba60-0cc2-4d38-a49b-96b09148b42e'),('hl7_archive.dir','hl7_archives','The default name or absolute path for the folder where to write the hl7_in_archives.','0c1ed3e1-1001-47cc-bfb6-96ec3597008d'),('hl7_processor.ignore_missing_patient_non_local','false','If true, hl7 messages for patients that are not found and are non-local will silently be dropped/ignored','e994a032-645d-4384-949f-c40d04583e4f'),('htmlformentry.database_version','1.2.0','DO NOT MODIFY.  Current database version number for the htmlformentry module.','869bf5ee-c4ba-4418-9948-9a2f9d38dc89'),('htmlformentry.mandatory','false','true/false whether or not the htmlformentry module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','7c3b5b1f-d6ef-49f0-b610-5944c87fc884'),('htmlformentry.started','true','DO NOT MODIFY. true/false whether or not the htmlformentry module has been started.  This is used to make sure modules that were running  prior to a restart are started again','03731fb6-d88d-4442-8f6c-a6b36ce1a883'),('htmlwidgets.mandatory','false','true/false whether or not the htmlwidgets module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','f70bb1bc-5905-452d-bfb0-f70a7525d9ee'),('htmlwidgets.started','true','DO NOT MODIFY. true/false whether or not the htmlwidgets module has been started.  This is used to make sure modules that were running  prior to a restart are started again','a3b15cb0-790d-4478-a0fa-8f7bc0e33f2d'),('layout.address.format','general','Format in which to display the person addresses.  Valid values are general, kenya, rwanda, usa, and lesotho','323c7733-7328-4465-b32e-a9e56caff027'),('layout.name.format','short','Format in which to display the person names.  Valid values are short, long','37a98937-3784-4ba8-ab68-909d0f82b54b'),('locale.allowed.list','en, es, fr, it, pt','Comma delimited list of locales allowed for use on system','865a9321-aa9d-45ce-9373-6392419f9b8b'),('location.field.style','default','Type of widget to use for location fields','75f13925-afa5-4eed-b644-98ac20e72041'),('log.level.openmrs','info','log level used by the logger \'org.openmrs\'. This value will override the log4j.xml value. Valid values are trace, debug, info, warn, error or fatal','7d923ffe-2991-43d1-a6f5-f298aad2087c'),('logic.database_version','1.4','DO NOT MODIFY.  Current database version number for the logic module.','debae1ba-5ae6-4764-a0b2-af289ef2c5c3'),('logic.default.ruleClassDirectory','logic/class','Default folder where compiled rule will be stored','656320a1-d609-420d-a76f-f6d998ca15fe'),('logic.default.ruleJavaDirectory','logic/sources','Default folder where rule\'s java file will be stored','a75c81ca-08ac-4560-9c64-d944c0f42750'),('logic.defaultTokens.conceptClasses','','When registering default tokens for logic, if you specify a comma-separated list of concept class names here, only concepts of those classes will have tokens registered. If you leave this blank, all classes will have tokens registered for their concepts.','32464e21-a6b2-4b3a-b6fb-5119095c53f1'),('logic.mandatory','false','true/false whether or not the logic module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','a306e398-d552-4563-bbd8-9f92c34ee103'),('logic.started','true','DO NOT MODIFY. true/false whether or not the logic module has been started.  This is used to make sure modules that were running  prior to a restart are started again','f9ae2b18-e3aa-4400-bf99-a2a9dd5fe160'),('mail.debug','false','true/false whether to print debugging information during mailing','26cb25eb-70a4-4b4b-a126-8f885222631a'),('mail.default_content_type','text/plain','Content type to append to the mail messages','866bf8a7-b5d3-4c64-957e-fad45cfdee49'),('mail.from','info@openmrs.org','Email address to use as the default from address','1ef16620-db47-43cd-9f7e-d1f824c6187d'),('mail.password','test','Password for the SMTP user (if smtp_auth is enabled)','028fc228-4370-4d3f-85aa-ca68966905c8'),('mail.smtp_auth','false','true/false whether the smtp host requires authentication','262f712a-df72-41f5-961c-085796b38364'),('mail.smtp_host','localhost','SMTP host name','deedbcc5-d2d8-4777-a0fb-7771813ca4a2'),('mail.smtp_port','25','SMTP port','df281ccc-bb80-4eb8-a1c4-7640bc7c3e79'),('mail.transport_protocol','smtp','Transport protocol for the messaging engine. Valid values: smtp','158fb236-5c8d-431b-a3c0-6e64b7d5a56f'),('mail.user','test','Username of the SMTP user (if smtp_auth is enabled)','9ac09900-b1c5-4dc1-85c9-d3d74b0bb11d'),('minSearchCharacters','3','Number of characters user must input before searching is started.','8a21f811-3995-404a-bf8f-3822ac353676'),('module_repository_folder','modules','Name of the folder in which to store the modules','f5024c02-75ba-471d-ad6b-c8e75c026733'),('newPatientForm.relationships','','Comma separated list of the RelationshipTypes to show on the new/short patient form.  The list is defined like \'3a, 4b, 7a\'.  The number is the RelationshipTypeId and the \'a\' vs \'b\' part is which side of the relationship is filled in by the user.','dc431729-b737-45f4-b8b0-55f37b31c6a4'),('new_patient_form.showRelationships','false','true/false whether or not to show the relationship editor on the addPatient.htm screen','2a01efa7-4d89-4cba-a7f3-f6b228ffa180'),('obs.complex_obs_dir','complex_obs','Default directory for storing complex obs.','50f6d820-13b3-42dc-8391-67e8c1bb7a8e'),('patient.defaultPatientIdentifierValidator','org.openmrs.patient.impl.LuhnIdentifierValidator','This property sets the default patient identifier validator.  The default validator is only used in a handful of (mostly legacy) instances.  For example, it\'s used to generate the isValidCheckDigit calculated column and to append the string \"(default)\" to the name of the default validator on the editPatientIdentifierType form.','3cd10f0c-14e6-40bf-b4b3-bdc6d563aa37'),('patient.headerAttributeTypes','','A comma delimited list of PersonAttributeType names that will be shown on the patient dashboard','7a6b0dc2-d5f3-4071-a892-3ad91b336192'),('patient.identifierPrefix','','This property is only used if patient.identifierRegex is empty.  The string here is prepended to the sql indentifier search string.  The sql becomes \"... where identifier like \'<PREFIX><QUERY STRING><SUFFIX>\';\".  Typically this value is either a percent sign (%) or empty.','aa80e097-2e0c-43a6-9205-52e3db58043a'),('patient.identifierRegex','','WARNING: Using this search property can cause a drop in mysql performance with large patient sets.  A MySQL regular expression for the patient identifier search strings.  The @SEARCH@ string is replaced at runtime with the user\'s search string.  An empty regex will cause a simply \'like\' sql search to be used. Example: ^0*@SEARCH@([A-Z]+-[0-9])?$','2881029f-a3af-4048-92a8-a260770424b9'),('patient.identifierSearchPattern','','If this is empty, the regex or suffix/prefix search is used.  Comma separated list of identifiers to check.  Allows for faster searching of multiple options rather than the slow regex. e.g. @SEARCH@,0@SEARCH@,@SEARCH-1@-@CHECKDIGIT@,0@SEARCH-1@-@CHECKDIGIT@ would turn a request for \"4127\" into a search for \"in (\'4127\',\'04127\',\'412-7\',\'0412-7\')\"','dd2bbc47-a3bf-4a34-b717-8084befe354f'),('patient.identifierSuffix','','This property is only used if patient.identifierRegex is empty.  The string here is prepended to the sql indentifier search string.  The sql becomes \"... where identifier like \'<PREFIX><QUERY STRING><SUFFIX>\';\".  Typically this value is either a percent sign (%) or empty.','f87690f8-0ca2-4c15-8aa6-ca42c4a672bd'),('patient.listingAttributeTypes','','A comma delimited list of PersonAttributeType names that should be displayed for patients in _lists_','41e40667-337a-4d40-ad90-b1213b0b745d'),('patient.viewingAttributeTypes','','A comma delimited list of PersonAttributeType names that should be displayed for patients when _viewing individually_','aeddf46e-47b1-43e8-bac8-59cd732e89da'),('patient_identifier.importantTypes','','A comma delimited list of PatientIdentifier names : PatientIdentifier locations that will be displayed on the patient dashboard.  E.g.: TRACnet ID:Rwanda,ELDID:Kenya','72c5a593-1d44-40e1-94fa-ad4c000fe971'),('person.searchMaxResults','1000','The maximum number of results returned by patient searches','e5deb72c-92ad-4f8a-9d24-299689e6d3cf'),('report.deleteReportsAgeInHours','72','Reports that are not explicitly saved are deleted automatically when they are this many hours old. (Values less than or equal to zero means do not delete automatically)','a39e3256-3caf-44b6-9295-3b64c5a430d2'),('report.xmlMacros','','Macros that will be applied to Report Schema XMLs when they are interpreted. This should be java.util.properties format.','055f2caf-9fc8-4fa4-930a-7baf77fd683b'),('reportProblem.url','http://errors.openmrs.org/scrap','The openmrs url where to submit bug reports','93e08d67-cc09-4d31-abf4-4336dc8a5f57'),('reporting.database_version','0.2.0','DO NOT MODIFY.  Current database version number for the reporting module.','37d524b1-6d5d-4894-9422-65869b38a0ea'),('reporting.mandatory','false','true/false whether or not the reporting module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','f11f87f2-1c0a-4c89-9df1-df8defd006cf'),('reporting.maxCachedReports','10','The maximum number of reports whose underlying data and output should be kept in the cache at any one time','339dc582-3fe5-4795-96bd-5dff1fb08a1c'),('reporting.maxReportsToRun','1','The maximum number of reports that should be processed at any one time','5367725a-23af-418d-8f4b-612814936c73'),('reporting.preferredIdentifierTypes','','Pipe-separated list of patient identifier type names, which should be displayed on default patient datasets','49490675-b016-4dba-95e5-e3228f404b12'),('reporting.runReportCohortFilterMode','showIfNull','Supports the values hide,showIfNull,show which determine whether the cohort selector should be available in the run report page','0917b61b-62da-4121-892f-1b298196feae'),('reporting.started','true','DO NOT MODIFY. true/false whether or not the reporting module has been started.  This is used to make sure modules that were running  prior to a restart are started again','79e0a823-d78c-4a2a-a7ad-d586a85bc4d9'),('reportingcompatibility.data_export_batch_size','7500','The number of patients to export at a time in a data export.  The larger this number the faster and more memory that is used.  The smaller this number the slower and less memory is used.','e12fbfe5-e54f-4283-9dbe-98b64d0f26f8'),('reportingcompatibility.mandatory','false','true/false whether or not the reportingcompatibility module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','1ce32767-ce07-4093-bec1-3f0a814de1d6'),('reportingcompatibility.started','true','DO NOT MODIFY. true/false whether or not the reportingcompatibility module has been started.  This is used to make sure modules that were running  prior to a restart are started again','be8fff8b-2db6-43ec-96f9-e09ed5dd48f9'),('scheduler.password','test','Password for the OpenMRS user that will perform the scheduler activities','52b730b1-1ad0-4818-9483-73c4e5614fbd'),('scheduler.username','admin','Username for the OpenMRS user that will perform the scheduler activities','730d5443-809e-4efd-b2ec-dc570f21c80b'),('searchWidget.batchSize','200','The maximum number of search results that are returned by an ajax call','51c66cb1-69f7-4c91-9d4e-59b0990e27a1'),('searchWidget.runInSerialMode','true','Specifies whether the search widgets should make ajax requests in serial or parallel order, a value of true is appropriate for implementations running on a slow network connection and vice versa','8325ace3-9152-48ef-9326-ca079234c9bc'),('searchWidget.searchDelayInterval','400','Specifies time interval in milliseconds when searching, between keyboard keyup event and triggering the search off, should be higher if most users are slow when typing so as to minimise the load on the server','a53d3df1-8f7c-4be2-9fbb-687de08fab32'),('security.passwordCannotMatchUsername','true','Configure whether passwords must not match user\'s username or system id','763a2e7b-e69a-4f54-af52-ebef31171d99'),('security.passwordCustomRegex','','Configure a custom regular expression that a password must match','a05d2412-88e6-40a8-88d4-016fa033fc09'),('security.passwordMinimumLength','8','Configure the minimum length required of all passwords','3ed542dc-4e6d-40b5-9b4c-0acd64d853bc'),('security.passwordRequiresDigit','true','Configure whether passwords must contain at least one digit','fc43f037-3b2d-41d6-9a9e-83f9540a4138'),('security.passwordRequiresNonDigit','true','Configure whether passwords must contain at least one non-digit','cd8a2c58-fc6d-4ed7-8f9a-3d7e360bd3db'),('security.passwordRequiresUpperAndLowerCase','true','Configure whether passwords must contain both upper and lower case characters','47d3ae7d-7666-4260-8784-62de4e6f7f91'),('serialization.xstream.mandatory','false','true/false whether or not the serialization.xstream module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','a71ee194-b8c0-46c1-aa5f-3236b748ee4b'),('serialization.xstream.started','true','DO NOT MODIFY. true/false whether or not the serialization.xstream module has been started.  This is used to make sure modules that were running  prior to a restart are started again','a57a35fd-7b00-4db8-9e20-36ddda0cee1d'),('use_patient_attribute.healthCenter','false','Indicates whether or not the \'health center\' attribute is shown when viewing/searching for patients','8b0552ef-78f3-4be4-92ce-6d23b69657c5'),('use_patient_attribute.mothersName','false','Indicates whether or not mother\'s name is able to be added/viewed for a patient','2d7c7727-5823-4c62-9d4f-759e6d3daf1a'),('user.headerAttributeTypes','','A comma delimited list of PersonAttributeType names that will be shown on the user dashboard. (not used in v1.5)','9aea648c-2b72-49e9-bf12-36d37a2f5054'),('user.listingAttributeTypes','','A comma delimited list of PersonAttributeType names that should be displayed for users in _lists_','c7314dad-9ac9-4f04-840f-1b96f76ff057'),('user.viewingAttributeTypes','','A comma delimited list of PersonAttributeType names that should be displayed for users when _viewing individually_','82aac492-abe4-44e8-8a3d-f4a0d4caf3f6'),('webservices.rest.allowedips','','A comma-separate list of IP addresses that are allowed to access the web services. An empty string allows everyone to access all ws. \n        IPs can be declared with bit masks e.g. 10.0.0.0/30 matches 10.0.0.0 - 10.0.0.3 and 10.0.0.0/24 matches 10.0.0.0 - 10.0.0.255.','9e43ed7a-7adb-4743-91f2-4d11eca64c32'),('webservices.rest.mandatory','false','true/false whether or not the webservices.rest module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','48974b4a-7442-4c39-8a1d-a9208406b5a6'),('webservices.rest.maxResultsAbsolute','100','The absolute max results limit. If the client requests a larger number of results, then will get an error','b425fe7b-809c-427e-8a5b-120a28535f98'),('webservices.rest.maxResultsDefault','50','The default max results limit if the user does not provide a maximum when making the web service call.','3ea6a83e-cbe4-403a-980b-c259d6a1ecde'),('webservices.rest.started','true','DO NOT MODIFY. true/false whether or not the webservices.rest module has been started.  This is used to make sure modules that were running  prior to a restart are started again','06ccaee3-b0b2-4e15-bcf1-d25db2512d4f'),('webservices.rest.uriPrefix','http://localhost:8092/openmrs','The URI prefix through which clients consuming web services will connect to the web application, should be of the form http://{ipAddress}:{port}/{contextPath}','e4dd817a-de80-4a20-bb1f-af19867ce174'),('xforms.allowBindEdit','false','Set to true if you want to allow editing of question bindings when designing forms.','36ff0ecd-6fbf-4f76-8fca-f041b7758e94'),('xforms.archive_dir','xforms/archive/%Y/%M','Directory containing the xforms archive items.  This will contain xform model xml files that have been processed and then submitted successfully into the formentry queue.','04ce7607-4849-48b4-8331-978d6493882d'),('xforms.autoGeneratePatientIdentifier','false','Set to true if you want the idgen module to generate patient identifiers when creating new patients using the xforms module.','01200d7c-ddff-4456-b3d5-a2442ba65040'),('xforms.cohortSerializer','org.openmrs.module.xforms.serialization.DefaultCohortSerializer','The patient cohort (cohort_id and name) serializer','1ba3f9ee-c4ff-43b6-9a2e-6e4a513348c4'),('xforms.complexobs_dir','xforms/complexobs','Directory for storing complex obs used by the xforms module.','5de29c7f-024c-494b-a74f-24c218c7e86d'),('xforms.database_version','3.7.7','DO NOT MODIFY.  Current database version number for the xforms module.','0630ffd9-59b9-4cda-b995-d89d8d3daab0'),('xforms.dateDisplayFormat','dd/MM/yyyy','The display format of dates used by the xforms module.','607e3deb-98ec-4e08-91f5-dc5d4082db48'),('xforms.dateSubmitFormat','yyyy-MM-dd','The format of the dates passed in the xml of the xforms model. Please make sure this matches with the date format of your data entry applications, else you will get wrong dates on the server.','69bc2030-7000-4822-b9a9-30f41a289d21'),('xforms.dateTimeDisplayFormat','dd/MM/yyyy hh:mm a','The display format of datetime used by the xforms module.','f606b2fd-053f-4f4f-ab43-0e11acd3891b'),('xforms.dateTimeSubmitFormat','yyyy-MM-dd\'T\'HH:mm:ssZ','The format of the datetime passed in the xml of the xforms model. Please make sure this matches with the date format of your data entry applications, else you will get wrong dates on the server.','57463be4-ee8c-47c1-89ab-dd9b4bd8c519'),('xforms.decimalSeparators','en:.;fr:.;es:,;it:.;pt:.','The decimal separators for each locale. e.g:  en:.;fr:.;es:,;it:.;pt:.','5bcad967-f2ba-4f9c-8d3d-d2657d7048b9'),('xforms.defaultFontFamily','Verdana, \'Lucida Grande\', \'Trebuchet MS\', Arial, Sans-Serif','The default font family used by the form designer.','9c76633b-adc6-47f4-9f15-54288c1dafb3'),('xforms.defaultFontSize','16','The default font size used by the form designer.','f9a35c28-7cf5-4d98-bf99-c17564db182e'),('xforms.encounterDateIncludesTime','false','Set to true if the encounter date should include time.','7c2c30c7-8f91-460f-a4d7-ac4bcb3f1ea2'),('xforms.error_dir','xforms/error','Directory containing the xforms error items.  This will contain xform model xml files that have not been submitted into the formentry queue because of processing errors.','bdf53c6f-d2b7-46b0-a0e2-33dfc4ee2862'),('xforms.includeUsersInXformsDownload','true','Set to true if you want to include users when downloading xforms, else set to false if you want to always load the users list separately.','bb3e91c4-53c2-491f-9f57-9141505b8ffb'),('xforms.isRemoteFormEntry','false','Set to true if this is a remote form entry server (Not the main server).','aed4c911-80a0-44c4-a120-466d02cad02b'),('xforms.localeList','en:English','The list of locales supported by the form designer. e.g:  en:English,fr:French,es:Spanish,it:Italian,pt:Portuguese','8142fde7-baab-4f89-9b3a-532dd88efb0d'),('xforms.mandatory','false','true/false whether or not the xforms module MUST start when openmrs starts.  This is used to make sure that mission critical modules are always running if openmrs is running.','80ee36c6-cf07-4039-9b7c-d1f75b14a8d9'),('xforms.multiSelectAppearance','full','The appearance of multi select input fields. Allowed values are: {full,minimal,compact}','c06b75dd-389a-4887-9b1b-eb23206aa189'),('xforms.newPatientFormId','','The id of the form for creating new patients','e71e2da3-82c7-4e77-a8fb-645fa71b5975'),('xforms.new_patient_identifier_type_id','1','The id of the patient identifier type which will be used when creating new patients from forms which do not have a patient_identifier.identifier_type_id field.','04b4bbe4-e293-4ea8-bebe-15fe4821839e'),('xforms.overwriteValidationsOnRefresh','false','Set to true if, on refresh, you want custom validations to be replaced by those from the database concepts.','83eeeea1-b0ec-4443-80b7-308adde46341'),('xforms.patientDownloadCohort','','The cohort for patients to download','39c1db78-657d-4da3-bdb2-ed7f19f83013'),('xforms.patientRegEncounterFormId','0','The id of the encounter form which will be combined with the patient registration form.','f297d9cc-eb65-4f1f-8f51-0e2fd32b7e49'),('xforms.patientSerializer','org.openmrs.module.xforms.serialization.DefaultPatientSerializer','The patient set serializer','fc6e1822-4cfd-437c-96ea-a0726b927153'),('xforms.preferredConceptSource','','The name for preferred concept source to be used for forms that can be shared with other OpenMRS installations.','bc4993b9-9d0e-415f-904e-650674a67714'),('xforms.queue_dir','xforms/queue','Directory containing the xforms queue items. This will contain xforms xml model files submitted and awaiting processing to be submitted into the formentry queue.','d36b028a-5504-43d9-bba7-97e4b6ea9b96'),('xforms.rejectExistingPatientCreation','true','Set to true to Reject forms for patients considered new when they already exist, by virture of patient identifier. Else set to false to allow them.','33e1843d-0c58-4b6d-a408-41ec57a23957'),('xforms.saveFormat','purcforms','The format in which the xforms will be saved. For now we support two formats: purcforms and javarosa','12e25486-c73c-4582-8c50-c4d0e30cf8c4'),('xforms.searchNewPatientAfterFormSubmission','true','Set to true if you want to search for a new patient after submitting a form, else set to false if you want to go back to the same patient.','4f17b00d-b6ff-4a96-8226-3dd0ded5bb58'),('xforms.setDefaultLocation','false','Set to true if you want to set the default location to that of the logged on user.','65bbf206-a1d9-4526-b2dd-bda3c319a0a6'),('xforms.setDefaultProvider','false','Set to true if you want to set the default provider to the logged on user, if he or she has the provider role.','16cbc75c-63f5-4a75-8d77-554b5c4712d5'),('xforms.showJavaScriptTab','false','Set to true if you want to display the JavaScript tab of the form designer.','f520d26f-86e8-478c-a579-1c78fc6f7a0c'),('xforms.showLanguageTab','false','Set to true if you want to display the language xml tab of the form designer.','b5ea4fdd-3d32-4b76-83c9-946d7782d6b1'),('xforms.showLayoutXmlTab','false','Set to true if you want to display the layout xml tab of the form designer.','e5b2ee98-44cb-420e-ad53-ba5354e2acb5'),('xforms.showModelXmlTab','false','Set to true if you want to display the model xml tab of the form designer.','f63a31b6-88e7-4959-8808-dd87a24777e3'),('xforms.showOfflineFormDesigner','false','Set to true if you want to show the form designer in off line mode.','df7f9ce4-e18c-40b2-82cb-3e118e48d7cb'),('xforms.showSubmitSuccessMsg','false','Set to true if you want to display the form submitted successfully message every time a form is submitted successfully.','46d419ec-4bcf-4d17-8148-b674593a79d3'),('xforms.showXformsSourceTab','false','Set to true if you want to display the xforms source tab of the form designer.','a7e2b66c-97d6-4626-9ac4-37fada0325e1'),('xforms.singleSelectAppearance','minimal','The appearance of single select input fields. Allowed values are: {full,minimal,compact}','27ededb1-2f32-45aa-bca4-d3bed135e1d8'),('xforms.smsFieldSepChar','=','The separator between questions and answers in the sms text.','eeb0a1ca-ee78-45da-8d1b-fa8475f22447'),('xforms.smsSendFailureReports','true','Set to true if you want sms sender to get failure reports, else set to false.','c80fc8f5-a898-4ccb-9d38-17607760f650'),('xforms.smsSendSuccessReports','true','Set to true if you want sms sender to get success reports, else set to false.','7b6f618e-b05c-4178-997a-de00b40b6436'),('xforms.started','true','DO NOT MODIFY. true/false whether or not the xforms module has been started.  This is used to make sure modules that were running  prior to a restart are started again','017971a9-f6a6-40f7-bed4-62996a64bd46'),('xforms.timeDisplayFormat','hh:mm a','The display format of time used by the xforms module.','d820ac2e-5716-43b5-a4d9-9086c9c92e56'),('xforms.timeSubmitFormat','HH:mm:ss','The format of the time passed in the xml of the xforms model. Please make sure this matches with the date format of your data entry applications, else you will get wrong times on the server.','7fb662e8-c666-45fe-bf5d-ecda065527cd'),('xforms.undoRedoBufferSize','-1','Set to the maximum number of actions you can undo or redo. The bigger the size, the more memory your browser needs. Default value is 100. Set to -1 if you do not want any limit.','8a1bb7e1-7d59-47cd-a7fc-27716448e85b'),('xforms.useEncounterXform','true','Set to true if you want to use XForms to edit encounters instead of the default openmrs edit encounter screen, else set to false.','4b812004-9884-43cb-aae0-fa10922bf10a'),('xforms.usePatientXform','false','Set to true if you want to use XForms to create new patients instead of the default openmrs create patient form, else set to false.','f1a9c637-8a04-42e9-b72f-873114b0411b'),('xforms.useStoredXform','true','Set to true if you want to use XForms uploaded into the database, else set to false if you want to always build an XForm on the fly from the current form definition.','f5f982c3-331a-4747-becc-466fa26d9fa0'),('xforms.userSerializer','org.openmrs.module.xforms.serialization.DefaultUserSerializer','The user set serializer','5203eb36-9d9e-4f2d-959c-c87c69e91745'),('xforms.xformDownloadFolder','','Folder to which XForms are download','95c53e5b-71f1-4d0c-a266-0dedfa330c1b'),('xforms.xformSerializer','org.fcitmuk.epihandy.EpihandyXformSerializer','The XForms serializer','34da87df-fee8-4d1b-9e03-7dcb33c4f739'),('xforms.xformUploadFolder','','Folder from which XForms data is uploaded','40c9ebda-6740-4832-ac82-72964c4514a6');
/*!40000 ALTER TABLE `global_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hl7_in_archive`
--

DROP TABLE IF EXISTS `hl7_in_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_in_archive` (
  `hl7_in_archive_id` int(11) NOT NULL AUTO_INCREMENT,
  `hl7_source` int(11) NOT NULL DEFAULT '0',
  `hl7_source_key` varchar(255) DEFAULT NULL,
  `hl7_data` mediumtext NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `message_state` int(1) DEFAULT '2',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_in_archive_id`),
  UNIQUE KEY `hl7_in_archive_uuid_index` (`uuid`),
  KEY `hl7_in_archive_message_state_idx` (`message_state`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hl7_in_archive`
--

LOCK TABLES `hl7_in_archive` WRITE;
/*!40000 ALTER TABLE `hl7_in_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `hl7_in_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hl7_in_error`
--

DROP TABLE IF EXISTS `hl7_in_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_in_error` (
  `hl7_in_error_id` int(11) NOT NULL AUTO_INCREMENT,
  `hl7_source` int(11) NOT NULL DEFAULT '0',
  `hl7_source_key` text,
  `hl7_data` mediumtext NOT NULL,
  `error` varchar(255) NOT NULL DEFAULT '',
  `error_details` mediumtext,
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_in_error_id`),
  UNIQUE KEY `hl7_in_error_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hl7_in_error`
--

LOCK TABLES `hl7_in_error` WRITE;
/*!40000 ALTER TABLE `hl7_in_error` DISABLE KEYS */;
/*!40000 ALTER TABLE `hl7_in_error` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hl7_in_queue`
--

DROP TABLE IF EXISTS `hl7_in_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_in_queue` (
  `hl7_in_queue_id` int(11) NOT NULL AUTO_INCREMENT,
  `hl7_source` int(11) NOT NULL DEFAULT '0',
  `hl7_source_key` text,
  `hl7_data` mediumtext NOT NULL,
  `message_state` int(1) NOT NULL DEFAULT '0',
  `date_processed` datetime DEFAULT NULL,
  `error_msg` text,
  `date_created` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_in_queue_id`),
  UNIQUE KEY `hl7_in_queue_uuid_index` (`uuid`),
  KEY `hl7_source` (`hl7_source`),
  CONSTRAINT `hl7_source` FOREIGN KEY (`hl7_source`) REFERENCES `hl7_source` (`hl7_source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hl7_in_queue`
--

LOCK TABLES `hl7_in_queue` WRITE;
/*!40000 ALTER TABLE `hl7_in_queue` DISABLE KEYS */;
/*!40000 ALTER TABLE `hl7_in_queue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hl7_source`
--

DROP TABLE IF EXISTS `hl7_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_source` (
  `hl7_source_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_source_id`),
  UNIQUE KEY `hl7_source_uuid_index` (`uuid`),
  KEY `creator` (`creator`),
  CONSTRAINT `creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hl7_source`
--

LOCK TABLES `hl7_source` WRITE;
/*!40000 ALTER TABLE `hl7_source` DISABLE KEYS */;
INSERT INTO `hl7_source` VALUES (1,'LOCAL','',1,'2006-09-01 00:00:00','8d6b8bb6-c2cc-11de-8d13-0010c6dffd0f');
/*!40000 ALTER TABLE `hl7_source` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `htmlformentry_html_form`
--

DROP TABLE IF EXISTS `htmlformentry_html_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `htmlformentry_html_form` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `xml_data` mediumtext NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `uuid` char(38) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `htmlformentry_html_form_uuid_index` (`uuid`),
  KEY `User who created htmlformentry_htmlform` (`creator`),
  KEY `Form with which this htmlform is related` (`form_id`),
  KEY `User who changed htmlformentry_htmlform` (`changed_by`),
  KEY `user_who_retired_html_form` (`retired_by`),
  CONSTRAINT `Form with which this htmlform is related` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`),
  CONSTRAINT `User who changed htmlformentry_htmlform` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `User who created htmlformentry_htmlform` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_html_form` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `htmlformentry_html_form`
--

LOCK TABLES `htmlformentry_html_form` WRITE;
/*!40000 ALTER TABLE `htmlformentry_html_form` DISABLE KEYS */;
/*!40000 ALTER TABLE `htmlformentry_html_form` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquibasechangelog`
--

DROP TABLE IF EXISTS `liquibasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liquibasechangelog` (
  `ID` varchar(63) NOT NULL,
  `AUTHOR` varchar(63) NOT NULL,
  `FILENAME` varchar(200) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `MD5SUM` varchar(32) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`ID`,`AUTHOR`,`FILENAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquibasechangelog`
--

LOCK TABLES `liquibasechangelog` WRITE;
/*!40000 ALTER TABLE `liquibasechangelog` DISABLE KEYS */;
INSERT INTO `liquibasechangelog` VALUES ('0','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:22','112fa925e7ce223f9dfc1c841176b4c','Custom Change','Run the old sqldiff file to get database up to the 1.4.0.20 version if needed. (Requires \'mysql\' to be on the PATH)',NULL,'1.9.4'),('02232009-1141','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:28:33','aa7af8d8a609c61fd504e6121b8feda','Modify Column','Modify the password column to fit the output of SHA-512 function',NULL,'1.9.4'),('1','upul','liquibase-update-to-latest.xml','2012-09-17 11:28:22','aeb2c081fe662e3375a02ea34696492','Add Column','Add the column to person_attribute type to connect each type to a privilege',NULL,'1.9.4'),('1226348923233-12','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','1ef82a3a056d8fe77c51789ee3e270','Insert Row (x11)','',NULL,'1.9.4'),('1226348923233-13','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','1fbe86152e13998b4e9dfa64f8f99e1','Insert Row (x2)','',NULL,'1.9.4'),('1226348923233-14','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','37d7a09a3680241c2cd870bf875f7679','Insert Row (x4)','',NULL,'1.9.4'),('1226348923233-15','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','6f5fdf6fe4573d335b764e8c3f6dec9','Insert Row (x15)','',NULL,'1.9.4'),('1226348923233-16','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','e3fb7531421d36297f9b551aa14eed3','Insert Row','',NULL,'1.9.4'),('1226348923233-17','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','2c3a40aea75302fa5eda34e68f0b5a8','Insert Row (x2)','',NULL,'1.9.4'),('1226348923233-18','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','a077f3e88c77b1bcfd024568ce97a57','Insert Row (x2)','',NULL,'1.9.4'),('1226348923233-2','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','17915c6b808f125502a8832f191896a','Insert Row (x5)','',NULL,'1.9.4'),('1226348923233-20','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','74d4c95919a87ad385f4678c05befeb','Insert Row','',NULL,'1.9.4'),('1226348923233-21','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','cad8f9efce142229ad5030139ae2ee62','Insert Row','',NULL,'1.9.4'),('1226348923233-22','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','98e8afcf7f83f1f2fe6ae566ae71b','Insert Row','',NULL,'1.9.4'),('1226348923233-23','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','877ae775e48051291b94467caebdbf9','Insert Row','',NULL,'1.9.4'),('1226348923233-5','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','e69c9cbc8e906c10b7b19ce95c6fbb','Insert Row','',NULL,'1.9.4'),('1226348923233-6','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','afbc17f0c1c778754be371db868719f','Insert Row (x14)','',NULL,'1.9.4'),('1226348923233-8','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','52afbf2cc39cde1fed9c97b8886ef','Insert Row (x7)','',NULL,'1.9.4'),('1226348923233-9','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','f8de838176dd923fe06ff3346fd2e9c7','Insert Row (x4)','',NULL,'1.9.4'),('1226412230538-9a','ben (generated)','liquibase-core-data.xml','2012-09-17 11:28:22','d19735782014e65d28267d83a681fac','Insert Row (x4)','',NULL,'1.9.4'),('1227303685425-1','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','4b1d6f6458503aad8cf53c8583648d1','Create Table','',NULL,'1.9.4'),('1227303685425-10','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','1ca890687d9198679930117b437bdcb8','Create Table','',NULL,'1.9.4'),('1227303685425-100','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','c4f0efe639c65a0ca719a7672c9ae99','Create Index','',NULL,'1.9.4'),('1227303685425-101','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','ec489290e24b9bc8e7cefa4cb193a5','Create Index','',NULL,'1.9.4'),('1227303685425-102','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','3fffc270441f9846be7f6f15b9186fe','Create Index','',NULL,'1.9.4'),('1227303685425-103','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','5bca419b6ce805277a7f1e853492965','Create Index','',NULL,'1.9.4'),('1227303685425-104','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','f5921bfce0567fad9dbebf4a464b7b8','Create Index','',NULL,'1.9.4'),('1227303685425-105','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','a8e830de2d9b5b30b5cee4f96e3d4f','Create Index','',NULL,'1.9.4'),('1227303685425-106','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','7e434b23ab45109451cc494fc7b66831','Create Index','',NULL,'1.9.4'),('1227303685425-107','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','347132fbd16c8712058e1f95765ce26','Create Index','',NULL,'1.9.4'),('1227303685425-108','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','5b4184878459e4fc9341a43b4be64ef','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-109','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','c2bfb4e612aa1973f1c365ad7118f342','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-11','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','fa7989647192649dc172d64046eb90','Create Table','',NULL,'1.9.4'),('1227303685425-110','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','d6659d52c5bcac8b59fb8ba1a37f86','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-111','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','f995cd7d34245c41d575977cf271ccc6','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-112','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','83c9df4fa7c9a7d84cdd930ff59776','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-113','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','581e495dd2e0605b389c1d497388786f','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-114','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','833048d0b4aae48cec412ca39b7b742','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-115','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','a08b4c2b2cced32d89b1d4b31b97458','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-116','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','eeec2cd3e6dddfebf775011fb81bd94','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-117','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','9f80b1cfa564da653099bec7178a435','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-118','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','1e5f598cefabaaa8ef2b1bec0b95597','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-119','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','698c2d5e06fb51de417d1ed586129','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-12','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','9375592183aa2b5d4ad11a937595d9','Create Table','',NULL,'1.9.4'),('1227303685425-120','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','d05ee670738deebde0131d30112ec4ae','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-121','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','b248338c8498759a3ea4de4e3b667793','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-122','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','f2636dd6c2704c55fb9747b3b8a2a','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-123','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','307598cfdfdae399ad1ce3e7c4a8467','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-124','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','ce9674c37a991460aed032ad5cb7d89','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-125','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','dface46a9bf5a1250d143e868208865','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-126','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','de1299f5403e3360e1c2c53390a8b29c','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-127','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','5e7f8e3b6bf0fc792f3f302d7cb2b3','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-128','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','2d81a031cf75efab818d6e71fd344c24','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-129','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','ad7b4d0e76518df21fff420664ea52','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-13','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','8a81103ce3bf6f96ae9a3cd5ea11da4','Create Table','',NULL,'1.9.4'),('1227303685425-130','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','ca1a1a1be5462940b223815c43d43ee','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-131','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','1e994b0469d1759c69824fa79bb47e','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-132','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','71079c961b9b64fc0642ed69b944bef','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-133','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','b7f75569bbf9932631edb2ded9a412','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-134','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','bfdf173f27999984c8fef82bb496c880','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-135','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','fceb5cc5466c89f54fab47951181ead','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-136','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','05f6a40bc30fe272b4bd740ad7709f','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-137','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','e4d42fbfe1248f205c9221b884c8e71','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-138','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','9ebf8d9bb9541ceba5a7ed25d55978','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-139','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','4ab8e4364cdb9f61f512845b537a5e33','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-14','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','2b11cd2e016b3967d4806872727ec7','Create Table','',NULL,'1.9.4'),('1227303685425-140','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','48d01547623e48d3b4d11d547c28c28','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-141','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','1f2917f6f1523ca240c9dfd44ccdfa66','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-142','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','d826371493865456e85a06649e83a9d','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-143','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','669e47429cecc0ebe20c9e4724ad1b','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-144','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','fc622179fac4171f122cdbb217a5332','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-145','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','3ab7a04513262a248a1584ea9eb6342','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-146','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','9c9b88b77c842272ab95b913d2c45f1','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-147','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','6d2f3c8fd1a6fd222cd9e64626ec2414','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-148','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','7f99bbffa5152188ccd74e889cc69344','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-149','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','2971495b795edfbc771618d9d178179','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-15','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','2b1462a7269e8d188326f9439ec1a0','Create Table','',NULL,'1.9.4'),('1227303685425-150','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','7453c6b2dc234075444921641f038cf','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-151','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','62bfd65133eb8649823b76e28f749ae4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-152','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','41f97398c21bcc4f3bbfa57ac36167','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-153','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','34a3388e508254798882f090113b91e9','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-154','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','cd1c528d9bbd8cd521f55f51507f2448','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-155','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','59169212323db3843f2d240afaab688','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-156','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','cb904d9071798e490aaf1a894777b','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-157','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','ec5e335c2d6ef840ba839d2d7cab26bf','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-158','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','edba928f8f3af2b720d1ae52c3da29','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-159','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','1d631d1b5585f307b5bba3e94104897','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-16','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','4a97582b5459ea8f1d718cc7044517b','Create Table','',NULL,'1.9.4'),('1227303685425-160','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','5af7bfc7acbef7db5684ff3e662d7b1','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-161','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','d65716f46e431b417677fb5754f631cf','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-162','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','a823cf39649373bf762fc2ddc9a3b4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-163','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','8efed95ebdac55227fd711cb18c652a','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-164','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','f7cb86e864d7b259bb63c73cb4cf1e8','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-165','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','5da4fd35f568ae85de6274f6fb179dd','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-166','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','d6a910cfe97bc6aed5e54ec1cadd89b','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-167','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','adf31a2b3145f38cfc94632966dc0cf','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-168','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','d3984f9f372ff0df21db96aeae982cab','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-169','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','296119308b945663ddbd5b4b719e53','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-17','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','88a9a422647683aedba68c299823683d','Create Table','',NULL,'1.9.4'),('1227303685425-170','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','be7a3bc89baac1b0b2e0fe51991bd015','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-171','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','ca59f633dbae6e045d58b4cca6cc7c5','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-172','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','6325d6535f7cf928fa84a37a72605dfb','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-173','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','351518f028727083f95bc8d54c295c2a','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-174','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','3e888947a28e23f88510af9514767590','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-175','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','5fef7a2498f6d631e1228efc2bdef6c','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-176','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','2bd625c243b7583c86a9bffd3215a741','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-177','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','426c4bccb537fb3f4c3d3e26fff73018','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-178','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','fc3d34e4c7f55660abad9d79fa9f19','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-179','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','41071cc7cfeec957e73ede6cec85063','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-18','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','44be2c894ba65ba6dff11859325ee1e7','Create Table','',NULL,'1.9.4'),('1227303685425-180','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:19','17f13d77392855ff797b2ee5938150b','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-181','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','1e87e405f78834da8449c3dd538ea69','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-182','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','7e5895b2826c9b539643c2a52db136bd','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-183','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','1ed87f73bb22c85a678e7486ce316e4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-184','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','dd523eb3c0d47022fd3ccde778a7b21','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-185','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','bf22dffeb46511b2c54caa2edde81cf','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-186','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','bcd25ca2c2592b19d299b434d4e2b855','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-187','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','c552885fe12d78ad9d9e902e85c2e','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-188','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','1be8a7c821c27ac5f24f37ea72dde4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-189','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','5ebe66c9ad8928b9dd571fbf2e684342','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-19','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','8fea50c3c68aa5e3caa8a29ac1ed2bc2','Create Table','',NULL,'1.9.4'),('1227303685425-190','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','765abd819360e17b449b349e3c6fb4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-191','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','eefbf72ce449dfa6d87903b9d48fc14','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-192','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','c49e3e3ef4748a6d227822ba9d9db75','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-193','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','8734c3bf33d23c5167926fde1c2497b1','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-194','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','acf63f4166b9efbeec94a33b9873b6','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-195','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','a6893ed94b1c673064e64f31e76922e3','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-196','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','4d10fc7df1588142908dfe1ecab77983','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-197','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','77df8c771bcafcdc69e0f036fa297e56','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-198','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','37bb62897883da1e2833a1ab2088edb','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-199','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','4dc522cef2d9e8f6679f1ad05d5e971e','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-2','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','484d95d2ffed12fa0ee8aee8974b567','Create Table','',NULL,'1.9.4'),('1227303685425-20','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','7faf5302b18d5ce205b723bca1542c','Create Table','',NULL,'1.9.4'),('1227303685425-200','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','939ac86f57297fc01f80b129247630f5','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-201','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','9bf8c421b14fa2c8e5784f89c3d8d65','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-202','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','309ecd6871c152c8c2f87a80f520a68a','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-203','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','c8ac825ea236dd980f088849eb6e974','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-204','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','3094b038b0f828dbe7d0296b28eb3f73','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-205','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','5a12d2856974981835ba499d73ac1','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-206','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','c1abccf6ee7c67d4471cc292fa878aa','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-207','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','ef6b7d9cb53938c0e8682daeb5af97e','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-208','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','a8a7f2b6ac8fa17b814ab859b4532fd','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-209','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','137480e6ed47c390e98547b0551d39f4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-21','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','ea3ef386b2dfb64cb9df5c1fbc6378','Create Table','',NULL,'1.9.4'),('1227303685425-210','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','fa37aa2bf95f6b24806ad4fab3b16e26','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-211','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','e3cfdbbc639a13bb42471dbecdc2d88','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-212','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','6eee35721da53ff117ababbae33bd5','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-213','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','c591a16e2a8fcc25ef4aa858692dfef','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-214','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','56c3ba4cfa55218d3aa0238ec2275f0','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-215','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','48563ec0b8762dce6bf94e068b27cb5','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-216','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','396121d768871a53120935cf19420d7','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-217','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','ebfbd07cd866453255b3532893163562','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-218','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','1530c85467d1b6d6980c794dbfd33f','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-219','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','5061bae5afea383d93b6f8b118253d','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-22','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','4b9fbcceb5de1c20b8338e96328198','Create Table','',NULL,'1.9.4'),('1227303685425-220','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','e4ccb7132533b42c47f77abf5ef26','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-221','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','ff49796922743ca6aec19ec3a4ee2d8','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-222','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','b6a54d3e4d64fd39ef72efb98dfc936','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-223','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','a4de4db8a76fd815c83b53fd5f67fee1','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-224','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','22ea56d2224297e8252864e90466c','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-225','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','dfccc3b975a9f5b8ec5b27164c2d6d','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-226','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','e34f2fe84f1222d6aa5eb652f8d17f44','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-227','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','6bf6811bac713b4761afbba7756e1a61','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-228','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','4732be71b8c8fbf39bc1df446af78751','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-229','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','73d11a5ae7bf33aa516119311cbc16f6','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-23','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','88e233ad3125c41ed528a721cfe2345f','Create Table','',NULL,'1.9.4'),('1227303685425-230','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','ee2e35bc91b5e1b01f2048c987caf6c4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-231','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','63bbb76e789fcad6b69bd4c64f5950','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-232','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','76be79df3e103cc45f4ee148555e4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-233','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','e7b3a215c88f4d892fa76882e5159f64','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-234','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','8c28fb289889fdd53acda37d11112ce2','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-235','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','6cdeded260bdceb9e1826d4ffbf5a375','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-236','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','28e4b2fa85d59877ad9fa54c6d2b031','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-237','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','a1ffd0638df4322f16f8726f98b4f2','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-238','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:20','41aa312b273ec9ebf460faaa6e6b299c','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-239','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','20d4b6e95fcfb79a613a24cf75e86c6','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-24','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','b2efc086868b3c3274b7d68014b6ea93','Create Table','',NULL,'1.9.4'),('1227303685425-240','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','589dcc4157bfcf62a427f2ce7e4e1c29','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-241','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','52bde811efcc8b60f64bc0317ee6045','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-242','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','c58692cfa01f856a60b98215ba62e5a7','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-243','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','6d44d317d139438e24ff60424ea7df8d','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-244','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','e6e61db0f6b2f486a52797219e8545f','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-245','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','f5403a8a515082dd136af948e6bf4c97','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-246','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','fa4d4b30d92fa994ee2977a353a726f1','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-247','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','67b39f2b2132f427ff2a6fcf4a79bdeb','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-248','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','e67632f4fa63536017f3bcecb073de71','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-249','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','4c81272b8d3d7e4bd95a8cf8e9a91e6a','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-25','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','add581f3645d35c6ef17892ea196362','Create Table','',NULL,'1.9.4'),('1227303685425-250','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','54fb2e3a25332da06e38a9d2b43badfd','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-251','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','fb1a29f3188a43c1501e142ba09fd778','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-252','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','e5c417adb79757dd942b194ad59cd2e3','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-253','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','412115fd6d7bedbba0d3439617328693','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-254','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','e76b81d6b51f58983432c296f319e48','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-255','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','80703c6fd0656cf832f32f75ad64f3','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-256','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','88b295bfc4d05f928c8d7cbf36c84d','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-257','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','853fb4af8c20137ecf9d858713cff6','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-258','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','c33b2874e3d38ddef784a0ccb03e3f74','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-259','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','bb18dfa78fe1322413ff4f0871712a','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-26','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','d11eef4b608b5de1be8c24f4cef73a1','Create Table','',NULL,'1.9.4'),('1227303685425-260','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','f0953d50abcef615f855b6d6ef0bbd3','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-261','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','2e24f8fdf2b3dfb9abb02ccaeee87f4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-262','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','28d3165b5789d4ccb62ac271c8d86bdd','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-263','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','7f93b659cc1a2a66c8666f7ab5793b','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-264','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','c51f76c76ca7945eeb2423b56fd754ff','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-265','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','46b310f19ea8c0da3afd50c7f2717f2','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-266','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','aa7981765d72e6c22281f91c71602ce2','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-267','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','b2bc8373ebb491233bea3b6403e2fea','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-268','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','f4882dfee1f2ffac3be37258a8ec4c43','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-269','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','4c9212f9fa9f24ef868c7fd5851b7e8f','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-27','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','98d1653ea222665c66abb33ccd18853f','Create Table','',NULL,'1.9.4'),('1227303685425-270','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','aaf31e19b19f9e4b840d08ea730d1','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-271','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','47c543135499fdcd7f1ea1a4a71e495d','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-272','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','7df76774c3f25483e4b9612eee98d8','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-273','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','6cbef8ca7253e9b6941625756e3ead12','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-274','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','99c026624d5987f4a39b124c473da2','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-275','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','bab45b77299235943c469ca9e0396cff','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-276','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','c50376c85bff6bdf26a4ca97e2bfe','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-277','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','e7e8229acc4533fd3fca7534e531063','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-278','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','aed02dbce85e293159b787d270e57d63','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-279','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','93ce9101c3bc7214721f3163b11c33','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-28','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','a185fd8a442e66a329161f429e8c10f5','Create Table','',NULL,'1.9.4'),('1227303685425-280','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','cef4a9e68e71659662adddd67c976','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-281','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','929d293722d1d652834da0ceefa9c841','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-282','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','7a2834f44349838fcfbcb61648bc8a0','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-283','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','dd19eeb1b52497428e644a84f0653c8e','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-284','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','8bc32e874e45a6cdd60184e9c92d6c4','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-285','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','66af85da9d747cf7b13b19c358fd93c','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-286','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','14ac9a98a545c5b172c021486d1d36','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-287','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','7c4e227cf011839ef78fb4a83ed59d6','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-288','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:21','ab73c07b88fcfd7fffcd2bd41e05ec8','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-289','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','ffb7495ff05d83cf97d9986f895efe0','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-29','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','3524a06b47e415769fd22f9bd6442d6','Create Table','',NULL,'1.9.4'),('1227303685425-290','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','fd25f4e5377a68bc11f6927ec4ba1dc','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-291','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','fbdd163ca23861baa2f84dce077b6c','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-292','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','d0eebd47c26dc07764741ee4b1ac72','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-293','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','28b8cd9a32fb115c9c746712afa223','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-294','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','ada81a1d3e575780a54bbe79643db8fc','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-295','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','93e944816ec2dc7b31984a9e4932f3bb','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-296','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','54d71bd27cb66b9eb12716bdce3c7c','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-297','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','a9429cc74fb131d5d50899a402b21f','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-298','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','e38bd6437d3ce8274e16d02e73e976a8','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-299','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','2dcda62270603cbfa1581d1339b0a18e','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-3','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','1eeb9f4467c832c61b2cadf8da592ba9','Create Table','',NULL,'1.9.4'),('1227303685425-30','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','b9cbeed525ae08c4f696bda851227be','Create Table','',NULL,'1.9.4'),('1227303685425-300','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','c861c2e3e1fcbae170b5fe8c161ba7c9','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-301','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','0444230b26f283b1b4b4c3583ca6a2','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-302','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','3ae36b982c1a4dc32b623ed7eab649','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-303','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:22','dabab8dcefdde7eefee9d8cc177725','Add Foreign Key Constraint','',NULL,'1.9.4'),('1227303685425-31','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','3242cd7de7211c9d3331dfb854afdabf','Create Table','',NULL,'1.9.4'),('1227303685425-32','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','8149ef2df786f5e2f7965547ac66d1','Create Table','',NULL,'1.9.4'),('1227303685425-33','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','59b63eeddd85b6e75834d5ce529622','Create Table','',NULL,'1.9.4'),('1227303685425-34','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','7363eda0ec9d3d747381124a9984be0','Create Table','',NULL,'1.9.4'),('1227303685425-35','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','88c2f1f647e779f89929826a776fddc6','Create Table','',NULL,'1.9.4'),('1227303685425-36','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','a822d3f8bdcdb8ba93d164244d681f19','Create Table','',NULL,'1.9.4'),('1227303685425-37','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','ad9c5348bed3c5416789fe87923b60','Create Table','',NULL,'1.9.4'),('1227303685425-38','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','25c5c2d1727636891151ad31d1e70cd','Create Table','',NULL,'1.9.4'),('1227303685425-39','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','67727fb610365d2da6efb233d77f5ada','Create Table','',NULL,'1.9.4'),('1227303685425-4','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','9046b645b2dff85ac97ddf1ad6f3074','Create Table','',NULL,'1.9.4'),('1227303685425-40','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','c136d2cb06570483dcf5f585ff754f','Create Table','',NULL,'1.9.4'),('1227303685425-41','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','f0f7a7a4bf316add1449b49698647ef1','Create Table','',NULL,'1.9.4'),('1227303685425-42','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','9678d659e5e8b86b3645dfb5e1cddea0','Create Table','',NULL,'1.9.4'),('1227303685425-43','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','40d07fbd79ec3ef39901dc110b4fd75','Create Table','',NULL,'1.9.4'),('1227303685425-44','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','637177ae6888cf72e917824068f9a3be','Create Table','',NULL,'1.9.4'),('1227303685425-45','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','f32f6038a35c9261a179c3f1dd96bc','Create Table','',NULL,'1.9.4'),('1227303685425-46','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','a7b96f4406744ac11bb6a878868e04d','Create Table','',NULL,'1.9.4'),('1227303685425-47','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','f5221be8e26cd0f59670598766464641','Create Table','',NULL,'1.9.4'),('1227303685425-48','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','11aa9575b3c9f149db94168be6fd324','Create Table','',NULL,'1.9.4'),('1227303685425-49','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','13665b264c197c7b30604f0a8a564e4','Create Table','',NULL,'1.9.4'),('1227303685425-5','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','38c61d5d99b0ab3c444fe36d95181e1','Create Table','',NULL,'1.9.4'),('1227303685425-50','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','98ee82b9722f705320cabb8fc8d9d284','Create Table','',NULL,'1.9.4'),('1227303685425-51','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','fe724c66d29e156281396c9d1b29d227','Create Table','',NULL,'1.9.4'),('1227303685425-52','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','3654e6f75973aa49246dbd741c726743','Create Table','',NULL,'1.9.4'),('1227303685425-53','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','eb398720a35d58159d92e721e66ae6d','Create Table','',NULL,'1.9.4'),('1227303685425-54','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','c9ed5a341cef1cbe862b1523b7ce2a5','Create Table','',NULL,'1.9.4'),('1227303685425-55','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','d441e31ef661e4402141d14ac5734c1','Create Table','',NULL,'1.9.4'),('1227303685425-56','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','aaaef34153e53d2adcc71d90a0955','Create Table','',NULL,'1.9.4'),('1227303685425-57','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','d3acd4c3442938508976801dddfee866','Create Table','',NULL,'1.9.4'),('1227303685425-58','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','b0c0fd9674f39661e6bff6c78e65fcd3','Create Table','',NULL,'1.9.4'),('1227303685425-59','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','6f7e652ad837a378845fa9eb9757b99','Create Table','',NULL,'1.9.4'),('1227303685425-6','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','7f540998f53dc2bb2d19de05a14f2','Create Table','',NULL,'1.9.4'),('1227303685425-60','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','622543a591c2bab1f089cac341d9ce2','Create Table','',NULL,'1.9.4'),('1227303685425-61','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','fe5acec38c81bfb3277e193dd0dcc7','Create Table','',NULL,'1.9.4'),('1227303685425-62','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','c4b0da30561adf7d40fcd7da77c574','Create Table','',NULL,'1.9.4'),('1227303685425-63','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','45fd4df9a91aaaea2f515dbb8bbaae0','Create Table','',NULL,'1.9.4'),('1227303685425-64','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','99138560e6e73c3bf6f54fb792552cf','Create Table','',NULL,'1.9.4'),('1227303685425-65','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','4e33e6a7cf3bd773f126612bf0b9','Create Table','',NULL,'1.9.4'),('1227303685425-66','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','c223439be520db74976e1c6399b591b0','Create Table','',NULL,'1.9.4'),('1227303685425-67','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','6a136f60cfeb7ed87b27fd71825a8','Create Table','',NULL,'1.9.4'),('1227303685425-68','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','6744cea65ee820c9addf59d78b446ce8','Create Table','',NULL,'1.9.4'),('1227303685425-69','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','c1ee0d08f2c8238f17125d4a58263','Create Table','',NULL,'1.9.4'),('1227303685425-7','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','b816bb1dbeecf547611bcd37d29ccd27','Create Table','',NULL,'1.9.4'),('1227303685425-70','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','88c072335290bda8394acf5344b5be','Create Table','',NULL,'1.9.4'),('1227303685425-71','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','5887deaf859b231a28fca6cd8f53985f','Create Table','',NULL,'1.9.4'),('1227303685425-72','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','6e889f9922853419f736de152bab863','Create Table','',NULL,'1.9.4'),('1227303685425-73','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','db75ceff7fabd137f5ffde95c02ab814','Add Primary Key','',NULL,'1.9.4'),('1227303685425-74','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','d7d5d089c54468ac2cfd638f66fcb4','Add Primary Key','',NULL,'1.9.4'),('1227303685425-75','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','39dd463941e952d670a5c44194bfe578','Add Primary Key','',NULL,'1.9.4'),('1227303685425-76','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','dc6155351d7db68e99ff6e95bbc87be','Add Primary Key','',NULL,'1.9.4'),('1227303685425-77','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','4fee106cf921289c298f9c67fc87c7c5','Add Primary Key','',NULL,'1.9.4'),('1227303685425-78','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','3a3de23a22ae9c344b47a4d939e6ff70','Add Primary Key','',NULL,'1.9.4'),('1227303685425-79','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','88ab4066e17d3a326dc54e5837c09f41','Add Primary Key','',NULL,'1.9.4'),('1227303685425-8','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','e55f3abe82ce4b674bcdc1b4996b8b9','Create Table','',NULL,'1.9.4'),('1227303685425-80','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','940c1a878d7788f304e14cf4c3394c7','Add Primary Key','',NULL,'1.9.4'),('1227303685425-81','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','219f4f94772b86a6785a1168655d630','Add Primary Key','',NULL,'1.9.4'),('1227303685425-82','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','4e2676797833eba977cbcae8806c05f','Add Primary Key','',NULL,'1.9.4'),('1227303685425-83','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','587abb7e972b9dbae4711c9da4e55b','Add Primary Key','',NULL,'1.9.4'),('1227303685425-84','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','9435c219edb4e839af13de48a94f765c','Add Primary Key','',NULL,'1.9.4'),('1227303685425-85','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','1bf11ad88f6aeb16115e53aec0aa1fc9','Create Index','',NULL,'1.9.4'),('1227303685425-86','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','1e8c4a47a221a275929253c81814e3','Create Index','',NULL,'1.9.4'),('1227303685425-87','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','d7aa9fb9b259691ef965935112690c6','Create Index','',NULL,'1.9.4'),('1227303685425-88','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','8637a8777e63c352c513c78c60a435fb','Create Index','',NULL,'1.9.4'),('1227303685425-89','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:17','8124613e1c06fe7dbdd82ec7d77b513','Create Index','',NULL,'1.9.4'),('1227303685425-9','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:16','ee733a5ef91bcfc5b5a366e4d672c6d4','Create Table','',NULL,'1.9.4'),('1227303685425-90','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','ce32a6b73bddd39523fd702837d5e0ac','Create Index','',NULL,'1.9.4'),('1227303685425-91','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','8b88d2f395eb2b5c7a48a2691527335a','Create Index','',NULL,'1.9.4'),('1227303685425-92','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','9935e165ac0c994c840f0b6ad7e6ea9','Create Index','',NULL,'1.9.4'),('1227303685425-93','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','1d54748d2664161ec2a4ec4761dfcbe','Create Index','',NULL,'1.9.4'),('1227303685425-94','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','e4cbe444f931f7892fe1ff2cc8ef9','Create Index','',NULL,'1.9.4'),('1227303685425-95','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','8e83cb5c20116c3bafc2afcec67fbbcd','Create Index','',NULL,'1.9.4'),('1227303685425-96','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','844a9c8a5e4772bb4383d22c1b7c4f','Create Index','',NULL,'1.9.4'),('1227303685425-97','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','50c460b4b15dbe1c357c9637d29fba2e','Create Index','',NULL,'1.9.4'),('1227303685425-98','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','ebc8c01af6a50d966426a572a317d1','Create Index','',NULL,'1.9.4'),('1227303685425-99','ben (generated)','liquibase-schema-only.xml','2012-09-17 11:28:18','82c523688fbc57714736dfa7aafb48','Create Index','',NULL,'1.9.4'),('2','upul','liquibase-update-to-latest.xml','2012-09-17 11:28:25','271913afcc5e1fc2a96a6b12705e60a4','Add Foreign Key Constraint','Create the foreign key from the privilege required for to edit\n			a person attribute type and the privilege.privilege column',NULL,'1.9.4'),('200805281223','bmckown','liquibase-update-to-latest.xml','2012-09-17 11:28:27','ca38d15c388fd3987ee8e997f48bcb43','Create Table, Add Foreign Key Constraint','Create the concept_complex table',NULL,'1.9.4'),('200805281224','bmckown','liquibase-update-to-latest.xml','2012-09-17 11:28:27','ca898c6096d952b57e28c5edc2a957','Add Column','Adding the value_complex column to obs.  This may take a long time if you have a large number of observations.',NULL,'1.9.4'),('200805281225','bmckown','liquibase-update-to-latest.xml','2012-09-17 11:28:27','c951352b0c976e84b83acf3cfcbb9e','Insert Row','Adding a \'complex\' Concept Datatype',NULL,'1.9.4'),('200805281226','bmckown','liquibase-update-to-latest.xml','2012-09-17 11:28:29','b8b85f33a0d9fde9f478d77b1b868c80','Drop Table (x2)','Dropping the mimetype and complex_obs tables as they aren\'t needed in the new complex obs setup',NULL,'1.9.4'),('200809191226','smbugua','liquibase-update-to-latest.xml','2012-09-17 11:28:29','99ca2b5ae282ba5ff1df9c48f5f2e97','Add Column','Adding the hl7 archive message_state column so that archives can be tracked\n			\n			(preCondition database_version check in place because this change was in the old format in trunk for a while)',NULL,'1.9.4'),('200809191927','smbugua','liquibase-update-to-latest.xml','2012-09-17 11:28:29','8acc3bbd9bb21da496f4dbcaacfe16','Rename Column, Modify Column','Adding the hl7 archive message_state column so that archives can be tracked',NULL,'1.9.4'),('200811261102','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:25','7ce199737ad5ea72959eb52ece15ab8','Update Data','Fix field property for new Tribe person attribute',NULL,'1.9.4'),('200901101524','Knoll_Frank','liquibase-update-to-latest.xml','2012-09-17 11:28:29','8b599959a0ae994041db885337fd94e','Modify Column','Changing datatype of drug.retire_reason from DATETIME to varchar(255)',NULL,'1.9.4'),('200901130950','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:29','add260b86fbdfe8ddfd15480f7c12530','Delete Data (x2)','Remove Manage Tribes and View Tribes privileges from all roles',NULL,'1.9.4'),('200901130951','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:29','51eaa74d22ee244cc0cbde5ef4758e1c','Delete Data (x2)','Remove Manage Mime Types, View Mime Types, and Purge Mime Types privilege',NULL,'1.9.4'),('200901161126','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:29','478dceabf15112ad9eccee255df8b','Delete Data','Removed the database_version global property',NULL,'1.9.4'),('20090121-0949','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:29','a38a30d48634ab7268204167761db86c','Custom SQL','Switched the default xslt to use PV1-19 instead of PV1-1',NULL,'1.9.4'),('20090122-0853','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:29','5ef0554257579cb649fe4414efd233e0','Custom SQL, Add Lookup Table, Drop Foreign Key Constraint, Delete Data (x2), Drop Table','Remove duplicate concept name tags',NULL,'1.9.4'),('20090123-0305','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:29','fc5153a938b4ce0aa455ca9fea7ad9','Add Unique Constraint','Add unique constraint to the tags table',NULL,'1.9.4'),('20090214-2246','isherman','liquibase-update-to-latest.xml','2012-09-17 11:28:33','6c77a79fc9913830755829adaa983227','Custom SQL','Add weight and cd4 to patientGraphConcepts user property (mysql specific)',NULL,'1.9.4'),('20090214-2247','isherman','liquibase-update-to-latest.xml','2012-09-17 11:28:33','664dee9bed7b178ea62e8de7a8824045','Custom SQL','Add weight and cd4 to patientGraphConcepts user property (using standard sql)',NULL,'1.9.4'),('20090214-2248','isherman','liquibase-update-to-latest.xml','2012-09-17 11:28:33','489fc62a7b3196ba3887b8a2ddc8d93c','Custom SQL','Add weight and cd4 to patientGraphConcepts user property (mssql specific)',NULL,'1.9.4'),('200902142212','ewolodzko','liquibase-update-to-latest.xml','2012-09-17 11:28:49','ca3341c589781757f7b5f3e73a2b8f5','Add Column','Add a sortWeight field to PersonAttributeType',NULL,'1.9.4'),('200902142213','ewolodzko','liquibase-update-to-latest.xml','2012-09-17 11:28:49','918476ca54d9c1e77076be12012841','Update Data','Add default sortWeights to all current PersonAttributeTypes',NULL,'1.9.4'),('20090224-1229','Keelhaul+bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:31','2112b4bd53e438fc2ac4dc2e60213440','Create Table, Add Foreign Key Constraint (x2)','Add location tags table',NULL,'1.9.4'),('20090224-1250','Keelhaul+bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:33','ae593490311f38ee69962330e3788938','Create Table, Add Foreign Key Constraint (x2), Add Primary Key','Add location tag map table',NULL,'1.9.4'),('20090224-1256','Keelhaul+bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:33','27add85e296c1f22deb2fa17d77a542f','Add Column, Add Foreign Key Constraint','Add parent_location column to location table',NULL,'1.9.4'),('20090225-1551','dthomas','liquibase-update-to-latest.xml','2012-09-17 11:28:33','c490c377e78a9740a765e19aacf076','Rename Column (x2)','Change location_tag.name to location_tag.tag and location_tag.retired_reason to location_tag.retire_reason',NULL,'1.9.4'),('20090301-1259','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:33','88d88f5cc99c93c264e6976feef8eecc','Update Data (x2)','Fixes the description for name layout global property',NULL,'1.9.4'),('20090316-1008','vanand','liquibase-update-to-latest.xml','2012-09-17 11:28:34','fcdde237e04473db1e6fd16f56bf80e9','Modify Column (x7), Update Data, Modify Column (x18), Update Data, Modify Column (x11)','Change column types of all boolean columns to smallint. The columns used to be either tinyint(4) or MySQL\'s BIT.\n			(This may take a while on large patient or obs tables)',NULL,'1.9.4'),('200903210905','mseaton','liquibase-update-to-latest.xml','2012-09-17 11:28:36','a13f5939f4ce2a6a9a21c1847d3ab6d','Create Table, Add Foreign Key Constraint (x3)','Add a table to enable generic storage of serialized objects',NULL,'1.9.4'),('20090402-1515-38-cohort','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','e5d01ebcda1a2b4ef993a3c859933947','Add Column','Adding \"uuid\" column to cohort table',NULL,'1.9.4'),('20090402-1515-38-concept','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','575aaf2321f320331ba877f49a44b8','Add Column','Adding \"uuid\" column to concept table',NULL,'1.9.4'),('20090402-1515-38-concept_answer','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','57377447c4be5152c5bb18fdceec2312','Add Column','Adding \"uuid\" column to concept_answer table',NULL,'1.9.4'),('20090402-1515-38-concept_class','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','4510924e4fc34a5b4f4786af54a6e9d6','Add Column','Adding \"uuid\" column to concept_class table',NULL,'1.9.4'),('20090402-1515-38-concept_datatype','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','ffa6230eb5d1021556b2c106176b081','Add Column','Adding \"uuid\" column to concept_datatype table',NULL,'1.9.4'),('20090402-1515-38-concept_description','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','a864b52fe66664a95bb25d08ca0fae4','Add Column','Adding \"uuid\" column to concept_description table',NULL,'1.9.4'),('20090402-1515-38-concept_map','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','70db4397c48f719f6b6db9b228a5940','Add Column','Adding \"uuid\" column to concept_map table',NULL,'1.9.4'),('20090402-1515-38-concept_name','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','6a5bf2fb43b89825adbab42e32f8c95','Add Column','Adding \"uuid\" column to concept_name table',NULL,'1.9.4'),('20090402-1515-38-concept_name_tag','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','e5175d7effbbe1f327f6765c8563de','Add Column','Adding \"uuid\" column to concept_name_tag table',NULL,'1.9.4'),('20090402-1515-38-concept_proposal','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','d812c01d4487ee3afd8408d322ae6e9','Add Column','Adding \"uuid\" column to concept_proposal table',NULL,'1.9.4'),('20090402-1515-38-concept_set','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','a76a7ad772591c707d1470bfb8e96ebe','Add Column','Adding \"uuid\" column to concept_set table',NULL,'1.9.4'),('20090402-1515-38-concept_source','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','82f9e9817631b23dd768d92c451a4b','Add Column','Adding \"uuid\" column to concept_source table',NULL,'1.9.4'),('20090402-1515-38-concept_state_conversion','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','da7523634d5355b5bb2dc7ba3b544682','Add Column','Adding \"uuid\" column to concept_state_conversion table',NULL,'1.9.4'),('20090402-1515-38-drug','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','867c42ea0b5a864beccff584fa963ff','Add Column','Adding \"uuid\" column to drug table',NULL,'1.9.4'),('20090402-1515-38-encounter','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','53d8465949124bc6247b28a4a58121','Add Column','Adding \"uuid\" column to encounter table',NULL,'1.9.4'),('20090402-1515-38-encounter_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','ee9b948fa08ad55c3ecd75c32df82d5','Add Column','Adding \"uuid\" column to encounter_type table',NULL,'1.9.4'),('20090402-1515-38-field','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','e24dae16c5a010a575c9c1ac84d8eb','Add Column','Adding \"uuid\" column to field table',NULL,'1.9.4'),('20090402-1515-38-field_answer','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','7bbeeb1abdc5389f5a7488ee35498b','Add Column','Adding \"uuid\" column to field_answer table',NULL,'1.9.4'),('20090402-1515-38-field_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','6271df216ece46cecde2c5f642e84a','Add Column','Adding \"uuid\" column to field_type table',NULL,'1.9.4'),('20090402-1515-38-form','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','2569936cbd22e8029253d4e6034f738','Add Column','Adding \"uuid\" column to form table',NULL,'1.9.4'),('20090402-1515-38-form_field','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','e237246d492a5d9046edba679c2ef7b4','Add Column','Adding \"uuid\" column to form_field table',NULL,'1.9.4'),('20090402-1515-38-global_property','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','77d9d563a6b488dde8856cd8e44e74','Add Column','Adding \"uuid\" column to global_property table',NULL,'1.9.4'),('20090402-1515-38-hl7_in_archive','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','cc8aa360f98aedcc8399a46edb48ca7e','Add Column','Adding \"uuid\" column to hl7_in_archive table',NULL,'1.9.4'),('20090402-1515-38-hl7_in_error','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','4857a237f2d29901dc2721861b13aa1','Add Column','Adding \"uuid\" column to hl7_in_error table',NULL,'1.9.4'),('20090402-1515-38-hl7_in_queue','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','3e0bedc6f28626d1ed8feba9b12744','Add Column','Adding \"uuid\" column to hl7_in_queue table',NULL,'1.9.4'),('20090402-1515-38-hl7_source','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','0c1637fe4ec912bdf75f4ec338f42','Add Column','Adding \"uuid\" column to hl7_source table',NULL,'1.9.4'),('20090402-1515-38-location','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','2fc3aaf750476ed2df71b6689d941546','Add Column','Adding \"uuid\" column to location table',NULL,'1.9.4'),('20090402-1515-38-location_tag','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','b8c3ac793659918dfb140d94207d5d','Add Column','Adding \"uuid\" column to location_tag table',NULL,'1.9.4'),('20090402-1515-38-note','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','c4722e59a9be1c47d0569216e92ad63','Add Column','Adding \"uuid\" column to note table',NULL,'1.9.4'),('20090402-1515-38-notification_alert','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','7a66a648529a78d215e17023cdf5d5','Add Column','Adding \"uuid\" column to notification_alert table',NULL,'1.9.4'),('20090402-1515-38-notification_template','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','739a3826ba3226875d8f944140e1c188','Add Column','Adding \"uuid\" column to notification_template table',NULL,'1.9.4'),('20090402-1515-38-obs','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','75c1752f21de4b95569c7ac66e826e2f','Add Column','Adding \"uuid\" column to obs table',NULL,'1.9.4'),('20090402-1515-38-orders','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','f19f5e991569bc1341e9dd31a412da43','Add Column','Adding \"uuid\" column to orders table',NULL,'1.9.4'),('20090402-1515-38-order_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','f2c93b646d83d969bf8ba09d11d36edd','Add Column','Adding \"uuid\" column to order_type table',NULL,'1.9.4'),('20090402-1515-38-patient_identifier','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','dd97565c4ff6c410838a5feb41975d0','Add Column','Adding \"uuid\" column to patient_identifier table',NULL,'1.9.4'),('20090402-1515-38-patient_identifier_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','613f3c8d0226c305678d461a89db238','Add Column','Adding \"uuid\" column to patient_identifier_type table',NULL,'1.9.4'),('20090402-1515-38-patient_program','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','a2c23c3ef77d8ea9a2406c428e207244','Add Column','Adding \"uuid\" column to patient_program table',NULL,'1.9.4'),('20090402-1515-38-patient_state','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','57563194b5fa551f129a9cce1b38e8e7','Add Column','Adding \"uuid\" column to patient_state table',NULL,'1.9.4'),('20090402-1515-38-person','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','e8a138182c0ba1053131888f8cf8b4','Add Column','Adding \"uuid\" column to person table',NULL,'1.9.4'),('20090402-1515-38-person_address','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','2617c04da91ecda5fd2b45237f5ddd','Add Column','Adding \"uuid\" column to person_address table',NULL,'1.9.4'),('20090402-1515-38-person_attribute','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','ae559b7cc39a175062126e4815d762','Add Column','Adding \"uuid\" column to person_attribute table',NULL,'1.9.4'),('20090402-1515-38-person_attribute_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','68f914b55c3b3fb7dc2a77fd8d933d2','Add Column','Adding \"uuid\" column to person_attribute_type table',NULL,'1.9.4'),('20090402-1515-38-person_name','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','1b8be5c78ca558b45a25d5eaefbbed7','Add Column','Adding \"uuid\" column to person_name table',NULL,'1.9.4'),('20090402-1515-38-privilege','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','13d0d85bf5609b955b128fd6f6bc5c6d','Add Column','Adding \"uuid\" column to privilege table',NULL,'1.9.4'),('20090402-1515-38-program','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','39a4f7c8c86791b166d280d9b22ae','Add Column','Adding \"uuid\" column to program table',NULL,'1.9.4'),('20090402-1515-38-program_workflow','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','70877f3fdcfd50bea8d8d0c19fb115ac','Add Column','Adding \"uuid\" column to program_workflow table',NULL,'1.9.4'),('20090402-1515-38-program_workflow_state','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','116efa4fd75f61e034c33a2d4e4c75e2','Add Column','Adding \"uuid\" column to program_workflow_state table',NULL,'1.9.4'),('20090402-1515-38-relationship','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','945c9c8a51c2ddfd75195df74b01c37','Add Column','Adding \"uuid\" column to relationship table',NULL,'1.9.4'),('20090402-1515-38-relationship_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','09c5c28357a6999636569c43bf1d4a','Add Column','Adding \"uuid\" column to relationship_type table',NULL,'1.9.4'),('20090402-1515-38-report_object','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','4bf73bf794f3b6ec1b5dff44e23679','Add Column','Adding \"uuid\" column to report_object table',NULL,'1.9.4'),('20090402-1515-38-report_schema_xml','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','2b7354fdbf3a45a725ad7392f73c1d2c','Add Column','Adding \"uuid\" column to report_schema_xml table',NULL,'1.9.4'),('20090402-1515-38-role','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','2955291013b5657ffa9eeeb526d5563','Add Column','Adding \"uuid\" column to role table',NULL,'1.9.4'),('20090402-1515-38-serialized_object','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','bbb72e88cde2dd2f44f72c175a75f10','Add Column','Adding \"uuid\" column to serialized_object table',NULL,'1.9.4'),('20090402-1516-cohort','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','c916ab788583392ea698441818ca8b','Update Data','Generating UUIDs for all rows in cohort table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','5cd0f78948d656fbe03411c3872b8d3d','Update Data','Generating UUIDs for all rows in concept table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_answer','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','76742a2d89cdf9216b23ff1cc43f7e2b','Update Data','Generating UUIDs for all rows in concept_answer table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_class','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','b036159e7e6a62be355ab2da84dc6d6','Update Data','Generating UUIDs for all rows in concept_class table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_datatype','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','ffd8b9d259bb98779a6a444a3c597','Update Data','Generating UUIDs for all rows in concept_datatype table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_description','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','471858eb414dd416787873d59ce92d','Update Data','Generating UUIDs for all rows in concept_description table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_map','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','fd1e26504b7b5e227de9a6265cdfaf6a','Update Data','Generating UUIDs for all rows in concept_map table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_name','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','af235654349acd661ecadde2b2c9d29','Update Data','Generating UUIDs for all rows in concept_name table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_name_tag','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','bddb56793bee7cbb59a749557e601b','Update Data','Generating UUIDs for all rows in concept_name_tag table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_proposal','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','d35f42aea2acad9d3eb98a0aa68d','Update Data','Generating UUIDs for all rows in concept_proposal table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_set','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','131a6e5d0c39e32443f72d07f14f86','Update Data','Generating UUIDs for all rows in concept_set table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_source','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','3376f58bc68455beaeca7723e8dfdc','Update Data','Generating UUIDs for all rows in concept_source table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-concept_state_conversion','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','2daca61a968dc1d71dd012c2a770a9a5','Update Data','Generating UUIDs for all rows in concept_state_conversion table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-drug','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','60428b7567bee56397d3ad9c3c74da2','Update Data','Generating UUIDs for all rows in drug table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-encounter','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','855a3866a99360f46dcb54965341a8e','Update Data','Generating UUIDs for all rows in encounter table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-encounter_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','2694d23adc4fec35aabf472b737fc36e','Update Data','Generating UUIDs for all rows in encounter_type table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-field','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','5e406b6bd57710696766bc498b9dc0','Update Data','Generating UUIDs for all rows in field table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-field_answer','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','1b547411666d85b4ee383c2feef3ce','Update Data','Generating UUIDs for all rows in field_answer table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-field_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','b5cd4a815ecfcae3e7a0e16539c469b1','Update Data','Generating UUIDs for all rows in field_type table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-form','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','867b94d343b1f0347371ec9fa8e3e','Update Data','Generating UUIDs for all rows in form table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-form_field','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','ea6c2b047b7c59190cd637547dbd3dd','Update Data','Generating UUIDs for all rows in form_field table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-global_property','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:37','c743637b2a65526c09996236e9967b2','Update Data','Generating UUIDs for all rows in global_property table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-hl7_in_archive','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','b1df4ba911718e7987ad3ff8f36fa2b','Update Data','Generating UUIDs for all rows in hl7_in_archive table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-hl7_in_error','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','a5fbc93dd5decad5f1c254acabd0fa47','Update Data','Generating UUIDs for all rows in hl7_in_error table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-hl7_in_queue','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','9319f535948f73e3fa77872421e21c28','Update Data','Generating UUIDs for all rows in hl7_in_queue table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-hl7_source','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','de74537c46e71afe35c4e3da63da98c','Update Data','Generating UUIDs for all rows in hl7_source table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-location','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','ba24775d1c4e3a5224a88d4f9fd53a1','Update Data','Generating UUIDs for all rows in location table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-location_tag','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','19627ee1543bc0b639f935921a5ef2a','Update Data','Generating UUIDs for all rows in location_tag table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-note','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','dc91bee3824a094b728628b5d74aa7','Update Data','Generating UUIDs for all rows in note table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-notification_alert','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','d9fccf1943822a79503c7f15837316','Update Data','Generating UUIDs for all rows in notification_alert table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-notification_template','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','ff8dd4c09bcb33e9a6b1cba9275646b','Update Data','Generating UUIDs for all rows in notification_template table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-obs','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','77f2862ce7ca7192a4043a35c06673','Update Data','Generating UUIDs for all rows in obs table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-orders','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','967a61314dfd1114c81afbe4dd52ae67','Update Data','Generating UUIDs for all rows in orders table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-order_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','f460eb4ea5f07d87cce18e84ba65e12','Update Data','Generating UUIDs for all rows in order_type table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-patient_identifier','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','7539b5d6c2a704d95dc5f56e6e12d73','Update Data','Generating UUIDs for all rows in patient_identifier table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-patient_identifier_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','79f11da93475eb7467b9297dfbd7c79','Update Data','Generating UUIDs for all rows in patient_identifier_type table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-patient_program','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','1e53eb71d25b1d1eda33fe22d66133','Update Data','Generating UUIDs for all rows in patient_program table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-patient_state','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','a0ebd331c38f5296e92c72b96b801d4c','Update Data','Generating UUIDs for all rows in patient_state table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-person','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','cf951b6a27951e66a9a0889eb4fd6f15','Update Data','Generating UUIDs for all rows in person table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-person_address','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','c630f59a4c330969255165c34377d1d','Update Data','Generating UUIDs for all rows in person_address table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-person_attribute','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','608a4c703f42a7269e5d7c39a1d86de','Update Data','Generating UUIDs for all rows in person_attribute table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-person_attribute_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','d04f6cc7bfd235bca17e6d1d10505c3','Update Data','Generating UUIDs for all rows in person_attribute_type table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-person_name','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','e22e8f89ef24732458581cc697d55f','Update Data','Generating UUIDs for all rows in person_name table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-privilege','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','e3eed777bb7727aa1c80e55e5b307c','Update Data','Generating UUIDs for all rows in privilege table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-program','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','7af9569b516c59b71147a7c8bea93d63','Update Data','Generating UUIDs for all rows in program table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-program_workflow','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','56214fa359f3e3bc39104d37fc996538','Update Data','Generating UUIDs for all rows in program_workflow table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-program_workflow_state','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','4fc3046f6f1a9eee5da901fdaacae34','Update Data','Generating UUIDs for all rows in program_workflow_state table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-relationship','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','76db1fe2b15c4e35c0cc6accb385ca','Update Data','Generating UUIDs for all rows in relationship table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-relationship_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','716f58764be9b098e3af891951c','Update Data','Generating UUIDs for all rows in relationship_type table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-report_object','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','acf4b4d7f2f06953fae294bfad74e5b9','Update Data','Generating UUIDs for all rows in report_object table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-report_schema_xml','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','9be2c79cdb24345c90a55ecf9786d1','Update Data','Generating UUIDs for all rows in report_schema_xml table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-role','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','7516c06f5ac23efd21394154e4f157d2','Update Data','Generating UUIDs for all rows in role table via built in uuid function.',NULL,'1.9.4'),('20090402-1516-serialized_object','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','e872c1f23acaea7d2953ab948d3a4b81','Update Data','Generating UUIDs for all rows in serialized_object table via built in uuid function.',NULL,'1.9.4'),('20090402-1517','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','89cc7a14b0582f157ea2dbb9de092fd','Custom Change','Adding UUIDs to all rows in all columns via a java class. (This will take a long time on large databases)',NULL,'1.9.4'),('20090402-1518','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','dfaf705d7776613e619dedf3e06d7640','Add Not-Null Constraint (x52)','Now that UUID generation is done, set the uuid columns to not \"NOT NULL\"',NULL,'1.9.4'),('20090402-1519-cohort','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:38','24d3119128e95cb2e5a1a76bfdc54','Create Index','Creating unique index on cohort.uuid column',NULL,'1.9.4'),('20090402-1519-concept','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','52b46db1629e2ba9581d2ff5569238f','Create Index','Creating unique index on concept.uuid column',NULL,'1.9.4'),('20090402-1519-concept_answer','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','fda1ed7f30fa23da5a9013529bbf1c3b','Create Index','Creating unique index on concept_answer.uuid column',NULL,'1.9.4'),('20090402-1519-concept_class','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','1451a7423139d4a59245f5754cd63dac','Create Index','Creating unique index on concept_class.uuid column',NULL,'1.9.4'),('20090402-1519-concept_datatype','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','7d6afd51a3308f3d7619676eb55e4be6','Create Index','Creating unique index on concept_datatype.uuid column',NULL,'1.9.4'),('20090402-1519-concept_description','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','2546c2e71f8999d46d31c112507ebf87','Create Index','Creating unique index on concept_description.uuid column',NULL,'1.9.4'),('20090402-1519-concept_map','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','61c1fac89a8e525494df82743285432','Create Index','Creating unique index on concept_map.uuid column',NULL,'1.9.4'),('20090402-1519-concept_name','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','3be74640f8f2d5993afb193b3fe3075','Create Index','Creating unique index on concept_name.uuid column',NULL,'1.9.4'),('20090402-1519-concept_name_tag','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','f58b6b51d744b015862e9835369c14f6','Create Index','Creating unique index on concept_name_tag.uuid column',NULL,'1.9.4'),('20090402-1519-concept_proposal','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','b7c251ed66288c2486d39b2e8e5056','Create Index','Creating unique index on concept_proposal.uuid column',NULL,'1.9.4'),('20090402-1519-concept_set','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','c99542d48ce96e6f351cc158a814ed0','Create Index','Creating unique index on concept_set.uuid column',NULL,'1.9.4'),('20090402-1519-concept_source','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','7ca56bc86099e23ed197af71e423b44b','Create Index','Creating unique index on concept_source.uuid column',NULL,'1.9.4'),('20090402-1519-concept_state_conversion','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','311f46b442c5740393c13b924cf4d2','Create Index','Creating unique index on concept_state_conversion.uuid column',NULL,'1.9.4'),('20090402-1519-drug','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','b4867846580078e85629b9554c9845','Create Index','Creating unique index on drug.uuid column',NULL,'1.9.4'),('20090402-1519-encounter','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','b0a5fc73f82532e4f2b392e95a9d1e9d','Create Index','Creating unique index on encounter.uuid column',NULL,'1.9.4'),('20090402-1519-encounter_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','5ed4dbb39ded5667d9897849eb609162','Create Index','Creating unique index on encounter_type.uuid column',NULL,'1.9.4'),('20090402-1519-field','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','f1bb647f7e95324ccdf6a8f644a36d47','Create Index','Creating unique index on field.uuid column',NULL,'1.9.4'),('20090402-1519-field_answer','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','d3dde65374ee7222c83b6487f936a4f','Create Index','Creating unique index on field_answer.uuid column',NULL,'1.9.4'),('20090402-1519-field_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','559a6a362f1de933a495f7c281bd28','Create Index','Creating unique index on field_type.uuid column',NULL,'1.9.4'),('20090402-1519-form','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','854ea11fedf3e622cec570e54de222','Create Index','Creating unique index on form.uuid column',NULL,'1.9.4'),('20090402-1519-form_field','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','243c552b3a5c3011b214d261bd32c5e','Create Index','Creating unique index on form_field.uuid column',NULL,'1.9.4'),('20090402-1519-global_property','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','edf3a71ce9d58b7b5c86889cdee84e','Create Index','Creating unique index on global_property.uuid column',NULL,'1.9.4'),('20090402-1519-hl7_in_archive','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','e3e811242f2c8ef055756866aafaed70','Create Index','Creating unique index on hl7_in_archive.uuid column',NULL,'1.9.4'),('20090402-1519-hl7_in_error','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:39','d4b3b34289fe9a4b7c609f495cdf20','Create Index','Creating unique index on hl7_in_error.uuid column',NULL,'1.9.4'),('20090402-1519-hl7_in_queue','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','21b688a853a9f43c5ad24a53df7fd8c','Create Index','Creating unique index on hl7_in_queue.uuid column',NULL,'1.9.4'),('20090402-1519-hl7_source','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','b5457643d13df39dac482d34fc884','Create Index','Creating unique index on hl7_source.uuid column',NULL,'1.9.4'),('20090402-1519-location','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','a6c5a18e65815abaa42ee07e792f1a','Create Index','Creating unique index on location.uuid column',NULL,'1.9.4'),('20090402-1519-location_tag','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','abf99b851bdc38952843b2635104cfc','Create Index','Creating unique index on location_tag.uuid column',NULL,'1.9.4'),('20090402-1519-note','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','526b32a1d8d77223bd45a722746d246','Create Index','Creating unique index on note.uuid column',NULL,'1.9.4'),('20090402-1519-notification_alert','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','f87f2b6d5f5074c33245321cfbb5878','Create Index','Creating unique index on notification_alert.uuid column',NULL,'1.9.4'),('20090402-1519-notification_template','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','add9db68c554fbc2b4360e69438d3a8','Create Index','Creating unique index on notification_template.uuid column',NULL,'1.9.4'),('20090402-1519-obs','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','7a584580843bfd1a170c061c65413c1','Create Index','Creating unique index on obs.uuid column',NULL,'1.9.4'),('20090402-1519-orders','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','8ffafd6886f4a5fc1515783eabec738','Create Index','Creating unique index on orders.uuid column',NULL,'1.9.4'),('20090402-1519-order_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','ae9ea28f147e50c959610dafa5ec25c','Create Index','Creating unique index on order_type.uuid column',NULL,'1.9.4'),('20090402-1519-patient_identifier','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','4b5861f5669e0f29087d617d27513f1','Create Index','Creating unique index on patient_identifier.uuid column',NULL,'1.9.4'),('20090402-1519-patient_identifier_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','73da1d5f87d0397bb396a9b3b2ee4ea','Create Index','Creating unique index on patient_identifier_type.uuid column',NULL,'1.9.4'),('20090402-1519-patient_program','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','34b7b01f4a362b637b239209f2a4820','Create Index','Creating unique index on patient_program.uuid column',NULL,'1.9.4'),('20090402-1519-patient_state','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','efc7e9ba6fc3626ad4afe9c9c41fc0','Create Index','Creating unique index on patient_state.uuid column',NULL,'1.9.4'),('20090402-1519-person','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','2a77bcf4862d901d292c5877dfce4f58','Create Index','Creating unique index on person.uuid column',NULL,'1.9.4'),('20090402-1519-person_address','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','3672c61c62cd26ef3a726613517f6234','Create Index','Creating unique index on person_address.uuid column',NULL,'1.9.4'),('20090402-1519-person_attribute','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','d77b26d4596f149a38d65f2be9fa6b51','Create Index','Creating unique index on person_attribute.uuid column',NULL,'1.9.4'),('20090402-1519-person_attribute_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:40','7affacc59b57fabf36578609cbc481c','Create Index','Creating unique index on person_attribute_type.uuid column',NULL,'1.9.4'),('20090402-1519-person_name','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','f98a895de49abac9dc5483f4de705adf','Create Index','Creating unique index on person_name.uuid column',NULL,'1.9.4'),('20090402-1519-privilege','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','e9406113927e90f14d8ea3aee7b265e1','Create Index','Creating unique index on privilege.uuid column',NULL,'1.9.4'),('20090402-1519-program','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','f34ba0c98814f03cff165d4b1c391312','Create Index','Creating unique index on program.uuid column',NULL,'1.9.4'),('20090402-1519-program_workflow','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','d537382486f2a3a870983bc2a7e72a4c','Create Index','Creating unique index on program_workflow.uuid column',NULL,'1.9.4'),('20090402-1519-program_workflow_state','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','8020f25b8e995b846d1c3c11e4232399','Create Index','Creating unique index on program_workflow_state.uuid column',NULL,'1.9.4'),('20090402-1519-relationship','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','e56ca578a8411decdfee74a91184d4f','Create Index','Creating unique index on relationship.uuid column',NULL,'1.9.4'),('20090402-1519-relationship_type','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','179c86d0bf953e67eac2c7cd235a11c','Create Index','Creating unique index on relationship_type.uuid column',NULL,'1.9.4'),('20090402-1519-report_object','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','7f15947ec522a8fe38517251fdcb4f27','Create Index','Creating unique index on report_object.uuid column',NULL,'1.9.4'),('20090402-1519-report_schema_xml','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','f1f2922dead3177b121670e26e2be414','Create Index','Creating unique index on report_schema_xml.uuid column',NULL,'1.9.4'),('20090402-1519-role','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','48679931cd75ae76824f0a194d011b8','Create Index','Creating unique index on role.uuid column',NULL,'1.9.4'),('20090402-1519-serialized_object','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:41','3457ded29450bcb294aea6848e244a1','Create Index','Creating unique index on serialized_object.uuid column',NULL,'1.9.4'),('20090408-1298','Cory McCarthy','liquibase-update-to-latest.xml','2012-09-17 11:28:36','a675aa3da8651979b233d4c4b8d7f1','Modify Column','Changed the datatype for encounter_type to \'text\' instead of just 50 chars',NULL,'1.9.4'),('200904091023','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','a1ee9e6f174e2d8e5ee076126135b7','Delete Data (x4)','Remove Manage Tribes and View Tribes privileges from the privilege table and role_privilege table.\n			The privileges will be recreated by the Tribe module if it is installed.',NULL,'1.9.4'),('20090414-0804','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:43','b5e187a86493a19ff33881d5c5adb61','Drop Foreign Key Constraint','Dropping foreign key on concept_set.concept_id table',NULL,'1.9.4'),('20090414-0805','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:43','afa3399786d9a6695173727b496ba2','Drop Primary Key','Dropping primary key on concept set table',NULL,'1.9.4'),('20090414-0806','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:43','3c192287a93261df2ef571d55d91a5e','Add Column','Adding new integer primary key to concept set table',NULL,'1.9.4'),('20090414-0807','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:43','548ba564f1cf1a5e4353bade7d0a71f','Create Index, Add Foreign Key Constraint','Adding index and foreign key to concept_set.concept_id column',NULL,'1.9.4'),('20090414-0808a','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:45','13cd2c97df1b7163cb18296c328110','Drop Foreign Key Constraint','Dropping foreign key on patient_identifier.patient_id column',NULL,'1.9.4'),('20090414-0808b','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:45','4a69eb4dc87ad51da7497646b55e09','Drop Primary Key','Dropping non-integer primary key on patient identifier table before adding a new integer primary key',NULL,'1.9.4'),('20090414-0809','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:45','912731dafcb26634d6737b05d1f428d','Add Column','Adding new integer primary key to patient identifier table',NULL,'1.9.4'),('20090414-0810','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:45','8ba5e84d1f85707b4a248a21cf7afe16','Create Index, Add Foreign Key Constraint','Adding index and foreign key on patient_identifier.patient_id column',NULL,'1.9.4'),('20090414-0811a','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:47','3b73a595618cc7d4279899c189aeab','Drop Foreign Key Constraint','Dropping foreign key on concept_word.concept_id column',NULL,'1.9.4'),('20090414-0811b','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:47','be823f8a9d48f3df0c83cbafaf803','Drop Primary Key','Dropping non-integer primary key on concept word table before adding new integer one',NULL,'1.9.4'),('20090414-0812','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:47','98a31beac32af7be584e5f35125ebe77','Add Column','Adding integer primary key to concept word table',NULL,'1.9.4'),('20090414-0812b','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:47','fa3df9294b026b6375cde2993bc936c','Add Foreign Key Constraint','Re-adding foreign key for concept_word.concept_name_id',NULL,'1.9.4'),('200904271042','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','1b8214c28c262ca31bb67549c2e29e3','Drop Column','Remove the now unused synonym column',NULL,'1.9.4'),('20090428-0811aa','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:47','8d1cd3bc4fbeb36c55125e68426804d','Drop Foreign Key Constraint','Removing concept_word.concept_name_id foreign key so that primary key can be changed to concept_word_id',NULL,'1.9.4'),('20090428-0854','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','9124601e6dc67a603d950617bd17cea','Add Foreign Key Constraint','Adding foreign key for concept_word.concept_id column',NULL,'1.9.4'),('200905071626','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','d2873b168b82edab25ab1080304772c9','Create Index','Add an index to the concept_word.concept_id column (This update may fail if it already exists)',NULL,'1.9.4'),('200905150814','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:36','52a64aaf6ac8af65b8023c0ea96162d','Delete Data','Deleting invalid concept words',NULL,'1.9.4'),('200905150821','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','d6dedef195a69f62fd1c73adcfd93146','Custom SQL','Deleting duplicate concept word keys',NULL,'1.9.4'),('200906301606','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','e0a8f2578ba4e6cbaf9a7f41465bd1b','Modify Column','Change person_attribute_type.sort_weight from an integer to a float',NULL,'1.9.4'),('200907161638-1','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','98edf5727177ea045dff438d7fca27','Modify Column','Change obs.value_numeric from a double(22,0) to a double',NULL,'1.9.4'),('200907161638-2','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','cd8d3abc418a18ff786b14f27798389','Modify Column','Change concept_numeric columns from a double(22,0) type to a double',NULL,'1.9.4'),('200907161638-3','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','8f30dc389272e5f94c34286683ba2c1','Modify Column','Change concept_set.sort_weight from a double(22,0) to a double',NULL,'1.9.4'),('200907161638-4','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','7875dcee562bcd9bb5ad49cddcd9241','Modify Column','Change concept_set_derived.sort_weight from a double(22,0) to a double',NULL,'1.9.4'),('200907161638-5','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','1b4a410e183ef30ba9d7b7ae965e2a1','Modify Column','Change drug table columns from a double(22,0) to a double',NULL,'1.9.4'),('200907161638-6','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:49','8e5c737f5d65e25f73354f4cd26523','Modify Column','Change drug_order.dose from a double(22,0) to a double',NULL,'1.9.4'),('200908291938-1','dthomas','liquibase-update-to-latest.xml','2012-09-17 11:28:52','5658a0964596d66e70804ddaa48bd77c','Modify Column','set hl7_code in ConceptSource to nullable column',NULL,'1.9.4'),('200908291938-2a','dthomas','liquibase-update-to-latest.xml','2012-09-17 11:28:52','4fff1cee1a3f13ccc2a13c5448f9b1c','Modify Column','set retired in ConceptSource to tinyint(1) not null',NULL,'1.9.4'),('20090831-1039-38-scheduler_task_config','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:50','612afca3f7f1697d93248951bc8c7','Add Column','Adding \"uuid\" column to scheduler_task_config table',NULL,'1.9.4'),('20090831-1040-scheduler_task_config','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:50','baf8889ac5cfe2670e6b9c4bb58a47a','Update Data','Generating UUIDs for all rows in scheduler_task_config table via built in uuid function.',NULL,'1.9.4'),('20090831-1041-scheduler_task_config','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:50','b3e5e26774a54c2cb3397196bedee570','Custom Change','Adding UUIDs to all rows in scheduler_task_config table via a java class for non mysql/oracle/mssql databases.',NULL,'1.9.4'),('20090831-1042-scheduler_task_config','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:50','11fe3a4afc8e8997caab8e71781e5b10','Add Not-Null Constraint','Now that UUID generation is done for scheduler_task_config, set the uuid column to not \"NOT NULL\"',NULL,'1.9.4'),('20090831-1043-scheduler_task_config','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:28:50','b8165a4b9f7ea28a6084d74aadbb9432','Create Index','Creating unique index on scheduler_task_config.uuid column',NULL,'1.9.4'),('20090907-1','Knoll_Frank','liquibase-update-to-latest.xml','2012-09-17 11:28:50','542a60e1a743881571dd1c86ed1c4','Rename Column','Rename the concept_source.date_voided column to date_retired',NULL,'1.9.4'),('20090907-2a','Knoll_Frank','liquibase-update-to-latest.xml','2012-09-17 11:28:52','eba5ce8a7d9634e6f528627a82d6b1','Drop Foreign Key Constraint','Remove the concept_source.voided_by foreign key constraint',NULL,'1.9.4'),('20090907-2b','Knoll_Frank','liquibase-update-to-latest.xml','2012-09-17 11:28:52','8412909f334c2221611ce07acfcb5a6','Rename Column, Add Foreign Key Constraint','Rename the concept_source.voided_by column to retired_by',NULL,'1.9.4'),('20090907-3','Knoll_Frank','liquibase-update-to-latest.xml','2012-09-17 11:28:52','627e3e7cc7d9a2aece268fa93bf88','Rename Column','Rename the concept_source.voided column to retired',NULL,'1.9.4'),('20090907-4','Knoll_Frank','liquibase-update-to-latest.xml','2012-09-17 11:28:52','74ad3e52598dfc9af0785c753ba818ac','Rename Column','Rename the concept_source.void_reason column to retire_reason',NULL,'1.9.4'),('200909281005-1','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:28:54','6e728239bd76fa58671bdb4d03ec346','Create Table','Create logic token table to store all registered token',NULL,'1.9.4'),('200909281005-2','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:28:56','c516c1b552f2fdace76762d81bb3bae6','Create Table','Create logic token tag table to store all tag associated with a token',NULL,'1.9.4'),('200909281005-3','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:28:58','5a8f5825e1de3fcbc5ed48f0b8cd3e77','Add Foreign Key Constraint','Adding foreign key for primary key of logic_rule_token_tag',NULL,'1.9.4'),('200909281005-4a','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:29:00','c1bd29bc82b2d227f9eb47f6676f28d','Drop Foreign Key Constraint','Removing bad foreign key for logic_rule_token.creator',NULL,'1.9.4'),('200909281005-4aa','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:29:02','bbc452fd8fa3ff3eaab4c5fbb2ac2fe2','Drop Foreign Key Constraint','Removing bad foreign key for logic_rule_token.creator',NULL,'1.9.4'),('200909281005-4b','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:29:04','ee57b86d35016d4a26116a195908d4d','Add Foreign Key Constraint','Adding correct foreign key for logic_rule_token.creator',NULL,'1.9.4'),('200909281005-5a','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:29:06','9d6119b183383447fe37c9494767927','Add Foreign Key Constraint','Adding foreign key for logic_rule_token.changed_by',NULL,'1.9.4'),('20091001-1023','rcrichton','liquibase-update-to-latest.xml','2012-09-17 11:29:11','6c5b675ed15654c61ad28b7794180c0','Add Column','add retired column to relationship_type table',NULL,'1.9.4'),('20091001-1024','rcrichton','liquibase-update-to-latest.xml','2012-09-17 11:29:11','cdee114b801e2fd29f1f906d3fa553c4','Add Column','add retired_by column to relationship_type table',NULL,'1.9.4'),('20091001-1025','rcrichton','liquibase-update-to-latest.xml','2012-09-17 11:29:13','ded86a7b7ba57a447fdb14ee12804','Add Foreign Key Constraint','Create the foreign key from the relationship.retired_by to users.user_id.',NULL,'1.9.4'),('20091001-1026','rcrichton','liquibase-update-to-latest.xml','2012-09-17 11:29:13','56da622349984de2d9d35dfe4f8c592b','Add Column','add date_retired column to relationship_type table',NULL,'1.9.4'),('20091001-1027','rcrichton','liquibase-update-to-latest.xml','2012-09-17 11:29:13','5c3441c4d4df1305e22a76a58b9e4','Add Column','add retire_reason column to relationship_type table',NULL,'1.9.4'),('200910271049-1','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','fe74f276e13dc72ddac24b3d5bfe7b73','Update Data (x5)','Setting core field types to have standard UUIDs',NULL,'1.9.4'),('200910271049-10','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','4990b358a988f5eb594e95f205e66','Update Data (x4)','Setting core roles to have standard UUIDs',NULL,'1.9.4'),('200910271049-2','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','537a62703f986d8a2db83317a3ac17bb','Update Data (x7)','Setting core person attribute types to have standard UUIDs',NULL,'1.9.4'),('200910271049-3','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','c2d6cd47499de2eaaad2bad5c11f9e1','Update Data (x4)','Setting core encounter types to have standard UUIDs',NULL,'1.9.4'),('200910271049-4','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','cbf2dfb5f6fec73a9efe9a591803a9c','Update Data (x12)','Setting core concept datatypes to have standard UUIDs',NULL,'1.9.4'),('200910271049-5','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','5c2629fbf1258fbb9bcd46e8327ee142','Update Data (x4)','Setting core relationship types to have standard UUIDs',NULL,'1.9.4'),('200910271049-6','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','c0dade1ffd2723e8441b6145f83346','Update Data (x15)','Setting core concept classes to have standard UUIDs',NULL,'1.9.4'),('200910271049-7','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','e768dab8025d6612bb581c86d95638a','Update Data (x2)','Setting core patient identifier types to have standard UUIDs',NULL,'1.9.4'),('200910271049-8','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','7d43ef855a73b2321b4fdb97190f7cb','Update Data','Setting core location to have standard UUIDs',NULL,'1.9.4'),('200910271049-9','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:06','9d61613fa6c3ad43f79f765d5a73e9','Update Data','Setting core hl7 source to have standard UUIDs',NULL,'1.9.4'),('200912031842','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:06','de4dd06c279ad749cb5f1de3ed45586','Drop Foreign Key Constraint, Add Foreign Key Constraint','Changing encounter.provider_id to reference person instead of users',NULL,'1.9.4'),('200912031846-1','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:09','3aff267520eec4d52e81ef7614b2da3','Add Column, Update Data','Adding person_id column to users table (if needed)',NULL,'1.9.4'),('200912031846-2','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:09','334f8c5891f058959155f2ed31da386b','Update Data, Add Not-Null Constraint','Populating users.person_id',NULL,'1.9.4'),('200912031846-3','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','f0ebbfbf2d223694de49da385ee203b','Add Foreign Key Constraint, Set Column as Auto-Increment','Restoring foreign key constraint on users.person_id',NULL,'1.9.4'),('200912071501-1','arthurs','liquibase-update-to-latest.xml','2012-09-17 11:29:06','38fc7314599265a8ac635daa063bc32','Update Data','Change name for patient.searchMaxResults global property to person.searchMaxResults',NULL,'1.9.4'),('200912091819','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','48846a9c134bd1da5b3234b4e1e041eb','Add Column, Add Foreign Key Constraint','Adding retired metadata columns to users table',NULL,'1.9.4'),('200912091820','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','3b8f7d29adb4b8ffdb9b4cd29877af1','Update Data','Migrating voided metadata to retired metadata for users table',NULL,'1.9.4'),('200912091821','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','fe9bd963d283aca8d01329a1b3595af1','Drop Foreign Key Constraint, Drop Column (x4)','Dropping voided metadata columns from users table',NULL,'1.9.4'),('200912140038','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','b2dc51da6fdb4a6d25763dccbd795dc0','Add Column','Adding \"uuid\" column to users table',NULL,'1.9.4'),('200912140039','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','28215abe10dd981bcd46c9d74e4cefe','Update Data','Generating UUIDs for all rows in users table via built in uuid function.',NULL,'1.9.4'),('200912140040','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','e45561523e219fe6c21194e37b9a9de6','Custom Change','Adding UUIDs to users table via a java class. (This will take a long time on large databases)',NULL,'1.9.4'),('200912141552','madanmohan','liquibase-update-to-latest.xml','2012-09-17 11:29:06','8e2978ddf67343ed80a18f7e92799bc','Add Column, Add Foreign Key Constraint','Add changed_by column to encounter table',NULL,'1.9.4'),('200912141553','madanmohan','liquibase-update-to-latest.xml','2012-09-17 11:29:06','c3f6cfc84f19785b1d7ecdd2ccf492','Add Column','Add date_changed column to encounter table',NULL,'1.9.4'),('20091215-0208','sunbiz','liquibase-update-to-latest.xml','2012-09-17 11:29:13','da9945f3554e8dd667c4790276eb5ad','Custom SQL','Prune concepts rows orphaned in concept_numeric tables',NULL,'1.9.4'),('20091215-0209','jmiranda','liquibase-update-to-latest.xml','2012-09-17 11:29:13','2feb74f1ca3fc1f8bb9b7986c321feed','Custom SQL','Prune concepts rows orphaned in concept_complex tables',NULL,'1.9.4'),('20091215-0210','jmiranda','liquibase-update-to-latest.xml','2012-09-17 11:29:13','15cd4ca5b736a89f932456fc17494293','Custom SQL','Prune concepts rows orphaned in concept_derived tables',NULL,'1.9.4'),('200912151032','n.nehete','liquibase-update-to-latest.xml','2012-09-17 11:29:11','fb31da7c25d166f7564bd77926dd286','Add Not-Null Constraint','Encounter Type should not be null when saving an Encounter',NULL,'1.9.4'),('200912211118','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:29:06','2bb03ed55ad1af379452fd18d9b46f74','Add Column','Adding language column to concept_derived table',NULL,'1.9.4'),('201001072007','upul','liquibase-update-to-latest.xml','2012-09-17 11:29:11','afee6c716d0435895c0c98a98099e4','Add Column','Add last execution time column to scheduler_task_config table',NULL,'1.9.4'),('20100128-1','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:28:49','5bf382cc41fbfa92bb13abdadba3e15','Insert Row','Adding \'System Developer\' role again (see ticket #1499)',NULL,'1.9.4'),('20100128-2','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:28:49','b351df1ec993c3906d66ebe5db67d120','Update Data','Switching users back from \'Administrator\' to \'System Developer\' (see ticket #1499)',NULL,'1.9.4'),('20100128-3','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:28:49','6e35b6f3bf99975e6bc5ad988280ef','Delete Data','Deleting \'Administrator\' role (see ticket #1499)',NULL,'1.9.4'),('20100306-095513a','thilini.hg','liquibase-update-to-latest.xml','2012-09-17 11:29:16','2adf32b4543c0bc7c99a9528da09afe','Drop Foreign Key Constraint','Dropping unused foreign key from notification alert table',NULL,'1.9.4'),('20100306-095513b','thilini.hg','liquibase-update-to-latest.xml','2012-09-17 11:29:16','fd3940c7dfe3195055dd6be9381e8863','Drop Column','Dropping unused user_id column from notification alert table',NULL,'1.9.4'),('20100322-0908','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:16','1e3b87e49b62a358b5289ce470ea0dc','Add Column, Update Data','Adding sort_weight column to concept_answers table and initially sets the sort_weight to the concept_answer_id',NULL,'1.9.4'),('20100323-192043','ricardosbarbosa','liquibase-update-to-latest.xml','2012-09-17 11:29:16','97bc5f19e765d66665f5478147716aa6','Update Data, Delete Data (x2)','Removing the duplicate privilege \'Add Concept Proposal\' in favor of \'Add Concept Proposals\'',NULL,'1.9.4'),('20100330-190413','ricardosbarbosa','liquibase-update-to-latest.xml','2012-09-17 11:29:16','2b508e1e4546813dcc3425e09e863be','Update Data, Delete Data (x2)','Removing the duplicate privilege \'Edit Concept Proposal\' in favor of \'Edit Concept Proposals\'',NULL,'1.9.4'),('20100412-2217','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:16','6ccd335bb8f1e3dfe5c9b62d018fb27','Add Column','Adding \"uuid\" column to notification_alert_recipient table',NULL,'1.9.4'),('20100412-2218','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:16','b04eb51018a426202057341c4430d851','Update Data','Generating UUIDs for all rows in notification_alert_recipient table via built in uuid function.',NULL,'1.9.4'),('20100412-2219','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:16','13e8f84e2a3ac2eb060e68376dbc19','Custom Change','Adding UUIDs to notification_alert_recipient table via a java class (if needed).',NULL,'1.9.4'),('20100412-2220','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:16','4df07eb79086636f8498cac5a168be2','Add Not-Null Constraint','Now that UUID generation is done, set the notification_alert_recipient.uuid column to not \"NOT NULL\"',NULL,'1.9.4'),('20100413-1509','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:16','f27227d57b9372076542621df1b23','Rename Column','Change location_tag.tag to location_tag.name',NULL,'1.9.4'),('20100415-forgotten-from-before','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:11','48ac37f91a13c53c558923d11c9cee','Add Not-Null Constraint','Adding not null constraint to users.uuid',NULL,'1.9.4'),('20100423-1402','slorenz','liquibase-update-to-latest.xml','2012-09-17 11:29:16','4a1427b91614d7b982182c47b347c834','Create Index','Add an index to the encounter.encounter_datetime column to speed up statistical\n			analysis.',NULL,'1.9.4'),('20100423-1406','slorenz','liquibase-update-to-latest.xml','2012-09-17 11:29:16','1df152ca6d1cace9092a2b04f4d3bd3','Create Index','Add an index to the obs.obs_datetime column to speed up statistical analysis.',NULL,'1.9.4'),('20100426-1947','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:16','873ce8452d8fffff16df292347215ce8','Insert Row','Adding daemon user to users table',NULL,'1.9.4'),('20100427-1334','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:16','4a22228b56866b7412bf56bf53d4876','Modify Column','Changing the datatype of global_property.property for mysql databases',NULL,'1.9.4'),('20100512-1400','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:16','c068ace9c7909db24b354c7db191a3b3','Insert Row','Create core order_type for drug orders',NULL,'1.9.4'),('20100513-1947','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:16','bd3c85b35c0c189d17a4edfb69479a','Delete Data (x2)','Removing scheduler.username and scheduler.password global properties',NULL,'1.9.4'),('20100517-1545','wyclif and djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:16','a4a006cfd7d2f3777c35a47c729a','Custom Change','Switch boolean concepts/observations to be stored as coded',NULL,'1.9.4'),('20100525-818-1','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:18','ae19ea64c1936cc692aab61c895d6cf','Create Table, Add Foreign Key Constraint (x2)','Create active list type table.',NULL,'1.9.4'),('20100525-818-2','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:21','681391d216a593f59201dfe225d7288','Create Table, Add Foreign Key Constraint (x7)','Create active list table',NULL,'1.9.4'),('20100525-818-3','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:23','85178ccb515c9892c5cf4826c2778f73','Create Table, Add Foreign Key Constraint','Create allergen table',NULL,'1.9.4'),('20100525-818-4','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:25','11996601ce66087a69ce6fba3938c63','Create Table','Create problem table',NULL,'1.9.4'),('20100525-818-5','syhaas','liquibase-update-to-latest.xml','2012-09-17 11:29:25','f67090e0c159e3c39418cb5d3ae3e82','Insert Row (x2)','Inserting default active list types',NULL,'1.9.4'),('20100526-1025','Harsha.cse','liquibase-update-to-latest.xml','2012-09-17 11:29:16','16f132945063bedfb4288b9f934f656e','Drop Not-Null Constraint (x2)','Drop Not-Null constraint from location column in Encounter and Obs table',NULL,'1.9.4'),('20100604-0933a','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:16','b61fce30223b9b7cc2766f60b3abfa49','Add Default Value','Changing the default value to 2 for \'message_state\' column in \'hl7_in_archive\' table',NULL,'1.9.4'),('20100604-0933b','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:16','45a4732916ca8fd35d3df9e320b2a333','Update Data','Converting 0 and 1 to 2 for \'message_state\' column in \'hl7_in_archive\' table',NULL,'1.9.4'),('20100607-1550a','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:25','d7635997e5f8fb833c733f7597ed203b','Add Column','Adding \'concept_name_type\' column to concept_name table',NULL,'1.9.4'),('20100607-1550b','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:25','4b3d935771a1703eeaf268dfbbb16da8','Add Column','Adding \'locale_preferred\' column to concept_name table',NULL,'1.9.4'),('20100607-1550c','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:27','f151486ccbf8cec7d2327d4cd674b9','Drop Foreign Key Constraint','Dropping foreign key constraint on concept_name_tag_map.concept_name_tag_id',NULL,'1.9.4'),('20100607-1550d','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:27','f9567368a5999517843d7b3f1fd03bce','Update Data, Delete Data (x2)','Setting the concept name type for short names',NULL,'1.9.4'),('20100607-1550e','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:27','e7ba70b44259f915524358042f5996','Update Data, Delete Data (x2)','Converting preferred names to FULLY_SPECIFIED names',NULL,'1.9.4'),('20100607-1550f','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:27','5ac0213d1b2a2d50d5a0e5e5e45eb10','Update Data, Delete Data (x2)','Converting concept names with country specific concept name tags to preferred names',NULL,'1.9.4'),('20100607-1550g','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:27','69da22ab528eb6dbc68c7eaaafa898','Delete Data (x2)','Deleting \'default\' and \'synonym\' concept name tags',NULL,'1.9.4'),('20100607-1550h','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:27','dadd4e593c3ed4ec37119a933c7e81c','Custom Change','Validating and attempting to fix invalid concepts and ConceptNames',NULL,'1.9.4'),('20100607-1550i','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:30','3133835f91f89b1d1b383ec21dad78fa','Add Foreign Key Constraint','Restoring foreign key constraint on concept_name_tag_map.concept_name_tag_id',NULL,'1.9.4'),('20100621-1443','jkeiper','liquibase-update-to-latest.xml','2012-09-17 11:29:30','5fc85cf41532f6ec8658f25954ee7d2d','Modify Column','Modify the error_details column of hl7_in_error to hold\n			stacktraces',NULL,'1.9.4'),('201008021047','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:30','89abf79d12ba23f8388a78f1f12bf2','Create Index','Add an index to the person_name.family_name2 to speed up patient and person searches',NULL,'1.9.4'),('201008201345','mseaton','liquibase-update-to-latest.xml','2012-09-17 11:29:30','7167c287f456287faab9b382c0caecc6','Custom Change','Validates Program Workflow States for possible configuration problems and reports warnings',NULL,'1.9.4'),('201008242121','misha680','liquibase-update-to-latest.xml','2012-09-17 11:29:30','4a3e4ae561a3e9a1b1131dcdbad2b313','Modify Column','Make person_name.person_id not NULLable',NULL,'1.9.4'),('20100924-1110','mseaton','liquibase-update-to-latest.xml','2012-09-17 11:29:30','deb3d45e5d143178be45776152517d3e','Add Column, Add Foreign Key Constraint','Add location_id column to patient_program table',NULL,'1.9.4'),('201009281047','misha680','liquibase-update-to-latest.xml','2012-09-17 11:29:30','fa4e5cc66ba97ec1d9ad37156591ff','Drop Column','Remove the now unused default_charge column',NULL,'1.9.4'),('201010051745','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:30','c401e7afbba90c8db5a422f994cb8d0','Update Data','Setting the global property \'patient.identifierRegex\' to an empty string',NULL,'1.9.4'),('201010051746','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:30','4535b642c1591f7114c3e02d9d7a37','Update Data','Setting the global property \'patient.identifierSuffix\' to an empty string',NULL,'1.9.4'),('201010151054','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:30','eb939ccae537abf05cfdb05c43b675e7','Create Index','Adding index to form.published column',NULL,'1.9.4'),('201010151055','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:30','861d3c98ab90a3c9a197343fce7ee11','Create Index','Adding index to form.retired column',NULL,'1.9.4'),('201010151056','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:30','51346ab9b4b6780c2c8ff8dc193b18f','Create Index','Adding multi column index on form.published and form.retired columns',NULL,'1.9.4'),('201010261143','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','b2fcb0991f6dfc4f79ee53e94798c2','Rename Column','Rename neighborhood_cell column to address3 and increase the size to 255 characters',NULL,'1.9.4'),('201010261145','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','6519b246e2343453ec336836c65d1','Rename Column','Rename township_division column to address4 and increase the size to 255 characters',NULL,'1.9.4'),('201010261147','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','3a39cf4d967d8670bd22c596d34859','Rename Column','Rename subregion column to address5 and increase the size to 255 characters',NULL,'1.9.4'),('201010261149','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','1a80befba2d24244ef24a171562ac82','Rename Column','Rename region column to address6 and increase the size to 255 characters',NULL,'1.9.4'),('201010261151','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','db73f1b9acdb7af277badc584754df','Rename Column','Rename neighborhood_cell column to address3 and increase the size to 255 characters',NULL,'1.9.4'),('201010261153','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','1c22a65ce145618429c7b57d9ee294b','Rename Column','Rename township_division column to address4 and increase the size to 255 characters',NULL,'1.9.4'),('201010261156','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','194dac315b5cb6964177d121659dd1d9','Rename Column','Rename subregion column to address5 and increase the size to 255 characters',NULL,'1.9.4'),('201010261159','crecabarren','liquibase-update-to-latest.xml','2012-09-17 11:29:30','db75c1bc82161f47b7831296871cb7','Rename Column','Rename region column to address6 and increase the size to 255 characters',NULL,'1.9.4'),('201011011600','jkeiper','liquibase-update-to-latest.xml','2012-09-17 11:29:30','c7c0354954e8e131a0e2f0aa692725b8','Create Index','Adding index to message_state column in HL7 archive table',NULL,'1.9.4'),('201011011605','jkeiper','liquibase-update-to-latest.xml','2012-09-17 11:29:30','15296bed5f5b94a941bf273ae6ecd3a','Custom Change','Moving \"deleted\" HL7s from HL7 archive table to queue table',NULL,'1.9.4'),('201012081716','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:29:33','eecf7bd9435b6f1b4a08318e22e96','Delete Data','Removing concept that are concept derived and the datatype',NULL,'1.9.4'),('201012081717','nribeka','liquibase-update-to-latest.xml','2012-09-17 11:29:35','842f7086de77f6b31d8f06d56cf56','Drop Table','Removing concept derived tables',NULL,'1.9.4'),('20101209-1721','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:30','6519d6c9a8c6f2f31c4a8784ac9c42a','Add Column','Add \'weight\' column to concept_word table',NULL,'1.9.4'),('20101209-1722','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:30','f8ea5691d93b71f1e9b0b3b823ac2f9a','Create Index','Adding index to concept_word.weight column',NULL,'1.9.4'),('20101209-1723','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:30','4d67c48557f5665a8c2be7e87e22913','Insert Row','Insert a row into the schedule_task_config table for the ConceptIndexUpdateTask',NULL,'1.9.4'),('20101209-1731','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:30','40dc15b89fa71ae2559d02d5a6b82f','Update Data','Setting the value of \'start_on_startup\' to trigger off conceptIndexUpdateTask on startup',NULL,'1.9.4'),('201012092009','djazayeri','liquibase-update-to-latest.xml','2012-09-17 11:29:30','435261f5d18756dba65d55f42528a4','Modify Column (x10)','Increasing length of address fields in person_address and location to 255',NULL,'1.9.4'),('201102280948','bwolfe','liquibase-update-to-latest.xml','2012-09-17 11:29:09','7248f6f988e3a1b5fd864a433b61aae','Drop Foreign Key Constraint','Removing the foreign key from users.user_id to person.person_id if it still exists',NULL,'1.9.4'),('20110329-2317','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:37','1cf77131c583d513d7513b604b8f3940','Delete Data','Removing \'View Encounters\' privilege from Anonymous user',NULL,'1.9.4'),('20110329-2318','wyclif','liquibase-update-to-latest.xml','2012-09-17 11:29:40','ca9b59cdedd44918453315a89962579e','Delete Data','Removing \'View Observations\' privilege from Anonymous user',NULL,'1.9.4'),('disable-foreign-key-checks','ben','liquibase-core-data.xml','2012-09-17 11:28:22','a5f57b3b22b63b75f613458ff51746e2','Custom SQL','',NULL,'1.9.4');
/*!40000 ALTER TABLE `liquibasechangelog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquibasechangeloglock`
--

DROP TABLE IF EXISTS `liquibasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liquibasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquibasechangeloglock`
--

LOCK TABLES `liquibasechangeloglock` WRITE;
/*!40000 ALTER TABLE `liquibasechangeloglock` DISABLE KEYS */;
INSERT INTO `liquibasechangeloglock` VALUES (1,0,NULL,NULL);
/*!40000 ALTER TABLE `liquibasechangeloglock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `location_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) DEFAULT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `city_village` varchar(255) DEFAULT NULL,
  `state_province` varchar(255) DEFAULT NULL,
  `postal_code` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `latitude` varchar(50) DEFAULT NULL,
  `longitude` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `county_district` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `address6` varchar(255) DEFAULT NULL,
  `address5` varchar(255) DEFAULT NULL,
  `address4` varchar(255) DEFAULT NULL,
  `retired` tinyint(4) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `parent_location` int(11) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `location_uuid_index` (`uuid`),
  KEY `name_of_location` (`name`),
  KEY `retired_status` (`retired`),
  KEY `user_who_created_location` (`creator`),
  KEY `user_who_retired_location` (`retired_by`),
  KEY `parent_location` (`parent_location`),
  CONSTRAINT `parent_location` FOREIGN KEY (`parent_location`) REFERENCES `location` (`location_id`),
  CONSTRAINT `user_who_created_location` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_location` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (1,'Unknown Location',NULL,'','','','','','',NULL,NULL,1,'2005-09-22 00:00:00',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'8d6c993e-c2cc-11de-8d13-0010c6dffd0f');
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location_tag`
--

DROP TABLE IF EXISTS `location_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_tag` (
  `location_tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`location_tag_id`),
  UNIQUE KEY `location_tag_uuid_index` (`uuid`),
  KEY `location_tag_creator` (`creator`),
  KEY `location_tag_retired_by` (`retired_by`),
  CONSTRAINT `location_tag_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `location_tag_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location_tag`
--

LOCK TABLES `location_tag` WRITE;
/*!40000 ALTER TABLE `location_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `location_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location_tag_map`
--

DROP TABLE IF EXISTS `location_tag_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_tag_map` (
  `location_id` int(11) NOT NULL,
  `location_tag_id` int(11) NOT NULL,
  PRIMARY KEY (`location_id`,`location_tag_id`),
  KEY `location_tag_map_tag` (`location_tag_id`),
  CONSTRAINT `location_tag_map_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `location_tag_map_tag` FOREIGN KEY (`location_tag_id`) REFERENCES `location_tag` (`location_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location_tag_map`
--

LOCK TABLES `location_tag_map` WRITE;
/*!40000 ALTER TABLE `location_tag_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `location_tag_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logic_rule_definition`
--

DROP TABLE IF EXISTS `logic_rule_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_rule_definition` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `rule_content` varchar(2048) NOT NULL,
  `language` varchar(255) NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `creator for rule_definition` (`creator`),
  KEY `changed_by for rule_definition` (`changed_by`),
  KEY `retired_by for rule_definition` (`retired_by`),
  CONSTRAINT `creator for rule_definition` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `changed_by for rule_definition` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `retired_by for rule_definition` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logic_rule_definition`
--

LOCK TABLES `logic_rule_definition` WRITE;
/*!40000 ALTER TABLE `logic_rule_definition` DISABLE KEYS */;
/*!40000 ALTER TABLE `logic_rule_definition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logic_rule_token`
--

DROP TABLE IF EXISTS `logic_rule_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_rule_token` (
  `logic_rule_token_id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `token` varchar(512) NOT NULL,
  `class_name` varchar(512) NOT NULL,
  `state` varchar(512) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`logic_rule_token_id`),
  UNIQUE KEY `logic_rule_token_uuid` (`uuid`),
  KEY `token_creator` (`creator`),
  KEY `token_changed_by` (`changed_by`),
  CONSTRAINT `token_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `person` (`person_id`),
  CONSTRAINT `token_creator` FOREIGN KEY (`creator`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logic_rule_token`
--

LOCK TABLES `logic_rule_token` WRITE;
/*!40000 ALTER TABLE `logic_rule_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `logic_rule_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logic_rule_token_tag`
--

DROP TABLE IF EXISTS `logic_rule_token_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_rule_token_tag` (
  `logic_rule_token_id` int(11) NOT NULL,
  `tag` varchar(512) NOT NULL,
  KEY `token_tag` (`logic_rule_token_id`),
  CONSTRAINT `token_tag` FOREIGN KEY (`logic_rule_token_id`) REFERENCES `logic_rule_token` (`logic_rule_token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logic_rule_token_tag`
--

LOCK TABLES `logic_rule_token_tag` WRITE;
/*!40000 ALTER TABLE `logic_rule_token_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `logic_rule_token_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logic_token_registration`
--

DROP TABLE IF EXISTS `logic_token_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_token_registration` (
  `token_registration_id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `token` varchar(512) NOT NULL,
  `provider_class_name` varchar(512) NOT NULL,
  `provider_token` varchar(512) NOT NULL,
  `configuration` varchar(2000) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`token_registration_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `token_registration_creator` (`creator`),
  KEY `token_registration_changed_by` (`changed_by`),
  CONSTRAINT `token_registration_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `token_registration_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logic_token_registration`
--

LOCK TABLES `logic_token_registration` WRITE;
/*!40000 ALTER TABLE `logic_token_registration` DISABLE KEYS */;
INSERT INTO `logic_token_registration` VALUES (1,2,'2012-09-17 11:30:17',NULL,NULL,'encounterLocation','org.openmrs.logic.datasource.EncounterDataSource','encounterLocation','encounterLocation','79b87637-488b-4460-a74e-878f9a362e8c'),(2,2,'2012-09-17 11:30:17',NULL,NULL,'encounterProvider','org.openmrs.logic.datasource.EncounterDataSource','encounterProvider','encounterProvider','e3f62c24-9073-4453-85d0-e18b55cea894'),(3,2,'2012-09-17 11:30:17',NULL,NULL,'encounter','org.openmrs.logic.datasource.EncounterDataSource','encounter','encounter','4ca84739-4e34-4c6d-ab21-d0d23aa3d53f'),(4,2,'2012-09-17 11:30:17',NULL,NULL,'identifier','org.openmrs.logic.datasource.PatientDataSource','identifier','identifier','457de528-b2d5-402d-a0e0-0af40355ae6c'),(5,2,'2012-09-17 11:30:17',NULL,NULL,'family name','org.openmrs.logic.datasource.PersonDataSource','family name','family name','3c83090d-a0e9-4857-985e-a5c2558bca53'),(6,2,'2012-09-17 11:30:17',NULL,NULL,'middle name','org.openmrs.logic.datasource.PersonDataSource','middle name','middle name','5dd47eea-90f2-44f6-bf02-0d950f3dbe35'),(7,2,'2012-09-17 11:30:17',NULL,NULL,'death date','org.openmrs.logic.datasource.PersonDataSource','death date','death date','f9133442-15c1-44cb-83fb-3341fcf3bdf1'),(8,2,'2012-09-17 11:30:17',NULL,NULL,'birthdate','org.openmrs.logic.datasource.PersonDataSource','birthdate','birthdate','5b199abe-a7b3-4f33-8872-adb06c54b53c'),(9,2,'2012-09-17 11:30:17',NULL,NULL,'cause of death','org.openmrs.logic.datasource.PersonDataSource','cause of death','cause of death','d4ac3af5-028e-42d8-afd3-08c1451199ce'),(10,2,'2012-09-17 11:30:17',NULL,NULL,'birthdate estimated','org.openmrs.logic.datasource.PersonDataSource','birthdate estimated','birthdate estimated','7eb93f21-3a64-48f5-bebf-a674ae6ae6a1'),(11,2,'2012-09-17 11:30:17',NULL,NULL,'gender','org.openmrs.logic.datasource.PersonDataSource','gender','gender','a280822c-5354-40d8-986c-a09f35119611'),(12,2,'2012-09-17 11:30:17',NULL,NULL,'family name2','org.openmrs.logic.datasource.PersonDataSource','family name2','family name2','dfb3d7a2-fa09-41bf-bc11-b88378046d95'),(13,2,'2012-09-17 11:30:17',NULL,NULL,'dead','org.openmrs.logic.datasource.PersonDataSource','dead','dead','2aa7741d-8829-4ca2-be96-f536a1d6d8a1'),(14,2,'2012-09-17 11:30:17',NULL,NULL,'given name','org.openmrs.logic.datasource.PersonDataSource','given name','given name','e3bfb003-6d9f-4c04-9dd4-996a6ea8e0d4'),(15,2,'2012-09-17 11:30:17',NULL,NULL,'CURRENT STATE','org.openmrs.logic.datasource.ProgramDataSource','CURRENT STATE','CURRENT STATE','a1d700b6-460a-49e7-b13c-5b89e65a8732'),(16,2,'2012-09-17 11:30:17',NULL,NULL,'PROGRAM ENROLLMENT','org.openmrs.logic.datasource.ProgramDataSource','PROGRAM ENROLLMENT','PROGRAM ENROLLMENT','248e7791-ce03-4514-928e-d1caa520da74'),(17,2,'2012-09-17 11:30:17',NULL,NULL,'PROGRAM COMPLETION','org.openmrs.logic.datasource.ProgramDataSource','PROGRAM COMPLETION','PROGRAM COMPLETION','bf22a585-5846-4593-abc0-3f80ce41adad');
/*!40000 ALTER TABLE `logic_token_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logic_token_registration_tag`
--

DROP TABLE IF EXISTS `logic_token_registration_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_token_registration_tag` (
  `token_registration_id` int(11) NOT NULL,
  `tag` varchar(512) NOT NULL,
  KEY `token_registration_tag` (`token_registration_id`),
  CONSTRAINT `token_registration_tag` FOREIGN KEY (`token_registration_id`) REFERENCES `logic_token_registration` (`token_registration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logic_token_registration_tag`
--

LOCK TABLES `logic_token_registration_tag` WRITE;
/*!40000 ALTER TABLE `logic_token_registration_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `logic_token_registration_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note`
--

DROP TABLE IF EXISTS `note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `note` (
  `note_id` int(11) NOT NULL DEFAULT '0',
  `note_type` varchar(50) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `obs_id` int(11) DEFAULT NULL,
  `encounter_id` int(11) DEFAULT NULL,
  `text` text NOT NULL,
  `priority` int(11) DEFAULT NULL,
  `parent` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`note_id`),
  UNIQUE KEY `note_uuid_index` (`uuid`),
  KEY `user_who_changed_note` (`changed_by`),
  KEY `user_who_created_note` (`creator`),
  KEY `encounter_note` (`encounter_id`),
  KEY `obs_note` (`obs_id`),
  KEY `note_hierarchy` (`parent`),
  KEY `patient_note` (`patient_id`),
  CONSTRAINT `encounter_note` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `note_hierarchy` FOREIGN KEY (`parent`) REFERENCES `note` (`note_id`),
  CONSTRAINT `obs_note` FOREIGN KEY (`obs_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `patient_note` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `user_who_changed_note` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_note` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note`
--

LOCK TABLES `note` WRITE;
/*!40000 ALTER TABLE `note` DISABLE KEYS */;
/*!40000 ALTER TABLE `note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_alert`
--

DROP TABLE IF EXISTS `notification_alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_alert` (
  `alert_id` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(512) NOT NULL,
  `satisfied_by_any` int(11) NOT NULL DEFAULT '0',
  `alert_read` int(11) NOT NULL DEFAULT '0',
  `date_to_expire` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`alert_id`),
  UNIQUE KEY `notification_alert_uuid_index` (`uuid`),
  KEY `user_who_changed_alert` (`changed_by`),
  KEY `alert_creator` (`creator`),
  CONSTRAINT `alert_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_alert` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_alert`
--

LOCK TABLES `notification_alert` WRITE;
/*!40000 ALTER TABLE `notification_alert` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_alert` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_alert_recipient`
--

DROP TABLE IF EXISTS `notification_alert_recipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_alert_recipient` (
  `alert_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `alert_read` int(11) NOT NULL DEFAULT '0',
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`alert_id`,`user_id`),
  KEY `alert_read_by_user` (`user_id`),
  CONSTRAINT `alert_read_by_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `id_of_alert` FOREIGN KEY (`alert_id`) REFERENCES `notification_alert` (`alert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_alert_recipient`
--

LOCK TABLES `notification_alert_recipient` WRITE;
/*!40000 ALTER TABLE `notification_alert_recipient` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_alert_recipient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_template`
--

DROP TABLE IF EXISTS `notification_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_template` (
  `template_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `template` text,
  `subject` varchar(100) DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  `recipients` varchar(512) DEFAULT NULL,
  `ordinal` int(11) DEFAULT '0',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `notification_template_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_template`
--

LOCK TABLES `notification_template` WRITE;
/*!40000 ALTER TABLE `notification_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `obs`
--

DROP TABLE IF EXISTS `obs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `obs` (
  `obs_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) NOT NULL,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `encounter_id` int(11) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `obs_datetime` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `location_id` int(11) DEFAULT NULL,
  `obs_group_id` int(11) DEFAULT NULL,
  `accession_number` varchar(255) DEFAULT NULL,
  `value_group_id` int(11) DEFAULT NULL,
  `value_boolean` tinyint(4) DEFAULT NULL,
  `value_coded` int(11) DEFAULT NULL,
  `value_coded_name_id` int(11) DEFAULT NULL,
  `value_drug` int(11) DEFAULT NULL,
  `value_datetime` datetime DEFAULT NULL,
  `value_numeric` double DEFAULT NULL,
  `value_modifier` varchar(2) DEFAULT NULL,
  `value_text` text,
  `date_started` datetime DEFAULT NULL,
  `date_stopped` datetime DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `value_complex` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`obs_id`),
  UNIQUE KEY `obs_uuid_index` (`uuid`),
  KEY `obs_concept` (`concept_id`),
  KEY `obs_enterer` (`creator`),
  KEY `encounter_observations` (`encounter_id`),
  KEY `obs_location` (`location_id`),
  KEY `obs_grouping_id` (`obs_group_id`),
  KEY `obs_order` (`order_id`),
  KEY `person_obs` (`person_id`),
  KEY `answer_concept` (`value_coded`),
  KEY `obs_name_of_coded_value` (`value_coded_name_id`),
  KEY `answer_concept_drug` (`value_drug`),
  KEY `user_who_voided_obs` (`voided_by`),
  KEY `obs_datetime_idx` (`obs_datetime`),
  CONSTRAINT `answer_concept` FOREIGN KEY (`value_coded`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `answer_concept_drug` FOREIGN KEY (`value_drug`) REFERENCES `drug` (`drug_id`),
  CONSTRAINT `encounter_observations` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `obs_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `obs_enterer` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `obs_grouping_id` FOREIGN KEY (`obs_group_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `obs_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `obs_name_of_coded_value` FOREIGN KEY (`value_coded_name_id`) REFERENCES `concept_name` (`concept_name_id`),
  CONSTRAINT `obs_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `person_obs` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `user_who_voided_obs` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `obs`
--

LOCK TABLES `obs` WRITE;
/*!40000 ALTER TABLE `obs` DISABLE KEYS */;
/*!40000 ALTER TABLE `obs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_type`
--

DROP TABLE IF EXISTS `order_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_type` (
  `order_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`order_type_id`),
  UNIQUE KEY `order_type_uuid_index` (`uuid`),
  KEY `retired_status` (`retired`),
  KEY `type_created_by` (`creator`),
  KEY `user_who_retired_order_type` (`retired_by`),
  CONSTRAINT `type_created_by` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_order_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_type`
--

LOCK TABLES `order_type` WRITE;
/*!40000 ALTER TABLE `order_type` DISABLE KEYS */;
INSERT INTO `order_type` VALUES (2,'Drug Order','An order for a medication to be given to the patient',1,'2010-05-12 00:00:00',0,NULL,NULL,NULL,'131168f4-15f5-102d-96e4-000c29c2a5d7');
/*!40000 ALTER TABLE `order_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_type_id` int(11) NOT NULL DEFAULT '0',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `orderer` int(11) DEFAULT '0',
  `encounter_id` int(11) DEFAULT NULL,
  `instructions` text,
  `start_date` datetime DEFAULT NULL,
  `auto_expire_date` datetime DEFAULT NULL,
  `discontinued` smallint(6) NOT NULL DEFAULT '0',
  `discontinued_date` datetime DEFAULT NULL,
  `discontinued_by` int(11) DEFAULT NULL,
  `discontinued_reason` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `accession_number` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `orders_uuid_index` (`uuid`),
  KEY `order_creator` (`creator`),
  KEY `user_who_discontinued_order` (`discontinued_by`),
  KEY `discontinued_because` (`discontinued_reason`),
  KEY `orders_in_encounter` (`encounter_id`),
  KEY `type_of_order` (`order_type_id`),
  KEY `orderer_not_drug` (`orderer`),
  KEY `order_for_patient` (`patient_id`),
  KEY `user_who_voided_order` (`voided_by`),
  CONSTRAINT `discontinued_because` FOREIGN KEY (`discontinued_reason`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `orderer_not_drug` FOREIGN KEY (`orderer`) REFERENCES `users` (`user_id`),
  CONSTRAINT `orders_in_encounter` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `order_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_for_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `type_of_order` FOREIGN KEY (`order_type_id`) REFERENCES `order_type` (`order_type_id`),
  CONSTRAINT `user_who_discontinued_order` FOREIGN KEY (`discontinued_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_order` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
  `patient_id` int(11) NOT NULL AUTO_INCREMENT,
  `tribe` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`patient_id`),
  KEY `user_who_changed_pat` (`changed_by`),
  KEY `user_who_created_patient` (`creator`),
  KEY `belongs_to_tribe` (`tribe`),
  KEY `user_who_voided_patient` (`voided_by`),
  CONSTRAINT `belongs_to_tribe` FOREIGN KEY (`tribe`) REFERENCES `tribe` (`tribe_id`),
  CONSTRAINT `person_id_for_patient` FOREIGN KEY (`patient_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `user_who_changed_pat` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_patient` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_patient` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_identifier`
--

DROP TABLE IF EXISTS `patient_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_identifier` (
  `patient_identifier_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) NOT NULL DEFAULT '0',
  `identifier` varchar(50) NOT NULL DEFAULT '',
  `identifier_type` int(11) NOT NULL DEFAULT '0',
  `preferred` smallint(6) NOT NULL DEFAULT '0',
  `location_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`patient_identifier_id`),
  UNIQUE KEY `patient_identifier_uuid_index` (`uuid`),
  KEY `identifier_name` (`identifier`),
  KEY `identifier_creator` (`creator`),
  KEY `defines_identifier_type` (`identifier_type`),
  KEY `patient_identifier_ibfk_2` (`location_id`),
  KEY `identifier_voider` (`voided_by`),
  KEY `idx_patient_identifier_patient` (`patient_id`),
  CONSTRAINT `identifies_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `defines_identifier_type` FOREIGN KEY (`identifier_type`) REFERENCES `patient_identifier_type` (`patient_identifier_type_id`),
  CONSTRAINT `identifier_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `identifier_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_identifier_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_identifier`
--

LOCK TABLES `patient_identifier` WRITE;
/*!40000 ALTER TABLE `patient_identifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_identifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_identifier_type`
--

DROP TABLE IF EXISTS `patient_identifier_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_identifier_type` (
  `patient_identifier_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `format` varchar(50) DEFAULT NULL,
  `check_digit` smallint(6) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `required` smallint(6) NOT NULL DEFAULT '0',
  `format_description` varchar(255) DEFAULT NULL,
  `validator` varchar(200) DEFAULT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`patient_identifier_type_id`),
  UNIQUE KEY `patient_identifier_type_uuid_index` (`uuid`),
  KEY `retired_status` (`retired`),
  KEY `type_creator` (`creator`),
  KEY `user_who_retired_patient_identifier_type` (`retired_by`),
  CONSTRAINT `type_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_patient_identifier_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_identifier_type`
--

LOCK TABLES `patient_identifier_type` WRITE;
/*!40000 ALTER TABLE `patient_identifier_type` DISABLE KEYS */;
INSERT INTO `patient_identifier_type` VALUES (1,'OpenMRS Identification Number','Unique number used in OpenMRS','',1,1,'2005-09-22 00:00:00',0,NULL,'org.openmrs.patient.impl.LuhnIdentifierValidator',0,NULL,NULL,NULL,'8d793bee-c2cc-11de-8d13-0010c6dffd0f'),(2,'Old Identification Number','Number given out prior to the OpenMRS system (No check digit)','',0,1,'2005-09-22 00:00:00',0,NULL,NULL,0,NULL,NULL,NULL,'8d79403a-c2cc-11de-8d13-0010c6dffd0f'),(3,'MoTeCH Id','MoTeCH Id','',0,1,'2012-09-17 11:32:04',0,'','',0,NULL,NULL,NULL,'c75dbf6f-876f-49fd-9368-cc79fc8bbab9');
/*!40000 ALTER TABLE `patient_identifier_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_program`
--

DROP TABLE IF EXISTS `patient_program`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_program` (
  `patient_program_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) NOT NULL DEFAULT '0',
  `program_id` int(11) NOT NULL DEFAULT '0',
  `date_enrolled` datetime DEFAULT NULL,
  `date_completed` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `location_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`patient_program_id`),
  UNIQUE KEY `patient_program_uuid_index` (`uuid`),
  KEY `user_who_changed` (`changed_by`),
  KEY `patient_program_creator` (`creator`),
  KEY `patient_in_program` (`patient_id`),
  KEY `program_for_patient` (`program_id`),
  KEY `user_who_voided_patient_program` (`voided_by`),
  KEY `patient_program_location_id` (`location_id`),
  CONSTRAINT `patient_program_location_id` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `patient_in_program` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `patient_program_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `program_for_patient` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`),
  CONSTRAINT `user_who_changed` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_patient_program` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_program`
--

LOCK TABLES `patient_program` WRITE;
/*!40000 ALTER TABLE `patient_program` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_program` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_state`
--

DROP TABLE IF EXISTS `patient_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_state` (
  `patient_state_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_program_id` int(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`patient_state_id`),
  UNIQUE KEY `patient_state_uuid_index` (`uuid`),
  KEY `patient_state_changer` (`changed_by`),
  KEY `patient_state_creator` (`creator`),
  KEY `patient_program_for_state` (`patient_program_id`),
  KEY `state_for_patient` (`state`),
  KEY `patient_state_voider` (`voided_by`),
  CONSTRAINT `patient_program_for_state` FOREIGN KEY (`patient_program_id`) REFERENCES `patient_program` (`patient_program_id`),
  CONSTRAINT `patient_state_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_state_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_state_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `state_for_patient` FOREIGN KEY (`state`) REFERENCES `program_workflow_state` (`program_workflow_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_state`
--

LOCK TABLES `patient_state` WRITE;
/*!40000 ALTER TABLE `patient_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `person_id` int(11) NOT NULL AUTO_INCREMENT,
  `gender` varchar(50) DEFAULT '',
  `birthdate` date DEFAULT NULL,
  `birthdate_estimated` smallint(6) NOT NULL DEFAULT '0',
  `dead` smallint(6) NOT NULL DEFAULT '0',
  `death_date` datetime DEFAULT NULL,
  `cause_of_death` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`person_id`),
  UNIQUE KEY `person_uuid_index` (`uuid`),
  KEY `person_birthdate` (`birthdate`),
  KEY `person_death_date` (`death_date`),
  KEY `person_died_because` (`cause_of_death`),
  KEY `user_who_changed_person` (`changed_by`),
  KEY `user_who_created_person` (`creator`),
  KEY `user_who_voided_person` (`voided_by`),
  CONSTRAINT `person_died_because` FOREIGN KEY (`cause_of_death`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_changed_person` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_person` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_person` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (1,'',NULL,0,0,NULL,NULL,1,'2005-01-01 00:00:00',NULL,NULL,0,NULL,NULL,NULL,'58b6146a-00dc-11e2-b6d9-00ff26c46bb6');
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_address`
--

DROP TABLE IF EXISTS `person_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_address` (
  `person_address_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) DEFAULT NULL,
  `preferred` smallint(6) NOT NULL DEFAULT '0',
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `city_village` varchar(255) DEFAULT NULL,
  `state_province` varchar(255) DEFAULT NULL,
  `postal_code` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `latitude` varchar(50) DEFAULT NULL,
  `longitude` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `county_district` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `address6` varchar(255) DEFAULT NULL,
  `address5` varchar(255) DEFAULT NULL,
  `address4` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`person_address_id`),
  UNIQUE KEY `person_address_uuid_index` (`uuid`),
  KEY `patient_address_creator` (`creator`),
  KEY `address_for_person` (`person_id`),
  KEY `patient_address_void` (`voided_by`),
  CONSTRAINT `address_for_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `patient_address_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_address_void` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_address`
--

LOCK TABLES `person_address` WRITE;
/*!40000 ALTER TABLE `person_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `person_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_attribute`
--

DROP TABLE IF EXISTS `person_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_attribute` (
  `person_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) NOT NULL DEFAULT '0',
  `value` varchar(50) NOT NULL DEFAULT '',
  `person_attribute_type_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`person_attribute_id`),
  UNIQUE KEY `person_attribute_uuid_index` (`uuid`),
  KEY `attribute_changer` (`changed_by`),
  KEY `attribute_creator` (`creator`),
  KEY `defines_attribute_type` (`person_attribute_type_id`),
  KEY `identifies_person` (`person_id`),
  KEY `attribute_voider` (`voided_by`),
  CONSTRAINT `attribute_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `attribute_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `attribute_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `defines_attribute_type` FOREIGN KEY (`person_attribute_type_id`) REFERENCES `person_attribute_type` (`person_attribute_type_id`),
  CONSTRAINT `identifies_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_attribute`
--

LOCK TABLES `person_attribute` WRITE;
/*!40000 ALTER TABLE `person_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `person_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_attribute_type`
--

DROP TABLE IF EXISTS `person_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_attribute_type` (
  `person_attribute_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `format` varchar(50) DEFAULT NULL,
  `foreign_key` int(11) DEFAULT NULL,
  `searchable` smallint(6) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `edit_privilege` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `sort_weight` double DEFAULT NULL,
  PRIMARY KEY (`person_attribute_type_id`),
  UNIQUE KEY `person_attribute_type_uuid_index` (`uuid`),
  KEY `attribute_is_searchable` (`searchable`),
  KEY `name_of_attribute` (`name`),
  KEY `person_attribute_type_retired_status` (`retired`),
  KEY `attribute_type_changer` (`changed_by`),
  KEY `attribute_type_creator` (`creator`),
  KEY `user_who_retired_person_attribute_type` (`retired_by`),
  KEY `privilege_which_can_edit` (`edit_privilege`),
  CONSTRAINT `attribute_type_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `attribute_type_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `privilege_which_can_edit` FOREIGN KEY (`edit_privilege`) REFERENCES `privilege` (`privilege`),
  CONSTRAINT `user_who_retired_person_attribute_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_attribute_type`
--

LOCK TABLES `person_attribute_type` WRITE;
/*!40000 ALTER TABLE `person_attribute_type` DISABLE KEYS */;
INSERT INTO `person_attribute_type` VALUES (1,'Race','Group of persons related by common descent or heredity','java.lang.String',0,0,1,'2007-05-04 00:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL,'8d871386-c2cc-11de-8d13-0010c6dffd0f',6),(2,'Birthplace','Location of persons birth','java.lang.String',0,0,1,'2007-05-04 00:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL,'8d8718c2-c2cc-11de-8d13-0010c6dffd0f',0),(3,'Citizenship','Country of which this person is a member','java.lang.String',0,0,1,'2007-05-04 00:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL,'8d871afc-c2cc-11de-8d13-0010c6dffd0f',1),(4,'Mother\'s Name','First or last name of this person\'s mother','java.lang.String',0,0,1,'2007-05-04 00:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL,'8d871d18-c2cc-11de-8d13-0010c6dffd0f',5),(5,'Civil Status','Marriage status of this person','org.openmrs.Concept',1054,0,1,'2007-05-04 00:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL,'8d871f2a-c2cc-11de-8d13-0010c6dffd0f',2),(6,'Health District','District/region in which this patient\' home health center resides','java.lang.String',0,0,1,'2007-05-04 00:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL,'8d872150-c2cc-11de-8d13-0010c6dffd0f',4),(7,'Health Center','Specific Location of this person\'s home health center.','org.openmrs.Location',0,0,1,'2007-05-04 00:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL,'8d87236c-c2cc-11de-8d13-0010c6dffd0f',3);
/*!40000 ALTER TABLE `person_attribute_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_name`
--

DROP TABLE IF EXISTS `person_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_name` (
  `person_name_id` int(11) NOT NULL AUTO_INCREMENT,
  `preferred` smallint(6) NOT NULL DEFAULT '0',
  `person_id` int(11) NOT NULL,
  `prefix` varchar(50) DEFAULT NULL,
  `given_name` varchar(50) DEFAULT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `family_name_prefix` varchar(50) DEFAULT NULL,
  `family_name` varchar(50) DEFAULT NULL,
  `family_name2` varchar(50) DEFAULT NULL,
  `family_name_suffix` varchar(50) DEFAULT NULL,
  `degree` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`person_name_id`),
  UNIQUE KEY `person_name_uuid_index` (`uuid`),
  KEY `first_name` (`given_name`),
  KEY `last_name` (`family_name`),
  KEY `middle_name` (`middle_name`),
  KEY `user_who_made_name` (`creator`),
  KEY `name_for_person` (`person_id`),
  KEY `user_who_voided_name` (`voided_by`),
  KEY `family_name2` (`family_name2`),
  CONSTRAINT `name_for_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `user_who_made_name` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_name` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_name`
--

LOCK TABLES `person_name` WRITE;
/*!40000 ALTER TABLE `person_name` DISABLE KEYS */;
INSERT INTO `person_name` VALUES (1,1,1,NULL,'Super','',NULL,'User',NULL,NULL,NULL,1,'2005-01-01 00:00:00',0,NULL,NULL,NULL,NULL,NULL,'58b926cd-00dc-11e2-b6d9-00ff26c46bb6');
/*!40000 ALTER TABLE `person_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `privilege`
--

DROP TABLE IF EXISTS `privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `privilege` (
  `privilege` varchar(50) NOT NULL DEFAULT '',
  `description` varchar(250) NOT NULL DEFAULT '',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`privilege`),
  UNIQUE KEY `privilege_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `privilege`
--

LOCK TABLES `privilege` WRITE;
/*!40000 ALTER TABLE `privilege` DISABLE KEYS */;
INSERT INTO `privilege` VALUES ('Add Allergies','Add allergies','2585aba9-cc25-45fc-b366-573904b31942'),('Add Cohorts','Able to add a cohort to the system','8779e5a6-2b77-4b95-98b7-bdac4abe3d37'),('Add Concept Proposals','Able to add concept proposals to the system','2f4c95a2-3801-40f1-8e25-0b48d6f20e43'),('Add Encounters','Able to add patient encounters','dda27a07-d4e0-4d4e-a496-8bd061aa4fd8'),('Add FormEntry Archive','Allows the user to add the formentry archive','43348b56-d512-41bf-8801-f975569fe08f'),('Add FormEntry Error','Allows a user to add a formentry error item','4fcf9e47-c63a-4635-b47b-36136668536a'),('Add FormEntry Queue','Allows user to add a queue item to database','89721867-e30e-47ad-95a1-ae36bb12cae6'),('Add Observations','Able to add patient observations','ace4055d-7721-4492-83e7-25f79b021bd4'),('Add Orders','Able to add orders','8ee3f9fe-d556-4aac-9117-c0410d7dcf2c'),('Add Patient Identifiers','Able to add patient identifiers','d7f5e07e-6e97-468e-b361-a7b00a1bcffc'),('Add Patient Programs','Able to add patients to programs','015dd4ec-eddb-4055-94c1-c41bf672cdcd'),('Add Patients','Able to add patients','2a5a38f7-66c0-43ca-8e42-55bccb7d7eab'),('Add People','Able to add person objects','ed4695d7-a914-4fb5-a21f-3551148b2f66'),('Add Problems','Add problems','2ce701d0-57bc-4353-bc7a-7c12c22302b6'),('Add Relationships','Able to add relationships','6aacd42b-1875-4736-afa7-c82d124da37d'),('Add Users','Able to add users to OpenMRS','855306c7-5571-422b-a1e6-b4a9e74456e4'),('Delete Cohorts','Able to add a cohort to the system','e542e7b8-bd23-4e24-8078-d570b391715b'),('Delete Concept Proposals','Able to delete concept proposals from the system','8ac98378-7ef8-49e7-aba3-b54c32d9f396'),('Delete Encounters','Able to delete patient encounters','457c151e-5a31-4fc5-ad39-16b7a48a14c8'),('Delete FormEntry Archive','Allows the user to delete a formentry archive','1430bf04-d430-4dc0-a84b-7cf986f648f6'),('Delete FormEntry Error','Allows a user to delete a formentry error item','ced01d74-3e14-4b61-bb98-e07d5ad0c0e6'),('Delete FormEntry Queue','Allows the user to delete formentry queue items','a4394b12-0f0e-4b04-aae5-58568618b4ed'),('Delete Observations','Able to delete patient observations','d8fe8331-960a-4070-83de-d203a1ca57cc'),('Delete Orders','Able to delete orders','3e307780-8e7a-4963-98a5-a4a04e75e88c'),('Delete Patient Identifiers','Able to delete patient identifiers','3cd86f69-3fc7-468e-b831-05e2f05b18ff'),('Delete Patient Programs','Able to delete patients from programs','2ebfd8c5-5047-4fc5-a3ef-e40d0821cb26'),('Delete Patients','Able to delete patients','5ac1c772-4daa-4068-94ea-e26c83eb5096'),('Delete People','Able to delete objects','39447156-d31f-46db-96d3-94411d6c7191'),('Delete Relationships','Able to delete relationships','71fa812d-a399-4786-a323-8fce9af5adfe'),('Delete Users','Able to delete users in OpenMRS','fbb50276-c7dd-4834-b2ef-3d6064c0cb02'),('Edit Allergies','Able to edit allergies','6359d34a-e450-4112-8ee2-eafc196fc1c2'),('Edit Cohorts','Able to add a cohort to the system','13dd3f91-5f91-4aa0-b400-1a4849e010b1'),('Edit Concept Proposals','Able to edit concept proposals in the system','476d893e-38ef-4e5f-a8ba-2ef83e051cab'),('Edit Encounters','Able to edit patient encounters','f8e708ca-6d97-48a7-8a09-04aeea2c84af'),('Edit FormEntry Archive','Allows the user to edit a formentry archive','076f002b-2424-4346-b803-52dd329b74fb'),('Edit FormEntry Error','Allows a user to edit a formentry error item','3ed169b7-015a-4038-94c2-49b22e8e8c97'),('Edit FormEntry Queue','Allows the user to edit the formentry queue','6005adab-b48b-425c-a1eb-c30fde6e6cdc'),('Edit Observations','Able to edit patient observations','ffa078ff-b7b9-4892-b425-ff8163e94774'),('Edit Orders','Able to edit orders','9d0c594e-b3be-4f40-bde1-f83a76110dac'),('Edit Patient Identifiers','Able to edit patient identifiers','f259240c-b9f6-43df-bd30-925a39f69bfc'),('Edit Patient Programs','Able to edit patients in programs','d56343c3-794c-49dc-af40-7a8e33a31c46'),('Edit Patients','Able to edit patients','cd36b428-7186-4b37-9e32-0ce914750e48'),('Edit People','Able to add person objects','ee565a38-0456-465e-aa5c-84446ce6ca19'),('Edit Problems','Able to edit problems','af680a33-fca9-48fb-b899-5882e8035c6e'),('Edit Relationships','Able to edit relationships','046cee1e-b10b-43d1-9a66-cb45e23d2896'),('Edit User Passwords','Able to change the passwords of users in OpenMRS','e2b9e7cf-959e-4dd5-b5b5-e20295a7a757'),('Edit Users','Able to edit users in OpenMRS','aedd9316-f770-4b78-8b72-dc01b2c624d4'),('Form Entry','Able to fill out forms','9e156b0e-3486-436d-87b1-a21faf3ca033'),('Manage Alerts','Able to add/edit/delete user alerts','1019b333-ecea-4f55-8d0b-7534451c639f'),('Manage Cohort Definitions','Add/Edit/Remove Cohort Definitions','15da2a0f-29be-4d77-887e-0853e807f5f7'),('Manage Concept Classes','Able to add/edit/retire concept classes','9de81b7f-500b-4231-b967-d2fdd844df10'),('Manage Concept Datatypes','Able to add/edit/retire concept datatypes','16152f57-8002-4318-9299-2a708df5e28c'),('Manage Concept Name tags','Able to add/edit/delete concept name tags','dbfd87b9-02a2-401c-af88-47ffe5d46fb7'),('Manage Concept Sources','Able to add/edit/delete concept sources','681bfb2d-6ae0-4e0a-a77b-c0390a9820b7'),('Manage Concepts','Able to add/edit/delete concept entries','f0a12a19-399d-4cb0-a948-a4afa0ce4558'),('Manage Data Set Definitions','Add/Edit/Remove Data Set Definitions','cefc1045-31fb-4ad3-a3e4-6da3f714d127'),('Manage Dimension Definitions','Add/Edit/Remove Dimension Definitions','216fc2b1-1e71-4af3-8141-05f5419cfe2f'),('Manage Encounter Types','Able to add/edit/retire encounter types','77cdb640-8a9a-4fd1-9083-ac3716e377f9'),('Manage Field Types','Able to add/edit/retire field types','6a25ff46-6ec7-414d-8ebb-3fa4292cb2a2'),('Manage FormEntry XSN','Allows user to upload and edit the xsns stored on the server','8a246ab9-6c6c-45c8-b32f-5f8c132c1866'),('Manage Forms','Able to add/edit/delete forms','eb8e2a99-ee23-4b93-bef2-7a94bafd7998'),('Manage Global Properties','Able to add/edit global properties','b3aad0f9-5211-4794-93c1-d8ffe825e60e'),('Manage Identifier Types','Able to add/edit/retire patient identifier types','8aee4211-3d81-46a2-a1ab-880ffe8a3bd7'),('Manage Implementation Id','Able to view/add/edit the implementation id for the system','628d52b9-fb5f-45fe-be12-8392a2ce016d'),('Manage Indicator Definitions','Add/Edit/Remove Indicator Definitions','ffc34fdc-d7eb-4126-a986-6d32b9108c61'),('Manage Location Tags','Able to add/edit/delete location tags','e6056a0d-4a05-499c-bfe3-08376fe020a9'),('Manage Locations','Able to add/edit/delete locations','cf4953ef-af18-4668-b5a2-098fcfa6d3e1'),('Manage Modules','Able to add/remove modules to the system','5c89fba1-8a25-47f3-9ba9-76aef2efa0d9'),('Manage Order Types','Able to add/edit/retire order types','8d4ff07c-6149-40cc-83f7-4d521a6ec59b'),('Manage Person Attribute Types','Able to add/edit/retire person attribute types','bb99d709-7c3a-4b69-8b49-6c70765233b8'),('Manage Privileges','Able to add/edit/delete privileges','06704624-19ad-4e05-845a-302aac7cf4fd'),('Manage Programs','Able to add/view/delete patient programs','19c616df-5ef9-499a-a2a6-adc5af35188f'),('Manage Relationship Types','Able to add/edit/retire relationship types','88d767e3-03ef-49c9-80a6-1531869a3d56'),('Manage Report Definitions','Add/Edit/Remove Report Definitions','4d9ff86e-45ba-4b93-adc5-6d5f7de6efc6'),('Manage Report Designs','Add/Edit/Remove Report Designs','ce24319c-2fe8-467a-93b6-c726f14d22e3'),('Manage Reports','Base privilege for add/edit/delete reporting definitions. This gives access to the administrative menus, but you need to grant additional privileges to manage each specific type of reporting definition','afc0eff0-2b12-4920-bb10-eae0611f283d'),('Manage Roles','Able to add/edit/delete user roles','52f6515f-24e8-4d9b-83ed-571e52affb03'),('Manage Rule Definitions','Allows creation and editing of user-defined rules','4455bccb-7fb2-4b2d-81ed-31542232ef94'),('Manage Scheduler','Able to add/edit/remove scheduled tasks','a3da366a-d85c-4486-bc71-8ffc642d2e92'),('Manage Tokens','Allows registering and removal of tokens','c1a3b627-037c-4775-9331-6e12cf3cb98b'),('Patient Dashboard - View Demographics Section','Able to view the \'Demographics\' tab on the patient dashboard','e237f6a6-d0c0-4827-a1f4-b577281e68d3'),('Patient Dashboard - View Encounters Section','Able to view the \'Encounters\' tab on the patient dashboard','9818ea6b-c7b4-46be-b1b1-84a16839ccf0'),('Patient Dashboard - View Forms Section','Able to view the \'Forms\' tab on the patient dashboard','6af741bb-58bb-4199-8023-8e8b4170c680'),('Patient Dashboard - View Graphs Section','Able to view the \'Graphs\' tab on the patient dashboard','caa6277c-d51b-401a-8b98-95f7c57179df'),('Patient Dashboard - View Overview Section','Able to view the \'Overview\' tab on the patient dashboard','eb2f65f2-6d07-448b-a3f1-657712492e98'),('Patient Dashboard - View Patient Summary','Able to view the \'Summary\' tab on the patient dashboard','c3834d69-b57a-4d99-bdde-f7466b5ba64e'),('Patient Dashboard - View Regimen Section','Able to view the \'Regimen\' tab on the patient dashboard','79a14062-a834-47ef-9d12-de76f1282414'),('Remove Allergies','Remove allergies','97e14685-c708-4e46-a64e-f27433b561b1'),('Remove Problems','Remove problems','385be059-d73c-44bb-94d7-ac01d8ee2049'),('Upload XSN','Allows user to upload/overwrite the XSNs defined for forms','5be56744-7ca0-43a7-a1f2-d998a5badb39'),('View Administration Functions','Able to view the \'Administration\' link in the navigation bar','41ef22e2-f929-49cb-bd2f-5092344c3c8a'),('View Allergies','Able to view allergies','c7f4d3e1-f141-4fc0-ab6c-3019cfb70210'),('View Concept Classes','Able to view concept classes','b8572d0b-1809-4d0e-abfb-423fdb320549'),('View Concept Datatypes','Able to view concept datatypes','20a48b14-9afb-471e-a6f0-5e0b91c86e3d'),('View Concept Proposals','Able to view concept proposals to the system','c99e7d90-6e31-4441-8c5c-16f363ace8dc'),('View Concept Sources','Able to view concept sources','b6f46ab9-e5b9-47d9-85e5-cd17a02979ed'),('View Concepts','Able to view concept entries','afcbe60d-1e0c-45dd-89ba-6f74cd190fe2'),('View Database Changes','Able to view database changes from the admin screen','fba5f977-bdc3-4287-af03-a9a05bcc1b3a'),('View Encounter Types','Able to view encounter types','b6f8373f-1644-46bb-a223-28ed6693709d'),('View Encounters','Able to view patient encounters','9605be8d-80a2-4b0d-94bf-2a456ad4c560'),('View Field Types','Able to view field types','a61b0798-4213-40c2-8336-57b0ea3230aa'),('View FormEntry Archive','Allows the user to view the formentry archive','f0a424f9-eb0a-439b-8db1-4194a6656479'),('View FormEntry Error','Allows a user to view a formentry error','e3d81a6c-2c19-4a4b-90f5-c871b28683b6'),('View FormEntry Queue','Allows user to view the queue items','ba46ab0f-ca56-4750-9c77-a7cecd2d8b5e'),('View Forms','Able to view forms','ccd4939d-2e44-4563-bf70-5f3c8baace73'),('View Global Properties','Able to view global properties on the administration screen','80776e9b-395c-4cb8-823b-b56ed4998bab'),('View Identifier Types','Able to view patient identifier types','d45dbf55-a933-402e-a5e0-dd2b1b89eea5'),('View Locations','Able to view locations','ec4379ec-b99e-4e3f-ac4a-00e004867015'),('View Navigation Menu','Able to view the navigation menu (Home, View Patients, Dictionary, Administration, My Profile','c205c724-3fc3-4e15-90e1-d33a36c0c373'),('View Observations','Able to view patient observations','0e704051-5122-4cbb-90c2-9e28e7f52d55'),('View Order Types','Able to view order types','fff4d310-f9b1-4816-b497-9e1fbb3c79bf'),('View Orders','Able to view orders','0214a446-0c2c-4d8e-9a21-4e99a41c54d5'),('View Patient Cohorts','Able to view patient cohorts','e351c04f-14cd-4101-a40e-20e35ea349a1'),('View Patient Identifiers','Able to view patient identifiers','128b1ac8-ab13-48f7-ad92-9badf8a449df'),('View Patient Programs','Able to see which programs that patients are in','41f4d608-72ad-41e7-b44f-2823d7b23b1d'),('View Patients','Able to view patients','86619b5c-3712-495b-b1ae-d631bc494946'),('View People','Able to view person objects','271235f8-6965-44c1-b750-36840bd5c839'),('View Person Attribute Types','Able to view person attribute types','5ba16f51-5bc8-4973-930b-717612799921'),('View Privileges','Able to view user privileges','bf85830a-be24-4c6e-a6ad-251f2fe75c87'),('View Problems','Able to view problems','571bfc94-67db-4510-aed5-640d3fb4dd9a'),('View Programs','Able to view patient programs','717d8626-1100-4378-830b-b708fd7c4236'),('View Relationship Types','Able to view relationship types','21519a0f-c074-4d12-b66e-ae852a9bd99f'),('View Relationships','Able to view relationships','9530828d-332c-4fac-a382-3dc0bf6d647e'),('View Roles','Able to view user roles','de5eb0cd-aecf-4c2e-85f3-a230c15e9459'),('View Rule Definitions','Allows viewing of user-defined rules. (This privilege is not necessary to run rules under normal usage.)','8ad17744-be64-4a86-bd37-d82b40bc032b'),('View Unpublished Forms','Able to view and fill out unpublished forms','7fb341cc-64f2-4fbc-a0e6-9092954e8726'),('View Users','Able to view users in OpenMRS','f74a0aff-8f35-4c16-bbcc-53cc274b3ac3');
/*!40000 ALTER TABLE `privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `program`
--

DROP TABLE IF EXISTS `program`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program` (
  `program_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`program_id`),
  UNIQUE KEY `program_uuid_index` (`uuid`),
  KEY `user_who_changed_program` (`changed_by`),
  KEY `program_concept` (`concept_id`),
  KEY `program_creator` (`creator`),
  CONSTRAINT `program_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `program_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_program` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `program`
--

LOCK TABLES `program` WRITE;
/*!40000 ALTER TABLE `program` DISABLE KEYS */;
/*!40000 ALTER TABLE `program` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `program_workflow`
--

DROP TABLE IF EXISTS `program_workflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_workflow` (
  `program_workflow_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) NOT NULL DEFAULT '0',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`program_workflow_id`),
  UNIQUE KEY `program_workflow_uuid_index` (`uuid`),
  KEY `workflow_changed_by` (`changed_by`),
  KEY `workflow_concept` (`concept_id`),
  KEY `workflow_creator` (`creator`),
  KEY `program_for_workflow` (`program_id`),
  CONSTRAINT `program_for_workflow` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`),
  CONSTRAINT `workflow_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `workflow_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `workflow_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `program_workflow`
--

LOCK TABLES `program_workflow` WRITE;
/*!40000 ALTER TABLE `program_workflow` DISABLE KEYS */;
/*!40000 ALTER TABLE `program_workflow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `program_workflow_state`
--

DROP TABLE IF EXISTS `program_workflow_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_workflow_state` (
  `program_workflow_state_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_workflow_id` int(11) NOT NULL DEFAULT '0',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `initial` smallint(6) NOT NULL DEFAULT '0',
  `terminal` smallint(6) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`program_workflow_state_id`),
  UNIQUE KEY `program_workflow_state_uuid_index` (`uuid`),
  KEY `state_changed_by` (`changed_by`),
  KEY `state_concept` (`concept_id`),
  KEY `state_creator` (`creator`),
  KEY `workflow_for_state` (`program_workflow_id`),
  CONSTRAINT `state_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `state_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `state_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `workflow_for_state` FOREIGN KEY (`program_workflow_id`) REFERENCES `program_workflow` (`program_workflow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `program_workflow_state`
--

LOCK TABLES `program_workflow_state` WRITE;
/*!40000 ALTER TABLE `program_workflow_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `program_workflow_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relationship`
--

DROP TABLE IF EXISTS `relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationship` (
  `relationship_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_a` int(11) NOT NULL,
  `relationship` int(11) NOT NULL DEFAULT '0',
  `person_b` int(11) NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) DEFAULT NULL,
  PRIMARY KEY (`relationship_id`),
  UNIQUE KEY `relationship_uuid_index` (`uuid`),
  KEY `relation_creator` (`creator`),
  KEY `person_a` (`person_a`),
  KEY `person_b` (`person_b`),
  KEY `relationship_type_id` (`relationship`),
  KEY `relation_voider` (`voided_by`),
  CONSTRAINT `person_a` FOREIGN KEY (`person_a`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `person_b` FOREIGN KEY (`person_b`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `relationship_type_id` FOREIGN KEY (`relationship`) REFERENCES `relationship_type` (`relationship_type_id`),
  CONSTRAINT `relation_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `relation_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relationship`
--

LOCK TABLES `relationship` WRITE;
/*!40000 ALTER TABLE `relationship` DISABLE KEYS */;
/*!40000 ALTER TABLE `relationship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relationship_type`
--

DROP TABLE IF EXISTS `relationship_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationship_type` (
  `relationship_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `a_is_to_b` varchar(50) NOT NULL,
  `b_is_to_a` varchar(50) NOT NULL,
  `preferred` int(11) NOT NULL DEFAULT '0',
  `weight` int(11) NOT NULL DEFAULT '0',
  `description` varchar(255) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `uuid` char(38) NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`relationship_type_id`),
  UNIQUE KEY `relationship_type_uuid_index` (`uuid`),
  KEY `user_who_created_rel` (`creator`),
  KEY `user_who_retired_relationship_type` (`retired_by`),
  CONSTRAINT `user_who_created_rel` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_relationship_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relationship_type`
--

LOCK TABLES `relationship_type` WRITE;
/*!40000 ALTER TABLE `relationship_type` DISABLE KEYS */;
INSERT INTO `relationship_type` VALUES (1,'Doctor','Patient',0,0,'Relationship from a primary care provider to the patient',1,'2007-05-04 00:00:00','8d919b58-c2cc-11de-8d13-0010c6dffd0f',0,NULL,NULL,NULL),(2,'Sibling','Sibling',0,0,'Relationship between brother/sister, brother/brother, and sister/sister',1,'2007-05-04 00:00:00','8d91a01c-c2cc-11de-8d13-0010c6dffd0f',0,NULL,NULL,NULL),(3,'Parent','Child',0,0,'Relationship from a mother/father to the child',1,'2007-05-04 00:00:00','8d91a210-c2cc-11de-8d13-0010c6dffd0f',0,NULL,NULL,NULL),(4,'Aunt/Uncle','Niece/Nephew',0,0,'',1,'2007-05-04 00:00:00','8d91a3dc-c2cc-11de-8d13-0010c6dffd0f',0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `relationship_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_object`
--

DROP TABLE IF EXISTS `report_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_object` (
  `report_object_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `report_object_type` varchar(255) NOT NULL,
  `report_object_sub_type` varchar(255) NOT NULL,
  `xml_data` text,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` smallint(6) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`report_object_id`),
  UNIQUE KEY `report_object_uuid_index` (`uuid`),
  KEY `user_who_changed_report_object` (`changed_by`),
  KEY `report_object_creator` (`creator`),
  KEY `user_who_voided_report_object` (`voided_by`),
  CONSTRAINT `report_object_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_report_object` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_report_object` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_object`
--

LOCK TABLES `report_object` WRITE;
/*!40000 ALTER TABLE `report_object` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_object` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_schema_xml`
--

DROP TABLE IF EXISTS `report_schema_xml`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_schema_xml` (
  `report_schema_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `xml_data` mediumtext NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`report_schema_id`),
  UNIQUE KEY `report_schema_xml_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_schema_xml`
--

LOCK TABLES `report_schema_xml` WRITE;
/*!40000 ALTER TABLE `report_schema_xml` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_schema_xml` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reporting_report_design`
--

DROP TABLE IF EXISTS `reporting_report_design`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reporting_report_design` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `report_definition_id` int(11) NOT NULL DEFAULT '0',
  `renderer_type` varchar(255) NOT NULL,
  `properties` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_definition_id for reporting_report_design` (`report_definition_id`),
  KEY `creator for reporting_report_design` (`creator`),
  KEY `changed_by for reporting_report_design` (`changed_by`),
  KEY `retired_by for reporting_report_design` (`retired_by`),
  CONSTRAINT `report_definition_id for reporting_report_design` FOREIGN KEY (`report_definition_id`) REFERENCES `serialized_object` (`serialized_object_id`),
  CONSTRAINT `creator for reporting_report_design` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `changed_by for reporting_report_design` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `retired_by for reporting_report_design` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reporting_report_design`
--

LOCK TABLES `reporting_report_design` WRITE;
/*!40000 ALTER TABLE `reporting_report_design` DISABLE KEYS */;
/*!40000 ALTER TABLE `reporting_report_design` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reporting_report_design_resource`
--

DROP TABLE IF EXISTS `reporting_report_design_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reporting_report_design_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `report_design_id` int(11) NOT NULL DEFAULT '0',
  `content_type` varchar(50) DEFAULT NULL,
  `extension` varchar(20) DEFAULT NULL,
  `contents` longblob,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_design_id for reporting_report_design_resource` (`report_design_id`),
  KEY `creator for reporting_report_design_resource` (`creator`),
  KEY `changed_by for reporting_report_design_resource` (`changed_by`),
  KEY `retired_by for reporting_report_design_resource` (`retired_by`),
  CONSTRAINT `report_design_id for reporting_report_design_resource` FOREIGN KEY (`report_design_id`) REFERENCES `reporting_report_design` (`id`),
  CONSTRAINT `creator for reporting_report_design_resource` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `changed_by for reporting_report_design_resource` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `retired_by for reporting_report_design_resource` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reporting_report_design_resource`
--

LOCK TABLES `reporting_report_design_resource` WRITE;
/*!40000 ALTER TABLE `reporting_report_design_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `reporting_report_design_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reporting_report_request`
--

DROP TABLE IF EXISTS `reporting_report_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reporting_report_request` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `base_cohort_uuid` char(38) DEFAULT NULL,
  `base_cohort_parameters` text,
  `report_definition_uuid` char(38) NOT NULL,
  `report_definition_parameters` text,
  `renderer_type` varchar(255) NOT NULL,
  `renderer_argument` varchar(255) DEFAULT NULL,
  `requested_by` int(11) NOT NULL DEFAULT '0',
  `request_datetime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `priority` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `evaluation_start_datetime` datetime DEFAULT NULL,
  `evaluation_complete_datetime` datetime DEFAULT NULL,
  `render_complete_datetime` datetime DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `requested_by for reporting_report_request` (`requested_by`),
  CONSTRAINT `requested_by for reporting_report_request` FOREIGN KEY (`requested_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reporting_report_request`
--

LOCK TABLES `reporting_report_request` WRITE;
/*!40000 ALTER TABLE `reporting_report_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `reporting_report_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `role` varchar(50) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`role`),
  UNIQUE KEY `role_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES ('Anonymous','Privileges for non-authenticated users.','4dffcc28-f56d-452d-b249-098fd6d8c61d'),('Authenticated','Privileges gained once authentication has been established.','cdfc2651-e413-4434-b76d-9281b7007b75'),('Provider','All users with the \'Provider\' role will appear as options in the default Infopath ','8d94f280-c2cc-11de-8d13-0010c6dffd0f'),('System Developer','Developers of the OpenMRS .. have additional access to change fundamental structure of the database model.','8d94f852-c2cc-11de-8d13-0010c6dffd0f');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_privilege`
--

DROP TABLE IF EXISTS `role_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_privilege` (
  `role` varchar(50) NOT NULL DEFAULT '',
  `privilege` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`privilege`,`role`),
  KEY `role_privilege` (`role`),
  CONSTRAINT `role_privilege` FOREIGN KEY (`role`) REFERENCES `role` (`role`),
  CONSTRAINT `privilege_definitons` FOREIGN KEY (`privilege`) REFERENCES `privilege` (`privilege`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_privilege`
--

LOCK TABLES `role_privilege` WRITE;
/*!40000 ALTER TABLE `role_privilege` DISABLE KEYS */;
INSERT INTO `role_privilege` VALUES ('Authenticated','View Concept Classes'),('Authenticated','View Concept Datatypes'),('Authenticated','View Encounter Types'),('Authenticated','View Field Types'),('Authenticated','View Global Properties'),('Authenticated','View Identifier Types'),('Authenticated','View Locations'),('Authenticated','View Order Types'),('Authenticated','View Person Attribute Types'),('Authenticated','View Privileges'),('Authenticated','View Relationship Types'),('Authenticated','View Relationships'),('Authenticated','View Roles');
/*!40000 ALTER TABLE `role_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_role`
--

DROP TABLE IF EXISTS `role_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_role` (
  `parent_role` varchar(50) NOT NULL DEFAULT '',
  `child_role` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`parent_role`,`child_role`),
  KEY `inherited_role` (`child_role`),
  CONSTRAINT `parent_role` FOREIGN KEY (`parent_role`) REFERENCES `role` (`role`),
  CONSTRAINT `inherited_role` FOREIGN KEY (`child_role`) REFERENCES `role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_role`
--

LOCK TABLES `role_role` WRITE;
/*!40000 ALTER TABLE `role_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduler_task_config`
--

DROP TABLE IF EXISTS `scheduler_task_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduler_task_config` (
  `task_config_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `schedulable_class` text,
  `start_time` datetime DEFAULT NULL,
  `start_time_pattern` varchar(50) DEFAULT NULL,
  `repeat_interval` int(11) NOT NULL DEFAULT '0',
  `start_on_startup` int(11) NOT NULL DEFAULT '0',
  `started` int(11) NOT NULL DEFAULT '0',
  `created_by` int(11) DEFAULT '0',
  `date_created` datetime DEFAULT '2005-01-01 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `last_execution_time` datetime DEFAULT NULL,
  PRIMARY KEY (`task_config_id`),
  UNIQUE KEY `scheduler_task_config_uuid_index` (`uuid`),
  KEY `scheduler_changer` (`changed_by`),
  KEY `scheduler_creator` (`created_by`),
  CONSTRAINT `scheduler_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `scheduler_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduler_task_config`
--

LOCK TABLES `scheduler_task_config` WRITE;
/*!40000 ALTER TABLE `scheduler_task_config` DISABLE KEYS */;
INSERT INTO `scheduler_task_config` VALUES (1,'Update Concept Index','Iterates through the concept dictionary, re-creating the concept index (which are used for searcing). This task is started when using the \'Update Concept Index Storage\' page and no range is given.  This task stops itself when one iteration has completed.','org.openmrs.scheduler.tasks.ConceptIndexUpdateTask',NULL,NULL,0,0,0,1,'2005-01-01 00:00:00',2,'2012-09-17 11:29:59','7c75911e-0310-11e0-8222-18a905e044dc',NULL),(2,'Initialize Logic Rule Providers',NULL,'org.openmrs.logic.task.InitializeLogicRuleProvidersTask','2012-09-17 11:32:00',NULL,1999999999,0,1,NULL,'2012-09-17 11:30:17',1,'2012-09-17 11:32:32','1cc8f47a-60ef-4e75-a4ba-75462ad94288','2012-09-17 11:32:00');
/*!40000 ALTER TABLE `scheduler_task_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduler_task_config_property`
--

DROP TABLE IF EXISTS `scheduler_task_config_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduler_task_config_property` (
  `task_config_property_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` text,
  `task_config_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`task_config_property_id`),
  KEY `task_config_for_property` (`task_config_id`),
  CONSTRAINT `task_config_for_property` FOREIGN KEY (`task_config_id`) REFERENCES `scheduler_task_config` (`task_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduler_task_config_property`
--

LOCK TABLES `scheduler_task_config_property` WRITE;
/*!40000 ALTER TABLE `scheduler_task_config_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `scheduler_task_config_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `serialized_object`
--

DROP TABLE IF EXISTS `serialized_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `serialized_object` (
  `serialized_object_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(5000) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `subtype` varchar(255) NOT NULL,
  `serialization_class` varchar(255) NOT NULL,
  `serialized_data` text NOT NULL,
  `date_created` datetime NOT NULL,
  `creator` int(11) NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `date_retired` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `retire_reason` varchar(1000) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`serialized_object_id`),
  UNIQUE KEY `serialized_object_uuid_index` (`uuid`),
  KEY `serialized_object_creator` (`creator`),
  KEY `serialized_object_changed_by` (`changed_by`),
  KEY `serialized_object_retired_by` (`retired_by`),
  CONSTRAINT `serialized_object_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `serialized_object_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `serialized_object_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `serialized_object`
--

LOCK TABLES `serialized_object` WRITE;
/*!40000 ALTER TABLE `serialized_object` DISABLE KEYS */;
/*!40000 ALTER TABLE `serialized_object` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tribe`
--

DROP TABLE IF EXISTS `tribe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tribe` (
  `tribe_id` int(11) NOT NULL AUTO_INCREMENT,
  `retired` tinyint(4) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`tribe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tribe`
--

LOCK TABLES `tribe` WRITE;
/*!40000 ALTER TABLE `tribe` DISABLE KEYS */;
/*!40000 ALTER TABLE `tribe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_property`
--

DROP TABLE IF EXISTS `user_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_property` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `property` varchar(100) NOT NULL DEFAULT '',
  `property_value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`user_id`,`property`),
  CONSTRAINT `user_property` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_property`
--

LOCK TABLES `user_property` WRITE;
/*!40000 ALTER TABLE `user_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `role` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`role`,`user_id`),
  KEY `user_role` (`user_id`),
  CONSTRAINT `user_role` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `role_definitions` FOREIGN KEY (`role`) REFERENCES `role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'Provider'),(1,'System Developer');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `system_id` varchar(50) NOT NULL DEFAULT '',
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `salt` varchar(128) DEFAULT NULL,
  `secret_question` varchar(255) DEFAULT NULL,
  `secret_answer` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `person_id` int(11) DEFAULT NULL,
  `retired` tinyint(4) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`user_id`),
  KEY `user_who_changed_user` (`changed_by`),
  KEY `user_creator` (`creator`),
  KEY `person_id_for_user` (`person_id`),
  KEY `user_who_retired_this_user` (`retired_by`),
  CONSTRAINT `person_id_for_user` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `user_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_user` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_this_user` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','','ae18ffbd34bbf62597f29e58307860456589cbac7ec1dd83f37ba2e91d10375662357caf7f44aa395728a031ecb7e3dc6fc9b54ae26e78b69a87eb74677cc4ce','8b636bb0d96f2991e89b1f2bc0c73de5bfc728bad245c8e50284cab3861998c12a2965e7948b0d15b4c67c844548d622185dba0aa0775952031daf6b6d775943',NULL,NULL,1,'2005-01-01 00:00:00',1,'2012-09-17 11:29:49',1,0,NULL,NULL,NULL,'6caea4fd-00dc-11e2-b6d9-00ff26c46bb6'),(2,'daemon','daemon',NULL,NULL,NULL,NULL,1,'2010-04-26 13:25:00',NULL,NULL,NULL,0,NULL,NULL,NULL,'A4F30A1B-5EB9-11DF-A648-37A07F9C90FB');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xforms_medical_history_field`
--

DROP TABLE IF EXISTS `xforms_medical_history_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xforms_medical_history_field` (
  `field_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `tabIndex` int(11) DEFAULT NULL,
  PRIMARY KEY (`field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xforms_medical_history_field`
--

LOCK TABLES `xforms_medical_history_field` WRITE;
/*!40000 ALTER TABLE `xforms_medical_history_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `xforms_medical_history_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xforms_person_repeat_attribute`
--

DROP TABLE IF EXISTS `xforms_person_repeat_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xforms_person_repeat_attribute` (
  `person_repeat_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) NOT NULL DEFAULT '0',
  `value` varchar(50) NOT NULL DEFAULT '',
  `person_attribute_type_id` int(11) NOT NULL DEFAULT '0',
  `value_id` int(11) NOT NULL DEFAULT '0',
  `value_id_type` int(11) NOT NULL DEFAULT '0',
  `value_display_order` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_repeat_attribute_id`),
  KEY `repeat_identifies_person` (`person_id`),
  KEY `repeat_defines_attribute_type` (`person_attribute_type_id`),
  KEY `repeat_attribute_creator` (`creator`),
  KEY `repeat_attribute_changer` (`changed_by`),
  KEY `repeat_attribute_voider` (`voided_by`),
  CONSTRAINT `repeat_attribute_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `repeat_attribute_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `repeat_attribute_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `repeat_defines_attribute_type` FOREIGN KEY (`person_attribute_type_id`) REFERENCES `person_attribute_type` (`person_attribute_type_id`),
  CONSTRAINT `repeat_identifies_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xforms_person_repeat_attribute`
--

LOCK TABLES `xforms_person_repeat_attribute` WRITE;
/*!40000 ALTER TABLE `xforms_person_repeat_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `xforms_person_repeat_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xforms_xform`
--

DROP TABLE IF EXISTS `xforms_xform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xforms_xform` (
  `form_id` int(11) NOT NULL,
  `xform_xml` longtext,
  `layout_xml` longtext,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `locale_xml` longtext,
  `javascript_src` longtext,
  PRIMARY KEY (`form_id`),
  KEY `user_who_created_xform` (`creator`),
  KEY `form_with_which_xform_is_related` (`form_id`),
  KEY `user_who_last_changed_xform` (`changed_by`),
  CONSTRAINT `user_who_created_xform` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_last_changed_xform` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xforms_xform`
--

LOCK TABLES `xforms_xform` WRITE;
/*!40000 ALTER TABLE `xforms_xform` DISABLE KEYS */;
/*!40000 ALTER TABLE `xforms_xform` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-09-17 11:33:03
