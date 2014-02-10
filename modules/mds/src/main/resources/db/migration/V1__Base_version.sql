-- MySQL dump 10.13  Distrib 5.5.35, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: motech_data_services
-- ------------------------------------------------------
-- Server version	5.5.35-0ubuntu0.12.04.2
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO,ANSI' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table "AvailableType"
--

DROP TABLE IF EXISTS "AvailableType";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "AvailableType" (
  "id" bigint(20) NOT NULL,
  "defaultName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "TYPE_ID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "AvailableType_N49" ("TYPE_ID"),
  CONSTRAINT "AvailableType_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "AvailableType"
--

LOCK TABLES "AvailableType" WRITE;
/*!40000 ALTER TABLE "AvailableType" DISABLE KEYS */;
INSERT INTO "AvailableType" VALUES (1,'int',1),(2,'str',2),(3,'bool',3),(4,'date',4),(5,'time',5),(6,'datetime',6),(7,'decimal',7),(8,'list',8);
/*!40000 ALTER TABLE "AvailableType" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "Entity"
--

DROP TABLE IF EXISTS "Entity";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "Entity" (
  "id" bigint(20) NOT NULL,
  "className" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "entityVersion" bigint(20) DEFAULT NULL,
  "module" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "namespace" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "DISCRIMINATOR" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "changesMade" bit(1) DEFAULT NULL,
  "draftOwnerUsername" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "lastModificationDate" datetime DEFAULT NULL,
  "parentEntity_id_OID" bigint(20) DEFAULT NULL,
  "parentVersion" bigint(20) DEFAULT NULL,
  "drafts_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "DRAFT_USER_IDX" ("parentEntity_id_OID","draftOwnerUsername"),
  KEY "Entity_N49" ("parentEntity_id_OID"),
  CONSTRAINT "Entity_FK1" FOREIGN KEY ("parentEntity_id_OID") REFERENCES "Entity" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "Entity"
--

LOCK TABLES "Entity" WRITE;
/*!40000 ALTER TABLE "Entity" DISABLE KEYS */;
/*!40000 ALTER TABLE "Entity" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "Field"
--

DROP TABLE IF EXISTS "Field";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "Field" (
  "id" bigint(20) NOT NULL,
  "defaultValue" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  "exposedViaRest" bit(1) NOT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "required" bit(1) NOT NULL,
  "tooltip" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "tracked" bit(1) NOT NULL,
  "type_id_OID" bigint(20) DEFAULT NULL,
  "fields_INTEGER_IDX" int(11) DEFAULT NULL,
  "uiDisplayable" bit(1) NOT NULL,
  "uiFilterable" bit(1) NOT NULL,
  PRIMARY KEY ("id"),
  KEY "Field_N50" ("entity_id_OID"),
  KEY "Field_N49" ("type_id_OID"),
  CONSTRAINT "Field_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "Field_FK2" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "Field"
--

LOCK TABLES "Field" WRITE;
/*!40000 ALTER TABLE "Field" DISABLE KEYS */;
/*!40000 ALTER TABLE "Field" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "FieldMetadata"
--

DROP TABLE IF EXISTS "FieldMetadata";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "FieldMetadata" (
  "id" bigint(20) NOT NULL,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "key" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "value" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "metadata_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldMetadata_N49" ("field_id_OID"),
  CONSTRAINT "FieldMetadata_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "FieldMetadata"
--

LOCK TABLES "FieldMetadata" WRITE;
/*!40000 ALTER TABLE "FieldMetadata" DISABLE KEYS */;
/*!40000 ALTER TABLE "FieldMetadata" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "FieldSetting"
--

DROP TABLE IF EXISTS "FieldSetting";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "FieldSetting" (
  "id" bigint(20) NOT NULL,
  "DETAILS_ID" bigint(20) DEFAULT NULL,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "value" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "settings_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldSetting_N50" ("field_id_OID"),
  KEY "FieldSetting_N49" ("DETAILS_ID"),
  CONSTRAINT "FieldSetting_FK2" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeSetting" ("id"),
  CONSTRAINT "FieldSetting_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "FieldSetting"
--

LOCK TABLES "FieldSetting" WRITE;
/*!40000 ALTER TABLE "FieldSetting" DISABLE KEYS */;
/*!40000 ALTER TABLE "FieldSetting" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "FieldValidation"
--

DROP TABLE IF EXISTS "FieldValidation";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "FieldValidation" (
  "id" bigint(20) NOT NULL,
  "DETAILS_ID" bigint(20) DEFAULT NULL,
  "enabled" bit(1) NOT NULL,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "value" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "validations_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldValidation_N49" ("field_id_OID"),
  KEY "FieldValidation_N50" ("DETAILS_ID"),
  CONSTRAINT "FieldValidation_FK2" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id"),
  CONSTRAINT "FieldValidation_FK1" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeValidation" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "FieldValidation"
--

LOCK TABLES "FieldValidation" WRITE;
/*!40000 ALTER TABLE "FieldValidation" DISABLE KEYS */;
/*!40000 ALTER TABLE "FieldValidation" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "Lookup"
--

DROP TABLE IF EXISTS "Lookup";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "Lookup" (
  "id" bigint(20) NOT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  "exposedViaRest" bit(1) NOT NULL,
  "lookupName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "singleObjectReturn" bit(1) NOT NULL,
  "lookups_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "Lookup_N49" ("entity_id_OID"),
  CONSTRAINT "Lookup_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "Lookup"
--

LOCK TABLES "Lookup" WRITE;
/*!40000 ALTER TABLE "Lookup" DISABLE KEYS */;
/*!40000 ALTER TABLE "Lookup" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "RestOptions"
--

DROP TABLE IF EXISTS "RestOptions";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "RestOptions" (
  "id" bigint(20) NOT NULL,
  "allowCreate" bit(1) NOT NULL,
  "allowDelete" bit(1) NOT NULL,
  "allowRead" bit(1) NOT NULL,
  "allowUpdate" bit(1) NOT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "RestOptions_N49" ("entity_id_OID"),
  CONSTRAINT "RestOptions_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "RestOptions"
--

LOCK TABLES "RestOptions" WRITE;
/*!40000 ALTER TABLE "RestOptions" DISABLE KEYS */;
/*!40000 ALTER TABLE "RestOptions" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "SEQUENCE_TABLE"
--

DROP TABLE IF EXISTS "SEQUENCE_TABLE";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "SEQUENCE_TABLE" (
  "SEQUENCE_NAME" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "NEXT_VAL" bigint(20) NOT NULL,
  PRIMARY KEY ("SEQUENCE_NAME")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "SEQUENCE_TABLE"
--

LOCK TABLES "SEQUENCE_TABLE" WRITE;
/*!40000 ALTER TABLE "SEQUENCE_TABLE" DISABLE KEYS */;
INSERT INTO "SEQUENCE_TABLE" VALUES ('org.motechproject.mds.domain.AvailableType',11),('org.motechproject.mds.domain.Type',11),('org.motechproject.mds.domain.TypeSetting',11),('org.motechproject.mds.domain.TypeSettingOption',11),('org.motechproject.mds.domain.TypeValidation',21);
/*!40000 ALTER TABLE "SEQUENCE_TABLE" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TYPE_SETTING_SETTING_OPTION"
--

DROP TABLE IF EXISTS "TYPE_SETTING_SETTING_OPTION";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TYPE_SETTING_SETTING_OPTION" (
  "TYPE_SETTING_ID_OID" bigint(20) NOT NULL,
  "SETTING_OPTION_ID_EID" bigint(20) DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("TYPE_SETTING_ID_OID","IDX"),
  KEY "TYPE_SETTING_SETTING_OPTION_N49" ("TYPE_SETTING_ID_OID"),
  KEY "TYPE_SETTING_SETTING_OPTION_N50" ("SETTING_OPTION_ID_EID"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK2" FOREIGN KEY ("SETTING_OPTION_ID_EID") REFERENCES "TypeSettingOption" ("id"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK1" FOREIGN KEY ("TYPE_SETTING_ID_OID") REFERENCES "TypeSetting" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TYPE_SETTING_SETTING_OPTION"
--

LOCK TABLES "TYPE_SETTING_SETTING_OPTION" WRITE;
/*!40000 ALTER TABLE "TYPE_SETTING_SETTING_OPTION" DISABLE KEYS */;
INSERT INTO "TYPE_SETTING_SETTING_OPTION" VALUES (1,1,0),(2,1,0),(3,1,0),(1,2,1),(2,2,1);
/*!40000 ALTER TABLE "TYPE_SETTING_SETTING_OPTION" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TYPE_TYPE_SETTING"
--

DROP TABLE IF EXISTS "TYPE_TYPE_SETTING";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TYPE_TYPE_SETTING" (
  "TYPE_ID_OID" bigint(20) NOT NULL,
  "TYPE_SETTING_ID_EID" bigint(20) DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  KEY "TYPE_TYPE_SETTING_N49" ("TYPE_SETTING_ID_EID"),
  KEY "TYPE_TYPE_SETTING_N50" ("TYPE_ID_OID"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK2" FOREIGN KEY ("TYPE_SETTING_ID_EID") REFERENCES "TypeSetting" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TYPE_TYPE_SETTING"
--

LOCK TABLES "TYPE_TYPE_SETTING" WRITE;
/*!40000 ALTER TABLE "TYPE_TYPE_SETTING" DISABLE KEYS */;
INSERT INTO "TYPE_TYPE_SETTING" VALUES (7,1,0),(7,2,1),(8,3,0),(8,4,1),(8,5,2);
/*!40000 ALTER TABLE "TYPE_TYPE_SETTING" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TYPE_TYPE_VALIDATION"
--

DROP TABLE IF EXISTS "TYPE_TYPE_VALIDATION";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TYPE_TYPE_VALIDATION" (
  "TYPE_ID_OID" bigint(20) NOT NULL,
  "TYPE_VALIDATION_ID_EID" bigint(20) DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  KEY "TYPE_TYPE_VALIDATION_N50" ("TYPE_VALIDATION_ID_EID"),
  KEY "TYPE_TYPE_VALIDATION_N49" ("TYPE_ID_OID"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK2" FOREIGN KEY ("TYPE_VALIDATION_ID_EID") REFERENCES "TypeValidation" ("id"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TYPE_TYPE_VALIDATION"
--

LOCK TABLES "TYPE_TYPE_VALIDATION" WRITE;
/*!40000 ALTER TABLE "TYPE_TYPE_VALIDATION" DISABLE KEYS */;
INSERT INTO "TYPE_TYPE_VALIDATION" VALUES (1,1,0),(1,2,1),(1,3,2),(1,4,3),(2,5,0),(2,6,1),(2,7,2),(7,8,0),(7,9,1),(7,10,2),(7,11,3);
/*!40000 ALTER TABLE "TYPE_TYPE_VALIDATION" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "Tracking"
--

DROP TABLE IF EXISTS "Tracking";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "Tracking" (
  "id" bigint(20) NOT NULL,
  "allowCreate" bit(1) NOT NULL,
  "allowDelete" bit(1) NOT NULL,
  "allowRead" bit(1) NOT NULL,
  "allowUpdate" bit(1) NOT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "Tracking_N49" ("entity_id_OID"),
  CONSTRAINT "Tracking_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "Tracking"
--

LOCK TABLES "Tracking" WRITE;
/*!40000 ALTER TABLE "Tracking" DISABLE KEYS */;
/*!40000 ALTER TABLE "Tracking" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "Type"
--

DROP TABLE IF EXISTS "Type";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "Type" (
  "id" bigint(20) NOT NULL,
  "description" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "typeClass" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "Type"
--

LOCK TABLES "Type" WRITE;
/*!40000 ALTER TABLE "Type" DISABLE KEYS */;
INSERT INTO "Type" VALUES (1,'mds.field.description.integer','mds.field.integer','java.lang.Integer'),(2,'mds.field.description.string','mds.field.string','java.lang.String'),(3,'mds.field.description.boolean','mds.field.boolean','java.lang.Boolean'),(4,'mds.field.description.date','mds.field.date','java.util.Date'),(5,'mds.field.description.time','mds.field.time','org.motechproject.commons.date.model.Time'),(6,'mds.field.description.datetime','mds.field.datetime','org.joda.time.DateTime'),(7,'mds.field.description.decimal','mds.field.decimal','java.lang.Double'),(8,'mds.field.description.combobox','mds.field.combobox','java.util.List');
/*!40000 ALTER TABLE "Type" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TypeSetting"
--

DROP TABLE IF EXISTS "TypeSetting";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeSetting" (
  "id" bigint(20) NOT NULL,
  "defaultValue" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "TYPE_ID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "TypeSetting_N49" ("TYPE_ID"),
  CONSTRAINT "TypeSetting_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeSetting"
--

LOCK TABLES "TypeSetting" WRITE;
/*!40000 ALTER TABLE "TypeSetting" DISABLE KEYS */;
INSERT INTO "TypeSetting" VALUES (1,'9','mds.form.label.precision',1),(2,'2','mds.form.label.scale',1),(3,'[]','mds.form.label.values',8),(4,'false','mds.form.label.allowUserSupplied',3),(5,'false','mds.form.label.allowMultipleSelections',3);
/*!40000 ALTER TABLE "TypeSetting" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TypeSettingOption"
--

DROP TABLE IF EXISTS "TypeSettingOption";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeSettingOption" (
  "id" bigint(20) NOT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeSettingOption"
--

LOCK TABLES "TypeSettingOption" WRITE;
/*!40000 ALTER TABLE "TypeSettingOption" DISABLE KEYS */;
INSERT INTO "TypeSettingOption" VALUES (1,'REQUIRE'),(2,'POSITIVE');
/*!40000 ALTER TABLE "TypeSettingOption" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TypeValidation"
--

DROP TABLE IF EXISTS "TypeValidation";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeValidation" (
  "id" bigint(20) NOT NULL,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "TYPE_ID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "TypeValidation_N49" ("TYPE_ID"),
  CONSTRAINT "TypeValidation_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeValidation"
--

LOCK TABLES "TypeValidation" WRITE;
/*!40000 ALTER TABLE "TypeValidation" DISABLE KEYS */;
INSERT INTO "TypeValidation" VALUES (1,'mds.field.validation.minValue',1),(2,'mds.field.validation.maxValue',1),(3,'mds.field.validation.mustBeInSet',1),(4,'mds.field.validation.cannotBeInSet',1),(5,'mds.field.validation.regex',2),(6,'mds.field.validation.minLength',1),(7,'mds.field.validation.maxLength',1),(8,'mds.field.validation.minValue',7),(9,'mds.field.validation.maxValue',7),(10,'mds.field.validation.mustBeInSet',7),(11,'mds.field.validation.cannotBeInSet',7);
/*!40000 ALTER TABLE "TypeValidation" ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-10 11:03:05
