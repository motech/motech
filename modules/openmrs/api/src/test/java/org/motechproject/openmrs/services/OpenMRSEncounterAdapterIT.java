package org.motechproject.openmrs.services;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.matcher.LambdaJMatcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.OpenMRSEncounter;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.motechproject.commons.date.util.DateUtil;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.motechproject.openmrs.TestIdGenerator.newGUID;
import static org.motechproject.commons.date.util.DateUtil.newDate;

public class OpenMRSEncounterAdapterIT extends OpenMRSIntegrationTestBase {

    @Autowired
    EncounterAdapter mrsEncounterAdapter;
    @Autowired
    EncounterService encounterService;
    @Autowired
    ObsService obsService;
    @Autowired
    PatientService patientService;
    @Autowired
    UserService userService;
    @Autowired
    ConceptService conceptService;
    @Autowired
    OpenMRSUserAdapter userAdapter;

    Facility facility;
    Patient patientAlan;
    @Autowired
    private OpenMRSObservationAdapter openMRSObservationAdapter;

    public void doOnceBefore() {
        facility = facilityAdapter.saveFacility(new OpenMRSFacility("name", "country", "region", "district", "province"));
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldSaveEncounterWithObservationsAndReturn() throws UserAlreadyExistsException {

        Person personCreator = new OpenMRSPerson();
        personCreator.setFirstName("SampleTest");
        Person personJohn = new OpenMRSPerson();
        personJohn.setFirstName("John");
        patientAlan = createPatient(facility);
        OpenMRSUser userCreator = createUser(new OpenMRSUser().userName("UserSuper").systemId("1000015").securityRole("Provider").person((OpenMRSPerson) personCreator));
        OpenMRSUser userJohn = createUser(new OpenMRSUser().userName("UserJohn").systemId("1000027").securityRole("Provider").person((OpenMRSPerson) personJohn));
        Person provider = userJohn.getPerson();

        Set<Observation> observations = new HashSet<Observation>();
        observations.add(new OpenMRSObservation<Double>(new Date(), "GRAVIDA", Double.valueOf("100.0")));
        observations.add(new OpenMRSObservation<String>(new Date(), "SERIAL NUMBER", "free text data serail number"));
        observations.add(new OpenMRSObservation<Date>(new Date(), "NEXT ANC DATE", new DateTime(2012, 11, 10, 1, 10).toDate()));
        observations.add(new OpenMRSObservation<Boolean>(new Date(), "PREGNANCY STATUS", false));
        final String encounterType = "PEDSRETURN";
        OpenMRSEncounter expectedEncounter = new OpenMRSEncounter.MRSEncounterBuilder().withProviderId(provider.getPersonId()).withCreatorId(userCreator.getUserId()).withFacilityId(facility.getFacilityId()).withDate(new Date()).withPatientId(patientAlan.getPatientId()).withObservations(observations).withEncounterType(encounterType).build();
        OpenMRSEncounter actualMRSEncounter = (OpenMRSEncounter) mrsEncounterAdapter.createEncounter(expectedEncounter);
        assertEncounter(expectedEncounter, actualMRSEncounter);

        final OpenMRSEncounter mrsEncounter = (OpenMRSEncounter) mrsEncounterAdapter.getLatestEncounterByPatientMotechId(patientAlan.getMotechId(), encounterType);
        assertEncounter(expectedEncounter, mrsEncounter);
    }

    @Test
    @Transactional(readOnly = true)
    public void creationOfEncounterShouldHappenInIdempotentWay() throws UserAlreadyExistsException {

        Person personCreator = new OpenMRSPerson().firstName("SampleTest");
        Person personJohn = new OpenMRSPerson().firstName("John");
        patientAlan = createPatient(facility);
        OpenMRSUser userCreator = createUser(new OpenMRSUser().userName(newGUID("UserAdmin")).systemId(newGUID("10000151")).securityRole("Provider").person((OpenMRSPerson) personCreator));
        OpenMRSUser userJohn = createUser(new OpenMRSUser().userName(newGUID("UserJohn1")).systemId(newGUID("10000271")).securityRole("Provider").person((OpenMRSPerson) personJohn));
        OpenMRSPerson provider = userJohn.getPerson();

        final Set<Observation> observations = new HashSet<Observation>();
        final Date encounterTime = DateUtil.newDateTime(newDate(2012, 3, 4), 3, 4, 3).toDate();
        final Date observationDate = new Date();
        observations.add(new OpenMRSObservation(observationDate, "SERIAL NUMBER", "free text data serail number"));
        observations.add(new OpenMRSObservation(observationDate, "NEXT ANC DATE", new DateTime(2012, 11, 10, 1, 10).toDate()));
        observations.add(new OpenMRSObservation(observationDate, "GRAVIDA", Double.valueOf("100.0")));
        final String encounterType = "PEDSRETURN";
        OpenMRSEncounter expectedEncounter = new OpenMRSEncounter.MRSEncounterBuilder().withProviderId(provider.getId()).withCreatorId(userCreator.getUserId()).withFacilityId(facility.getFacilityId()).withDate(encounterTime).withPatientId(patientAlan.getPatientId()).withObservations(observations).withEncounterType(encounterType).build();
        OpenMRSEncounter duplicateEncounter = new OpenMRSEncounter.MRSEncounterBuilder().withProviderId(provider.getId()).withCreatorId(userCreator.getUserId()).withFacilityId(facility.getFacilityId()).withDate(encounterTime).withPatientId(patientAlan.getPatientId()).withObservations(new HashSet<Observation>() {{
            add(new OpenMRSObservation(observationDate, "SERIAL NUMBER", "free text data serail number"));
            add(new OpenMRSObservation(observationDate, "NEXT ANC DATE", new DateTime(2012, 11, 10, 1, 10).toDate()));
            add(new OpenMRSObservation(observationDate, "PREGNANCY STATUS", false));
        }}).withEncounterType(encounterType).build();

        OpenMRSEncounter oldEncounter = (OpenMRSEncounter) mrsEncounterAdapter.createEncounter(expectedEncounter);
        OpenMRSEncounter newEncounter = (OpenMRSEncounter) mrsEncounterAdapter.createEncounter(duplicateEncounter);
        assertEncounter(expectedEncounter, oldEncounter);
        assertEncounter(duplicateEncounter, newEncounter);
        assertThat(newEncounter.getId(), not(is(oldEncounter.getId())));

        List<OpenMRSObservation> oldObservations = Lambda.select(oldEncounter.getObservations(), having(on(OpenMRSObservation.class).getConceptName(), isIn(asList("SERIAL NUMBER", "NEXT ANC DATE"))));
        List<OpenMRSObservation> newObservations = Lambda.select(newEncounter.getObservations(), having(on(OpenMRSObservation.class).getConceptName(), isIn(asList("SERIAL NUMBER", "NEXT ANC DATE"))));
        assertObservation(new HashSet<Observation>(oldObservations), new HashSet<Observation>(newObservations));

        assertTrue(CollectionUtils.isEmpty(Lambda.select(newEncounter.getObservations(), having(on(OpenMRSObservation.class).getConceptName(), is("GRAVIDA")))));
        assertTrue(CollectionUtils.isNotEmpty(Lambda.select(newEncounter.getObservations(), having(on(OpenMRSObservation.class).getConceptName(), is("PREGNANCY STATUS")))));

        final List<String> oldObservationIds = extract(oldEncounter.getObservations(), on(OpenMRSObservation.class).getId());
        for (String observationId : oldObservationIds) {
            assertNull("[" + observationId + "]," + newEncounter.getObservations(), obsService.getObs(Integer.parseInt(observationId)));
        }
    }

    private void assertEncounter(OpenMRSEncounter expectedEncounter, OpenMRSEncounter actualMRSEncounter) {
        assertEquals(expectedEncounter.getDate(), actualMRSEncounter.getDate());
        assertEquals(expectedEncounter.getCreator().getUserId(), actualMRSEncounter.getCreator().getUserId());
        assertEquals(expectedEncounter.getProvider().getProviderId(), actualMRSEncounter.getProvider().getProviderId());
        assertEquals(expectedEncounter.getFacility().getId(), actualMRSEncounter.getFacility().getId());
        assertEquals(expectedEncounter.getEncounterType(), actualMRSEncounter.getEncounterType());
        assertEquals(expectedEncounter.getPatient().getPatientId(), actualMRSEncounter.getPatient().getPatientId());
        assertObservation(expectedEncounter.getObservations(), actualMRSEncounter.getObservations());
    }

    private void assertObservation(Set<? extends Observation> expectedSet, Set<? extends Observation> actualSet) {
        assertEquals(expectedSet.size(), actualSet.size());
        for (Observation actual : actualSet) {
            assertTrue("Observation not same" + actual + " - expected set is " + expectedSet, isObservationPresent(expectedSet, actual));
        }
    }

    private boolean isObservationPresent(Set<? extends Observation> expectedSet, final Observation actual) {
        List<OpenMRSObservation> mrsObservations = (List<OpenMRSObservation>) Lambda.select(expectedSet, new LambdaJMatcher<Observation>() {

            @Override
            public boolean matches(Object o) {
                Observation expected = (OpenMRSObservation) o;
                return assertObservation(expected, actual);
            }
        });
        return isNotEmpty(mrsObservations) && mrsObservations.get(0) != null;
    }

    private boolean assertObservation(Observation expected, Observation actual) {
        return new EqualsBuilder().append(expected.getConceptName(), actual.getConceptName())
                .append(expected.getDate(), actual.getDate())
                .append(expected.getValue(), actual.getValue()).isEquals();
    }
}
