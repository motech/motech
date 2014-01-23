CREATE DATABASE IF NOT EXISTS motech_data_services;

CREATE TABLE IF NOT EXISTS motech_data_services.AvailableFieldTypeMapping(
    id bigint NOT NULL,
    defaultName varchar(255),
    description varchar(255),
    displayName varchar(255),
    typeClass varchar(255),
    PRIMARY KEY (id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS motech_data_services.TypeSettingsMapping(
    id bigint NOT NULL,
    name varchar(255),
    value varchar(255),
    valueType bigint,
    type bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (valueType) REFERENCES motech_data_services.AvailableFieldTypeMapping(id),
    FOREIGN KEY (type) REFERENCES motech_data_services.AvailableFieldTypeMapping(id)
)ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS motech_data_services.SettingOptionsMapping(
    id bigint NOT NULL,
    name varchar(255),
    settingId bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (settingId) REFERENCES motech_data_services.TypeSettingsMapping(id)
)ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS motech_data_services.TypeValidationMapping(
    id bigint NOT NULL,
    name varchar(255),
    type bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (type) REFERENCES motech_data_services.AvailableFieldTypeMapping (id)
)ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS motech_data_services.ValidationCriterionMapping(
    id bigint NOT NULL,
    displayName varchar(255),
    value varchar(255),
    enabled bool,
    validation bigint,
    valueType bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (validation) REFERENCES motech_data_services.TypeValidationMapping (id),
    FOREIGN KEY (valueType) REFERENCES motech_data_services.AvailableFieldTypeMapping (id)
)ENGINE = INNODB;

INSERT IGNORE INTO motech_data_services.AvailableFieldTypeMapping
    (id, defaultName, description, displayName, typeClass)
VALUES
    (1, 'int', 'mds.field.description.integer', 'mds.field.integer', 'java.lang.Integer'),
    (2, 'string', 'mds.field.description.string', 'mds.field.string', 'java.lang.String'),
    (3, 'bool', 'mds.field.description.boolean', 'mds.field.boolean', 'java.lang.Boolean'),
    (4, 'date', 'mds.field.description.date', 'mds.field.date', 'java.util.Date'),
    (5, 'time', 'mds.field.description.time', 'mds.field.time', 'org.motechproject.commons.date.model.Time'),
    (6, 'dateTime', 'mds.field.description.datetime', 'mds.field.datetime', 'org.joda.time.DateTime'),
    (7, 'double', 'mds.field.description.decimal', 'mds.field.decimal', 'java.lang.Double'),
    (8, 'list', 'mds.field.description.combobox', 'mds.field.combobox', 'java.util.List');

INSERT IGNORE INTO motech_data_services.TypeSettingsMapping
    (id, name, value, valueType, type)
VALUES
    (1, 'mds.form.label.precision', '9', 1, 7),
    (2, 'mds.form.label.scale', '2', 1, 7),
    (3, 'mds.form.label.values', NULL, 3, 8),
    (4, 'mds.form.label.allowUserSupplied', 'false', 3, 8),
    (5, 'mds.form.label.allowMultipleSelections', 'false', 3, 8);

INSERT IGNORE INTO motech_data_services.SettingOptionsMapping
    (id, name, settingId)
VALUES
    (1, 'REQUIRE', 1),
    (2, 'POSITIVE', 1),
    (3, 'REQUIRE', 2),
    (4, 'POSITIVE', 2),
    (5, 'REQUIRE', 3);

INSERT IGNORE INTO motech_data_services.TypeValidationMapping
    (id, name, type)
VALUES
    (1, 'int', 1),
    (2, 'double', 7),
    (3, 'string', 2);

INSERT IGNORE INTO motech_data_services.ValidationCriterionMapping
    (id, displayName, value, enabled, validation, valueType)
VALUES
    (1, 'mds.field.validation.minValue', NULL, FALSE, 1, 1),
    (2, 'mds.field.validation.maxValue', NULL, FALSE, 1, 1),
    (3, 'mds.field.validation.mustBeInSet', NULL, FALSE, 1, 2),
    (4, 'mds.field.validation.cannotBeInSet', NULL, FALSE, 1, 2),
    (5, 'mds.field.validation.minValue', NULL, FALSE, 2, 7),
    (6, 'mds.field.validation.maxValue', NULL, FALSE, 2, 7),
    (7, 'mds.field.validation.mustBeInSet', NULL, FALSE, 2, 2),
    (8, 'mds.field.validation.cannotBeInSet', NULL, FALSE, 2, 2),
    (9, 'mds.field.validation.regex', NULL, FALSE, 3, 2),
    (10, 'mds.field.validation.minLength', NULL, FALSE, 3, 1),
    (11, 'mds.field.validation.maxLength', NULL, FALSE, 3, 1);





