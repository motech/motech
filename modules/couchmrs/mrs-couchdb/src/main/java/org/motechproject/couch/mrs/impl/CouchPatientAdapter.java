package org.motechproject.couch.mrs.impl;

import org.joda.time.DateTime;
import org.motechproject.couch.mrs.model.CouchAttribute;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchPatient;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.PatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CouchPatientAdapter implements PatientAdapter {

    @Autowired
    private AllCouchPatients allCouchPatients;

    @Autowired
    private AllCouchFacilities allCouchFacilities;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public Patient savePatient(Patient patient) {
        CouchPatientImpl couchPatient = (patient instanceof CouchPatientImpl) ? (CouchPatientImpl) patient :
                createPatient(patient);

        try {
            allCouchPatients.addPatient(couchPatient);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventHelper.patientParameters(patient)));
        } catch (MRSCouchException e) {
            return null;
        }

        return patient;
    }

    @Override
    public Patient updatePatient(Patient patient) {
        List<CouchPatientImpl> patients = allCouchPatients.findByMotechId(patient.getMotechId());

        if (patients != null && patients.get(0) != null) {
            CouchPatientImpl patientToUpdate = patients.get(0);
            CouchPatientImpl patientTemp = createPatient(patient);

            patientToUpdate.setMotechId(patient.getMotechId());
            patientToUpdate.setPatientId(patient.getPatientId());
            patientToUpdate.setFacilityId(patientTemp.getFacilityId());
            patientToUpdate.setPerson(patientTemp.getPerson());

            allCouchPatients.update(patientToUpdate);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PATIENT_SUBJECT, EventHelper.patientParameters(patient)));
        }

        return patient;
    }

    @Override
    public Patient getPatient(String patientId) {

        return returnPatient(allCouchPatients.findByPatientId(patientId));

    }

    @Override
    public Patient getPatientByMotechId(String motechId) {

        return returnPatient(allCouchPatients.findByMotechId(motechId));

    }

    @Override
    public List<Patient> search(String name, String motechId) {
        return generatePatientList(allCouchPatients.findByNameAndMotechId(name, motechId));
    }

    @Override
    public Integer getAgeOfPatientByMotechId(String motechId) {
        Patient patient = getPatientByMotechId(motechId);

        Person person = patient.getPerson();

        if (person != null) {
            return person.getAge();
        }

        return null;
    }

    @Override
    public void deceasePatient(String motechId, String conceptName,
            Date dateOfDeath, String comment) throws PatientNotFoundException {
        List<CouchPatientImpl> patients = allCouchPatients.findByMotechId(motechId);

        if (patients == null || patients.size() == 0) {
            throw new PatientNotFoundException("The patient by motech Id: " + motechId + " was not found in the database.");
        }

        Person personToUpdate = patients.get(0).getPerson();

        personToUpdate.setDead(true);
        personToUpdate.setDeathDate(new DateTime(dateOfDeath));

        allCouchPatients.update(patients.get(0));
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.PATIENT_DECEASED_SUBJECT, EventHelper.patientParameters(returnPatient(patients))));
    }


    @Override
    public List<Patient> getAllPatients(){
        return generatePatientList(allCouchPatients.findAllPatients());
    }

    private Patient returnPatient(List<CouchPatientImpl> couchPatients) {

        if (couchPatients != null && couchPatients.size() > 0) {
            CouchPatientImpl couchPatient = couchPatients.get(0);
            String facilityId = couchPatient.getFacilityId();
            List<CouchFacility> facilities = allCouchFacilities.findByFacilityId(facilityId);
            CouchFacility facility = null;
            if (facilities != null && facilities.size() > 0) {
                facility = facilities.get(0);
            }
            return new CouchPatient(couchPatient.getPatientId(), couchPatient.getMotechId(), couchPatient.getPerson(), facility);
        }

        return null;
    }

    private List<Patient> generatePatientList(List<CouchPatientImpl> patients) {

        List<Patient> patientsList = new ArrayList<Patient>();

        for (CouchPatientImpl couchPatient : patients) {
            Patient patient = getPatientByMotechId(couchPatient.getMotechId());
            if (patient != null) {
                patientsList.add(patient);
            }
        }

        return patientsList;
    }

    private CouchPatientImpl createPatient (Patient patient) {
        List<Attribute> attributeList = new ArrayList<>();

        if (patient.getPerson() != null) {
            for (Attribute attribute : patient.getPerson().getAttributes()){
                CouchAttribute couchAttribute = new CouchAttribute();
                couchAttribute.setName(attribute.getName());
                couchAttribute.setValue(attribute.getValue());

                attributeList.add(couchAttribute);
            }
        }

        CouchPerson person = new CouchPerson();
        person.setAddress(patient.getPerson().getAddress());
        person.setFirstName(patient.getPerson().getFirstName());
        person.setLastName(patient.getPerson().getLastName());
        person.setAge(patient.getPerson().getAge());
        person.setBirthDateEstimated(patient.getPerson().getBirthDateEstimated());
        person.setDateOfBirth(patient.getPerson().getDateOfBirth());
        person.setDead(patient.getPerson().isDead());
        person.setDeathDate(patient.getPerson().getDeathDate());
        person.setGender(patient.getPerson().getGender());
        person.setMiddleName(patient.getPerson().getMiddleName());
        person.setPersonId(patient.getPerson().getPersonId());
        person.setPreferredName(patient.getPerson().getPreferredName());
        person.setAttributes(attributeList);

        Facility facility = patient.getFacility();

        String facilityId = null;
        if (facility != null) {
            facilityId = facility.getFacilityId();
        }

        return new CouchPatientImpl(patient.getPatientId(), patient.getMotechId(), person, facilityId);
    }
}
