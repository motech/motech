package org.motechproject.couch.mrs.impl;

import org.joda.time.DateTime;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.motechproject.couch.mrs.util.CouchDAOBroker;
import org.motechproject.couch.mrs.util.CouchMRSConverterUtil;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CouchPatientAdapter implements MRSPatientAdapter {

    @Autowired
    private AllCouchPatients allCouchPatients;

    @Autowired
    private CouchDAOBroker daoBroker;

    @Autowired
    private AllCouchPersons allCouchPersons;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public MRSPatient savePatient(MRSPatient patient) {
        CouchPatientImpl couchPatient = (patient instanceof CouchPatientImpl) ? (CouchPatientImpl) patient :
            CouchMRSConverterUtil.createPatient(patient);

        CouchPerson couchPerson = CouchMRSConverterUtil.convertPersonToCouchPerson(patient.getPerson());

        try {
            allCouchPersons.addPerson(couchPerson);
            couchPatient.setPersonId(couchPerson.getPersonId());
            allCouchPatients.addPatient(couchPatient);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventHelper.patientParameters(patient)));
        } catch (MRSCouchException e) {
            return null;
        }

        return patient;
    }

    @Override
    public MRSPatient updatePatient(MRSPatient patient) {
        List<CouchPatientImpl> patients = allCouchPatients.findByMotechId(patient.getMotechId());

        if (patients != null && patients.get(0) != null) {
            CouchPatientImpl patientToUpdate = patients.get(0);
            CouchPatientImpl patientTemp = CouchMRSConverterUtil.createPatient(patient);

            CouchPerson personToUpdate = allCouchPersons.findByPersonId(patientTemp.getPersonId()).get(0);
            List<MRSAttribute> attributeList = CouchMRSConverterUtil.createAttributeList(patient.getPerson());

            personToUpdate.setAddress(patient.getPerson().getAddress());
            personToUpdate.setFirstName(patient.getPerson().getFirstName());
            personToUpdate.setLastName(patient.getPerson().getLastName());
            personToUpdate.setAge(patient.getPerson().getAge());
            personToUpdate.setBirthDateEstimated(patient.getPerson().getBirthDateEstimated());
            personToUpdate.setDateOfBirth(patient.getPerson().getDateOfBirth());
            personToUpdate.setDead(patient.getPerson().isDead());
            personToUpdate.setDeathDate(patient.getPerson().getDeathDate());
            personToUpdate.setGender(patient.getPerson().getGender());
            personToUpdate.setMiddleName(patient.getPerson().getMiddleName());
            personToUpdate.setPersonId(patient.getPerson().getPersonId());
            personToUpdate.setPreferredName(patient.getPerson().getPreferredName());
            personToUpdate.setAttributes(attributeList);
            allCouchPersons.update(personToUpdate);

            patientToUpdate.setMotechId(patient.getMotechId());
            patientToUpdate.setPatientId(patient.getPatientId());
            patientToUpdate.setFacilityId(patientTemp.getFacilityId());
            patientToUpdate.setPersonId(patientTemp.getPersonId());
            allCouchPatients.update(patientToUpdate);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PATIENT_SUBJECT, EventHelper.patientParameters(patient)));
        }

        return patient;
    }

    @Override
    public MRSPatient getPatient(String patientId) {

        return daoBroker.buildFullPatient(allCouchPatients.findByPatientId(patientId));

    }

    @Override
    public MRSPatient getPatientByMotechId(String motechId) {

        return daoBroker.buildFullPatient(allCouchPatients.findByMotechId(motechId));

    }

    @Override
    public List<MRSPatient> search(String name, String motechId) {
        MRSPatient receivedPatient = daoBroker.buildFullPatient(allCouchPatients.findByMotechId(motechId));
        List<MRSPatient> patientsList = new ArrayList<MRSPatient>();
        if (receivedPatient.getPerson().getPreferredName().contains(name)) {
            patientsList.add(receivedPatient);
            return patientsList;
        } else {
            return null;
        }
    }

    @Override
    public Integer getAgeOfPatientByMotechId(String motechId) {
        MRSPatient patient = getPatientByMotechId(motechId);

        MRSPerson person = patient.getPerson();

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

        CouchPerson personToUpdate = allCouchPersons.findByPersonId(patients.get(0).getPersonId()).get(0);
        personToUpdate.setDead(true);
        personToUpdate.setDeathDate(new DateTime(dateOfDeath));

        allCouchPersons.update(personToUpdate);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.PATIENT_DECEASED_SUBJECT, EventHelper.patientParameters(daoBroker.buildFullPatient(patients))));
    }


    @Override
    public List<MRSPatient> getAllPatients() {
        return generatePatientList(allCouchPatients.findAllPatients());
    }

    @Override
    public void deletePatient(MRSPatient patient) throws PatientNotFoundException {
        List<CouchPatientImpl> patients = allCouchPatients.findByMotechId(patient.getMotechId());

        if (patients == null || patients.size() == 0) {
            throw new PatientNotFoundException("The patient by motech Id: " + patient.getMotechId() + " was not found in the database.");
        }
        CouchPerson personToRemove = allCouchPersons.findByPersonId(patients.get(0).getPersonId()).get(0);
        allCouchPersons.remove(personToRemove);

        allCouchPatients.remove(patients.get(0));
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_PATIENT_SUBJECT, EventHelper.patientParameters(patient)));
    }

    private List<MRSPatient> generatePatientList(List<CouchPatientImpl> patients) {

        List<MRSPatient> patientsList = new ArrayList<>();

        for (CouchPatientImpl couchPatient : patients) {
            MRSPatient patient = getPatientByMotechId(couchPatient.getMotechId());
            if (patient != null) {
                patientsList.add(patient);
            }
        }

        return patientsList;
    }
}
