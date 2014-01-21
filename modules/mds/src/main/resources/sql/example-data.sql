CREATE TABLE IF NOT EXISTS motech_data_services.EntityMapping (
  `id` bigint(20) NOT NULL,
  `className` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `module` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `namespace` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT IGNORE INTO motech_data_services.EntityMapping
    (`id`, `className`, `module`, `namespace`)
VALUES
    ('9001', 'Patient', 'OpenMRS', 'navio'),
    ('9002', 'Person', 'OpenMRS', 'navio'),
    ('9003', 'Patient', 'OpenMRS', 'accra'),
    ('9004', 'Person', 'OpenMRS', 'accra'),
    ('9005', 'Appointments', 'Appointments', NULL),
    ('9006', 'Call Log Item', 'IVR', NULL),
    ('9007', 'Voucher', NULL, NULL),
    ('9008', 'Campaign', "Message Campaign", NULL);