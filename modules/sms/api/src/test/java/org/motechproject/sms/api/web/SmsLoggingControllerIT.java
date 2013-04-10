package org.motechproject.sms.api.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SMSType;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.domain.SmsRecords;
import org.motechproject.sms.api.repository.AllSmsRecords;
import org.motechproject.sms.api.service.SmsRecordSearchCriteria;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.testing.utils.rest.RestTestUtil.jsonMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testApplicationSmsApi.xml"})
public class SmsLoggingControllerIT {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8"));

    @Autowired
    private SmsService smsService;

    @Autowired
    private AllSmsRecords allSmsRecords;

    @Autowired
    SMSLoggingController smsLoggingController;

    MockMvc controllerWithMockService;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        controllerWithMockService = MockMvcBuilders.standaloneSetup(smsLoggingController).build();
        createSmsRecords();
    }

    @Test
    public void shouldReturnAllSMSLogging() throws Exception{
        Set<DeliveryStatus> statusSet = new HashSet<>();
        statusSet.add(DeliveryStatus.DELIVERED);
        statusSet.add(DeliveryStatus.KEEPTRYING);
        statusSet.add(DeliveryStatus.UNKNOWN);
        statusSet.add(DeliveryStatus.INPROGRESS);
        statusSet.add(DeliveryStatus.ABORTED);
        Set<SMSType> typeSet = new HashSet<>();
        typeSet.add(SMSType.INBOUND);
        typeSet.add(SMSType.OUTBOUND);
        SmsRecordSearchCriteria criteria = new SmsRecordSearchCriteria().withQueryParam(new QueryParam(0, 7, "", false))
                .withDeliveryStatuses(statusSet)
                .withSmsTypes(typeSet);
        SmsRecords records = allSmsRecords.findAllBy(criteria);
        String expectedResponse = createResponse(new SmsLoggingRecords(1, 7, records));
        controllerWithMockService.perform(
                get("/smslogging?phoneNumber=&messageContent=&timeFrom=&timeTo=&deliveryStatus=INPROGRESS,DELIVERED,KEEPTRYING,ABORTED,UNKNOWN&smsType=INBOUND,OUTBOUND&rows=7&page=1&sidx=&sord=asc")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );
    }

    @Test
    public void shouldReturnOnlySMSWithOutboundType() throws Exception {
        Set<DeliveryStatus> statusSet = new HashSet<>();
        statusSet.add(DeliveryStatus.DELIVERED);
        statusSet.add(DeliveryStatus.KEEPTRYING);
        statusSet.add(DeliveryStatus.UNKNOWN);
        statusSet.add(DeliveryStatus.INPROGRESS);
        statusSet.add(DeliveryStatus.ABORTED);
        Set<SMSType> typeSet = new HashSet<>();
        typeSet.add(SMSType.OUTBOUND);
        SmsRecordSearchCriteria criteria = new SmsRecordSearchCriteria().withQueryParam(new QueryParam(0, 7, "", false))
                .withDeliveryStatuses(statusSet)
                .withSmsTypes(typeSet);
        SmsRecords records = allSmsRecords.findAllBy(criteria);
        String expectedResponse = createResponse(new SmsLoggingRecords(1, 7, records));
        controllerWithMockService.perform(
                get("/smslogging?phoneNumber=&messageContent=&timeFrom=&timeTo=&deliveryStatus=INPROGRESS,DELIVERED,KEEPTRYING,ABORTED,UNKNOWN&smsType=OUTBOUND&rows=7&page=1&sidx=&sord=asc")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );
    }

    @Test
    public void shouldReturnSMSOnlyDeliveredStatus() throws Exception{
        Set<DeliveryStatus> statusSet = new HashSet<>();
        statusSet.add(DeliveryStatus.DELIVERED);
        Set<SMSType> typeSet = new HashSet<>();
        typeSet.add(SMSType.INBOUND);
        typeSet.add(SMSType.OUTBOUND);
        SmsRecordSearchCriteria criteria = new SmsRecordSearchCriteria().withQueryParam(new QueryParam(0, 7, "", false))
                .withDeliveryStatuses(statusSet)
                .withSmsTypes(typeSet);
        SmsRecords records = allSmsRecords.findAllBy(criteria);
        String expectedResponse = createResponse(new SmsLoggingRecords(1, 7, records));
        controllerWithMockService.perform(
                get("/smslogging?phoneNumber=&messageContent=&timeFrom=&timeTo=&deliveryStatus=DELIVERED&smsType=INBOUND,OUTBOUND&rows=7&page=1&sidx=&sord=asc")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );
    }

    @Test
    public void shouldReturnSMSByRange() throws Exception{
        Set<DeliveryStatus> statusSet = new HashSet<>();
        statusSet.add(DeliveryStatus.DELIVERED);
        statusSet.add(DeliveryStatus.KEEPTRYING);
        statusSet.add(DeliveryStatus.UNKNOWN);
        statusSet.add(DeliveryStatus.INPROGRESS);
        statusSet.add(DeliveryStatus.ABORTED);
        Set<SMSType> typeSet = new HashSet<>();
        typeSet.add(SMSType.INBOUND);
        typeSet.add(SMSType.OUTBOUND);
        Range<DateTime> range = new Range<DateTime>(new DateTime(1992, 3, 15, 11, 40), new DateTime(2004, 1, 19, 4, 34));
        SmsRecordSearchCriteria criteria = new SmsRecordSearchCriteria().withQueryParam(new QueryParam(0, 7, "", false))
                .withDeliveryStatuses(statusSet)
                .withSmsTypes(typeSet)
                .withMessageTimeRange(range);
        SmsRecords records = allSmsRecords.findAllBy(criteria);
        String expectedResponse = createResponse(new SmsLoggingRecords(1, 7, records));
        controllerWithMockService.perform(
                get("/smslogging?phoneNumber=&messageContent=&timeFrom=1992-03-15 11:40:00&timeTo=2004-01-19 04:34:00&deliveryStatus=INPROGRESS,DELIVERED,KEEPTRYING,ABORTED,UNKNOWN&smsType=INBOUND,OUTBOUND&rows=7&page=1&sidx=&sord=asc")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );
    }

    @After
    public void tearDown() {
        allSmsRecords.removeAll();
    }

    private void createSmsRecords() {
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, "123456789", "message1", new DateTime(1992, 3, 15, 11, 40), DeliveryStatus.INPROGRESS, "1234"));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, "654321567", "message2", new DateTime(1984, 6, 18, 9, 30), DeliveryStatus.KEEPTRYING, "1234"));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, "725439851", "message3", new DateTime(2002, 2, 25, 18, 35), DeliveryStatus.UNKNOWN, "1234"));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.INBOUND, "190362783", "message4", new DateTime(2004, 1, 19, 4, 34), DeliveryStatus.ABORTED, "1234"));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.INBOUND, "845725146", "message5", new DateTime(2007, 6, 5, 5, 5), DeliveryStatus.DELIVERED, "1234"));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.INBOUND, "872365283", "message6", new DateTime(2010, 7, 9, 9, 49), DeliveryStatus.KEEPTRYING, "1234"));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.INBOUND, "230470239", "message7", new DateTime(1975, 3, 17, 7, 33), DeliveryStatus.DELIVERED, "1234"));
    }

    private String createResponse(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

}
