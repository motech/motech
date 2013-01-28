package org.motechproject.openmrs.services;

import org.junit.Test;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.OpenMRSEncounter;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.ObservationAdapter;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OpenMRSObservationAdapterIT extends OpenMRSIntegrationTestBase {
    @Autowired
    private ObservationAdapter openMrsObservationAdapter;

    @Autowired
    private EncounterAdapter mrsEncounterAdapter;

    @Autowired
    ObsService obsService;

    Facility facility;
    Patient patientAlan;

    @Override
    public void doOnceBefore() {
        facility = facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldVoidObservationsEvenWithDuplicateCalls() throws UserAlreadyExistsException, ObservationNotFoundException {

        Person personCreator = new OpenMRSPerson().firstName("SampleTest");
        Person personJohn = new OpenMRSPerson().firstName("John");
        patientAlan = createPatient(facility);
        OpenMRSUser userCreator = createUser(new OpenMRSUser().userName("UserSuper").systemId("1000015").securityRole("Provider").person((OpenMRSPerson) personCreator));
        OpenMRSUser userJohn = createUser(new OpenMRSUser().userName("UserJohn").systemId("1000027").securityRole("Provider").person((OpenMRSPerson) personJohn));
        OpenMRSPerson provider = userJohn.getPerson();

        Set<OpenMRSObservation> observations = new HashSet<OpenMRSObservation>();
        observations.add(new OpenMRSObservation<Double>(new Date(), "GRAVIDA", Double.valueOf("100.0")));
        final String encounterType = "PEDSRETURN";
        OpenMRSEncounter expectedEncounter = new OpenMRSEncounter.MRSEncounterBuilder().withProviderId(provider.getId()).withCreatorId(userCreator.getUserId()).withFacilityId(facility.getFacilityId()).withDate(new Date()).withPatientId(patientAlan.getPatientId()).withObservations(observations).withEncounterType(encounterType).build();
        mrsEncounterAdapter.createEncounter(expectedEncounter);

        final OpenMRSObservation mrsObservation = (OpenMRSObservation) openMrsObservationAdapter.findObservation(patientAlan.getMotechId(), "GRAVIDA");

        openMrsObservationAdapter.voidObservation(mrsObservation, "reason 1", patientAlan.getMotechId());
        final String reason2 = "reason 2";
        openMrsObservationAdapter.voidObservation(mrsObservation, reason2, patientAlan.getMotechId());

        final Obs actualOpenMRSObs = obsService.getObs(Integer.valueOf(mrsObservation.getId()));
        assertTrue(actualOpenMRSObs.isVoided());
        assertThat(actualOpenMRSObs.getVoidReason(), is(reason2));
    }
}
