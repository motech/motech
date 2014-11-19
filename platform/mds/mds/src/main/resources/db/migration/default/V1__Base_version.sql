-- Table structure for table "Entity"
--

DROP TABLE IF EXISTS "Entity";
CREATE TABLE "Entity" (
  "id" bigint NOT NULL,
  "className" varchar(255) DEFAULT NULL,
  "entityVersion" bigint DEFAULT NULL,
  "module" varchar(255) DEFAULT NULL,
  "name" varchar(255) DEFAULT NULL,
  "namespace" varchar(255)  DEFAULT NULL,
  "DISCRIMINATOR" varchar(255) NOT NULL,
  "changesMade" boolean DEFAULT NULL,
  "draftOwnerUsername" varchar(255) DEFAULT NULL,
  "lastModificationDate" timestamp NULL,
  "parentEntity_id_OID" bigint DEFAULT NULL,
  "parentVersion" bigint DEFAULT NULL,
  "drafts_INTEGER_IDX" bigint DEFAULT NULL,
  "securityMode" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "DRAFT_USER_IDX" UNIQUE ("parentEntity_id_OID", "draftOwnerUsername"),
  CONSTRAINT "Entity_FK1" FOREIGN KEY ("parentEntity_id_OID") REFERENCES "Entity" ("id")
);


--
-- Table structure for table "Type"
--

DROP TABLE IF EXISTS "Type";
CREATE TABLE "Type" (
  "id" bigint NOT NULL,
  "description" varchar(255) DEFAULT NULL,
  "displayName" varchar(255) DEFAULT NULL,
  "defaultName" varchar(255) DEFAULT NULL,
  "typeClass" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id")
);

--
-- Dumping data for table "Type"
--

INSERT INTO "Type" VALUES (1,'mds.field.description.integer','mds.field.integer','integer','java.lang.Integer'),(2,'mds.field.description.string','mds.field.string','str','java.lang.String'),(3,'mds.field.description.boolean','mds.field.boolean','bool','java.lang.Boolean'),(4,'mds.field.description.date','mds.field.date','date','java.util.Date'),(5,'mds.field.description.time','mds.field.time','time','org.motechproject.commons.date.model.Time'),(6,'mds.field.description.datetime','mds.field.datetime','datetime','org.joda.time.DateTime'),(7,'mds.field.description.decimal','mds.field.decimal','dec','java.lang.Double'),(8,'mds.field.description.combobox','mds.field.combobox','list','java.util.List'),(9,'mds.field.description.long','mds.field.long','longName','java.lang.Long');

--
-- Table structure for table "TypeSetting"
--

DROP TABLE IF EXISTS "TypeSetting";
CREATE TABLE "TypeSetting" (
  "id" bigint NOT NULL,
  "defaultValue" varchar(255) DEFAULT NULL,
  "name" varchar(255) DEFAULT NULL,
  "TYPE_ID" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "TypeSetting_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);

--
-- Dumping data for table "TypeSetting"
--

INSERT INTO "TypeSetting" VALUES (1,'9','mds.form.label.precision',1),(2,'2','mds.form.label.scale',1),(3,'[]','mds.form.label.values',8),(4,'false','mds.form.label.allowUserSupplied',3),(5,'false','mds.form.label.allowMultipleSelections',3);

--
-- Table structure for table "TypeValidation"
--

DROP TABLE IF EXISTS "TypeValidation";
CREATE TABLE "TypeValidation" (
  "id" bigint NOT NULL,
  "displayName" varchar(255) DEFAULT NULL,
  "TYPE_ID" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "TypeValidation_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);

--
-- Dumping data for table "TypeValidation"
--

INSERT INTO "TypeValidation" VALUES (1,'mds.field.validation.minValue',1),(2,'mds.field.validation.maxValue',1),(3,'mds.field.validation.mustBeInSet',1),(4,'mds.field.validation.cannotBeInSet',1),(5,'mds.field.validation.regex',2),(6,'mds.field.validation.minLength',1),(7,'mds.field.validation.maxLength',1),(8,'mds.field.validation.minValue',7),(9,'mds.field.validation.maxValue',7),(10,'mds.field.validation.mustBeInSet',7),(11,'mds.field.validation.cannotBeInSet',7);

--
-- Table structure for table "TypeSettingOption"
--

DROP TABLE IF EXISTS "TypeSettingOption";
CREATE TABLE "TypeSettingOption" (
  "id" bigint NOT NULL,
  "name" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id")
);

--
-- Dumping data for table "TypeSettingOption"
--

INSERT INTO "TypeSettingOption" VALUES (1,'REQUIRE'),(2,'POSITIVE');

--
-- Table structure for table "EntityAudit"
--

DROP TABLE IF EXISTS "EntityAudit";
CREATE TABLE "EntityAudit" (
  "id" bigint NOT NULL,
  "modificationDate" timestamp NULL,
  "ownerUsername" varchar(255) NOT NULL,
  "version" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "EntityAudit_FK1" FOREIGN KEY ("id") REFERENCES "Entity" ("id")
);

--
-- Table structure for table "Entity_securityMembers"
--

DROP TABLE IF EXISTS "Entity_securityMembers";
CREATE TABLE "Entity_securityMembers" (
  "Entity_OID" bigint NOT NULL,
  "SecurityMember" varchar(255) NOT NULL,
  PRIMARY KEY ("Entity_OID","SecurityMember"),
  CONSTRAINT "Entity_securityMembers_FK1" FOREIGN KEY ("Entity_OID") REFERENCES "Entity" ("id")
);


--
-- Table structure for table "Field"
--

DROP TABLE IF EXISTS "Field";
CREATE TABLE "Field" (
  "id" bigint NOT NULL,
  "defaultValue" varchar(255) DEFAULT NULL,
  "displayName" varchar(255) DEFAULT NULL,
  "entity_id_OID" bigint DEFAULT NULL,
  "exposedViaRest" boolean NOT NULL,
  "name" varchar(255) DEFAULT NULL,
  "required" boolean NOT NULL,
  "tooltip" varchar(255) DEFAULT NULL,
  "tracked" boolean NOT NULL,
  "type_id_OID" bigint DEFAULT NULL,
  "fields_INTEGER_IDX" bigint DEFAULT NULL,
  "uiDisplayable" boolean NOT NULL,
  "uiFilterable" boolean NOT NULL,
  "uiDisplayPosition" bigint DEFAULT NULL,
  "readOnly" boolean NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "ENTITY_FIELDNAME_IDX" UNIQUE ("entity_id_OID","name"),
  CONSTRAINT "Field_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "Field_FK2" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

--
-- Dumping data for table "Field"
--

--
-- Table structure for table "FieldMetadata"
--

DROP TABLE IF EXISTS "FieldMetadata";
CREATE TABLE "FieldMetadata" (
  "id" bigint NOT NULL,
  "field_id_OID" bigint DEFAULT NULL,
  "key" varchar(255) DEFAULT NULL,
  "value" varchar(255) DEFAULT NULL,
  "metadata_INTEGER_IDX" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "FieldMetadata_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);

--
-- Table structure for table "FieldSetting"
--

DROP TABLE IF EXISTS "FieldSetting";
CREATE TABLE "FieldSetting" (
  "id" bigint NOT NULL,
  "DETAILS_ID" bigint DEFAULT NULL,
  "field_id_OID" bigint DEFAULT NULL,
  "value" varchar(255) DEFAULT NULL,
  "settings_INTEGER_IDX" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "FieldSetting_FK2" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeSetting" ("id"),
  CONSTRAINT "FieldSetting_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);

--
-- Table structure for table "FieldValidation"
--

DROP TABLE IF EXISTS "FieldValidation";
CREATE TABLE "FieldValidation" (
  "id" bigint NOT NULL,
  "DETAILS_ID" bigint DEFAULT NULL,
  "enabled" boolean NOT NULL,
  "field_id_OID" bigint DEFAULT NULL,
  "value" varchar(255) DEFAULT NULL,
  "validations_INTEGER_IDX" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "FieldValidation_FK2" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id"),
  CONSTRAINT "FieldValidation_FK1" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeValidation" ("id")
);


--
-- Table structure for table "Lookup"
--

DROP TABLE IF EXISTS "Lookup";
CREATE TABLE "Lookup" (
  "id" bigint NOT NULL,
  "entity_id_OID" bigint DEFAULT NULL,
  "exposedViaRest" boolean NOT NULL,
  "lookupName" varchar(255) DEFAULT NULL,
  "singleObjectReturn" boolean NOT NULL,
  "lookups_INTEGER_IDX" bigint DEFAULT NULL,
  "readOnly" boolean NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "Lookup_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

--
-- Table structure for table "LookupFields"
--

DROP TABLE IF EXISTS "LookupFields";
CREATE TABLE "LookupFields" (
  "id_OID" bigint NOT NULL,
  "id_EID" bigint NOT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  CONSTRAINT "LookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id"),
  CONSTRAINT "LookupFields_FK2" FOREIGN KEY ("id_EID") REFERENCES "Field" ("id")
);

--
-- Table structure for table "RestOptions"
--

DROP TABLE IF EXISTS "RestOptions";
CREATE TABLE "RestOptions" (
  "id" bigint NOT NULL,
  "allowCreate" boolean NOT NULL,
  "allowDelete" boolean NOT NULL,
  "allowRead" boolean NOT NULL,
  "allowUpdate" boolean NOT NULL,
  "entity_id_OID" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "RestOptions_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

--
-- Table structure for table "SEQUENCE_TABLE"
--

DROP TABLE IF EXISTS "SEQUENCE_TABLE";
CREATE TABLE "SEQUENCE_TABLE" (
  "SEQUENCE_NAME" varchar(255) NOT NULL,
  "NEXT_VAL" bigint NOT NULL,
  PRIMARY KEY ("SEQUENCE_NAME")
);

--
-- Dumping data for table "SEQUENCE_TABLE"
--

INSERT INTO "SEQUENCE_TABLE" VALUES ('org.motechproject.mds.domain.Type',12),('org.motechproject.mds.domain.TypeSetting',11),('org.motechproject.mds.domain.TypeSettingOption',11),('org.motechproject.mds.domain.TypeValidation',21);

--
-- Table structure for table "TYPE_SETTING_SETTING_OPTION"
--

DROP TABLE IF EXISTS "TYPE_SETTING_SETTING_OPTION";
CREATE TABLE "TYPE_SETTING_SETTING_OPTION" (
  "TYPE_SETTING_ID_OID" bigint NOT NULL,
  "SETTING_OPTION_ID_EID" bigint DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("TYPE_SETTING_ID_OID","IDX"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK2" FOREIGN KEY ("SETTING_OPTION_ID_EID") REFERENCES "TypeSettingOption" ("id"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK1" FOREIGN KEY ("TYPE_SETTING_ID_OID") REFERENCES "TypeSetting" ("id")
);

--
-- Dumping data for table "TYPE_SETTING_SETTING_OPTION"
--

INSERT INTO "TYPE_SETTING_SETTING_OPTION" VALUES (1,1,0),(2,1,0),(3,1,0),(1,2,1),(2,2,1);

--
-- Table structure for table "TYPE_TYPE_SETTING"
--

DROP TABLE IF EXISTS "TYPE_TYPE_SETTING";
CREATE TABLE "TYPE_TYPE_SETTING" (
  "TYPE_ID_OID" bigint NOT NULL,
  "TYPE_SETTING_ID_EID" bigint DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK2" FOREIGN KEY ("TYPE_SETTING_ID_EID") REFERENCES "TypeSetting" ("id")
);

INSERT INTO "TYPE_TYPE_SETTING" VALUES (7,1,0),(7,2,1),(8,3,0),(8,4,1),(8,5,2);

--
-- Table structure for table "TYPE_TYPE_VALIDATION"
--

DROP TABLE IF EXISTS "TYPE_TYPE_VALIDATION";
CREATE TABLE "TYPE_TYPE_VALIDATION" (
  "TYPE_ID_OID" bigint NOT NULL,
  "TYPE_VALIDATION_ID_EID" bigint DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK2" FOREIGN KEY ("TYPE_VALIDATION_ID_EID") REFERENCES "TypeValidation" ("id"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id")
);

--
-- Dumping data for table "TYPE_TYPE_VALIDATION"
--

INSERT INTO "TYPE_TYPE_VALIDATION" VALUES (1,1,0),(1,2,1),(1,3,2),(1,4,3),(2,5,0),(2,6,1),(2,7,2),(7,8,0),(7,9,1),(7,10,2),(7,11,3);

--
-- Table structure for table "Tracking"
--

DROP TABLE IF EXISTS "Tracking";
CREATE TABLE "Tracking" (
  "id" bigint NOT NULL,
  "allowCreate" boolean NOT NULL,
  "allowDelete" boolean NOT NULL,
  "allowRead" boolean NOT NULL,
  "allowUpdate" boolean NOT NULL,
  "entity_id_OID" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "Tracking_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);


--
-- Table structure for table "TypeValidation_annotations"
--

DROP TABLE IF EXISTS "TypeValidation_annotations";
CREATE TABLE "TypeValidation_annotations" (
  "id_OID" bigint NOT NULL,
  "ANNOTATION" varchar(255) DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  CONSTRAINT "TypeValidation_annotations_FK1" FOREIGN KEY ("id_OID") REFERENCES "TypeValidation" ("id")
);

--
-- Dumping data for table "TypeValidation_annotations"
--

INSERT INTO "TypeValidation_annotations" VALUES (1,'javax.validation.constraints.DecimalMin',0),(1,'javax.validation.constraints.Min',1),(2,'javax.validation.constraints.DecimalMax',0),(2,'javax.validation.constraints.Max',1),(3,'org.motechproject.mds.annotations.InSet',0),(4,'org.motechproject.mds.annotations.NotInSet',0),(5,'javax.validation.constraints.Pattern',0),(6,'javax.validation.constraints.DecimalMin',0),(6,'javax.validation.constraints.Size',1),(7,'javax.validation.constraints.DecimalMax',0),(7,'javax.validation.constraints.Size',1),(8,'javax.validation.constraints.DecimalMin',0),(8,'javax.validation.constraints.Min',1),(9,'javax.validation.constraints.DecimalMax',0),(9,'javax.validation.constraints.Max',1),(10,'org.motechproject.mds.annotations.InSet',0),(11,'org.motechproject.mds.annotations.NotInSet',0);
