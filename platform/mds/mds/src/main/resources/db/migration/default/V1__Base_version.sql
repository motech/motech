DROP TABLE IF EXISTS "Entity";
CREATE TABLE "Entity" (
  "id" serial,
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
  "tableName" varchar(255) DEFAULT NULL,
  "maxFetchDepth" bigint DEFAULT NULL,
  "securityOptionsModified" boolean NOT NULL DEFAULT false,
  "readOnlySecurityMode" varchar(255) DEFAULT NULL,
  "bundleSymbolicName" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "DRAFT_USER_IDX" UNIQUE ("parentEntity_id_OID", "draftOwnerUsername"),
  CONSTRAINT "Entity_FK1" FOREIGN KEY ("parentEntity_id_OID") REFERENCES "Entity" ("id")
);


DROP TABLE IF EXISTS "Type";
CREATE TABLE "Type" (
  "id" serial,
  "description" varchar(255) DEFAULT NULL,
  "displayName" varchar(255) DEFAULT NULL,
  "defaultName" varchar(255) DEFAULT NULL,
  "typeClass" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "TypeSetting";
CREATE TABLE "TypeSetting" (
  "id" serial,
  "defaultValue" varchar(255) DEFAULT NULL,
  "name" varchar(255) DEFAULT NULL,
  "TYPE_ID" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "TypeSetting_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);

DROP TABLE IF EXISTS "TypeValidation";
CREATE TABLE "TypeValidation" (
  "id" serial,
  "displayName" varchar(255) DEFAULT NULL,
  "TYPE_ID" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "TypeValidation_FK1" FOREIGN KEY ("TYPE_ID") REFERENCES "Type" ("id")
);

DROP TABLE IF EXISTS "TypeSettingOption";
CREATE TABLE "TypeSettingOption" (
  "id" serial,
  "name" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "EntityAudit";
CREATE TABLE "EntityAudit" (
  "id" serial,
  "modificationDate" timestamp NULL,
  "ownerUsername" varchar(255) NOT NULL,
  "version" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "EntityAudit_FK1" FOREIGN KEY ("id") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "Entity_securityMembers";
CREATE TABLE "Entity_securityMembers" (
  "Entity_OID" bigint NOT NULL,
  "SecurityMember" varchar(255) NOT NULL,
  PRIMARY KEY ("Entity_OID","SecurityMember"),
  CONSTRAINT "Entity_securityMembers_FK1" FOREIGN KEY ("Entity_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "EntityDraft_fieldNameChanges";
CREATE TABLE "EntityDraft_fieldNameChanges" (
  "id_OID" bigint NOT NULL,
  "key" varchar(255) NOT NULL,
  "value" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id_OID", "key"),
  CONSTRAINT "EntityDraft_fieldNameChanges_FK1" FOREIGN KEY ("id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "Field";
CREATE TABLE "Field" (
  "id" serial,
  "defaultValue" varchar(255) DEFAULT NULL,
  "displayName" varchar(255) DEFAULT NULL,
  "entity_id_OID" bigint DEFAULT NULL,
  "exposedViaRest" boolean NOT NULL,
  "name" varchar(255) DEFAULT NULL,
  "required" boolean NOT NULL,
  "tooltip" varchar(255) DEFAULT NULL,
  "type_id_OID" bigint DEFAULT NULL,
  "fields_INTEGER_IDX" bigint DEFAULT NULL,
  "uiDisplayable" boolean NOT NULL,
  "uiFilterable" boolean NOT NULL,
  "uiDisplayPosition" bigint DEFAULT NULL,
  "readOnly" boolean NOT NULL,
  "nonEditable" boolean NOT NULL DEFAULT false,
  "nonDisplayable" boolean NOT NULL DEFAULT false,
  "placeholder" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "ENTITY_FIELDNAME_IDX" UNIQUE ("entity_id_OID","name"),
  CONSTRAINT "Field_FK1" FOREIGN KEY ("type_id_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "Field_FK2" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "FieldMetadata";
CREATE TABLE "FieldMetadata" (
  "id" serial,
  "field_id_OID" bigint DEFAULT NULL,
  "key" varchar(255) DEFAULT NULL,
  "value" varchar(255) DEFAULT NULL,
  "metadata_INTEGER_IDX" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "FieldMetadata_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);

DROP TABLE IF EXISTS "FieldSetting";
CREATE TABLE "FieldSetting" (
  "id" serial,
  "DETAILS_ID" bigint DEFAULT NULL,
  "field_id_OID" bigint DEFAULT NULL,
  "value" TEXT DEFAULT NULL,
  "settings_INTEGER_IDX" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "FieldSetting_FK2" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeSetting" ("id"),
  CONSTRAINT "FieldSetting_FK1" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id")
);

DROP TABLE IF EXISTS "FieldValidation";
CREATE TABLE "FieldValidation" (
  "id" serial,
  "DETAILS_ID" bigint DEFAULT NULL,
  "enabled" boolean NOT NULL,
  "field_id_OID" bigint DEFAULT NULL,
  "value" varchar(1024) DEFAULT NULL,
  "validations_INTEGER_IDX" bigint DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "FieldValidation_FK2" FOREIGN KEY ("field_id_OID") REFERENCES "Field" ("id"),
  CONSTRAINT "FieldValidation_FK1" FOREIGN KEY ("DETAILS_ID") REFERENCES "TypeValidation" ("id")
);

DROP TABLE IF EXISTS "Lookup";
CREATE TABLE "Lookup" (
  "id" serial,
  "entity_id_OID" bigint DEFAULT NULL,
  "exposedViaRest" boolean NOT NULL,
  "lookupName" varchar(255) DEFAULT NULL,
  "singleObjectReturn" boolean NOT NULL,
  "lookups_INTEGER_IDX" bigint DEFAULT NULL,
  "readOnly" boolean NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "Lookup_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "LookupFields";
CREATE TABLE "LookupFields" (
  "id_OID" bigint NOT NULL,
  "id_EID" bigint NOT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  CONSTRAINT "LookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id"),
  CONSTRAINT "LookupFields_FK2" FOREIGN KEY ("id_EID") REFERENCES "Field" ("id")
);

DROP TABLE IF EXISTS "Lookup_fieldsOrder";
CREATE TABLE "Lookup_fieldsOrder" (
  "id_OID" bigint NOT NULL,
  "fieldName" varchar(255) NOT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("id_OID", "IDX"),
  CONSTRAINT "Lookup_fieldsOrder_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_rangeLookupFields";
CREATE TABLE "Lookup_rangeLookupFields" (
  "id_OID" bigint NOT NULL,
  "fieldName" varchar(255) NOT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("id_OID", "IDX"),
  CONSTRAINT "Lookup_rangeLookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_setLookupFields";
CREATE TABLE "Lookup_setLookupFields" (
  "id_OID" bigint NOT NULL,
  "fieldName" varchar(255) NOT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("id_OID", "IDX"),
  CONSTRAINT "Lookup_setLookupFields_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_customOperators";
CREATE TABLE "Lookup_customOperators" (
  "id_OID" bigint NOT NULL,
  "key" varchar(255) NOT NULL,
  "value" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id_OID", "key"),
  CONSTRAINT "Lookup_customOperators_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "Lookup_useGenericParams";
CREATE TABLE "Lookup_useGenericParams" (
  "id_OID" bigint NOT NULL,
  "key" varchar(255) NOT NULL,
  "value" boolean DEFAULT FALSE,
  PRIMARY KEY ("id_OID", "key"),
  CONSTRAINT "Lookup_useGenericParams_FK1" FOREIGN KEY ("id_OID") REFERENCES "Lookup" ("id")
);

DROP TABLE IF EXISTS "RestOptions";
CREATE TABLE "RestOptions" (
  "id" serial,
  "allowCreate" boolean NOT NULL,
  "allowDelete" boolean NOT NULL,
  "allowRead" boolean NOT NULL,
  "allowUpdate" boolean NOT NULL,
  "entity_id_OID" bigint DEFAULT NULL,
  "modifiedByUser" boolean NOT NULL DEFAULT false,
  PRIMARY KEY ("id"),
  CONSTRAINT "RestOptions_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "SEQUENCE_TABLE";
CREATE TABLE "SEQUENCE_TABLE" (
  "SEQUENCE_NAME" varchar(255) NOT NULL,
  "NEXT_VAL" bigint NOT NULL,
  PRIMARY KEY ("SEQUENCE_NAME")
);

DROP TABLE IF EXISTS "TYPE_SETTING_SETTING_OPTION";
CREATE TABLE "TYPE_SETTING_SETTING_OPTION" (
  "TYPE_SETTING_ID_OID" bigint NOT NULL,
  "SETTING_OPTION_ID_EID" bigint DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("TYPE_SETTING_ID_OID","IDX"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK2" FOREIGN KEY ("SETTING_OPTION_ID_EID") REFERENCES "TypeSettingOption" ("id"),
  CONSTRAINT "TYPE_SETTING_SETTING_OPTION_FK1" FOREIGN KEY ("TYPE_SETTING_ID_OID") REFERENCES "TypeSetting" ("id")
);

DROP TABLE IF EXISTS "TYPE_TYPE_SETTING";
CREATE TABLE "TYPE_TYPE_SETTING" (
  "TYPE_ID_OID" bigint NOT NULL,
  "TYPE_SETTING_ID_EID" bigint DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id"),
  CONSTRAINT "TYPE_TYPE_SETTING_FK2" FOREIGN KEY ("TYPE_SETTING_ID_EID") REFERENCES "TypeSetting" ("id")
);

DROP TABLE IF EXISTS "TYPE_TYPE_VALIDATION";
CREATE TABLE "TYPE_TYPE_VALIDATION" (
  "TYPE_ID_OID" bigint NOT NULL,
  "TYPE_VALIDATION_ID_EID" bigint DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("TYPE_ID_OID","IDX"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK2" FOREIGN KEY ("TYPE_VALIDATION_ID_EID") REFERENCES "TypeValidation" ("id"),
  CONSTRAINT "TYPE_TYPE_VALIDATION_FK1" FOREIGN KEY ("TYPE_ID_OID") REFERENCES "Type" ("id")
);

DROP TABLE IF EXISTS "Tracking";
CREATE TABLE "Tracking" (
  "id" serial,
  "entity_id_OID" bigint DEFAULT NULL,
  "allowCreateEvent" boolean NOT NULL DEFAULT true,
  "allowDeleteEvent" boolean NOT NULL DEFAULT true,
  "allowUpdateEvent" boolean NOT NULL DEFAULT true,
  "modifiedByUser" boolean NOT NULL DEFAULT false,
  "nonEditable" boolean NOT NULL DEFAULT false,
  PRIMARY KEY ("id"),
  CONSTRAINT "Tracking_FK1" FOREIGN KEY ("entity_id_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "TypeValidation_annotations";
CREATE TABLE "TypeValidation_annotations" (
  "id_OID" bigint NOT NULL,
  "ANNOTATION" varchar(255) DEFAULT NULL,
  "IDX" bigint NOT NULL,
  PRIMARY KEY ("id_OID","IDX"),
  CONSTRAINT "TypeValidation_annotations_FK1" FOREIGN KEY ("id_OID") REFERENCES "TypeValidation" ("id")
);

DROP TABLE IF EXISTS "SchemaChangeLock";
CREATE TABLE "SchemaChangeLock" (
    "id" serial,
    "lockId" int UNIQUE
);

DROP TABLE IF EXISTS "MigrationMapping";
CREATE TABLE "MigrationMapping" (
  "flywayMigrationVersion" int NOT NULL,
  "moduleMigrationVersion" int NOT NULL,
  "moduleName" varchar(255) NOT NULL,
  PRIMARY KEY ("flywayMigrationVersion")
);

DROP TABLE IF EXISTS "Entity_readOnlySecurityMembers";
CREATE TABLE "Entity_readOnlySecurityMembers" (
  "Entity_OID" bigint NOT NULL,
  "ReadOnlySecurityMember" varchar(255) NOT NULL,
  PRIMARY KEY ("Entity_OID","ReadOnlySecurityMember"),
  CONSTRAINT "Entity_readOnlySecurityMembers_FK1" FOREIGN KEY ("Entity_OID") REFERENCES "Entity" ("id")
);

DROP TABLE IF EXISTS "UserPreferences";
CREATE TABLE IF NOT EXISTS "UserPreferences" (
  "username" varchar(255) NOT NULL,
  "className" varchar(255) NOT NULL,
  "gridRowsNumber" int,
  PRIMARY KEY ("username", "className")
);

DROP TABLE IF EXISTS "UserPreferences_selectedFields";
CREATE TABLE IF NOT EXISTS "UserPreferences_selectedFields" (
  "className_OID" varchar(255) NOT NULL,
  "username_OID" varchar(255) NOT NULL,
  "selectedField" bigint,
  "IDX" int,
  PRIMARY KEY ("className_OID", "username_OID", "IDX"),
  CONSTRAINT "UserPreferences_selectedFields_FK1" FOREIGN KEY ("username_OID", "className_OID") REFERENCES "UserPreferences" ("username", "className"),
  CONSTRAINT "UserPreferences_selectedFields_FK2" FOREIGN KEY ("selectedField") REFERENCES "Field" ("id") ON DELETE CASCADE
);

DROP TABLE IF EXISTS "UserPreferences_unselectedFields";
CREATE TABLE IF NOT EXISTS "UserPreferences_unselectedFields" (
  "className_OID" varchar(255) NOT NULL,
  "username_OID" varchar(255) NOT NULL,
  "unselectedField" bigint,
  "IDX" int,
  PRIMARY KEY ("className_OID", "username_OID", "IDX"),
  CONSTRAINT "UserPreferences_unselectedFields_FK1" FOREIGN KEY ("username_OID", "className_OID") REFERENCES "UserPreferences" ("username", "className"),
  CONSTRAINT "UserPreferences_unselectedFields_FK2" FOREIGN KEY ("unselectedField") REFERENCES "Field" ("id") ON DELETE CASCADE
);

DROP TABLE IF EXISTS "ConfigSettings";
CREATE TABLE IF NOT EXISTS "ConfigSettings" (
  "id" serial,
  "afterTimeUnit" varchar(255) NOT NULL,
  "afterTimeValue" int,
  "deleteMode" varchar(255) NOT NULL,
  "emptyTrash" boolean,
  "defaultGridSize" int DEFAULT 50,
  "refreshModuleAfterTimeout" boolean DEFAULT FALSE,
  PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "BundleFailsReport";
CREATE TABLE "BundleFailsReport" (
  "id" serial,
  "bundleRestartStatus" varchar(255) NOT NULL,
  "bundleSymbolicName" varchar(255) NOT NULL,
  "errorMessage" text NOT NULL,
  "nodeName" varchar(255),
  "reportDate" timestamp,
  PRIMARY KEY ("id")
);

INSERT INTO "Type" VALUES (1, 'mds.field.description.integer', 'mds.field.integer', 'integer', 'java.lang.Integer'),
                          (2,'mds.field.description.string','mds.field.string','str','java.lang.String'),
                          (3,'mds.field.description.boolean','mds.field.boolean','bool','java.lang.Boolean'),
                          (4,'mds.field.description.date','mds.field.javaUtilDate','date','java.util.Date'),
                          (5,'mds.field.description.time','mds.field.time','time','org.motechproject.commons.date.model.Time'),
                          (6,'mds.field.description.datetime','mds.field.datetime','datetime','org.joda.time.DateTime'),
                          (7,'mds.field.description.decimal','mds.field.decimal','dec','java.lang.Double'),
                          (8,'mds.field.description.combobox','mds.field.combobox','collection','java.util.Collection'),
                          (9,'mds.field.description.long','mds.field.long','longName','java.lang.Long'),
                          (10,'mds.field.description.map','mds.field.map','map','java.util.Map'),
                          (11,'mds.field.description.period','mds.field.period','period','org.joda.time.Period'),
                          (12,'mds.field.description.locale','mds.field.locale','locale','java.util.Locale'),
                          (13,'mds.field.description.blob','mds.field.blob','blob','[Ljava.lang.Byte;'),
                          (14,'mds.field.description.localDate','mds.field.date','localDate','org.joda.time.LocalDate'),
                          (15,'mds.field.description.relationship','mds.field.relationship','relationship','org.motechproject.mds.domain.Relationship'),
                          (16,'mds.field.description.relationship.oneToMany','mds.field.relationship.oneToMany','oneToManyRelationship','org.motechproject.mds.domain.OneToManyRelationship');

INSERT INTO "TypeSetting" VALUES (1,'9','mds.form.label.precision',1),
                                 (2,'2','mds.form.label.scale',1),
                                 (3,'[]','mds.form.label.values',8),
                                 (4,'false','mds.form.label.allowUserSupplied',3),
                                 (5,'false','mds.form.label.allowMultipleSelections',3);

INSERT INTO "TypeValidation" VALUES (1,'mds.field.validation.minValue',1),
                                    (2,'mds.field.validation.maxValue',1),
                                    (3,'mds.field.validation.mustBeInSet',2),
                                    (4,'mds.field.validation.cannotBeInSet',1),
                                    (5,'mds.field.validation.regex',2),
                                    (6,'mds.field.validation.minLength',1),
                                    (7,'mds.field.validation.maxLength',1),
                                    (8,'mds.field.validation.minValue',7),
                                    (9,'mds.field.validation.maxValue',7),
                                    (10,'mds.field.validation.mustBeInSet',2),
                                    (11,'mds.field.validation.cannotBeInSet',7);

INSERT INTO "TypeSettingOption" VALUES (1,'REQUIRE'),
                                       (2,'POSITIVE');

INSERT INTO "TYPE_SETTING_SETTING_OPTION" VALUES (1,1,0),
                                                 (2,1,0),
                                                 (3,1,0),
                                                 (1,2,1),
                                                 (2,2,1);

INSERT INTO "TYPE_TYPE_SETTING" VALUES (7,1,0),
                                       (7,2,1),
                                       (8,3,0),
                                       (8,4,1),
                                       (8,5,2);

INSERT INTO "TYPE_TYPE_VALIDATION" VALUES (1,1,0),
                                          (1,2,1),
                                          (1,3,2),
                                          (1,4,3),
                                          (2,5,0),
                                          (2,6,1),
                                          (2,7,2),
                                          (7,8,0),
                                          (7,9,1),
                                          (7,10,2),
                                          (7,11,3);

INSERT INTO "TypeValidation_annotations" VALUES (1,'javax.validation.constraints.DecimalMin',0),
                                                (1,'javax.validation.constraints.Min',1),
                                                (2,'javax.validation.constraints.DecimalMax',0),
                                                (2,'javax.validation.constraints.Max',1),
                                                (3,'org.motechproject.mds.annotations.InSet',0),
                                                (4,'org.motechproject.mds.annotations.NotInSet',0),
                                                (5,'javax.validation.constraints.Pattern',0),
                                                (6,'javax.validation.constraints.DecimalMin',0),
                                                (6,'javax.validation.constraints.Size',1),
                                                (7,'javax.validation.constraints.DecimalMax',0),
                                                (7,'javax.validation.constraints.Size',1),
                                                (8,'javax.validation.constraints.DecimalMin',0),
                                                (8,'javax.validation.constraints.Min',1),
                                                (9,'javax.validation.constraints.DecimalMax',0),
                                                (9,'javax.validation.constraints.Max',1),(
                                                10,'org.motechproject.mds.annotations.InSet',0),
                                                (11,'org.motechproject.mds.annotations.NotInSet',0);

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.cascadePersist', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.cascadeUpdate', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'false', 'mds.form.label.cascadeDelete', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.Relationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.Relationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.Relationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'false', 'mds.form.label.textarea', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'java.lang.String' AND ts."name" LIKE 'mds.form.label.textarea';

INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.relationship.oneToOne','mds.field.relationship.oneToOne','oneToOneRelationship','org.motechproject.mds.domain.OneToOneRelationship'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), '255', 'mds.form.label.maxTextLength', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Integer'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'java.lang.String' AND ts."name" LIKE 'mds.form.label.maxTextLength';

INSERT INTO "TYPE_SETTING_SETTING_OPTION"
SELECT ts."id", 1, 0
FROM "TypeSetting" ts
WHERE ts."name" LIKE 'mds.form.label.maxTextLength'
LIMIT 1;

INSERT INTO "TYPE_SETTING_SETTING_OPTION"
SELECT ts."id", 2, 1
FROM "TypeSetting" ts
WHERE ts."name" LIKE 'mds.form.label.maxTextLength'
LIMIT 1;

INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.relationship.manyToOne','mds.field.relationship.manyToOne','manyToOneRelationship','org.motechproject.mds.domain.ManyToOneRelationship'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';

INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.relationship.manyToMany','mds.field.relationship.manyToMany','manyToManyRelationship','org.motechproject.mds.domain.ManyToManyRelationship'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';

INSERT INTO "SchemaChangeLock" ("id", "lockId")
SELECT 1, 1
WHERE
    NOT EXISTS (
        SELECT id FROM "SchemaChangeLock" WHERE id = 1
    );

INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.datetime','mds.field.datetime8','datetime','java.time.LocalDateTime'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.localDate','mds.field.date8','localDate','java.time.LocalDate'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;