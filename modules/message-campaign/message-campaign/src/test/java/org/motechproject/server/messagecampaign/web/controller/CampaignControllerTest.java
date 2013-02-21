package org.motechproject.server.messagecampaign.web.controller;


import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.loader.CampaignJsonLoader;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.testing.utils.rest.RestTestUtil.jsonMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class CampaignControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8"));

    private CampaignJsonLoader campaignJsonLoader = new CampaignJsonLoader();

    private MockMvc controller;

    @InjectMocks
    private CampaignController campaignController = new CampaignController();

    @Mock
    private MessageCampaignService messageCampaignService;

    @Mock
    private AllCampaignEnrollments allCampaignEnrollments;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = MockMvcBuilders.standaloneSetup(campaignController).build();
    }

    @Test
    public void shouldReturnCampaignDetails() throws Exception {
        final CampaignRecord campaignRecord = readSingleCampaign("campaignDetails.json");
        final String campaignName = "Absolute Dates Message Program";
        when(messageCampaignService.getCampaignRecord(campaignName)).thenReturn(campaignRecord);

        final String expectedResponse = loadJson("campaignDetails.json");

        controller.perform(
            get("/web-api/campaigns/{campaignName}", campaignName)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );

        verify(messageCampaignService).getCampaignRecord(campaignName);
    }

    @Test
    public void shouldReturn404ForNonExistentCampaign() throws Exception {
        final String campaignName = "PREGNANCY";
        when(messageCampaignService.getCampaignRecord(campaignName)).thenReturn(null);

        final String expectedResponse = "Campaign not found: " + campaignName;

        controller.perform(
            get("/web-api/campaigns/{campaignName}", campaignName)
        ).andExpect(
            status().is(HttpStatus.NOT_FOUND.value())
        ).andExpect(
            content().string(expectedResponse)
        );

        verify(messageCampaignService).getCampaignRecord(campaignName);
    }

    @Test
    public void shouldCreateNewCampaign() throws Exception {
        final CampaignRecord campaignRecord = readSingleCampaign("campaignDetails.json");

        controller.perform(
            post("/web-api/campaigns/")
            .body(loadJson("campaignDetails.json").getBytes())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<CampaignRecord> captor = ArgumentCaptor.forClass(CampaignRecord.class);
        verify(messageCampaignService).saveCampaign(captor.capture());

        assertEquals(campaignRecord, captor.getValue());
    }

    @Test
    public void shouldRetrieveAllCampaigns() throws Exception {
        final List<CampaignRecord> expectedRecords = readCampaigns("campaignList.json");
        when(messageCampaignService.getAllCampaignRecords()).thenReturn(expectedRecords);

        final String expectedResponse = loadJson("campaignList.json");

        controller.perform(
            get("/web-api/campaigns/")
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );

        verify(messageCampaignService).getAllCampaignRecords();
    }

    @Test
    public void shouldDeleteCampaign() throws Exception {
        final String campaignName = "PREGNANCY";
        final CampaignRecord campaignRecord = readSingleCampaign("campaignDetails.json");

        when(messageCampaignService.getCampaignRecord(campaignName)).thenReturn(campaignRecord);

        controller.perform(
            delete("/web-api/campaigns/{campaignName}", campaignName)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        );

        verify(messageCampaignService).deleteCampaign(campaignName);
    }

    @Test
    public void shouldReturn404WhenDeletingNonExistentCampaign() throws Exception {
        final String campaignName = "PREGNANCY";
        final String expectedResponse = "Campaign not found: " + campaignName;

        doThrow(new CampaignNotFoundException(expectedResponse))
                .when(messageCampaignService).deleteCampaign(campaignName);

        controller.perform(
            delete("/web-api/campaigns/{campaignName}", campaignName)
        ).andExpect(
            status().is(HttpStatus.NOT_FOUND.value())
        ).andExpect(
            content().string(expectedResponse)
        );

        verify(messageCampaignService).deleteCampaign(campaignName);
    }

    private List<CampaignRecord> readCampaigns(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/campaigns/" + filename)) {
            return campaignJsonLoader.loadCampaigns(in);
        }
    }

    private CampaignRecord readSingleCampaign(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/campaigns/" + filename)) {
            return campaignJsonLoader.loadSingleCampaign(in);
        }
    }

    private String loadJson(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/campaigns/" + filename)) {
            return IOUtils.toString(in);
        }
    }
}
