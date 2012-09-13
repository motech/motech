package org.motechproject.openmrs.rest.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.util.DateUtil;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MRSEncounterAdapterImplTest {

    private static final int DAY = 1;
    private static final int MONTH = 6;
    private static final int YEAR = 2012;

    @Mock
    private MRSPatientAdapter patientAdapter;

    @Mock
    private RestClient restfulClient;

    @Mock
    private OpenMrsUrlHolder urlHolder;

    @Mock
    private MRSPersonAdapterImpl personAdapter;

    @Mock
    private MRSConceptAdapterImpl conceptAdapter;

    private MRSEncounterAdapterImpl impl;

    @Before
    public void setUp() {
        initMocks(this);
        impl = new MRSEncounterAdapterImpl(restfulClient, patientAdapter, urlHolder, personAdapter, conceptAdapter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullEncounter() {
        impl.createEncounter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullPatient() {
        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withId(null).withProvider(new MRSPerson()).withCreator(new MRSUser()).withFacility(new MRSFacility("")).withDate(new Date()).withPatient(null).withObservations(null).withEncounterType("ADULTINITIAL").build();
        impl.createEncounter(encounter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullPatientId() {
        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withProviderId("A").withCreatorId("A").withFacilityId("A").withDate(new Date()).withPatientId(null).withObservations(null).withEncounterType("ADULTINITIAL").build();
        impl.createEncounter(encounter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyPatientId() {
        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withProviderId("A").withCreatorId("A").withFacilityId("A").withDate(new Date()).withPatientId(null).withObservations(null).withEncounterType("ADULTINITIAL").build();
        impl.createEncounter(encounter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullDateTime() {
        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withProviderId("A").withCreatorId("A").withFacilityId("A").withDate(null).withPatientId("A").withObservations(null).withEncounterType("ADULTINITIAL").build();
        impl.createEncounter(encounter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullEncounterType() {
        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withProviderId("A").withCreatorId("A").withFacilityId("A").withDate(new Date()).withPatientId("A").withObservations(null).withEncounterType(null).build();
        impl.createEncounter(encounter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyEncounterType() {
        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withProviderId("A").withCreatorId("A").withFacilityId("A").withDate(new Date()).withPatientId("A").withObservations(null).withEncounterType("").build();
        impl.createEncounter(encounter);
    }

    @Test
    public void shouldSendCorrectJson() throws HttpException, IOException {
        DateMidnight date = new DateMidnight(YEAR, MONTH, DAY);
        DateTime dateTime = date.toDateTime(DateTimeZone.UTC);
        MRSObservation ob = new MRSObservation(dateTime.toDate(), "Test Concept", "Test Value");
        Set<MRSObservation> obs = new HashSet<MRSObservation>();
        obs.add(ob);

        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withProviderId("PPR").withCreatorId(null).withFacilityId("LLL").withDate(dateTime.toDate()).withPatientId("PPP").withObservations(obs).withEncounterType("ADULTINITIAL").build();

        when(conceptAdapter.resolveConceptUuidFromConceptName("Test Concept")).thenReturn("CCC");
        when(restfulClient.postForJson(any(URI.class), any(String.class))).thenReturn("{}");

        impl.createEncounter(encounter);

        JsonElement expected = TestUtils.parseJsonFile("json/encounter-create.json");

        // manually setting the date because embedding the date in the json file
        // causes test failures across time zones
        // Is there a better way of handling this ?
        expected.getAsJsonObject().addProperty("encounterDatetime", DateUtil.formatToOpenMrsDate(dateTime.toDate()));
        JsonArray array = expected.getAsJsonObject().get("obs").getAsJsonArray();
        array.get(0).getAsJsonObject().addProperty("obsDatetime", DateUtil.formatToOpenMrsDate(date.toDate()));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(restfulClient).postForJson(any(URI.class), captor.capture());
        JsonElement sent = TestUtils.parseJsonString(captor.getValue());

        assertEquals(expected, sent);
    }

    @Test
    public void shouldParseJsonForEncounters() {

    }
}
