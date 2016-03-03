/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO,ANSI' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS "Entity";
CREATE TABLE "Entity" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
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
  "tableName" varchar(255) DEFAULT NULL,
  "maxFetchDepth" bigint(20) DEFAULT NULL,
  "securityOptionsModified" bit(1) NOT NULL default 0,
  "bundleSymbolicName" varchar(255) DEFAULT NULL,
  "readOnlySecurityMode" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "DRAFT_USER_IDX" ("parentEntity_id_OID","draftOwnerUsername"),
  KEY "Entity_KeyIdx1" ("parentEntity_id_OID"),
  CONSTRAINT "Entity_FK1" FOREIGN KEY ("parentEntity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "EntityAudit";
CREATE TABLE "EntityAudit" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "modificationDate" datetime DEFAULT NULL,
  "ownerUsername" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "version" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "EntityAudit_FK1" FOREIGN KEY ("id") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "Entity_securityMembers";
CREATE TABLE "Entity_securityMembers" (
  "Entity_OID" bigint(20) NOT NULL,
  "SecurityMember" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY ("Entity_OID","SecurityMember"),
  KEY "Entity_securityMembers_KeyIdx1" ("Entity_OID"),
  CONSTRAINT "Entity_securityMembers_FK1" FOREIGN KEY ("Entity_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "EntityDraft_fieldNameChanges";
CREATE TABLE "EntityDraft_fieldNameChanges" (
  "id_OID" bigint(20) NOT NULL,
  "oldName" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  "newName" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY ("id_OID", "oldName"),
  KEY "EntityDraft_fieldNameChanges_KeyIdx1" ("id_OID"),
  CONSTRAINT "EntityDraft_fieldNameChanges_FK1" FOREIGN KEY ("id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "Field";
CREATE TABLE "Field" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "defaultValue" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  "exposedViaRest" bit(1) NOT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "required" bit(1) NOT NULL,
  "tooltip" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "type_id_OID" bigint(20) DEFAULT NULL,
  "fields_INTEGER_IDX" int(11) DEFAULT NULL,
  "uiDisplayable" bit(1) NOT NULL,
  "uiFilterable" bit(1) NOT NULL,
  "uiDisplayPosition" bigint(20) DEFAULT NULL,
  "readOnly" bit(1) NOT NULL,
  "nonEditable" bit(1) NOT NULL default 0,
  "nonDisplayable" bit(1) NOT NULL default 0,
  "placeholder" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "ENTITY_FIELDNAME_IDX" ("entity_id_OID","name"),
  KEY "Field_KeyIdx2" ("entity_id_OID"),
  KEY "Field_KeyIdx1" ("type_id_OID"),
  CONSTRAINT "Field_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "Field_FK2" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "FieldMetadata";
CREATE TABLE "FieldMetadata" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "key" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "value" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "metadata_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldMetadata_KeyIdx1" ("field_id_OID"),
  CONSTRAINT "FieldMetadata_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);

DROP TABLE IF EXISTS "FieldSetting";
CREATE TABLE "FieldSetting" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "DETAILS_ID" bigint(20) DEFAULT NULL,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "value" TEXT CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "settings_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldSetting_KeyIdx2" ("field_id_OID"),
  KEY "FieldSetting_KeyIdx1" ("DETAILS_ID"),
  CONSTRAINT "FieldSetting_FK2" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeSetting" ("id"),
  CONSTRAINT "FieldSetting_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);

DROP TABLE IF EXISTS "FieldValidation";
CREATE TABLE "FieldValidation" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "DETAILS_ID" bigint(20) DEFAULT NULL,
  "enabled" bit(1) NOT NULL,
  "field_id_OID" bigint(20) DEFAULT NULL,
  "value" varchar(1024) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "validations_INTEGER_IDX" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "FieldValidation_KeyIdx1" ("field_id_OID"),
  KEY "FieldValidation_KeyIdx2" ("DETAILS_ID"),
  CONSTRAINT "FieldValidation_FK2" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id"),
  CONSTRAINT "FieldValidation_FK1" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeValidation" ("id")
);

DROP TABLE IF EXISTS "Lookup";
CREATE TABLE "Lookup" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  "exposedViaRest" bit(1) NOT NULL,
  "lookupName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "singleObjectReturn" bit(1) NOT NULL,
  "lookups_INTEGER_IDX" int(11) DEFAULT NULL,
  "readOnly" bit(1) NOT NULL,
  PRIMARY KEY ("id"),
  KEY "Lookup_KeyIdx1" ("entity_id_OID"),
  CONSTRAINT "Lookup_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "LookupFields";
CREATE TABLE "LookupFields" (
  "id_OID" bigint(20) NOT NULL,
  "id_EID" bigint(20) NOT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  KEY "LookupFields_KeyIdx1" ("id_OID"),
  KEY "LookupFields_KeyIdx2" ("id_EID"),
  CONSTRAINT "LookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id"),
  CONSTRAINT "LookupFields_FK2" FOREIGN KEY ("id_EID") REFERENCES "Field" ("id")
);

DROP TABLE IF EXISTS "Lookup_fieldsOrder";
CREATE TABLE "Lookup_fieldsOrder" (
  "id_OID" bigint(20) NOT NULL,
  "fieldName" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("id_OID", "IDX"),
  KEY "Lookup_fieldsOrder_KeyIdx1" ("id_OID"),
  CONSTRAINT "Lookup_fieldsOrder_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_rangeLookupFields";
CREATE TABLE "Lookup_rangeLookupFields" (
  "id_OID" bigint(20) NOT NULL,
  "fieldName" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("id_OID", "IDX"),
  KEY "Lookup_rangeLookupFields_KeyIdx1" ("id_OID"),
  CONSTRAINT "Lookup_rangeLookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_setLookupFields";
CREATE TABLE "Lookup_setLookupFields" (
  "id_OID" bigint(20) NOT NULL,
  "fieldName" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("id_OID", "IDX"),
  KEY "Lookup_setLookupFields_KeyIdx1" ("id_OID"),
  CONSTRAINT "Lookup_setLookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_customOperators";
CREATE TABLE "Lookup_customOperators" (
  "id_OID" bigint(20) NOT NULL,
  "fieldName" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  "operator" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY ("id_OID", "fieldName"),
  KEY "Lookup_customOperators_KeyIdx1" ("id_OID"),
  CONSTRAINT "Lookup_customOperators_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_useGenericParams";
CREATE TABLE "Lookup_useGenericParams" (
  "id_OID" bigint(20) NOT NULL,
  "param" varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  "value" bit(1) DEFAULT FALSE,
  PRIMARY KEY ("id_OID", "param"),
  KEY "Lookup_useGenericParams_KeyIdx1" ("id_OID"),
  CONSTRAINT "Lookup_useGenericParams_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "RestOptions";
CREATE TABLE "RestOptions" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "allowCreate" bit(1) NOT NULL,
  "allowDelete" bit(1) NOT NULL,
  "allowRead" bit(1) NOT NULL,
  "allowUpdate" bit(1) NOT NULL,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  "modifiedByUser" bit(1) NOT NULL default 0,
  PRIMARY KEY ("id"),
  KEY "RestOptions_KeyIdx1" ("entity_id_OID"),
  CONSTRAINT "RestOptions_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "SEQUENCE_TABLE";
CREATE TABLE "SEQUENCE_TABLE" (
  "SEQUENCE_NAME" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "NEXT_VAL" bigint(20) NOT NULL,
  PRIMARY KEY ("SEQUENCE_NAME")
);

DROP TABLE IF EXISTS "TYPE_SETTING_SETTING_OPTION";
CREATE TABLE "TYPE_SETTING_SETTING_OPTION" (
  "TYPE_SETTING_ID_OID" bigint(20) NOT NULL,
  "SETTING_OPTION_ID_EID" bigint(20) DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("TYPE_SETTING_ID_OID","IDX"),
  KEY "TYPE_SETTING_SETTING_OPTION_KeyIdx1" ("TYPE_SETTING_ID_OID"),
  KEY "TYPE_SETTING_SETTING_OPTION_KeyIdx2" ("SETTING_OPTION_ID_EID"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK2" FOREIGN KEY ("SETTING_OPTION_ID_EID") REFERENCES "TypeSettingOption" ("id"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK1" FOREIGN KEY ("TYPE_SETTING_ID_OID") REFERENCES "TypeSetting" ("id")
);

DROP TABLE IF EXISTS "TYPE_TYPE_SETTING";
CREATE TABLE "TYPE_TYPE_SETTING" (
  "TYPE_ID_OID" bigint(20) NOT NULL,
  "TYPE_SETTING_ID_EID" bigint(20) DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  KEY "TYPE_TYPE_SETTING_KeyIdx1" ("TYPE_SETTING_ID_EID"),
  KEY "TYPE_TYPE_SETTING_KeyIdx2" ("TYPE_ID_OID"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK2" FOREIGN KEY ("TYPE_SETTING_ID_EID") REFERENCES "TypeSetting" ("id")
);

DROP TABLE IF EXISTS "TYPE_TYPE_VALIDATION";
CREATE TABLE "TYPE_TYPE_VALIDATION" (
  "TYPE_ID_OID" bigint(20) NOT NULL,
  "TYPE_VALIDATION_ID_EID" bigint(20) DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  KEY "TYPE_TYPE_VALIDATION_KeyIdx2" ("TYPE_VALIDATION_ID_EID"),
  KEY "TYPE_TYPE_VALIDATION_KeyIdx1" ("TYPE_ID_OID"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK2" FOREIGN KEY ("TYPE_VALIDATION_ID_EID") REFERENCES "TypeValidation" ("id"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id")
);

DROP TABLE IF EXISTS "Tracking";
CREATE TABLE "Tracking" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "entity_id_OID" bigint(20) DEFAULT NULL,
  "recordHistory" bit(1) NOT NULL,
  "allowCreateEvent" bit(1) NOT NULL default 1,
  "allowDeleteEvent" bit(1) NOT NULL default 1,
  "allowUpdateEvent" bit(1) NOT NULL default 1,
  "modifiedByUser" bit(1) NOT NULL default 0,
  "nonEditable" bit(1) NOT NULL default 0,
  PRIMARY KEY ("id"),
  KEY "Tracking_KeyIdx1" ("entity_id_OID"),
  CONSTRAINT "Tracking_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "Type";
CREATE TABLE "Type" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "description" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "defaultName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "typeClass" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "TypeSetting";
CREATE TABLE "TypeSetting" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "defaultValue" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "TYPE_ID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "TypeSetting_KeyIdx1" ("TYPE_ID"),
  CONSTRAINT "TypeSetting_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);

DROP TABLE IF EXISTS "TypeSettingOption";
CREATE TABLE "TypeSettingOption" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "name" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "TypeValidation";
CREATE TABLE "TypeValidation" (
  "id" bigint(20) NOT NULL AUTO_INCREMENT,
  "displayName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "TYPE_ID" bigint(20) DEFAULT NULL,
  PRIMARY KEY ("id"),
  KEY "TypeValidation_KeyIdx1" ("TYPE_ID"),
  CONSTRAINT "TypeValidation_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);

DROP TABLE IF EXISTS "TypeValidation_annotations";
CREATE TABLE "TypeValidation_annotations" (
  "id_OID" bigint(20) NOT NULL,
  "ANNOTATION" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  "IDX" int(11) NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  KEY "TypeValidation_annotations_KeyIdx1" ("id_OID"),
  CONSTRAINT "TypeValidation_annotations_FK1" FOREIGN KEY ("id_OID") REFERENCES "TypeValidation" ("id")
);

DROP TABLE IF EXISTS "SchemaChangeLock";
CREATE TABLE "SchemaChangeLock" (
    "id" bigint(20) PRIMARY KEY AUTO_INCREMENT,
    "lockId" int(1) UNIQUE
);

DROP TABLE IF EXISTS "MigrationMapping";
CREATE TABLE "MigrationMapping" (
  "flywayMigrationVersion" int NOT NULL,
  "moduleMigrationVersion" int NOT NULL,
  "moduleName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY ("flywayMigrationVersion")
);

DROP TABLE IF EXISTS "Entity_readOnlySecurityMembers";
CREATE TABLE "Entity_readOnlySecurityMembers" (
  "Entity_OID" bigint(20) NOT NULL,
  "ReadOnlySecurityMember" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY ("Entity_OID", "ReadOnlySecurityMember"),
  KEY "Entity_readOnlySecurityMembers_KeyIdx1" ("Entity_OID"),
  CONSTRAINT "Entity_readOnlySecurityMembers_FK1" FOREIGN KEY ("Entity_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "UserPreferences";
CREATE TABLE "UserPreferences" (
  "className" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "username" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "gridRowsNumber" int,
  PRIMARY KEY ("className", "username")
);

DROP TABLE IF EXISTS "UserPreferences_selectedFields";
CREATE TABLE "UserPreferences_selectedFields" (
  "className_OID" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "username_OID" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "selectedField" bigint(20),
  "IDX" int(11),
  PRIMARY KEY ("className_OID", "username_OID", "IDX"),
  KEY "UserPreferences_selectedFields_KeyIdx1" ("selectedField"),
  KEY "UserPreferences_selectedFields_KeyIdx2" ("className_OID", "username_OID"),
  CONSTRAINT "UserPreferences_visibleFields_FK1" FOREIGN KEY ("className_OID", "username_OID") REFERENCES "UserPreferences" ("className", "username"),
  CONSTRAINT "UserPreferences_visibleFields_FK2" FOREIGN KEY ("selectedField") REFERENCES "Field" ("id") ON DELETE CASCADE
);

DROP TABLE IF EXISTS "UserPreferences_unselectedFields";
CREATE TABLE "UserPreferences_unselectedFields" (
  "className_OID" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "username_OID" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "unselectedField" bigint(20),
  "IDX" int(11),
  PRIMARY KEY ("className_OID", "username_OID", "IDX"),
  KEY "UserPreferences_unselectedFields_KeyIdx1" ("unselectedField"),
  KEY "UserPreferences_unselectedFields_KeyIdx2" ("className_OID", "username_OID"),
  CONSTRAINT "UserPreferences_unselectedFields_FK1" FOREIGN KEY ("className_OID", "username_OID") REFERENCES "UserPreferences" ("className", "username"),
  CONSTRAINT "UserPreferences_unselectedFields_FK2" FOREIGN KEY ("unselectedField") REFERENCES "Field" ("id") ON DELETE CASCADE
);

DROP TABLE IF EXISTS "ConfigSettings";
CREATE TABLE "ConfigSettings" (
  "id" bigint(20) AUTO_INCREMENT,
  "afterTimeUnit" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "afterTimeValue" int(11),
  "deleteMode" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "emptyTrash" bit(1),
  "defaultGridSize" int DEFAULT 50,
  "refreshModuleAfterTimeout" bit(1) DEFAULT 0,
  PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "BundleFailsReport";
CREATE TABLE "BundleFailsReport" (
  "id" bigint(20) AUTO_INCREMENT,
  "bundleRestartStatus" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "bundleSymbolicName" varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  "errorMessage" mediumtext NOT NULL,
  "nodeName"	varchar(255) CHARACTER SET latin1 COLLATE latin1_bin,
  "reportDate" datetime,
  PRIMARY KEY ("id")
);

INSERT INTO "TYPE_SETTING_SETTING_OPTION" VALUES (1, 1, 0),
                                                 (2, 1, 0),
                                                 (3, 1, 0),
                                                 (1, 2, 1),
                                                 (2, 2, 1);

INSERT INTO "TYPE_TYPE_SETTING" VALUES (7, 1, 0),
                                       (7, 2, 1),
                                       (8, 3, 0),
                                       (8, 4, 1),
                                       (8, 5, 2);

INSERT INTO "TYPE_TYPE_VALIDATION" VALUES (1, 1, 0),
                                          (1, 2, 1),
                                          (1, 3, 2),
                                          (1, 4, 3),
                                          (2, 5, 0),
                                          (2, 6, 1),
                                          (2, 7, 2),
                                          (7, 8, 0),
                                          (7, 9, 1),
                                          (7, 10, 2),
                                          (7, 11, 3);


INSERT INTO "Type" VALUES (1, 'mds.field.description.integer', 'mds.field.integer', 'integer', 'java.lang.Integer'),
                          (2, 'mds.field.description.string', 'mds.field.string', 'str', 'java.lang.String'),
                          (3, 'mds.field.description.boolean', 'mds.field.boolean', 'bool', 'java.lang.Boolean'),
                          (4, 'mds.field.description.date', 'mds.field.javaUtilDate', 'date', 'java.util.Date'),
                          (5, 'mds.field.description.time', 'mds.field.time', 'time', 'org.motechproject.commons.date.model.Time'),
                          (6, 'mds.field.description.datetime', 'mds.field.datetime', 'datetime', 'org.joda.time.DateTime'),
                          (7, 'mds.field.description.decimal', 'mds.field.decimal', 'dec', 'java.lang.Double'),
                          (8, 'mds.field.description.combobox', 'mds.field.combobox', 'collection', 'java.util.Collection'),
                          (9, 'mds.field.description.long', 'mds.field.long',' longName', 'java.lang.Long'),
                          (10,'mds.field.description.map', 'mds.field.map', 'map', 'java.util.Map'),
                          (11, 'mds.field.description.period', 'mds.field.period', 'period', 'org.joda.time.Period'),
                          (12, 'mds.field.description.locale', 'mds.field.locale', 'locale', 'java.util.Locale'),
                          (13, 'mds.field.description.blob', 'mds.field.blob', 'blob', '[Ljava.lang.Byte;'),
                          (14, 'mds.field.description.localDate', 'mds.field.date', 'date', 'org.joda.time.LocalDate'),
                          (15, 'mds.field.description.relationship', 'mds.field.relationship', 'relationship', 'org.motechproject.mds.domain.Relationship'),
                          (16, 'mds.field.description.relationship.oneToMany', 'mds.field.relationship.oneToMany', 'oneToManyRelationship', 'org.motechproject.mds.domain.OneToManyRelationship');

INSERT INTO "TypeSetting" VALUES (1, '9', 'mds.form.label.precision', 1),
                                 (2, '2', 'mds.form.label.scale', 1),
                                 (3, '[]', 'mds.form.label.values', 8),
                                 (4, 'false', 'mds.form.label.allowUserSupplied', 3),
                                 (5, 'false', 'mds.form.label.allowMultipleSelections', 3);

INSERT INTO "TypeSettingOption" VALUES (1, 'REQUIRE'),
                                       (2, 'POSITIVE');

INSERT INTO "TypeValidation" VALUES (1, 'mds.field.validation.minValue', 1),
                                    (2, 'mds.field.validation.maxValue', 1),
                                    (3, 'mds.field.validation.mustBeInSet', 2),
                                    (4, 'mds.field.validation.cannotBeInSet', 1),
                                    (5, 'mds.field.validation.regex', 2),
                                    (6, 'mds.field.validation.minLength', 1),
                                    (7, 'mds.field.validation.maxLength', 1),
                                    (8, 'mds.field.validation.minValue', 7),
                                    (9, 'mds.field.validation.maxValue', 7),
                                    (10, 'mds.field.validation.mustBeInSet', 7),
                                    (11, 'mds.field.validation.cannotBeInSet', 2);

INSERT INTO "TypeValidation_annotations" VALUES (1, 'javax.validation.constraints.DecimalMin', 0),
                                                (1, 'javax.validation.constraints.Min', 1),
                                                (2, 'javax.validation.constraints.DecimalMax', 0),
                                                (2, 'javax.validation.constraints.Max', 1),
                                                (3, 'org.motechproject.mds.annotations.InSet', 0),
                                                (4, 'org.motechproject.mds.annotations.NotInSet', 0),
                                                (5, 'javax.validation.constraints.Pattern', 0),
                                                (6, 'javax.validation.constraints.DecimalMin', 0),
                                                (6, 'javax.validation.constraints.Size', 1),
                                                (7, 'javax.validation.constraints.DecimalMax', 0),
                                                (7, 'javax.validation.constraints.Size', 1),
                                                (8, 'javax.validation.constraints.DecimalMin', 0),
                                                (8, 'javax.validation.constraints.Min', 1),
                                                (9, 'javax.validation.constraints.DecimalMax', 0),
                                                (9, 'javax.validation.constraints.Max', 1),
                                                (10, 'org.motechproject.mds.annotations.InSet', 0),
                                                (11, 'org.motechproject.mds.annotations.NotInSet', 0);


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

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

INSERT INTO TypeSetting
SELECT (ts.id + 1), 'false', 'mds.form.label.textarea', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Boolean'
ORDER BY ts.id DESC
LIMIT 1;

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'java.lang.String' AND ts.name LIKE 'mds.form.label.textarea';

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.oneToOne','mds.field.relationship.oneToOne','oneToOneRelationship','org.motechproject.mds.domain.OneToOneRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

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

INSERT INTO TypeSetting
SELECT (ts.id + 1), '255', 'mds.form.label.maxTextLength', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Integer'
ORDER BY ts.id DESC
LIMIT 1;

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'java.lang.String' AND ts.name LIKE 'mds.form.label.maxTextLength';

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

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.manyToOne','mds.field.relationship.manyToOne','manyToOneRelationship','org.motechproject.mds.domain.ManyToOneRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

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

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.manyToMany','mds.field.relationship.manyToMany','manyToManyRelationship','org.motechproject.mds.domain.ManyToManyRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

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

INSERT IGNORE INTO SchemaChangeLock VALUES (1, 1);

INSERT INTO Type
SELECT id + 1, 'mds.field.description.datetime','mds.field.datetime8','datetime','java.time.LocalDateTime'
FROM Type
ORDER BY id DESC
LIMIT 1;

INSERT INTO Type
SELECT id + 1, 'mds.field.description.localDate','mds.field.date8','localDate','java.time.LocalDate'
FROM Type
ORDER BY id DESC
LIMIT 1;