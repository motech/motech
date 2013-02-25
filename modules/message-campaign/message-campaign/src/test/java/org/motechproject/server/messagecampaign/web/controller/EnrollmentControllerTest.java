package org.motechproject.server.messagecampaign.web.controller;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.commons.date.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.search.Criterion;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.messagecampaign.web.ex.EnrollmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.testing.utils.rest.RestTestUtil.jsonMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class EnrollmentControllerTest {

    private static final String USER_ID = "47sf6a";
    private static final String CAMPAIGN_NAME = "PREGNANCY";

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8"));

    private MockMvc controller;

    @InjectMocks
    private EnrollmentController enrollmentController = new EnrollmentController();

    @Mock
    private MessageCampaignService messageCampaignService;

    @Mock
    private CampaignEnrollmentService enrollmentService;

    @Mock
    private CampaignEnrollment secondEnrollment;

    @Mock
    private AllCampaignEnrollments allCampaignEnrollments;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = MockMvcBuilders.standaloneSetup(enrollmentController).build();
    }

    @Test
    public void shouldEnrollUser() throws Exception {
        controller.perform(
            post("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loadJson("enrollmentRequest.json").getBytes("UTF-8"))
        ).andExpect(
            status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).startFor(captor.capture());

        assertEquals(new Time(20, 1), captor.getValue().deliverTime());
        assertEquals(new LocalDate(2013, 8, 14), captor.getValue().referenceDate());
        assertEquals(USER_ID, captor.getValue().externalId());
        assertEquals(CAMPAIGN_NAME, captor.getValue().campaignName());
    }

    @Test
    public void shouldUpdateUserEnrollmentWithExtId() throws Exception {
        controller.perform(
            post("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loadJson("updateEnrollmentExternalId.json").getBytes("UTF-8"))
        ).andExpect(
            status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).updateEnrollment(captor.capture(), eq("couchId"));

        assertEquals(new Time(20, 1), captor.getValue().deliverTime());
        assertEquals(new LocalDate(2013, 8, 14), captor.getValue().referenceDate());
        assertEquals(USER_ID, captor.getValue().externalId());
        assertEquals(CAMPAIGN_NAME, captor.getValue().campaignName());
    }

    @Test
    public void shouldReturn404WhenUpdatingNonexistentEnrollment() throws Exception {
        final String expectedResponse = "Enrollment with id couchId not found";

        doThrow(new EnrollmentNotFoundException(expectedResponse))
                .when(messageCampaignService).updateEnrollment(any(CampaignRequest.class), eq("couchId"));

        controller.perform(
            post("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loadJson("updateEnrollmentExternalId.json").getBytes("UTF-8"))
        ).andExpect(
            status().is(HttpStatus.NOT_FOUND.value())
        ).andExpect(
            content().string(expectedResponse)
        );
    }

    @Test
    public void shouldReturn400WhenCreatingADuplicate() throws Exception {
        final String expectedResponse = String.format("%s is already enrolled in %s campaign, enrollmentId: %s",
                USER_ID, CAMPAIGN_NAME, "couchId2");

        doThrow(new IllegalArgumentException(expectedResponse))
                .when(messageCampaignService).updateEnrollment(any(CampaignRequest.class), eq("couchId"));

        controller.perform(
            post("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loadJson("updateEnrollmentExternalId.json").getBytes("UTF-8"))
        ).andExpect(
            status().is(HttpStatus.BAD_REQUEST.value())
        ).andExpect(
            content().string(expectedResponse)
        );
    }

    @Test
    public void shouldReturnUserEnrollmentDetails() throws Exception {
        CampaignEnrollment enrollment = new CampaignEnrollment(USER_ID, CAMPAIGN_NAME);
        enrollment.setReferenceDate(new LocalDate(2013, 3, 10));
        enrollment.setDeliverTime(new Time(20, 30));
        enrollment.setId("couchId");

        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class))).thenReturn(asList(enrollment));

        final String expectedResponse = loadJson("enrollmentDetails.json");

        controller.perform(
            get("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );

        ArgumentCaptor<CampaignEnrollmentsQuery> captor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(enrollmentService).search(captor.capture());

        verifyCampaignNameCriterion(captor.getValue().getPrimaryCriterion(), CAMPAIGN_NAME);
        assertEquals(1, captor.getValue().getSecondaryCriteria().size());
        verifyExternalIdCriterion(captor.getValue().getSecondaryCriteria().get(0), USER_ID);
    }

    @Test
    public void shouldReturn404ForNonExistentEnrollment() throws Exception {
        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(Collections.<CampaignEnrollment>emptyList());

        final String expectedResponse = "No enrollments found for user " + USER_ID;

        controller.perform(
            get("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
        ).andExpect(
            status().is(HttpStatus.NOT_FOUND.value())
        ).andExpect(
            content().string(expectedResponse)
        );
    }

    @Test
    public void shouldReturnEnrollmentsForCampaigns() throws Exception {
        CampaignEnrollment enrollment1 = new CampaignEnrollment("47sf6a", CAMPAIGN_NAME);
        enrollment1.setDeliverTime(new Time(20, 1));
        enrollment1.setReferenceDate(new LocalDate(2013, 4, 1));
        enrollment1.setId("enrollmentId1");
        CampaignEnrollment enrollment2 = new CampaignEnrollment("d6gt40", CAMPAIGN_NAME);
        enrollment2.setDeliverTime(new Time(10, 0));
        enrollment2.setReferenceDate(new LocalDate(2013, 8, 18));
        enrollment2.setId("enrollmentId2");
        CampaignEnrollment enrollment3 = new CampaignEnrollment("o34j6f", CAMPAIGN_NAME);
        enrollment3.setDeliverTime(new Time(10, 0));
        enrollment3.setReferenceDate(new LocalDate(2013, 1, 25));
        enrollment3.setId("enrollmentId3");

        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(asList(enrollment1, enrollment2, enrollment3));

        final String expectedResponse = loadJson("enrollmentsForCampaign.json");

        controller.perform(
            get("/web-api/enrollments/{campaignName}/users", CAMPAIGN_NAME)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );

        ArgumentCaptor<CampaignEnrollmentsQuery> captor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(enrollmentService).search(captor.capture());

        verifyCampaignNameCriterion(captor.getValue().getPrimaryCriterion(), CAMPAIGN_NAME);
        assertTrue(captor.getValue().getSecondaryCriteria().isEmpty());
    }

    @Test
    public void shouldReturn404ForCampaignNotFound() throws Exception {
        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(Collections.<CampaignEnrollment>emptyList());

        final String expectedResponse = "{\"campaignName\":\"PREGNANCY\", \"enrollments\":[]}";

        controller.perform(
            get("/web-api/enrollments/{campaignName}/users", CAMPAIGN_NAME)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );
    }

    @Test
    public void shouldReturnEnrollmentsForUser() throws Exception {
        CampaignEnrollment enrollment1 = new CampaignEnrollment(USER_ID, CAMPAIGN_NAME);
        enrollment1.setDeliverTime(new Time(20, 1));
        enrollment1.setReferenceDate(new LocalDate(2013, 4, 1));
        enrollment1.setId("enrollmentId1");
        CampaignEnrollment enrollment2 = new CampaignEnrollment(USER_ID, "CHILD_DEVELOPMENT");
        enrollment2.setDeliverTime(new Time(10, 0));
        enrollment2.setReferenceDate(new LocalDate(2013, 8, 18));
        enrollment2.setId("enrollmentId2");

        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(asList(enrollment1, enrollment2));

        final String expectedResponse = loadJson("enrollmentsForUser.json");

        controller.perform(
            get("/web-api/enrollments/users/{user_id}", USER_ID)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );

        ArgumentCaptor<CampaignEnrollmentsQuery> captor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(enrollmentService).search(captor.capture());

        verifyExternalIdCriterion(captor.getValue().getPrimaryCriterion(), USER_ID);
        assertTrue(captor.getValue().getSecondaryCriteria().isEmpty());
    }

    @Test
    public void shouldReturn404WhenUserHasNoEnrollments() throws Exception {
        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(Collections.<CampaignEnrollment>emptyList());

        final String expectedResponse = "No enrollments found for user " + USER_ID;

        controller.perform(
            get("/web-api/enrollments/users/{user_id}", USER_ID)
        ).andExpect(
            status().is(HttpStatus.NOT_FOUND.value())
        ).andExpect(
            content().string(expectedResponse)
        );
    }

    @Test
    public void shouldReturnAllEnrollmentsFilteredByStatus() throws Exception {
        CampaignEnrollment enrollment1 = new CampaignEnrollment(USER_ID, CAMPAIGN_NAME);
        enrollment1.setDeliverTime(new Time(20, 1));
        enrollment1.setReferenceDate(new LocalDate(2012, 1, 2));
        enrollment1.setId("enrollmentId1");
        CampaignEnrollment enrollment2 = new CampaignEnrollment("d6gt40", CAMPAIGN_NAME);
        enrollment2.setDeliverTime(new Time(10, 0));
        enrollment2.setReferenceDate(new LocalDate(2012, 2, 15));
        enrollment2.setId("enrollmentId2");
        CampaignEnrollment enrollment3 = new CampaignEnrollment("o34j6f", "CHILD_DEVELOPMENT");
        enrollment3.setDeliverTime(new Time(10, 0));
        enrollment3.setReferenceDate(new LocalDate(2012, 3, 13));
        enrollment3.setId("enrollmentId3");

        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(asList(enrollment1, enrollment2, enrollment3));

        final String expectedResponse = loadJson("enrollmentList.json");

        controller.perform(
            get("/web-api/enrollments/users/?enrollmentStatus=COMPLETED")
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );

        ArgumentCaptor<CampaignEnrollmentsQuery> captor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(enrollmentService).search(captor.capture());

        verifyStatusCriterion(captor.getValue().getPrimaryCriterion(), CampaignEnrollmentStatus.COMPLETED);
        assertTrue(captor.getValue().getSecondaryCriteria().isEmpty());
    }

    @Test
    public void shouldFilterEnrollmentsByExternalIdAndCampaignName() throws Exception {
        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(Collections.<CampaignEnrollment>emptyList());

        controller.perform(
            get("/web-api/enrollments/users/?campaignName={campaignName}&externalId={externalId}", CAMPAIGN_NAME, USER_ID)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<CampaignEnrollmentsQuery> captor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(enrollmentService).search(captor.capture());

        verifyCampaignNameCriterion(captor.getValue().getPrimaryCriterion(), CAMPAIGN_NAME);
        assertEquals(1, captor.getValue().getSecondaryCriteria().size());
        verifyExternalIdCriterion(captor.getValue().getSecondaryCriteria().get(0), USER_ID);
    }

    @Test
    public void shouldUpdateEnrollment() throws Exception {
        controller.perform(
            put("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loadJson("enrollmentUpdate.json").getBytes("UTF-8"))
        ).andExpect(
            status().is(HttpStatus.OK.value())
        );

        final Time expectedTime = new Time(17, 1);
        final LocalDate expectedDate = new LocalDate(2013, 8, 20);

        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);

        verify(messageCampaignService).stopAll(captor.capture());

        assertEquals(CAMPAIGN_NAME, captor.getValue().campaignName());
        assertEquals(USER_ID, captor.getValue().externalId());
        assertEquals(expectedTime, captor.getValue().deliverTime());
        assertEquals(expectedDate, captor.getValue().referenceDate());

        verify(messageCampaignService).startFor(captor.capture());

        assertEquals(CAMPAIGN_NAME, captor.getValue().campaignName());
        assertEquals(USER_ID, captor.getValue().externalId());
        assertEquals(expectedTime, captor.getValue().deliverTime());
        assertEquals(expectedDate, captor.getValue().referenceDate());
    }

    @Test
    public void shouldDeleteEnrollment() throws Exception {
        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(asList(new CampaignEnrollment(CAMPAIGN_NAME, USER_ID)));

        controller.perform(
            delete("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<CampaignEnrollmentsQuery> queryCaptor = ArgumentCaptor.forClass(CampaignEnrollmentsQuery.class);
        verify(enrollmentService).search(queryCaptor.capture());

        verifyCampaignNameCriterion(queryCaptor.getValue().getPrimaryCriterion(), CAMPAIGN_NAME);
        assertEquals(1, queryCaptor.getValue().getSecondaryCriteria().size());
        verifyExternalIdCriterion(queryCaptor.getValue().getSecondaryCriteria().get(0), USER_ID);

        ArgumentCaptor<CampaignRequest> campaignRequestCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).stopAll(campaignRequestCaptor.capture());

        assertEquals(CAMPAIGN_NAME, campaignRequestCaptor.getValue().campaignName());
        assertEquals(USER_ID, campaignRequestCaptor.getValue().externalId());
    }

    @Test
    public void shouldReturn404WhenDeletingNonExistingEnrollment() throws Exception {
        when(enrollmentService.search(any(CampaignEnrollmentsQuery.class)))
                .thenReturn(Collections.<CampaignEnrollment>emptyList());

        final String expectedResponse = "No enrollments found for user " + USER_ID;

        controller.perform(
            delete("/web-api/enrollments/{campaignName}/users/{userId}", CAMPAIGN_NAME, USER_ID)
        ).andExpect(
            status().is(HttpStatus.NOT_FOUND.value())
        ).andExpect(
            content().string(expectedResponse)
        );
    }

    private void verifyExternalIdCriterion(Criterion criterion, String externalId) {
        criterion.fetch(allCampaignEnrollments);
        verify(allCampaignEnrollments).findByExternalId(externalId);
    }

    private void verifyCampaignNameCriterion(Criterion criterion, String campaignName) {
        criterion.fetch(allCampaignEnrollments);
        verify(allCampaignEnrollments).findByCampaignName(campaignName);
    }

    private void verifyStatusCriterion(Criterion criterion, CampaignEnrollmentStatus status) {
        criterion.fetch(allCampaignEnrollments);
        verify(allCampaignEnrollments).findByStatus(status);
    }

    private String loadJson(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/enrollments/" + filename)) {
            return IOUtils.toString(in);
        }
    }
}

