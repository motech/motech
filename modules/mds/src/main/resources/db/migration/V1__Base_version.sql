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
-- Table structure for table "AvailableFieldType"
--

DROP TABLE IF EXISTS "AvailableFieldType";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "AvailableFieldType" (
  "id" bigint(20) NOT NULL,
  "defaultName" varchar(255) DEFAULT NULL,
  "description" varchar(255) DEFAULT NULL,
  "displayName" varchar(255) DEFAULT NULL,
  "typeClass" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "AvailableFieldType_U1" ("displayName")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "AvailableFieldType"
--

LOCK TABLES "AvailableFieldType" WRITE;
/*!40000 ALTER TABLE "AvailableFieldType" DISABLE KEYS */;
INSERT INTO "AvailableFieldType" VALUES (1,'int','mds.field.description.integer','mds.field.integer','java.lang.Integer'),(2,'string','mds.field.description.string','mds.field.string','java.lang.String'),(3,'bool','mds.field.description.boolean','mds.field.boolean','java.lang.Boolean'),(4,'date','mds.field.description.date','mds.field.date','java.util.Date'),(5,'time','mds.field.description.time','mds.field.time','org.motechproject.commons.date.model.Time'),(6,'dateTime','mds.field.description.datetime','mds.field.datetime','org.joda.time.DateTime'),(7,'double','mds.field.description.decimal','mds.field.decimal','java.lang.Double'),(8,'list','mds.field.description.combobox','mds.field.combobox','java.util.List');
/*!40000 ALTER TABLE "AvailableFieldType" ENABLE KEYS */;
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
  "namespace" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "DISCRIMINATOR" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "changesMade" bit(1) DEFAULT NULL,
  "draftOwnerUsername" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "lastModificationDate" datetime DEFAULT NULL,
  "parentEntity_id_OID" bigint(20) DEFAULT NULL,
  "parentVersion" bigint(20) DEFAULT NULL,
  "drafts_INTEGER_IDX" int(11) DEFAULT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
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
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "required" bit(1) NOT NULL,
  "tooltip" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "type_id_OID" bigint(20) DEFAULT NULL,
  "validation_id_OID" bigint(20) DEFAULT NULL,
  "fields_INTEGER_IDX" int(11) DEFAULT NULL,
  "tracked" bit(1) NOT NULL,
  "exposedViaRest" bit(1) NOT NULL,
  "uiDisplayable" bit(1) NOT NULL,
  "uiFilterable" bit(1) NOT NULL,
  PRIMARY KEY ("id"),
  KEY "Field_N49" ("type_id_OID"),
  KEY "Field_N50" ("entity_id_OID"),
  KEY "Field_N51" ("validation_id_OID"),
  CONSTRAINT "Field_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "AvailableFieldType" ("id"),
  CONSTRAINT "Field_FK2" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id"),
  CONSTRAINT "Field_FK3" FOREIGN KEY ("validation_id_OID") REFERENCES "TypeValidation" ("id")
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
INSERT INTO "SEQUENCE_TABLE" VALUES ('org.motechproject.mds.domain.TypeValidation',11),('org.motechproject.mds.domain.ValidationCriterion',21);
/*!40000 ALTER TABLE "SEQUENCE_TABLE" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "SettingOptions"
--

DROP TABLE IF EXISTS "SettingOptions";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "SettingOptions" (
  "id" bigint(20) NOT NULL,
  "name" varchar(255) DEFAULT NULL,
  "settingId" bigint(20) DEFAULT NULL,
  "settingOptions_INTEGER_IDX" int(11) DEFAULT NULL,
  "typeSettings_id_OID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "settingId" ("settingId"),
  KEY "SettingOptions_N49" ("typeSettings_id_OID"),
  CONSTRAINT "SettingOptions_FK1" FOREIGN KEY ("typeSettings_id_OID") REFERENCES "TypeSettings" ("id"),
  CONSTRAINT "SettingOptions_ibfk_1" FOREIGN KEY ("settingId") REFERENCES "TypeSettings" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "SettingOptions"
--

LOCK TABLES "SettingOptions" WRITE;
/*!40000 ALTER TABLE "SettingOptions" DISABLE KEYS */;
INSERT INTO "SettingOptions" VALUES (1,'REQUIRE',1,NULL,NULL),(2,'POSITIVE',1,NULL,NULL),(3,'REQUIRE',2,NULL,NULL),(4,'POSITIVE',2,NULL,NULL),(5,'REQUIRE',3,NULL,NULL);
/*!40000 ALTER TABLE "SettingOptions" ENABLE KEYS */;
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
-- Table structure for table "TypeSettings"
--

DROP TABLE IF EXISTS "TypeSettings";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeSettings" (
  "id" bigint(20) NOT NULL,
  "name" varchar(255) DEFAULT NULL,
  "value" varchar(255) DEFAULT NULL,
  "valueType" bigint(20) DEFAULT NULL,
  "type" bigint(20) DEFAULT NULL,
  "typeSettings_INTEGER_IDX" int(11) DEFAULT NULL,
  "valueType_id_OID" bigint(20) DEFAULT NULL,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "type_id_OID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "valueType" ("valueType"),
  KEY "type" ("type"),
  KEY "TypeSettings_N51" ("field_id_OID"),
  KEY "TypeSettings_N50" ("type_id_OID"),
  KEY "TypeSettings_N49" ("valueType_id_OID"),
  CONSTRAINT "TypeSettings_FK3" FOREIGN KEY ("valueType_id_OID") REFERENCES "AvailableFieldType" ("id"),
  CONSTRAINT "TypeSettings_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "AvailableFieldType" ("id"),
  CONSTRAINT "TypeSettings_FK2" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id"),
  CONSTRAINT "TypeSettings_ibfk_1" FOREIGN KEY ("valueType") REFERENCES "AvailableFieldType" ("id"),
  CONSTRAINT "TypeSettings_ibfk_2" FOREIGN KEY ("type") REFERENCES "AvailableFieldType" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeSettings"
--

LOCK TABLES "TypeSettings" WRITE;
/*!40000 ALTER TABLE "TypeSettings" DISABLE KEYS */;
INSERT INTO "TypeSettings" VALUES (1,'mds.form.label.precision','9',1,7,NULL,NULL,NULL,NULL),(2,'mds.form.label.scale','2',1,7,NULL,NULL,NULL,NULL),(3,'mds.form.label.values',NULL,3,8,NULL,NULL,NULL,NULL),(4,'mds.form.label.allowUserSupplied','false',3,8,NULL,NULL,NULL,NULL),(5,'mds.form.label.allowMultipleSelections','false',3,8,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE "TypeSettings" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TypeValidation"
--

DROP TABLE IF EXISTS "TypeValidation";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeValidation" (
  "id" bigint(20) NOT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "type" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "TypeValidation_N49" ("type"),
  CONSTRAINT "TypeValidation_FK1" FOREIGN KEY ("type") REFERENCES "AvailableFieldType" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeValidation"
--

LOCK TABLES "TypeValidation" WRITE;
/*!40000 ALTER TABLE "TypeValidation" DISABLE KEYS */;
INSERT INTO "TypeValidation" VALUES (1,'int',1),(2,'double',7),(3,'string',2);
/*!40000 ALTER TABLE "TypeValidation" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "ValidationCriterion"
--

DROP TABLE IF EXISTS "ValidationCriterion";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "ValidationCriterion" (
  "id" bigint(20) NOT NULL,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "enabled" bit(1) NOT NULL,
  "type_id_OID" bigint(20) DEFAULT NULL,
  "validation_id_OID" bigint(20) DEFAULT NULL,
  "value" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "criteria_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "ValidationCriterion_N49" ("validation_id_OID"),
  KEY "ValidationCriterion_N50" ("type_id_OID"),
  CONSTRAINT "ValidationCriterion_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "AvailableFieldType" ("id"),
  CONSTRAINT "ValidationCriterion_FK2" FOREIGN KEY ("validation_id_OID") REFERENCES "TypeValidation" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "ValidationCriterion"
--

LOCK TABLES "ValidationCriterion" WRITE;
/*!40000 ALTER TABLE "ValidationCriterion" DISABLE KEYS */;
INSERT INTO "ValidationCriterion" VALUES (1,'mds.field.validation.minValue','\0',1,1,'',0),(2,'mds.field.validation.maxValue','\0',1,1,'',1),(3,'mds.field.validation.mustBeInSet','\0',2,1,'',2),(4,'mds.field.validation.cannotBeInSet','\0',2,1,'',3),(5,'mds.field.validation.minValue','\0',1,2,'',0),(6,'mds.field.validation.maxValue','\0',1,2,'',1),(7,'mds.field.validation.mustBeInSet','\0',2,2,'',2),(8,'mds.field.validation.cannotBeInSet','\0',2,2,'',3),(9,'mds.field.validation.regex','\0',2,3,'',0),(10,'mds.field.validation.minLength','\0',1,3,'',1),(11,'mds.field.validation.maxLength','\0',1,3,'',2);
/*!40000 ALTER TABLE "ValidationCriterion" ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-08  8:01:00
