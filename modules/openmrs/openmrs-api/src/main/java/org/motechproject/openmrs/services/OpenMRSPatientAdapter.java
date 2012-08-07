package org.motechproject.openmrs.services;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.IdentifierType;
import org.motechproject.openmrs.helper.PatientHelper;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

/**
 * Manages Patients in OpenMRS
 */
public class OpenMRSPatientAdapter implements MRSPatientAdapter {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PersonService personService;

    @Autowired
    private OpenMRSFacilityAdapter facilityAdapter;

    @Autowired
    private OpenMRSPersonAdapter personAdapter;

    @Autowired
    private PatientHelper patientHelper;

    @Autowired
    private OpenMRSConceptAdapter openMrsConceptAdapter;

    /**
     * Finds a patient by patient id
     *
     * @param patientId Value to be used to find a patient
     * @return Patient object if found, else null
     */
    @Override
    public MRSPatient getPatient(String patientId) {
        org.openmrs.Patient openMrsPatient = getOpenMrsPatient(patientId);
        return (openMrsPatient == null) ? null : getMrsPatient(openMrsPatient);
    }

    /**
     * Gets the patient's age
     *
     * @param motechId Motech id of the patient
     * @return The age of the patient if found, else null
     */
    @Override
    public Integer getAgeOfPatientByMotechId(String motechId) {
        Patient patient = getOpenmrsPatientByMotechId(motechId);
        return (patient != null) ? patient.getAge() : null;
    }

    /**
     * Finds a patient by motech id
     *
     * @param motechId Value to be used to find a patient
     * @return Patient object if found, else null
     */
    @Override
    public MRSPatient getPatientByMotechId(String motechId) {
        final Patient patient = getOpenmrsPatientByMotechId(motechId);
        return (patient != null) ? getMrsPatient(patient) : null;
    }

    /**
     * Saves a patient to the OpenMRS system
     *
     * @param patient Object to be saved
     * @return saved instance of Patient
     */
    @Override
    public MRSPatient savePatient(MRSPatient patient) {
        Patient existingOpenMrsPatient = getOpenmrsPatientByMotechId(patient.getMotechId());
        if (existingOpenMrsPatient != null) {
            return updatePatient(patient, existingOpenMrsPatient);
        } else {
            org.openmrs.Patient openMRSPatient = patientHelper.buildOpenMrsPatient(patient,
                    getPatientIdentifierType(IdentifierType.IDENTIFIER_MOTECH_ID),
                    facilityAdapter.getLocation(patient.getFacility().getId()), getAllPersonAttributeTypes());
            return getMrsPatient(patientService.savePatient(openMRSPatient));
        }
    }

    /**
     * Finds a patient by Motech id and updates the patient's details in the MRS system
     *
     * @param patient Patient instance with updated values (MOTECH identifier cannot be changed)
     * @return The updated Patient object if found, else null
     */
    @Override
    public MRSPatient updatePatient(MRSPatient patient) {
        return updatePatient(patient, getOpenmrsPatientByMotechId(patient.getMotechId()));
    }

    MRSPatient updatePatient(MRSPatient fromMRSPatient, Patient openMrsPatient) {
        MRSPerson person = fromMRSPatient.getPerson();
        updatePersonName(openMrsPatient, person);
        openMrsPatient.setBirthdate(person.getDateOfBirth());
        openMrsPatient.setBirthdateEstimated(person.getBirthDateEstimated());
        openMrsPatient.setGender(person.getGender());

        for (Attribute attribute : person.getAttributes()) {
            PersonAttribute personAttribute = openMrsPatient.getAttribute(attribute.name());
            if (personAttribute != null) {
                openMrsPatient.removeAttribute(personAttribute);
            }
            openMrsPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(attribute.name()), attribute.value()));
        }
        Set<PersonAddress> addresses = openMrsPatient.getAddresses();
        if (!addresses.isEmpty()) {
            PersonAddress address = addresses.iterator().next();
            address.setAddress1(person.getAddress());
        } else {
            final String address = person.getAddress();
            PersonAddress personAddress = new PersonAddress();
            personAddress.setAddress1(address);
            openMrsPatient.addAddress(personAddress);
        }
        openMrsPatient.getPatientIdentifier().setLocation(facilityAdapter.getLocation(fromMRSPatient.getFacility().getId()));
        openMrsPatient.setDead(person.isDead());
        openMrsPatient.setDeathDate(person.deathDate());

        return getMrsPatient(patientService.savePatient(openMrsPatient));
    }

    private void updatePersonName(Patient openMrsPatient, MRSPerson person) {
        if (StringUtils.isNotEmpty(person.getPreferredName())) {
            if (openMrsPatient.getNames().size() == 2) {
                for (PersonName name : openMrsPatient.getNames()) {
                    if (name.isPreferred()) {
                        name.setGivenName(person.getPreferredName());
                    } else {
                        name.setGivenName(person.getFirstName());
                    }
                    name.setMiddleName(person.getMiddleName());
                    name.setFamilyName(person.getLastName());
                }
            } else {
                PersonName personName = openMrsPatient.getPersonName();
                personName.setGivenName(person.getFirstName());
                personName.setMiddleName(person.getMiddleName());
                personName.setFamilyName(person.getLastName());
                PersonName preferredName = new PersonName(person.getPreferredName(), person.getMiddleName(), person.getLastName());
                preferredName.setPreferred(true);
                openMrsPatient.addName(preferredName);
            }
        } else {
            PersonName personName = openMrsPatient.getPersonName();
            personName.setGivenName(person.getFirstName());
            personName.setMiddleName(person.getMiddleName());
            personName.setFamilyName(person.getLastName());
        }
    }

    PatientIdentifierType getPatientIdentifierType(IdentifierType identifierType) {
        return patientService.getPatientIdentifierTypeByName(identifierType.getName());
    }

    MRSPatient getMrsPatient(org.openmrs.Patient patient) {
        final PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
        MRSFacility mrsFacility = (patientIdentifier != null) ? facilityAdapter.convertLocationToFacility(patientIdentifier.getLocation()) : null;
        String motechId = (patientIdentifier != null) ? patientIdentifier.getIdentifier() : null;
        MRSPerson mrsPerson = personAdapter.openMRSToMRSPerson(patient);
        return new MRSPatient(String.valueOf(patient.getId()), motechId, mrsPerson, mrsFacility);
    }

    org.openmrs.Patient getOpenMrsPatient(String patientId) {
        return patientService.getPatient(Integer.parseInt(patientId));
    }

    /**
     * Searches for patients in the MRS system by patient's name and/or MOTECH id
     *
     * @param name     Name of the patient to be searched for (Optional : can be null)
     * @param motechId Motech id of the patient to be searched for (Optional : can be null)
     * @return Matched patients for the given search criteria [if both parameters are null, will return all the patients]
     */
    @Override
    public List<MRSPatient> search(String name, String motechId) {
        List<MRSPatient> patients = convert(patientService.getPatients(name, motechId, Arrays.asList(patientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName())), false),
                new Converter<Patient, MRSPatient>() {
                    @Override
                    public MRSPatient convert(Patient patient) {
                        return getMrsPatient(patient);
                    }
                });
        Collections.sort(patients, new Comparator<MRSPatient>() {
            @Override
            public int compare(MRSPatient personToBeCompared1, MRSPatient personToBeCompared2) {
                if (personToBeCompared1.getPerson().getFirstName() == null && personToBeCompared2.getPerson().getFirstName() == null) {
                    return personToBeCompared1.getMotechId().compareTo(personToBeCompared2.getMotechId());
                } else {
                    if (personToBeCompared1.getPerson().getFirstName() == null) {
                        return -1;
                    } else if (personToBeCompared2.getPerson().getFirstName() == null) {
                        return 1;
                    } else {
                        return personToBeCompared1.getPerson().getFirstName().compareTo(personToBeCompared2.getPerson().getFirstName());
                    }
                }
            }
        });
        return patients;
    }

    /**
     * Marks a patient as dead with the given date of death and comment
     *
     * @param motechId    Deceased patient's MOTECH id
     * @param conceptName Concept name for tracking deceased patients
     * @param dateOfDeath Patient's date of death
     * @param comment     Additional information for the cause of death
     * @throws PatientNotFoundException Throws this exception if patient with the given MOTECH id does not exists
     */
    @Override
    public void deceasePatient(String motechId, String conceptName, Date dateOfDeath, String comment) throws PatientNotFoundException {
        Patient patient = getOpenmrsPatientByMotechId(motechId);
        if (patient == null) {
            throw new PatientNotFoundException("Patient for the MOTECH ID: " + motechId + " is not found");
        }
        patient.setDeathDate(dateOfDeath);
        patient.setDead(true);
        Concept concept = openMrsConceptAdapter.getConceptByName(conceptName);
        patient.setCauseOfDeath(concept);
        patientService.savePatient(patient);
        patientService.saveCauseOfDeathObs(patient, dateOfDeath, concept, comment);
    }

    private List<PersonAttributeType> getAllPersonAttributeTypes() {
        return personService.getAllPersonAttributeTypes(false);
    }

    Patient getOpenmrsPatientByMotechId(String motechId) {
        PatientIdentifierType motechIdType = patientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName());
        List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
        idTypes.add(motechIdType);
        List<org.openmrs.Patient> patients = patientService.getPatients(null, motechId, idTypes, true);
        return (CollectionUtils.isNotEmpty(patients)) ? patients.get(0) : null;
    }
}
