package org.motechproject.openmrs.services;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.Patient;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.IdentifierType;
import org.motechproject.openmrs.helper.PatientHelper;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

public class OpenMRSPatientAdaptor implements MRSPatientAdaptor {

    @Autowired
    PatientService patientService;

    @Autowired
    PersonService personService;

    @Autowired
    UserService userService;

    @Autowired
    OpenMRSFacilityAdaptor facilityAdaptor;

    @Autowired
    PatientHelper patientHelper;

    @Override
    public Patient getPatient(String patientId) {
        org.openmrs.Patient patient = patientService.getPatient(Integer.parseInt(patientId));
        if (patient == null) {
            return null;
        }
        return getMrsPatient(patient);
    }

    @Override
    public Patient getPatientByMotechId(String motechId) {
        PatientIdentifierType motechIdType = patientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName());
        List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
        idTypes.add(motechIdType);

        List<org.openmrs.Patient> patients = patientService.getPatients(null, motechId, idTypes, true);
        if (CollectionUtils.isEmpty(patients)) {
            return null;
        }
        return getMrsPatient(patients.get(0));
    }

    @Override
    public Patient savePatient(Patient patient) {
        final org.openmrs.Patient openMRSPatient = patientHelper.buildOpenMrsPatient(patient, userService.generateSystemId(),
                getPatientIdentifierType(IdentifierType.IDENTIFIER_MOTECH_ID),
                facilityAdaptor.getLocation(Integer.parseInt(patient.getFacility().getId())), getAllPersonAttributeTypes());

        return getMrsPatient(patientService.savePatient(openMRSPatient));
    }

    private Patient getMrsPatient(org.openmrs.Patient savedPatient) {
        final List<Attribute> attributes = project(savedPatient.getAttributes(), Attribute.class,
                on(PersonAttribute.class).getAttributeType().toString(), on(PersonAttribute.class).getValue());
        PersonName firstName = patientHelper.getFirstName(savedPatient);
        final PatientIdentifier patientIdentifier = savedPatient.getPatientIdentifier();
        return new Patient(String.valueOf(savedPatient.getId()), firstName.getGivenName(),
                firstName.getMiddleName(), firstName.getFamilyName(), patientHelper.getPreferredName(savedPatient),
                savedPatient.getBirthdate(), savedPatient.getBirthdateEstimated(), savedPatient.getGender(), patientHelper.getAddress(savedPatient), attributes,
                (patientIdentifier != null) ? facilityAdaptor.createFacility(patientIdentifier.getLocation()) : null);
    }

    public PatientIdentifierType getPatientIdentifierType(IdentifierType identifierType) {
        return patientService.getPatientIdentifierTypeByName(identifierType.getName());
    }

    private List<PersonAttributeType> getAllPersonAttributeTypes() {
        return personService.getAllPersonAttributeTypes(false);
    }
}
