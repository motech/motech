package org.motechproject.openmrs.services;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.matcher.LambdaJMatcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.*;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.motechproject.util.DateUtil;
import org.openmrs.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.openmrs.TestIdGenerator.newGUID;
import static org.motechproject.util.DateUtil.newDate;

public class OpenMRSEncounterAdapterIT extends OpenMRSIntegrationTestBase {

    @Autowired
    MRSEncounterAdapter mrsEncounterAdapter;
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

    MRSFacility facility;
    MRSPatient patientAlan;
    @Autowired
    private OpenMRSObservationAdapter openMRSObservationAdapter;

    public void doOnceBefore() {
        facility = facilityAdapter.saveFacility(new MRSFacility("name", "country", "region", "district", "province"));
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldSaveEncounterWithObservationsAndReturn() throws UserAlreadyExistsException {

        MRSPerson personCreator = new MRSPerson().firstName("SampleTest");
        MRSPerson personJohn = new MRSPerson().firstName("John");
        patientAlan = createPatient(facility);
        MRSUser userCreator = createUser(new MRSUser().userName("UserSuper").systemId("1000015").securityRole("Provider").person(personCreator));
        MRSUser userJohn = createUser(new MRSUser().userName("UserJohn").systemId("1000027").securityRole("Provider").person(personJohn));
        MRSPerson provider = userJohn.getPerson();

        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        observations.add(new MRSObservation<Double>(new Date(), "GRAVIDA", Double.valueOf("100.0")));
        observations.add(new MRSObservation<String>(new Date(), "SERIAL NUMBER", "free text data serail number"));
        observations.add(new MRSObservation<Date>(new Date(), "NEXT ANC DATE", new DateTime(2012, 11, 10, 1, 10).toDate()));
        observations.add(new MRSObservation<Boolean>(new Date(), "PREGNANCY STATUS", false));
        final String encounterType = "PEDSRETURN";
        MRSEncounter expectedEncounter = new MRSEncounter(provider.getId(), userCreator.getId(), facility.getId(), new Date(), patientAlan.getId(), observations, encounterType);
        MRSEncounter actualMRSEncounter = mrsEncounterAdapter.createEncounter(expectedEncounter);
        assertEncounter(expectedEncounter, actualMRSEncounter);

        final MRSEncounter mrsEncounter = mrsEncounterAdapter.getLatestEncounterByPatientMotechId(patientAlan.getMotechId(), encounterType);
        assertEncounter(expectedEncounter, mrsEncounter);
    }

    @Test
    @Transactional(readOnly = true)
    public void creationOfEncounterShouldHappenInIdempotentWay() throws UserAlreadyExistsException {

        MRSPerson personCreator = new MRSPerson().firstName("SampleTest");
        MRSPerson personJohn = new MRSPerson().firstName("John");
        patientAlan = createPatient(facility);
        MRSUser userCreator = createUser(new MRSUser().userName(newGUID("UserAdmin")).systemId(newGUID("10000151")).securityRole("Provider").person(personCreator));
        MRSUser userJohn = createUser(new MRSUser().userName(newGUID("UserJohn1")).systemId(newGUID("10000271")).securityRole("Provider").person(personJohn));
        MRSPerson provider = userJohn.getPerson();

        final Set<MRSObservation> observations = new HashSet<MRSObservation>();
        final Date encounterTime = DateUtil.newDateTime(newDate(2012, 3, 4), 3, 4, 3).toDate();
        final Date observationDate = new Date();
        observations.add(new MRSObservation(observationDate, "SERIAL NUMBER", "free text data serail number"));
        observations.add(new MRSObservation(observationDate, "NEXT ANC DATE", new DateTime(2012, 11, 10, 1, 10).toDate()));
        observations.add(new MRSObservation(observationDate, "GRAVIDA", Double.valueOf("100.0")));
        final String encounterType = "PEDSRETURN";
        MRSEncounter expectedEncounter = new MRSEncounter(provider.getId(), userCreator.getId(), facility.getId(), encounterTime, patientAlan.getId(), observations, encounterType);
        MRSEncounter duplicateEncounter = new MRSEncounter(provider.getId(), userCreator.getId(), facility.getId(), encounterTime, patientAlan.getId(), new HashSet<MRSObservation>() {{
            add(new MRSObservation(observationDate, "SERIAL NUMBER", "free text data serail number"));
            add(new MRSObservation(observationDate, "NEXT ANC DATE", new DateTime(2012, 11, 10, 1, 10).toDate()));
            add(new MRSObservation(observationDate, "PREGNANCY STATUS", false));
        }}, encounterType);

        MRSEncounter oldEncounter = mrsEncounterAdapter.createEncounter(expectedEncounter);
        MRSEncounter newEncounter = mrsEncounterAdapter.createEncounter(duplicateEncounter);
        assertEncounter(expectedEncounter, oldEncounter);
        assertEncounter(duplicateEncounter, newEncounter);
        assertThat(newEncounter.getId(), not(is(oldEncounter.getId())));

        List<MRSObservation> oldObservations = Lambda.select(oldEncounter.getObservations(), having(on(MRSObservation.class).getConceptName(), isIn(asList("SERIAL NUMBER", "NEXT ANC DATE"))));
        List<MRSObservation> newObservations = Lambda.select(newEncounter.getObservations(), having(on(MRSObservation.class).getConceptName(), isIn(asList("SERIAL NUMBER", "NEXT ANC DATE"))));
        assertObservation(new HashSet<MRSObservation>(oldObservations), new HashSet<MRSObservation>(newObservations));

        assertTrue(CollectionUtils.isEmpty(Lambda.select(newEncounter.getObservations(), having(on(MRSObservation.class).getConceptName(), is("GRAVIDA")))));
        assertTrue(CollectionUtils.isNotEmpty(Lambda.select(newEncounter.getObservations(), having(on(MRSObservation.class).getConceptName(), is("PREGNANCY STATUS")))));

        final List<String> oldObservationIds = extract(oldEncounter.getObservations(), on(MRSObservation.class).getId());
        for (String observationId : oldObservationIds) {
            assertNull("[" + observationId + "]," + newEncounter.getObservations(), obsService.getObs(Integer.parseInt(observationId)));
        }
    }

    private void assertEncounter(MRSEncounter expectedEncounter, MRSEncounter actualMRSEncounter) {
        assertEquals(expectedEncounter.getDate(), actualMRSEncounter.getDate());
        assertEquals(expectedEncounter.getCreator().getId(), actualMRSEncounter.getCreator().getId());
        assertEquals(expectedEncounter.getProvider().getId(), actualMRSEncounter.getProvider().getId());
        assertEquals(expectedEncounter.getFacility().getId(), actualMRSEncounter.getFacility().getId());
        assertEquals(expectedEncounter.getEncounterType(), actualMRSEncounter.getEncounterType());
        assertEquals(expectedEncounter.getPatient().getId(), actualMRSEncounter.getPatient().getId());
        assertObservation(expectedEncounter.getObservations(), actualMRSEncounter.getObservations());
    }

    private void assertObservation(Set<MRSObservation> expectedSet, Set<MRSObservation> actualSet) {
        assertEquals(expectedSet.size(), actualSet.size());
        for (MRSObservation actual : actualSet) {
            assertTrue("Observation not same" + actual + " - expected set is " + expectedSet, isObservationPresent(expectedSet, actual));
        }
    }

    private boolean isObservationPresent(Set<MRSObservation> expectedSet, final MRSObservation actual) {
        List<MRSObservation> mrsObservations = Lambda.select(expectedSet, new LambdaJMatcher<MRSObservation>() {

            @Override
            public boolean matches(Object o) {
                MRSObservation expected = (MRSObservation) o;
                return assertObservation(expected, actual);
            }
        });
        return isNotEmpty(mrsObservations) && mrsObservations.get(0) != null;
    }

    private boolean assertObservation(MRSObservation expected, MRSObservation actual) {
        return new EqualsBuilder().append(expected.getConceptName(), actual.getConceptName())
                .append(expected.getDate(), actual.getDate())
                .append(expected.getValue(), actual.getValue()).isEquals();
    }
}
