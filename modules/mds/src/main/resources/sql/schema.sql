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
