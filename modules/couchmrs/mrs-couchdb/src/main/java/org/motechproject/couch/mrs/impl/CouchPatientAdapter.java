package org.motechproject.couch.mrs.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchPatient;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.services.PatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouchPatientAdapter implements PatientAdapter {

    @Autowired
    private AllCouchPatients allCouchPatients;

    @Autowired
    private AllCouchFacilities allCouchFacilities;

    @Override
    public Patient savePatient(Patient patient) {
        Facility facility = patient.getFacility();

        String facilityId = null;
        if (facility != null) {
            facilityId = facility.getFacilityId();
        }

        CouchPatientImpl couchPatient = new CouchPatientImpl(patient.getPatientId(), patient.getMotechId(), patient.getPerson(), facilityId);

        try {
            allCouchPatients.addPatient(couchPatient);
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
            Facility updatedFacility = patient.getFacility();

            if (updatedFacility != null) {
                patientToUpdate.setFacilityId(updatedFacility.getFacilityId());
            }

            patientToUpdate.setMotechId(patient.getMotechId());
            patientToUpdate.setPatientId(patient.getPatientId());
            patientToUpdate.setPerson((CouchPerson) patient.getPerson());
            allCouchPatients.update(patientToUpdate);
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
}
