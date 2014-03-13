INSERT IGNORE INTO motech_data_services.EntityMapping
    (`id`, `className`, `name`, `module`, `namespace`)
VALUES
    ('9001', 'org.motechproject.openmrs.ws.resource.model.Patient', 'Patient', 'MOTECH OpenMRS Web Services', 'navio'),
    ('9002', 'org.motechproject.openmrs.ws.resource.model.Person', 'Person', 'MOTECH OpenMRS Web Services', 'navio'),
    ('9003', 'org.motechproject.openmrs.ws.resource.model.Patient', 'Patient', 'MOTECH OpenMRS Web Services', 'accra'),
    ('9004', 'org.motechproject.openmrs.ws.resource.model.Person', 'Person', 'MOTECH OpenMRS Web Services', 'accra'),
    ('9005', 'org.motechproject.appointments.api.model.Appointment', 'Appointment', 'MOTECH Appointments API', NULL),
    ('9006', 'org.motechproject.ivr.domain.CallDetailRecord', 'CallDetailRecord', 'MOTECH IVR API', NULL),
    ('9007', 'org.motechproject.mds.entity.Voucher', 'Voucher', NULL, NULL),
    ('9008', 'org.motechproject.messagecampaign.domain.campaign.Campaign', 'Campaign', 'MOTECH Message Campaign', NULL);
