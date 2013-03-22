package org.motechproject.openmrs.services;

import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.motechproject.openmrs.model.OpenMRSEncounter;
import org.motechproject.openmrs.model.OpenMRSFacility;
import org.motechproject.openmrs.model.OpenMRSObservation;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.model.OpenMRSUser;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OpenMRSObservationAdapterIT extends OpenMRSIntegrationTestBase {
    @Autowired
    private MRSObservationAdapter openMrsObservationAdapter;

    @Autowired
    private MRSEncounterAdapter mrsEncounterAdapter;

    @Autowired
    ObsService obsService;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();
    MRSFacility facility;
    MRSPatient patientAlan;

    @Override
    public void doOnceBefore() {
        facility = facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldVoidObservationsEvenWithDuplicateCalls() throws UserAlreadyExistsException, ObservationNotFoundException, InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.DELETED_OBSERVATION_SUBJECT));

        MRSPerson personCreator = new OpenMRSPerson().firstName("SampleTest");
        MRSPerson personJohn = new OpenMRSPerson().firstName("John");
        patientAlan = createPatient(facility);
        MRSUser userCreator = createUser(new OpenMRSUser().userName("UserSuper").systemId("1000015").securityRole("Provider").person(personCreator));
        MRSUser userJohn = createUser(new OpenMRSUser().userName("UserJohn").systemId("1000027").securityRole("Provider").person(personJohn));
        MRSPerson provider = userJohn.getPerson();

        Set<OpenMRSObservation> observations = new HashSet<OpenMRSObservation>();
        observations.add(new OpenMRSObservation<Double>(new Date(), "GRAVIDA", Double.valueOf("100.0")));
        final String encounterType = "PEDSRETURN";
        MRSEncounter expectedEncounter = new OpenMRSEncounter.MRSEncounterBuilder().withProviderId(provider.getPersonId()).withCreatorId(userCreator.getUserId()).withFacilityId(facility.getFacilityId()).withDate(new Date()).withPatientId(patientAlan.getPatientId()).withObservations(observations).withEncounterType(encounterType).build();
        mrsEncounterAdapter.createEncounter(expectedEncounter);


        final OpenMRSObservation mrsObservation = (OpenMRSObservation) openMrsObservationAdapter.findObservation(patientAlan.getMotechId(), "GRAVIDA");

        synchronized (lock) {
            openMrsObservationAdapter.voidObservation(mrsObservation, "reason 1", patientAlan.getMotechId());
            lock.wait(60000);
        }

        final String reason2 = "reason 2";

        synchronized (lock) {
            openMrsObservationAdapter.voidObservation(mrsObservation, reason2, patientAlan.getMotechId());
            lock.wait(60000);
        }

        assertTrue(mrsListener.deleted);
        assertEquals(mrsObservation.getConceptName(), mrsListener.eventParameters.get(EventKeys.OBSERVATION_CONCEPT_NAME));
        assertEquals(mrsObservation.getDate(), mrsListener.eventParameters.get(EventKeys.OBSERVATION_DATE));
        assertEquals(mrsObservation.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));

        final Obs actualOpenMRSObs = obsService.getObs(Integer.valueOf(mrsObservation.getId()));
        assertTrue(actualOpenMRSObs.isVoided());
        assertThat(actualOpenMRSObs.getVoidReason(), is(reason2));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean deleted = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.DELETED_OBSERVATION_SUBJECT})
        public void handle(MotechEvent event) {
            deleted = true;
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
