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
-- Table structure for table "AvailableFieldTypeMapping"
--

DROP TABLE IF EXISTS "AvailableFieldTypeMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "AvailableFieldTypeMapping" (
  "id" bigint(20) NOT NULL,
  "defaultName" varchar(255) DEFAULT NULL,
  "description" varchar(255) DEFAULT NULL,
  "displayName" varchar(255) DEFAULT NULL,
  "typeClass" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "AvailableFieldTypeMapping_U1" ("displayName")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "AvailableFieldTypeMapping"
--

LOCK TABLES "AvailableFieldTypeMapping" WRITE;
/*!40000 ALTER TABLE "AvailableFieldTypeMapping" DISABLE KEYS */;
INSERT INTO "AvailableFieldTypeMapping" VALUES (1,'int','mds.field.description.integer','mds.field.integer','java.lang.Integer'),(2,'string','mds.field.description.string','mds.field.string','java.lang.String'),(3,'bool','mds.field.description.boolean','mds.field.boolean','java.lang.Boolean'),(4,'date','mds.field.description.date','mds.field.date','java.util.Date'),(5,'time','mds.field.description.time','mds.field.time','org.motechproject.commons.date.model.Time'),(6,'dateTime','mds.field.description.datetime','mds.field.datetime','org.joda.time.DateTime'),(7,'double','mds.field.description.decimal','mds.field.decimal','java.lang.Double'),(8,'list','mds.field.description.combobox','mds.field.combobox','java.util.List');
/*!40000 ALTER TABLE "AvailableFieldTypeMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "EntityMapping"
--

DROP TABLE IF EXISTS "EntityMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "EntityMapping" (
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
  KEY "EntityMapping_N49" ("parentEntity_id_OID"),
  CONSTRAINT "EntityMapping_FK1" FOREIGN KEY ("parentEntity_id_OID") REFERENCES "EntityMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "EntityMapping"
--

LOCK TABLES "EntityMapping" WRITE;
/*!40000 ALTER TABLE "EntityMapping" DISABLE KEYS */;
/*!40000 ALTER TABLE "EntityMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "FieldMapping"
--

DROP TABLE IF EXISTS "FieldMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "FieldMapping" (
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
  "tracking" bit(1) NOT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldMapping_N49" ("type_id_OID"),
  KEY "FieldMapping_N50" ("entity_id_OID"),
  KEY "FieldMapping_N51" ("validation_id_OID"),
  CONSTRAINT "FieldMapping_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "AvailableFieldTypeMapping" ("id"),
  CONSTRAINT "FieldMapping_FK2" FOREIGN KEY ("entity_id_OID") REFERENCES "EntityMapping" ("id"),
  CONSTRAINT "FieldMapping_FK3" FOREIGN KEY ("validation_id_OID") REFERENCES "TypeValidationMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "FieldMapping"
--

LOCK TABLES "FieldMapping" WRITE;
/*!40000 ALTER TABLE "FieldMapping" DISABLE KEYS */;
/*!40000 ALTER TABLE "FieldMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "FieldMetadataMapping"
--

DROP TABLE IF EXISTS "FieldMetadataMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "FieldMetadataMapping" (
  "id" bigint(20) NOT NULL,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "key" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "value" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "metadata_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldMetadataMapping_N49" ("field_id_OID"),
  CONSTRAINT "FieldMetadataMapping_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "FieldMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "FieldMetadataMapping"
--

LOCK TABLES "FieldMetadataMapping" WRITE;
/*!40000 ALTER TABLE "FieldMetadataMapping" DISABLE KEYS */;
/*!40000 ALTER TABLE "FieldMetadataMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "LookupMapping"
--

DROP TABLE IF EXISTS "LookupMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "LookupMapping" (
  "id" bigint(20) NOT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  "exposedViaRest" bit(1) NOT NULL,
  "lookupName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "singleObjectReturn" bit(1) NOT NULL,
  "lookups_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "LookupMapping_N49" ("entity_id_OID"),
  CONSTRAINT "LookupMapping_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "EntityMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "LookupMapping"
--

LOCK TABLES "LookupMapping" WRITE;
/*!40000 ALTER TABLE "LookupMapping" DISABLE KEYS */;
/*!40000 ALTER TABLE "LookupMapping" ENABLE KEYS */;
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
INSERT INTO "SEQUENCE_TABLE" VALUES ('org.motechproject.mds.domain.TypeValidationMapping',11),('org.motechproject.mds.domain.ValidationCriterionMapping',21);
/*!40000 ALTER TABLE "SEQUENCE_TABLE" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "SettingOptionsMapping"
--

DROP TABLE IF EXISTS "SettingOptionsMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "SettingOptionsMapping" (
  "id" bigint(20) NOT NULL,
  "name" varchar(255) DEFAULT NULL,
  "settingId" bigint(20) DEFAULT NULL,
  "settingOptions_INTEGER_IDX" int(11) DEFAULT NULL,
  "typeSettings_id_OID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "settingId" ("settingId"),
  KEY "SettingOptionsMapping_N49" ("typeSettings_id_OID"),
  CONSTRAINT "SettingOptionsMapping_FK1" FOREIGN KEY ("typeSettings_id_OID") REFERENCES "TypeSettingsMapping" ("id"),
  CONSTRAINT "SettingOptionsMapping_ibfk_1" FOREIGN KEY ("settingId") REFERENCES "TypeSettingsMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "SettingOptionsMapping"
--

LOCK TABLES "SettingOptionsMapping" WRITE;
/*!40000 ALTER TABLE "SettingOptionsMapping" DISABLE KEYS */;
INSERT INTO "SettingOptionsMapping" VALUES (1,'REQUIRE',1,NULL,NULL),(2,'POSITIVE',1,NULL,NULL),(3,'REQUIRE',2,NULL,NULL),(4,'POSITIVE',2,NULL,NULL),(5,'REQUIRE',3,NULL,NULL);
/*!40000 ALTER TABLE "SettingOptionsMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TrackingMapping"
--

DROP TABLE IF EXISTS "TrackingMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TrackingMapping" (
  "id" bigint(20) NOT NULL,
  "allowCreate" bit(1) NOT NULL,
  "allowDelete" bit(1) NOT NULL,
  "allowRead" bit(1) NOT NULL,
  "allowUpdate" bit(1) NOT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "TrackingMapping_N49" ("entity_id_OID"),
  CONSTRAINT "TrackingMapping_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "EntityMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TrackingMapping"
--

LOCK TABLES "TrackingMapping" WRITE;
/*!40000 ALTER TABLE "TrackingMapping" DISABLE KEYS */;
/*!40000 ALTER TABLE "TrackingMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TypeSettingsMapping"
--

DROP TABLE IF EXISTS "TypeSettingsMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeSettingsMapping" (
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
  KEY "TypeSettingsMapping_N51" ("field_id_OID"),
  KEY "TypeSettingsMapping_N50" ("type_id_OID"),
  KEY "TypeSettingsMapping_N49" ("valueType_id_OID"),
  CONSTRAINT "TypeSettingsMapping_FK3" FOREIGN KEY ("valueType_id_OID") REFERENCES "AvailableFieldTypeMapping" ("id"),
  CONSTRAINT "TypeSettingsMapping_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "AvailableFieldTypeMapping" ("id"),
  CONSTRAINT "TypeSettingsMapping_FK2" FOREIGN KEY ("field_id_OID") REFERENCES "FieldMapping" ("id"),
  CONSTRAINT "TypeSettingsMapping_ibfk_1" FOREIGN KEY ("valueType") REFERENCES "AvailableFieldTypeMapping" ("id"),
  CONSTRAINT "TypeSettingsMapping_ibfk_2" FOREIGN KEY ("type") REFERENCES "AvailableFieldTypeMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeSettingsMapping"
--

LOCK TABLES "TypeSettingsMapping" WRITE;
/*!40000 ALTER TABLE "TypeSettingsMapping" DISABLE KEYS */;
INSERT INTO "TypeSettingsMapping" VALUES (1,'mds.form.label.precision','9',1,7,NULL,NULL,NULL,NULL),(2,'mds.form.label.scale','2',1,7,NULL,NULL,NULL,NULL),(3,'mds.form.label.values',NULL,3,8,NULL,NULL,NULL,NULL),(4,'mds.form.label.allowUserSupplied','false',3,8,NULL,NULL,NULL,NULL),(5,'mds.form.label.allowMultipleSelections','false',3,8,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE "TypeSettingsMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "TypeValidationMapping"
--

DROP TABLE IF EXISTS "TypeValidationMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeValidationMapping" (
  "id" bigint(20) NOT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "type" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "TypeValidationMapping_N49" ("type"),
  CONSTRAINT "TypeValidationMapping_FK1" FOREIGN KEY ("type") REFERENCES "AvailableFieldTypeMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeValidationMapping"
--

LOCK TABLES "TypeValidationMapping" WRITE;
/*!40000 ALTER TABLE "TypeValidationMapping" DISABLE KEYS */;
INSERT INTO "TypeValidationMapping" VALUES (1,'int',1),(2,'double',7),(3,'string',2);
/*!40000 ALTER TABLE "TypeValidationMapping" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "ValidationCriterionMapping"
--

DROP TABLE IF EXISTS "ValidationCriterionMapping";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "ValidationCriterionMapping" (
  "id" bigint(20) NOT NULL,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "enabled" bit(1) NOT NULL,
  "type_id_OID" bigint(20) DEFAULT NULL,
  "validation_id_OID" bigint(20) DEFAULT NULL,
  "value" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "criteria_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "ValidationCriterionMapping_N49" ("validation_id_OID"),
  KEY "ValidationCriterionMapping_N50" ("type_id_OID"),
  CONSTRAINT "ValidationCriterionMapping_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "AvailableFieldTypeMapping" ("id"),
  CONSTRAINT "ValidationCriterionMapping_FK2" FOREIGN KEY ("validation_id_OID") REFERENCES "TypeValidationMapping" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "ValidationCriterionMapping"
--

LOCK TABLES "ValidationCriterionMapping" WRITE;
/*!40000 ALTER TABLE "ValidationCriterionMapping" DISABLE KEYS */;
INSERT INTO "ValidationCriterionMapping" VALUES (1,'mds.field.validation.minValue','\0',1,1,'',0),(2,'mds.field.validation.maxValue','\0',1,1,'',1),(3,'mds.field.validation.mustBeInSet','\0',2,1,'',2),(4,'mds.field.validation.cannotBeInSet','\0',2,1,'',3),(5,'mds.field.validation.minValue','\0',1,2,'',0),(6,'mds.field.validation.maxValue','\0',1,2,'',1),(7,'mds.field.validation.mustBeInSet','\0',2,2,'',2),(8,'mds.field.validation.cannotBeInSet','\0',2,2,'',3),(9,'mds.field.validation.regex','\0',2,3,'',0),(10,'mds.field.validation.minLength','\0',1,3,'',1),(11,'mds.field.validation.maxLength','\0',1,3,'',2);
/*!40000 ALTER TABLE "ValidationCriterionMapping" ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-05  9:43:10
