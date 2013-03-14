package org.motechproject.openmrs.services;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.motechproject.openmrs.util.PatientTestUtil;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OpenMRSPatientAdapterIT extends OpenMRSIntegrationTestBase {

    @Autowired
    private LocationService locationService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    @Transactional(readOnly = true)
    public void shouldSaveAPatientAndRetrieve() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT));
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;
        String motechId = "1234567";

        final OpenMRSFacility savedFacility = (OpenMRSFacility) facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));

        OpenMRSPerson mrsPerson = new OpenMRSPerson().firstName(first).middleName(middle).lastName(last).dateOfBirth(new DateTime(birthDate)).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address1);
        final org.motechproject.mrs.domain.Patient patient = new OpenMRSPatient(motechId, mrsPerson, savedFacility);
        final org.motechproject.mrs.domain.Patient savedPatient;

        synchronized (lock) {
            savedPatient = patientAdapter.savePatient(patient);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertEquals(savedPatient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(savedPatient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(savedPatient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(savedPatient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        eventListenerRegistry.clearListenersForBean("mrsTestListener");
        new PatientTestUtil().verifyReturnedPatient(first, middle, last, address1, birthDate, birthDateEstimated, gender, savedFacility, savedPatient, motechId);
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldUpdateAPatient() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT));

        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;
        String motechId = "1234567";

        final OpenMRSPatient savedPatient;

        synchronized (lock) {
            savedPatient = (OpenMRSPatient) createMRSPatient(first, middle, last, address1, birthDate, gender, birthDateEstimated, motechId);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertEquals(savedPatient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(savedPatient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(savedPatient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(savedPatient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        final String updatedMiddleName = "new middle name";
        OpenMRSPerson mrsPersonUpdated = new OpenMRSPerson().firstName(first).middleName(updatedMiddleName).lastName(last).dateOfBirth(new DateTime(birthDate)).birthDateEstimated(birthDateEstimated)
                .gender(gender).address("address changed").addAttribute(new OpenMRSAttribute("Insured", "true")).addAttribute(new OpenMRSAttribute("NHIS Number", "123465"));

        final OpenMRSFacility changedFacility = (OpenMRSFacility) facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", null, null));
        final org.motechproject.mrs.domain.Patient patientToBeUpdated = new OpenMRSPatient(savedPatient.getPatientId(), "1234567", mrsPersonUpdated, changedFacility);
        final org.motechproject.mrs.domain.Patient updatedPatient;

        synchronized (lock) {
            updatedPatient = patientAdapter.updatePatient(patientToBeUpdated);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertTrue(mrsListener.updated);
        assertEquals(updatedPatient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(updatedPatient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(updatedPatient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(updatedPatient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        assertThat(savedPatient.getMotechId(), is(equalTo(updatedPatient.getMotechId())));
        assertThat(updatedPatient.getPerson().getMiddleName(), is(equalTo(updatedMiddleName)));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    private org.motechproject.mrs.domain.Patient createMRSPatient(String first, String middle, String last, String address1, Date birthDate, String gender, Boolean birthDateEstimated, String motechId) {
        final OpenMRSFacility savedFacility = (OpenMRSFacility) facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));

        OpenMRSPerson mrsPerson = new OpenMRSPerson().firstName(first).middleName(middle).lastName(last).dateOfBirth(new DateTime(birthDate)).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address1).addAttribute(new OpenMRSAttribute("Insured", "true"));
        final OpenMRSPatient patient = new OpenMRSPatient(motechId, mrsPerson, savedFacility);
        return patientAdapter.savePatient(patient);
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldSearchPatientsByNameOrId() {
//        Should also handle when name is null in the DB for production records
        final String motechId1 = "423546";
        final String firstName1 = "Amesh";
        final String middleName1 = "Ben";
        final String lastName1 = "Doug";

        final String motechId2 = "12356";
        final String firstName2 = "Amet";
        final String middleName2 = "Brit";
        final String lastName2 = "Cathey";

        final String motechId3 = "7890";
        final String firstName3 = null;
        final String middleName3 = "nullFirstNameCheck1";
        final String lastName3 = "Douglas";

        final String motechId4 = "7891";
        final String firstName4 = null;
        final String middleName4 = "nullFirstNameCheck1";
        final String lastName4 = "Catherina";

        final String address = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;

        final OpenMRSFacility savedFacility = (OpenMRSFacility) facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));

        createPatientInOpenMrs(motechId1, firstName1, middleName1, lastName1, address, birthDate, gender, birthDateEstimated, savedFacility);
        createPatientInOpenMrs(motechId2, firstName2, middleName2, lastName2, address, birthDate, gender, birthDateEstimated, savedFacility);
        createPatientInOpenMrs(motechId3, firstName3, middleName3, lastName3, address, birthDate, gender, birthDateEstimated, savedFacility);
        createPatientInOpenMrs(motechId4, firstName4, middleName4, lastName4, address, birthDate, gender, birthDateEstimated, savedFacility);

        List<org.motechproject.mrs.domain.Patient> returnedPatients = patientAdapter.search("Am", null);

        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);
        new PatientTestUtil().verifyReturnedPatient(firstName2, middleName2, lastName2, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(1), motechId2);
        assertThat(returnedPatients.size(), is(equalTo(2)));

        assertThat(patientAdapter.search("x", null), is(equalTo(Arrays.<org.motechproject.mrs.domain.Patient>asList())));

        returnedPatients = patientAdapter.search("Ames", null);
        assertThat(returnedPatients.size(), is(equalTo(1)));
        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);

        returnedPatients = patientAdapter.search(null, "423546");
        assertThat(returnedPatients.size(), is(equalTo(1)));
        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);

        returnedPatients = patientAdapter.search(null, "23");
        assertThat(returnedPatients.size(), is(equalTo(0)));

        assertThat(patientAdapter.search(null, "0000"), is(equalTo(Arrays.<org.motechproject.mrs.domain.Patient>asList())));

        returnedPatients = patientAdapter.search("Cathey", "12356");
        assertThat(returnedPatients.size(), is(equalTo(1)));
        new PatientTestUtil().verifyReturnedPatient(firstName2, middleName2, lastName2, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId2);

        //Both First Name Are null
        returnedPatients = patientAdapter.search("nullFirstNameCheck", null);
        assertThat(returnedPatients.size(), is(equalTo(2)));

        //One of the first name is null
        returnedPatients = patientAdapter.search("Doug", null);
        assertThat(returnedPatients.size(), is(equalTo(2)));
        returnedPatients = patientAdapter.search("Cath", null);
        assertThat(returnedPatients.size(), is(equalTo(2)));

        returnedPatients = patientAdapter.search("A", "123");
        assertThat(returnedPatients.size(), is(equalTo(0)));

        assertThat(patientAdapter.search("x", "0000"), is(equalTo(Arrays.<org.motechproject.mrs.domain.Patient>asList())));
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldCreatePatientInIdempotentWayWithMotechId() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT));

        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;
        String motechId = "1234567";

        final OpenMRSFacility savedFacility = (OpenMRSFacility) facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));

        OpenMRSPerson mrsPerson = new OpenMRSPerson().firstName(first).middleName(middle).lastName(last).dateOfBirth(new DateTime(birthDate)).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address1);
        final OpenMRSPatient patient = new OpenMRSPatient(motechId, mrsPerson, savedFacility);
        final OpenMRSPatient savedPatient;

        synchronized (lock) {
            savedPatient = (OpenMRSPatient) patientAdapter.savePatient(patient);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertEquals(savedPatient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(savedPatient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(savedPatient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(savedPatient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        final OpenMRSPatient duplicatePatient;

        synchronized (lock) {
            duplicatePatient = (OpenMRSPatient) patientAdapter.savePatient(patient);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertTrue(mrsListener.updated);
        assertEquals(duplicatePatient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(duplicatePatient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(duplicatePatient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(duplicatePatient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        assertThat(savedPatient.getMotechId(), is(duplicatePatient.getMotechId()));
        new PatientTestUtil().verifyReturnedPatient(first, middle, last, address1, birthDate, birthDateEstimated, gender, savedFacility, savedPatient, motechId);
        new PatientTestUtil().verifyReturnedPatient(first, middle, last, address1, birthDate, birthDateEstimated, gender, savedFacility, duplicatePatient, motechId);
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldSaveCauseOfDeath() throws Exception {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT));

        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;
        String motechId = "1234567";

        final OpenMRSPatient savedPatient;

        synchronized (lock) {
            savedPatient = (OpenMRSPatient) createMRSPatient(first, middle, last, address1, birthDate, gender, birthDateEstimated, motechId);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertEquals(savedPatient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(savedPatient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(savedPatient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(savedPatient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        Date dateOfDeath = new Date();

        synchronized (lock) {
            patientAdapter.deceasePatient(savedPatient.getMotechId(), "NONE", dateOfDeath, null);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertTrue(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertEquals(savedPatient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(savedPatient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(savedPatient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(savedPatient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        Patient actualPatient = patientService.getPatient(Integer.valueOf(savedPatient.getPatientId()));
        assertThat(actualPatient.isDead(), is(true));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldGetAllPatientsList() {

        final String motechId1 = "423546";
        final String firstName1 = "Amesh";
        final String middleName1 = "Ben";
        final String lastName1 = "Doug";

        final String motechId2 = "12356";
        final String firstName2 = "Amet";
        final String middleName2 = "Brit";
        final String lastName2 = "Cathey";

        final String motechId3 = "7890";
        final String firstName3 = null;
        final String middleName3 = "nullFirstNameCheck1";
        final String lastName3 = "Douglas";

        final String motechId4 = "7891";
        final String firstName4 = null;
        final String middleName4 = "nullFirstNameCheck1";
        final String lastName4 = "Catherina";

        final String address = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;

        final OpenMRSFacility savedFacility = (OpenMRSFacility) facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));

        createPatientInOpenMrs(motechId1, firstName1, middleName1, lastName1, address, birthDate, gender, birthDateEstimated, savedFacility);
        createPatientInOpenMrs(motechId2, firstName2, middleName2, lastName2, address, birthDate, gender, birthDateEstimated, savedFacility);
        createPatientInOpenMrs(motechId3, firstName3, middleName3, lastName3, address, birthDate, gender, birthDateEstimated, savedFacility);
        createPatientInOpenMrs(motechId4, firstName4, middleName4, lastName4, address, birthDate, gender, birthDateEstimated, savedFacility);

        List<org.motechproject.mrs.domain.Patient> returnedPatients = patientAdapter.getAllPatients();

        assertThat(returnedPatients.size(), is(equalTo(4)));
        assertThat(returnedPatients.get(0).getPerson().getAddress(), is(equalTo(address)));
    }


    private org.motechproject.mrs.domain.Patient createPatientInOpenMrs(String motechId, String firstName, String middleName, String lastName, String address, Date birthDate, String gender, Boolean birthDateEstimated, OpenMRSFacility savedFacility) {

        OpenMRSPerson mrsPerson = new OpenMRSPerson().firstName(firstName).middleName(middleName).lastName(lastName).dateOfBirth(new DateTime(birthDate)).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address);
        final OpenMRSPatient patient = new OpenMRSPatient(motechId, mrsPerson, savedFacility);
        return patientAdapter.savePatient(patient);
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean updated = false;
        private boolean deceased = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_PATIENT_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.UPDATED_PATIENT_SUBJECT)) {
                updated = true;
            } else if (event.getSubject().equals(EventKeys.PATIENT_DECEASED_SUBJECT)) {
                deceased = true;
            }
            eventParameters = event.getParameters();
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public String getIdentifier() {
            return "mrsTestListener";
        }
    }
}
