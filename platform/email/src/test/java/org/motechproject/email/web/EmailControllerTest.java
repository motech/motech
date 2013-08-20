package org.motechproject.email.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.email.service.impl.EmailAuditServiceImpl;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailControllerTest {
    @Mock
    private EmailSenderService senderService;

    @Mock
    private EmailAuditServiceImpl auditService;

    @Mock
    private MotechUserService motechUserService;

    @Mock
    private MotechRoleService motechRoleService;

    @InjectMocks
    private EmailController emailController = new EmailController();


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());
        when(auditService.findEmailRecords(Matchers.any(EmailRecordSearchCriteria.class))).thenReturn(getTestEmailRecords());
                when(motechUserService.getRoles(anyString())).thenReturn(asList("Email Admin"));
        when(motechRoleService.getRole("Email Admin")).thenReturn(new RoleDto("Email Admin",
                asList("viewBasicEmailLogs", "viewDetailedEmailLogs")));
    }

    @Test
    public void shouldReturnRecordsFilteredByAddress() {
        GridSettings filter = new GridSettings();
        filter.setSubject("gmail.com");
        filter.setPage(1);
        filter.setRows(10);
        EmailRecords<EmailRecordDto> recs = emailController.getEmails(filter);

        GridSettings filter2 = new GridSettings();
        filter2.setSubject("yahoo.com");
        filter2.setPage(1);
        filter2.setRows(10);
        EmailRecords<EmailRecordDto> recs2 = emailController.getEmails(filter2);

        assertNotNull(recs);
        assertThat(recs.getRecords(), is(4));
        assertNotNull(recs2);
        assertThat(recs2.getRecords(), is(3));
    }

    @Test
    public void shouldSortByDate() {
        GridSettings filter = new GridSettings();
        filter.setSortColumn("deliveryTime");
        filter.setSortDirection("asc");
        filter.setPage(1);
        filter.setRows(10);
        EmailRecords<EmailRecordDto> recs = emailController.getEmails(filter);

        assertNotNull(recs);
        assertThat(recs.getRows().get(0).getDeliveryTime(), is(new DateTime(1000).toString("Y-MM-dd hh:mm:ss")));
        assertThat(recs.getRows().get(1).getDeliveryTime(), is(new DateTime(2000).toString("Y-MM-dd hh:mm:ss")));
        assertThat(recs.getRows().get(2).getDeliveryTime(), is(new DateTime(3000).toString("Y-MM-dd hh:mm:ss")));
        assertThat(recs.getRows().get(3).getDeliveryTime(), is(new DateTime(4000).toString("Y-MM-dd hh:mm:ss")));
    }

    @Test
    public void shouldSortBySubject() {
        GridSettings filter = new GridSettings();
        filter.setSortColumn("subject");
        filter.setSortDirection("asc");
        filter.setPage(1);
        filter.setRows(10);
        EmailRecords<EmailRecordDto> recs = emailController.getEmails(filter);

        assertNotNull(recs);
        assertThat(recs.getRows().get(0).getSubject(), is("Asubject3"));
        assertThat(recs.getRows().get(1).getSubject(), is("Bsubject5"));
        assertThat(recs.getRows().get(2).getSubject(), is("subject"));
    }

    @Test
    public void shouldReturnGivenRecord() {
        GridSettings filter = new GridSettings();
        filter.setSortColumn("message");
        filter.setSortDirection("asc");
        filter.setPage(1);
        filter.setRows(10);
        emailController.getEmails(filter);
        EmailRecords<EmailRecordDto> rec1 = emailController.getEmail(1);
        EmailRecords<EmailRecordDto> rec4 = emailController.getEmail(4);

        assertNotNull(rec1);
        assertNotNull(rec4);
        assertThat(rec1.getRows().get(0).getMessage(), is("message"));
        assertThat(rec4.getRows().get(0).getMessage(), is("message4"));
    }

    @Test
    public void shouldReturnGivenRecordAfterFiltering() {
        GridSettings filter = new GridSettings();
        filter.setSortColumn("message");
        filter.setPage(1);
        filter.setRows(10);
        filter.setSortDirection("asc");
        filter.setSubject("@gmail.com");
        emailController.getEmails(filter);
        EmailRecords<EmailRecordDto> rec1 = emailController.getEmail(1);
        EmailRecords<EmailRecordDto> rec3 = emailController.getEmail(3);

        assertNotNull(rec1);
        assertNotNull(rec3);
        assertThat(emailController.getEmails(filter).getRecords(), is(4));
        assertThat(rec1.getRows().get(0).getMessage(), is("message"));
        assertThat(rec3.getRows().get(0).getMessage(), is("message3"));
    }

    @Test
    public void shouldReturnGivenRecordAfterSorting() {
        GridSettings filter = new GridSettings();
        filter.setSortColumn("deliveryTime");
        filter.setSortDirection("desc");
        filter.setPage(1);
        filter.setRows(10);
        emailController.getEmails(filter);
        EmailRecords<EmailRecordDto> rec1 = emailController.getEmail(1);
        EmailRecords<EmailRecordDto> rec4 = emailController.getEmail(4);

        assertNotNull(rec1);
        assertNotNull(rec4);
        assertThat(rec1.getRows().get(0).getMessage(), is("message4"));
        assertThat(rec4.getRows().get(0).getMessage(), is("message2"));
    }

    @Test
    public void shouldReturnProperMailsForAutoComplete() {
        List<String> available = emailController.getAvailableMails("subject", "abc");
        List<String> available2 = emailController.getAvailableMails("subject", "def");
        List<String> available3 = emailController.getAvailableMails("subject", "abc@g");

        assertNotNull(available);
        assertNotNull(available2);
        assertNotNull(available3);
        assertThat(available.size(), is(2));
        assertThat(available2.size(), is(2));
        assertThat(available3.size(), is(1));
        assertThat(available3.get(0), is("abc@gmail.com"));
    }


    private List<EmailRecord> getTestEmailRecords() {
        List<EmailRecord> records = new ArrayList<>();
        records.add(new EmailRecord("abc@gmail.com", "def@gmail.com", "subject", "message",
               new DateTime(1000), DeliveryStatus.SENT));
        records.add(new EmailRecord("def@gmail.com", "abc@gmail.com", "subject2", "message2",
               new DateTime(2000), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "def@gmail.com", "Asubject3", "message3",
               new DateTime(3000), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "abc@gmail.com", "subject4", "message4",
               new DateTime(5000), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "def@yahoo.com", "Bsubject5", "message5",
               new DateTime(4000), DeliveryStatus.SENT));
        return records;
    }
}
