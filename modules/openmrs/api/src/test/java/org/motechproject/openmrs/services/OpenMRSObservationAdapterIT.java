package org.motechproject.openmrs.services;

import org.junit.Test;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSObservationAdapter;
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
    private MRSObservationAdapter openMrsObservationAdapter;

    @Autowired
    private MRSEncounterAdapter mrsEncounterAdapter;

    @Autowired
    ObsService obsService;

    MRSFacility facility;
    MRSPatient patientAlan;

    @Override
    public void doOnceBefore() {
        facility = facilityAdapter.saveFacility(new MRSFacility("name", "country", "region", "district", "province"));
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldVoidObservationsEvenWithDuplicateCalls() throws UserAlreadyExistsException, ObservationNotFoundException {

        MRSPerson personCreator = new MRSPerson().firstName("SampleTest");
        MRSPerson personJohn = new MRSPerson().firstName("John");
        patientAlan = createPatient(facility);
        MRSUser userCreator = createUser(new MRSUser().userName("UserSuper").systemId("1000015").securityRole("Provider").person(personCreator));
        MRSUser userJohn = createUser(new MRSUser().userName("UserJohn").systemId("1000027").securityRole("Provider").person(personJohn));
        MRSPerson provider = userJohn.getPerson();

        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        observations.add(new MRSObservation<Double>(new Date(), "GRAVIDA", Double.valueOf("100.0")));
        final String encounterType = "PEDSRETURN";
        MRSEncounter expectedEncounter = new MRSEncounter.MRSEncounterBuilder().withProviderId(provider.getId()).withCreatorId(userCreator.getId()).withFacilityId(facility.getId()).withDate(new Date()).withPatientId(patientAlan.getId()).withObservations(observations).withEncounterType(encounterType).build();
        mrsEncounterAdapter.createEncounter(expectedEncounter);

        final MRSObservation mrsObservation = openMrsObservationAdapter.findObservation(patientAlan.getMotechId(), "GRAVIDA");

        openMrsObservationAdapter.voidObservation(mrsObservation, "reason 1", patientAlan.getMotechId());
        final String reason2 = "reason 2";
        openMrsObservationAdapter.voidObservation(mrsObservation, reason2, patientAlan.getMotechId());

        final Obs actualOpenMRSObs = obsService.getObs(Integer.valueOf(mrsObservation.getId()));
        assertTrue(actualOpenMRSObs.isVoided());
        assertThat(actualOpenMRSObs.getVoidReason(), is(reason2));
    }
}
