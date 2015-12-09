/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO,ANSI' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

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
  "securityMode" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
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
-- Table structure for table "EntityAudit"
--

DROP TABLE IF EXISTS "EntityAudit";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "EntityAudit" (
  "id" bigint(20) NOT NULL,
  "modificationDate" datetime DEFAULT NULL,
  "ownerUsername" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "version" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "EntityAudit_FK1" FOREIGN KEY ("id") REFERENCES "Entity" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "EntityAudit"
--

LOCK TABLES "EntityAudit" WRITE;
/*!40000 ALTER TABLE "EntityAudit" DISABLE KEYS */;
/*!40000 ALTER TABLE "EntityAudit" ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table "Entity_securityMembers"
--

DROP TABLE IF EXISTS "Entity_securityMembers";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "Entity_securityMembers" (
  "Entity_OID" bigint(20) NOT NULL,
  "SecurityMember" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY ("Entity_OID","SecurityMember"),
  KEY "Entity_securityMembers_N49" ("Entity_OID"),
  CONSTRAINT "Entity_securityMembers_FK1" FOREIGN KEY ("Entity_OID") REFERENCES "Entity" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "Entity_securityMembers"
--

LOCK TABLES "Entity_securityMembers" WRITE;
/*!40000 ALTER TABLE "Entity_securityMembers" DISABLE KEYS */;
/*!40000 ALTER TABLE "Entity_securityMembers" ENABLE KEYS */;
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
  "uiDisplayPosition" bigint(20) DEFAULT NULL,
  "readOnly" bit(1) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "ENTITY_FIELDNAME_IDX" ("entity_id_OID","name"),
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
  "readOnly" bit(1) NOT NULL,
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
-- Table structure for table "LookupFields"
--

DROP TABLE IF EXISTS "LookupFields";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "LookupFields" (
  "id_OID" bigint(20) NOT NULL,
  "id_EID" bigint(20) NOT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  KEY "LookupFields_N49" ("id_OID"),
  KEY "LookupFields_N50" ("id_EID"),
  CONSTRAINT "LookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id"),
  CONSTRAINT "LookupFields_FK2" FOREIGN KEY ("id_EID") REFERENCES "Field" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "LookupFields"
--

LOCK TABLES "LookupFields" WRITE;
/*!40000 ALTER TABLE "LookupFields" DISABLE KEYS */;
/*!40000 ALTER TABLE "LookupFields" ENABLE KEYS */;
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
INSERT INTO "SEQUENCE_TABLE" VALUES ('org.motechproject.mds.domain.Type',12),('org.motechproject.mds.domain.TypeSetting',11),('org.motechproject.mds.domain.TypeSettingOption',11),('org.motechproject.mds.domain.TypeValidation',21);
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
  "defaultName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "typeClass" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "Type"
--

LOCK TABLES "Type" WRITE;
/*!40000 ALTER TABLE "Type" DISABLE KEYS */;
INSERT INTO "Type" VALUES (1,'mds.field.description.integer','mds.field.integer','integer','java.lang.Integer'),(2,'mds.field.description.string','mds.field.string','str','java.lang.String'),(3,'mds.field.description.boolean','mds.field.boolean','bool','java.lang.Boolean'),(4,'mds.field.description.date','mds.field.date','date','java.util.Date'),(5,'mds.field.description.time','mds.field.time','time','org.motechproject.commons.date.model.Time'),(6,'mds.field.description.datetime','mds.field.datetime','datetime','org.joda.time.DateTime'),(7,'mds.field.description.decimal','mds.field.decimal','dec','java.lang.Double'),(8,'mds.field.description.combobox','mds.field.combobox','list','java.util.List'),(9,'mds.field.description.long','mds.field.long','longName','java.lang.Long');
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

--
-- Table structure for table "TypeValidation_annotations"
--

DROP TABLE IF EXISTS "TypeValidation_annotations";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE "TypeValidation_annotations" (
  "id_OID" bigint(20) NOT NULL,
  "ANNOTATION" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  KEY "TypeValidation_annotations_N49" ("id_OID"),
  CONSTRAINT "TypeValidation_annotations_FK1" FOREIGN KEY ("id_OID") REFERENCES "TypeValidation" ("id")
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table "TypeValidation_annotations"
--

LOCK TABLES "TypeValidation_annotations" WRITE;
/*!40000 ALTER TABLE "TypeValidation_annotations" DISABLE KEYS */;
INSERT INTO "TypeValidation_annotations" VALUES (1,'javax.validation.constraints.DecimalMin',0),(1,'javax.validation.constraints.Min',1),(2,'javax.validation.constraints.DecimalMax',0),(2,'javax.validation.constraints.Max',1),(3,'org.motechproject.mds.annotations.InSet',0),(4,'org.motechproject.mds.annotations.NotInSet',0),(5,'javax.validation.constraints.Pattern',0),(6,'javax.validation.constraints.DecimalMin',0),(6,'javax.validation.constraints.Size',1),(7,'javax.validation.constraints.DecimalMax',0),(7,'javax.validation.constraints.Size',1),(8,'javax.validation.constraints.DecimalMin',0),(8,'javax.validation.constraints.Min',1),(9,'javax.validation.constraints.DecimalMax',0),(9,'javax.validation.constraints.Max',1),(10,'org.motechproject.mds.annotations.InSet',0),(11,'org.motechproject.mds.annotations.NotInSet',0);
/*!40000 ALTER TABLE "TypeValidation_annotations" ENABLE KEYS */;
UNLOCK TABLES;

-- Old V2

INSERT INTO "Type" VALUES (10,'mds.field.description.map','mds.field.map','mapName','java.util.Map');

-- Old V3

--
-- Change value type for InSet and NotInSet validations
--

UPDATE TypeValidation
SET TYPE_ID = (SELECT id
               FROM Type
               WHERE typeClass LIKE 'java.lang.String')
WHERE displayName LIKE 'mds.field.validation.mustBeInSet';

UPDATE TypeValidation
SET TYPE_ID = (SELECT id
               FROM Type
               WHERE typeClass LIKE 'java.lang.String')
WHERE displayName LIKE 'mds.field.validation.cannotBeInSet';

-- Old V4

--
-- Merge Integer and Long types into one type named Integer but handled by Long type class
--

-- First let's change the class responsible for handling the Integer type
UPDATE Type
SET typeClass = 'java.lang.Long'
WHERE typeClass LIKE 'java.lang.Integer';

-- Assign all fields of long type to integer type
UPDATE Field
SET type_id_OID = (SELECT id
                   FROM Type
                   WHERE displayName LIKE 'mds.field.integer')
WHERE type_id_OID = (SELECT id
                     FROM Type
                     WHERE displayName LIKE 'mds.field.long');

-- Now we can remove the Long type from the database
DELETE FROM Type
WHERE displayName LIKE 'mds.field.long';

-- Old V5

--
-- Add new type to MDS - org.joda.time.Period
--

INSERT INTO "Type" VALUES (11,'mds.field.description.period','mds.field.period','period','org.joda.time.Period');


-- Old V6

--
-- add locale type to the MDS
--

INSERT INTO Type
SELECT id + 1, 'mds.field.description.locale','mds.field.locale','locale','java.util.Locale'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

-- Old V7


INSERT INTO "Type" VALUES (13,'mds.field.description.blob','mds.field.blob','blobName','[Ljava.lang.Byte;');


-- Old V8


--
-- Add new type to MDS
--

INSERT INTO "Type" VALUES (14,'mds.field.description.localDate','mds.field.localDate','localDateName','org.joda.time.LocalDate');


-- Old V9

--
-- Bring back the Integer handler type to MDS
--

-- First let's change the class responsible for handling the Integer type
UPDATE Type
SET typeClass = 'java.lang.Integer'
WHERE typeClass LIKE 'java.lang.Long';

-- Create a separate class for handling Long type
INSERT INTO Type
SELECT id + 1, 'mds.field.description.long','mds.field.long','long','java.lang.Long'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

-- The handler class for existing fields must stay the same (Long)
-- Assign fields of the Integer type to the Long type
UPDATE Field
SET type_id_OID = (SELECT id
                   FROM Type
                   WHERE displayName LIKE 'mds.field.long')
WHERE type_id_OID = (SELECT id
                     FROM Type
                     WHERE displayName LIKE 'mds.field.integer');

-- Old V10

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship','mds.field.relationship','relationshipName','org.motechproject.mds.domain.Relationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.oneToMany','mds.field.relationship.oneToMany','oneToManyRelationshipName','org.motechproject.mds.domain.OneToManyRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

-- Old V11

--
-- insert settings for relationship types
--

INSERT INTO TypeSetting
SELECT (ts.id + 1), 'true', 'mds.form.label.cascadePersist', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Boolean'
ORDER BY ts.id DESC
LIMIT 1;

INSERT INTO TypeSetting
SELECT (ts.id + 1), 'true', 'mds.form.label.cascadeUpdate', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Boolean'
ORDER BY ts.id DESC
LIMIT 1;

INSERT INTO TypeSetting
SELECT (ts.id + 1), 'false', 'mds.form.label.cascadeDelete', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Boolean'
ORDER BY ts.id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM TypeSetting ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.TypeSetting';

--
-- connect settings with relationship types
--

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.Relationship' AND ts.name LIKE 'mds.form.label.cascadePersist';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.Relationship' AND ts.name LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 2
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.Relationship' AND ts.name LIKE 'mds.form.label.cascadeDelete';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts.name LIKE 'mds.form.label.cascadePersist';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts.name LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 2
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts.name LIKE 'mds.form.label.cascadeDelete';

-- Old V12

--
-- insert settings for string type
--

INSERT INTO TypeSetting
SELECT (ts.id + 1), 'false', 'mds.form.label.textarea', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Boolean'
ORDER BY ts.id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM TypeSetting ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.TypeSetting';

--
-- connect settings with string types
--

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'java.lang.String' AND ts.name LIKE 'mds.form.label.textarea';

-- Old V13

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.oneToOne','mds.field.relationship.oneToOne','oneToOneRelationshipName','org.motechproject.mds.domain.OneToOneRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadePersist';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 2
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadeDelete';

-- Old V14

ALTER TABLE "FieldSetting" DROP COLUMN "value";
ALTER TABLE "FieldSetting" ADD COLUMN "value" TEXT DEFAULT NULL;

-- Old V15

-- This migrations adds the length setting to the String type
--
-- insert settings for string type
--

INSERT INTO TypeSetting
SELECT (ts.id + 1), '255', 'mds.form.label.maxTextLength', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Integer'
ORDER BY ts.id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM TypeSetting ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.TypeSetting';

--
-- connect settings with string types
--

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'java.lang.String' AND ts.name LIKE 'mds.form.label.maxTextLength';

--
-- add validations to the setting
--

INSERT INTO TYPE_SETTING_SETTING_OPTION
SELECT ts.id, 1, 0
FROM TypeSetting ts
WHERE ts.name LIKE 'mds.form.label.maxTextLength'
LIMIT 1;

INSERT INTO TYPE_SETTING_SETTING_OPTION
SELECT ts.id, 2, 1
FROM TypeSetting ts
WHERE ts.name LIKE 'mds.form.label.maxTextLength'
LIMIT 1;

-- Old V16

-- Adjusts default names for some types.
-- We should be consistent with these names.
--

UPDATE Type
SET defaultName = 'map'
WHERE defaultName = 'mapName';

UPDATE Type
SET defaultName = 'blob'
WHERE defaultName = 'blobName';

UPDATE Type
SET defaultName = 'localDate'
WHERE defaultName = 'localDateName';

UPDATE Type
SET defaultName = 'relationship'
WHERE defaultName = 'relationshipName';

UPDATE Type
SET defaultName = 'oneToManyRelationship'
WHERE defaultName = 'oneToManyRelationshipName';

UPDATE Type
SET defaultName = 'oneToOneRelationship'
WHERE defaultName = 'oneToOneRelationshipName';


-- Old V17

-- Adds ManyToOne relationship type ---

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.manyToOne','mds.field.relationship.manyToOne','manyToOneRelationship','org.motechproject.mds.domain.ManyToOneRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadePersist';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 2
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadeDelete';

-- Old V18

-- Adds ManyToMany relationship type ---

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.manyToMany','mds.field.relationship.manyToMany','manyToManyRelationship','org.motechproject.mds.domain.ManyToManyRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts.name LIKE 'mds.form.label.cascadePersist';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts.name LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 2
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts.name LIKE 'mds.form.label.cascadeDelete';

-- Old V19

-- This migration changes length of the value column in FieldValidation ---

ALTER TABLE FieldValidation MODIFY value VARCHAR(1024);

-- Old V20

-- adds recordHistory column ---

ALTER TABLE Tracking add recordHistory bit(1) NOT NULL;

-- Old V21

-- clean up auditing columns ---

ALTER TABLE Field DROP COLUMN tracked;

ALTER TABLE Tracking DROP COLUMN allowCreate;
ALTER TABLE Tracking DROP COLUMN allowRead;
ALTER TABLE Tracking DROP COLUMN allowUpdate;
ALTER TABLE Tracking DROP COLUMN allowDelete;

-- Old V22
-- adds crudEvents columns ---

ALTER TABLE Tracking add allowCreateEvent bit(1) NOT NULL default 0;
ALTER TABLE Tracking add allowDeleteEvent bit(1) NOT NULL default 0;
ALTER TABLE Tracking add allowUpdateEvent bit(1) NOT NULL default 0;

-- Old V23

-- Rename Date to java.util.Date and LocalDate to Date

UPDATE Type
SET displayName = 'mds.field.javaUtilDate'
WHERE typeClass LIKE 'java.util.Date';

UPDATE Type
SET displayName = 'mds.field.date',
defaultName = 'date'
WHERE typeClass LIKE 'org.joda.time.LocalDate';
-- Old V24

-- adds tableName column ---

ALTER TABLE Entity ADD tableName varchar(255) DEFAULT NULL;

-- Old V25

-- change default value of the MDS CRUD events and add new flag ---

ALTER TABLE Tracking ALTER allowCreateEvent SET DEFAULT 1;
ALTER TABLE Tracking ALTER allowDeleteEvent SET DEFAULT 1;
ALTER TABLE Tracking ALTER allowUpdateEvent SET DEFAULT 1;
ALTER TABLE Tracking add modifiedByUser bit(1) NOT NULL default 0;

-- Old V26
-- add new flag to RestOptions---

ALTER TABLE RestOptions add modifiedByUser bit(1) NOT NULL default 0;

-- Old V27

CREATE TABLE RestDocs(
    id bigint(20) PRIMARY KEY,
    documentation mediumtext
);
-- Old V28
CREATE TABLE IF NOT EXISTS SchemaChangeLock(
    id bigint(20) PRIMARY KEY,
    lockId int(1) UNIQUE
);

INSERT IGNORE INTO SchemaChangeLock VALUES (1, 1);

-- Old V29
DROP TABLE IF EXISTS RestDocs;

-- Old V30

-- conditionally adds maxFetchDepth column ---

DROP PROCEDURE IF EXISTS addMaxFetchDepth;

DELIMITER //
CREATE PROCEDURE addMaxFetchDepth()
BEGIN

IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'Entity' AND column_name = 'maxFetchDepth') THEN

ALTER TABLE Entity ADD maxFetchDepth bigint(20) DEFAULT NULL;

END IF;

END//

DELIMITER ;

CALL addMaxFetchDepth();

DROP PROCEDURE IF EXISTS addMaxFetchDepth;

-- Old V31

-- adds securityOptionsModified column ---

ALTER TABLE Entity add securityOptionsModified bit(1) NOT NULL default 0;

-- Old V32
-- adds nonEditable column ---

DELIMITER $$
CREATE PROCEDURE insert_non_editable_column()
BEGIN
    IF NOT EXISTS (SELECT column_name
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_schema = DATABASE()
            AND table_name = 'Field'
            AND column_name = 'nonEditable')
    THEN
        ALTER TABLE Field add nonEditable bit(1) NOT NULL default 0;
    END IF;
END
$$

DELIMITER ;
CALL insert_non_editable_column();
DROP PROCEDURE insert_non_editable_column;

-- Old V33

-- Spring Migration!!!

-- Old V34

-- adds nonDisplayable column ---

DELIMITER $$
CREATE PROCEDURE insert_non_displayable_column()
BEGIN
    IF NOT EXISTS (SELECT column_name
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_schema = DATABASE()
            AND table_name = 'Field'
            AND column_name = 'nonDisplayable')
    THEN
        ALTER TABLE Field add nonDisplayable bit(1) NOT NULL default 0;
    END IF;
END
$$

DELIMITER ;
CALL insert_non_displayable_column();
DROP PROCEDURE insert_non_displayable_column;

-- Old V35

-- adds placeholder column ---

ALTER TABLE Field ADD placeholder varchar(255) DEFAULT NULL;

-- Old V36

CREATE TABLE MigrationMapping (
  flywayMigrationVersion int NOT NULL,
  moduleMigrationVersion int NOT NULL,
  moduleName varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (flywayMigrationVersion)
);
-- Old V37

--
-- Change combobox typeClass and defaultName
--

UPDATE Type
SET typeClass = 'java.util.Collection', defaultName = 'collection'
WHERE displayName LIKE 'mds.field.combobox';

-- Old V38

-- adds nonEditable column ---

ALTER TABLE Tracking add nonEditable bit(1) NOT NULL default 0;

-- Old V39

-- adds fieldsOrder column ---

ALTER TABLE Lookup ADD fieldsOrder MEDIUMBLOB DEFAULT NULL;

-- Old V40

-- add readOnlySecurityMembers table and readOnlySecurityMode column in Entity table --

ALTER TABLE Entity ADD readOnlySecurityMode varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL;

CREATE TABLE IF NOT EXISTS Entity_readOnlySecurityMembers (
  Entity_OID bigint(20) NOT NULL,
  ReadOnlySecurityMember varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (Entity_OID, ReadOnlySecurityMember),
  KEY Entity_readOnlySecurityMembers_N49 (Entity_OID),
  CONSTRAINT Entity_readOnlySecurityMembers_FK1 FOREIGN KEY (Entity_OID) REFERENCES Entity (id)
);

-- Old V41

-- adds bundleSymbolicName column ---

ALTER TABLE Entity ADD bundleSymbolicName varchar(255) DEFAULT NULL;

-- Old V42

-- add support for java.time API
INSERT INTO Type VALUES (21, 'mds.field.description.datetime', 'mds.field.datetime8', 'datetime', 'java.time.LocalDateTime');
INSERT INTO Type VALUES (22, 'mds.field.description.localDate', 'mds.field.date8', 'localDate', 'java.time.LocalDate');
-- Old V43

CREATE TABLE IF NOT EXISTS UserPreferences (
  className varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  gridRowsNumber int,
  PRIMARY KEY (className, username)
);

CREATE TABLE IF NOT EXISTS UserPreferences_selectedFields (
  className_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  selectedField bigint(20),
  IDX int(11),
  PRIMARY KEY (className_OID, username_OID, IDX),
  KEY UserPreferences_selectedFields_N49 (selectedField),
  KEY UserPreferences_selectedFields_N50 (className_OID, username_OID),
  CONSTRAINT UserPreferences_visibleFields_FK1 FOREIGN KEY (className_OID, username_OID) REFERENCES UserPreferences (className, username),
  CONSTRAINT UserPreferences_visibleFields_FK2 FOREIGN KEY (selectedField) REFERENCES Field (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS UserPreferences_unselectedFields (
  className_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  unselectedField bigint(20),
  IDX int(11),
  PRIMARY KEY (className_OID, username_OID, IDX),
  KEY UserPreferences_unselectedFields_N49 (unselectedField),
  KEY UserPreferences_unselectedFields_N50 (className_OID, username_OID),
  CONSTRAINT UserPreferences_unselectedFields_FK1 FOREIGN KEY (className_OID, username_OID) REFERENCES UserPreferences (className, username),
  CONSTRAINT UserPreferences_unselectedFields_FK2 FOREIGN KEY (unselectedField) REFERENCES Field (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ConfigSettings (
  id bigint(20),
  afterTimeUnit varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  afterTimeValue int(11),
  deleteMode varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  emptyTrash bit(1),
  PRIMARY KEY (id)
);

ALTER TABLE ConfigSettings add defaultGridSize int DEFAULT 50;